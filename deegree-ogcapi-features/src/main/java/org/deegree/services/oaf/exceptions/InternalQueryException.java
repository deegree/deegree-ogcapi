package org.deegree.services.oaf.exceptions;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class InternalQueryException extends OgcApiFeaturesException {

    public InternalQueryException() {
        super();
    }

    public InternalQueryException( Throwable e ) {
        super( e );
    }

    public InternalQueryException( String msg ) {
        super( msg );
    }

    @Override
    public Response.Status getStatusCode() {
        return INTERNAL_SERVER_ERROR;
    }

}
