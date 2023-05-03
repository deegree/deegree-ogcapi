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

import org.deegree.services.oaf.exceptions.OgcApiFeaturesExceptionMapper;
import org.deegree.services.oaf.link.LinkRelation;
import org.deegree.services.oaf.openapi.OpenApiCreator;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.APPLICATION_XML_TYPE;
import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static javax.ws.rs.core.MediaType.TEXT_HTML_TYPE;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_ATOM_NS_URL;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_CORE_NS_URL;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_OPENAPI;
import static org.deegree.services.oaf.RequestFormat.HTML;
import static org.deegree.services.oaf.RequestFormat.JSON;
import static org.deegree.services.oaf.RequestFormat.XML;
import static org.deegree.services.oaf.TestData.mockWorkspaceInitializer;
import static org.deegree.services.oaf.link.LinkRelation.ALTERNATE;
import static org.deegree.services.oaf.link.LinkRelation.CONFORMANCE;
import static org.deegree.services.oaf.link.LinkRelation.DATA;
import static org.deegree.services.oaf.link.LinkRelation.SELF;
import static org.deegree.services.oaf.link.LinkRelation.SERVICE_DESC;
import static org.deegree.services.oaf.link.LinkRelation.SERVICE_DOC;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.HasXPathMatcher.hasXPath;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class LandingPageTest extends JerseyTest {

    @Override
    protected Application configure() {
        enable( TestProperties.LOG_TRAFFIC );
        ResourceConfig resourceConfig = new ResourceConfig( LandingPage.class, OgcApiFeaturesExceptionMapper.class );
        resourceConfig.register( new AbstractBinder() {
            @Override
            protected void configure() {
                bind( mockWorkspaceInitializer() ).to( DeegreeWorkspaceInitializer.class );
                bindAsContract( OpenApiCreator.class );
            }
        } );
        return resourceConfig;
    }

    @Test
    public void test_LandingPageDeclaration_Json_ShouldBeAvailable() {
        Response response = target( "/datasets/oaf" ).request( APPLICATION_JSON_TYPE ).get();
        assertThat( response.getStatus(), is( 200 ) );
        assertThat( response.getMediaType(), is( APPLICATION_JSON_TYPE ) );
        assertThat( response.readEntity( String.class ), isJson() );
    }

    @Test
    public void test_LandingPageDeclaration_Json_FormatXml_ShouldBeAvailable() {
        Response response = target( "/datasets/oaf" ).queryParam( "f", XML.name() ).request(
                        APPLICATION_JSON_TYPE ).get();
        assertThat( response.getStatus(), is( 200 ) );
        assertThat( response.getMediaType(), is( APPLICATION_XML_TYPE ) );
        String xml = response.readEntity( String.class );
        assertThat( xml, hasXPath( "/core:LandingPage" ).withNamespaceContext( nsContext() ) );
    }

    @Test
    public void test_LandingPageDeclaration_Json_FormatHtml_ShouldBeAvailable() {
        Response response = target( "/datasets/oaf" ).queryParam( "f", HTML.name() ).request(
                        APPLICATION_JSON_TYPE ).get();
        assertThat( response.getStatus(), is( 200 ) );
        assertThat( response.getMediaType(), is( TEXT_HTML_TYPE ) );
    }

    @Test
    public void test_LandingPageDeclaration_Xml_ShouldBeAvailable() {
        Response response = target( "/datasets/oaf" ).request( APPLICATION_XML ).get();
        assertThat( response.getStatus(), is( 200 ) );
        assertThat( response.getMediaType(), is( APPLICATION_XML_TYPE ) );
        String xml = response.readEntity( String.class );
        assertThat( xml, hasXPath( "/core:LandingPage" ).withNamespaceContext( nsContext() ) );
    }

    @Test
    public void test_LandingPageDeclaration_Xml_FormatJson_ShouldBeAvailable() {
        Response response = target( "/datasets/oaf" ).queryParam( "f", JSON.name() ).request( APPLICATION_XML ).get();
        assertThat( response.getStatus(), is( 200 ) );
        assertThat( response.getMediaType(), is( APPLICATION_JSON_TYPE ) );
        assertThat( response.readEntity( String.class ), isJson() );
    }

    @Test
    public void test_LandingPageDeclaration_Xml_FormatHtml_ShouldBeAvailable() {
        Response response = target( "/datasets/oaf" ).queryParam( "f", HTML.name() ).request( APPLICATION_XML ).get();
        assertThat( response.getStatus(), is( 200 ) );
        assertThat( response.getMediaType(), is( TEXT_HTML_TYPE ) );
    }

    @Test
    public void test_LandingPageDeclaration_Html_ShouldBeAvailable() {
        Response response = target( "/datasets/oaf" ).request( TEXT_HTML_TYPE ).get();
        assertThat( response.getStatus(), is( 200 ) );
        assertThat( response.getMediaType(), is( TEXT_HTML_TYPE ) );
    }

    @Test
    public void test_LandingPageDeclaration_Hml_FormatJson_ShouldBeAvailable() {
        Response response = target( "/datasets/oaf" ).queryParam( "f", JSON.name() ).request( TEXT_HTML_TYPE ).get();
        assertThat( response.getStatus(), is( 200 ) );
        assertThat( response.getMediaType(), is( APPLICATION_JSON_TYPE ) );
        assertThat( response.readEntity( String.class ), isJson() );
    }

    @Test
    public void test_LandingPageDeclaration_Hml_FormatXml_ShouldBeAvailable() {
        Response response = target( "/datasets/oaf" ).queryParam( "f", XML.name() ).request( TEXT_HTML_TYPE ).get();
        assertThat( response.getStatus(), is( 200 ) );
        assertThat( response.getMediaType(), is( APPLICATION_XML_TYPE ) );
    }

    @Test
    public void test_LandingPageDeclaration_Json_UnknownDatasetId() {
        Response response = target( "/datasets/unknown" ).request( APPLICATION_JSON_TYPE ).get();
        assertThat( response.getStatus(), is( 404 ) );
        assertThat( response.getMediaType(), is( APPLICATION_JSON_TYPE ) );
        assertThat( response.readEntity( String.class ), isJson() );
    }

    @Test
    public void test_LandingPageDeclaration_Xml_UnknownDatasetId() {
        Response response = target( "/datasets/unknown" ).request( APPLICATION_XML_TYPE ).get();
        assertThat( response.getStatus(), is( 404 ) );
        assertThat( response.getMediaType(), is( APPLICATION_XML_TYPE ) );
        String xml = response.readEntity( String.class );
        assertThat( xml, hasXPath( "/core:ExceptionReport" ).withNamespaceContext( nsContext() ) );
    }

    @Test
    public void test_LandingPageDeclaration_Json_UnknownFormat() {
        Response response = target( "/datasets/oaf" ).queryParam( "f", "unknown" ).request(
                        APPLICATION_JSON_TYPE ).get();
        assertThat( response.getStatus(), is( 400 ) );
        assertThat( response.getMediaType(), is( APPLICATION_JSON_TYPE ) );
        assertThat( response.readEntity( String.class ), isJson() );
    }

    @Test
    public void test_LandingPage_Links() {
        Response response = target( "/datasets/oaf" ).request( APPLICATION_XML ).get();
        String xml = response.readEntity( String.class );
        assertThat( xml, hasXPath( linkWith( SELF, APPLICATION_JSON ) ).withNamespaceContext( nsContext() ) );
        assertThat( xml, hasXPath( linkWith( ALTERNATE, TEXT_HTML ) ).withNamespaceContext( nsContext() ) );
        assertThat( xml, hasXPath( linkWith( ALTERNATE, APPLICATION_XML ) ).withNamespaceContext( nsContext() ) );
        assertThat( xml,
                    hasXPath( linkWith( SERVICE_DESC, APPLICATION_OPENAPI ) ).withNamespaceContext( nsContext() ) );
        assertThat( xml, hasXPath( linkWith( SERVICE_DOC, TEXT_HTML ) ).withNamespaceContext( nsContext() ) );
        assertThat( xml, hasXPath( linkWith( CONFORMANCE, APPLICATION_JSON ) ).withNamespaceContext( nsContext() ) );
        assertThat( xml, hasXPath( linkWith( CONFORMANCE, TEXT_HTML ) ).withNamespaceContext( nsContext() ) );
        assertThat( xml, hasXPath( linkWith( CONFORMANCE, APPLICATION_XML ) ).withNamespaceContext( nsContext() ) );
        assertThat( xml, hasXPath( linkWith( DATA, APPLICATION_JSON ) ).withNamespaceContext( nsContext() ) );
        assertThat( xml, hasXPath( linkWith( DATA, TEXT_HTML ) ).withNamespaceContext( nsContext() ) );
        assertThat( xml, hasXPath( linkWith( DATA, APPLICATION_XML ) ).withNamespaceContext( nsContext() ) );
    }

    private String linkWith( LinkRelation self, String applicationJson ) {
        return "/core:LandingPage/atom:link[@rel='" + self.getRel() +
               "' and @type='" + applicationJson + "']";
    }

    private Map<String, String> nsContext() {
        Map<String, String> nsContext = new HashMap<>();
        nsContext.put( "core", XML_CORE_NS_URL );
        nsContext.put( "atom", XML_ATOM_NS_URL );
        return nsContext;
    }

}
