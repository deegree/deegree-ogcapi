package org.deegree.services.oaf.config.datasets;

import org.deegree.services.jaxb.ogcapi.datasets.Datasets;
import org.deegree.workspace.Resource;
import org.deegree.workspace.ResourceMetadata;
import org.deegree.workspace.Workspace;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * {@link Resource} parsing the {@link DatasetsConfiguration}.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class DatasetsConfigResource implements Resource {

    private static final Logger LOG = getLogger( DatasetsConfigResource.class );

    private final ResourceMetadata<DatasetsConfigResource> metadata;

    private final Workspace workspace;

    private final Datasets config;

    private DatasetsConfiguration DatasetsConfiguration;

    public DatasetsConfigResource( ResourceMetadata<DatasetsConfigResource> metadata, Workspace workspace,
                                   Datasets config ) {
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
        DatasetsConfiguration = parseDatasetsConfiguration();
    }

    @Override
    public void destroy() {

    }

    public DatasetsConfiguration getDatasetsConfiguration() {
        return DatasetsConfiguration;
    }

    private DatasetsConfiguration parseDatasetsConfiguration() {
        if ( config == null )
            return null;
        String title = config.getTitle();
        String description = config.getDescription();
        Contact configuredContact = null;
        Datasets.Contact contact = config.getContact();

        if ( contact != null ) {
            configuredContact = new Contact( contact.getName(), contact.getEMail(), contact.getUrl() );
        }
        return new DatasetsConfiguration( title, description, configuredContact );
    }

}