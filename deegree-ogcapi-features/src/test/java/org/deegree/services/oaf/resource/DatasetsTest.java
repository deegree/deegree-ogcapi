package org.deegree.services.oaf.resource;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import static org.deegree.services.oaf.domain.conformance.ConformanceClass.CORE;
import static org.deegree.services.oaf.domain.conformance.ConformanceClass.OPENAPI30;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class DatasetsTest extends JerseyTest {

    @Override
    protected Application configure() {
        enable( TestProperties.LOG_TRAFFIC );
        return new ResourceConfig( Conformance.class );
    }

    @Test
    public void test_ConformanceDeclarationShouldBeAvailable() {
        final String json = target( "/datasets/oaf/conformance" ).request( MediaType.APPLICATION_JSON_TYPE ).get( String.class );
        assertThat( json, containsString( CORE.getConformanceClass() ) );
        assertThat( json, containsString( OPENAPI30.getConformanceClass() ) );
    }

}
