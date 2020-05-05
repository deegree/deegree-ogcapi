package org.deegree.services.oaf.resource;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DatasetsTest extends JerseyTest {

    @Override
    protected Application configure() {
        enable( TestProperties.LOG_TRAFFIC );
        return new ResourceConfig( Datasets.class );
    }

    @Test
    public void test_DatatsetsDeclaration_JSON_ShouldBeAvailable() {
        Response response = target( "/datasets" ).request( MediaType.APPLICATION_JSON_TYPE ).get();
        String json = response.readEntity( String.class );
        assertThat( response.getStatus(), is( 200 ) );
    }

    @Test
    public void test_DatatsetsDeclaration_HTML_ShouldBeAvailable() {
        Response response = target( "/datasets" ).request( MediaType.APPLICATION_JSON_TYPE ).get();
        String json = response.readEntity( String.class );
        assertThat( response.getStatus(), is( 200 ) );
    }

}
