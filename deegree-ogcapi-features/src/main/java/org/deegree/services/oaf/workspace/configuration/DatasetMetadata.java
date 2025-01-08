/*-
 * #%L
 * deegree-ogcapi-features - OGC API Features (OAF) implementation - Querying and modifying of geospatial data objects
 * %%
 * Copyright (C) 2019 - 2020 lat/lon GmbH, info@lat-lon.de, www.lat-lon.de
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package org.deegree.services.oaf.workspace.configuration;

import org.deegree.commons.ows.metadata.MetadataUrl;
import org.deegree.commons.ows.metadata.ServiceIdentification;
import org.deegree.commons.ows.metadata.ServiceProvider;
import org.deegree.commons.ows.metadata.party.Address;
import org.deegree.commons.ows.metadata.party.ContactInfo;
import org.deegree.commons.ows.metadata.party.ResponsibleParty;
import org.deegree.commons.tom.ows.LanguageString;
import org.deegree.services.metadata.OWSMetadataProvider;
import org.deegree.services.oaf.domain.License;
import org.deegree.services.oaf.domain.landingpage.Contact;
import org.deegree.services.ogcapi.features.LicenseType;
import org.deegree.services.ogcapi.features.MetadataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates metadata of the service
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class DatasetMetadata {

	private static final String DEFAULT_TITLE = "deegree OGC API - Features";

	private static final String DEFAULT_DESCRIPTION = "OGC API - Features 1.0 implementation";

	private String title = DEFAULT_TITLE;

	private String description = DEFAULT_DESCRIPTION;

	private final Contact providerContact = new Contact();

	private final Contact creatorContact = new Contact();

	private List<MetadataUrl> metadataUrls = new ArrayList<>();

	private License providerLicense;

	private License datasetLicense;

	public DatasetMetadata(OWSMetadataProvider metadata, MetadataType configMetadata) {
		if (metadata != null) {
			this.title = getTitle(metadata);
			this.description = getDescription(metadata);
			parseLicenses(configMetadata);
			parseDatasetContact(configMetadata);
			parseProviderContact(metadata.getServiceProvider());
			parseMetadataUrls(configMetadata);
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
	 * @return never <code>null</code>
	 */
	public Contact getProviderContact() {
		return providerContact;
	}

	/**
	 * @return never <code>null</code>
	 */
	public Contact getCreatorContact() {
		return creatorContact;
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
	public boolean hasProviderLicenseUrl() {
		if (providerLicense != null && providerLicense.getUrl() != null)
			return true;
		return false;
	}

	/**
	 * @return may be <code>null</code>
	 */
	public License getProviderLicense() {
		return providerLicense;
	}

	/**
	 * @return <code>true</code> if license is available, <code>false</code> otherwise
	 */
	public boolean hasDatasetLicenseUrl() {
		if (datasetLicense != null && datasetLicense.getUrl() != null)
			return true;
		return false;
	}

	/**
	 * @return may be <code>null</code>
	 */
	public License getDatasetLicense() {
		return datasetLicense;
	}

	private void parseDatasetContact(MetadataType metadata) {
		if (metadata == null)
			return;
		MetadataType.DatasetCreator datasetCreator = metadata.getDatasetCreator();
		if (datasetCreator != null) {
			this.creatorContact.setName(datasetCreator.getName());
			this.creatorContact.setUrl(datasetCreator.getUrl());
			this.creatorContact.setEmail(datasetCreator.getEMail());
		}
	}

	private void parseProviderContact(ServiceProvider serviceProvider) {
		if (serviceProvider != null) {
			providerContact.setName(serviceProvider.getProviderName());
			ResponsibleParty serviceContact = serviceProvider.getServiceContact();
			if (serviceContact != null) {
				ContactInfo contactInfo = serviceContact.getContactInfo();
				if (contactInfo != null) {
					Address address = contactInfo.getAddress();
					if (address != null) {
						List<String> electronicMailAddress = address.getElectronicMailAddress();
						if (electronicMailAddress != null && !electronicMailAddress.isEmpty())
							this.providerContact.setEmail(electronicMailAddress.get(0));
					}
					if (contactInfo.getOnlineResource() != null) {
						this.providerContact.setUrl(contactInfo.getOnlineResource().toExternalForm());
					}
				}
			}
		}
	}

	private void parseMetadataUrls(MetadataType serviceMetadata) {
		if (serviceMetadata != null) {
			serviceMetadata.getMetadataURL().forEach(metadataUrl -> {
				this.metadataUrls.add(new MetadataUrl(metadataUrl.getValue(), null, metadataUrl.getFormat()));
			});
		}
	}

	private void parseLicenses(MetadataType metadata) {
		if (metadata == null)
			return;
		LicenseType providerLicense = metadata.getProviderLicense();
		if (providerLicense != null) {
			LicenseType.Url url = providerLicense.getUrl();
			this.providerLicense = new License(providerLicense.getName(), url != null ? url.getValue() : null,
					url != null ? url.getFormat() : null, providerLicense.getDescription());
		}
		LicenseType datasetLicense = metadata.getDatasetLicense();
		if (datasetLicense != null) {
			LicenseType.Url url = datasetLicense.getUrl();
			this.datasetLicense = new License(datasetLicense.getName(), url != null ? url.getValue() : null,
					url != null ? url.getFormat() : null, datasetLicense.getDescription());
		}
	}

	private String getTitle(OWSMetadataProvider metadata) {
		ServiceIdentification serviceIdentification = metadata.getServiceIdentification();
		if (serviceIdentification != null) {
			LanguageString title = serviceIdentification.getTitle(null);
			if (title != null)
				return title.getString();
		}
		return DEFAULT_TITLE;
	}

	private String getDescription(OWSMetadataProvider metadata) {
		ServiceIdentification serviceIdentification = metadata.getServiceIdentification();
		if (serviceIdentification != null) {
			LanguageString description = serviceIdentification.getAbstract(null);
			if (description != null)
				return description.getString();
		}
		return DEFAULT_DESCRIPTION;
	}

	@Override
	public String toString() {
		return "DatasetMetadata{" + "title='" + title + '\'' + ", description='" + description + '\''
				+ ", providerContact=" + providerContact + ", creatorContact=" + creatorContact + ", metadataUrls="
				+ metadataUrls + ", providerLicense=" + providerLicense + ", datasetLicense=" + datasetLicense + '}';
	}

}
