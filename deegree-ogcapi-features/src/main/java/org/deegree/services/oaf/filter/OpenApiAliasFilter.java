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
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

/**
 * Filter that provides OpenApi endpoints under the alias "openapi" in addition to the original "api".
 */
@Provider
@PreMatching
public class OpenApiAliasFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		UriInfo orgUri = requestContext.getUriInfo();
		List<PathSegment> segments = orgUri.getPathSegments();
		
		// if the relative path is /datasets/{dataset}/openapi* replace "openapi" with "api"
		if (segments.size() == 3 && segments.get(2).getPath().startsWith("openapi") && "datasets".equals(segments.get(0).getPath())) {
			StringBuilder newPath = new StringBuilder(orgUri.getBaseUri().getPath());
			newPath.append("datasets/");
			newPath.append(segments.get(1).getPath());
			newPath.append("/");
			newPath.append(segments.get(2).getPath().replace("openapi", "api"));
			requestContext.setRequestUri(orgUri.getBaseUri(), orgUri.getRequestUriBuilder().replacePath(newPath.toString()).build());
		}
	}

}
