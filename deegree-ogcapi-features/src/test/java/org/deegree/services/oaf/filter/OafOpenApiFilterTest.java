package org.deegree.services.oaf.filter;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.deegree.services.oaf.exceptions.InvalidConfigurationException;
import org.deegree.services.oaf.exceptions.UnknownDatasetId;
import org.deegree.services.oaf.openapi.OafOpenApiFilter;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.deegree.services.oaf.workspace.configuration.FeatureTypeMetadata;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;
import org.deegree.services.oaf.workspace.configuration.OafDatasets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.xml.namespace.QName;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(DeegreeWorkspaceInitializer.class)
public class OafOpenApiFilterTest {

    @Test
    public void testFilterOperation()
                    throws UnknownDatasetId {
        OpenAPIV3Parser parser = new OpenAPIV3Parser();
        URL resource = OafOpenApiFilterTest.class.getResource( "openapi.json" );
        OpenAPI openAPI = parser.read( resource.toExternalForm() );

        mockOafConfiguration( "strassenbaum" );

        OafOpenApiFilter filter = new OafOpenApiFilter( "oaf");
        filter.filterOpenAPI( openAPI, null, null, null );

        assertThat( openAPI.getPaths().get( "/collections/strassenbaum" ), notNullValue() );
        assertThat( openAPI.getPaths().get( "/collections/strassenbaum/items" ), notNullValue() );
        assertThat( openAPI.getPaths().get( "/collections/strassenbaum/items/{featureId}" ), notNullValue() );
    }

    private void mockOafConfiguration( String collectionId ) {
        PowerMockito.mockStatic( DeegreeWorkspaceInitializer.class );
        OafDatasetConfiguration oafConfiguration = mock( OafDatasetConfiguration.class );
        Map<String, FeatureTypeMetadata> featureTypeMetadata = new HashMap<>();
        FeatureTypeMetadata ftm = new FeatureTypeMetadata( new QName( collectionId ) );
        featureTypeMetadata.put( collectionId, ftm );
        when( oafConfiguration.getFeatureTypeMetadata() ).thenReturn( featureTypeMetadata );

        OafDatasets oafDatasets = new OafDatasets();
        oafDatasets.addDataset( "oaf", oafConfiguration );
        when( DeegreeWorkspaceInitializer.getOafDatasets() ).thenReturn( oafDatasets );
    }
}