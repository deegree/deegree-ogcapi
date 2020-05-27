package org.deegree.services.oaf.config.htmlview;

import org.deegree.workspace.standard.DefaultResourceManager;
import org.deegree.workspace.standard.DefaultResourceManagerMetadata;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class HtmlViewConfigManager extends DefaultResourceManager<HtmlViewConfigResource> {

    public HtmlViewConfigManager() {
        super( new DefaultResourceManagerMetadata<>( OgcApiConfigProvider.class,
                                                     "OGC API HTML config", "html" ) );
    }

}
