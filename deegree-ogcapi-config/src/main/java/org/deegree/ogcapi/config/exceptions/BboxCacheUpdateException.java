package org.deegree.ogcapi.config.exceptions;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class BboxCacheUpdateException extends ConfigException {

    private static final String EXCEPTION_MSG = "Error while updating bbox cache: '%s'";

    public BboxCacheUpdateException( Exception e ) {
        super( String.format( EXCEPTION_MSG, e ) );
    }

}
