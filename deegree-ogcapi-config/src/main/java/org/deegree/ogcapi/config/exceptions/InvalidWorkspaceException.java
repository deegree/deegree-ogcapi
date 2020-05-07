package org.deegree.ogcapi.config.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Provider
public class InvalidWorkspaceException extends Exception implements ExceptionMapper<InvalidWorkspaceException> {

    private static final String EXCEPTION_MSG = "No such workspace: '%s'";

    public InvalidWorkspaceException( String workspaceName ) {
        super( String.format( EXCEPTION_MSG, workspaceName ) );
    }

    @Override
    public Response toResponse( InvalidWorkspaceException e ) {
        return Response.status( NOT_FOUND ).entity( e.getLocalizedMessage() ).type( TEXT_PLAIN ).build();
    }
}
