package org.deegree.services.oaf.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Provider
public class InvalidConfigurationException extends Exception implements ExceptionMapper<InvalidConfigurationException> {

    public InvalidConfigurationException() {
        super();
    }

    public InvalidConfigurationException( String message ) {
        super( String.format( message ) );
    }

    public InvalidConfigurationException( String message, Exception e ) {
        super( message, e );
    }

    @Override
    public Response toResponse( InvalidConfigurationException exception ) {
        return Response.status( BAD_REQUEST ).entity( exception.getMessage() ).type( TEXT_PLAIN ).build();
    }

}
