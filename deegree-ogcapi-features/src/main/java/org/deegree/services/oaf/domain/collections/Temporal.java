package org.deegree.services.oaf.domain.collections;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_CORE_NS_URL;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@XmlRootElement(name = "TemporalExtent", namespace = XML_CORE_NS_URL)
@XmlAccessorType(XmlAccessType.FIELD)
public class Temporal {

    @XmlTransient
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private List<Date> interval;

    @XmlAttribute
    @JsonInclude(NON_NULL)
    private String trs;

    public Temporal() {
    }

    public Temporal( List<Date> interval, String trs ) {
        this.interval = interval;
        this.trs = trs;
    }

    public List<Date> getInterval() {
        return interval;
    }

    public void setInterval( List<Date> interval ) {
        this.interval = interval;
    }

    public String getTrs() {
        return trs;
    }

    public void setTrs( String trs ) {
        this.trs = trs;
    }

    @JsonIgnore
    @XmlElement(name = "begin", namespace = XML_CORE_NS_URL)
    public Date getBegin() {
        if ( interval == null || interval.size() < 2 )
            return null;
        return interval.get( 0 );
    }

    @JsonIgnore
    @XmlElement(name = "end", namespace = XML_CORE_NS_URL)
    public Date getEnd() {
        if ( interval == null || interval.size() < 2 )
            return null;
        return interval.get( 1 );
    }

}