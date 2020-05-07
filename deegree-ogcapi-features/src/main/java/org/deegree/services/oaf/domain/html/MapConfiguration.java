package org.deegree.services.oaf.domain.html;

/**
 * Encapsulates zhe configuration of the map
 */
public class MapConfiguration {

    private final String wmsUrl;

    private final String wmsLayers;

    private final String crsCode;

    private final String crsProj4Definition;

    public MapConfiguration( String wmsUrl, String wmsLayers, String crsCode, String crsProj4Definition ) {
        this.wmsUrl = wmsUrl;
        this.wmsLayers = wmsLayers;
        this.crsCode = crsCode;
        this.crsProj4Definition = crsProj4Definition;
    }

    public String getWmsUrl() {
        return wmsUrl;
    }

    public String getWmsLayers() {
        return wmsLayers;
    }

    public String getCrsCode() {
        return crsCode;
    }

    public String getCrsProj4Definition() {
        return crsProj4Definition;
    }
}
