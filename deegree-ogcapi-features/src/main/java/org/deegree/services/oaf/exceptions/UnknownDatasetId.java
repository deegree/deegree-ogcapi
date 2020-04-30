package org.deegree.services.oaf.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Provider
public class UnknownDatasetId extends OgcApiFeaturesException {

    private static final String EXCEPTION_MSG = "A dataset with id '%s' is not available.";

    public UnknownDatasetId() {
    }

    public UnknownDatasetId( String datasetId ) {
        super( String.format( EXCEPTION_MSG, datasetId ) );
    }

    @Override
    protected Response.Status getStatusCode() {
        return NOT_FOUND;
    }

}