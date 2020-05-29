package org.deegree.services.oaf.config.htmlview;

import org.deegree.workspace.ResourceLocation;
import org.deegree.workspace.ResourceMetadata;
import org.deegree.workspace.Workspace;

import java.net.URL;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class HtmlViewConfigProvider extends OgcApiConfigProvider {

    @Override
    public String getNamespace() {
        return "http://www.deegree.org/ogcapi/htmlview";
    }

    @Override
    public ResourceMetadata<HtmlViewConfigResource> createFromLocation( Workspace workspace,
                                                                        ResourceLocation<HtmlViewConfigResource> location ) {
        return new HtmlViewConfigMetadata( workspace, location, this );
    }

    @Override
    public URL getSchema() {
        return HtmlViewConfigProvider.class.getResource( "/META-INF/schemas/ogcapi/htmlview/3.4.0/htmlview.xsd" );
    }

}
