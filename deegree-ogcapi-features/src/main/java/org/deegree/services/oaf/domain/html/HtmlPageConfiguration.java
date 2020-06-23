package org.deegree.services.oaf.domain.html;

/**
 * Encapsulates zhe configuration of the imprint
 */
public class HtmlPageConfiguration {

    private final String imprintUrl;

    private final String privacyUrl;

    public HtmlPageConfiguration( String imprintUrl, String privacyUrl ) {
        this.imprintUrl = imprintUrl;
        this.privacyUrl = privacyUrl;
    }

    public String getImprintUrl() {
        return imprintUrl;
    }

    public String getPrivacyUrl() {
        return privacyUrl;
    }
}