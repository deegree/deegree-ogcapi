package org.deegree.services.oaf.feature;

import org.deegree.services.oaf.link.Link;

import javax.ws.rs.core.Response;
import java.util.Date;

import static org.deegree.services.oaf.OgcApiFeaturesConstants.HEADER_Link;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.HEADER_NUMBER_MATCHED;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.HEADER_NUMBER_RETURNED;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.HEADER_TIMESTAMP;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class FeatureResponseUtil {

    private FeatureResponseUtil() {
    }

    /**
     * Creates a response with the expected HTTP Headers
     *
     * @param featureResponse
     *                 never <code>null</code>
     * @return never <code>null</code>
     */
    public static Response createGmlResponseWithHeaders( FeatureResponse featureResponse ) {
        Response.ResponseBuilder response = Response.ok( featureResponse, APPLICATION_GML );
        response.header( HEADER_TIMESTAMP, new Date() );
        response.header( HEADER_NUMBER_RETURNED, featureResponse.getNumberOfFeatures() );
        response.header( HEADER_NUMBER_MATCHED, featureResponse.getNumberOfFeaturesMatched() );
        featureResponse.getLinks().forEach( link -> {
            response.header( HEADER_Link, asString( link ) );
        } );
        return response.build();
    }

    private static String asString( Link link ) {
        StringBuilder linkBuilder = new StringBuilder();
        linkBuilder.append( "<" ).append( link.getHref() ).append( ">; " );
        linkBuilder.append( "rel=\"" ).append( link.getRel() ).append( "\"; " );
        linkBuilder.append( "title=\"" ).append( link.getTitle() ).append( "\"; " );
        linkBuilder.append( "type=\"" ).append( link.getType() ).append( "\"" );
        return linkBuilder.toString();
    }

}
