package org.deegree.services.oaf;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public final class OgcApiFeaturesConstants {

    public static final String DEFAULT_CRS = "http://www.opengis.net/def/crs/OGC/1.3/CRS84";

    public static final String XML_CORE_NS_URL = "http://www.opengis.net/ogcapi-features-1/1.0";

    public static final String XML_ATOM_NS_URL = "http://www.w3.org/2005/Atom";

    public static final String XML_SF_NS_URL = "http://www.opengis.net/ogcapi-features-1/1.0/sf";

    public static final String XML_CORE_SCHEMA_URL = "http://schemas.opengis.net/ogcapi/features/part1/1.0/xml/core.xsd";

    public static final String XML_SF_SCHEMA_URL = "http://schemas.opengis.net/ogcapi/features/part1/1.0/xml/core-sf.xsd";

    public static final String HEADER_TIMESTAMP = "Date";

    public static final String HEADER_NUMBER_RETURNED = "OGC-NumberReturned";

    public static final String HEADER_NUMBER_MATCHED ="OGC-NumberMatched";

    public static final String HEADER_Link ="Link";

    private OgcApiFeaturesConstants() {
    }

}