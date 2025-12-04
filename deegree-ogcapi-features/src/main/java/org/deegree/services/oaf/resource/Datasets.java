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

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.TEXT_HTML;
import static org.deegree.services.oaf.RequestFormat.HTML;
import static org.deegree.services.oaf.RequestFormat.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.deegree.services.oaf.RequestedMediaType;
import org.deegree.services.oaf.config.datasets.DatasetsConfiguration;
import org.deegree.services.oaf.domain.dataset.Dataset;
import org.deegree.services.oaf.exceptions.InvalidParameterValue;
import org.deegree.services.oaf.link.Link;
import org.deegree.services.oaf.link.LinkBuilder;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;
import org.deegree.services.oaf.workspace.configuration.OafDatasets;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Path("/datasets")
public class Datasets {

	@Inject
	private DeegreeWorkspaceInitializer deegreeWorkspaceInitializer;

	@GET
	@Produces({ APPLICATION_JSON })
	@Operation(hidden = true)
	public Response datasetsJson(@Context UriInfo uriInfo,
			@Parameter(description = "The request output format.", style = ParameterStyle.FORM,
					schema = @Schema(allowableValues = { "json", "html", "xml" })) @QueryParam("f") String format)
			throws InvalidParameterValue {
		RequestedMediaType requestedMediaType = new RequestedMediaType(format, JSON, APPLICATION_JSON);
		return datasets(uriInfo, requestedMediaType);
	}

	@GET
	@Produces({ TEXT_HTML })
	@Operation(hidden = true)
	public Response datasetsHtml(@Context UriInfo uriInfo,
			@Parameter(description = "The request output format.", style = ParameterStyle.FORM,
					schema = @Schema(allowableValues = { "json", "html", "xml" })) @QueryParam("f") String format)
			throws InvalidParameterValue {
		RequestedMediaType requestedMediaType = new RequestedMediaType(format, HTML, TEXT_HTML);
		return datasets(uriInfo, requestedMediaType);
	}

	private Response datasets(UriInfo uriInfo, RequestedMediaType requestedMediaType) throws InvalidParameterValue {
		if (HTML.equals(requestedMediaType.getRequestFormat())) {
			return Response.ok(getClass().getResourceAsStream("/datasets.html"), TEXT_HTML).build();
		}
		LinkBuilder linkBuilder = new LinkBuilder(uriInfo, requestedMediaType.getSelfMediaType());
		List<Link> links = linkBuilder.createDatasetsLinks();
		List<Dataset> datasets = new ArrayList<>();

		OafDatasets oafDatasets = deegreeWorkspaceInitializer.getOafDatasets();
		Map<String, OafDatasetConfiguration> datasetsConfigurations = oafDatasets.getDatasets();
		datasetsConfigurations.forEach((id, oafDatasetConfiguration) -> {
			List<Link> datasetLinks = linkBuilder.createDatasetLinks(id);
			String title = oafDatasetConfiguration.getServiceMetadata() == null ? null
					: oafDatasetConfiguration.getServiceMetadata().getTitle();
			Dataset dataset = new Dataset(id, title, datasetLinks);
			datasets.add(dataset);
		});
		DatasetsConfiguration datasetsConfiguration = deegreeWorkspaceInitializer.getDatasetsConfiguration();
		org.deegree.services.oaf.domain.dataset.Datasets allDatasets = new org.deegree.services.oaf.domain.dataset.Datasets()
			.withLinks(links)
			.withDatasets(datasets)
			.withDatasetsConfiguration(datasetsConfiguration);
		return Response.ok(allDatasets, APPLICATION_JSON).build();
	}

}
