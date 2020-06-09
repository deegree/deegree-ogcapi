package org.deegree.services.oaf.domain.html;

/**
 * Encapsulates zhe configuration of the impressum
 */
public class HtmlPageConfiguration {

    private final String impressumUrl;

    private final String privacyUrl;

    public HtmlPageConfiguration( String impressumUrl, String privacyUrl ) {
        this.impressumUrl = impressumUrl;
        this.privacyUrl = privacyUrl;
    }

    public String getImpressumUrl() {
        return impressumUrl;
    }

    public String getPrivacyUrl() {
        return privacyUrl;
    }
}