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

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.deegree.services.oaf.OgcApiFeaturesMediaType;
import org.deegree.services.oaf.filter.ApiVersionPathFilter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test OpenAPI resource with optional version segment enabled for path.
 */
public class OpenApiVersionSegmentTest extends OpenApiTest {
	
	
	@BeforeClass
	public static void setProperties() {
		System.setProperty( ApiVersionPathFilter.PARAMETER_ENABLE_VERSION_SEGMENT, "true" );
	}

	@AfterClass
	public static void resetProperties() {
		System.setProperty( ApiVersionPathFilter.PARAMETER_ENABLE_VERSION_SEGMENT, "" );
	}
	
	/**
	 * Test if API definition is also available on path with version segment.
	 */
	@Test
    public void test_OpenApiDeclarationShouldBeAvailableWithVersionSegment() {
        int status = target("/datasets/oaf/v1/api").request(
                OgcApiFeaturesMediaType.APPLICATION_OPENAPI).get().getStatus();
        assertThat(status, is(200));
    }
    
	/**
	 * Test that version segment is contained in server URL.
	 */
    @Test
    public void test_OpenApi_serverUrl() {
        String json = target("/datasets/oaf/api").request(OgcApiFeaturesMediaType.APPLICATION_OPENAPI).get(
                String.class);

        assertThat(json, isJson());
        assertThat(json, hasJsonPath("$.servers[0].url", endsWith("/v1")));
    }

}
