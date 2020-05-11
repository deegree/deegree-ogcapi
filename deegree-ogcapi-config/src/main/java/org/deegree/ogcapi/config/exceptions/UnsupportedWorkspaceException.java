package org.deegree.ogcapi.config.exceptions;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class UnsupportedWorkspaceException extends ConfigException {

    private static final String EXCEPTION_MSG = "Unsupported workspace: '%s' . Workspace must be: 'ogcapi-workspace'";

    public UnsupportedWorkspaceException( String workspaceName ) {
        super( String.format( EXCEPTION_MSG, workspaceName ) );
    }

    @Override
    protected Response.Status getStatusCode() {
        return NOT_FOUND;
    }

}