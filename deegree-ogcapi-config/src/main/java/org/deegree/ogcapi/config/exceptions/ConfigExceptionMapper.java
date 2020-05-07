package org.deegree.ogcapi.config.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Provider
public class ConfigExceptionMapper implements ExceptionMapper<ConfigException> {

    @Override
    public Response toResponse( ConfigException e ) {
        return Response.status( e.getStatusCode() ).entity( e.getLocalizedMessage() ).type( e.getType() ).build();
    }

}
