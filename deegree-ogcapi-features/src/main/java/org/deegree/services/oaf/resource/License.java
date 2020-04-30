package org.deegree.services.oaf.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.deegree.services.oaf.exceptions.UnknownDatasetId;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;
import org.deegree.services.oaf.workspace.configuration.ServiceMetadata;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Path("/datasets/{datasetId}/license")
public class License {

    @GET
    @Produces({ TEXT_PLAIN })
    @Operation(summary = "License", description = "License of all collections of this datasets", responses = {
                    @ApiResponse(description = "default response", content = @Content(mediaType = "text/plain")),
                    @ApiResponse(responseCode = "404", description = "No license available", content = @Content(mediaType = "text/plain")) })
    @Tag(name = "Capabilities")
    public Object license(
                    @PathParam("datasetId")
                                    String datasetId )
                    throws UnknownDatasetId {
        OafDatasetConfiguration dataset = DeegreeWorkspaceInitializer.getOafDatasets().getDataset( datasetId );
        ServiceMetadata metadata = dataset.getServiceMetadata();

        if ( !metadata.hasLicense() )
            return Response.status( Response.Status.NOT_FOUND ).entity( "No license available" ).build();

        String license = createLicenseContent( metadata );
        return Response.ok( license ).build();
    }

    private String createLicenseContent( ServiceMetadata metadata ) {
        StringBuilder license = new StringBuilder();
        if ( metadata.getFees() != null )
            license.append( metadata.getFees() ).append( "; " );
        List<String> accessConstraints = metadata.getAccessConstraints();
        if ( accessConstraints != null && !accessConstraints.isEmpty() )
            accessConstraints.forEach( ac -> license.append( ac ).append( ", " ) );
        return license.toString();
    }

}