package org.deegree.services.oaf.resource.html;

import io.swagger.v3.oas.annotations.Operation;
import org.deegree.services.oaf.config.htmlview.HtmlViewConfiguration;
import org.deegree.services.oaf.domain.html.ImpressumConfiguration;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
@Path("/")
public class Global {

    @Context
    ServletContext servletContext;

    @Inject
    private DeegreeWorkspaceInitializer deegreeWorkspaceInitializer;

    @Operation(hidden = true)
    @Path("css/main.css")
    @GET
    public InputStream getDefaultCssFile()
                    throws FileNotFoundException {
        HtmlViewConfiguration globalHtmlViewConfiguration = deegreeWorkspaceInitializer.getGlobalHtmlViewConfiguration();
        if ( globalHtmlViewConfiguration != null && globalHtmlViewConfiguration.getCssFile() != null )
            return new FileInputStream( globalHtmlViewConfiguration.getCssFile() );
        return getClass().getResourceAsStream( "/css/main.css" );
    }

    @Operation(hidden = true)
    @Path("impressum")
    @GET
    @Produces(APPLICATION_JSON)
    public Response getDefaultImpressumUrl() {
        HtmlViewConfiguration globalHtmlViewConfiguration = deegreeWorkspaceInitializer.getGlobalHtmlViewConfiguration();
        if ( globalHtmlViewConfiguration == null || globalHtmlViewConfiguration.getImpressumUrl() == null )
            return Response.status( Response.Status.NOT_FOUND ).build();
        return Response.ok( new ImpressumConfiguration( globalHtmlViewConfiguration.getImpressumUrl() ),
                            APPLICATION_JSON ).build();
    }

}
