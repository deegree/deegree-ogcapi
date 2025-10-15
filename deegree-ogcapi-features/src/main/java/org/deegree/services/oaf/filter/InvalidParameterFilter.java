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

import org.deegree.services.oaf.domain.exceptions.OgcApiFeaturesExceptionReport;
import org.deegree.services.oaf.resource.Features;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.deegree.services.oaf.exceptions.ExceptionMediaTypeUtil.selectMediaType;

/**
 * Checks the passed query parameters. If an query parameter has an invalid value the
 * request aborts with a BAD_REQUEST.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Provider
public class InvalidParameterFilter implements ContainerRequestFilter {

	private static final String EXCEPTION_MSG = "Parameter with name '%s' has invalid content '%s'.";

	@Context
	private ResourceInfo resourceInfo;

	@Context
	private Request request;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		if (resourceInfo.getResourceClass().isAssignableFrom(Features.class)) {
			MultivaluedMap<String, String> queryParameters = requestContext.getUriInfo().getQueryParameters();
			boolean isValid = validateInteger(requestContext, "limit", queryParameters);
			if (isValid) {
				validateInteger(requestContext, "offset", queryParameters);
			}
		}
	}

	private boolean validateInteger(ContainerRequestContext requestContext, String paramKey,
			MultivaluedMap<String, String> queryParameters) {
		List<String> queryParam = queryParameters.get(paramKey);
		if (queryParam != null) {
			try {
				queryParam.forEach(s -> {
					Integer.valueOf(s);
				});
			}
			catch (NumberFormatException e) {
				Response response = createInvalidParameterResponse(paramKey, queryParam);
				requestContext.abortWith(response);
				return false;
			}
		}
		return true;
	}

	private Response createInvalidParameterResponse(String param, List<String> queryParam) {
		String queryParamString = queryParam.stream().collect(Collectors.joining(","));
		MediaType selectedType = selectMediaType(request);
		String message = EXCEPTION_MSG.formatted(param, queryParamString);
		OgcApiFeaturesExceptionReport oafExceptionReport = new OgcApiFeaturesExceptionReport(message,
				BAD_REQUEST.getStatusCode());
		return Response.status(BAD_REQUEST).entity(oafExceptionReport).type(selectedType).build();
	}

}
