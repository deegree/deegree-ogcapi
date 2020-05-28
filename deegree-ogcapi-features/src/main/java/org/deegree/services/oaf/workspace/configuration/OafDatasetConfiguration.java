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

    private Map<QName, FeatureStore> featureStores;

    public OafDatasetConfiguration( String id, Map<String, FeatureTypeMetadata> featureTypeMetadata,
                                    DatasetMetadata serviceMetadata, List<String> suppportedCrs,
                                    Map<QName, FeatureStore> featureStores ) {
        this.id = id;
        this.featureTypeMetadata = featureTypeMetadata;
        this.serviceMetadata = serviceMetadata;
        this.suppportedCrs = suppportedCrs;
        this.featureStores = featureStores;
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
}