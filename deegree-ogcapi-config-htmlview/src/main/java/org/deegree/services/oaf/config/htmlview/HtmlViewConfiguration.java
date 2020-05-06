package org.deegree.services.oaf.config.htmlview;

import java.io.File;

/**
 * Encapsulates the configuration of the HtmlView
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class HtmlViewConfiguration {

    private final File cssFile;

    private final String impressumUrl;

    private final String wmsUrl;

    private final String wmsLayers;

    private final String crsCode;

    private final String crsProj4Definition;

    /**
     * @param cssFile
     *                 must be an file and must exist, may be <code>null</code>
     * @param impressumUrl
     *                 the URL to retrieve the impressum, may be <code>null</code>
     * @param wmsUrl
     *                 the URL of the WMS, may be <code>null</code>
     * @param wmsLayers
     *                 the layers of the WMS, may be <code>null</code>
     * @param crsCode
     *                 the crs code of the map, may be <code>null</code>
     * @param crsProj4Definition
     *                 the proj4 definion of the crs of the map, may be <code>null</code>
     */
    public HtmlViewConfiguration( File cssFile, String impressumUrl, String wmsUrl, String wmsLayers, String crsCode,
                                  String crsProj4Definition ) {
        this.cssFile = cssFile;
        this.impressumUrl = impressumUrl;
        this.wmsUrl = wmsUrl;
        this.wmsLayers = wmsLayers;
        this.crsCode = crsCode;
        this.crsProj4Definition = crsProj4Definition;
    }

    /**
     * @return the configured cssFile, must be an file and must exist, may be <code>null</code>
     */
    public File getCssFile() {
        return cssFile;
    }

    /**
     * @return the URL of the WMS, may be <code>null</code>
     */
    public String getWmsUrl() {
        return wmsUrl;
    }

    /**
     * @return ths layers (comma separated) provided by the WMS, may be <code>null</code> if wmsUrl is <code>null</code>
     */
    public String getWmsLayers() {
        return wmsLayers;
    }

    /**
     * @return the crs code of the map, may be <code>null</code>
     */
    public String getCrsCode() {
        return crsCode;
    }

    /**
     * @return the proj4 definion of the crs of the map, may be <code>null</code>
     */
    public String getCrsProj4Definition() {
        return crsProj4Definition;
    }

    /**
     * @return the URL to retrieve the impressum, may be <code>null</code>
     */
    public String getImpressumUrl() {
        return impressumUrl;
    }
}