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
package org.deegree.services.oaf.workspace.configuration;

import org.deegree.feature.persistence.FeatureStore;
import org.deegree.services.oaf.exceptions.UnknownCollectionId;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;

/**
 * The configuration of a single Datataset.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class OafDatasetConfiguration {

    private final String id;

    private final Map<String, FeatureTypeMetadata> featureTypeMetadata;

    private final DatasetMetadata serviceMetadata;

    private final List<String> suppportedCrs;

    private final boolean useExistingGMLSchema;

    public OafDatasetConfiguration(String id, Map<String, FeatureTypeMetadata> featureTypeMetadata,
                                   DatasetMetadata serviceMetadata, List<String> suppportedCrs,
                                   boolean useExistingGMLSchema) {
        this.id = id;
        this.featureTypeMetadata = featureTypeMetadata;
        this.serviceMetadata = serviceMetadata;
        this.suppportedCrs = suppportedCrs;
        this.useExistingGMLSchema = useExistingGMLSchema;
    }

    /**
     * @return the id of this dataset, never <code>null</code>
     */
    public String getId() {
        return id;
    }

    /**
     * @return the {@link FeatureTypeMetadata} off all feature types (OAF collections) provided by this OAF  dataset.
     */
    public Map<String, FeatureTypeMetadata> getFeatureTypeMetadata() {
        return featureTypeMetadata;
    }

    /**
     * @param collectionId
     *                 must not be <code>null</code>
     * @return the {@link FeatureTypeMetadata} of the feature type (OAF collection) with the passed name, may be <code>null</code>
     * @throws UnknownCollectionId
     *                 if the collection is not available
     */
    public FeatureTypeMetadata getFeatureTypeMetadata( String collectionId )
                    throws UnknownCollectionId {
        checkCollection( collectionId );
        return featureTypeMetadata.get( collectionId );
    }

    /**
     * @return the name of the CRS supported by this OAF dataset, may be <code>null</code>
     */
    public List<String> getSuppportedCrs() {
        return suppportedCrs;
    }

    /**
     * @return metadata of this dataset, never <code>null</code>
     */
    public DatasetMetadata getServiceMetadata() {
        return serviceMetadata;
    }

    /**
     * Checks if a collection with the passed collection id is available by this configuration.
     *
     * @param collectionId
     *                 the id of the collection
     * @throws UnknownCollectionId
     *                 if the collection is not available
     */
    public void checkCollection( String collectionId )
                    throws UnknownCollectionId {
        if ( !featureTypeMetadata.containsKey( collectionId ) ) {
            throw new UnknownCollectionId( collectionId );
        }
    }

    /**
     * @return <code>true</code> if the existing GML applicationschema should be returned (if exists), <code>false</code> if the schema should be (re)generated.
     */
    public boolean isUseExistingGMLSchema() {
        return useExistingGMLSchema;
    }

    @Override
    public String toString() {
        return "OafDatasetConfiguration{" +
                "id='" + id + '\'' +
                ", featureTypeMetadata=" + featureTypeMetadata +
                ", serviceMetadata=" + serviceMetadata +
                ", suppportedCrs=" + suppportedCrs +
                ", useExistingGMLSchema=" + useExistingGMLSchema +
                '}';
    }
}
