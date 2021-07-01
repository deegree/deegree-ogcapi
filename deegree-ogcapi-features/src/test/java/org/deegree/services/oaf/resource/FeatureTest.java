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
package org.deegree.services.oaf.resource;

import org.deegree.services.oaf.domain.collections.Collection;
import org.deegree.services.oaf.domain.collections.Collections;
import org.deegree.services.oaf.feature.FeatureResponseGmlWriter;
import org.deegree.services.oaf.feature.FeaturesRequest;
import org.deegree.services.oaf.link.LinkBuilder;
import org.deegree.services.oaf.workspace.DataAccess;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import static org.deegree.services.oaf.OgcApiFeaturesConstants.DEFAULT_CRS;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.HEADER_CONTENT_CRS;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.HEADER_Link;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.HEADER_NUMBER_MATCHED;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.HEADER_NUMBER_RETURNED;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.HEADER_TIMESTAMP;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GEOJSON;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_32;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_32_TYPE;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_SF0;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_SF0_TYPE;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_SF2;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_SF2_TYPE;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_TYPE;
import static org.deegree.services.oaf.TestData.createCollection;
import static org.deegree.services.oaf.TestData.createCollections;
import static org.deegree.services.oaf.TestData.feature;
import static org.deegree.services.oaf.TestData.features;
import static org.deegree.services.oaf.TestData.mockWorkspaceInitializer;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class FeatureTest extends JerseyTest {

    @Override
    protected Application configure() {
        enable( TestProperties.LOG_TRAFFIC );
        ResourceConfig resourceConfig = new ResourceConfig( Feature.class, FeatureResponseGmlWriter.class );
        resourceConfig.register( new AbstractBinder() {
            @Override
            protected void configure() {
                bind( mockDataAccess() ).to( DataAccess.class );
                bind( mockWorkspaceInitializer() ).to( DeegreeWorkspaceInitializer.class );
            }
        } );
        return resourceConfig;
    }

    @Test
    public void test_FeatureDeclaration_Json_ShouldBeAvailable() {
        Response response = target( "/datasets/oaf/collections/test/items/42" ).request(
                        APPLICATION_GEOJSON ).get();
        assertThat( response.getStatus(), is( 200 ) );
        assertThat( response.getHeaders().get( HEADER_CONTENT_CRS ).get( 0 ), is( DEFAULT_CRS ) );
    }

    @Test
    public void test_FeatureDeclaration_Gml_ShouldBeAvailable() {
        Response response = target( "/datasets/oaf/collections/test/items/42" ).request( APPLICATION_GML ).get();
        assertThat( response.getStatus(), is( 200 ) );
        assertThat( response.getMediaType(), is( APPLICATION_GML_TYPE ) );
        MultivaluedMap<String, Object> headers = response.getHeaders();
        assertThat( headers.get( HEADER_TIMESTAMP ).get( 0 ), is( notNullValue() ) );
        assertThat( headers.get( HEADER_NUMBER_RETURNED ).get( 0 ), is( "1" ) );
        assertThat( headers.get( HEADER_NUMBER_MATCHED ).get( 0 ), is( "1" ) );
        assertThat( headers.get( HEADER_Link ).size(), is( 1 ) );
        assertThat( headers.get( HEADER_CONTENT_CRS ).get( 0 ), is( "<" + DEFAULT_CRS + ">" ) );
    }

    @Test
    public void test_FeatureDeclaration_Gml32_ShouldBeAvailable() {
        Response response = target( "/datasets/oaf/collections/test/items/42" ).request( APPLICATION_GML_32 ).get();
        assertThat( response.getStatus(), is( 200 ) );
        assertThat( response.getMediaType(), is( APPLICATION_GML_32_TYPE ) );
    }

    @Test
    public void test_FeatureDeclaration_Gml32ProfileSF0_ShouldBeAvailable() {
        Response response = target( "/datasets/oaf/collections/test/items/42" ).request( APPLICATION_GML_SF0 ).get();
        assertThat( response.getStatus(), is( 200 ) );
        assertThat( response.getMediaType(), is( APPLICATION_GML_SF0_TYPE ) );
    }

    @Test
    public void test_FeatureDeclaration_Gml32ProfileSF2_ShouldBeAvailable() {
        Response response = target( "/datasets/oaf/collections/test/items/42" ).request( APPLICATION_GML_SF2 ).get();
        assertThat( response.getStatus(), is( 200 ) );
        MultivaluedMap<String, Object> headers = response.getHeaders();
        assertThat( response.getMediaType(), is( APPLICATION_GML_SF2_TYPE ) );
    }

    private DataAccess mockDataAccess() {
        DataAccess testFactory = Mockito.mock( DataAccess.class );
        Collection collection = createCollection();
        Collections testCollection = createCollections( collection );
        try {
            when( testFactory.createCollections( any( OafDatasetConfiguration.class ),
                                                 any( LinkBuilder.class ) ) ).thenReturn( testCollection );
            when( testFactory.createCollection( any( OafDatasetConfiguration.class ), eq( "test" ),
                                                any( LinkBuilder.class ) ) ).thenReturn( collection );
            when( testFactory.retrieveFeatures( any( OafDatasetConfiguration.class ), eq( "test" ),
                                                any( FeaturesRequest.class ),
                                                any( LinkBuilder.class ) ) ).thenReturn( features() );
            when( testFactory.retrieveFeature( any( OafDatasetConfiguration.class ), eq( "test" ), eq( "42" ),
                                               eq( DEFAULT_CRS ), any( LinkBuilder.class ) ) ).thenReturn( feature() );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return testFactory;
    }

}
