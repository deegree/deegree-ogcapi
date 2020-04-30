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

    public OgcApiFeaturesExceptionReport( String exceptionText, int exceptionCode ) {
        this.oafExceptionText = new OgcApiFeaturesExceptionText( exceptionText, Integer.toString( exceptionCode ) );
    }

    public OgcApiFeaturesExceptionText getOafExceptionText() {
        return oafExceptionText;
    }

    public void setOafExceptionText( OgcApiFeaturesExceptionText oafExceptionText ) {
        this.oafExceptionText = oafExceptionText;
    }

}