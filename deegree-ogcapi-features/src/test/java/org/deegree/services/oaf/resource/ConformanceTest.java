package org.deegree.services.oaf.resource;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;
import org.xmlmatchers.namespace.SimpleNamespaceContext;

import javax.ws.rs.core.Application;
import javax.xml.namespace.NamespaceContext;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_ATOM_NS_URL;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_CORE_NS_URL;
import static org.deegree.services.oaf.domain.conformance.ConformanceClass.CORE;
import static org.deegree.services.oaf.domain.conformance.ConformanceClass.OPENAPI30;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.xmlmatchers.XmlMatchers.hasXPath;
import static org.xmlmatchers.transform.XmlConverters.the;

public class ConformanceTest extends JerseyTest {

    @Override
    protected Application configure() {
        enable( TestProperties.LOG_TRAFFIC );
        return new ResourceConfig( Conformance.class );
    }

    @Test
    public void test_ConformanceDeclaration_Json_ShouldBeAvailable() {
        final String json = target( "/datasets/oaf/conformance" ).request( APPLICATION_JSON_TYPE ).get( String.class );

        assertThat( json, hasJsonPath( "$.conformsTo", hasItem( CORE.getConformanceClass() ) ) );
        assertThat( json, hasJsonPath( "$.conformsTo", hasItem( OPENAPI30.getConformanceClass() ) ) );
    }

    @Test
    public void test_ConformanceDeclaration_Xml_ShouldBeAvailable() {
        final String xml = target( "/datasets/oaf/conformance" ).request( APPLICATION_XML ).get( String.class );

        assertThat( the( xml ), hasXPath( "//c:ConformsTo/atom:link[@href = '" +CORE.getConformanceClass()+ "']" , nsContext() ) );
        assertThat( the( xml ), hasXPath( "//c:ConformsTo/atom:link[@href = '" +OPENAPI30.getConformanceClass()+ "']" , nsContext() ) );
    }

    private NamespaceContext nsContext() {
        SimpleNamespaceContext nsContext = new SimpleNamespaceContext()
                        .withBinding( "c", XML_CORE_NS_URL )
                        .withBinding( "atom", XML_ATOM_NS_URL );
        return nsContext;
    }

}
