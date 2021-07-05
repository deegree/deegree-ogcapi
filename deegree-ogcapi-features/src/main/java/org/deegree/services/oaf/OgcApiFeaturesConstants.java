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

    public static final String HEADER_LINK ="Link";

    public static final String XML_SF_NS_SCHEMA_LOCATION = "http://schemas.opengis.net/ogcapi/features/part1/1.0/xml/core-sf.xsd";

    private OgcApiFeaturesConstants() {
    }

}
