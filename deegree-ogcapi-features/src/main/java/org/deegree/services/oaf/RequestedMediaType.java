package org.deegree.services.oaf;

import java.util.Objects;

import org.deegree.services.oaf.exceptions.InvalidParameterValue;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public record RequestedMediaType(String format, RequestFormat defaultFormat, String requestedMediaType,
		String defaultMediaType) {

	public RequestedMediaType(String format, RequestFormat defaultFormat, String defaultMediaType) {
		this(format, defaultFormat, null, defaultMediaType);
	}

	/**
	 * @return the requested format, never <code>null</code>
	 * @throws InvalidParameterValue if the format value ist invalid
	 */
	public RequestFormat getRequestFormat() throws InvalidParameterValue {
		return RequestFormat.byFormatParameter(format, defaultFormat);
	}

	/**
	 * @return the requested media type (used in self links), never <code>null</code>
	 */
	public String getSelfMediaType() {
		if (Objects.nonNull(requestedMediaType) && !requestedMediaType.isEmpty()) {
			return requestedMediaType;
		}
		return defaultMediaType;
	}
}
