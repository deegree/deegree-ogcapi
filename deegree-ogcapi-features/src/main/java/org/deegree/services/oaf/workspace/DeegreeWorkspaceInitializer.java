package org.deegree.services.oaf.workspace;

import org.deegree.commons.config.DeegreeWorkspace;
import org.deegree.commons.config.ResourceInitException;
import org.deegree.services.oaf.OafResource;
import org.deegree.services.oaf.OgcApiProvider;
import org.deegree.services.oaf.config.htmlview.HtmlViewConfigResource;
import org.deegree.services.oaf.config.htmlview.HtmlViewConfiguration;
import org.deegree.services.oaf.config.htmlview.OgcApiConfigProvider;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;
import org.deegree.services.oaf.workspace.configuration.OafDatasets;
import org.deegree.workspace.Resource;
import org.deegree.workspace.ResourceIdentifier;
import org.deegree.workspace.Workspace;
import org.slf4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Instantiation of the deegree workspces. This is a workaround!
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@WebListener
public class DeegreeWorkspaceInitializer implements ServletContextListener {

    private static final Logger LOG = getLogger( DeegreeWorkspaceInitializer.class );

    public static final String DEEGREE_WORKSPACE_NAME = "ogcapi-workspace";

    private static OafDatasets oafConfiguration = new OafDatasets();

    private static Map<String, HtmlViewConfiguration> htmlViewConfigurations = new HashMap<>();

    private static HtmlViewConfiguration globalHtmlViewConfiguration;

    @Override
    public void contextInitialized( ServletContextEvent event ) {
        DeegreeWorkspace workspace = DeegreeWorkspace.getInstance( DEEGREE_WORKSPACE_NAME );
        try {
            workspace.initAll();

            Workspace newWorkspace = workspace.getNewWorkspace();
            List<ResourceIdentifier<Resource>> oafResourceIdentifiers = newWorkspace.getResourcesOfType(
                            OgcApiProvider.class );
            oafResourceIdentifiers.forEach( resourceResourceIdentifier -> {
                String id = resourceResourceIdentifier.getId();
                OafResource resource = (OafResource) newWorkspace.getResource( OgcApiProvider.class, id );
                OafDatasetConfiguration oafConfiguration = resource.getOafConfiguration();
                this.oafConfiguration.addDataset( id, oafConfiguration );
                HtmlViewConfiguration htmlViewConfiguration = resource.getHtmlViewConfiguration();
                if ( htmlViewConfiguration != null )
                    this.htmlViewConfigurations.put( id, htmlViewConfiguration );
            } );

            HtmlViewConfigResource globalHtmlViewConfigResource = workspace.getNewWorkspace().getResource(
                            OgcApiConfigProvider.class,
                            "htmlview" );
            if ( globalHtmlViewConfigResource != null )
                this.globalHtmlViewConfiguration = globalHtmlViewConfigResource.getHtmlViewConfiguration();
        } catch ( ResourceInitException e ) {
            LOG.error( "Workspace could not be initialised", e );
            throw new WorkspaceInitException( e );
        }
    }

    @Override
    public void contextDestroyed( ServletContextEvent event ) {
    }

    public static OafDatasets getOafDatasets() {
        return oafConfiguration;
    }

    /**
     * @param datasetId
     *                 the id of the dataset
     * @return the {@link HtmlViewConfiguration}  of the dataset with the passed id, <code>null</code> if not available
     */
    public static HtmlViewConfiguration getHtmlViewConfiguration( String datasetId ) {
        if ( htmlViewConfigurations.containsKey( datasetId ) )
            return htmlViewConfigurations.get( datasetId );
        return null;
    }

    /**
     * @return the global {@link HtmlViewConfiguration}, <code>null</code> if not available
     */
    public static HtmlViewConfiguration getGlobalHtmlViewConfiguration() {
        return globalHtmlViewConfiguration;
    }

}