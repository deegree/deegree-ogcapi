package org.deegree.services.oaf;

import org.deegree.workspace.Resource;
import org.deegree.workspace.standard.DefaultResourceManager;
import org.deegree.workspace.standard.DefaultResourceManagerMetadata;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class OafManager extends DefaultResourceManager<Resource> {

    public OafManager() {
        super( new DefaultResourceManagerMetadata<Resource>( OgcApiProvider.class, "OGC API", "services" ) );
    }

}
