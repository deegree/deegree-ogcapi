package org.deegree.services.oaf.resource.html;

import io.swagger.v3.oas.annotations.Operation;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Path("/webjars/{path: .+}")
public class Webjars {

    @Context
    ServletContext servletContext;

    @Produces({"text/javascript", "text/css"})
    @Operation(hidden = true)
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

}
