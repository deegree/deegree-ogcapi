/*-
 * #%L
 * deegree-ogcapi-config - OGC API Config implementation
 * %%
 * Copyright (C) 2019 - 2020 lat/lon GmbH, info@lat-lon.de, www.lat-lon.de
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package org.deegree.ogcapi.config.actions;

import org.deegree.commons.config.DeegreeWorkspace;
import org.deegree.commons.config.ResourceInitException;
import org.deegree.commons.utils.Pair;
import org.deegree.ogcapi.config.exceptions.InvalidPathException;
import org.deegree.ogcapi.config.exceptions.ValidationException;
import org.deegree.services.controller.OGCFrontController;
import org.deegree.workspace.ErrorHandler;
import org.deegree.workspace.Resource;
import org.deegree.workspace.ResourceIdentifier;
import org.deegree.workspace.ResourceManager;
import org.deegree.workspace.ResourceMetadata;
import org.deegree.workspace.Workspace;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Action to validate files of a workspace.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class Validate {

	private static final Logger LOG = getLogger(Validate.class);

	/**
	 * @return
	 * @throws IOException
	 */
	public static String validate() throws ValidationException {
		DeegreeWorkspace workspace = OGCFrontController.getServiceWorkspace();
		try {
			workspace.destroyAll();
			workspace.initAll();
		}
		catch (ResourceInitException e) {
			throw new ValidationException(e);
		}
		return validate(workspace);
	}

	/**
	 * @param path identifying the resource to validate, never <code>null</code>
	 * @return
	 * @throws IOException
	 */
	public static String validate(String path) throws ValidationException, InvalidPathException {
		DeegreeWorkspace workspace = OGCFrontController.getServiceWorkspace();
		try {
			workspace.destroyAll();
			workspace.initAll();
		}
		catch (ResourceInitException e) {
			throw new ValidationException(e);
		}
		return validate(workspace, path);
	}

	private static String validate(DeegreeWorkspace ws, String file) throws InvalidPathException {
		File wsLocation = ws.getLocation();
		File requestedPath = new File(wsLocation, file);
		if (!requestedPath.exists()) {
			throw new InvalidPathException(ws.getName(), file);
		}

		PathMatcher pathMatcher = createPatternMatcher(requestedPath);
		Map<String, List<String>> resourcesToErrors = validateWithMatcher(ws, pathMatcher);
		String wsName = ws.getName();
		StringBuilder sb = new StringBuilder();
		if (resourcesToErrors.isEmpty()) {
			sb.append("Resource ").append(file).append(" in workspace ").append(wsName).append(" is valid.");
		}
		else {
			sb.append("Resource ")
				.append(file)
				.append(" in workspace ")
				.append(wsName)
				.append(" is not valid. The files with errors are shown below.");
			writeErrors(resourcesToErrors, sb);
		}
		return sb.toString();
	}

	private static String validate(DeegreeWorkspace ws) {
		StringBuilder sb = new StringBuilder();
		Map<String, List<String>> resourcesToErrors = validateWithMatcher(ws, null);
		String wsName = ws.getName();
		if (resourcesToErrors.isEmpty()) {
			sb.append("Workspace ").append(wsName).append(" is valid.");
		}
		else {
			sb.append("Workspace ").append(wsName).append(" is not valid. The files with errors are shown below.");
			writeErrors(resourcesToErrors, sb);
		}
		return sb.toString();
	}

	private static Map<String, List<String>> validateWithMatcher(DeegreeWorkspace ws, PathMatcher pathMatcher) {
		Workspace newWorkspace = ws.getNewWorkspace();
		ErrorHandler errorHandler = newWorkspace.getErrorHandler();
		return collectErrors(ws, newWorkspace, pathMatcher, errorHandler);
	}

	private static void writeErrors(Map<String, List<String>> resourcesToErrors, StringBuilder sb) {
		for (Map.Entry<String, List<String>> resourceToErrors : resourcesToErrors.entrySet()) {
			sb.append("\n");
			sb.append(":\n");
			for (String error : resourceToErrors.getValue()) {
				sb.append("   - ").append(error).append("\n");
			}
		}
	}

	private static Map<String, List<String>> collectErrors(DeegreeWorkspace ws, Workspace newWorkspace,
			PathMatcher pathMatcher, ErrorHandler errorHandler) {
		Map<String, List<String>> resourceToErrors = new TreeMap<>();
		if (errorHandler.hasErrors()) {
			List<ResourceManager<? extends Resource>> resourceManagers = newWorkspace.getResourceManagers();
			for (ResourceManager<? extends Resource> resourceManager : resourceManagers) {
				Collection<? extends ResourceMetadata<? extends Resource>> resourceMetadata = resourceManager
					.getResourceMetadata();
				for (ResourceMetadata<? extends Resource> rm : resourceMetadata) {
					collectErrors(ws, rm, pathMatcher, errorHandler, resourceToErrors);
				}
			}
		}
		return resourceToErrors;
	}

	private static Map<String, List<String>> collectErrors(DeegreeWorkspace ws, ResourceMetadata<? extends Resource> rm,
			PathMatcher pathMatcher, ErrorHandler errorHandler, Map<String, List<String>> resourceToErrors) {
		File resourceLocation = rm.getLocation().getAsFile();
		if (resourceLocation != null) {
			ResourceIdentifier<? extends Resource> identifier = rm.getIdentifier();
			List<String> errors = errorHandler.getErrors(identifier);
			if (isResourceRequestedAndHasErrors(pathMatcher, resourceLocation, errors)) {
				String id = retrieveIdentifierWithPath(ws, rm, resourceLocation);
				resourceToErrors.put(id, errors);
			}
		}
		else {
			LOG.warn("Validation of resources without file location is not implemented yet.");
		}
		return resourceToErrors;
	}

	private static boolean isResourceRequestedAndHasErrors(PathMatcher pathMatcher, File resourceLocation,
			List<String> errors) {
		return !errors.isEmpty() && resourceLocation != null
				&& (pathMatcher == null || pathMatcher.matches(resourceLocation.toPath()));
	}

	private static PathMatcher createPatternMatcher(File requestedPath) {
		String pattern = requestedPath.toString();
		FileSystem fileSystem = FileSystems.getDefault();
		if (requestedPath.isDirectory())
			pattern = pattern + fileSystem.getSeparator() + "*";
		pattern = pattern.replace("\\", "\\\\");
		return fileSystem.getPathMatcher("glob:" + pattern);
	}

	private static String retrieveIdentifierWithPath(DeegreeWorkspace ws, ResourceMetadata<? extends Resource> rm,
			File resourceLocation) {
		File wsLocation = ws.getLocation();
		URI identifierWithPath = wsLocation.toURI().relativize(resourceLocation.toURI());
		return identifierWithPath.toString();
	}

}
