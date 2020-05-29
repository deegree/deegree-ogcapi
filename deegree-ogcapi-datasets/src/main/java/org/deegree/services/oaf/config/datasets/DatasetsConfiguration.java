package org.deegree.services.oaf.config.datasets;

/**
 * Encapsulates the configuration of the Datasets
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class DatasetsConfiguration {

    private final String title;

    private final String description;

    private final Contact contact;

    public DatasetsConfiguration( String title, String description,
                                  Contact contact ) {
        this.title = title;
        this.description = description;
        this.contact = contact;
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
}
