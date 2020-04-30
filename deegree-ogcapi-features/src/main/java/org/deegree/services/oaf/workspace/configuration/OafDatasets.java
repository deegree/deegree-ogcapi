package org.deegree.services.oaf.workspace.configuration;

import org.deegree.services.oaf.exceptions.UnknownDatasetId;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates all datasets configurations.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class OafDatasets {

    private Map<String, OafDatasetConfiguration> datasetConfigurations = new HashMap<>();

    /**
     * Add a new dataset.
     *
     * @param id
     *                 id of the dataset, never <code>null</code>
     * @param datasetConfiguration
     *                 the configuration of the dataset, never <code>null</code>
     */
    public void addDataset( String id, OafDatasetConfiguration datasetConfiguration ) {
        this.datasetConfigurations.put( id, datasetConfiguration );
    }

    public Map<String, OafDatasetConfiguration> getDatasets() {
        return datasetConfigurations;
    }

    public OafDatasetConfiguration getDataset( String datasetId )
                    throws UnknownDatasetId {
        if ( !datasetConfigurations.containsKey( datasetId ) ) {
            throw new UnknownDatasetId( datasetId );
        }
        return datasetConfigurations.get( datasetId );
    }
}