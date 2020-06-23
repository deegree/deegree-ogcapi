package org.deegree.services.oaf.resource.html;

import io.swagger.v3.oas.annotations.Operation;
import org.deegree.services.oaf.config.htmlview.HtmlViewConfiguration;
import org.deegree.services.oaf.domain.html.HtmlPageConfiguration;
import org.deegree.services.oaf.domain.html.MapConfiguration;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Path("/datasets/{datasetId}")
public class Dataset {

    @Context
    ServletContext servletContext;

    @Inject
    private DeegreeWorkspaceInitializer deegreeWorkspaceInitializer;

    @Operation(hidden = true)
    @Path("/css/main.css")
    @GET
    public InputStream getCssFile( @PathParam("datasetId") String datasetId )
                    throws FileNotFoundException {
        HtmlViewConfiguration htmlViewConfiguration = deegreeWorkspaceInitializer.getHtmlViewConfiguration( datasetId );
        if ( htmlViewConfiguration != null && htmlViewConfiguration.getCssFile() != null )
            return new FileInputStream( htmlViewConfiguration.getCssFile() );
        return getClass().getResourceAsStream( "/css/main.css" );
    }

    @Operation(hidden = true)
    @Path("/config/map")
    @GET
    @Produces(APPLICATION_JSON)
    public Response getMapConfig( @PathParam("datasetId") String datasetId ) {
        HtmlViewConfiguration htmlViewConfiguration = deegreeWorkspaceInitializer.getHtmlViewConfiguration( datasetId );
        if ( htmlViewConfiguration == null || htmlViewConfiguration.getWmsUrl() == null )
            return Response.status( Response.Status.NOT_FOUND ).build();
        MapConfiguration mapConfiguration = new MapConfiguration( htmlViewConfiguration.getWmsUrl(),
                                                                  htmlViewConfiguration.getWmsLayers(),
                                                                  htmlViewConfiguration.getCrsCode(),
                                                                  htmlViewConfiguration.getCrsProj4Definition() );
        return Response.ok( mapConfiguration, APPLICATION_JSON ).build();
    }

    @Operation(hidden = true)
    @Path("/config/html")
    @GET
    @Produces(APPLICATION_JSON)
    public Response getHtmlConfig( @PathParam("datasetId") String datasetId ) {
        HtmlViewConfiguration htmlViewConfiguration = deegreeWorkspaceInitializer.getHtmlViewConfiguration( datasetId );
        if ( htmlViewConfiguration == null || ( htmlViewConfiguration.getLegalNoticeUrl() == null
                                                && htmlViewConfiguration.getPrivacyUrl() == null ) )
            return Response.status( Response.Status.NOT_FOUND ).build();
        HtmlPageConfiguration configuration = new HtmlPageConfiguration( htmlViewConfiguration.getLegalNoticeUrl(),
                                                                         htmlViewConfiguration.getPrivacyUrl() );
        return Response.ok( configuration, APPLICATION_JSON ).build();
    }

}
