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

import org.deegree.feature.Feature;
import org.deegree.feature.stream.FeatureInputStream;
import org.deegree.services.oaf.io.SchemaLocation;
import org.deegree.services.oaf.link.Link;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class FeaturesResponseBuilder {

    private FeatureInputStream features;

    private Feature feature;

    private int numberOfFeaturesMatched;

    private Map<String, String> featureTypeNsPrefixes;

    private int numberOfFeatures;

    private int startIndex;

    private List<Link> links;

    private boolean isMaxFeaturesAndStartIndexApplicable;

    private String responseCrsName;

    private String namespaceURI;

    private SchemaLocation schemaLocation;

    private String featureId;

    public FeaturesResponseBuilder( FeatureInputStream features ) {
        this.features = features;
    }

    public FeaturesResponseBuilder( Feature feature ) {
        this.feature = feature;
    }

    public FeaturesResponseBuilder withFeatureTypeNsPrefixes( Map<String, String> featureTypeNsPrefixes ) {
        this.featureTypeNsPrefixes = featureTypeNsPrefixes;
        return this;
    }

    public FeaturesResponseBuilder withNumberOfFeatures( int numberOfFeatures ) {
        this.numberOfFeatures = numberOfFeatures;
        return this;
    }

    public FeaturesResponseBuilder withNumberOfFeaturesMatched( int numberOfFeaturesMatched ) {
        this.numberOfFeaturesMatched = numberOfFeaturesMatched;
        return this;
    }

    public FeaturesResponseBuilder withStartIndex( int startIndex ) {
        this.startIndex = startIndex;
        return this;
    }

    public FeaturesResponseBuilder withLinks( List<Link> links ) {
        this.links = links;
        return this;
    }

    public FeaturesResponseBuilder withMaxFeaturesAndStartIndexApplicable(
                    boolean maxFeaturesAndStartIndexApplicable ) {
        isMaxFeaturesAndStartIndexApplicable = maxFeaturesAndStartIndexApplicable;
        return this;
    }

    public FeaturesResponseBuilder withResponseCrsName( String responseCrsName ) {
        this.responseCrsName = responseCrsName;
        return this;
    }

    public FeaturesResponseBuilder withSchemaLocation( String namespaceURI, String schemaLocation ) {
        if ( namespaceURI == null || schemaLocation == null )
            throw new IllegalArgumentException( "namespacesURI and schemaLocation must be set" );
        this.schemaLocation = new SchemaLocation( namespaceURI, schemaLocation );
        return this;
    }

    public FeaturesResponseBuilder withFeatureId( String featureId ) {
        this.featureId = featureId;
        return this;
    }

    public FeaturesResponse buildFeaturesResponse() {
        return new FeaturesResponse( features, featureTypeNsPrefixes, numberOfFeatures, numberOfFeaturesMatched,
                                     startIndex, links,
                                     isMaxFeaturesAndStartIndexApplicable, responseCrsName, schemaLocation );
    }

    public FeatureResponse buildFeatureResponse() {
        return new FeatureResponse( feature, featureTypeNsPrefixes, links,
                                    responseCrsName, schemaLocation );
    }
}
