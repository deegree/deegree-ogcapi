package org.deegree.services.oaf.feature;

import org.deegree.cs.coordinatesystems.ICRS;
import org.deegree.feature.stream.FeatureInputStream;
import org.deegree.services.oaf.link.Link;

import java.util.List;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class FeatureResponse {

    private final FeatureInputStream features;

    private final int numberOfFeaturesMatched;

    private final int numberOfFeatures;

    private final int startIndex;

    private final List<Link> links;

    private final boolean isMaxFeaturesAndStartIndexApplicable;

    private final String responseCrsName;

    public FeatureResponse( FeatureInputStream features, int numberOfFeatures, int numberOfFeaturesMatched,
                            int startIndex, List<Link> links, boolean isMaxFeaturesAndStartIndexApplicable,
                            String responseCrsName ) {
        this.features = features;
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
}
