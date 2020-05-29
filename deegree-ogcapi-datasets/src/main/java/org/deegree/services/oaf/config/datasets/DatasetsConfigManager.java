package org.deegree.services.oaf.config.datasets;

import org.deegree.workspace.standard.DefaultResourceManager;
import org.deegree.workspace.standard.DefaultResourceManagerMetadata;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class DatasetsConfigManager extends DefaultResourceManager<DatasetsConfigResource> {

    public DatasetsConfigManager() {
        super( new DefaultResourceManagerMetadata<>( OgcApiDatasetsProvider.class,
                                                     "OGC API Datasets config", "ogcapi" ) );
    }

}
