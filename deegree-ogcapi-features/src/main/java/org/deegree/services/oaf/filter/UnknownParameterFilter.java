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
import org.deegree.services.oaf.exceptions.UnknownCollectionId;
import org.deegree.services.oaf.exceptions.UnknownDatasetId;
import org.deegree.services.oaf.resource.Features;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.deegree.services.oaf.workspace.configuration.FeatureTypeMetadata;
import org.deegree.services.oaf.workspace.configuration.FilterProperty;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.deegree.services.oaf.exceptions.ExceptionMediaTypeUtil.selectMediaType;

/**
 * Checks the passed query parameters. If an unsupported query parameter is detected the
 * requests aborts with a BAD_REQUEST.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Provider
public class UnknownParameterFilter implements ContainerRequestFilter {

	private static final String EXCEPTION_MSG = "Parameter with name '%s' is not specified.";

	@Context
	private ResourceInfo resourceInfo;

	@Context
	private HttpServletRequest servletRequest;

	@Context
	private Request request;

	@Context
	private UriInfo uriInfo;

	@Inject
	private DeegreeWorkspaceInitializer deegreeWorkspaceInitializer;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		Set<String> expectedParams = collectExpectedParamsFromAnnotations();
		for (String param : OverrideAcceptFilter.QUERY_PARAMS) {
			expectedParams.add(param);
		}
		addFilterParamsIfRequired(expectedParams);
		Set<String> requestParams = servletRequest.getParameterMap().keySet();
		requestParams.forEach(param -> {
			if (!expectedParams.contains(param)) {
				Response unknownParameterResponse = createUnknownParameterResponse(param);
				requestContext.abortWith(unknownParameterResponse);
			}
		});
	}

	private Response createUnknownParameterResponse(String param) {
		MediaType selectedType = selectMediaType(request);
		String message = String.format(EXCEPTION_MSG, param);
		OgcApiFeaturesExceptionReport oafExceptionReport = new OgcApiFeaturesExceptionReport(message,
				BAD_REQUEST.getStatusCode());
		return Response.status(BAD_REQUEST).entity(oafExceptionReport).type(selectedType).build();
	}

	private Set<String> collectExpectedParamsFromAnnotations() {
		Set<String> expectedParams = new HashSet<>();
		Method method = resourceInfo.getResourceMethod();
		for (Annotation[] annotations : method.getParameterAnnotations()) {
			for (Annotation annotation : annotations) {
				if (annotation instanceof QueryParam) {
					expectedParams.add(((QueryParam) annotation).value());
				}
			}
		}
		return expectedParams;
	}

	private void addFilterParamsIfRequired(Set<String> expectedParams) {
		if (resourceInfo.getResourceClass().isAssignableFrom(Features.class)) {
			MultivaluedMap<String, String> pathParameters = uriInfo.getPathParameters();
			String datasetId = pathParameters.get("datasetId").get(0);
			String collectionId = pathParameters.get("collectionId").get(0);

			try {
				OafDatasetConfiguration oafConfiguration = deegreeWorkspaceInitializer.getOafDatasets()
					.getDataset(datasetId);
				FeatureTypeMetadata featureTypeMetadata = oafConfiguration.getFeatureTypeMetadata(collectionId);
				if (featureTypeMetadata != null) {
					List<FilterProperty> filterProperties = featureTypeMetadata.getFilterProperties();
					filterProperties.forEach(filterProperty -> {
						String filterName = filterProperty.getName().getLocalPart();
						expectedParams.add(filterName);
					});
				}
			}
			catch (UnknownDatasetId | UnknownCollectionId e) {
				// will be handled later
			}

		}
	}

}
