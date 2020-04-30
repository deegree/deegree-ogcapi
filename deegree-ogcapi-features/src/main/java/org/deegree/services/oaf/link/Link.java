package org.deegree.services.oaf.link;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_ATOM_NS_URL;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@XmlRootElement(name = "link", namespace = XML_ATOM_NS_URL)
@XmlAccessorType(XmlAccessType.FIELD)
public class Link {

    @XmlAttribute
    private String href;

    @XmlAttribute
    private String rel;

    @XmlAttribute
    private String type;

    @XmlAttribute
    private String title;

    public Link() {
    }

    public Link( String href, String rel, String type, String title ) {
        this.href = href;
        this.rel = rel;
        this.type = type;
        this.title = title;
    }

    public Link( String href ) {
        this.href = href;
    }

    public String getHref() {
        return href;
    }

    public void setHref( String href ) {
        this.href = href;
    }

    public String getRel() {
        return rel;
    }

    public void setRel( String rel ) {
        this.rel = rel;
    }

    public String getType() {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

}
