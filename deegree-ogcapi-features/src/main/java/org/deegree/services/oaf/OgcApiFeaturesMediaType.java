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
package org.deegree.services.oaf;

import javax.ws.rs.core.MediaType;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the mime types specified by
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public final class OgcApiFeaturesMediaType {

    public static final String APPLICATION_OPENAPI = "application/vnd.oai.openapi+json;version=3.0";
    public static final MediaType APPLICATION_OPENAPI_TYPE = new MediaType("application", "vnd.oai.openapi+json;version=3.0");
    
    public static final String APPLICATION_OPENAPI_YAML = "application/vnd.oai.openapi+yaml;version=3.0";
    public static final MediaType APPLICATION_OPENAPI_YAML_TYPE = new MediaType("application", "vnd.oai.openapi+yaml;version=3.0");
    
    // Note: There is no registered media type yet, but this is the latest proposal
    // See https://github.com/ietf-wg-httpapi/mediatypes/blob/main/draft-ietf-httpapi-yaml-mediatypes.md
    public static final String APPLICATION_YAML = "application/yaml";
    public static final MediaType APPLICATION_YAML_TYPE = new MediaType("application", "yaml");

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
