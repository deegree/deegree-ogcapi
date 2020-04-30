package org.deegree.services.oaf;

import org.deegree.workspace.Resource;
import org.deegree.workspace.ResourceLocation;
import org.deegree.workspace.ResourceMetadata;
import org.deegree.workspace.Workspace;

import java.net.URL;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class OafProvider extends OgcApiProvider {

    @Override
    public String getNamespace() {
        return "http://www.deegree.org/services/oaf";
    }

    @Override
    public ResourceMetadata<Resource> createFromLocation( Workspace workspace, ResourceLocation<Resource> location ) {
        return new OafMetadata( workspace, location, this );
    }

    @Override
    public URL getSchema() {
        return OafProvider.class.getResource( "/META-INF/schemas/services/oaf/3.4.0/oaf_configuration.xsd" );
    }

}
