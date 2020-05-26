package org.deegree.services.oaf.workspace;

import org.deegree.cs.exceptions.UnknownCRSException;
import org.deegree.cs.persistence.CRSManager;
import org.deegree.feature.persistence.FeatureStore;
import org.deegree.feature.persistence.FeatureStoreException;
import org.deegree.feature.persistence.query.Query;
import org.deegree.feature.stream.FeatureInputStream;
import org.deegree.feature.types.FeatureType;
import org.deegree.filter.FilterEvaluationException;
import org.deegree.services.oaf.domain.collections.Collection;
import org.deegree.services.oaf.domain.collections.Collections;
import org.deegree.services.oaf.exceptions.InternalQueryException;
import org.deegree.services.oaf.exceptions.InvalidConfigurationException;
import org.deegree.services.oaf.exceptions.InvalidParameterValue;
import org.deegree.services.oaf.exceptions.UnknownCollectionId;
import org.deegree.services.oaf.feature.FeatureResponse;
import org.deegree.services.oaf.feature.FeaturesRequest;
import org.deegree.services.oaf.link.Link;
import org.deegree.services.oaf.link.LinkBuilder;
import org.deegree.services.oaf.link.NextLink;
import org.deegree.services.oaf.workspace.configuration.FeatureTypeMetadata;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class DeegreeDataAccess implements DataAccess {

    @Override
    public Collections createCollections( OafDatasetConfiguration oafConfiguration, LinkBuilder linkBuilder ) {
        String datasetId = oafConfiguration.getId();
        List<Link> links = linkBuilder.createCollectionsLinks( datasetId, oafConfiguration.getServiceMetadata() );
        List<Collection> collections = createCollectionList( oafConfiguration, datasetId, linkBuilder );
        return new Collections( links, collections );
    }

    @Override
    public Collection createCollection( OafDatasetConfiguration oafConfiguration, String collectionId,
                                        LinkBuilder linkBuilder )
                    throws UnknownCollectionId {
        String datasetId = oafConfiguration.getId();
        Map<String, FeatureTypeMetadata> featureTypeNames = oafConfiguration.getFeatureTypeMetadata();
        if ( featureTypeNames.containsKey( collectionId ) ) {
            return createCollection( oafConfiguration, datasetId, featureTypeNames.get( collectionId ), linkBuilder );
        }
        throw new UnknownCollectionId( collectionId );
    }

    @Override
    public FeatureResponse retrieveFeatures( OafDatasetConfiguration oafConfiguration, String collectionId,
                                             FeaturesRequest featuresRequest,
                                             LinkBuilder linkBuilder )
                    throws UnknownCollectionId, InternalQueryException, InvalidParameterValue {
        FeatureTypeMetadata featureType = validateAndRetrieveFeatureType( oafConfiguration, collectionId );
        String crs = validateAndRetrieveCrs( featuresRequest.getResponseCrs() );
        FeatureStore featureStore = oafConfiguration.getFeatureStore( featureType.getName(), collectionId );
        try {
            DeegreeQueryBuilder queryBuilder = new DeegreeQueryBuilder( oafConfiguration );
            Query query = queryBuilder.createQuery( featureType, featuresRequest );
            int numberOfFeaturesMatched = featureStore.queryHits( query );
            FeatureInputStream features = featureStore.query( query );
            boolean isMaxFeaturesAndStartIndexApplicable = featureStore.isMaxFeaturesAndStartIndexApplicable(
                            new Query[] { query } );
            int limit = featuresRequest.getLimit();
            int offset = featuresRequest.getOffset();
            NextLink nextLink = new NextLink( numberOfFeaturesMatched, limit, offset );
            String datasetId = oafConfiguration.getId();
            List<Link> links = linkBuilder.createFeaturesLinks( datasetId, collectionId, nextLink );
            Map<String, String> featureTypeNsPrefixes = getFeatureTypeNsPrefixes( featureStore );
            return new FeatureResponse( features, featureTypeNsPrefixes, limit, numberOfFeaturesMatched, offset, links,
                                        isMaxFeaturesAndStartIndexApplicable, crs );
        } catch ( FeatureStoreException | FilterEvaluationException | InvalidConfigurationException e ) {
            throw new InternalQueryException( e );
        }
    }

    @Override
    public FeatureResponse retrieveFeature( OafDatasetConfiguration oafConfiguration, String collectionId,
                                            String featureId, String responseCrs,
                                            LinkBuilder linkBuilder )
                    throws InternalQueryException, InvalidParameterValue, UnknownCollectionId {
        FeatureTypeMetadata featureType = validateAndRetrieveFeatureType( oafConfiguration, collectionId );
        String crs = validateAndRetrieveCrs( responseCrs );
        FeatureStore featureStore = oafConfiguration.getFeatureStore( featureType.getName(), collectionId );
        try {
            DeegreeQueryBuilder queryBuilder = new DeegreeQueryBuilder( oafConfiguration );
            Query queryById = queryBuilder.createQueryById( featureType.getName(), featureId );
            FeatureInputStream feature = featureStore.query( queryById );
            String datasetId = oafConfiguration.getId();
            List<Link> inks = linkBuilder.createFeatureLinks( datasetId, collectionId, featureId );
            Map<String, String> featureTypeNsPrefixes = getFeatureTypeNsPrefixes( featureStore );
            return new FeatureResponse( feature, featureTypeNsPrefixes, 1, 1, 0, inks, true, crs );
        } catch ( FeatureStoreException | FilterEvaluationException e ) {
            throw new InternalQueryException( e );
        }
    }

    private FeatureTypeMetadata validateAndRetrieveFeatureType( OafDatasetConfiguration oafConfiguration,
                                                                String collectionId )
                    throws UnknownCollectionId {
        Map<String, FeatureTypeMetadata> featureTypeNames = oafConfiguration.getFeatureTypeMetadata();
        if ( !featureTypeNames.containsKey( collectionId ) )
            throw new UnknownCollectionId( collectionId );
        return featureTypeNames.get( collectionId );
    }

    private String validateAndRetrieveCrs( String crs )
                    throws InvalidParameterValue {
        if ( crs == null || crs.isEmpty() )
            return null;
        try {
            CRSManager.lookup( crs );
        } catch ( UnknownCRSException e ) {
            throw new InvalidParameterValue( "crs", "Unknown CRS " + crs );
        }
        return crs;
    }

    private List<Collection> createCollectionList( OafDatasetConfiguration oafConfiguration, String datasetId,
                                                   LinkBuilder linkBuilder ) {
        Map<String, FeatureTypeMetadata> featureTypeNames = oafConfiguration.getFeatureTypeMetadata();
        List<Collection> collectionList = new ArrayList<>( featureTypeNames.size() );
        for ( Map.Entry<String, FeatureTypeMetadata> entry : featureTypeNames.entrySet() ) {
            collectionList.add( createCollection( oafConfiguration, datasetId, entry.getValue(), linkBuilder ) );
        }
        return collectionList;
    }

    private Collection createCollection( OafDatasetConfiguration oafConfiguration, String datasetId,
                                         FeatureTypeMetadata featureType, LinkBuilder linkBuilder ) {
        // TODO: name must be unique!
        String featureTypeId = featureType.getName().getLocalPart();
        List<Link> links = linkBuilder.createCollectionLinks( datasetId, featureTypeId, featureType.getMetadataUrls() );
        String title = featureType.getTitle();
        String description = featureType.getDescription();
        List<String> suppportedCrs = oafConfiguration.getSuppportedCrs();
        return new Collection( featureTypeId, title, description, links, featureType.getExtent(), suppportedCrs );
    }

    private Map<String, String> getFeatureTypeNsPrefixes( FeatureStore featureStore ) {
        Map<String, String> prefixToNs = new HashMap<String, String>();
        FeatureType[] featureTypes = featureStore.getSchema().getFeatureTypes();
        for ( FeatureType ft : featureTypes ) {
            QName ftName = ft.getName();
            if ( ftName.getPrefix() != null ) {
                prefixToNs.put( ftName.getPrefix(), ftName.getNamespaceURI() );
            }
        }
        return prefixToNs;
    }

}