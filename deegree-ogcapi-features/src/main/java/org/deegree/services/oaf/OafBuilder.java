package org.deegree.services.oaf;

import org.deegree.services.ogcapi.features.DeegreeOAF;
import org.deegree.workspace.Resource;
import org.deegree.workspace.ResourceBuilder;
import org.deegree.workspace.ResourceMetadata;
import org.deegree.workspace.Workspace;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class OafBuilder implements ResourceBuilder<Resource> {

    private ResourceMetadata<Resource> metadata;

    private Workspace workspace;

    private DeegreeOAF config;

    public OafBuilder( ResourceMetadata<Resource> metadata, Workspace workspace, DeegreeOAF config ) {
        this.metadata = metadata;
        this.workspace = workspace;
        this.config = config;
    }

    @Override
    public Resource build() {
        return new OafResource( metadata, workspace, config );
    }
}
