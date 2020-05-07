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

    private final Map<String, FeatureTypeMetadata> featureTypeMetadata;

    private final DatasetMetadata serviceMetadata;

    private final List<String> suppportedCrs;

    private Map<QName, FeatureStore> featureStores;

    public OafDatasetConfiguration( Map<String, FeatureTypeMetadata> featureTypeMetadata,
                                    DatasetMetadata serviceMetadata, List<String> suppportedCrs,
                                    Map<QName, FeatureStore> featureStores ) {
        this.featureTypeMetadata = featureTypeMetadata;
        this.serviceMetadata = serviceMetadata;
        this.suppportedCrs = suppportedCrs;
        this.featureStores = featureStores;
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
     */
    public FeatureTypeMetadata getFeatureTypeMetadata( String collectionId ) {
        return featureTypeMetadata.get( collectionId );
    }

    /**
     * @return the name of the CRS supported by this OAF dataset, may be <code>null</code>
     */
    public List<String> getSuppportedCrs() {
        return suppportedCrs;
    }

    /**
     * @param featureStoreName
     *                 name of the feature type (OAF collection), must not be <code>null</code>
     * @param collectionId
     *                 the id of the collection, may be <code>null</code>
     * @return the {@link FeatureStore} providing the feature type (OAF collection) with the passed name, never <code>null</code>
     * @throws UnknownCollectionId
     *                 if not feature store with the passed name could be found
     */
    public FeatureStore getFeatureStore( QName featureStoreName, String collectionId )
                    throws UnknownCollectionId {
        if ( featureStores.containsKey( featureStoreName ) ) {
            return featureStores.get( featureStoreName );
        }
        throw new UnknownCollectionId( collectionId );
    }

    /**
     * @return metadata of this dataset, never <code>null</code>
     */
    public DatasetMetadata getServiceMetadata() {
        return serviceMetadata;
    }
}