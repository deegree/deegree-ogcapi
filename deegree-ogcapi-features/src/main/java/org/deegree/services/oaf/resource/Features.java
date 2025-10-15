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
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.deegree.services.oaf.RequestFormat;
import org.deegree.services.oaf.exceptions.InternalQueryException;
import org.deegree.services.oaf.exceptions.InvalidParameterValue;
import org.deegree.services.oaf.exceptions.UnknownCollectionId;
import org.deegree.services.oaf.exceptions.UnknownDatasetId;
import org.deegree.services.oaf.io.response.FeaturesResponse;
import org.deegree.services.oaf.io.response.FeaturesResponseCreator;
import org.deegree.services.oaf.io.request.FeaturesRequest;
import org.deegree.services.oaf.io.request.FeaturesRequestBuilder;
import org.deegree.services.oaf.link.LinkBuilder;
import org.deegree.services.oaf.domain.FilterLang;
import org.deegree.services.oaf.workspace.DataAccess;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.deegree.services.oaf.workspace.configuration.FeatureTypeMetadata;
import org.deegree.cql2.FilterProperty;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jakarta.ws.rs.core.MediaType.TEXT_HTML;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GEOJSON;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_32;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_SF0;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_SF2;
import static org.deegree.services.oaf.RequestFormat.HTML;
import static org.deegree.services.oaf.RequestFormat.JSON;
import static org.deegree.services.oaf.RequestFormat.XML;
import static org.deegree.services.oaf.RequestFormat.byFormatParameter;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Path("/datasets/{datasetId}/collections/{collectionId}/items")
public class Features {

	private final FeaturesResponseCreator featureResponseCreator = new FeaturesResponseCreator();

	@Inject
	private DeegreeWorkspaceInitializer deegreeWorkspaceInitializer;

	@Inject
	private DataAccess dataAccess;

	@GET
	@Produces({ APPLICATION_GEOJSON })
	@Operation(operationId = "features", summary = "retrieves features of collection {collectionId}",
			description = "Retrieves the features of the collection with the id {collectionId}")
	@Tag(name = "Data")
	public Response featuresGeoJson(@Context UriInfo uriInfo, @PathParam("datasetId") String datasetId,
			@PathParam("collectionId") String collectionId,
			@Parameter(
					description = "Limits the number of items presented in the response document. Ignored if bulk is true.",
					style = ParameterStyle.FORM,
					schema = @Schema(defaultValue = "10", minimum = "1",
							maximum = "1000")) @QueryParam("limit") int limit,
			@Parameter(
					description = "The start index of the items presented in the response document. Ignored if bulk is true.",
					style = ParameterStyle.FORM,
					schema = @Schema(defaultValue = "0", minimum = "0")) @QueryParam("offset") int offset,
			@Parameter(
					description = "The bulk parameter is used to download all items of the collection. LIMIT and OFFSET are ignored if bulk is true.",
					style = ParameterStyle.FORM,
					schema = @Schema(defaultValue = "false")) @QueryParam("bulk") boolean bulk,
			@Parameter(
					description = "The bounding boxes that describe the spatial extent of the dataset [minx, miny, maxx, maxy]. Example: '567190,5934330,567200,5934360'",
					explode = Explode.FALSE, style = ParameterStyle.FORM,
					array = @ArraySchema(minItems = 4, maxItems = 6)) @QueryParam("bbox") List<Double> bbox,
			@Parameter(
					description = "The coordinate reference system of the value of the bbox parameter. Example: 'EPSG:25832' Default: http://www.opengis.net/def/crs/OGC/1.3/CRS84",
					style = ParameterStyle.FORM) @QueryParam("bbox-crs") String bboxCrs,
			@Parameter(
					description = "The datetime used as filter. Either a date-time or a period string that adheres to RFC 3339. Examples: A date-time: '2018-02-12T23:20:50Z' A period: '2018-02-12T00:00:00Z/2018-03-18T12:31:12Z' or '2018-02-12T00:00:00Z/P1M6DT12H31M12S''",
					style = ParameterStyle.FORM) @QueryParam("datetime") String datetime,
			@Parameter(description = "The filter expression to be applied when retrieving features.",
					style = ParameterStyle.FORM) @QueryParam("filter") String filter,
			@Parameter(
					description = "The CRS of the geometries used in the filter expression. Example: 'EPSG:25832'  Default: http://www.opengis.net/def/crs/OGC/1.3/CRS84",
					style = ParameterStyle.FORM) @QueryParam("filter-crs") String filterCrs,
			@Parameter(description = "The encoding of the filter parameter.", style = ParameterStyle.FORM,
					schema = @Schema(allowableValues = { "cql2-text" })) @QueryParam("filter-lang") String filterLang,
			@Parameter(
					description = "The coordinate reference system of the response geometries. Example: 'EPSG:25832' Default: http://www.opengis.net/def/crs/OGC/1.3/CRS84",
					style = ParameterStyle.FORM) @QueryParam("crs") String crs,
			@Parameter(description = "The request output format.", style = ParameterStyle.FORM,
					schema = @Schema(allowableValues = { "json", "html", "xml" })) @QueryParam("f") String format)
			throws UnknownCollectionId, InternalQueryException, InvalidParameterValue, UnknownDatasetId {
		return features(uriInfo, datasetId, collectionId, limit, offset, bulk, bbox, bboxCrs, datetime, filter,
				filterLang, filterCrs, crs, format, JSON);
	}

	@GET
	@Produces({ APPLICATION_GML, APPLICATION_GML_32, APPLICATION_GML_SF0, APPLICATION_GML_SF2 })
	@Operation(hidden = true)
	public Response featuresGml(@Context UriInfo uriInfo, @HeaderParam("Accept") String acceptHeader,
			@PathParam("datasetId") String datasetId, @PathParam("collectionId") String collectionId,
			@Parameter(
					description = "Limits the number of items presented in the response document. Ignored if bulk=true.",
					style = ParameterStyle.FORM,
					schema = @Schema(defaultValue = "10", minimum = "1",
							maximum = "1000")) @QueryParam("limit") int limit,
			@Parameter(
					description = "The start index of the items presented in the response document. Ignored if bulk=true.",
					style = ParameterStyle.FORM,
					schema = @Schema(defaultValue = "0", minimum = "0")) @QueryParam("offset") int offset,
			@Parameter(
					description = "The bulk parameter is used to download all items of the collection. LIMIT and OFFSET are ignored if bulk is true.",
					style = ParameterStyle.FORM,
					schema = @Schema(defaultValue = "false")) @QueryParam("bulk") boolean bulk,
			@Parameter(description = "The bounding boxes that describe the spatial extent of the dataset.",
					explode = Explode.FALSE, style = ParameterStyle.FORM,
					array = @ArraySchema(minItems = 4, maxItems = 6)) @QueryParam("bbox") List<Double> bbox,
			@Parameter(description = "The coordinate reference system of the value of the bbox parameter.",
					style = ParameterStyle.FORM) @QueryParam("bbox-crs") String bboxCrs,
			@Parameter(description = "The datetime used as filter.",
					style = ParameterStyle.FORM) @QueryParam("datetime") String datetime,
			@Parameter(description = "The filter expression to be applied when retrieving features.",
					style = ParameterStyle.FORM) @QueryParam("filter") String filter,
			@Parameter(
					description = "The CRS of the geometries used in the filter expression. Example: 'EPSG:25832'  Default: http://www.opengis.net/def/crs/OGC/1.3/CRS84",
					style = ParameterStyle.FORM) @QueryParam("filter-crs") String filterCrs,
			@Parameter(description = "The encoding of the filter parameter.", style = ParameterStyle.FORM,
					schema = @Schema(allowableValues = { "cql2-text" })) @QueryParam("filter-lang") String filterLang,
			@Parameter(description = "The coordinate reference system of the response geometries.",
					style = ParameterStyle.FORM) @QueryParam("crs") String crs,
			@Parameter(description = "The request output format.", style = ParameterStyle.FORM,
					schema = @Schema(allowableValues = { "json", "html", "xml" })) @QueryParam("f") String format)
			throws UnknownCollectionId, InternalQueryException, InvalidParameterValue, UnknownDatasetId {
		return features(uriInfo, datasetId, collectionId, limit, offset, bulk, bbox, bboxCrs, datetime, filter,
				filterLang, filterCrs, crs, format, XML, acceptHeader);
	}

	@GET
	@Produces({ TEXT_HTML })
	@Operation(hidden = true)
	public Response featuresHtml(@Context UriInfo uriInfo, @PathParam("datasetId") String datasetId,
			@PathParam("collectionId") String collectionId,
			@Parameter(
					description = "Limits the number of items presented in the response document. Ignored if bulk=true.",
					style = ParameterStyle.FORM,
					schema = @Schema(defaultValue = "10", minimum = "1",
							maximum = "1000")) @QueryParam("limit") int limit,
			@Parameter(
					description = "The start index of the items presented in the response document. Ignored if bulk=true.",
					style = ParameterStyle.FORM,
					schema = @Schema(defaultValue = "0", minimum = "0")) @QueryParam("offset") int offset,
			@Parameter(
					description = "The bulk parameter is used to download all items of the collection. LIMIT and OFFSET are ignored if bulk is true.",
					style = ParameterStyle.FORM,
					schema = @Schema(defaultValue = "false")) @QueryParam("bulk") boolean bulk,
			@Parameter(description = "The bounding boxes that describe the spatial extent of the dataset.",
					explode = Explode.FALSE, style = ParameterStyle.FORM,
					array = @ArraySchema(minItems = 4, maxItems = 6)) @QueryParam("bbox") List<Double> bbox,
			@Parameter(description = "The coordinate reference system of the value of the bbox parameter.",
					style = ParameterStyle.FORM) @QueryParam("bbox-crs") String bboxCrs,
			@Parameter(description = "The datetime used as filter.",
					style = ParameterStyle.FORM) @QueryParam("datetime") String datetime,
			@Parameter(description = "The filter expression to be applied when retrieving features.",
					style = ParameterStyle.FORM) @QueryParam("filter") String filter,
			@Parameter(
					description = "The CRS of the geometries used in the filter expression. Example: 'EPSG:25832'  Default: http://www.opengis.net/def/crs/OGC/1.3/CRS84",
					style = ParameterStyle.FORM) @QueryParam("filter-crs") String filterCrs,
			@Parameter(description = "The encoding of the filter parameter.", style = ParameterStyle.FORM,
					schema = @Schema(allowableValues = { "cql2-text" })) @QueryParam("filter-lang") String filterLang,
			@Parameter(description = "The coordinate reference system of the response geometries.",
					style = ParameterStyle.FORM) @QueryParam("crs") String crs,
			@Parameter(description = "The request output format.", style = ParameterStyle.FORM,
					schema = @Schema(allowableValues = { "json", "html", "xml" })) @QueryParam("f") String format)
			throws InvalidParameterValue, UnknownDatasetId, UnknownCollectionId, InternalQueryException {
		return features(uriInfo, datasetId, collectionId, limit, offset, bulk, bbox, bboxCrs, datetime, filter,
				filterLang, filterCrs, crs, format, HTML);
	}

	@GET
	@Operation(hidden = true)
	public Response featuresOther(@Context UriInfo uriInfo, @PathParam("datasetId") String datasetId,
			@PathParam("collectionId") String collectionId,
			@Parameter(
					description = "Limits the number of items presented in the response document. Ignored if bulk=true.",
					style = ParameterStyle.FORM,
					schema = @Schema(defaultValue = "10", minimum = "1",
							maximum = "1000")) @QueryParam("limit") int limit,
			@Parameter(
					description = "The start index of the items presented in the response document. Ignored if bulk=true.",
					style = ParameterStyle.FORM,
					schema = @Schema(defaultValue = "0", minimum = "0")) @QueryParam("offset") int offset,
			@Parameter(
					description = "The bulk parameter is used to download all items of the collection. LIMIT and OFFSET are ignored if bulk is true.",
					style = ParameterStyle.FORM,
					schema = @Schema(defaultValue = "false")) @QueryParam("bulk") boolean bulk,
			@Parameter(description = "The bounding boxes that describe the spatial extent of the dataset.",
					explode = Explode.FALSE, style = ParameterStyle.FORM,
					array = @ArraySchema(minItems = 4, maxItems = 6)) @QueryParam("bbox") List<Double> bbox,
			@Parameter(description = "The coordinate reference system of the value of the bbox parameter.",
					style = ParameterStyle.FORM) @QueryParam("bbox-crs") String bboxCrs,
			@Parameter(description = "The datetime used as filter.",
					style = ParameterStyle.FORM) @QueryParam("datetime") String datetime,
			@Parameter(description = "The filter expression to be applied when retrieving features.",
					style = ParameterStyle.FORM) @QueryParam("filter") String filter,
			@Parameter(
					description = "The CRS of the geometries used in the filter expression. Example: 'EPSG:25832'  Default: http://www.opengis.net/def/crs/OGC/1.3/CRS84",
					style = ParameterStyle.FORM) @QueryParam("filter-crs") String filterCrs,
			@Parameter(description = "The encoding of the filter parameter.", style = ParameterStyle.FORM,
					schema = @Schema(allowableValues = { "cql2-text" })) @QueryParam("filter-lang") String filterLang,
			@Parameter(description = "The coordinate reference system of the response geometries.",
					style = ParameterStyle.FORM) @QueryParam("crs") String crs,
			@Parameter(description = "The request output format.", style = ParameterStyle.FORM,
					schema = @Schema(allowableValues = { "json", "html", "xml" })) @QueryParam("f") String format)
			throws InvalidParameterValue, UnknownDatasetId, UnknownCollectionId, InternalQueryException {
		return features(uriInfo, datasetId, collectionId, limit, offset, bulk, bbox, bboxCrs, datetime, filter,
				filterLang, filterCrs, crs, format, JSON);
	}

	private Response features(UriInfo uriInfo, String datasetId, String collectionId, int limit, int offset,
			boolean isBulkUpload, List<Double> bbox, String bboxCrs, String datetime, String filter, String filterLang,
			String filterCrs, String crs, String format, RequestFormat defaultFormat)
			throws InvalidParameterValue, UnknownDatasetId, UnknownCollectionId, InternalQueryException {
		return features(uriInfo, datasetId, collectionId, limit, offset, isBulkUpload, bbox, bboxCrs, datetime, filter,
				filterLang, filterCrs, crs, format, defaultFormat, null);
	}

	private Response features(UriInfo uriInfo, String datasetId, String collectionId, int limit, int offset,
			boolean isBulkUpload, List<Double> bbox, String bboxCrs, String datetime, String filter, String filterLang,
			String filterCrs, String crs, String formatParamValue, RequestFormat defaultFormat, String acceptHeader)
			throws UnknownDatasetId, InvalidParameterValue, UnknownCollectionId, InternalQueryException {
		FilterLang.fromType(filterLang);
		RequestFormat requestFormat = byFormatParameter(formatParamValue, defaultFormat);
		OafDatasetConfiguration oafConfiguration = deegreeWorkspaceInitializer.getOafDatasets().getDataset(datasetId);
		oafConfiguration.checkCollection(collectionId);
		if (HTML.equals(requestFormat)) {
			return Response.ok(getClass().getResourceAsStream("/features.html"), TEXT_HTML).build();
		}

		Map<FilterProperty, List<String>> filterParameters = findQueryableParameters(datasetId, collectionId,
				uriInfo.getQueryParameters());

		FeaturesRequest featuresRequest = new FeaturesRequestBuilder(collectionId).withLimit(limit)
			.withOffset(offset)
			.withBulkUpload(isBulkUpload)
			.withBbox(bbox, bboxCrs)
			.withDatetime(datetime)
			.withResponseCrs(crs)
			.withQueryableParameters(filterParameters)
			.withFilter(filter, filterCrs)
			.build();
		LinkBuilder linkBuilder = new LinkBuilder(uriInfo);
		FeaturesResponse featureResponse = dataAccess.retrieveFeatures(oafConfiguration, collectionId, featuresRequest,
				linkBuilder);
		if (XML.equals(requestFormat)) {
			return featureResponseCreator.createGmlResponseWithHeaders(featureResponse, acceptHeader);
		}
		return featureResponseCreator.createJsonResponseWithHeaders(featureResponse);
	}

	private Map<FilterProperty, List<String>> findQueryableParameters(String datasetId, String collectionId,
			MultivaluedMap<String, String> queryParameters) throws UnknownDatasetId, UnknownCollectionId {
		Map<FilterProperty, List<String>> filterRequestProperties = new HashMap<>();
		OafDatasetConfiguration oafConfiguration = deegreeWorkspaceInitializer.getOafDatasets().getDataset(datasetId);
		FeatureTypeMetadata featureTypeMetadata = oafConfiguration.getFeatureTypeMetadata(collectionId);
		if (featureTypeMetadata != null) {
			List<FilterProperty> filterProperties = featureTypeMetadata.getFilterProperties();
			filterProperties.forEach(filterProperty -> {
				String filterName = filterProperty.getName().getLocalPart();
				if (queryParameters.containsKey(filterName)) {
					filterRequestProperties.put(filterProperty, queryParameters.get(filterName));
				}
			});
		}
		return filterRequestProperties;
	}

}
