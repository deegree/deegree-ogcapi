package org.deegree.services.oaf.domain.conformance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.deegree.services.oaf.domain.OafDomainResource;
import org.deegree.services.oaf.link.Link;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.stream.Collectors;

import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_ATOM_NS_URL;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_CORE_NS_URL;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@XmlRootElement(name = "ConformsTo", namespace = XML_CORE_NS_URL)
public class Conformance extends OafDomainResource {

    @XmlElement(name = "link", namespace = XML_ATOM_NS_URL)
    @JsonIgnore
    private List<Link> links;

    public Conformance() {
    }

    public Conformance( List<Link> links ) {
        this.links = links;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks( List<Link> links ) {
        this.links = links;
    }

    @JsonProperty
    public List<String> getConformsTo() {
        return links.stream().map( link -> link.getHref() ).collect( Collectors.toList() );
    }

}