package org.deegree.services.oaf.io.response;

import javax.xml.namespace.QName;

import org.deegree.services.oaf.io.SchemaLocation;
import org.deegree.services.oaf.link.Link;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public abstract class AbstractFeatureResponse {

	private final Map<String, String> featureTypeNsPrefixes;

	private final QName geometryProperty;

	private final boolean skipGeometryExportAsWkt;

	private final String responseCrsName;

	private final SchemaLocation schemaLocation;

	private final List<Link> links;

	AbstractFeatureResponse(Map<String, String> featureTypeNsPrefixes, QName geometryProperty,
			boolean skipGeometryExportAsWkt, String responseCrsName, SchemaLocation schemaLocation, List<Link> links) {
		this.featureTypeNsPrefixes = featureTypeNsPrefixes;
		this.geometryProperty = geometryProperty;
		this.skipGeometryExportAsWkt = skipGeometryExportAsWkt;
		this.responseCrsName = responseCrsName;
		this.schemaLocation = schemaLocation;
		this.links = links;

	}

	public String getResponseCrsName() {
		return responseCrsName;
	}

	public Map<String, String> getFeatureTypeNsPrefixes() {
		if (featureTypeNsPrefixes == null)
			return Collections.emptyMap();
		return featureTypeNsPrefixes;
	}

	public QName getGeometryProperty() {
		return geometryProperty;
	}

	public boolean isSkipGeometryExportAsWkt() {
		return skipGeometryExportAsWkt;
	}

	public SchemaLocation getSchemaLocation() {
		return schemaLocation;
	}

	public List<Link> getLinks() {
		return links;
	}

}
