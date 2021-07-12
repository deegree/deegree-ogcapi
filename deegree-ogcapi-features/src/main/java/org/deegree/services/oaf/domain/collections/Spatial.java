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
package org.deegree.services.oaf.domain.collections;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_CORE_NS_URL;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@XmlRootElement(name = "SpatialExtent", namespace = XML_CORE_NS_URL)
@XmlAccessorType(XmlAccessType.FIELD)
public class Spatial {

    @XmlTransient
    private List<List<Double>> bbox = new ArrayList<List<Double>>();

    @XmlAttribute
    private String crs;

    public Spatial() {
    }

    public Spatial( List<List<Double>> bbox, String crs ) {
        this.bbox = bbox;
        this.crs = crs;
    }

    public List<List<Double>> getBbox() {
        return bbox;
    }

    public void setBbox( List<List<Double>> bbox ) {
        this.bbox = bbox;
    }

    public String getCrs() {
        return crs;
    }

    public void setCrs( String crs ) {
        this.crs = crs;
    }

    @JsonIgnore
    @XmlList
    @XmlElement(name = "LowerCorner", namespace = XML_CORE_NS_URL)
    public List<Double> getLowerCorner() {
        if ( bbox == null || bbox.size() < 1 || bbox.get( 0 ).size() < 4 )
            return null;
        return bbox.get( 0 ).subList( 0, 2 );
    }

    @JsonIgnore
    @XmlList
    @XmlElement(name = "UpperCorner", namespace = XML_CORE_NS_URL)
    public List<Double> getUpperCorner() {
        if ( bbox == null || bbox.size() < 1 || bbox.get( 0 ).size() < 4 )
            return null;
        return bbox.get( 0 ).subList( 2, 4 );
    }
}
