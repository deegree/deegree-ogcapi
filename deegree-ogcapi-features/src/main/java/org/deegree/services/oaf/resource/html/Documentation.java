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

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static javax.ws.rs.core.MediaType.TEXT_HTML;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Path("/documentation")
public class Documentation {

    @Context
    private ServletContext servletContext;

    @GET
    @Produces(TEXT_HTML)
    @Path("/{path: .+}")
    @Operation(hidden = true)
    public InputStream getFile(
                    @PathParam("path")
                                    String path ) {
        try {
            String base = servletContext.getRealPath( "deegree-ogcapi-documentation/" );
            File f = new File( String.format( "%s/%s", base, path ) );
            return new FileInputStream( f );
        } catch ( FileNotFoundException e ) {
            System.out.println( e.getLocalizedMessage() );
            return null;
        }
    }

}
