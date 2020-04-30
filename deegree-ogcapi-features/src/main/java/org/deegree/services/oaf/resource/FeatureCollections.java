package org.deegree.services.oaf.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.deegree.services.oaf.RequestFormat;
import org.deegree.services.oaf.domain.collections.Collections;
import org.deegree.services.oaf.exceptions.InvalidParameterValue;
import org.deegree.services.oaf.exceptions.UnknownDatasetId;
import org.deegree.services.oaf.link.LinkBuilder;
import org.deegree.services.oaf.workspace.DataAccess;
import org.deegree.services.oaf.workspace.DataAccessFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.InputStream;

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
@Path("/datasets/{datasetId}/collections")
public class FeatureCollections {

    private DataAccess collectionFactory = DataAccessFactory.getInstance();

    @GET
    @Produces({ APPLICATION_JSON })
    @Operation(summary = "describes collections", description = "Describes all collections provided by this service")
    @Tag(name = "Collections")
    public Response collectionsJson(
                    @PathParam("datasetId")
                                    String datasetId,
                    @Parameter(description = "The request output format.", style = ParameterStyle.FORM)
                    @QueryParam("f")
                                    String format,
                    @Context
                                    UriInfo uriInfo )
                    throws UnknownDatasetId, InvalidParameterValue {
        return collections( datasetId, uriInfo, format, JSON );
    }

    @GET
    @Produces({ APPLICATION_XML })
    @Operation(summary = "describes collections", description = "Describes all collections provided by this service")
    @Tag(name = "Collections")
    public Response collectionsXml(
                    @PathParam("datasetId")
                                    String datasetId,
                    @Parameter(description = "The request output format.", style = ParameterStyle.FORM)
                    @QueryParam("f")
                                    String format,
                    @Context
                                    UriInfo uriInfo )
                    throws UnknownDatasetId, InvalidParameterValue {
        return collections( datasetId, uriInfo, format, XML );
    }

    @GET
    @Produces({ TEXT_HTML })
    @Operation(hidden = true)
    public InputStream collectionsHtml(
                    @Parameter(description = "The request output format.", style = ParameterStyle.FORM)
                    @QueryParam("f")
                                    String format ) {
        return getClass().getResourceAsStream( "/collections.html" );
    }

    @GET
    @Operation(hidden = true)
    public Response collectionsOther(
                    @PathParam("datasetId")
                                    String datasetId,
                    @Context
                                    UriInfo uriInfo,
                    @Parameter(description = "The request output format.", style = ParameterStyle.FORM)
                    @QueryParam("f")
                                    String format )
                    throws UnknownDatasetId, InvalidParameterValue {
        return collections( datasetId, uriInfo, format, JSON );
    }

    private Response collections( String datasetId, UriInfo uriInfo, String formatParamValue,
                                  RequestFormat defaultFormat )
                    throws UnknownDatasetId, InvalidParameterValue {
        RequestFormat requestFormat = byFormatParameter( formatParamValue, defaultFormat );
        if ( HTML.equals( requestFormat ) ) {
            return Response.ok( getClass().getResourceAsStream( "/collections.html" ), TEXT_HTML ).build();
        }

        LinkBuilder linkBuilder = new LinkBuilder( uriInfo );
        Collections collections = collectionFactory.createCollections( datasetId, linkBuilder );
        return Response.ok( collections, mediaTypeFromRequestFormat( requestFormat ) ).build();
    }

    private String mediaTypeFromRequestFormat( RequestFormat requestFormat ) {
        return XML.equals( requestFormat ) ? APPLICATION_XML : APPLICATION_JSON;
    }

}
