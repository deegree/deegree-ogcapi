/*-
 * #%L
 * deegree-ogcapi-config - OGC API Config implementation
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
package org.deegree.ogcapi.config.actions;

import org.deegree.commons.config.DeegreeWorkspace;
import org.deegree.commons.config.ResourceInitException;
import org.deegree.commons.utils.Pair;
import org.deegree.commons.utils.kvp.KVPUtils;
import org.deegree.feature.persistence.FeatureStore;
import org.deegree.feature.persistence.FeatureStoreException;
import org.deegree.feature.persistence.FeatureStoreProvider;
import org.deegree.feature.types.FeatureType;
import org.deegree.ogcapi.config.exceptions.BboxCacheUpdateException;
import org.deegree.workspace.ResourceIdentifier;
import org.deegree.workspace.Workspace;
import org.slf4j.Logger;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Action to update the bboxes of feature stores.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class UpdateBboxCache {

    private static final Logger LOG = getLogger( UpdateBboxCache.class );

    public static final String FEATURESTOREID = "FEATURESTOREID";

    /**
     * Updates the bounding boxes of all feature types in all feature stores.
     *
     * @param p
     *                 identifying the resource to validate, never <code>null</code>
     * @param queryString
     * @return
     * @throws IOException
     *                 if the OutputStream of the response could not be requested
     */
    public static String updateBboxCache( Pair<DeegreeWorkspace, String> p, String queryString )
                    throws BboxCacheUpdateException {
        DeegreeWorkspace ws = p.first;
        try {
            ws.initAll();
        } catch ( ResourceInitException e ) {
            throw new BboxCacheUpdateException( e );
        }
        try {
            List<String> featureStoreIds = parseFeatureStoreIds( queryString );
            return updateBboxCache( ws.getNewWorkspace(), featureStoreIds );
        } catch ( Exception e ) {
            throw new BboxCacheUpdateException( e );
        }
    }

    private static String updateBboxCache( Workspace workspace, List<String> featureStoreIds ) {
        List<String> featureStoreIdsToUpdate = findFeatureStoreIdsToUpdate( featureStoreIds, workspace );
        UpdateLog updateLog = new UpdateLog();
        for ( String featureStoreId : featureStoreIdsToUpdate ) {
            updateCacheOfFeatureStore( workspace, featureStoreId, updateLog );
        }
        return updateLog.logResult();
    }

    private static void updateCacheOfFeatureStore( Workspace workspace, String featureStoreId, UpdateLog updateLog ) {
        FeatureStore featureStore = workspace.getResource( FeatureStoreProvider.class, featureStoreId );
        if ( featureStore == null )
            throw new IllegalArgumentException( "FeatureStore with ID " + featureStoreId + " does not exist" );
        FeatureType[] featureTypes = featureStore.getSchema().getFeatureTypes();
        for ( FeatureType featureType : featureTypes ) {
            QName featureTypeName = featureType.getName();
            try {
                featureStore.calcEnvelope( featureTypeName );
                updateLog.addSucceed( featureStoreId, featureTypeName );
            } catch ( FeatureStoreException e ) {
                updateLog.addFailed( featureStoreId, featureTypeName );
                LOG.debug( "Update of FeatureType " + featureTypeName + ", from FeatureStore with ID " + featureStoreId
                           + " failed", e );
            }
        }
    }

    private static List<String> findFeatureStoreIdsToUpdate( List<String> featureStoreIds, Workspace workspace ) {
        if ( !featureStoreIds.isEmpty() )
            return featureStoreIds;
        List<String> allFeatureStoreIds = new ArrayList<>();
        for ( ResourceIdentifier<FeatureStore> resourceIdentifier : workspace.getResourcesOfType(
                        FeatureStoreProvider.class ) ) {
            String featureStoreId = resourceIdentifier.getId();
            allFeatureStoreIds.add( featureStoreId );
        }
        return allFeatureStoreIds;
    }

    private static List<String> parseFeatureStoreIds( String queryString )
                    throws UnsupportedEncodingException {
        if ( queryString == null )
            return Collections.emptyList();
        Map<String, String> normalizedKVPMap = KVPUtils.getNormalizedKVPMap( queryString, null );
        String featureStoreId = normalizedKVPMap.get( FEATURESTOREID );
        if ( featureStoreId == null )
            return Collections.emptyList();
        return Arrays.asList( KVPUtils.splitList( featureStoreId ) );
    }

    static class UpdateLog {

        Map<String, FailedAndSucceed> resultsPerFeatureStore = new HashMap<>();

        void addFailed( String featureStore, QName featureType ) {
            if ( !resultsPerFeatureStore.containsKey( featureStore ) )
                resultsPerFeatureStore.put( featureStore, new FailedAndSucceed() );
            resultsPerFeatureStore.get( featureStore ).addFailed( featureType );
        }

        void addSucceed( String featureStore, QName featureType ) {
            if ( !resultsPerFeatureStore.containsKey( featureStore ) )
                resultsPerFeatureStore.put( featureStore, new FailedAndSucceed() );
            resultsPerFeatureStore.get( featureStore ).addSucceed( featureType );
        }

        private String logResult() {
            StringBuilder sb = new StringBuilder();
            sb.append( "Update of bbox cache finished: \n\n" );
            for ( Map.Entry<String, FailedAndSucceed> resultPerFeatureStore : resultsPerFeatureStore.entrySet() ) {
                sb.append( "FeatureStoreId: " ).append( resultPerFeatureStore.getKey() ).append( "\n" );
                List<QName> succeed = resultPerFeatureStore.getValue().succeed;
                sb.append( "  *  " ).append( succeed.size() ).append( " feature types successful succeed: \n" );
                for ( QName featureType : succeed )
                    sb.append( "    -  " ).append( featureType ).append( "\n" );
                List<QName> failed = resultPerFeatureStore.getValue().failed;
                sb.append( "  *  " ).append( failed.size() ).append( " feature types failed: \n" );
                for ( QName featureType : failed )
                    sb.append( "    -  " ).append( featureType ).append( "\n" );
                sb.append( "\n" );
            }
            return sb.toString();
        }

        static class FailedAndSucceed {
            List<QName> failed = new ArrayList<>();

            List<QName> succeed = new ArrayList<>();

            void addFailed( QName featureType ) {
                failed.add( featureType );
            }

            void addSucceed( QName featureType ) {
                succeed.add( featureType );
            }

        }
    }

}
