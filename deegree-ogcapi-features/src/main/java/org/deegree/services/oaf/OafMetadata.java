package org.deegree.services.oaf;

import org.deegree.commons.xml.jaxb.JAXBUtils;
import org.deegree.feature.persistence.FeatureStore;
import org.deegree.feature.persistence.FeatureStoreManager;
import org.deegree.feature.persistence.FeatureStoreProvider;
import org.deegree.services.jaxb.oaf.DeegreeOAF;
import org.deegree.services.metadata.OWSMetadataProvider;
import org.deegree.services.metadata.OWSMetadataProviderManager;
import org.deegree.services.oaf.config.htmlview.HtmlViewConfigManager;
import org.deegree.services.oaf.config.htmlview.HtmlViewConfigResource;
import org.deegree.workspace.Resource;
import org.deegree.workspace.ResourceBuilder;
import org.deegree.workspace.ResourceIdentifier;
import org.deegree.workspace.ResourceInitException;
import org.deegree.workspace.ResourceLocation;
import org.deegree.workspace.ResourceMetadata;
import org.deegree.workspace.Workspace;
import org.deegree.workspace.standard.AbstractResourceMetadata;
import org.deegree.workspace.standard.AbstractResourceProvider;
import org.deegree.workspace.standard.DefaultResourceIdentifier;

import java.util.List;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class OafMetadata extends AbstractResourceMetadata<Resource> {

    private static final String CONFIG_JAXB_PACKAGE = "org.deegree.services.jaxb.oaf";

    public OafMetadata( Workspace workspace, ResourceLocation<Resource> location,
                        AbstractResourceProvider<Resource> provider ) {
        super( workspace, location, provider );
    }

    @Override
    public ResourceBuilder<Resource> prepare() {
        try {
            DeegreeOAF cfg = (DeegreeOAF) JAXBUtils.unmarshall( CONFIG_JAXB_PACKAGE, provider.getSchema(),
                                                                location.getAsStream(), workspace );

            List<String> list = cfg.getFeatureStoreId();
            if ( list != null && !list.isEmpty() ) {
                for ( String id : list ) {
                    dependencies.add( new DefaultResourceIdentifier<FeatureStore>( FeatureStoreProvider.class, id ) );
                }
            } else {
                FeatureStoreManager fmgr = workspace.getResourceManager( FeatureStoreManager.class );
                for ( ResourceMetadata<FeatureStore> md : fmgr.getResourceMetadata() ) {
                    softDependencies.add( md.getIdentifier() );
                }
            }

            OWSMetadataProviderManager mmgr = workspace.getResourceManager( OWSMetadataProviderManager.class );
            for ( ResourceMetadata<OWSMetadataProvider> md : mmgr.getResourceMetadata() ) {
                ResourceIdentifier<OWSMetadataProvider> id = md.getIdentifier();
                if ( id.getId().equals( getIdentifier().getId() + "_metadata" ) ) {
                    softDependencies.add( id );
                }
            }

            HtmlViewConfigManager hvmgr = workspace.getResourceManager( HtmlViewConfigManager.class );
            for ( ResourceMetadata<HtmlViewConfigResource> hv : hvmgr.getResourceMetadata() ) {
                ResourceIdentifier<HtmlViewConfigResource> id = hv.getIdentifier();
                if ( id.getId().equals( getIdentifier().getId() + "_htmlview" ) ) {
                    softDependencies.add( id );
                }
            }

            return new OafBuilder( this, workspace, cfg );
        } catch ( Exception e ) {
            throw new ResourceInitException( e.getLocalizedMessage(), e );
        }
    }
}
