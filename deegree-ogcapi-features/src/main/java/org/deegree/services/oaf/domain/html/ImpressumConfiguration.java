package org.deegree.services.oaf.domain.html;

/**
 * Encapsulates zhe configuration of the impressum
 */
public class ImpressumConfiguration {

    private final String impressumUrl;

    public ImpressumConfiguration( String impressumUrl ) {
        this.impressumUrl = impressumUrl;
    }

    public String getImpressumUrl() {
        return impressumUrl;
    }
}