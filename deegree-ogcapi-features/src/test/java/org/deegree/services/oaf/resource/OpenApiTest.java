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

import org.deegree.commons.utils.TunableParameter;
import org.deegree.services.oaf.OgcApiFeaturesMediaType;
import org.deegree.services.oaf.filter.OpenApiAliasFilter;
import org.deegree.services.oaf.openapi.OpenApiCreator;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.io.IOException;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static org.deegree.services.oaf.TestData.mockWorkspaceInitializer;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class OpenApiTest extends JerseyTest {

	@TempDir
	public static File temporaryFolder;

	@BeforeAll
	static void copyToTmpFolder() throws IOException {
		newFile(temporaryFolder, "swagger-ui-bundle.css");
		newFile(temporaryFolder, "swagger-ui-bundle.js");
		newFile(temporaryFolder, "index.html");
	}

	@Override
	protected Application configure() {
		TunableParameter.resetCache();

		enable(TestProperties.LOG_TRAFFIC);
		ServletContext servletContext = mock(ServletContext.class);
		when(servletContext.getContextPath()).thenReturn("");
		when(servletContext.getRealPath("/swagger-ui/")).thenReturn(temporaryFolder.toString());
		ServletConfig servletConfig = mock(ServletConfig.class);
		when(servletConfig.getServletContext()).thenReturn(servletContext);
		ResourceConfig resourceConfig = new ResourceConfig();
		resourceConfig.packages("org.deegree.services.oaf.resource");
		resourceConfig.register(OpenApiAliasFilter.class);
		resourceConfig.register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(servletContext).to(ServletContext.class);
				bind(servletConfig).to(ServletConfig.class);
				bindAsContract(OpenApiCreator.class);
				bind(mockWorkspaceInitializer()).to(DeegreeWorkspaceInitializer.class);
			}
		});
		return resourceConfig;
	}

	@Test
	void open_api_declaration_should_be_available() {
		int status = target("/datasets/oaf/api").request(OgcApiFeaturesMediaType.APPLICATION_OPENAPI).get().getStatus();
		assertThat(status, is(200));
	}

	@Test
	void open_api_declaration_alias_should_be_available() {
		int status = target("/datasets/oaf/openapi").request(OgcApiFeaturesMediaType.APPLICATION_OPENAPI)
			.get()
			.getStatus();
		assertThat(status, is(200));
	}

	@Test
	void open_api_html_should_be_available() {
		int status = target("/datasets/oaf/api").request(MediaType.TEXT_HTML).get().getStatus();
		assertThat(status, is(200));
	}

	@Test
	void open_api_yaml_should_be_available() {
		Response response = target("/datasets/oaf/api").request(OgcApiFeaturesMediaType.APPLICATION_YAML_TYPE).get();
		assertThat(response.getStatus(), is(200));
		assertThat(response.getHeaderString(HttpHeaders.CONTENT_TYPE), is(OgcApiFeaturesMediaType.APPLICATION_YAML));
	}

	@Test
	void open_api_css_should_return_correct_mime_type() {
		Response response = target("/datasets/oaf/api/swagger-ui-bundle.css").request().get();
		assertThat(response.getStatus(), is(200));
		assertThat(response.getMediaType().toString(), is("text/css"));
	}

	@Test
	void open_api_javascript_should_return_correct_mime_type() {
		Response response = target("/datasets/oaf/api/swagger-ui-bundle.js").request().get();
		assertThat(response.getStatus(), is(200));
		assertThat(response.getMediaType().toString(), is("text/javascript"));
	}

	/**
	 * Test that by default there is no CORS header returned.
	 */
	@Test
	void open_api_cors_header() {
		Response response = target("/datasets/oaf/api").request(OgcApiFeaturesMediaType.APPLICATION_OPENAPI).get();
		assertNull(response.getHeaderString("Access-Control-Allow-Origin"));
	}

	@Test
	void open_api_content() {
		String json = target("/datasets/oaf/api").request(OgcApiFeaturesMediaType.APPLICATION_OPENAPI)
			.get(String.class);

		assertThat(json, isJson());
		assertThat(json, hasJsonPath("$.openapi", equalTo("3.0.1")));
		assertThat(json, hasJsonPath("$.paths./"));
		assertThat(json, hasJsonPath("$.paths./conformance"));
		assertThat(json, hasJsonPath("$.paths./collections"));
		assertThat(json, hasJsonPath("$.paths./collections/strassenbaumkataster"));
		assertThat(json, hasJsonPath("$.paths./collections/strassenbaumkataster/items"));
		assertThat(json, hasJsonPath("$.paths./collections/strassenbaumkataster/items/{featureId}"));
		assertThat(json, hasJsonPath("$.paths./api"));
	}

	private static File newFile(File parent, String child) throws IOException {
		File result = new File(parent, child);
		result.createNewFile();
		return result;
	}

}
