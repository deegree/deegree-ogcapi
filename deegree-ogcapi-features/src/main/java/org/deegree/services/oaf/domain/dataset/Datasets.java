package org.deegree.services.oaf.domain.dataset;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.deegree.services.oaf.config.datasets.DatasetsConfiguration;
import org.deegree.services.oaf.domain.landingpage.Contact;
import org.deegree.services.oaf.link.Link;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class Datasets {

    @JsonInclude(NON_NULL)
    private String title;

    @JsonInclude(NON_NULL)
    private String description;

    @JsonInclude(NON_NULL)
    private Contact contact;

    private List<Link> links;

    private List<Dataset> datasets;

    public Datasets() {
        this.links = links;
        this.datasets = datasets;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Contact getContact() {
        return contact;
    }

    public List<Link> getLinks() {
        return links;
    }

    public List<Dataset> getDatasets() {
        return datasets;
    }

    public Datasets withLinks( List<Link> links ) {
        this.links = links;
        return this;
    }

    public Datasets withDatasets( List<Dataset> datasets ) {
        this.datasets = datasets;
        return this;
    }

    public Datasets withDatasetsConfiguration( DatasetsConfiguration datasetsConfiguration ) {
        if ( datasetsConfiguration != null ) {
            this.title = datasetsConfiguration.getTitle();
            this.description = datasetsConfiguration.getDescription();
            org.deegree.services.oaf.config.datasets.Contact contact = datasetsConfiguration.getContact();
            if ( contact != null ) {
                this.contact = new Contact( contact.getName(), contact.getEmail(), contact.getUrl() );
            }
        }
        return this;
    }
}