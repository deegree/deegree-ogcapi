package org.deegree.services.oaf.config.datasets;

import org.deegree.services.jaxb.ogcapi.datasets.Datasets;
import org.deegree.workspace.ResourceBuilder;
import org.deegree.workspace.ResourceMetadata;
import org.deegree.workspace.Workspace;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class DatasetsConfigBuilder implements ResourceBuilder<DatasetsConfigResource> {

    private final Datasets config;

    private ResourceMetadata<DatasetsConfigResource> metadata;

    private Workspace workspace;

    public DatasetsConfigBuilder( ResourceMetadata<DatasetsConfigResource> metadata, Workspace workspace,
                                  Datasets config ) {
        this.metadata = metadata;
        this.workspace = workspace;
        this.config = config;
    }

    @Override
    public DatasetsConfigResource build() {
        return new DatasetsConfigResource( metadata, workspace, config );
    }
}
