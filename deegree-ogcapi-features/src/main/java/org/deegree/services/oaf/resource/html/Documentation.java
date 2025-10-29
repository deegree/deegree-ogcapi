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
package org.deegree.services.oaf.resource.html;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;

import static jakarta.ws.rs.core.MediaType.TEXT_HTML;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Path("/documentation")
public class Documentation {

	private static final Logger LOG = getLogger(Documentation.class);

	@Context
	private ServletContext servletContext;

	@GET
	@Produces(TEXT_HTML)
	@Path("/index")
	@Operation(hidden = true)
	public Response getIndexHtml() {
		return getFileFromDocumentation("index.html");
	}

	@GET
	@Path("/{path: .+}")
	@Operation(hidden = true)
	public Response getFile(@PathParam("path") String path) {
		return getFileFromDocumentation(path);
	}

	private Response getFileFromDocumentation(String path) {
		String base = servletContext.getRealPath("deegree-ogcapi-documentation/");
		File f = new File("%s/%s".formatted(base, path));
		try {
			return Response.ok(new FileInputStream(f)).build();
		}
		catch (FileNotFoundException e) {
			LOG.warn("Could not find requested file ", e);
			return Response.status(HttpStatus.SC_NOT_FOUND).build();
		}
	}

}
