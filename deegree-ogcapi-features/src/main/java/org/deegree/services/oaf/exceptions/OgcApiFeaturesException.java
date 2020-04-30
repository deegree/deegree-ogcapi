package org.deegree.services.oaf.exceptions;

import org.deegree.services.oaf.domain.exceptions.OgcApiFeaturesExceptionReport;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.deegree.services.oaf.exceptions.ExceptionMediaTypeUtil.selectMediaType;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public abstract class OgcApiFeaturesException extends Exception implements ExceptionMapper<OgcApiFeaturesException> {

    @Context
    Request request;

    public OgcApiFeaturesException() {
        super();
    }

    public OgcApiFeaturesException( String message ) {
        super( message );
    }

    public OgcApiFeaturesException( Throwable e ) {
        super( e );
    }

    protected abstract Response.Status getStatusCode();

    @Override
    public Response toResponse( OgcApiFeaturesException excpetion ) {
        MediaType selectedType = selectMediaType( request );

        OgcApiFeaturesExceptionReport oafExceptionReport = new OgcApiFeaturesExceptionReport( excpetion.getMessage(),
                                                                                              NOT_FOUND.getStatusCode() );

        return Response.status( getStatusCode() ).entity( oafExceptionReport ).type( selectedType ).build();
    }

}