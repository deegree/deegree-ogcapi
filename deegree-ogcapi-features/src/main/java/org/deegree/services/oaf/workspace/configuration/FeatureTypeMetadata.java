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
import org.deegree.feature.persistence.FeatureStore;
import org.deegree.feature.types.FeatureType;
import org.deegree.services.oaf.domain.collections.Extent;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class FeatureTypeMetadata {

	private QName name;

	private QName dateTimeProperty;

	private Extent extent;

	private String title;

	private String description;

	private List<MetadataUrl> metadataUrls;

	private List<FilterProperty> filterProperties;

	private FeatureType featureType;

	private FeatureStore featureStore;

	private List<String> storageCrsCodes;

	public FeatureTypeMetadata(QName featureTypeName) {
		this.name = featureTypeName;
	}

	public FeatureTypeMetadata dateTimeProperty(QName dateTimeProperty) {
		this.dateTimeProperty = dateTimeProperty;
		return this;
	}

	public FeatureTypeMetadata extent(Extent extent) {
		this.extent = extent;
		return this;
	}

	public FeatureTypeMetadata title(String title) {
		this.title = title;
		return this;
	}

	public FeatureTypeMetadata description(String description) {
		this.description = description;
		return this;
	}

	public FeatureTypeMetadata metadataUrls(List<MetadataUrl> metadataUrls) {
		this.metadataUrls = metadataUrls;
		return this;
	}

	public FeatureTypeMetadata filterProperties(List<FilterProperty> filterProperties) {
		this.filterProperties = filterProperties;
		return this;
	}

	public FeatureTypeMetadata featureType(FeatureType featureType) {
		this.featureType = featureType;
		return this;
	}

	public FeatureTypeMetadata featureStore(FeatureStore featureStore) {
		this.featureStore = featureStore;
		return this;
	}

	public FeatureTypeMetadata storageCrsCodes(List<String> storageCrsCodes) {
		this.storageCrsCodes = storageCrsCodes;
		return this;
	}

	public QName getName() {
		return name;
	}

	public Extent getExtent() {
		return extent;
	}

	public QName getDateTimeProperty() {
		return dateTimeProperty;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public List<MetadataUrl> getMetadataUrls() {
		return metadataUrls;
	}

	public List<FilterProperty> getFilterProperties() {
		return filterProperties;
	}

	public FeatureType getFeatureType() {
		return featureType;
	}

	public FeatureStore getFeatureStore() {
		return featureStore;
	}

	public List<String> getStorageCrsCodes() {
		return storageCrsCodes;
	}

}
