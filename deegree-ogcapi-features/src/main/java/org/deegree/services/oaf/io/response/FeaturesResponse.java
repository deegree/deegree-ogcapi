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
package org.deegree.services.oaf.io.response;

import org.deegree.feature.stream.FeatureInputStream;
import org.deegree.services.oaf.io.SchemaLocation;
import org.deegree.services.oaf.link.Link;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class FeaturesResponse extends AbstractFeatureResponse {

    private final FeatureInputStream features;

    private final int numberOfFeaturesMatched;

    private final int numberOfFeatures;

    private final int startIndex;

    private final boolean isMaxFeaturesAndStartIndexApplicable;

    FeaturesResponse( FeatureInputStream features,
                      Map<String, String> featureTypeNsPrefixes,
                      int numberOfFeatures, int numberOfFeaturesMatched,
                      int startIndex, List<Link> links, boolean isMaxFeaturesAndStartIndexApplicable,
                      String responseCrsName, SchemaLocation schemaLocation ) {
        super( featureTypeNsPrefixes, responseCrsName, schemaLocation, links );
        this.features = features;
        this.numberOfFeatures = numberOfFeatures;
        this.numberOfFeaturesMatched = numberOfFeaturesMatched;
        this.startIndex = startIndex;
        this.isMaxFeaturesAndStartIndexApplicable = isMaxFeaturesAndStartIndexApplicable;
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

    public boolean isMaxFeaturesAndStartIndexApplicable() {
        return isMaxFeaturesAndStartIndexApplicable;
    }

    public int getStartIndex() {
        return startIndex;
    }

}
