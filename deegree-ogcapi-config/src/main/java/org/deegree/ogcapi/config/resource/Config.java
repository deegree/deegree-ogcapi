/*-
 * #%L
 * deegree-ogcapi-config - OGC API Config implementation
 * %%
 * Copyright (C) 2019 - 2020 lat/lon GmbH, info@lat-lon.de, www.lat-lon.de
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package org.deegree.ogcapi.config.resource;

import io.swagger.v3.oas.annotations.Operation;
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
import org.deegree.ogcapi.config.exceptions.RestartException;
import org.deegree.ogcapi.config.exceptions.UnsupportedWorkspaceException;
import org.deegree.ogcapi.config.exceptions.UpdateException;
import org.deegree.ogcapi.config.exceptions.UploadException;
import org.deegree.ogcapi.config.exceptions.ValidationException;
import org.deegree.services.config.ApiKey;
import org.deegree.services.controller.OGCFrontController;
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
    @Operation(description = "/config/download - download currently running workspace")
    @Path("/download")
    public Response download( @Context HttpServletRequest request ) {
        token.validate( request );
        StreamingOutput streamingOutput = outputStream -> {
            try {
                downloadWorkspace( outputStream );
            } catch ( DownloadException e ) {
                throw new WebApplicationException( e );
            }
        };
        return Response.ok( streamingOutput, "application/zip" )
                       .header( "Content-Disposition",
                                "attachment; filename=" + OGCFrontController.getServiceWorkspace().getName() + ".zip" )
                       .build();
    }

    @GET
    @Operation(description = "/config/download/<path> - download file in workspace")
    @Path("/download/{path : (.+)?}")
    public Response download( @Context HttpServletRequest request,
                              @PathParam("path") String path )
                    throws InvalidPathException {
        token.validate( request );
        File file = downloadFile( path );
        if ( file.getName().endsWith( ".xml" ) )
            return Response.ok( file, APPLICATION_XML_TYPE ).build();
        else
            return Response.ok( file, APPLICATION_OCTET_STREAM_TYPE ).build();
    }

    @GET
    @Operation(description = "/config/restart - restart currently running workspace")
    @Path("/restart")
    public Response restart( @Context HttpServletRequest request )
                    throws RestartException {
        token.validate( request );

        String restart = Restart.restart();
        afterRestartOrUpdate();
        return Response.ok( restart, APPLICATION_OCTET_STREAM_TYPE ).build();
    }

    @GET
    @Operation(description = "/config/restart/<path> - restarts all resources connected to the specified one")
    @Path("/restart/{path : (.+)?}")
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
        String update = Update.update();
        afterRestartOrUpdate();
        return Response.ok( update, TEXT_PLAIN ).build();
    }

    @GET
    @Operation(description =
                    "/config/update/bboxcache[?featureStoreId=] - recalculates the bounding boxes of all feature stores of the currently running workspace, with the parameter 'featureStoreId' a comma separated list of feature stores to update can be passed")
    @Path("/update/bboxcache")
    public Response updateBboxcache( @Context HttpServletRequest request,
                                     @QueryParam("featureStoreId") String featureStoreId )
                    throws BboxCacheUpdateException {
        token.validate( request );
        String log = UpdateBboxCache.updateBboxCache( request.getQueryString() );
        return Response.ok( log, TEXT_PLAIN ).build();
    }

    @GET
    @Operation(description = "/config/list - list currently running workspace")
    @Path("/list")
    public Response list( @Context HttpServletRequest request )
                    throws InvalidPathException, UnsupportedWorkspaceException {
        token.validate( request );
        String fileList = List.list();
        return Response.ok( fileList, TEXT_PLAIN ).build();
    }

    @GET
    @Operation(description = "/config/list[/path] - list directory in workspace of the currently running workspace")
    @Path("/list/{path : (.+)?}")
    public Response list( @Context HttpServletRequest request,
                          @PathParam("path") String path )
                    throws InvalidPathException {
        token.validate( request );
        String fileList = List.list( path );
        return Response.ok( fileList, TEXT_PLAIN ).build();
    }

    @GET
    @Operation(description = "/config/validate[/path] - validate currently running workspace")
    @Path("/validate")
    public Response validate( @Context HttpServletRequest request )
                    throws ValidationException {
        token.validate( request );
        String validationResult = Validate.validate();
        return Response.ok( validationResult, TEXT_PLAIN ).build();
    }

    @GET
    @Operation(description = "/config/validate[/path] - validate file in workspace")
    @Path("/validate/{path : (.+)?}")
    public Response validateR( @Context HttpServletRequest request,
                               @PathParam("path") String path )
                    throws ValidationException, InvalidPathException {
        token.validate( request );
        String validationResult = Validate.validate( path );
        return Response.ok( validationResult, TEXT_PLAIN ).build();
    }

    @PUT
    @Operation(description = "/config/upload/path/file - upload file into current workspace")
    @Path("/upload/{path : (.+)?}")
    public Response upload( @Context HttpServletRequest request,
                            @PathParam("path") String path )
                    throws IOException, UploadException {
        token.validate( request );
        String upload = Upload.upload( path, request );
        return Response.ok( upload, TEXT_PLAIN ).build();
    }

    @DELETE
    @Operation(description = "/config/delete - delete currently running workspace")
    @Path("/delete")
    public Response delete( @Context HttpServletRequest request )
                    throws DeleteException {
        token.validate( request );
        String delete = Delete.delete();
        return Response.ok( delete, TEXT_PLAIN ).build();
    }

    @DELETE
    @Operation(description = "/config/delete[/path] - delete file in workspace")
    @Path("/delete/{path : (.+)?}")
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
