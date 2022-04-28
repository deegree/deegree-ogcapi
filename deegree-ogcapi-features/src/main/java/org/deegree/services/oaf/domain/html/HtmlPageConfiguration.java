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
package org.deegree.services.oaf.domain.html;

/**
 * Encapsulates the configuration of the legal notice
 */
public class HtmlPageConfiguration {

    private final String legalNoticeUrl;

    private final String privacyUrl;

    private final String documentationUrl;

    public HtmlPageConfiguration( String legalNoticeUrl, String privacyUrl, String documentationUrl ) {
        this.legalNoticeUrl = legalNoticeUrl;
        this.privacyUrl = privacyUrl;
        this.documentationUrl = documentationUrl;
    }

    public String getLegalNoticeUrl() {
        return legalNoticeUrl;
    }

    public String getPrivacyUrl() {
        return privacyUrl;
    }

    public String getDocumentationUrl() {
        return documentationUrl;
    }

    @Override
    public String toString() {
        return "HtmlPageConfiguration{" +
                "legalNoticeUrl='" + legalNoticeUrl + '\'' +
                ", privacyUrl='" + privacyUrl + '\'' +
                ", documentationUrl='" + documentationUrl + '\'' +
                '}';
    }
}
