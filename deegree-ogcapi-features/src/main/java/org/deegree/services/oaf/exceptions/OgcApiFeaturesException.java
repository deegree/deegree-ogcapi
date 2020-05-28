package org.deegree.services.oaf.exceptions;

import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public abstract class OgcApiFeaturesException extends Exception {

    public OgcApiFeaturesException() {
        super();
    }

    public OgcApiFeaturesException( String message ) {
        super( message );
    }

    public OgcApiFeaturesException( Throwable e ) {
        super( e );
    }

    public abstract Response.Status getStatusCode();

}