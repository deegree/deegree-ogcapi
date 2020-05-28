package org.deegree.services.oaf.exceptions;

import org.deegree.services.oaf.domain.exceptions.OgcApiFeaturesExceptionReport;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.deegree.services.oaf.exceptions.ExceptionMediaTypeUtil.selectMediaType;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Provider
public class OgcApiFeaturesExceptionMapper implements ExceptionMapper<OgcApiFeaturesException> {

    @Context
    Request request;

    @Override
    public Response toResponse( OgcApiFeaturesException exception ) {
        MediaType selectedType = selectMediaType( request );

        OgcApiFeaturesExceptionReport oafExceptionReport = new OgcApiFeaturesExceptionReport( exception.getMessage(),
                                                                                              NOT_FOUND.getStatusCode() );

        return Response.status( exception.getStatusCode() ).entity( oafExceptionReport ).type( selectedType ).build();
    }

}