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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.deegree.services.oaf.exceptions.UnknownDatasetId;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.deegree.services.oaf.workspace.configuration.DatasetMetadata;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Path("/datasets/{datasetId}/license")
public class License {

	@Inject
	private DeegreeWorkspaceInitializer deegreeWorkspaceInitializer;

	@Path("/provider")
	@GET
	@Produces({ TEXT_PLAIN })
	@Operation(summary = "License", description = "License of all collections of this datasets",
			responses = { @ApiResponse(description = "default response", content = @Content(mediaType = "text/plain")),
					@ApiResponse(responseCode = "404", description = "No license available",
							content = @Content(mediaType = "text/plain")) })
	@Tag(name = "Capabilities")
	public Response providerLicense(@PathParam("datasetId") String datasetId) throws UnknownDatasetId {
		OafDatasetConfiguration dataset = deegreeWorkspaceInitializer.getOafDatasets().getDataset(datasetId);
		DatasetMetadata metadata = dataset.getServiceMetadata();

		if (metadata.hasProviderLicenseUrl())
			return Response.status(Response.Status.NOT_FOUND).entity("No license available").build();
		return Response.ok(metadata.getProviderLicense().getDescription()).build();
	}

	@Path("/dataset")
	@GET
	@Produces({ TEXT_PLAIN })
	@Operation(summary = "License", description = "License of all collections of this datasets",
			responses = { @ApiResponse(description = "default response", content = @Content(mediaType = "text/plain")),
					@ApiResponse(responseCode = "404", description = "No license available",
							content = @Content(mediaType = "text/plain")) })
	@Tag(name = "Capabilities")
	public Response datasetLicense(@PathParam("datasetId") String datasetId) throws UnknownDatasetId {
		OafDatasetConfiguration dataset = deegreeWorkspaceInitializer.getOafDatasets().getDataset(datasetId);
		DatasetMetadata metadata = dataset.getServiceMetadata();

		if (metadata.hasDatasetLicenseUrl())
			return Response.status(Response.Status.NOT_FOUND).entity("No license available").build();
		return Response.ok(metadata.getDatasetLicense().getDescription()).build();
	}

}
