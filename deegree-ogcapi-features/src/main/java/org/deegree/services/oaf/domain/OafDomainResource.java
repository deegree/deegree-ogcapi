package org.deegree.services.oaf.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class OafDomainResource {

    @XmlAttribute
    @JsonIgnore
    private String service = "OGCAPI-Features";

    @XmlAttribute
    @JsonIgnore
    private String version = "1.0.0";

    public String getService() {
        return service;
    }

    public void setService( String service ) {
        this.service = service;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion( String version ) {
        this.version = version;
    }
}