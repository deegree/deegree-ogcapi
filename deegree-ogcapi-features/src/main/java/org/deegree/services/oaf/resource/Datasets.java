package org.deegree.services.oaf.resource;

import org.deegree.services.oaf.domain.dataset.Dataset;
import org.deegree.services.oaf.link.Link;
import org.deegree.services.oaf.link.LinkBuilder;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;
import org.deegree.services.oaf.workspace.configuration.OafDatasets;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Path("/datasets")
public class Datasets {

    @GET
    @Produces({ APPLICATION_JSON })
    public org.deegree.services.oaf.domain.dataset.Datasets datasets(
                    @Context
                                    UriInfo uriInfo ) {
        LinkBuilder linkBuilder = new LinkBuilder( uriInfo );
        List<Link> links = linkBuilder.createDatasetsLinks();
        List<Dataset> datasets = new ArrayList<>();

        OafDatasets oafDatasets = DeegreeWorkspaceInitializer.getOafDatasets();
        Map<String, OafDatasetConfiguration> datasetsConfigurations = oafDatasets.getDatasets();
        datasetsConfigurations.forEach( ( id, oafDatasetConfiguration ) -> {
            List<Link> datasetLinks = linkBuilder.createDatasetLinks( id );
            Dataset dataset = new Dataset( id, datasetLinks );
            datasets.add( dataset );
        } );
        return new org.deegree.services.oaf.domain.dataset.Datasets( links, datasets);
    }

}