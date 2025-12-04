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
import static jakarta.ws.rs.core.MediaType.APPLICATION_XML;
import static jakarta.ws.rs.core.MediaType.TEXT_HTML;
import static org.deegree.services.oaf.RequestFormat.HTML;
import static org.deegree.services.oaf.RequestFormat.JSON;
import static org.deegree.services.oaf.RequestFormat.XML;
import static org.deegree.services.oaf.RequestFormat.byFormatParameter;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.deegree.services.oaf.RequestFormat;
import org.deegree.services.oaf.exceptions.InvalidParameterValue;
import org.deegree.services.oaf.exceptions.UnknownDatasetId;
import org.deegree.services.oaf.link.Link;
import org.deegree.services.oaf.link.LinkBuilder;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.deegree.services.oaf.workspace.configuration.DatasetMetadata;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Path("/datasets/{datasetId}")
public class LandingPage {

	@Inject
	private DeegreeWorkspaceInitializer deegreeWorkspaceInitializer;

	@GET
	@Produces({ APPLICATION_JSON })
	@Operation(operationId = "landingPage", summary = "landing page", description = "Landing page of this dataset")
	@Tag(name = "Capabilities")
	@ApiResponse(description = "default response",
			content = @Content(schema = @Schema(implementation = LandingPage.class)))
	public Response landingPageJson(@Context UriInfo uriInfo,
			@Parameter(description = "The request output format.", style = ParameterStyle.FORM,
					schema = @Schema(allowableValues = { "json", "html", "xml" })) @QueryParam("f") String format,
			@PathParam("datasetId") String datasetId) throws UnknownDatasetId, InvalidParameterValue {
		return landingPage(uriInfo, datasetId, format, JSON, APPLICATION_JSON);
	}

	@GET
	@Produces({ APPLICATION_XML })
	@Tag(name = "Capabilities")
	@Operation(hidden = true)
	public Response landingPageJsonXml(@Context UriInfo uriInfo,
			@Parameter(description = "The request output format.", style = ParameterStyle.FORM,
					schema = @Schema(allowableValues = { "json", "html", "xml" })) @QueryParam("f") String format,
			@PathParam("datasetId") String datasetId) throws UnknownDatasetId, InvalidParameterValue {
		return landingPage(uriInfo, datasetId, format, XML, APPLICATION_XML);
	}

	@GET
	@Produces({ TEXT_HTML })
	@Operation(hidden = true)
	public Response landingPageHtml(@Context UriInfo uriInfo,
			@Parameter(description = "The request output format.", style = ParameterStyle.FORM,
					schema = @Schema(allowableValues = { "json", "html", "xml" })) @QueryParam("f") String format,
			@PathParam("datasetId") String datasetId) throws UnknownDatasetId, InvalidParameterValue {
		return landingPage(uriInfo, datasetId, format, HTML, TEXT_HTML);
	}

	@GET
	@Operation(hidden = true)
	public Response landingPageOther(@Context UriInfo uriInfo,
			@Parameter(description = "The request output format.", style = ParameterStyle.FORM,
					schema = @Schema(allowableValues = { "json", "html", "xml" })) @QueryParam("f") String format,
			@PathParam("datasetId") String datasetId) throws UnknownDatasetId, InvalidParameterValue {
		return landingPage(uriInfo, datasetId, format, JSON, APPLICATION_JSON);
	}

	private Response landingPage(UriInfo uriInfo, String datasetId, String formatParamValue,
			RequestFormat defaultFormat, String requestedMediaTyp) throws UnknownDatasetId, InvalidParameterValue {
		OafDatasetConfiguration dataset = deegreeWorkspaceInitializer.getOafDatasets().getDataset(datasetId);
		RequestFormat requestFormat = byFormatParameter(formatParamValue, defaultFormat);
		if (HTML.equals(requestFormat)) {
			return Response.ok(getClass().getResourceAsStream("/landingpage.html"), TEXT_HTML).build();
		}

		DatasetMetadata metadata = dataset.getServiceMetadata();

		LinkBuilder linkBuilder = new LinkBuilder(uriInfo, requestedMediaTyp);
		List<Link> links = linkBuilder.createLandingPageLinks(datasetId, metadata);
		org.deegree.services.oaf.domain.landingpage.LandingPage landingPage = new org.deegree.services.oaf.domain.landingpage.LandingPage(
				metadata.getTitle(), metadata.getDescription(), links);
		landingPage.setContact(metadata.getCreatorContact());
		return Response.ok(landingPage, mediaTypeFromRequestFormat(requestFormat)).build();
	}

	private String mediaTypeFromRequestFormat(RequestFormat requestFormat) {
		return XML.equals(requestFormat) ? APPLICATION_XML : APPLICATION_JSON;
	}

}
