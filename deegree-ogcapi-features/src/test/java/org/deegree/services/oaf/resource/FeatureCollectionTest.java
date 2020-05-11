package org.deegree.services.oaf.resource;

import org.deegree.services.oaf.workspace.DataAccess;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;
import org.xmlmatchers.namespace.SimpleNamespaceContext;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.xml.namespace.NamespaceContext;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static org.deegree.services.oaf.TestData.mockDataAccess;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.xmlmatchers.XmlMatchers.hasXPath;
import static org.xmlmatchers.transform.XmlConverters.the;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class FeatureCollectionTest extends JerseyTest {

    @Override
    protected Application configure() {
        enable( TestProperties.LOG_TRAFFIC );
        ResourceConfig resourceConfig = new ResourceConfig( FeatureCollection.class );
        resourceConfig.register( new AbstractBinder() {
            @Override
            protected void configure() {
                bind( mockDataAccess() ).to( DataAccess.class );
            }
        } );
        return resourceConfig;
    }

    @Test
    public void test_CollectionDeclaration_Json_ShouldBeAvailable() {
        Response response = target( "/datasets/oaf/collections/test" ).request( APPLICATION_JSON_TYPE ).get();
        assertThat( response.getStatus(), is( 200 ) );
    }

    @Test
    public void test_CollectionDeclaration_Xml_ShouldBeAvailable() {
        Response response = target( "/datasets/oaf/collections/test" ).request( APPLICATION_XML ).get();
        assertThat( response.getStatus(), is( 200 ) );
        String xml = response.readEntity( String.class );
        assertThat( the( xml ), hasXPath( "/core:Collections/core:Collection", nsContext() ) );
    }

    private NamespaceContext nsContext() {
        return new SimpleNamespaceContext().withBinding( "core", "http://www.opengis.net/ogcapi-features-1/1.0" );
    }

}
