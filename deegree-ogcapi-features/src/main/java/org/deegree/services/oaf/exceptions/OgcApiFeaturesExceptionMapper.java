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
package org.deegree.services.oaf.exceptions;

import org.deegree.services.oaf.domain.exceptions.OgcApiFeaturesExceptionReport;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.deegree.services.oaf.exceptions.ExceptionMediaTypeUtil.selectMediaType;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Provider
public class OgcApiFeaturesExceptionMapper implements ExceptionMapper<OgcApiFeaturesException> {

    @Context
    Request request;

    @Override
    public Response toResponse( OgcApiFeaturesException exception ) {
        MediaType selectedType = selectMediaType( request );

        OgcApiFeaturesExceptionReport oafExceptionReport = new OgcApiFeaturesExceptionReport( exception.getMessage(),
                                                                                              NOT_FOUND.getStatusCode() );

        return Response.status( exception.getStatusCode() ).entity( oafExceptionReport ).type( selectedType ).build();
    }

}
