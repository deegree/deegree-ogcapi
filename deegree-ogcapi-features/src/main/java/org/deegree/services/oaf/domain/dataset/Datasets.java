package org.deegree.services.oaf.domain.dataset;

import org.deegree.services.oaf.link.Link;

import java.util.List;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class Datasets {

    private List<Link> links;

    private List<Dataset> datasets;

    public Datasets( List<Link> links, List<Dataset> datasets ) {
        this.links = links;
        this.datasets = datasets;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks( List<Link> links ) {
        this.links = links;
    }

    public List<Dataset> getDatasets() {
        return datasets;
    }

    public void setDatasets( List<Dataset> datasets ) {
        this.datasets = datasets;
    }
}