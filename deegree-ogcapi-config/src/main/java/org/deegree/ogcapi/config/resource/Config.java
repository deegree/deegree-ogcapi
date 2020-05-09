package org.deegree.ogcapi.config.resource;

import io.swagger.v3.oas.annotations.Operation;
import org.deegree.commons.config.DeegreeWorkspace;
import org.deegree.commons.utils.Pair;
import org.deegree.ogcapi.config.actions.Delete;
import org.deegree.ogcapi.config.actions.List;
import org.deegree.ogcapi.config.actions.Restart;
import org.deegree.ogcapi.config.actions.Update;
import org.deegree.ogcapi.config.actions.UpdateBboxCache;
import org.deegree.ogcapi.config.actions.Upload;
import org.deegree.ogcapi.config.actions.Validate;
import org.deegree.ogcapi.config.exceptions.BboxCacheUpdateException;
import org.deegree.ogcapi.config.exceptions.DeleteException;
import org.deegree.ogcapi.config.exceptions.DownloadException;
import org.deegree.ogcapi.config.exceptions.InvalidPathException;
import org.deegree.ogcapi.config.exceptions.InvalidWorkspaceException;
import org.deegree.ogcapi.config.exceptions.RestartException;
import org.deegree.ogcapi.config.exceptions.UpdateException;
import org.deegree.ogcapi.config.exceptions.UploadException;
import org.deegree.ogcapi.config.exceptions.ValidationException;
import org.deegree.services.config.ApiKey;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.File;
import java.io.IOException;

import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_XML_TYPE;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static org.deegree.ogcapi.config.actions.Download.downloadFile;
import static org.deegree.ogcapi.config.actions.Download.downloadWorkspace;
import static org.deegree.services.config.actions.Utils.getWorkspaceAndPath;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Path("/config")
public class Config {

    private static final Logger LOG = getLogger( Config.class );

    @Inject
    private RestartOrUpdateHandler restartOrUpdateHandler;

    private static ApiKey token = new ApiKey();

    @GET
    @Operation(description = "/config/download[/path] - download currently running workspace or file in workspace \n"
                             + "/config/download/wsname[/path] - download workspace with name <wsname> or file in workspace")
    @Path("/download{path : (.+)?}")
    public Response download( @Context HttpServletRequest request,
                              @PathParam("path") String path )
                    throws InvalidPathException {
        token.validate( request );
        Pair<DeegreeWorkspace, String> p = getWorkspaceAndPath( path );

        DeegreeWorkspace workspace = p.first;
        if ( p.second == null ) {
            StreamingOutput streamingOutput = outputStream -> {
                try {
                    downloadWorkspace( workspace, outputStream );
                } catch ( InvalidWorkspaceException | DownloadException e ) {
                    throw new WebApplicationException( e );
                }
            };
            return Response.ok( streamingOutput, "application/zip" )
                           .header( "Content-Disposition", "attachment; filename=" + workspace.getName() + ".zip" )
                           .build();
        } else {
            File file = downloadFile( workspace, p.second );
            if ( file.getName().endsWith( ".xml" ) )
                return Response.ok( file, APPLICATION_XML_TYPE ).build();
            else
                return Response.ok( file, APPLICATION_OCTET_STREAM_TYPE ).build();
        }

    }

    @GET
    @Operation(description = "/config/restart - restart currently running workspace\n"
                             + "/config/restart[/path] - restarts all resources connected to the specified one\n"
                             + "/config/restart/wsname - restart with workspace <wsname>")
    @Path("/restart{path : (.+)?}")
    public Response restart( @Context HttpServletRequest request,
                             @PathParam("path") String path )
                    throws RestartException {
        token.validate( request );
        String restart = Restart.restart( path );
        afterRestartOrUpdate();
        return Response.ok( restart, APPLICATION_OCTET_STREAM_TYPE ).build();
    }

    @GET
    @Operation(description =
                    "/config/update - update currently running workspace, rescan config files and update resources")
    @Path("/update")
    public Response update( @Context HttpServletRequest request,
                            @QueryParam("featureStoreId") String featureStoreId )
                    throws UpdateException {
        token.validate( request );
        String update = Update.update( null );
        afterRestartOrUpdate();
        return Response.ok( update, TEXT_PLAIN ).build();
    }

    @GET
    @Operation(description =
                    "/config/update/wsname - update with workspace <wsname>, rescan config files and update resources"
                    +
                    "/config/update/bboxcache[?featureStoreId=] - recalculates the bounding boxes of all feature stores of the currently running workspace, with the parameter 'featureStoreId' a comma separated list of feature stores to update can be passed\n"
                    + "/config/update/bboxcache/wsname[?featureStoreId=] - recalculates the bounding boxes of all feature stores of the workspace with name <wsname>, with the parameter 'featureStoreId' a comma separated list of feature stores to update can be passed")
    @Path("/update/{wsname}")
    public Response updateWorkspace( @Context HttpServletRequest request,
                                     @PathParam("wsname") String wsname,
                                     @QueryParam("featureStoreId") String featureStoreId )
                    throws BboxCacheUpdateException, UpdateException {
        token.validate( request );
        if ( wsname != null && wsname.startsWith( "bboxcache" ) ) {
            String log = UpdateBboxCache.updateBboxCache( wsname, request.getQueryString() );
            return Response.ok( log, TEXT_PLAIN ).build();
        }
        String update = Update.update( wsname );
        afterRestartOrUpdate();
        return Response.ok( update, TEXT_PLAIN ).build();
    }

    @GET
    @Operation(description =
                    "/config/update/bboxcache[?featureStoreId=] - recalculates the bounding boxes of all feature stores of the currently running workspace, with the parameter 'featureStoreId' a comma separated list of feature stores to update can be passed\n"
                    + "/config/update/bboxcache/wsname[?featureStoreId=] - recalculates the bounding boxes of all feature stores of the workspace with name <wsname>, with the parameter 'featureStoreId' a comma separated list of feature stores to update can be passed")
    @Path("/update/bboxcache/{wsname}")
    public Response updateBboxCache( @Context HttpServletRequest request,
                                     @PathParam("wsname") String wsname,
                                     @QueryParam("featureStoreId") String featureStoreId )
                    throws BboxCacheUpdateException {
        token.validate( request );
        String log = UpdateBboxCache.updateBboxCache( wsname, request.getQueryString() );
        return Response.ok( log, TEXT_PLAIN ).build();
    }

    @GET
    @Operation(description = "/config/list[/path] - list currently running workspace or directory in workspace\n"
                             + "/config/list/wsname[/path] - list workspace with name <wsname> or directory in workspace")
    @Path("/list{path : (.+)?}")
    public Response list( @Context HttpServletRequest request,
                          @PathParam("path") String path )
                    throws InvalidPathException, InvalidWorkspaceException {
        token.validate( request );
        String fileList = List.list( path );
        return Response.ok( fileList, TEXT_PLAIN ).build();
    }

    @GET
    @Operation(description = "/config/validate[/path] - validate currently running workspace or file in workspace\n"
                             + "/config/validate/wsname[/path] - validate workspace with name <wsname> or file in workspace")
    @Path("/validate{path : (.+)?}")
    public Response validate( @Context HttpServletRequest request,
                              @PathParam("path") String path )
                    throws ValidationException {
        token.validate( request );
        String validationResult = Validate.validate( path );
        return Response.ok( validationResult, TEXT_PLAIN ).build();
    }

    @PUT
    @Operation(description = "/config/upload/wsname.zip - upload workspace <wsname>\n"
                             + "/config/upload/path/file - upload file into current workspace\n"
                             + "/config/upload/wsname/path/file - upload file into workspace with name <wsname>")
    @Path("/upload{path : (.+)?}")
    public Response upload( @Context HttpServletRequest request,
                            @PathParam("path") String path )
                    throws IOException, UploadException {
        token.validate( request );
        String upload = Upload.upload( path, request );
        return Response.ok( upload, TEXT_PLAIN ).build();
    }

    @DELETE
    @Operation(description = "/config/delete[/path] - delete currently running workspace or file in workspace\n"
                             + "/config/delete/wsname[/path] - delete workspace with name <wsname> or file in workspace")
    @Path("/delete{path : (.+)?}")
    public Response delete( @Context HttpServletRequest request,
                            @PathParam("path") String path )
                    throws InvalidPathException, DeleteException {
        token.validate( request );
        String delete = Delete.delete( path );
        return Response.ok( delete, TEXT_PLAIN ).build();
    }

    private void afterRestartOrUpdate() {
        if ( restartOrUpdateHandler != null ) {
            LOG.info( "Handle after restart/update" );
            restartOrUpdateHandler.afterRestartOrUpdate();
        }
    }

}