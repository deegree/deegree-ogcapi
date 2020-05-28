package org.deegree.services.oaf.exceptions;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class UnknownCollectionId extends OgcApiFeaturesException {

    private static final String EXCEPTION_MSG = "A collection with id '%s' is not available.";

    public UnknownCollectionId() {
        super();
    }

    public UnknownCollectionId( String collectionId ) {
        super( String.format( EXCEPTION_MSG, collectionId ) );
    }

    @Override
    public Response.Status getStatusCode() {
        return NOT_FOUND;
    }

}