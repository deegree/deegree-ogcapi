package org.deegree.services.oaf.resource;

import org.deegree.services.oaf.domain.collections.Collection;
import org.deegree.services.oaf.domain.collections.Collections;
import org.deegree.services.oaf.feature.FeatureResponseGmlWriter;
import org.deegree.services.oaf.feature.FeaturesRequest;
import org.deegree.services.oaf.link.LinkBuilder;
import org.deegree.services.oaf.openapi.OpenApiCreator;
import org.deegree.services.oaf.workspace.DataAccess;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

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
public class FeaturesTest extends JerseyTest {

    @Override
    protected Application configure() {
        enable( TestProperties.LOG_TRAFFIC );
        ResourceConfig resourceConfig = new ResourceConfig( Features.class, FeatureResponseGmlWriter.class );
        resourceConfig.register( new AbstractBinder() {
            @Override
            protected void configure() {
                bind( mockDataAccess() ).to( DataAccess.class );
                bind( mockWorkspaceInitializer( "strassenbaum" ) ).to( DeegreeWorkspaceInitializer.class );
                bindAsContract( OpenApiCreator.class );
            }
        } );
        return resourceConfig;
    }

    @Test
    public void test_FeaturesDeclaration_Json_ShouldBeAvailable() {
        int statusCode = target( "/datasets/oaf/collections/test/items" ).request(
                        APPLICATION_GEOJSON ).get().getStatus();
        assertThat( statusCode, is( 200 ) );
    }

    @Test
    public void test_FeaturesDeclaration_Gml_ShouldBeAvailable() {
        Response response = target( "/datasets/oaf/collections/test/items" ).request( APPLICATION_GML ).get();
        assertThat( response.getStatus(), is( 200 ) );
        assertThat( response.getMediaType(), is( APPLICATION_GML_TYPE ) );
        MultivaluedMap<String, Object> headers = response.getHeaders();
        assertThat( headers.get( HEADER_TIMESTAMP ).get( 0 ), is( notNullValue() ) );
        assertThat( headers.get( HEADER_NUMBER_RETURNED ).get( 0 ), is( "10" ) );
        assertThat( headers.get( HEADER_NUMBER_MATCHED ).get( 0 ), is( "100" ) );
        assertThat( headers.get( HEADER_Link ).size(), is( 1 ) );
    }

    @Test
    public void test_FeaturesDeclaration_Gml32_ShouldBeAvailable() {
        Response response = target( "/datasets/oaf/collections/test/items" ).request( APPLICATION_GML_32 ).get();
        assertThat( response.getStatus(), is( 200 ) );
        assertThat( response.getMediaType(), is( APPLICATION_GML_32_TYPE ) );
    }

    @Test
    public void test_FeaturesDeclaration_Gml32ProfileSF0_ShouldBeAvailable() {
        Response response = target( "/datasets/oaf/collections/test/items" ).request( APPLICATION_GML_SF0 ).get();
        assertThat( response.getStatus(), is( 200 ) );
        assertThat( response.getMediaType(), is( APPLICATION_GML_SF0_TYPE ) );
    }

    @Test
    public void test_FeaturesDeclaration_Gml32ProfileSF2_ShouldBeAvailable() {
        Response response = target( "/datasets/oaf/collections/test/items" ).request( APPLICATION_GML_SF2 ).get();
        assertThat( response.getStatus(), is( 200 ) );
        MultivaluedMap<String, Object> headers = response.getHeaders();
        assertThat( response.getMediaType(), is( APPLICATION_GML_SF2_TYPE ) );
    }

    private DataAccess mockDataAccess() {
        DataAccess testFactory = Mockito.mock( DataAccess.class );
        Collection collection = createCollection();
        Collections testCollection = createCollections( collection );
        try {
            when( testFactory.createCollections( eq( "oaf" ), any( LinkBuilder.class ) ) ).thenReturn( testCollection );
            when( testFactory.createCollection( eq( "oaf" ), eq( "test" ), any( LinkBuilder.class ) ) ).thenReturn(
                            collection );
            when( testFactory.retrieveFeatures( eq( "oaf" ), eq( "test" ), any( FeaturesRequest.class ),
                                                any( LinkBuilder.class ) ) ).thenReturn( features() );
            when( testFactory.retrieveFeature( eq( "oaf" ), eq( "test" ), eq( "42" ), isNull(),
                                               any( LinkBuilder.class ) ) ).thenReturn( feature() );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return testFactory;
    }

}
