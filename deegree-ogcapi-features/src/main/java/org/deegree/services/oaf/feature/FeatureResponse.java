package org.deegree.services.oaf.feature;

import org.deegree.feature.stream.FeatureInputStream;
import org.deegree.services.oaf.link.Link;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class FeatureResponse {

    private final FeatureInputStream features;

    private final int numberOfFeaturesMatched;

    private final Map<String, String> featureTypeNsPrefixes;

    private final int numberOfFeatures;

    private final int startIndex;

    private final List<Link> links;

    private final boolean isMaxFeaturesAndStartIndexApplicable;

    private final String responseCrsName;

    public FeatureResponse( FeatureInputStream features,
                            Map<String, String> featureTypeNsPrefixes,
                            int numberOfFeatures, int numberOfFeaturesMatched,
                            int startIndex, List<Link> links, boolean isMaxFeaturesAndStartIndexApplicable,
                            String responseCrsName ) {
        this.features = features;
        this.featureTypeNsPrefixes = featureTypeNsPrefixes;
        this.numberOfFeatures = numberOfFeatures;
        this.numberOfFeaturesMatched = numberOfFeaturesMatched;
        this.startIndex = startIndex;
        this.links = links;
        this.isMaxFeaturesAndStartIndexApplicable = isMaxFeaturesAndStartIndexApplicable;
        this.responseCrsName = responseCrsName;
    }

    public FeatureInputStream getFeatures() {
        return features;
    }

    public int getNumberOfFeatures() {
        return numberOfFeatures;
    }

    public int getNumberOfFeaturesMatched() {
        return numberOfFeaturesMatched;
    }

    public List<Link> getLinks() {
        return links;
    }

    public boolean isMaxFeaturesAndStartIndexApplicable() {
        return isMaxFeaturesAndStartIndexApplicable;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public String getResponseCrsName() {
        return responseCrsName;
    }

    public Map<String, String> getFeatureTypeNsPrefixes() {
        if ( featureTypeNsPrefixes == null )
            return Collections.emptyMap();
        return featureTypeNsPrefixes;
    }
}
