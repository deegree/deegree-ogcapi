package org.deegree.services.oaf.resource.html;

import io.swagger.v3.oas.annotations.Operation;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static javax.ws.rs.core.MediaType.TEXT_HTML;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Path("/documentation")
public class Documentation {

    @Context
    private ServletContext servletContext;

    @GET
    @Produces(TEXT_HTML)
    @Path("/{path: .+}")
    @Operation(hidden = true)
    public InputStream getFile(
                    @PathParam("path")
                                    String path ) {
        try {
            String base = servletContext.getRealPath( "deegree-ogcapi-documentation/" );
            File f = new File( String.format( "%s/%s", base, path ) );
            return new FileInputStream( f );
        } catch ( FileNotFoundException e ) {
            System.out.println( e.getLocalizedMessage() );
            return null;
        }
    }

}
