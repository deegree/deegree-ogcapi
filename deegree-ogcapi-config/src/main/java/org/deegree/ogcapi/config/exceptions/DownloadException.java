package org.deegree.ogcapi.config.exceptions;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class DownloadException extends ConfigException {

    private static final String EXCEPTION_MSG = "Error while downloading: '%s'";

    public DownloadException( Exception reason ) {
        super( String.format( EXCEPTION_MSG, reason ) );
    }

}