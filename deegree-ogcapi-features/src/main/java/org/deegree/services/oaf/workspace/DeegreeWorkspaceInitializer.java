/*-
 * #%L
 * deegree-ogcapi-features - OGC API Features (OAF) implementation - Querying and modifying of geospatial data objects
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
package org.deegree.services.oaf.workspace;

import org.deegree.commons.config.DeegreeWorkspace;
import org.deegree.services.controller.OGCFrontController;
import org.deegree.services.oaf.OafResource;
import org.deegree.services.oaf.OgcApiProvider;
import org.deegree.services.oaf.config.datasets.DatasetsConfigResource;
import org.deegree.services.oaf.config.datasets.DatasetsConfiguration;
import org.deegree.services.oaf.config.datasets.OgcApiDatasetsProvider;
import org.deegree.services.oaf.config.htmlview.HtmlViewConfigResource;
import org.deegree.services.oaf.config.htmlview.HtmlViewConfiguration;
import org.deegree.services.oaf.config.htmlview.OgcApiConfigProvider;
import org.deegree.services.oaf.exceptions.UnknownAppschema;
import org.deegree.services.oaf.link.LinkBuilder;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;
import org.deegree.services.oaf.workspace.configuration.OafDatasets;
import org.deegree.services.ogcapi.features.DeegreeOAF.ConfigureCollection;
import org.deegree.services.ogcapi.features.DeegreeOAF.ConfigureCollections;
import org.deegree.workspace.Resource;
import org.deegree.workspace.ResourceIdentifier;
import org.deegree.workspace.Workspace;
import org.slf4j.Logger;

import jakarta.ws.rs.core.UriInfo;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Instantiation of the deegree workspaces. This is a workaround!
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class DeegreeWorkspaceInitializer {

	private static final Logger LOG = getLogger(DeegreeWorkspaceInitializer.class);

	private static final String APPSCHEMAS_PATH = "appschemas";

	private static Path pathToAppschemas;

	private static DatasetsConfiguration datasetsConfiguration;

	private static OafDatasets oafConfiguration = new OafDatasets();

	private static Map<String, HtmlViewConfiguration> htmlViewConfigurations = new HashMap<>();

	private static HtmlViewConfiguration globalHtmlViewConfiguration;

	private static Map<String, List<ConfigureCollection>> additionalCollectionMap = new HashMap<>();

	private static Map<String, List<ConfigureCollections>> additionalCollectionsMap = new HashMap<>();

	public void initialize() {
		DeegreeWorkspace workspace = OGCFrontController.getServiceWorkspace();
		initConfiguration(workspace.getNewWorkspace());
		pathToAppschemas = resolveAppschemasPath(workspace);
	}

	public void reinitialize() {
		LOG.info("Reinitialize workspace");
		clearConfigs();
		DeegreeWorkspace workspace = OGCFrontController.getServiceWorkspace();
		initConfiguration(workspace.getNewWorkspace());
		pathToAppschemas = resolveAppschemasPath(workspace);
	}

	public OafDatasets getOafDatasets() {
		return oafConfiguration;
	}

	/**
	 * @param datasetId the id of the dataset
	 * @return the {@link HtmlViewConfiguration} of the dataset with the passed id,
	 * <code>null</code> if not available
	 */
	public HtmlViewConfiguration getHtmlViewConfiguration(String datasetId) {
		if (htmlViewConfigurations.containsKey(datasetId))
			return htmlViewConfigurations.get(datasetId);
		return null;
	}

	/**
	 * @return the global {@link HtmlViewConfiguration}, <code>null</code> if not
	 * available
	 */
	public HtmlViewConfiguration getGlobalHtmlViewConfiguration() {
		return globalHtmlViewConfiguration;
	}

	/**
	 * @return the datasets configuration, may be <code>null</code>
	 */
	public DatasetsConfiguration getDatasetsConfiguration() {
		return datasetsConfiguration;
	}

	/**
	 * @param path th erelative path to the appschema (relative to
	 * CURRENT_WORKSPCAE/appschemas)
	 * @return the path to the appschema, never <code>null</code>
	 * @throws UnknownAppschema if the path does not address an existing appschema
	 * relative to CURRENT_WORKSPCAE/appschemas
	 */
	public Path getAppschemaFile(String path) throws UnknownAppschema {
		Path appschema = pathToAppschemas.resolve(path);
		if (!Files.exists(appschema) || !Files.isReadable(appschema))
			throw new UnknownAppschema(path);
		return appschema;
	}

	public static Map<String, List<ConfigureCollection>> getAdditionalCollectionMap() {
		return additionalCollectionMap;
	}

	public static Map<String, List<ConfigureCollections>> getAdditionalCollectionsMap() {
		return additionalCollectionsMap;
	}

	public String createAppschemaUrl(UriInfo uriInfo, String uri) {
		Path uriPath = Path.of(URI.create(uri));
		if (uriPath.startsWith(pathToAppschemas)) {
			Path relativizeUriPath = pathToAppschemas.relativize(uriPath);
			LinkBuilder linkBuilder = new LinkBuilder(uriInfo);
			return linkBuilder.createSchemaLink(relativizeUriPath.toString());
		}
		return null;
	}

	private void initConfiguration(Workspace newWorkspace) {
		initOafDatasets(newWorkspace);
		initGlobalHtmlView(newWorkspace);
		initDatasets(newWorkspace);
	}

	private void initDatasets(Workspace newWorkspace) {
		List<ResourceIdentifier<DatasetsConfigResource>> datasetsResourceIdentifier = newWorkspace
			.getResourcesOfType(OgcApiDatasetsProvider.class);
		if (datasetsResourceIdentifier.size() > 1)
			LOG.warn("Multiple datasets configurations are available. They are ignored!");
		if (datasetsResourceIdentifier.size() == 1) {
			String id = datasetsResourceIdentifier.get(0).getId();
			DatasetsConfigResource datasetsConfigResource = newWorkspace.getResource(OgcApiDatasetsProvider.class, id);
			datasetsConfiguration = datasetsConfigResource.getDatasetsConfiguration();
		}
	}

	private void initGlobalHtmlView(Workspace newWorkspace) {
		HtmlViewConfigResource globalHtmlViewConfigResource = newWorkspace.getResource(OgcApiConfigProvider.class,
				"htmlview");
		if (globalHtmlViewConfigResource != null)
			globalHtmlViewConfiguration = globalHtmlViewConfigResource.getHtmlViewConfiguration();
	}

	private void initOafDatasets(Workspace newWorkspace) {
		List<ResourceIdentifier<Resource>> oafResourceIdentifiers = newWorkspace
			.getResourcesOfType(OgcApiProvider.class);
		oafResourceIdentifiers.forEach(resourceResourceIdentifier -> {
			String id = resourceResourceIdentifier.getId();
			OafResource resource = (OafResource) newWorkspace.getResource(OgcApiProvider.class, id);
			OafDatasetConfiguration oafDatasetConfiguration = resource.getOafConfiguration();
			oafConfiguration.addDataset(id, oafDatasetConfiguration);
			HtmlViewConfiguration htmlViewConfiguration = resource.getHtmlViewConfiguration();
			if (htmlViewConfiguration != null)
				htmlViewConfigurations.put(id, htmlViewConfiguration);
			additionalCollectionMap.put(id, resource.getAdditionalCollectionList());
			additionalCollectionsMap.put(id, resource.getAdditionalCollectionsList());
		});
	}

	private void clearConfigs() {
		oafConfiguration = new OafDatasets();
		htmlViewConfigurations.clear();
		globalHtmlViewConfiguration = null;
		datasetsConfiguration = null;
	}

	private Path resolveAppschemasPath(DeegreeWorkspace workspace) {
		File workspaceLocation = workspace.getLocation();
		return Path.of(workspaceLocation.toURI()).resolve(APPSCHEMAS_PATH);
	}

}
