/*-
 * #%L
 * deegree-ogcapi-features - OGC API Features (OAF) implementation - Querying and modifying of geospatial data objects
 * %%
 * Copyright (C) 2019 - 2020 lat/lon GmbH, info@lat-lon.de, www.lat-lon.de
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package org.deegree.services.oaf;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.deegree.ogcapi.config.resource.RestartOrUpdateHandler;
import org.deegree.services.controller.OGCFrontController;
import org.deegree.services.oaf.openapi.OpenApiCreator;
import org.deegree.services.oaf.workspace.DataAccess;
import org.deegree.services.oaf.workspace.DeegreeDataAccess;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceRestartOrUpdateHandler;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.util.TimeZone;

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
        LOG.debug( "deegree OGCFrontController initialized. Config REST API is available" );

        DeegreeWorkspaceInitializer deegreeWorkspaceInitializer = new DeegreeWorkspaceInitializer();
        deegreeWorkspaceInitializer.initialize();

        packages( "com.fasterxml.jackson.jaxrs.json" );
        packages( "org.deegree.services.oaf.resource" );
        packages( "org.deegree.services.oaf.feature" );
        packages( "org.deegree.services.oaf.converter" );
        packages( "org.deegree.services.oaf.exceptions" );
        packages( "org.deegree.services.oaf.filter" );
        packages( "org.deegree.ogcapi.config.resource" );
        packages( "org.deegree.ogcapi.config.exceptions" );
        register( new AbstractBinder() {
            @Override
            protected void configure() {
                bindAsContract( OpenApiCreator.class );
                bindAsContract( DeegreeDataAccess.class ).to( DataAccess.class );
                bind( deegreeWorkspaceInitializer ).to( DeegreeWorkspaceInitializer.class );
                bind( DeegreeWorkspaceRestartOrUpdateHandler.class ).to( RestartOrUpdateHandler.class );
            }
        } );
        LOG.info( "deegree OGC API - Features implementation successfully initialized" );
    }

    private void initOgcFrontCntroller( @Context ServletConfig servletConfig ) {
        try {
            OGCFrontController ogcFrontController = new OGCFrontController();
            ogcFrontController.init( servletConfig );
        } catch ( ServletException e ) {
            LOG.error( "Initialization of the OGCFrontController failed. Config REST API is not available", e );
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
