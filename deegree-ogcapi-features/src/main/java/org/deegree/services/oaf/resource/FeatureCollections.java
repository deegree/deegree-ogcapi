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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.deegree.services.oaf.RequestFormat;
import org.deegree.services.oaf.domain.collections.Collection;
import org.deegree.services.oaf.domain.collections.Collections;
import org.deegree.services.oaf.exceptions.InvalidParameterValue;
import org.deegree.services.oaf.exceptions.UnknownDatasetId;
import org.deegree.services.oaf.link.Link;
import org.deegree.services.oaf.link.LinkBuilder;
import org.deegree.services.oaf.workspace.DataAccess;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;
import org.deegree.services.ogcapi.features.AddLink;
import org.deegree.services.ogcapi.features.DeegreeOAF.ConfigureCollection;
import org.deegree.services.ogcapi.features.DeegreeOAF.ConfigureCollections;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_XML;
import static jakarta.ws.rs.core.MediaType.TEXT_HTML;
import static org.deegree.services.oaf.RequestFormat.HTML;
import static org.deegree.services.oaf.RequestFormat.JSON;
import static org.deegree.services.oaf.RequestFormat.XML;
import static org.deegree.services.oaf.RequestFormat.byFormatParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Path("/datasets/{datasetId}/collections")
public class FeatureCollections {

	@Inject
	private DeegreeWorkspaceInitializer deegreeWorkspaceInitializer;

	@Inject
	private DataAccess dataAccess;

	@GET
	@Produces({ APPLICATION_JSON })
	@Operation(operationId = "collections", summary = "describes collections",
			description = "Describes all collections provided by this service")
	@Tag(name = "Collections")
	@ApiResponse(description = "default response",
			content = @Content(schema = @Schema(implementation = Collections.class)))
	public Response collectionsJson(@PathParam("datasetId") String datasetId,
			@Parameter(description = "The request output format.", style = ParameterStyle.FORM,
					schema = @Schema(allowableValues = { "json", "html", "xml" })) @QueryParam("f") String format,
			@Context UriInfo uriInfo) throws UnknownDatasetId, InvalidParameterValue {
		return collections(datasetId, uriInfo, format, JSON);
	}

	@GET
	@Produces({ APPLICATION_XML })
	@Operation(hidden = true)
	public Response collectionsXml(@PathParam("datasetId") String datasetId,
			@Parameter(description = "The request output format.", style = ParameterStyle.FORM,
					schema = @Schema(allowableValues = { "json", "html", "xml" })) @QueryParam("f") String format,
			@Context UriInfo uriInfo) throws UnknownDatasetId, InvalidParameterValue {
		return collections(datasetId, uriInfo, format, XML);
	}

	@GET
	@Produces({ TEXT_HTML })
	@Operation(hidden = true)
	public Response collectionsHtml(@PathParam("datasetId") String datasetId, @Context UriInfo uriInfo,
			@Parameter(description = "The request output format.", style = ParameterStyle.FORM,
					schema = @Schema(allowableValues = { "json", "html", "xml" })) @QueryParam("f") String format)
			throws UnknownDatasetId, InvalidParameterValue {
		return collections(datasetId, uriInfo, format, HTML);
	}

	@GET
	@Operation(hidden = true)
	public Response collectionsOther(@PathParam("datasetId") String datasetId, @Context UriInfo uriInfo,
			@Parameter(description = "The request output format.", style = ParameterStyle.FORM,
					schema = @Schema(allowableValues = { "json", "html", "xml" })) @QueryParam("f") String format)
			throws UnknownDatasetId, InvalidParameterValue {
		return collections(datasetId, uriInfo, format, JSON);
	}

	private Response collections(String datasetId, UriInfo uriInfo, String formatParamValue,
			RequestFormat defaultFormat) throws UnknownDatasetId, InvalidParameterValue {
		RequestFormat requestFormat = byFormatParameter(formatParamValue, defaultFormat);
		OafDatasetConfiguration oafConfiguration = deegreeWorkspaceInitializer.getOafDatasets().getDataset(datasetId);
		if (HTML.equals(requestFormat)) {
			return Response.ok(getClass().getResourceAsStream("/collections.html"), TEXT_HTML).build();
		}

		LinkBuilder linkBuilder = new LinkBuilder(uriInfo);
		Collections collections = dataAccess.createCollections(oafConfiguration, linkBuilder);
		addAdditionalCollectionsLinks(datasetId, collections);
		for (Collection collection : collections.getCollections()) {
			addAdditionalCollectionLinks(datasetId, collection);
		}

		return Response.ok(collections, mediaTypeFromRequestFormat(requestFormat)).build();
	}

	private void addAdditionalCollectionsLinks(String datasetId, Collections collections) {
		Map<String, List<ConfigureCollections>> additionalCollectionsMap = DeegreeWorkspaceInitializer
			.getAdditionalCollectionsMap();

		if (additionalCollectionsMap.containsKey(datasetId)) {
			List<ConfigureCollections> configureCollectionsList = additionalCollectionsMap.get(datasetId);
			for (ConfigureCollections additionalcolls : configureCollectionsList) {
				List<AddLink> addLinks = additionalcolls.getAddLink();
				if (addLinks != null) {
					List<Link> oafLinks = new ArrayList<>();
					for (AddLink addLink : addLinks) {
						oafLinks
							.add(new Link(addLink.getHref(), addLink.getRel(), addLink.getType(), addLink.getTitle()));
					}
					collections.addAdditionalLinks(oafLinks);
				}
			}
		}
	}

	private void addAdditionalCollectionLinks(String datasetId, Collection collection) {
		Map<String, List<ConfigureCollection>> additionalCollectionMap = DeegreeWorkspaceInitializer
			.getAdditionalCollectionMap();

		if (additionalCollectionMap.containsKey(datasetId)) {
			List<ConfigureCollection> configureCollectionList = additionalCollectionMap.get(datasetId);
			for (ConfigureCollection additionalcoll : configureCollectionList) {
				if (additionalcoll != null && collection.getId().equals(additionalcoll.getId())) {
					List<AddLink> addLinks = additionalcoll.getAddLink();
					if (addLinks != null) {
						List<Link> oafLinks = new ArrayList<>();
						for (AddLink addLink : addLinks) {
							oafLinks.add(new Link(addLink.getHref(), addLink.getRel(), addLink.getType(),
									addLink.getTitle()));
						}
						collection.addAdditionalLinks(oafLinks);
					}
				}
			}
		}
	}

	private String mediaTypeFromRequestFormat(RequestFormat requestFormat) {
		return XML.equals(requestFormat) ? APPLICATION_XML : APPLICATION_JSON;
	}

}
