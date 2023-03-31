package org.deegree.services.oaf.filter;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.deegree.services.oaf.OgcApiFeaturesMediaType;

/**
 * Filter that allows the Accept HTTP header to be overridden/extended by providing a query parameter or a file extension.
 */
@Provider
@PreMatching
public class OverrideAcceptFilter implements ContainerRequestFilter {
	
	/**
	 * Name of query parameter that may specify a media type.
	 */
	public static final List<String> QUERY_PARAMS = Collections.unmodifiableList(Arrays.asList("accept", "f")) ;
	
	/**
	 * Map of supported extensions with their translation to a media type.
	 */
	private static final Map<String, String> ACCEPT_EXTENSIONS = Collections.unmodifiableMap(buildExtensionMap());
	
	private static Map<String, String> buildExtensionMap() {
		Map<String, String> result = new HashMap<>();
		
		result.put("json", MediaType.APPLICATION_JSON);
		result.put("yaml", OgcApiFeaturesMediaType.APPLICATION_YAML);
		result.put("xml", MediaType.APPLICATION_XML);
		result.put("html", MediaType.TEXT_HTML);
		
		return result;
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		// Priority 1: Check if query parameter overrides accepted format 
		List<String> overrideTypes = new ArrayList<>();
		for (String param : QUERY_PARAMS) {
			List<String> values = requestContext.getUriInfo().getQueryParameters().get(param);
			if (values != null) {
				overrideTypes.addAll(values);
			}
		}
		
		if (!overrideTypes.isEmpty()) {
			// allow using "extensions" instead of mime types
			overrideTypes = overrideTypes.stream().map(t -> {
				String mapped = ACCEPT_EXTENSIONS.get(t);
				if (mapped != null)
					return mapped;
				else
					return t;
			}).collect(Collectors.toList());
		}
		
		if (overrideTypes.isEmpty()) {
			// Priority 2: Check if extension overrides accepted format (only specific extensions supported)
			String path = requestContext.getUriInfo().getPath();
			
			Optional<String> overrideType = ACCEPT_EXTENSIONS.entrySet().stream()
					.filter(e -> path.endsWith("." + e.getKey()))
					.findAny()
					.map(e -> {
						stripRequestSuffix(requestContext, "." + e.getKey());
						
						return e.getValue();
					});
			
			if (overrideType.isPresent()) {
				overrideTypes = Collections.singletonList(overrideType.get());
			}
		}
		
		if (!overrideTypes.isEmpty()) {
			// if accepted type should be overridden prepend it to any existing accept header
			MultivaluedMap<String, String> headers = requestContext.getHeaders();
			List<String> newTypes = new ArrayList<>(overrideTypes);
			List<String> orgAccept = headers.get(HttpHeaders.ACCEPT);
			if (orgAccept != null) {
				newTypes.addAll(orgAccept);
			}
			
			// overwrite with combined header
			// the original header is included to gracefully handle unsupported values or cases where the query parameter has a different use
			headers.put(HttpHeaders.ACCEPT, Collections.singletonList(newTypes.stream().collect(Collectors.joining(", "))));
		}
	}

	/**
	 * Strip a suffix from the request URI.
	 * 
	 * @param requestContext the request context to modify
	 * @param suffix the suffix to remove from the request URI
	 */
	private void stripRequestSuffix(ContainerRequestContext requestContext, String suffix) {
		UriInfo org = requestContext.getUriInfo();
		
		String path = org.getRequestUri().getPath();
		if (path.endsWith(suffix)) {
			path = path.substring(0, path.length() - suffix.length());
		}
		
		URI stripped = org.getRequestUriBuilder().replacePath(path).build();
		
		requestContext.setRequestUri(org.getBaseUri(), stripped);
	}

}
