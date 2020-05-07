package org.deegree.ogcapi.config.exceptions;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class ValidationException extends ConfigException {

    private static final String EXCEPTION_MSG = "Error while validating: '%s'";

    public ValidationException( Exception e ) {
        super( String.format( EXCEPTION_MSG, e ) );
    }

}
