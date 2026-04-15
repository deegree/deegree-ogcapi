/*-
 * #%L
 * deegree-ogcapi-features - OGC API Features (OAF) implementation - Querying and modifying of geospatial data objects
 * %%
 * Copyright (C) 2019 - 2026 lat/lon GmbH, info@lat-lon.de, www.lat-lon.de
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
package org.deegree.services.oaf.link;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class UriInfoWithHeaderFromRequest implements UriInfo {

	private final UriInfo uriInfo;

	private final String contextPathFromServletContextWithoutLeadingSlash;

	private final String proto;

	private final String host;

	private final String port;

	private final String pathPrefix;

	public UriInfoWithHeaderFromRequest(UriInfo uriInfo, HttpServletRequest request) {
		this.uriInfo = uriInfo;
		this.contextPathFromServletContextWithoutLeadingSlash = parseContextPathFromServletContextWithoutLeadingSlash(
				request);

		String xForwardedProto = request != null ? request.getHeader("X-Forwarded-Proto") : null;
		String xForwardedPort = request != null ? request.getHeader("X-Forwarded-Port") : null;
		String xForwardedHost = request != null ? request.getHeader("X-Forwarded-Host") : null;
		String xForwardedPrefix = request != null ? request.getHeader("X-Forwarded-Prefix") : null;

		this.proto = parseProtocol(xForwardedProto);
		this.host = parseHost(xForwardedHost);
		this.port = parsePort(xForwardedPort, xForwardedHost);
		this.pathPrefix = parsePrefix(xForwardedPrefix, request != null ? request.getContextPath() : null);
	}

	@Override
	public String getPath() {
		return "/" + overwritePathWithContextPathFromHeader(uriInfo.getPathSegments()).stream()
			.map(PathSegment::getPath)
			.collect(Collectors.joining("/"));
	}

	@Override
	public String getPath(boolean b) {
		return "/" + overwritePathWithContextPathFromHeader(uriInfo.getPathSegments(b)).stream()
			.map(PathSegment::getPath)
			.collect(Collectors.joining("/"));
	}

	@Override
	public List<PathSegment> getPathSegments() {
		return overwritePathWithContextPathFromHeader(uriInfo.getPathSegments());
	}

	@Override
	public List<PathSegment> getPathSegments(boolean b) {
		return overwritePathWithContextPathFromHeader(uriInfo.getPathSegments(b));
	}

	@Override
	public URI getRequestUri() {
		return uriInfo.getRequestUri();
	}

	@Override
	public UriBuilder getRequestUriBuilder() {
		UriBuilder requestUriBuilder = overwriteWithHeaderValues(uriInfo.getRequestUriBuilder());
		requestUriBuilder.replacePath(pathPrefix).path(getPath());
		return requestUriBuilder;
	}

	@Override
	public URI getAbsolutePath() {
		return uriInfo.getAbsolutePath();
	}

	@Override
	public UriBuilder getAbsolutePathBuilder() {
		return uriInfo.getAbsolutePathBuilder();
	}

	@Override
	public URI getBaseUri() {
		return uriInfo.getBaseUri();
	}

	@Override
	public UriBuilder getBaseUriBuilder() {
		UriBuilder baseUriBuilder = overwriteWithHeaderValues(uriInfo.getBaseUriBuilder());
		baseUriBuilder.replacePath(pathPrefix);
		return baseUriBuilder;
	}

	@Override
	public MultivaluedMap<String, String> getPathParameters() {
		return uriInfo.getPathParameters();
	}

	@Override
	public MultivaluedMap<String, String> getPathParameters(boolean b) {
		return uriInfo.getPathParameters(b);
	}

	@Override
	public MultivaluedMap<String, String> getQueryParameters() {
		return uriInfo.getQueryParameters();
	}

	@Override
	public MultivaluedMap<String, String> getQueryParameters(boolean b) {
		return uriInfo.getQueryParameters(b);
	}

	@Override
	public List<String> getMatchedURIs() {
		return uriInfo.getMatchedURIs();
	}

	@Override
	public List<String> getMatchedURIs(boolean b) {
		return uriInfo.getMatchedURIs(b);
	}

	@Override
	public List<Object> getMatchedResources() {
		return uriInfo.getMatchedResources();
	}

	@Override
	public URI resolve(URI uri) {
		return uriInfo.resolve(uri);
	}

	@Override
	public URI relativize(URI uri) {
		return uriInfo.relativize(uri);
	}

	private UriBuilder overwriteWithHeaderValues(UriBuilder baseUriBuilder) {
		if (proto != null && !proto.isBlank())
			baseUriBuilder.scheme(proto);
		if (host != null && !host.isBlank())
			baseUriBuilder.host(host);
		if (port != null && !port.isBlank())
			baseUriBuilder.port(Integer.parseInt(port));

		return baseUriBuilder;
	}

	private List<PathSegment> overwritePathWithContextPathFromHeader(List<PathSegment> pathSegments) {
		if (pathPrefix == null)
			return pathSegments;
		if (pathPrefix.isEmpty() && !pathSegments.isEmpty()
				&& pathSegments.get(0).getPath().equals(contextPathFromServletContextWithoutLeadingSlash))
			return pathSegments.stream().skip(1).toList();
		if (!pathPrefix.isEmpty()) {
			if (!pathSegments.isEmpty()
					&& pathSegments.get(0).getPath().equals(contextPathFromServletContextWithoutLeadingSlash)) {
				return pathSegments.stream().skip(1).collect(Collectors.toList());
			}
			else {
				return new ArrayList<>(pathSegments);
			}
		}
		return pathSegments;
	}

	private static String parseContextPathFromServletContextWithoutLeadingSlash(HttpServletRequest request) {
		if (request == null || request.getContextPath() == null)
			return null;
		return request.getContextPath().startsWith("/") ? request.getContextPath().substring(1)
				: request.getContextPath();
	}

	private String parseProtocol(String xForwardedProto) {
		if (xForwardedProto != null && !xForwardedProto.isEmpty())
			return xForwardedProto;
		return null;
	}

	private String parseHost(String xForwardedHost) {
		if (xForwardedHost != null && xForwardedHost.contains(":"))
			return xForwardedHost.substring(0, xForwardedHost.indexOf(":"));
		return xForwardedHost;
	}

	private String parsePort(String xForwardedPort, String xForwardedHost) {
		if (xForwardedPort != null && !xForwardedPort.isEmpty())
			return xForwardedPort;
		else if (xForwardedHost != null && xForwardedHost.contains(":")
				&& (xForwardedHost.lastIndexOf(":") + 1) < xForwardedHost.length())
			return xForwardedHost.substring(xForwardedHost.lastIndexOf(":") + 1);
		return null;
	}

	private String parsePrefix(String xForwardedPrefix, String contextPath) {
		if (xForwardedPrefix == null)
			return contextPath;
		return xForwardedPrefix;
	}

}
