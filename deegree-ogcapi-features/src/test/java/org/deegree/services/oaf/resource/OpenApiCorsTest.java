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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import jakarta.ws.rs.core.Response;

import org.deegree.services.oaf.OgcApiFeaturesMediaType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test OpenApi resource with CORS header enabled.
 */
class OpenApiCorsTest extends OpenApiTest {

	@BeforeAll
	static void setProperties() {
		System.setProperty(OpenApi.PARAMETER_CORS_ALLOWALL, "true");
	}

	@AfterAll
	static void resetProperties() {
		System.setProperty(OpenApi.PARAMETER_CORS_ALLOWALL, "");
	}

	/**
	 * Test that when enabled a CORS header is returned.
	 */
	@Test
	void test_OpenApiCorsHeader() {
		Response response = target("/datasets/oaf/api").request(OgcApiFeaturesMediaType.APPLICATION_OPENAPI).get();
		assertThat(response.getHeaderString("Access-Control-Allow-Origin"), is("*"));
	}

}
