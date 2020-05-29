package org.deegree.services.oaf.config.datasets;

import org.deegree.commons.xml.jaxb.JAXBUtils;
import org.deegree.services.jaxb.ogcapi.datasets.Datasets;
import org.deegree.workspace.ResourceBuilder;
import org.deegree.workspace.ResourceInitException;
import org.deegree.workspace.ResourceLocation;
import org.deegree.workspace.Workspace;
import org.deegree.workspace.standard.AbstractResourceMetadata;
import org.deegree.workspace.standard.AbstractResourceProvider;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class DatasetsConfigMetadata extends AbstractResourceMetadata<DatasetsConfigResource> {

    private static final String CONFIG_JAXB_PACKAGE = "org.deegree.services.jaxb.ogcapi.datasets";

    public DatasetsConfigMetadata( Workspace workspace, ResourceLocation<DatasetsConfigResource> location,
                                   AbstractResourceProvider<DatasetsConfigResource> provider ) {
        super( workspace, location, provider );
    }

    @Override
    public ResourceBuilder<DatasetsConfigResource> prepare() {
        try {
            Datasets cfg = (Datasets) JAXBUtils.unmarshall( CONFIG_JAXB_PACKAGE, provider.getSchema(),
                                                            location.getAsStream(), workspace );
            return new DatasetsConfigBuilder( this, workspace, cfg );
        } catch ( Exception e ) {
            throw new ResourceInitException( e.getLocalizedMessage(), e );
        }
    }
}
