package org.deegree.services.oaf.domain.collections;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.deegree.services.oaf.domain.OafDomainResource;
import org.deegree.services.oaf.link.Link;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_ATOM_NS_URL;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_CORE_NS_URL;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@XmlRootElement(name = "Collections", namespace = XML_CORE_NS_URL)
public class Collections extends OafDomainResource {

    @XmlElement(name = "link", namespace = XML_ATOM_NS_URL)
    @JsonProperty("links")
    private List<Link> links;

    @XmlElement(name = "Collection", namespace = XML_CORE_NS_URL)
    @JsonProperty("collections")
    private List<Collection> collections;

    public Collections() {
    }

    public Collections( List<Link> links, List<Collection> collections ) {
        this.links = links;
        this.collections = collections;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks( List<Link> links ) {
        this.links = links;
    }

    public List<Collection> getCollections() {
        return collections;
    }

    public void setCollections( List<Collection> collections ) {
        this.collections = collections;
    }
}
