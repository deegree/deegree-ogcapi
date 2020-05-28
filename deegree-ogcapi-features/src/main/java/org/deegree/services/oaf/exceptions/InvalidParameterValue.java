package org.deegree.services.oaf.exceptions;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class InvalidParameterValue extends OgcApiFeaturesException {

    private static final String EXCEPTION_MSG = "Parameter '%s has invalid content: ";

    public InvalidParameterValue() {
    }

    public InvalidParameterValue( String parameterName, String reason ) {
        super( String.format( EXCEPTION_MSG, parameterName, reason ) );
    }

    @Override
    public Response.Status getStatusCode() {
        return BAD_REQUEST;
    }

}
