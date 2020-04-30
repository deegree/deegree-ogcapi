package org.deegree.services.oaf.domain.collections;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_CORE_NS_URL;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@XmlRootElement(name = "Extent", namespace = XML_CORE_NS_URL)
@XmlAccessorType(XmlAccessType.FIELD)
public class Extent {

    @XmlElement(name = "Spatial", namespace = XML_CORE_NS_URL)
    private Spatial spatial;

    @XmlElement(name = "Temporal", namespace = XML_CORE_NS_URL)
    @JsonInclude(NON_NULL)
    private Temporal temporal;

    public Spatial getSpatial() {
        return spatial;
    }

    public void setSpatial( Spatial spatial ) {
        this.spatial = spatial;
    }

    public Temporal getTemporal() {
        return temporal;
    }

    public void setTemporal( Temporal temporal ) {
        this.temporal = temporal;
    }

}
