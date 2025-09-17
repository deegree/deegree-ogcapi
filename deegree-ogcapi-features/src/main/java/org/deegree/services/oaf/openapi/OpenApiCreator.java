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
package org.deegree.services.oaf.openapi;

import io.swagger.v3.core.filter.SpecFilter;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.jaxrs2.integration.ServletConfigContextUtils;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.deegree.services.oaf.exceptions.UnknownDatasetId;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.deegree.services.oaf.workspace.configuration.DatasetMetadata;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;
import org.slf4j.Logger;

import jakarta.inject.Inject;
import jakarta.servlet.ServletConfig;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class OpenApiCreator {

	private static final Logger LOG = getLogger(OpenApiCreator.class);

	private static final String VERSION = "1.0";

	@Context
	private ServletConfig servletConfig;

	@Context
	private UriInfo uriInfo;

	@Context
	private Application app;

	@Inject
	private DeegreeWorkspaceInitializer deegreeWorkspaceInitializer;

	public OpenAPI createOpenApi(HttpHeaders headers, UriInfo uriInfo, String datasetId) throws Exception {
		OpenAPI oas = createOpenApiDocument(datasetId);
		SwaggerConfiguration oasConfig = createSwaggerConfiguration(oas);

		String ctxId = ServletConfigContextUtils.getContextIdFromServletConfig(servletConfig);
		OpenApiContext ctx = (new JaxrsOpenApiContextBuilder()).servletConfig(servletConfig)
			.application(app)
			// .resourcePackages( this.resourcePackages )
			// .configLocation( this.configLocation )
			.openApiConfiguration(oasConfig)
			.ctxId(ctxId)
			.buildContext(true);
		OpenAPI oas2 = ctx.read();
		// Set info again, otherwise same info values are used in multiple datasets
		// (OAF-270)
		oas2.info(oas.getInfo());

		if (oas2 != null && ctx.getOpenApiConfiguration() != null
				&& ctx.getOpenApiConfiguration().getFilterClass() != null) {
			try {
				OafOpenApiFilter filter = new OafOpenApiFilter(uriInfo, datasetId, deegreeWorkspaceInitializer);
				SpecFilter f = new SpecFilter();
				oas2 = f.filter(oas2, filter, getQueryParams(this.uriInfo.getQueryParameters()), getCookies(headers),
						getHeaders(headers));
			}
			catch (Exception e) {
				LOG.error("failed to load filter", e);
			}
		}

		return oas2;
	}

	private SwaggerConfiguration createSwaggerConfiguration(OpenAPI oas) {
		SwaggerConfiguration oasConfig = new SwaggerConfiguration().openAPI(oas)
			.cacheTTL(0l)
			.prettyPrint(true)
			.filterClass(OafOpenApiFilter.class.getCanonicalName())
			.resourcePackages(Stream.of("org.deegree.services.oaf.resource").collect(Collectors.toSet()));
		return oasConfig;
	}

	private static Map<String, List<String>> getQueryParams(MultivaluedMap<String, String> params) {
		Map<String, List<String>> output = new HashMap();
		if (params != null) {
			params.entrySet().stream().forEach(entry -> {
				output.put(entry.getKey(), entry.getValue());
			});
		}
		return output;
	}

	private static Map<String, String> getCookies(HttpHeaders headers) {
		Map<String, String> output = new HashMap();
		if (headers != null) {
			headers.getCookies().entrySet().stream().forEach(entry -> {
				output.put(entry.getKey(), entry.getValue().getValue());
			});
		}
		return output;
	}

	private static Map<String, List<String>> getHeaders(HttpHeaders headers) {
		Map<String, List<String>> output = new HashMap();
		if (headers != null) {
			headers.getRequestHeaders().entrySet().stream().forEach(entry -> {
				output.put(entry.getKey(), entry.getValue());
			});
		}
		return output;
	}

	private OpenAPI createOpenApiDocument(String datasetId) throws UnknownDatasetId {
		OafDatasetConfiguration oafConfiguration = deegreeWorkspaceInitializer.getOafDatasets().getDataset(datasetId);
		DatasetMetadata metadata = oafConfiguration.getServiceMetadata();
		Info info = createInfo(metadata);
		OpenAPI oas = new OpenAPI();
		oas.info(info);
		return oas;
	}

	private Info createInfo(DatasetMetadata metadata) {
		String title = metadata.getTitle();
		String description = metadata.getDescription();
		Contact contact = createContact(metadata);
		License license = createLicense(metadata);
		return new Info().title(title).description(description).version(VERSION).contact(contact).license(license);
	}

	private Contact createContact(DatasetMetadata metadata) {
		org.deegree.services.oaf.domain.landingpage.Contact providerContact = metadata.getProviderContact();
		if (providerContact != null) {
			String contactName = providerContact.getName();
			String contactUrl = providerContact.getUrl();
			String email = providerContact.getEmail();
			return new Contact().name(contactName).url(contactUrl).email(email);
		}
		return null;
	}

	private License createLicense(DatasetMetadata metadata) {
		org.deegree.services.oaf.domain.License providerLicense = metadata.getProviderLicense();
		if (providerLicense != null) {
			String url = providerLicense.getUrl();
			if (url == null)
				url = uriInfo.getBaseUriBuilder()
					.path("datasets")
					.path(uriInfo.getPathParameters().get("datasetId").get(0))
					.path("license")
					.path("provider")
					.toString();
			return new License().name(providerLicense.getName()).url(url);
		}
		return null;
	}

}
