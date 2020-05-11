package org.deegree.services.oaf.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.deegree.services.oaf.RequestFormat;
import org.deegree.services.oaf.exceptions.InvalidParameterValue;
import org.deegree.services.oaf.exceptions.UnknownDatasetId;
import org.deegree.services.oaf.link.Link;
import org.deegree.services.oaf.link.LinkBuilder;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.deegree.services.oaf.workspace.configuration.DatasetMetadata;
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
import java.util.List;

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
@Path("/datasets/{datasetId}")
public class LandingPage {

    @Inject
    private DeegreeWorkspaceInitializer deegreeWorkspaceInitializer;

    @GET
    @Produces({ APPLICATION_JSON })
    @Operation(summary = "landing page", description = "Landing page of this dataset")
    @Tag(name = "Capabilities")
    public Response landingPageJson(
                    @Context UriInfo uriInfo,
                    @Parameter(description = "The request output format.", style = ParameterStyle.FORM)
                    @QueryParam("f") String format,
                    @PathParam("datasetId") String datasetId )
                    throws UnknownDatasetId, InvalidParameterValue {
        return landingPage( uriInfo, datasetId, format, JSON );
    }

    @GET
    @Produces({ APPLICATION_XML })
    @Operation(summary = "landing page", description = "Landing page of this dataset")
    @Tag(name = "Capabilities")
    public Response landingPageJsonXml(
                    @Context UriInfo uriInfo,
                    @Parameter(description = "The request output format.", style = ParameterStyle.FORM)
                    @QueryParam("f") String format,
                    @PathParam("datasetId") String datasetId )
                    throws UnknownDatasetId, InvalidParameterValue {
        return landingPage( uriInfo, datasetId, format, XML );
    }

    @GET
    @Produces({ TEXT_HTML })
    @Operation(hidden = true)
    public Response landingPageHtml(
                    @Context UriInfo uriInfo,
                    @Parameter(description = "The request output format.", style = ParameterStyle.FORM)
                    @QueryParam("f") String format,
                    @PathParam("datasetId") String datasetId )
                    throws UnknownDatasetId, InvalidParameterValue {
        return landingPage( uriInfo, datasetId, format, HTML );
    }

    @GET
    @Operation(hidden = true)
    public Response landingPageOther(
                    @Context UriInfo uriInfo,
                    @Parameter(description = "The request output format.", style = ParameterStyle.FORM)
                    @QueryParam("f") String format,
                    @PathParam("datasetId") String datasetId )
                    throws UnknownDatasetId, InvalidParameterValue {
        return landingPage( uriInfo, datasetId, format, JSON );
    }

    private Response landingPage( UriInfo uriInfo, String datasetId, String formatParamValue,
                                  RequestFormat defaultFormat )
                    throws UnknownDatasetId, InvalidParameterValue {
        RequestFormat requestFormat = byFormatParameter( formatParamValue, defaultFormat );
        if ( HTML.equals( requestFormat ) ) {
            return Response.ok( getClass().getResourceAsStream( "/landingpage.html" ), TEXT_HTML ).build();
        }

        OafDatasetConfiguration dataset = deegreeWorkspaceInitializer.getOafDatasets().getDataset( datasetId );
        DatasetMetadata metadata = dataset.getServiceMetadata();

        LinkBuilder linkBuilder = new LinkBuilder( uriInfo );
        List<Link> links = linkBuilder.createLandingPageLinks( datasetId, metadata );
        org.deegree.services.oaf.domain.landingpage.LandingPage landingPage = new org.deegree.services.oaf.domain.landingpage.LandingPage(
                        metadata.getTitle(), metadata.getDescription(), links );
        landingPage.setContact( metadata.getCreatorContact() );
        return Response.ok( landingPage, mediaTypeFromRequestFormat( requestFormat ) ).build();
    }

    private String mediaTypeFromRequestFormat( RequestFormat requestFormat ) {
        return XML.equals( requestFormat ) ? APPLICATION_XML : APPLICATION_JSON;
    }

}