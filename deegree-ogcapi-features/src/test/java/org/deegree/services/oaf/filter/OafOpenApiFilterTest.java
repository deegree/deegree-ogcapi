package org.deegree.services.oaf.filter;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.deegree.services.oaf.exceptions.UnknownDatasetId;
import org.deegree.services.oaf.openapi.OafOpenApiFilter;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.junit.Test;

import java.net.URL;

import static org.deegree.services.oaf.TestData.mockWorkspaceInitializer;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class OafOpenApiFilterTest {

    @Test
    public void testFilterOperation()
                    throws UnknownDatasetId {
        OpenAPIV3Parser parser = new OpenAPIV3Parser();
        URL resource = OafOpenApiFilterTest.class.getResource( "openapi.json" );
        OpenAPI openAPI = parser.read( resource.toExternalForm() );

        DeegreeWorkspaceInitializer deegreeWorkspaceInitializer = mockWorkspaceInitializer( "strassenbaum" );

        OafOpenApiFilter filter = new OafOpenApiFilter( "oaf", deegreeWorkspaceInitializer );
        filter.filterOpenAPI( openAPI, null, null, null );

        assertThat( openAPI.getPaths().get( "/collections/strassenbaum" ), notNullValue() );
        assertThat( openAPI.getPaths().get( "/collections/strassenbaum/items" ), notNullValue() );
        assertThat( openAPI.getPaths().get( "/collections/strassenbaum/items/{featureId}" ), notNullValue() );
    }

}