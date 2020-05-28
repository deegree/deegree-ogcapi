package org.deegree.services.oaf.exceptions;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class UnknownDatasetId extends OgcApiFeaturesException {

    private static final String EXCEPTION_MSG = "A dataset with id '%s' is not available.";

    public UnknownDatasetId() {
        super();
    }

    public UnknownDatasetId( String datasetId ) {
        super( String.format( EXCEPTION_MSG, datasetId ) );
    }

    @Override
    public Response.Status getStatusCode() {
        return NOT_FOUND;
    }

}