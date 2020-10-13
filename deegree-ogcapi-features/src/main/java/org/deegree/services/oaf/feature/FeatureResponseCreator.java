/*-
 * #%L
 * deegree-ogcapi-features - OGC API Features (OAF) implementation - Querying and modifying of geospatial data objects
 * %%
 * Copyright (C) 2019 - 2020 lat/lon GmbH, info@lat-lon.de, www.lat-lon.de
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package org.deegree.services.oaf.feature;

import org.deegree.services.oaf.link.Link;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.Map;

import static org.deegree.services.oaf.OgcApiFeaturesConstants.HEADER_Link;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.HEADER_NUMBER_MATCHED;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.HEADER_NUMBER_RETURNED;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.HEADER_TIMESTAMP;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_32;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_SF0;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_SF0_TYPE;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_SF2;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_SF2_TYPE;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class FeatureResponseCreator {

    /**
     * Creates a response with the expected HTTP Headers
     *
     * @param featureResponse
     *                 never <code>null</code>
     * @param acceptHeader
     *                 of the request, may be <code>null</code>
     * @return never <code>null</code>
     */
    public Response createGmlResponseWithHeaders( FeatureResponse featureResponse,
                                                  String acceptHeader ) {

        String mediaType = detectMediaType( acceptHeader );
        Response.ResponseBuilder response = Response.ok( featureResponse, mediaType );
        response.header( HEADER_TIMESTAMP, new Date() );
        response.header( HEADER_NUMBER_RETURNED, featureResponse.getNumberOfFeatures() );
        response.header( HEADER_NUMBER_MATCHED, featureResponse.getNumberOfFeaturesMatched() );
        featureResponse.getLinks().forEach( link -> {
            response.header( HEADER_Link, asString( link ) );
        } );
        return response.build();
    }

    private String detectMediaType( String acceptHeader ) {
        if ( acceptHeader == null || acceptHeader.isEmpty() )
            return APPLICATION_GML;
        MediaType mediaType = MediaType.valueOf( acceptHeader );
        Map<String, String> parameters = mediaType.getParameters();
        if ( parameters.isEmpty() )
            return APPLICATION_GML;
        String profileValue = parameters.get( "profile" );
        if ( profileValue != null && !profileValue.isEmpty() ) {
            if ( APPLICATION_GML_SF0_TYPE.getParameters().get( "profile" ).equals( profileValue ) )
                return APPLICATION_GML_SF0;
            else if ( APPLICATION_GML_SF2_TYPE.getParameters().get( "profile" ).equals( profileValue ) )
                return APPLICATION_GML_SF2;
        }
        return APPLICATION_GML_32;
    }

    private String asString( Link link ) {
        StringBuilder linkBuilder = new StringBuilder();
        linkBuilder.append( "<" ).append( link.getHref() ).append( ">; " );
        linkBuilder.append( "rel=\"" ).append( link.getRel() ).append( "\"; " );
        linkBuilder.append( "title=\"" ).append( link.getTitle() ).append( "\"; " );
        linkBuilder.append( "type=\"" ).append( link.getType() ).append( "\"" );
        return linkBuilder.toString();
    }

}
