package org.deegree.services.oaf.resource;

import io.swagger.v3.oas.annotations.Operation;
import org.deegree.services.oaf.config.htmlview.HtmlViewConfiguration;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Path( "" )
public class HtmlResource {

    @Context
    ServletContext servletContext;

    @Operation(hidden = true)
    @Path("/webjars/{path: .+}")
    @GET
    public InputStream getFile( @PathParam("path") String path ) {
        try {
            String base = servletContext.getRealPath( "/webjars/" );
            File f = new File( String.format( "%s/%s", base, path ) );
            return new FileInputStream( f );
        } catch ( FileNotFoundException e ) {
            System.out.println( e.getLocalizedMessage() );
            return null;
        }
    }

    @Operation(hidden = true)
    @Path("/css/main.css")
    @GET
    public InputStream getDefaultCssFile() {
        return getClass().getResourceAsStream( "/css/main.css" );
    }

    @Operation(hidden = true)
    @Path("/datasets/{datasetId}/css/main.css")
    @GET
    public InputStream getCssFile( @PathParam("datasetId") String datasetId )
                    throws FileNotFoundException {
        HtmlViewConfiguration htmlViewConfiguration = DeegreeWorkspaceInitializer.getHtmlViewConfiguration( datasetId );
        if ( htmlViewConfiguration != null && htmlViewConfiguration.getCssFile() != null )
            return new FileInputStream( htmlViewConfiguration.getCssFile() );
        return getClass().getResourceAsStream( "/css/main.css" );
    }

    @Operation(hidden = true)
    @Path("/datasets/{datasetId}/config/map")
    @GET
    @Produces(APPLICATION_JSON)
    public Response getMapConfig( @PathParam("datasetId") String datasetId ) {
        HtmlViewConfiguration htmlViewConfiguration = DeegreeWorkspaceInitializer.getHtmlViewConfiguration( datasetId );
        if ( htmlViewConfiguration == null || htmlViewConfiguration.getWmsUrl() == null )
        return Response.status( Response.Status.NOT_FOUND ).build();
        MapConfiguration mapConfiguration = new MapConfiguration( htmlViewConfiguration.getWmsUrl(),
                                                                  htmlViewConfiguration.getWmsLayers(),
                                                                  htmlViewConfiguration.getCrsCode(),
                                                                  htmlViewConfiguration.getCrsProj4Definition() );
        return Response.ok( mapConfiguration, APPLICATION_JSON ).build();
    }


    @Operation(hidden = true)
    @Path("/datasets/{datasetId}/config/impressum")
    @GET
    @Produces(APPLICATION_JSON)
    public Response getImpresumUrl( @PathParam("datasetId") String datasetId ) {
        HtmlViewConfiguration htmlViewConfiguration = DeegreeWorkspaceInitializer.getHtmlViewConfiguration( datasetId );
        if ( htmlViewConfiguration == null || htmlViewConfiguration.getImpressumUrl() == null )
            return Response.status( Response.Status.NOT_FOUND ).build();
        return Response.ok( new ImpressumConfiguration( htmlViewConfiguration.getImpressumUrl() ),
                            APPLICATION_JSON ).build();
    }


    public class MapConfiguration {

        private final String wmsUrl;

        private final String wmsLayers;

        private final String crsCode;

        private final String crsProj4Definition;

        public MapConfiguration( String wmsUrl, String wmsLayers, String crsCode, String crsProj4Definition ) {
            this.wmsUrl = wmsUrl;
            this.wmsLayers = wmsLayers;
            this.crsCode = crsCode;
            this.crsProj4Definition = crsProj4Definition;
        }

        public String getWmsUrl() {
            return wmsUrl;
        }

        public String getWmsLayers() {
            return wmsLayers;
        }

        public String getCrsCode() {
            return crsCode;
        }

        public String getCrsProj4Definition() {
            return crsProj4Definition;
        }
    }

    public class ImpressumConfiguration {

        private final String impressumUrl;

        public ImpressumConfiguration( String impressumUrl ) {
            this.impressumUrl = impressumUrl;
        }

        public String getImpressumUrl() {
            return impressumUrl;
        }
    }

}
