package org.deegree.ogcapi.config.exceptions;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class InvalidWorkspaceException extends ConfigException {

    private static final String EXCEPTION_MSG = "No such workspace: '%s'";

    public InvalidWorkspaceException( String workspaceName ) {
        super( String.format( EXCEPTION_MSG, workspaceName ) );
    }

    @Override
    protected Response.Status getStatusCode() {
        return NOT_FOUND;
    }

}
