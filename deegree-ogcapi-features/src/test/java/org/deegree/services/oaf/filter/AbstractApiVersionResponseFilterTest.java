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
package org.deegree.services.oaf.filter;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_XML_TYPE;
import static org.deegree.services.oaf.TestData.mockDataAccess;
import static org.deegree.services.oaf.TestData.mockWorkspaceInitializer;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.deegree.commons.utils.TunableParameter;
import org.deegree.services.oaf.openapi.OpenApiCreator;
import org.deegree.services.oaf.resource.FeatureCollection;
import org.deegree.services.oaf.workspace.DataAccess;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

/**
 * Base class for testing if API version response header is present.
 */
public abstract class AbstractApiVersionResponseFilterTest extends JerseyTest {
	
	private final boolean headerExpected;

    public AbstractApiVersionResponseFilterTest(boolean headerExpected) {
		super();
		this.headerExpected = headerExpected;
	}

	@Override
    protected Application configure() {
		TunableParameter.resetCache();
		
        enable( TestProperties.LOG_TRAFFIC );
        ResourceConfig resourceConfig = new ResourceConfig( FeatureCollection.class );
        resourceConfig.register(ApiVersionResponseFilter.class);
        resourceConfig.register( new AbstractBinder() {
            @Override
            protected void configure() {
                bind( mockDataAccess() ).to( DataAccess.class );
                bind( mockWorkspaceInitializer() ).to( DeegreeWorkspaceInitializer.class );
            }
        } );
        return resourceConfig;
    }
	
	private void verifyResponse(Response response) {
    	if (headerExpected) {
    		assertThat( response.getHeaderString(ApiVersionResponseFilter.HEADER_API_VERSION), is( OpenApiCreator.VERSION ) );
    	}
    	else {
    		assertThat( response.getHeaderString(ApiVersionResponseFilter.HEADER_API_VERSION), is( nullValue() ) );
    	}
	}
    
    @Test
    public void test_Collection() {
        Response response = target( "/datasets/oaf/collections/test" ).request( APPLICATION_JSON_TYPE ).get();
        verifyResponse(response);
    }
    
    @Test
    public void test_CollectionXml() {
        Response response = target( "/datasets/oaf/collections" ).request( APPLICATION_XML_TYPE ).get();
        verifyResponse(response);
    }
    
    @Test
    public void test_Collections() {
        Response response = target( "/datasets/oaf/collections" ).request( APPLICATION_JSON_TYPE ).get();
        verifyResponse(response);
    }
    
}
