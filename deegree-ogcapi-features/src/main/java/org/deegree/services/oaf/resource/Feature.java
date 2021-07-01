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
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.deegree.services.oaf.RequestFormat;
import org.deegree.services.oaf.exceptions.InternalQueryException;
import org.deegree.services.oaf.exceptions.InvalidParameterValue;
import org.deegree.services.oaf.exceptions.UnknownCollectionId;
import org.deegree.services.oaf.exceptions.UnknownDatasetId;
import org.deegree.services.oaf.feature.FeatureResponse;
import org.deegree.services.oaf.feature.FeatureResponseCreator;
import org.deegree.services.oaf.link.LinkBuilder;
import org.deegree.services.oaf.workspace.DataAccess;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static javax.ws.rs.core.MediaType.TEXT_HTML;
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
@Path("/datasets/{datasetId}/collections/{collectionId}/items/{featureId}")
public class Feature {

    @Inject
    private DeegreeWorkspaceInitializer deegreeWorkspaceInitializer;

    @Inject
    private DataAccess dataAccess;

    private final FeatureResponseCreator featureResponseCreator = new FeatureResponseCreator();

    @GET
    @Produces({ APPLICATION_GEOJSON })
    @Operation(operationId = "feature", summary = "retrieves feature of collection {collectionId}", description = "Retrieves one single feature of the collection with the id {collectionId}")
    @Tag(name = "Data")
    public Response featureJson(
                    @Context
                                    UriInfo uriInfo,
                    @PathParam("datasetId")
                                    String datasetId,
                    @PathParam("collectionId")
                                    String collectionId,
                    @PathParam("featureId")
                                    String featureId,
                    @Parameter(description = "The coordinate reference system of the response geometries.", style = ParameterStyle.FORM)
                    @QueryParam("crs")
                                    String crs,
                    @Parameter(description = "The request output format.", style = ParameterStyle.FORM,
                                    schema = @Schema(allowableValues = { "json", "html", "xml" }))
                    @QueryParam("f")
                                    String format )
                    throws UnknownCollectionId, InternalQueryException, InvalidParameterValue, UnknownDatasetId {
        return feature( uriInfo, datasetId, collectionId, featureId, crs, format, JSON );
    }

    @GET
    @Produces({ APPLICATION_GML, APPLICATION_GML_32, APPLICATION_GML_SF0, APPLICATION_GML_SF2 })
    @Operation(hidden = true)
    public Response featureGml(
                    @Context
                                    UriInfo uriInfo,
                    @HeaderParam("Accept") String acceptHeader,
                    @PathParam("datasetId")
                                    String datasetId,
                    @PathParam("collectionId")
                                    String collectionId,
                    @PathParam("featureId")
                                    String featureId,
                    @Parameter(description = "The coordinate reference system of the response geometries.", style = ParameterStyle.FORM)
                    @QueryParam("crs")
                                    String crs,
                    @Parameter(description = "The request output format.", style = ParameterStyle.FORM,
                                    schema = @Schema(allowableValues = { "json", "html", "xml" }))
                    @QueryParam("f")
                                    String format )
                    throws UnknownCollectionId, InternalQueryException, InvalidParameterValue, UnknownDatasetId {
        return feature( uriInfo, datasetId, collectionId, featureId, crs, format, XML, acceptHeader );
    }

    @GET
    @Produces({ TEXT_HTML })
    @Operation(hidden = true)
    public Response featureHtml(
                    @Context
                                    UriInfo uriInfo,
                    @PathParam("datasetId")
                                    String datasetId,
                    @PathParam("collectionId")
                                    String collectionId,
                    @PathParam("featureId")
                                    String featureId,
                    @Parameter(description = "The coordinate reference system of the response geometries.", style = ParameterStyle.FORM)
                    @QueryParam("crs")
                                    String crs,
                    @Parameter(description = "The request output format.", style = ParameterStyle.FORM,
                                    schema = @Schema(allowableValues = { "json", "html", "xml" }))
                    @QueryParam("f")
                                    String format )
                    throws InvalidParameterValue, UnknownDatasetId, UnknownCollectionId, InternalQueryException {
        return feature( uriInfo, datasetId, collectionId, featureId, crs, format, HTML );
    }

    @GET
    @Operation(hidden = true)
    public Response featureOther(
                    @Context
                                    UriInfo uriInfo,
                    @PathParam("datasetId")
                                    String datasetId,
                    @PathParam("collectionId")
                                    String collectionId,
                    @PathParam("featureId")
                                    String featureId,
                    @Parameter(description = "The coordinate reference system of the response geometries.", style = ParameterStyle.FORM)
                    @QueryParam("crs")
                                    String crs,
                    @Parameter(description = "The request output format.", style = ParameterStyle.FORM,
                                    schema = @Schema(allowableValues = { "json", "html", "xml" }))
                    @QueryParam("f")
                                    String format )
                    throws InvalidParameterValue, UnknownDatasetId, UnknownCollectionId, InternalQueryException {
        return feature( uriInfo, datasetId, collectionId, featureId, crs, format, HTML );
    }

    private Response feature( UriInfo uriInfo, String datasetId, String collectionId, String featureId, String crs,
                              String formatParamValue, RequestFormat defaultFormat )
                    throws UnknownCollectionId, InternalQueryException, InvalidParameterValue, UnknownDatasetId {
        return feature( uriInfo, datasetId, collectionId, featureId, crs, formatParamValue, defaultFormat, null );
    }

    private Response feature( UriInfo uriInfo, String datasetId, String collectionId, String featureId, String crs,
                              String formatParamValue, RequestFormat defaultFormat, String acceptHeader )
                    throws UnknownCollectionId, InternalQueryException, InvalidParameterValue, UnknownDatasetId {
        RequestFormat requestFormat = byFormatParameter( formatParamValue, defaultFormat );
        OafDatasetConfiguration oafConfiguration = deegreeWorkspaceInitializer.getOafDatasets().getDataset( datasetId );
        oafConfiguration.checkCollection( collectionId );
        if ( HTML.equals( requestFormat ) ) {
            return Response.ok( getClass().getResourceAsStream( "/feature.html" ), TEXT_HTML ).build();
        }

        LinkBuilder linkBuilder = new LinkBuilder( uriInfo );
        FeatureResponse featureResponse = dataAccess.retrieveFeature( oafConfiguration, collectionId, featureId, crs,
                                                                      linkBuilder );
        if ( XML.equals( requestFormat ) ) {
            return featureResponseCreator.createGmlResponseWithHeaders( featureResponse, acceptHeader );
        }
        return featureResponseCreator.createJsonResponseWithHeaders( featureResponse );
    }

}
