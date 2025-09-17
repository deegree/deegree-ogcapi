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

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_XML;
import static jakarta.ws.rs.core.MediaType.TEXT_HTML;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GEOJSON;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_32;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_SF0;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_SF2;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_OPENAPI;
import static org.deegree.services.oaf.TestData.mockWorkspaceInitializer;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.deegree.services.oaf.openapi.OafOpenApiFilter;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
class OafOpenApiFilterTest {

	private static final String BASE_URI = "http://localhost:8081/deegree-services-oaf";

	@Mock
	static UriInfo uriInfo = mock(UriInfo.class);

	@BeforeAll
	static void mockUriInfo(){
        when(uriInfo.getBaseUriBuilder()).thenReturn(UriBuilder.fromUri(BASE_URI),UriBuilder.fromUri(BASE_URI),UriBuilder.fromUri(BASE_URI));
    }

	@Test
	void filterOperation() throws Exception {
		OpenAPIV3Parser parser = new OpenAPIV3Parser();
		URL resource = OafOpenApiFilterTest.class.getResource("openapi.json");
		OpenAPI openAPI = parser.read(resource.toExternalForm());

		DeegreeWorkspaceInitializer deegreeWorkspaceInitializer = mockWorkspaceInitializer();

		OafOpenApiFilter filter = new OafOpenApiFilter(uriInfo, "oaf", deegreeWorkspaceInitializer);
		filter.filterOpenAPI(openAPI, null, null, null);

		Paths paths = openAPI.getPaths();

		assertThat(paths.get("/api"), notNullValue());
		assertThat(paths.get("/api"), hasResponseMediaType(APPLICATION_OPENAPI, TEXT_HTML));

		assertThat(paths.get("/conformance"), notNullValue());
		assertThat(paths.get("/conformance"), hasResponseMediaType(APPLICATION_JSON, APPLICATION_XML, TEXT_HTML));

		assertThat(paths.get("/collections"), notNullValue());
		assertThat(paths.get("/collections"), hasResponseMediaType(APPLICATION_JSON, APPLICATION_XML, TEXT_HTML));

		assertThat(paths.get("/collections/strassenbaumkataster"), notNullValue());
		assertThat(paths.get("/collections/strassenbaumkataster"),
				hasResponseMediaType(APPLICATION_JSON, APPLICATION_XML, TEXT_HTML));

		assertThat(paths.get("/collections/strassenbaumkataster/items"), notNullValue());
		assertThat(paths.get("/collections/strassenbaumkataster/items"), hasResponseMediaType(APPLICATION_GEOJSON,
				APPLICATION_GML, APPLICATION_GML_32, APPLICATION_GML_SF0, APPLICATION_GML_SF2, TEXT_HTML));

		assertThat(paths.get("/collections/strassenbaumkataster/items/{featureId}"), notNullValue());
		assertThat(paths.get("/collections/strassenbaumkataster/items/{featureId}"),
				hasResponseMediaType(APPLICATION_GEOJSON, APPLICATION_GML, APPLICATION_GML_32, APPLICATION_GML_SF0,
						APPLICATION_GML_SF2, TEXT_HTML));

		List<Server> servers = openAPI.getServers();
		assertThat(servers.size(), is(1));
		assertThat(servers.get(0).getUrl(), is("http://localhost:8081/deegree-services-oaf/datasets/oaf"));
	}

	@Test
	void filterOperationWithPrimitiveList() throws Exception {
		OpenAPIV3Parser parser = new OpenAPIV3Parser();
		URL resource = OafOpenApiFilterTest.class.getResource("openapi.json");
		OpenAPI openAPI = parser.read(resource.toExternalForm());

		DeegreeWorkspaceInitializer deegreeWorkspaceInitializer = mockWorkspaceInitializer(
				new QName("http://www.deegree.org/app", "KitaEinrichtungen"));

		OafOpenApiFilter filter = new OafOpenApiFilter(uriInfo, "oaf", deegreeWorkspaceInitializer);
		filter.filterOpenAPI(openAPI, null, null, null);

		Paths paths = openAPI.getPaths();
		PathItem path = paths.get("/collections/KitaEinrichtungen/items");
		assertThat(path, notNullValue());
		Schema schema = path.getGet().getResponses().getDefault().getContent().get("application/geo+json").getSchema();
		Map<String, Schema> properties = schema.getProperties();
		assertThat(properties.get("type").getType(), is("string"));
		assertThat(properties.get("numberMatched").getType(), is("integer"));
		assertThat(properties.get("numberReturned").getType(), is("integer"));
		assertThat(properties.get("timeStamp").getType(), is("string"));
		assertThat(properties.get("links").getType(), is("array"));

		Schema featuresSchema = (Schema) properties.get("features");
		Map<String, Schema> featuresProperties = featuresSchema.getProperties();

		assertThat(featuresProperties.get("type").getType(), is("string"));
		assertThat(featuresProperties.get("id").getType(), is("string"));
		assertThat(featuresProperties.get("geometry").getType(), is("object"));
		assertThat(featuresProperties.get("properties").getType(), is("object"));

		Schema propertiesSchema = featuresProperties.get("properties");
		ArraySchema leistungsnameSchema = (ArraySchema) propertiesSchema.getProperties().get("Leistungsname");
		assertThat(leistungsnameSchema.getType(), is("array"));
		assertThat(leistungsnameSchema.getItems().getType(), is("string"));

		List<Server> servers = openAPI.getServers();
		assertThat(servers.size(), is(1));
		assertThat(servers.get(0).getUrl(), is("http://localhost:8081/deegree-services-oaf/datasets/oaf"));
	}

	@Test
	void filterOperationWithComplexData() throws Exception {
		OpenAPIV3Parser parser = new OpenAPIV3Parser();
		URL resource = OafOpenApiFilterTest.class.getResource("openapi.json");
		OpenAPI openAPI = parser.read(resource.toExternalForm());

		DeegreeWorkspaceInitializer deegreeWorkspaceInitializer = mockWorkspaceInitializer(
				new QName("http://www.deegree.org/datasource/feature/sql", "Zuwanderung"));

		OafOpenApiFilter filter = new OafOpenApiFilter(uriInfo, "oaf", deegreeWorkspaceInitializer);
		filter.filterOpenAPI(openAPI, null, null, null);

		Paths paths = openAPI.getPaths();
		PathItem path = paths.get("/collections/Zuwanderung/items");
		assertThat(path, notNullValue());
		Schema schema = path.getGet().getResponses().getDefault().getContent().get("application/geo+json").getSchema();
		Schema featuresSchema = (Schema) schema.getProperties().get("features");
		Schema propertiesSchema = (Schema) featuresSchema.getProperties().get("properties");

		Schema wohnungslose_jepSchema = (Schema) propertiesSchema.getProperties().get("wohnungslose_jep");
		Schema zeitreiheSchema = (Schema) wohnungslose_jepSchema.getProperties().get("zeitreihe");

		ArraySchema zeitreihenElementSchema = (ArraySchema) zeitreiheSchema.getProperties().get("zeitreihen-element");
		assertThat(zeitreihenElementSchema.getType(), is("array"));
		Schema zeitreihenElementItems = zeitreihenElementSchema.getItems();
		assertThat(((Schema) zeitreihenElementItems.getProperties().get("wert")).getType(), is("string"));
		assertThat(((Schema) zeitreihenElementItems.getProperties().get("datum")).getType(), is("string"));

		ArraySchema countryListSchema = (ArraySchema) zeitreihenElementItems.getProperties().get("country-list");
		assertThat(countryListSchema.getType(), is("array"));
		Schema countryListItems = countryListSchema.getItems();
		ArraySchema countryComplexSchema = (ArraySchema) countryListItems.getProperties().get("country-complex");
		Schema countryComplexItems = countryComplexSchema.getItems();
		assertThat(((Schema) countryComplexItems.getProperties().get("name")).getType(), is("string"));
		assertThat(((Schema) countryComplexItems.getProperties().get("pop")).getType(), is("number"));

		List<Server> servers = openAPI.getServers();
		assertThat(servers.size(), is(1));
		assertThat(servers.get(0).getUrl(), is("http://localhost:8081/deegree-services-oaf/datasets/oaf"));
	}

	private Matcher<PathItem> hasResponseMediaType(String... mediaTypes) {
		return new BaseMatcher<PathItem>() {
			@Override
			public boolean matches(Object o) {
				Content content = ((PathItem) o).getGet().getResponses().getDefault().getContent();
				for (String mediaType : mediaTypes) {
					if (!content.containsKey(mediaType))
						return false;
				}
				return true;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("At least one of the expected media types is not supported: ");
				description.appendValue(mediaTypes);
			}
		};
	}

}
