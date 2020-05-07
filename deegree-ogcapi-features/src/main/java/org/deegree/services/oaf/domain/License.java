package org.deegree.services.oaf.domain;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class License {

    private final String name;

    private final String url;

    private final String urlFormat;

    private final String description;

    public License( String name, String url, String urlFormat, String description ) {
        this.name = name;
        this.url = url;
        this.urlFormat = urlFormat;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getUrlFormat() {
        return urlFormat;
    }

    public String getDescription() {
        return description;
    }
}