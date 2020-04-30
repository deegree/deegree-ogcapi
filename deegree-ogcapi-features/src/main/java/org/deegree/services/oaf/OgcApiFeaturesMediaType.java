package org.deegree.services.oaf;

import javax.ws.rs.core.MediaType;
import java.util.Collections;

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

    public static final MediaType APPLICATION_GML_SF0_TYPE = new MediaType( "application", "gml+xml",
                                                                            singletonMap( "profile",
                                                                                          "http://www.opengis.net/def/profile/ogc/2.0/gml-sf0" ) );

    public static final MediaType APPLICATION_GML_SF2_TYPE = new MediaType( "application", "gml+xml",
                                                                            singletonMap( "profile",
                                                                                          "http://www.opengis.net/def/profile/ogc/2.0/gml-sf2" ) );

    private OgcApiFeaturesMediaType() {
    }

}
