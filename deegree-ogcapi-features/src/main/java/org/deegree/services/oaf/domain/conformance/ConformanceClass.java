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
package org.deegree.services.oaf.domain.conformance;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public enum ConformanceClass {

	/* Core */
	CORE("http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/core"),

	/* OpenAPI 3.0 */
	OPENAPI30("http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/oas30"),

	/* HTML */
	HTML("http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/html"),

	/* GeoJSON */
	GEOJSON("http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/geojson"),

	/* GML Simple Features Level 0 */
	GMLSF0("http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/gmlsf0"),

	/* GML Simple Features Level 2 */
	GMLSF2("http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/gmlsf2"),

	/* Part 2 - CRS */
	CRS("http://www.opengis.net/spec/ogcapi-features-2/1.0/conf/crs");

	private final String conformanceClass;

	ConformanceClass(String conformanceClass) {
		this.conformanceClass = conformanceClass;
	}

	public String getConformanceClass() {
		return this.conformanceClass;
	}

}
