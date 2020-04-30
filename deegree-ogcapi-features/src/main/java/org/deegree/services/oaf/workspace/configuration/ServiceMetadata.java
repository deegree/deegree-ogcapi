package org.deegree.services.oaf.workspace.configuration;

import org.deegree.commons.ows.metadata.MetadataUrl;
import org.deegree.commons.ows.metadata.ServiceIdentification;
import org.deegree.commons.ows.metadata.ServiceProvider;
import org.deegree.commons.ows.metadata.party.Address;
import org.deegree.commons.ows.metadata.party.ContactInfo;
import org.deegree.commons.ows.metadata.party.ResponsibleParty;
import org.deegree.commons.tom.ows.LanguageString;
import org.deegree.services.jaxb.oaf.ServiceMetadataType;
import org.deegree.services.metadata.OWSMetadataProvider;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Encapsulates metadata of the service
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class ServiceMetadata {

    private static final Logger LOG = getLogger( ServiceMetadata.class );

    private static final String DEFAULT_TITLE = "deegree OGC API - Features";

    private static final String DEFAULT_DESCRIPTION = "OGC API - Features 1.0 implementation";

    private String title = DEFAULT_TITLE;

    private String description = DEFAULT_DESCRIPTION;

    private String providerName;

    private String providerEmail;

    private String providerUrl;

    private List<MetadataUrl> metadataUrls = new ArrayList<>();

    private String fees;

    private List<String> accessConstraints;

    public ServiceMetadata( OWSMetadataProvider metadata, ServiceMetadataType serviceMetadata ) {
        if ( metadata != null ) {
            this.title = getTitle( metadata );
            this.description = getDescription( metadata );
            parseLicense( metadata );
            parseContact( metadata.getServiceProvider() );
            parseMetadataUrls( serviceMetadata );
        }
    }

    /**
     * @return never <code>null</code>
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return never <code>null</code>
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return may be <code>null</code>
     */
    public String getProviderName() {
        return providerName;
    }

    /**
     * @return may be <code>null</code>
     */
    public String getProviderUrl() {
        return providerUrl;
    }

    /**
     * @return may be <code>null</code>
     */
    public String getProviderEmail() {
        return providerEmail;
    }

    /**
     * @return never <code>null</code>
     */
    public List<MetadataUrl> getMetadataUrls() {
        return metadataUrls;
    }

    /**
     * @return <code>true</code> if license is available, <code>false</code> otherwise
     */
    public boolean hasLicense() {
        if ( fees != null || ( accessConstraints != null && !accessConstraints.isEmpty() ) )
            return true;
        return false;
    }

    /**
     * @return may be <code>null</code>
     */
    public String getFees() {
        return fees;
    }

    /**
     * @return may be <code>null</code> or empty
     */
    public List<String> getAccessConstraints() {
        return accessConstraints;
    }

    private void parseContact( ServiceProvider serviceProvider ) {
        if ( serviceProvider != null ) {
            this.providerName = serviceProvider.getProviderName();
            ResponsibleParty serviceContact = serviceProvider.getServiceContact();
            if ( serviceContact != null ) {
                ContactInfo contactInfo = serviceContact.getContactInfo();
                if ( contactInfo != null ) {
                    Address address = contactInfo.getAddress();
                    if ( address != null ) {
                        List<String> electronicMailAddress = address.getElectronicMailAddress();
                        if ( electronicMailAddress != null && !electronicMailAddress.isEmpty() )
                            this.providerEmail = electronicMailAddress.get( 0 );
                    }
                    if ( contactInfo.getOnlineResource() != null ) {
                        this.providerUrl = contactInfo.getOnlineResource().toExternalForm();
                    }
                }
            }
        }
    }

    private void parseMetadataUrls( ServiceMetadataType serviceMetadata ) {
        if ( serviceMetadata != null ) {
            serviceMetadata.getMetadataURL().forEach( metadataUrl -> {
                this.metadataUrls.add( new MetadataUrl( metadataUrl.getValue(), null, metadataUrl.getFormat() ) );
            } );
        }
    }

    private void parseLicense( OWSMetadataProvider metadata ) {
        ServiceIdentification serviceIdentification = metadata.getServiceIdentification();
        if ( serviceIdentification != null ) {
            this.fees = serviceIdentification.getFees();
            this.accessConstraints = serviceIdentification.getAccessConstraints();
        }
    }

    private String getTitle( OWSMetadataProvider metadata ) {
        ServiceIdentification serviceIdentification = metadata.getServiceIdentification();
        if ( serviceIdentification != null ) {
            LanguageString title = serviceIdentification.getTitle( null );
            if ( title != null )
                return title.getString();
        }
        return DEFAULT_TITLE;
    }

    private String getDescription( OWSMetadataProvider metadata ) {
        ServiceIdentification serviceIdentification = metadata.getServiceIdentification();
        if ( serviceIdentification != null ) {
            LanguageString description = serviceIdentification.getAbstract( null );
            if ( description != null )
                return description.getString();
        }
        return DEFAULT_DESCRIPTION;
    }

}
