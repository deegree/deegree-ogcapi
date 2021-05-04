package org.deegree.services.oaf.schema;

import org.deegree.feature.types.FeatureType;

import java.util.Map;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class SchemaResponse {

    private final FeatureType featureType;

    public SchemaResponse( FeatureType featureType ) {
        this.featureType = featureType;
    }

    public FeatureType getFeatureType() {
        return featureType;
    }

}
