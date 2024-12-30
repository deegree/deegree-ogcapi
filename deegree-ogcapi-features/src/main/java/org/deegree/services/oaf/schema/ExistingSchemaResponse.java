package org.deegree.services.oaf.schema;

import org.deegree.feature.types.FeatureType;
import org.deegree.gml.schema.GMLSchemaInfoSet;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class ExistingSchemaResponse extends SchemaResponse {

	private final GMLSchemaInfoSet gmlSchema;

	public ExistingSchemaResponse(FeatureType featureType, GMLSchemaInfoSet gmlSchema) {
		super(featureType);
		this.gmlSchema = gmlSchema;
	}

	public GMLSchemaInfoSet getGmlSchema() {
		return gmlSchema;
	}

}
