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

import org.deegree.services.oaf.resource.FeatureCollection;
import org.deegree.services.oaf.workspace.DataAccess;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static org.deegree.services.oaf.TestData.mockDataAccess;
import static org.deegree.services.oaf.TestData.mockWorkspaceInitializer;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test overriding filter for overriding format via query parameter or extension.
 */
public class OverrideAcceptFilterTest extends JerseyTest {

	@Override
	protected Application configure() {
		enable(TestProperties.LOG_TRAFFIC);
		ResourceConfig resourceConfig = new ResourceConfig(FeatureCollection.class);
		resourceConfig.register(OverrideAcceptFilter.class);
		resourceConfig.register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(mockDataAccess()).to(DataAccess.class);
				bind(mockWorkspaceInitializer()).to(DeegreeWorkspaceInitializer.class);
			}
		});
		return resourceConfig;
	}

	@Test
	public void test_Header() {
		Response response = target("/datasets/oaf/collections/test").request(APPLICATION_JSON_TYPE).get();
		assertThat(response.getStatus(), is(200));
		assertThat(response.getHeaderString(HttpHeaders.CONTENT_TYPE), is(APPLICATION_JSON));
	}

	@Test
	public void test_Extension() {
		Response response = target("/datasets/oaf/collections/test.xml").request().get();
		assertThat(response.getStatus(), is(200));
		assertThat(response.getHeaderString(HttpHeaders.CONTENT_TYPE), is(APPLICATION_XML));
	}

	@Test
	public void test_ExtensionFallback() {
		Response response = target("/datasets/oaf/collections/test.xkcd").request(APPLICATION_JSON_TYPE).get();
		assertThat(response.getStatus(), is(200));
		assertThat(response.getHeaderString(HttpHeaders.CONTENT_TYPE), is(APPLICATION_JSON));
	}

	@Test
	public void test_QueryParam_Accept() {
		Response response = target("/datasets/oaf/collections/test").queryParam("accept", "xml").request().get();
		assertThat(response.getStatus(), is(200));
		assertThat(response.getHeaderString(HttpHeaders.CONTENT_TYPE), is(APPLICATION_XML));
	}

	@Test
	public void test_QueryParamMediaType_Accept() {
		Response response = target("/datasets/oaf/collections/test").queryParam("accept", APPLICATION_XML)
			.request()
			.get();
		assertThat(response.getStatus(), is(200));
		assertThat(response.getHeaderString(HttpHeaders.CONTENT_TYPE), is(APPLICATION_XML));
	}

	@Test
	public void test_QueryParam_f_xml() {
		Response response = target("/datasets/oaf/collections/test").queryParam("f", "xml").request().get();
		assertThat(response.getStatus(), is(200));
		assertThat(response.getHeaderString(HttpHeaders.CONTENT_TYPE), is(APPLICATION_XML));
	}

	@Test
	public void test_QueryParam_f_json() {
		Response response = target("/datasets/oaf/collections/test").queryParam("f", "json").request().get();
		assertThat(response.getStatus(), is(200));
		assertThat(response.getHeaderString(HttpHeaders.CONTENT_TYPE), is(APPLICATION_JSON));
	}

	@Test
	public void test_Priorities() {
		Response response = target("/datasets/oaf/collections/test.html").queryParam("accept", "xml")
			.request(APPLICATION_JSON_TYPE)
			.get();
		assertThat(response.getStatus(), is(200));
		assertThat(response.getHeaderString(HttpHeaders.CONTENT_TYPE), is(APPLICATION_XML));
	}

	@Test
	public void test_PriorityExtension() {
		Response response = target("/datasets/oaf/collections/test.html").request(APPLICATION_JSON_TYPE).get();
		assertThat(response.getStatus(), is(200));
		assertThat(response.getHeaderString(HttpHeaders.CONTENT_TYPE), is(TEXT_HTML));
	}

}
