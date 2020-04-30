package org.deegree.services.oaf.resource;

import io.swagger.v3.core.filter.SpecFilter;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.jaxrs2.integration.ServletConfigContextUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.models.OpenAPI;
import org.deegree.services.oaf.openapi.OafOpenApiFilter;
import org.deegree.services.oaf.openapi.OpenApiCreator;
import org.slf4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_OPENAPI;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_OPENAPI_TYPE;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Path("/datasets/{datasetId}/api")
public class OpenApi {


    @Context
    ServletContext servletContext;

    @Context
    ServletConfig config;

    @Context
    Application app;

    private OpenApiCreator openApiCreator;

    @GET
    @Produces({ APPLICATION_OPENAPI })
    @Operation(summary = "api documentation", description = "api documentation")
    @Tag(name = "Capabilities")
    public Response getOpenApiJson(
                    @Context HttpHeaders headers,
                    @Context UriInfo uriInfo,
                    @PathParam("datasetId")
                                    String datasetId )
                    throws Exception {
        OpenAPI openApi = this.openApiCreator.createOpenApi( headers, config, app, uriInfo, datasetId );

        if ( openApi == null )
            return Response.status( 404 ).build();
        return Response.status( Response.Status.OK ).entity( Json.mapper().writeValueAsString( openApi ) ).type(
                        APPLICATION_OPENAPI_TYPE ).build();
    }

    @GET
    @Produces({ TEXT_HTML })
    @Operation(hidden = true)
    public InputStream getOpenApiHtml() {
        return getFile( "index.html" );
    }

    @Operation(hidden = true)
    @Path("/{path: .+}")
    @GET
    public InputStream getFile(
                    @PathParam("path")
                                    String path ) {
        try {
            if ( path.startsWith( "api/" ) )
                path = path.substring( 4 );
            String base = servletContext.getRealPath( "/swagger-ui/" );
            File f = new File( String.format( "%s/%s", base, path ) );
            return new FileInputStream( f );
        } catch ( FileNotFoundException e ) {
            System.out.println( e.getLocalizedMessage() );
            return null;
        }
    }

    /**
     * @param openApiCreator
     *                 used to create the OpenAPI configuration never <code>null</code>
     */
    public void setOpenApiCreator( OpenApiCreator openApiCreator ) {
        this.openApiCreator = openApiCreator;
    }

}