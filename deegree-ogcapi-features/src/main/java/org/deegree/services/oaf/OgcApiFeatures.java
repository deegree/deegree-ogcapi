package org.deegree.services.oaf;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.deegree.services.controller.OGCFrontController;
import org.deegree.services.oaf.openapi.OpenApiCreator;
import org.deegree.services.oaf.resource.LandingPage;
import org.deegree.services.oaf.resource.OpenApi;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.TimeZone;

import static org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer.DEEGREE_WORKSPACE_NAME;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@ApplicationPath("/")
public class OgcApiFeatures extends ResourceConfig {

    private static final Logger LOG = getLogger( OgcApiFeatures.class );

    public OgcApiFeatures( @Context ServletConfig servletConfig ) {
        super();
        register( new ObjectMapperContextResolver() );

        initOgcFrontCntroller( servletConfig );

        OpenApi openApiResource = new OpenApi();
        openApiResource.setOpenApiCreator( new OpenApiCreator( servletConfig ) );
        register( openApiResource );

        LandingPage landingPageResource = new LandingPage();
        register( landingPageResource );
        packages( "org.deegree.services.oaf.resource" );
        packages( "com.fasterxml.jackson.jaxrs.json" );
        packages( "org.deegree.services.oaf.feature" );
        packages( "org.deegree.services.oaf.converter" );
        packages( "org.deegree.services.oaf.exceptions" );
        packages( "org.deegree.services.oaf.filter" );
    }

    private void initOgcFrontCntroller( @Context ServletConfig servletConfig ) {
        try {
            OGCFrontController ogcFrontController = new OGCFrontController();
            ogcFrontController.init( servletConfig );
            ogcFrontController.setActiveWorkspaceName( DEEGREE_WORKSPACE_NAME );
        } catch ( ServletException | IOException e ) {
            LOG.error( "Initialization of the OGCFrontController failed.CConfig REST API is not available", e );
        }
    }

    @Provider
    public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

        private final ObjectMapper mapper;

        public ObjectMapperContextResolver() {
            this.mapper = createObjectMapper();
        }

        @Override
        public ObjectMapper getContext( Class<?> type ) {
            return mapper;
        }

        private ObjectMapper createObjectMapper() {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setTimeZone( TimeZone.getDefault() );
            return mapper;
        }
    }

}