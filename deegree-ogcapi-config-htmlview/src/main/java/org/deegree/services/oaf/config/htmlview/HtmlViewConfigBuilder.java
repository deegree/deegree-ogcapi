package org.deegree.services.oaf.config.htmlview;

import org.deegree.services.jaxb.ogcapi.htmlview.HtmlView;
import org.deegree.workspace.ResourceBuilder;
import org.deegree.workspace.ResourceMetadata;
import org.deegree.workspace.Workspace;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class HtmlViewConfigBuilder implements ResourceBuilder<HtmlViewConfigResource> {

    private ResourceMetadata<HtmlViewConfigResource> metadata;

    private Workspace workspace;

    private HtmlView config;

    public HtmlViewConfigBuilder( ResourceMetadata<HtmlViewConfigResource> metadata, Workspace workspace,
                                  HtmlView config ) {
        this.metadata = metadata;
        this.workspace = workspace;
        this.config = config;
    }

    @Override
    public HtmlViewConfigResource build() {
        return new HtmlViewConfigResource( metadata, workspace, config );
    }
}
