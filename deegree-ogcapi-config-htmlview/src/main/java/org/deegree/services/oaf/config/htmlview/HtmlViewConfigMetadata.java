package org.deegree.services.oaf.config.htmlview;

import org.deegree.commons.xml.jaxb.JAXBUtils;
import org.deegree.services.jaxb.config.htmlview.HtmlView;
import org.deegree.workspace.ResourceBuilder;
import org.deegree.workspace.ResourceInitException;
import org.deegree.workspace.ResourceLocation;
import org.deegree.workspace.Workspace;
import org.deegree.workspace.standard.AbstractResourceMetadata;
import org.deegree.workspace.standard.AbstractResourceProvider;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class HtmlViewConfigMetadata extends AbstractResourceMetadata<HtmlViewConfigResource> {

    private static final String CONFIG_JAXB_PACKAGE = "org.deegree.services.jaxb.config.htmlview";

    public HtmlViewConfigMetadata( Workspace workspace, ResourceLocation<HtmlViewConfigResource> location,
                                   AbstractResourceProvider<HtmlViewConfigResource> provider ) {
        super( workspace, location, provider );
    }

    @Override
    public ResourceBuilder<HtmlViewConfigResource> prepare() {
        try {
            HtmlView cfg = (HtmlView) JAXBUtils.unmarshall( CONFIG_JAXB_PACKAGE, provider.getSchema(),
                                                            location.getAsStream(), workspace );
            return new HtmlViewConfigBuilder( this, workspace, cfg );
        } catch ( Exception e ) {
            throw new ResourceInitException( e.getLocalizedMessage(), e );
        }
    }
}
