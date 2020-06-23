package org.deegree.services.oaf.domain.html;

/**
 * Encapsulates zhe configuration of the legal notice
 */
public class HtmlPageConfiguration {

    private final String legalNoticeUrl;

    private final String privacyUrl;

    public HtmlPageConfiguration( String legalNoticeUrl, String privacyUrl ) {
        this.legalNoticeUrl = legalNoticeUrl;
        this.privacyUrl = privacyUrl;
    }

    public String getLegalNoticeUrl() {
        return legalNoticeUrl;
    }

    public String getPrivacyUrl() {
        return privacyUrl;
    }
}