/*-
 * #%L
 * deegree-ogcapi-config-htmlview - OGC API Features (OAF) implementation - Configuration of the HTML View
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
package org.deegree.services.oaf.config.htmlview;

import java.io.File;

/**
 * Encapsulates the configuration of the HtmlView
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class HtmlViewConfiguration {

    private final File cssFile;

    private final String legalNoticeUrl;

    private final String privacyUrl;

    private final String documentationUrl;

    private final String wmsUrl;

    private final String wmsVersion;

    private final String wmsLayers;

    private final String crsCode;

    private final String crsProj4Definition;

    /**
     * @param cssFile
     *                 must be an file and must exist, may be <code>null</code>
     * @param legalNoticeUrl
     *                 the URL to retrieve the legalNotice, may be <code>null</code>
     * @param privacyUrl
     *                 the URL to retrieve the privacy, may be <code>null</code>
     * @param documentationUrl
     *                 the URL to retrieve the documentation, may be <code>null</code>
     * @param wmsUrl
     *                 the URL of the WMS, may be <code>null</code>
     * @param wmsVersion
     *                 the WMS Version to use, may be <code>null</code>
     * @param wmsLayers
     *                 the layers of the WMS, may be <code>null</code>
     * @param crsCode
     *                 the crs code of the map, may be <code>null</code>
     * @param crsProj4Definition
     */
    public HtmlViewConfiguration( File cssFile, String legalNoticeUrl, String privacyUrl, String documentationUrl,
                                  String wmsUrl, String wmsVersion, String wmsLayers, String crsCode, String crsProj4Definition ) {
        this.cssFile = cssFile;
        this.legalNoticeUrl = legalNoticeUrl;
        this.privacyUrl = privacyUrl;
        this.documentationUrl = documentationUrl;
        this.wmsUrl = wmsUrl;
        this.wmsVersion = wmsVersion;
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
     * @return the WMS Version to use, may be <code>null</code>
     */
    public String getWmsVersion() {
        return wmsVersion;
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
     * @return the URL to retrieve the legalNotice, may be <code>null</code>
     */
    public String getLegalNoticeUrl() {
        return legalNoticeUrl;
    }

    /**
     * @return the URL to retrieve the privacy policy, may be <code>null</code>
     */
    public String getPrivacyUrl() {
        return privacyUrl;
    }

    /**
     * @return the URL to retrieve the documentation, may be <code>null</code>
     */
    public String getDocumentationUrl() {
        return documentationUrl;
    }

}
