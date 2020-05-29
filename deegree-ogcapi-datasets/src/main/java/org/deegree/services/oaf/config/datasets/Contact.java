package org.deegree.services.oaf.config.datasets;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class Contact {

    private final String name;

    private final String email;

    private final String url;

    public Contact( String name, String email, String url ) {
        this.name = name;
        this.email = email;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUrl() {
        return url;
    }
}