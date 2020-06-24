package org.deegree.services.oaf.domain.html;

/**
 * Encapsulates zhe configuration of the legal notice
 */
public class HtmlPageConfiguration {

    private final String legalNoticeUrl;

    private final String privacyUrl;

    private final String documentationUrl;

    public HtmlPageConfiguration( String legalNoticeUrl, String privacyUrl, String documentationUrl ) {
        this.legalNoticeUrl = legalNoticeUrl;
        this.privacyUrl = privacyUrl;
        this.documentationUrl = documentationUrl;
    }

    public String getLegalNoticeUrl() {
        return legalNoticeUrl;
    }

    public String getPrivacyUrl() {
        return privacyUrl;
    }

    public String getDocumentationUrl() {
        return documentationUrl;
    }
}