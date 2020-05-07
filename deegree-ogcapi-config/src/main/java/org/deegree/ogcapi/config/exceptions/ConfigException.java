package org.deegree.ogcapi.config.exceptions;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public abstract class ConfigException extends Exception {

    protected ConfigException( String msg ) {
        super( msg );
    }

    /**
     * @return the status code of the response. INTERNAL_SERVER_ERROR per default.
     */
    protected Response.Status getStatusCode() {
        return INTERNAL_SERVER_ERROR;
    }

    /**
     * @return the media type of the response. TEXT_PLAIN per default.
     */
    protected String getType() {
        return TEXT_PLAIN;
    }
}