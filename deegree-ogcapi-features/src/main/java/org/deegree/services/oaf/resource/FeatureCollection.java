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
import org.deegree.services.oaf.exceptions.UnknownCollectionId;
import org.deegree.services.oaf.exceptions.UnknownDatasetId;
import org.deegree.services.oaf.link.LinkBuilder;
import org.deegree.services.oaf.workspace.DataAccess;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static org.deegree.services.oaf.RequestFormat.HTML;
import static org.deegree.services.oaf.RequestFormat.JSON;
import static org.deegree.services.oaf.RequestFormat.XML;
import static org.deegree.services.oaf.RequestFormat.byFormatParameter;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Path("/datasets/{datasetId}/collections/{collectionId}")
public class FeatureCollection {

    @Inject
    private DeegreeWorkspaceInitializer deegreeWorkspaceInitializer;

    @Inject
    private DataAccess dataAccess;

    @GET
    @Produces({ APPLICATION_JSON })
    @Operation(operationId = "collection", summary = "describes collection {collectionId}", description = "Describes the collection with the id {collectionId}")
    @Tag(name = "Collections")
    @ApiResponse(description = "default response", content = @Content(schema = @Schema(implementation = Collections.class)))
    public Response collectionJson(
                    @PathParam("datasetId")
                                    String datasetId,
                    @PathParam("collectionId")
                                    String collectionId,
                    @Parameter(description = "The request output format.", style = ParameterStyle.FORM,
                                    schema = @Schema(allowableValues = { "json", "html", "xml" }))
                    @QueryParam("f")
                                    String format,
                    @Context
                                    UriInfo uriInfo )
                    throws UnknownCollectionId, UnknownDatasetId, InvalidParameterValue {
        return collection( datasetId, collectionId, uriInfo, format, JSON );
    }

    @GET
    @Produces({ APPLICATION_XML })
    @Operation(hidden = true)
    public Response collectionXml(
                    @PathParam("datasetId")
                                    String datasetId,
                    @PathParam("collectionId")
                                    String collectionId,
                    @Parameter(description = "The request output format.", style = ParameterStyle.FORM,
                                    schema = @Schema(allowableValues = { "json", "html", "xml" }))
                    @QueryParam("f")
                                    String format,
                    @Context
                                    UriInfo uriInfo )
                    throws UnknownCollectionId, UnknownDatasetId, InvalidParameterValue {
        return collection( datasetId, collectionId, uriInfo, format, XML );
    }

    @GET
    @Produces({ TEXT_HTML })
    @Operation(hidden = true)
    public Response collectionHtml(
                    @PathParam("datasetId")
                                    String datasetId,
                    @PathParam("collectionId")
                                    String collectionId,
                    @Parameter(description = "The request output format.", style = ParameterStyle.FORM,
                                    schema = @Schema(allowableValues = { "json", "html", "xml" }))
                    @QueryParam("f")
                                    String format,
                    @Context
                                    UriInfo uriInfo )
                    throws UnknownCollectionId, InvalidParameterValue, UnknownDatasetId {
        return collection( datasetId, collectionId, uriInfo, format, HTML );
    }

    @GET
    @Operation(hidden = true)
    public Response collectionOther(
                    @PathParam("datasetId")
                                    String datasetId,
                    @PathParam("collectionId")
                                    String collectionId,
                    @Parameter(description = "The request output format.", style = ParameterStyle.FORM,
                                    schema = @Schema(allowableValues = { "json", "html", "xml" }))
                    @QueryParam("f")
                                    String format,
                    @Context
                                    UriInfo uriInfo )
                    throws UnknownCollectionId, InvalidParameterValue, UnknownDatasetId {
        return collection( datasetId, collectionId, uriInfo, format, JSON );
    }

    private Response collection( String datasetId, String collectionId, UriInfo uriInfo, String formatParamValue,
                                 RequestFormat defaultFormat )
                    throws UnknownCollectionId, UnknownDatasetId, InvalidParameterValue {
        RequestFormat requestFormat = byFormatParameter( formatParamValue, defaultFormat );
        OafDatasetConfiguration oafConfiguration = deegreeWorkspaceInitializer.getOafDatasets().getDataset( datasetId );
        if ( HTML.equals( requestFormat ) ) {
            return Response.ok( getClass().getResourceAsStream( "/collection.html" ), TEXT_HTML ).build();
        }
        LinkBuilder linkBuilder = new LinkBuilder( uriInfo );
        Collection collection = dataAccess.createCollection( oafConfiguration, collectionId, linkBuilder );
        if ( XML.equals( requestFormat ) ) {
            Collections collections = new Collections();
            collections.addCollection( collection );
            return Response.ok( collections, APPLICATION_XML ).build();
        }
        return Response.ok( collection, APPLICATION_JSON ).build();
    }

}
