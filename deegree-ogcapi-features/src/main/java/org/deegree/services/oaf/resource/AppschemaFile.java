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
package org.deegree.services.oaf.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.deegree.services.oaf.exceptions.UnknownAppschema;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static jakarta.ws.rs.core.MediaType.APPLICATION_XML;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Path("/appschemas/{path: .+\\.xsd$}")
public class AppschemaFile {

	@Inject
	private DeegreeWorkspaceInitializer deegreeWorkspaceInitializer;

	@GET
	@Produces({ APPLICATION_XML })
	@Operation(hidden = true, operationId = "appschema",
			summary = "retrieve application schema of collection {collectionId}",
			description = "Retrieves the application schema of the collection with the id {collectionId}")
	@Tag(name = "Schema")
	public InputStream appschemaFile(@Context UriInfo uriInfo, @PathParam("path") String path)
			throws UnknownAppschema, IOException {
		java.nio.file.Path appschemaFile = deegreeWorkspaceInitializer.getAppschemaFile(path);
		return Files.newInputStream(appschemaFile);
	}

}
