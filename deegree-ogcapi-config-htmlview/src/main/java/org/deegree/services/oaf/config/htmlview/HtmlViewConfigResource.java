package org.deegree.services.oaf.config.htmlview;

import org.deegree.services.jaxb.ogcapi.htmlview.HtmlView;
import org.deegree.workspace.Resource;
import org.deegree.workspace.ResourceMetadata;
import org.deegree.workspace.Workspace;
import org.slf4j.Logger;

import java.io.File;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * {@link Resource} parsing the {@link HtmlViewConfiguration}.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class HtmlViewConfigResource implements Resource {

    private static final Logger LOG = getLogger( HtmlViewConfigResource.class );

    private final ResourceMetadata<HtmlViewConfigResource> metadata;

    private final Workspace workspace;

    private final HtmlView config;

    private HtmlViewConfiguration htmlViewConfiguration;

    public HtmlViewConfigResource( ResourceMetadata<HtmlViewConfigResource> metadata, Workspace workspace,
                                   HtmlView config ) {
        this.metadata = metadata;
        this.workspace = workspace;
        this.config = config;
    }

    @Override
    public ResourceMetadata<? extends Resource> getMetadata() {
        return metadata;
    }

    @Override
    public void init() {
        htmlViewConfiguration = parseHtmlViewConfiguration();
    }

    @Override
    public void destroy() {

    }

    public HtmlViewConfiguration getHtmlViewConfiguration() {
        return htmlViewConfiguration;
    }

    private HtmlViewConfiguration parseHtmlViewConfiguration() {
        if ( config == null )
            return null;
        File cssFile = null;
        String configuredCssFile = config.getCssFile();
        if ( configuredCssFile != null ) {
            cssFile = this.metadata.getLocation().resolveToFile( configuredCssFile );
            if ( !cssFile.exists() || !cssFile.isFile() ) {
                LOG.warn( "Configured cssFile does not exist or is not a valid file" );
            }
        }
        String wmsUrl = null;
        String wmsLayers = null;
        String crsCode = null;
        String crsProj4Definition = null;

        HtmlView.Map map = config.getMap();
        if ( map != null ) {
            wmsUrl = map.getWMSUrl();
            wmsLayers = map.getWMSLayers();
            if ( map.getCrsProj4Definition() != null ) {
                crsCode = map.getCrsProj4Definition().getCode();
                crsProj4Definition = map.getCrsProj4Definition().getValue();
            }
        }
        return new HtmlViewConfiguration( cssFile, config.getImprintUrl(), config.getPrivacyPolicyUrl(), wmsUrl,
                                          wmsLayers, crsCode, crsProj4Definition );
    }

}