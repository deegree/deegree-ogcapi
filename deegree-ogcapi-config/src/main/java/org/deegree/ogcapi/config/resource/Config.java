package org.deegree.ogcapi.config.resource;

import io.swagger.v3.oas.annotations.Operation;
import org.deegree.services.config.ApiKey;
import org.deegree.services.config.actions.Crs;
import org.deegree.services.config.actions.Delete;
import org.deegree.services.config.actions.Download;
import org.deegree.services.config.actions.Invalidate;
import org.deegree.services.config.actions.List;
import org.deegree.services.config.actions.ListFonts;
import org.deegree.services.config.actions.ListWorkspaces;
import org.deegree.services.config.actions.Restart;
import org.deegree.services.config.actions.Update;
import org.deegree.services.config.actions.UpdateBboxCache;
import org.deegree.services.config.actions.Upload;
import org.deegree.services.config.actions.Validate;
import org.slf4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Path("/config")
public class Config {

    private static final Logger LOG = getLogger( Config.class );

    private static ApiKey token = new ApiKey();

    @GET
    @Operation(description = "/config/download[/path] - download currently running workspace or file in workspace \n"
                             + "/config/download/wsname[/path] - download workspace with name <wsname> or file in workspace")
    @Path("/download{path : (.+)?}")
    public void download( @Context HttpServletRequest request,
                          @Context HttpServletResponse response,
                          @PathParam("path") String path )
                    throws IOException {
        token.validate( request );
        Download.download( path, response );
    }

    @GET
    @Operation(description = "/config/restart - restart currently running workspace\n"
                             + "/config/restart[/path] - restarts all resources connected to the specified one\n"
                             + "/config/restart/wsname - restart with workspace <wsname>")
    @Path("/restart{path : (.+)?}")
    public void restart( @Context HttpServletRequest request,
                         @Context HttpServletResponse response,
                         @PathParam("path") String path )
                    throws IOException {
        token.validate( request );
        Restart.restart( path, response );
    }

    @GET
    @Operation(description =
                    "/config/update - update currently running workspace, rescan config files and update resources\n"
                    + "/config/update/wsname - update with workspace <wsname>, rescan config files and update resources")
    @Path("/update/{wsname}")
    public void update( @Context HttpServletRequest request,
                        @Context HttpServletResponse response,
                        @PathParam("wsname") String wsname )
                    throws IOException, ServletException {
        token.validate( request );
        Update.update( wsname, response );
    }

    @GET
    @Operation(description =
                    "/config/update/bboxcache[?featureStoreId=] - recalculates the bounding boxes of all feature stores of the currently running workspace, with the parameter 'featureStoreId' a comma separated list of feature stores to update can be passed\n"
                    + "/config/update/bboxcache/wsname[?featureStoreId=] - recalculates the bounding boxes of all feature stores of the workspace with name <wsname>, with the parameter 'featureStoreId' a comma separated list of feature stores to update can be passed\\n\" );")
    @Path("/update/bboxcache/{wsname}")
    public void updateBboxCache( @Context HttpServletRequest request,
                                 @Context HttpServletResponse response,
                                 @PathParam("wsname") String wsname,
                                 @QueryParam("featureStoreId") String featureStoreId )
                    throws IOException {
        token.validate( request );
        UpdateBboxCache.updateBboxCache( wsname, request.getQueryString(), response );
    }

    @GET
    @Operation(description = "/config/listworkspaces - list available workspace names")
    @Path("/listworkspaces")
    public void listworkspaces( @Context HttpServletRequest request,
                                @Context HttpServletResponse response )
                    throws IOException {
        token.validate( request );
        ListWorkspaces.listWorkspaces( response );
    }

    @GET
    @Operation(description = "/config/listfonts - list currently available fonts on the server")
    @Path("/listfonts")
    public void listfonts( @Context HttpServletRequest request,
                           @Context HttpServletResponse response )
                    throws IOException {
        token.validate( request );
        ListFonts.listFonts( response );
    }

    @GET
    @Operation(description = "/config/list[/path] - list currently running workspace or directory in workspace\n"
                             + "/config/list/wsname[/path] - list workspace with name <wsname> or directory in workspace")
    @Path("/list{path : (.+)?}")
    public void list( @Context HttpServletRequest request,
                      @Context HttpServletResponse response,
                      @PathParam("path") String path )
                    throws IOException {
        token.validate( request );
        List.list( path, response );
    }

    @GET
    @Operation(description = "/config/invalidate/datasources/tile/id/matrixset[?bbox=] - invalidate part or all of a tile store cache's tile matrix set")
    @Path("/invalidate/datasources/tile/id/matrixset")
    public void invalidate( @Context HttpServletRequest request,
                            @Context HttpServletResponse response,
                            @PathParam("path") String path,
                            @QueryParam("bbox") String bbox )
                    throws IOException {
        token.validate( request );
        Invalidate.invalidate( path, request.getQueryString(), response );
    }

    @GET
    @Operation(description = "/config/crs/list - list available CRS definitions")
    @Path("/crs/list")
    public void crsList( @Context HttpServletRequest request,
                         @Context HttpServletResponse response,
                         @PathParam("path") String path )
                    throws IOException {
        token.validate( request );
        Crs.listCrs( response );
    }

    @POST
    @Operation(description = "/config/crs/getcodes with wkt=<wkt> - retrieves a list of CRS codes corresponding to the WKT (POSTed KVP)")
    @Path("/crs/getcodes")
    public void crsGetCodes( @Context HttpServletRequest request,
                             @Context HttpServletResponse response,
                             @PathParam("path") String path )
                    throws IOException {
        token.validate( request );
        Crs.getCodes( request, response );
    }

    @GET
    @Operation(description = "/config/crs/<code> - checks if a CRS definition is available, returns true/false")
    @Path("/crs/{code}")
    public void crsCodes( @Context HttpServletRequest request,
                          @Context HttpServletResponse response,
                          @PathParam("code") String code )
                    throws IOException {
        token.validate( request );
        Crs.checkCrs( code, response );
    }

    @GET
    @Operation(description = "/config/validate[/path] - validate currently running workspace or file in workspace\n"
                             + "/config/validate/wsname[/path] - validate workspace with name <wsname> or file in workspace")
    @Path("/validate{path : (.+)?}")
    public void validate( @Context HttpServletRequest request,
                          @Context HttpServletResponse response,
                          @PathParam("path") String path )
                    throws IOException {
        token.validate( request );
        Validate.validate( path, response );
    }

    @PUT
    @Operation(description = "/config/upload/wsname.zip - upload workspace <wsname>\n"
                             + "/config/upload/path/file - upload file into current workspace\n"
                             + "/config/upload/wsname/path/file - upload file into workspace with name <wsname>")
    @Path("/upload{path : (.+)?}")
    public void upload( @Context HttpServletRequest request,
                        @Context HttpServletResponse response,
                        @PathParam("path") String path )
                    throws IOException {
        token.validate( request );
        Upload.upload( path, request, response );
    }

    @DELETE
    @Operation(description = "/config/delete[/path] - delete currently running workspace or file in workspace\n"
                             + "/config/delete/wsname[/path] - delete workspace with name <wsname> or file in workspace")
    @Path("/delete{path : (.+)?}")
    public void delete( @Context HttpServletRequest request,
                        @Context HttpServletResponse response,
                        @PathParam("path") String path )
                    throws IOException {
        token.validate( request );
        Delete.delete( path, response );
    }
}