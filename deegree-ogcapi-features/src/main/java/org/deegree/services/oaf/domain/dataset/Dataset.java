package org.deegree.services.oaf.domain.dataset;

import org.deegree.services.oaf.link.Link;

import java.util.List;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class Dataset {

    private String name;

    private String title;

    private List<Link> links;

    public Dataset( String name, String title, List<Link> links ) {
        this.name = name;
        this.title = title;
        this.links = links;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks( List<Link> links ) {
        this.links = links;
    }

}