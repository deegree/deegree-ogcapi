package org.deegree.services.oaf.domain.landingpage;

import org.deegree.services.oaf.domain.OafDomainResource;
import org.deegree.services.oaf.link.Link;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_ATOM_NS_URL;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_CORE_NS_URL;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@XmlRootElement(name = "LandingPage", namespace = XML_CORE_NS_URL)
public class LandingPage extends OafDomainResource {

    @XmlElement(name = "Title", namespace = XML_CORE_NS_URL)
    private String title;

    @XmlElement(name = "Description", namespace = XML_CORE_NS_URL)
    private String description;

    @XmlTransient
    private Contact contact;

    @XmlElement(name = "link", namespace = XML_ATOM_NS_URL)
    private List<Link> links;

    public LandingPage() {
    }

    public LandingPage( String title, String description, List<Link> links ) {
        this.title = title;
        this.description = description;
        this.links = links;
    }

    public LandingPage( String title, String description, Contact contact, List<Link> links ) {
        this.title = title;
        this.description = description;
        this.contact = contact;
        this.links = links;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact( Contact contact ) {
        this.contact = contact;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks( List<Link> links ) {
        this.links = links;
    }
}