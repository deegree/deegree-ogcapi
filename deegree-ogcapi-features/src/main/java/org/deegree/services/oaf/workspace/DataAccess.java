package org.deegree.services.oaf.workspace;

import org.deegree.services.oaf.domain.collections.Collection;
import org.deegree.services.oaf.domain.collections.Collections;
import org.deegree.services.oaf.exceptions.InternalQueryException;
import org.deegree.services.oaf.exceptions.InvalidParameterValue;
import org.deegree.services.oaf.exceptions.UnknownDatasetId;
import org.deegree.services.oaf.exceptions.UnknownCollectionId;
import org.deegree.services.oaf.feature.FeatureResponse;
import org.deegree.services.oaf.feature.FeaturesRequest;
import org.deegree.services.oaf.link.LinkBuilder;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public interface DataAccess {

    Collections createCollections( String datasetId, LinkBuilder linkBuilder )
                    throws UnknownDatasetId;

    Collection createCollection( String datasetId, String collectionId, LinkBuilder linkBuilder )
                    throws UnknownCollectionId, UnknownDatasetId;

    FeatureResponse retrieveFeatures( String datasetId, String collectionId, FeaturesRequest featuresRequest,
                                      LinkBuilder linkBuilder )
                    throws UnknownCollectionId, InternalQueryException, InvalidParameterValue, UnknownDatasetId;

    FeatureResponse retrieveFeature( String datasetId, String collectionId, String featureId, String crs,
                                     LinkBuilder linkBuilder )
                    throws UnknownCollectionId, InternalQueryException, InvalidParameterValue, UnknownDatasetId;
}