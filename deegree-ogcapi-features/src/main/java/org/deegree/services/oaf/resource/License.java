package org.deegree.services.oaf.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.deegree.services.oaf.exceptions.UnknownDatasetId;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.deegree.services.oaf.workspace.configuration.DatasetMetadata;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Path("/datasets/{datasetId}/license")
public class License {

    @Inject
    private DeegreeWorkspaceInitializer deegreeWorkspaceInitializer;

    @Path("/provider")
    @GET
    @Produces({ TEXT_PLAIN })
    @Operation(summary = "License", description = "License of all collections of this datasets", responses = {
                    @ApiResponse(description = "default response", content = @Content(mediaType = "text/plain")),
                    @ApiResponse(responseCode = "404", description = "No license available", content = @Content(mediaType = "text/plain")) })
    @Tag(name = "Capabilities")
    public Response providerLicense(
                    @PathParam("datasetId")
                                    String datasetId )
                    throws UnknownDatasetId {
        OafDatasetConfiguration dataset = deegreeWorkspaceInitializer.getOafDatasets().getDataset( datasetId );
        DatasetMetadata metadata = dataset.getServiceMetadata();

        if ( metadata.hasProviderLicenseUrl() )
            return Response.status( Response.Status.NOT_FOUND ).entity( "No license available" ).build();
        return Response.ok( metadata.getProviderLicense().getDescription() ).build();
    }

    @Path("/dataset")
    @GET
    @Produces({ TEXT_PLAIN })
    @Operation(summary = "License", description = "License of all collections of this datasets", responses = {
                    @ApiResponse(description = "default response", content = @Content(mediaType = "text/plain")),
                    @ApiResponse(responseCode = "404", description = "No license available", content = @Content(mediaType = "text/plain")) })
    @Tag(name = "Capabilities")
    public Response datasetLicense(
                    @PathParam("datasetId")
                                    String datasetId )
                    throws UnknownDatasetId {
        OafDatasetConfiguration dataset = deegreeWorkspaceInitializer.getOafDatasets().getDataset( datasetId );
        DatasetMetadata metadata = dataset.getServiceMetadata();

        if ( metadata.hasDatasetLicenseUrl() )
            return Response.status( Response.Status.NOT_FOUND ).entity( "No license available" ).build();
        return Response.ok( metadata.getDatasetLicense().getDescription() ).build();
    }

}