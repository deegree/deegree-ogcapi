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
package org.deegree.services.oaf.filter;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.deegree.services.oaf.exceptions.UnknownDatasetId;
import org.deegree.services.oaf.openapi.OafOpenApiFilter;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.net.URL;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GEOJSON;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_32;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_SF0;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_SF2;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_OPENAPI;
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

        DeegreeWorkspaceInitializer deegreeWorkspaceInitializer = mockWorkspaceInitializer();

        OafOpenApiFilter filter = new OafOpenApiFilter( "oaf", deegreeWorkspaceInitializer );
        filter.filterOpenAPI( openAPI, null, null, null );

        Paths paths = openAPI.getPaths();

        assertThat( paths.get( "/api" ), notNullValue() );
        assertThat( paths.get( "/api" ), hasResponseMediaType( APPLICATION_OPENAPI, TEXT_HTML ) );

        assertThat( paths.get( "/conformance" ), notNullValue() );
        assertThat( paths.get( "/conformance" ), hasResponseMediaType( APPLICATION_JSON, APPLICATION_XML, TEXT_HTML ) );

        assertThat( paths.get( "/collections" ), notNullValue() );
        assertThat( paths.get( "/collections" ), hasResponseMediaType( APPLICATION_JSON, APPLICATION_XML, TEXT_HTML ) );

        assertThat( paths.get( "/collections/strassenbaumkataster" ), notNullValue() );
        assertThat( paths.get( "/collections/strassenbaumkataster" ),
                    hasResponseMediaType( APPLICATION_JSON, APPLICATION_XML, TEXT_HTML ) );

        assertThat( paths.get( "/collections/strassenbaumkataster/items" ), notNullValue() );
        assertThat( paths.get( "/collections/strassenbaumkataster/items" ),
                    hasResponseMediaType( APPLICATION_GEOJSON, APPLICATION_GML, APPLICATION_GML_32, APPLICATION_GML_SF0,
                                          APPLICATION_GML_SF2, TEXT_HTML ) );

        assertThat( paths.get( "/collections/strassenbaumkataster/items/{featureId}" ), notNullValue() );
        assertThat( paths.get( "/collections/strassenbaumkataster/items/{featureId}" ),
                    hasResponseMediaType( APPLICATION_GEOJSON, APPLICATION_GML, APPLICATION_GML_32, APPLICATION_GML_SF0,
                                          APPLICATION_GML_SF2, TEXT_HTML ) );
    }

    private Matcher<PathItem> hasResponseMediaType( String... mediaTypes ) {
        return new BaseMatcher<PathItem>() {
            @Override
            public boolean matches( Object o ) {
                Content content = ( (PathItem) o ).getGet().getResponses().getDefault().getContent();
                for ( String mediaType : mediaTypes ) {
                    if ( !content.containsKey( mediaType ) )
                        return false;
                }
                return true;
            }

            @Override
            public void describeTo( Description description ) {
                description.appendText( "At least one of the expected media types is not supported: " );
                description.appendValue( mediaTypes );
            }
        };
    }

}
