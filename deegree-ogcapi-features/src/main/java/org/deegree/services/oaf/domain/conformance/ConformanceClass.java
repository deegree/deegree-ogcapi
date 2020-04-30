package org.deegree.services.oaf.domain.conformance;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public enum ConformanceClass {

    /* Core */
    CORE( "http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/core" ),

    /* OpenAPI 3.0 */
    OPENAPI30( "http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/oas30" ),

    /* HTML */
    HTML( "http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/html" ),

    /* GeoJSON */
    GEOJSON( "http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/geojson" ),

    /* GML Simple Features Level 0 */
    GMLSF0("http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/gmlsf0"),

    /* GML Simple Features Level 2 */
    GMLSF2("http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/gmlsf2");

    private final String conformanceClass;

    ConformanceClass( String conformanceClass ) {
        this.conformanceClass = conformanceClass;
    }

    public String getConformanceClass() {
        return this.conformanceClass;
    }
}