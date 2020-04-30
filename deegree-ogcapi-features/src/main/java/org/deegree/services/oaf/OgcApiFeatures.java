package org.deegree.services.oaf;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.deegree.services.oaf.openapi.OafOpenApiFilter;
import org.deegree.services.oaf.openapi.OpenApiCreator;
import org.deegree.services.oaf.resource.LandingPage;
import org.deegree.services.oaf.resource.OpenApi;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;
import org.deegree.services.oaf.workspace.configuration.ServiceMetadata;
import org.glassfish.jersey.server.ResourceConfig;

import javax.servlet.ServletConfig;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@ApplicationPath("/")
public class OgcApiFeatures extends ResourceConfig {

    public OgcApiFeatures( @Context ServletConfig servletConfig) {
        super();
        register( new ObjectMapperContextResolver() );

        OpenApi openApiResource = new OpenApi();
        openApiResource.setOpenApiCreator(new OpenApiCreator(servletConfig) );
        register(openApiResource);

        LandingPage landingPageResource = new LandingPage();
        register( landingPageResource );
        packages("org.deegree.services.oaf.resource");
        packages( "com.fasterxml.jackson.jaxrs.json" );
        packages( "org.deegree.services.oaf.feature" );
        packages( "org.deegree.services.oaf.converter" );
        packages( "org.deegree.services.oaf.exceptions" );
        packages( "org.deegree.services.oaf.filter" );
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