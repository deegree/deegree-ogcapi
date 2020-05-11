package org.deegree.services.oaf.resource;

import org.deegree.services.oaf.OgcApiFeaturesMediaType;
import org.deegree.services.oaf.openapi.OpenApiCreator;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.deegree.services.oaf.workspace.configuration.DatasetMetadata;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;
import org.deegree.services.oaf.workspace.configuration.OafDatasets;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static org.deegree.services.oaf.TestData.mockWorkspaceInitializer;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class OpenApiTest extends JerseyTest {

    @Override
    protected Application configure() {
        enable( TestProperties.LOG_TRAFFIC );
        ServletContext servletContext = mock( ServletContext.class );
        when( servletContext.getContextPath() ).thenReturn( "" );
        ServletConfig servletConfig = mock( ServletConfig.class );
        when( servletConfig.getServletContext() ).thenReturn( servletContext );
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.packages( "org.deegree.services.oaf.resource" );
        resourceConfig.register( new AbstractBinder() {
            @Override
            protected void configure() {
                bind( servletConfig ).to( ServletConfig.class );
                bindAsContract( OpenApiCreator.class );
                bind( mockWorkspaceInitializer( "strassenbaum" ) ).to( DeegreeWorkspaceInitializer.class );
            }
        } );
        return resourceConfig;
    }

    @Before
    public void mockWorkspace() {
        OafDatasetConfiguration oafConfiguration = Mockito.mock( OafDatasetConfiguration.class );
        DatasetMetadata serviceMetadata = Mockito.mock( DatasetMetadata.class );
        when( oafConfiguration.getServiceMetadata() ).thenReturn( serviceMetadata );
        OafDatasets oafDatasets = new OafDatasets();
        oafDatasets.addDataset( "oaf", oafConfiguration );
        DeegreeWorkspaceInitializer deegreeWorkspaceInitializer = mock( DeegreeWorkspaceInitializer.class );
        when( deegreeWorkspaceInitializer.getOafDatasets() ).thenReturn( oafDatasets );
    }

    @Test
    public void test_OpenApiDeclarationShouldBeAvailable() {
        int status = target( "/datasets/oaf/api" ).request(
                        OgcApiFeaturesMediaType.APPLICATION_OPENAPI ).get().getStatus();
        assertThat( status, is( 200 ) );
    }

    @Ignore
    @Test
    public void test_OpenApiContent() {
        String json = target( "/datasets/oaf/api" ).request( OgcApiFeaturesMediaType.APPLICATION_OPENAPI ).get(
                        String.class );

        assertThat( json, isJson() );
        assertThat( json, hasJsonPath( "$.openapi", equalTo( "3.0.1" ) ) );
        assertThat( json, hasJsonPath( "$.paths./" ) );
        assertThat( json, hasJsonPath( "$.paths./conformance" ) );
        assertThat( json, hasJsonPath( "$.paths./collections" ) );
        assertThat( json, hasJsonPath( "$.paths./collections/{collectionId}" ) );
        assertThat( json, hasJsonPath( "$.paths./collections/{collectionId}/items" ) );
        assertThat( json, hasJsonPath( "$.paths./collections/{collectionId}/items/{featureId}" ) );
        assertThat( json, hasJsonPath( "$.paths./api" ) );
    }

}