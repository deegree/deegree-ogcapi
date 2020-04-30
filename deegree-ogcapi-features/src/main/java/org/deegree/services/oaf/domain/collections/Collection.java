package org.deegree.services.oaf.domain.collections;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.deegree.services.oaf.link.Link;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_ATOM_NS_URL;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_CORE_NS_URL;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@XmlRootElement(name = "Collection", namespace = XML_CORE_NS_URL)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonPropertyOrder({ "id", "title", "description", "links", "extent" })
public class Collection {

    @XmlElement(name = "Id", namespace = XML_CORE_NS_URL)
    @JsonProperty("id")
    private String id;

    @XmlElement(name = "Title", namespace = XML_CORE_NS_URL)
    @JsonProperty("title")
    @JsonInclude(NON_NULL)
    private String title;

    @XmlElement(name = "Description", namespace = XML_CORE_NS_URL)
    @JsonProperty("description")
    @JsonInclude(NON_NULL)
    private String description;

    @XmlElement(name = "link", namespace = XML_ATOM_NS_URL)
    @JsonProperty("links")
    private List<Link> links;

    @XmlElement(name = "Extent", namespace = XML_CORE_NS_URL)
    @JsonInclude(NON_NULL)
    private Extent extent;

    @JsonProperty("itemType")
    @XmlTransient
    private String itemType = "feature";

    @JsonInclude(NON_NULL)
    @JsonProperty("crs")
    @XmlTransient
    private List<String> crs;

    public Collection() {
    }

    public Collection( String id, String title, String description, List<Link> links, Extent extent, List<String> crs ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.links = links;
        this.extent = extent;
        this.crs = crs;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
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

    public Extent getExtent() {
        return extent;
    }

    public void setExtent( Extent extent ) {
        this.extent = extent;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks( List<Link> links ) {
        this.links = links;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType( String itemType ) {
        this.itemType = itemType;
    }

    public List<String> getCrs() {
        return crs;
    }

    public void setCrs( List<String> crs ) {
        this.crs = crs;
    }
}