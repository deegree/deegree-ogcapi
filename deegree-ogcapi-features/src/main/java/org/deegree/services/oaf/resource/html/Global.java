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
import org.deegree.services.oaf.config.htmlview.HtmlViewConfiguration;
import org.deegree.services.oaf.domain.html.HtmlPageConfiguration;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Path("/")
public class Global {

    @Context
    ServletContext servletContext;

    @Inject
    private DeegreeWorkspaceInitializer deegreeWorkspaceInitializer;

    @Operation(hidden = true)
    @Path("css/main.css")
    @GET
    public InputStream getDefaultCssFile()
                    throws FileNotFoundException {
        HtmlViewConfiguration globalHtmlViewConfiguration = deegreeWorkspaceInitializer.getGlobalHtmlViewConfiguration();
        if ( globalHtmlViewConfiguration != null && globalHtmlViewConfiguration.getCssFile() != null )
            return new FileInputStream( globalHtmlViewConfiguration.getCssFile() );
        return getClass().getResourceAsStream( "/css/main.css" );
    }

    @Operation(hidden = true)
    @Path("html")
    @GET
    @Produces(APPLICATION_JSON)
    public Response getDefaultHtmlConfig() {
        HtmlViewConfiguration globalHtmlViewConfiguration = deegreeWorkspaceInitializer.getGlobalHtmlViewConfiguration();
        if ( globalHtmlViewConfiguration == null || ( globalHtmlViewConfiguration.getLegalNoticeUrl() == null
                                                      && globalHtmlViewConfiguration.getPrivacyUrl() == null
                                                      && globalHtmlViewConfiguration.getDocumentationUrl() == null ) )
            return Response.status( Response.Status.NOT_FOUND ).build();
        HtmlPageConfiguration configuration = new HtmlPageConfiguration( globalHtmlViewConfiguration.getLegalNoticeUrl(),
                                                                         globalHtmlViewConfiguration.getPrivacyUrl(),
                                                                         globalHtmlViewConfiguration.getDocumentationUrl() );
        return Response.ok( configuration, APPLICATION_JSON ).build();
    }

}
