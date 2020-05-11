package org.deegree.services.oaf.resource;

import org.deegree.services.oaf.openapi.OpenApiCreator;
import org.deegree.services.oaf.workspace.DataAccess;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static org.deegree.services.oaf.TestData.mockDataAccess;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class FeatureCollectionsTest extends JerseyTest {

    @Override
    protected Application configure() {
        enable( TestProperties.LOG_TRAFFIC );
        ResourceConfig resourceConfig = new ResourceConfig( FeatureCollections.class );
        resourceConfig.register( new AbstractBinder() {
            @Override
            protected void configure() {
                bind( mockDataAccess() ).to( DataAccess.class );
            }
        } );
        return resourceConfig;
    }

    @Test
    public void test_CollectionsDeclaration_Json_ShouldBeAvailable() {
        Response response = target( "/datasets/oaf/collections" ).request( APPLICATION_JSON_TYPE ).get();

        assertThat( response.getStatus(), is( 200 ) );
    }

    @Test
    public void test_CollectionsDeclaration_Xml_ShouldBeAvailable() {
        Response response = target( "/datasets/oaf/collections" ).request( APPLICATION_XML ).get();

        assertThat( response.getStatus(), is( 200 ) );
    }

}
