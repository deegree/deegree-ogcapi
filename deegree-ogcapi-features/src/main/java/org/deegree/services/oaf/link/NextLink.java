package org.deegree.services.oaf.link;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class NextLink {

    private final int numberOfFeaturesMatched;

    private final int requestLimit;

    private final int requestOffset;

    public NextLink( int numberOfFeaturesMatched, int requestLimit, int requestOffset ) {
        this.numberOfFeaturesMatched = numberOfFeaturesMatched;
        this.requestLimit = requestLimit;
        this.requestOffset = requestOffset;
    }

    public String createUri( UriInfo uriInfo ) {
        int newOffset = this.requestOffset + this.requestLimit;
        if ( newOffset >= numberOfFeaturesMatched )
            return null;
        UriBuilder requestUriBuilder = uriInfo.getRequestUriBuilder();
        requestUriBuilder.replaceQueryParam( "offset", newOffset );
        requestUriBuilder.replaceQueryParam( "limit", requestLimit );
        return requestUriBuilder.build().toString();
    }

}