package org.deegree.services.oaf.config.datasets;

import org.deegree.workspace.ResourceLocation;
import org.deegree.workspace.ResourceMetadata;
import org.deegree.workspace.Workspace;

import java.net.URL;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class DatasetsConfigProvider extends OgcApiDatasetsProvider {

    @Override
    public String getNamespace() {
        return "http://www.deegree.org/ogcapi/datasets";
    }

    @Override
    public ResourceMetadata<DatasetsConfigResource> createFromLocation( Workspace workspace,
                                                                        ResourceLocation<DatasetsConfigResource> location ) {
        return new DatasetsConfigMetadata( workspace, location, this );
    }

    @Override
    public URL getSchema() {
        return DatasetsConfigProvider.class.getResource( "/META-INF/schemas/ogcapi/datasets/3.4.0/datasets.xsd" );
    }

}
