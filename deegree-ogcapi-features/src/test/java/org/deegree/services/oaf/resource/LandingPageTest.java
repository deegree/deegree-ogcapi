package org.deegree.services.oaf.resource;

import org.deegree.services.oaf.exceptions.UnknownDatasetId;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;
import org.deegree.services.oaf.workspace.configuration.OafDatasets;
import org.deegree.services.oaf.workspace.configuration.DatasetMetadata;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xmlmatchers.namespace.SimpleNamespaceContext;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.APPLICATION_XML_TYPE;
import static javax.ws.rs.core.MediaType.TEXT_HTML_TYPE;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_CORE_NS_URL;
import static org.deegree.services.oaf.RequestFormat.HTML;
import static org.deegree.services.oaf.RequestFormat.JSON;
import static org.deegree.services.oaf.RequestFormat.XML;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.xmlmatchers.XmlMatchers.hasXPath;
import static org.xmlmatchers.transform.XmlConverters.the;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(DeegreeWorkspaceInitializer.class)
public class LandingPageTest extends JerseyTest {

    @Override
    protected Application configure() {
        enable( TestProperties.LOG_TRAFFIC );
        return new ResourceConfig( LandingPage.class, UnknownDatasetId.class );
    }

    @Before
    public void mockWorkspace() {
        PowerMockito.mockStatic( DeegreeWorkspaceInitializer.class );
        OafDatasetConfiguration oafConfiguration = Mockito.mock( OafDatasetConfiguration.class );
        DatasetMetadata serviceMetadata = Mockito.mock( DatasetMetadata.class );
        when( oafConfiguration.getServiceMetadata() ).thenReturn( serviceMetadata );
        OafDatasets oafDatasets = new OafDatasets();
        oafDatasets.addDataset( "oaf", oafConfiguration );
        when( DeegreeWorkspaceInitializer.getOafDatasets() ).thenReturn( oafDatasets );
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
        assertThat( the( xml ), hasXPath( "/core:LandingPage", nsContext() ) );
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
        assertThat( the( xml ), hasXPath( "/core:LandingPage", nsContext() ) );
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
        assertThat( the( xml ), hasXPath( "/core:ExceptionReport", nsContext() ) );
    }

    @Test
    public void test_LandingPageDeclaration_Json_UnknownFormat() {
        Response response = target( "/datasets/oaf" ).queryParam( "f", "unknown" ).request(
                        APPLICATION_JSON_TYPE ).get();
        assertThat( response.getStatus(), is( 404 ) );
        assertThat( response.getMediaType(), is( APPLICATION_JSON_TYPE ) );
        assertThat( response.readEntity( String.class ), isJson() );
    }

    private SimpleNamespaceContext nsContext() {
        return new SimpleNamespaceContext().withBinding( "core", XML_CORE_NS_URL );
    }

}
