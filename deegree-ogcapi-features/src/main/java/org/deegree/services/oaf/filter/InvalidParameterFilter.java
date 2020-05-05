package org.deegree.services.oaf.filter;

import org.deegree.services.oaf.domain.exceptions.OgcApiFeaturesExceptionReport;
import org.deegree.services.oaf.resource.Features;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.deegree.services.oaf.exceptions.ExceptionMediaTypeUtil.selectMediaType;

/**
 * Checks the passed query parameters. If an query parameter has an invalid value the request aborts with a BAD_REQUEST.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Provider
public class InvalidParameterFilter implements ContainerRequestFilter {

    private static final String EXCEPTION_MSG = "Parameter with name '%s' has invalid content '%s'.";

    @Context
    private ResourceInfo resourceInfo;

    @Context
    private Request request;

    @Override
    public void filter( ContainerRequestContext requestContext )
                    throws IOException {
        if ( resourceInfo.getResourceClass().isAssignableFrom( Features.class ) ) {
            MultivaluedMap<String, String> queryParameters = requestContext.getUriInfo().getQueryParameters();
            boolean isValid = validateInteger( requestContext, "limit", queryParameters );
            if ( isValid ) {
                validateInteger( requestContext, "offset", queryParameters );
            }
        }
    }

    private boolean validateInteger( ContainerRequestContext requestContext, String paramKey,
                                     MultivaluedMap<String, String> queryParameters ) {
        List<String> queryParam = queryParameters.get( paramKey );
        if ( queryParam != null ) {
            try {
                queryParam.forEach( s -> {
                    Integer.valueOf( s );
                } );
            } catch ( NumberFormatException e ) {
                Response response = createInvalidParameterResponse( paramKey, queryParam );
                requestContext.abortWith( response );
                return false;
            }
        }
        return true;
    }

    private Response createInvalidParameterResponse( String param, List<String> queryParam ) {
        String queryParamString = queryParam.stream().collect( Collectors.joining( "," ) );
        MediaType selectedType = selectMediaType( request );
        String message = String.format( EXCEPTION_MSG, param, queryParamString );
        OgcApiFeaturesExceptionReport oafExceptionReport = new OgcApiFeaturesExceptionReport(
                        message,
                        BAD_REQUEST.getStatusCode() );
        return Response
                        .status( BAD_REQUEST )
                        .entity( oafExceptionReport )
                        .type( selectedType )
                        .build();
    }

}
