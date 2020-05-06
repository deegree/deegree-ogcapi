package org.deegree.services.oaf.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Variant;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_XML_TYPE;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GEOJSON_TYPE;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_32_TYPE;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_SF0_TYPE;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_SF2_TYPE;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_TYPE;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_OPENAPI_TYPE;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class ExceptionMediaTypeUtil {

    /**
     * Selects the best matching media types (application/json or application/xml) supporting exception messaged by the requested media type.
     *
     * @param request
     *                 never <code>null</code>
     * @return the best matching media type (application/json or application/xml), never <code>null</code>
     */
    public static MediaType selectMediaType( Request request ) {
        List<Variant> variants = Variant.mediaTypes( APPLICATION_JSON_TYPE, APPLICATION_OPENAPI_TYPE,
                                                     APPLICATION_GEOJSON_TYPE, APPLICATION_XML_TYPE,
                                                     APPLICATION_GML_TYPE, APPLICATION_GML_32_TYPE,
                                                     APPLICATION_GML_SF0_TYPE, APPLICATION_GML_SF2_TYPE ).add().build();
        Variant selected = request.selectVariant( variants );
        if ( selected == null )
            return APPLICATION_JSON_TYPE;
        MediaType mediaType = selected.getMediaType();
        if ( APPLICATION_XML_TYPE.equals( mediaType ) || APPLICATION_GML_TYPE.equals( mediaType )
             || APPLICATION_GML_32_TYPE.equals( mediaType ) || APPLICATION_GML_SF0_TYPE.equals( mediaType )
             || APPLICATION_GML_SF2_TYPE.equals( mediaType ) )
            return APPLICATION_XML_TYPE;

        return APPLICATION_JSON_TYPE;
    }
}