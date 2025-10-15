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

import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jakarta.ws.rs.core.Application;
import javax.xml.namespace.QName;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static jakarta.ws.rs.core.MediaType.APPLICATION_XML;
import static org.deegree.services.oaf.TestData.mockWorkspaceInitializer;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AppSchemaFileTest extends JerseyTest {

	@TempDir
	public static File temporaryFolder;

	private static Path pathToXsd;

	@BeforeAll
	static void initXsd() throws IOException {
		pathToXsd = Path.of(newFile(temporaryFolder, "kita.xsd").toURI());
	}

	@Override
	protected Application configure() {
		enable(TestProperties.LOG_TRAFFIC);
		ResourceConfig resourceConfig = new ResourceConfig(AppschemaFile.class);
		resourceConfig.register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(mockWorkspaceInitializer(new QName("http://www.deegree.org/app", "KitaEinrichtungen"), pathToXsd))
					.to(DeegreeWorkspaceInitializer.class);
			}
		});
		return resourceConfig;
	}

	@Test
	void app_schema_file_declaration_xml_should_be_available() {
		int statusCode = target("/appschemas/kita.xsd").request(APPLICATION_XML).get().getStatus();

		assertThat(statusCode, is(200));
	}

	private static File newFile(File parent, String child) throws IOException {
		File result = new File(parent, child);
		result.createNewFile();
		return result;
	}

}
