package org.deegree.ogcapi.config.exceptions;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class RestartException extends ConfigException {

    private static final String EXCEPTION_MSG = "Error while reloading: '%s'";

    public RestartException( Exception reason ) {
        super( String.format( EXCEPTION_MSG, reason ) );
    }

}