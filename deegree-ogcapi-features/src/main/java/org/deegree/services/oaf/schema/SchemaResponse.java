package org.deegree.services.oaf.schema;

import org.deegree.feature.types.FeatureType;

import java.util.Map;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class SchemaResponse {

    private final FeatureType featureType;

    private final Map<String, String> nsToSchemaLocation;

    private final Map<String, String> prefixToNs;

    public SchemaResponse( FeatureType featureType, Map<String, String> nsToSchemaLocation,
                           Map<String, String> prefixToNs ) {
        this.featureType = featureType;
        this.nsToSchemaLocation = nsToSchemaLocation;
        this.prefixToNs = prefixToNs;
    }

    public FeatureType getFeatureType() {
        return featureType;
    }

    public Map<String, String> getNsToSchemaLocation() {
        return nsToSchemaLocation;
    }

    public Map<String, String> getPrefixToNs() {
        return prefixToNs;
    }
}
