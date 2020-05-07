package org.deegree.ogcapi.config.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Provider
public class DownloadException extends Exception implements ExceptionMapper<DownloadException> {

    private static final String EXCEPTION_MSG = "Error while downloading: '%s'";

    public DownloadException( Exception reason ) {
        super( String.format( EXCEPTION_MSG, reason ) );
    }

    @Override
    public Response toResponse( DownloadException e ) {
        return Response.status( INTERNAL_SERVER_ERROR ).entity( e.getLocalizedMessage() ).type( TEXT_PLAIN ).build();
    }
}