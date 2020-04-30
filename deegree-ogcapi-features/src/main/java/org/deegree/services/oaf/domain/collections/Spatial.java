package org.deegree.services.oaf.domain.collections;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_CORE_NS_URL;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@XmlRootElement(name = "SpatialExtent", namespace = XML_CORE_NS_URL)
@XmlAccessorType(XmlAccessType.FIELD)
public class Spatial {

    @XmlTransient
    private List<Double> bbox;

    @XmlAttribute
    private String crs;

    public Spatial() {
    }

    public Spatial( List<Double> bbox, String crs ) {
        this.bbox = bbox;
        this.crs = crs;
    }

    public List<Double> getBbox() {
        return bbox;
    }

    public void setBbox( List<Double> bbox ) {
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
        if ( bbox == null || bbox.size() < 4 )
            return null;
        return bbox.subList( 0, 2 );
    }

    @JsonIgnore
    @XmlList
    @XmlElement(name = "UpperCorner", namespace = XML_CORE_NS_URL)
    public List<Double> getUpperCorner() {
        if ( bbox == null || bbox.size() < 4 )
            return null;
        return bbox.subList( 2, 4 );
    }
}