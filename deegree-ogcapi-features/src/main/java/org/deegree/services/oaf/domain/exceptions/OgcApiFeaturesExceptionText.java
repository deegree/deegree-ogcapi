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
@XmlRootElement(name = "Exception", namespace = XML_CORE_NS_URL)
@XmlAccessorType(XmlAccessType.FIELD)
public class OgcApiFeaturesExceptionText {

    @XmlElement(name = "ExceptionText", namespace = XML_CORE_NS_URL)
    private String exceptionText;

    @XmlAttribute
    private String exceptionCode;

    public OgcApiFeaturesExceptionText() {
    }

    public OgcApiFeaturesExceptionText( String exceptionText, String exceptionCode ) {
        this.exceptionText = exceptionText;
        this.exceptionCode = exceptionCode;
    }

    public String getExceptionText() {
        return exceptionText;
    }

    public void setExceptionText( String exceptionText ) {
        this.exceptionText = exceptionText;
    }

    public String getExceptionCode() {
        return exceptionCode;
    }

    public void setExceptionCode( String exceptionCode ) {
        this.exceptionCode = exceptionCode;
    }
}
