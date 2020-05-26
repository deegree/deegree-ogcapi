package org.deegree.services.oaf.workspace;

import org.deegree.services.oaf.domain.collections.Collection;
import org.deegree.services.oaf.domain.collections.Collections;
import org.deegree.services.oaf.exceptions.InternalQueryException;
import org.deegree.services.oaf.exceptions.InvalidParameterValue;
import org.deegree.services.oaf.exceptions.UnknownCollectionId;
import org.deegree.services.oaf.feature.FeatureResponse;
import org.deegree.services.oaf.feature.FeaturesRequest;
import org.deegree.services.oaf.link.LinkBuilder;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public interface DataAccess {

    Collections createCollections( OafDatasetConfiguration oafConfiguration, LinkBuilder linkBuilder );

    Collection createCollection( OafDatasetConfiguration oafConfiguration, String collectionId,
                                 LinkBuilder linkBuilder )
                    throws UnknownCollectionId;

    FeatureResponse retrieveFeatures( OafDatasetConfiguration oafConfiguration, String collectionId,
                                      FeaturesRequest featuresRequest,
                                      LinkBuilder linkBuilder )
                    throws InternalQueryException, InvalidParameterValue, UnknownCollectionId;

    FeatureResponse retrieveFeature( OafDatasetConfiguration oafConfiguration, String collectionId, String featureId,
                                     String crs,
                                     LinkBuilder linkBuilder )
                    throws InternalQueryException, InvalidParameterValue, UnknownCollectionId;
}