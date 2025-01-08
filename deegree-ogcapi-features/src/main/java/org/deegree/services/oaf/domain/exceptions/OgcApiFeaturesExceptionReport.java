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
package org.deegree.services.oaf.domain.exceptions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_CORE_NS_URL;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@XmlRootElement(name = "ExceptionReport", namespace = XML_CORE_NS_URL)
@XmlAccessorType(XmlAccessType.FIELD)
public class OgcApiFeaturesExceptionReport {

	@XmlAttribute
	private String version = "1.0.0";

	@XmlElement(name = "Exception", namespace = XML_CORE_NS_URL)
	public OgcApiFeaturesExceptionText oafExceptionText;

	public OgcApiFeaturesExceptionReport() {
	}

	public OgcApiFeaturesExceptionReport(String exceptionText, int exceptionCode) {
		this.oafExceptionText = new OgcApiFeaturesExceptionText(exceptionText, Integer.toString(exceptionCode));
	}

	public OgcApiFeaturesExceptionText getOafExceptionText() {
		return oafExceptionText;
	}

	public void setOafExceptionText(OgcApiFeaturesExceptionText oafExceptionText) {
		this.oafExceptionText = oafExceptionText;
	}

}
