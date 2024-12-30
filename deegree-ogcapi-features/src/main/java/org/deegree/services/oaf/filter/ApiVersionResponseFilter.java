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


import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.deegree.commons.utils.TunableParameter;
import org.deegree.services.oaf.openapi.OpenApiCreator;


/**
 * Class to add API version to response headers.
 * 
 * @author Kapil Agnihotri
 */
@Provider
public class ApiVersionResponseFilter implements ContainerResponseFilter {

	/**
	 * Name for parameter that allows enabling returning the API version as response header. 
	 */
	public static final String PARAMETER_ENABLE_VERSION_HEADER = "deegree.oaf.openapi.version_response_header";
	
	private boolean addApiVersionToHeader = TunableParameter.get(PARAMETER_ENABLE_VERSION_HEADER, false);

	/**
	 * Name for response header with API version.
	 */
	public static final String HEADER_API_VERSION = "API-Version";

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {

		if (addApiVersionToHeader) {
			responseContext.getHeaders().add(HEADER_API_VERSION, OpenApiCreator.VERSION);
		}
	}

}
