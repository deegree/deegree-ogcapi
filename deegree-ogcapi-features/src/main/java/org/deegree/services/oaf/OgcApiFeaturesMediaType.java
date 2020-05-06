package org.deegree.services.oaf;

import javax.ws.rs.core.MediaType;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;

/**
 * Defines the mime types specified by
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public final class OgcApiFeaturesMediaType {

    public static final String APPLICATION_OPENAPI = "application/vnd.oai.openapi+json;version=3.0";
    public static final MediaType APPLICATION_OPENAPI_TYPE = new MediaType("application", "vnd.oai.openapi+json;version=3.0");

    public static final String APPLICATION_GEOJSON = "application/geo+json";
    public static final MediaType APPLICATION_GEOJSON_TYPE = new MediaType("application", "geo+json");

    public static final String APPLICATION_GML = "application/gml+xml";
    public static final MediaType APPLICATION_GML_TYPE = new MediaType("application", "gml+xml");

    public static final String APPLICATION_GML_32 = "application/gml+xml;version=3.2";
    public static final MediaType APPLICATION_GML_32_TYPE;

    public static final String APPLICATION_GML_SF0 = "application/gml+xml;version=3.2;profile=\"http://www.opengis.net/def/profile/ogc/2.0/gml-sf0\"";

    public static final MediaType APPLICATION_GML_SF0_TYPE;

    public static final String APPLICATION_GML_SF2 = "application/gml+xml;version=3.2;profile=\"http://www.opengis.net/def/profile/ogc/2.0/gml-sf2\"";

    public static final MediaType APPLICATION_GML_SF2_TYPE;

    static {
        Map<String, String> parametersSF0 = new HashMap<>();
        parametersSF0.put( "version", "3.2" );
        parametersSF0.put( "profile", "http://www.opengis.net/def/profile/ogc/2.0/gml-sf0" );
        APPLICATION_GML_SF0_TYPE = new MediaType( "application", "gml+xml", parametersSF0 );

        Map<String, String> parametersSF2 = new HashMap<>();
        parametersSF2.put( "version", "3.2" );
        parametersSF2.put( "profile", "http://www.opengis.net/def/profile/ogc/2.0/gml-sf2" );
        APPLICATION_GML_SF2_TYPE = new MediaType( "application", "gml+xml", parametersSF2 );

        Map<String, String> parameters32 = new HashMap<>();
        parameters32.put( "version", "3.2" );
        APPLICATION_GML_32_TYPE = new MediaType( "application", "gml+xml", parameters32 );
    }

    private OgcApiFeaturesMediaType() {
    }

}
