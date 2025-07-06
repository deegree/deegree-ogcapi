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
package org.deegree.services.oaf;

import org.deegree.commons.ows.metadata.MetadataUrl;
import org.deegree.commons.tom.gml.property.PropertyType;
import org.deegree.commons.tom.ows.LanguageString;
import org.deegree.commons.tom.primitive.BaseType;
import org.deegree.commons.tom.primitive.PrimitiveType;
import org.deegree.commons.utils.Pair;
import org.deegree.cs.exceptions.TransformationException;
import org.deegree.cs.exceptions.UnknownCRSException;
import org.deegree.cs.persistence.CRSManager;
import org.deegree.feature.persistence.FeatureStore;
import org.deegree.feature.persistence.FeatureStoreException;
import org.deegree.feature.persistence.FeatureStoreProvider;
import org.deegree.feature.types.AppSchema;
import org.deegree.feature.types.FeatureType;
import org.deegree.feature.types.property.SimplePropertyType;
import org.deegree.geometry.Envelope;
import org.deegree.geometry.GeometryTransformer;
import org.deegree.services.metadata.OWSMetadataProvider;
import org.deegree.services.metadata.provider.OWSMetadataProviderProvider;
import org.deegree.services.oaf.config.htmlview.HtmlViewConfigResource;
import org.deegree.services.oaf.config.htmlview.HtmlViewConfiguration;
import org.deegree.services.oaf.config.htmlview.OgcApiConfigProvider;
import org.deegree.services.oaf.domain.collections.Extent;
import org.deegree.services.oaf.domain.collections.Spatial;
import org.deegree.services.oaf.domain.collections.Temporal;
import org.deegree.services.oaf.exceptions.InvalidConfigurationException;
import org.deegree.services.oaf.workspace.configuration.DatasetMetadata;
import org.deegree.services.oaf.workspace.configuration.FeatureTypeMetadata;
import org.deegree.services.oaf.workspace.configuration.FilterProperty;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;
import org.deegree.services.ogcapi.features.DateTimePropertyType;
import org.deegree.services.ogcapi.features.DeegreeOAF;
import org.deegree.services.ogcapi.features.DeegreeOAF.ConfigureCollection;
import org.deegree.services.ogcapi.features.DeegreeOAF.ConfigureCollections;
import org.deegree.services.ogcapi.features.GeometryPropertyType;
import org.deegree.workspace.Resource;
import org.deegree.workspace.ResourceInitException;
import org.deegree.workspace.ResourceMetadata;
import org.deegree.workspace.Workspace;
import org.slf4j.Logger;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.deegree.commons.xml.CommonNamespaces.GML3_2_NS;
import static org.deegree.commons.xml.CommonNamespaces.GMLNS;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.DEFAULT_CRS;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * {@link Resource} parsing the deegreOAF configuration as
 * {@link OafDatasetConfiguration}.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class OafResource implements Resource {

	private static final Logger LOG = getLogger(OafResource.class);

	private final ResourceMetadata<Resource> metadata;

	private final Workspace workspace;

	private final DeegreeOAF config;

	private OafDatasetConfiguration oafConfiguration;

	private HtmlViewConfiguration htmlViewConfiguration;

	private List<ConfigureCollection> additionalCollectionList = new ArrayList<>();

	private List<ConfigureCollections> additionalCollectionsList = new ArrayList<>();

	public List<ConfigureCollection> getAdditionalCollectionList() {
		return additionalCollectionList;
	}

	public List<ConfigureCollections> getAdditionalCollectionsList() {
		return additionalCollectionsList;
	}

	public OafResource(ResourceMetadata<Resource> metadata, Workspace workspace, DeegreeOAF config) {
		this.metadata = metadata;
		this.workspace = workspace;
		this.config = config;
	}

	@Override
	public ResourceMetadata<? extends Resource> getMetadata() {
		return metadata;
	}

	@Override
	public void init() {
		OWSMetadataProvider owsMetadataProvider = getMetadata(workspace);
		try {
			Map<String, FeatureTypeMetadata> featureTypeMetadata = parseFeatureTypeMetadata(owsMetadataProvider);
			DatasetMetadata datasetMetadata = new DatasetMetadata(owsMetadataProvider, config.getMetadata());
			List<String> supportedCrs = parseQueryCrs(config);
			String id = metadata.getIdentifier().getId();
			boolean useExistingGMLSchema = config.isUseExistingGMLSchema() != null ? config.isUseExistingGMLSchema()
					: true;
			this.oafConfiguration = new OafDatasetConfiguration(id, featureTypeMetadata, datasetMetadata, supportedCrs,
					useExistingGMLSchema);
			this.htmlViewConfiguration = getHtmlViewConfig(workspace);

			this.additionalCollectionList = config.getConfigureCollection();

			this.additionalCollectionsList = config.getConfigureCollections();

			LOG.debug("Initialising deegree ogcapi with " + oafConfiguration + " and HTML view config "
					+ htmlViewConfiguration);
		}
		catch (InvalidConfigurationException e) {
			throw new ResourceInitException("OAF Configuration could not be parsed", e);
		}
	}

	@Override
	public void destroy() {

	}

	/**
	 * @return the parsed {@link OafDatasetConfiguration}, may be <code>null</code> if the
	 * configuration could not be parsed
	 */
	public OafDatasetConfiguration getOafConfiguration() {
		return this.oafConfiguration;
	}

	/**
	 * @return the parsed {@link HtmlViewConfiguration}, may be <code>null</code> if the
	 * configuration could not be parsed or is not available
	 */
	public HtmlViewConfiguration getHtmlViewConfiguration() {
		return this.htmlViewConfiguration;
	}

	private OWSMetadataProvider getMetadata(Workspace workspace) {
		return workspace.getResource(OWSMetadataProviderProvider.class,
				getMetadata().getIdentifier().getId() + "_metadata");
	}

	private HtmlViewConfiguration getHtmlViewConfig(Workspace workspace) {
		String htmlViewId = config.getHtmlViewId();
		if (htmlViewId != null) {
			HtmlViewConfigResource htmlViewConfigResource = workspace.getResource(OgcApiConfigProvider.class,
					htmlViewId);
			return htmlViewConfigResource.getHtmlViewConfiguration();
		}
		HtmlViewConfigResource globalHtmlViewConfigResource = workspace.getResource(OgcApiConfigProvider.class,
				"htmlview");
		if (globalHtmlViewConfigResource != null)
			return globalHtmlViewConfigResource.getHtmlViewConfiguration();
		return null;
	}

	private Map<String, FeatureTypeMetadata> parseFeatureTypeMetadata(OWSMetadataProvider metadata)
			throws InvalidConfigurationException {
		Map<String, FeatureTypeMetadata> featureTypeNames = new HashMap<>();

		List<FeatureStore> featureStores = retrieveFeatureStoreIds();
		for (FeatureStore featureStore : featureStores) {
			addFeatureTypesOfStore(metadata, featureTypeNames, featureStore);
		}
		return featureTypeNames;

	}

	private List<String> parseQueryCrs(DeegreeOAF deegreeOAF) throws InvalidConfigurationException {
		List<String> queryCRSs = deegreeOAF.getQueryCRS();
		if (queryCRSs.isEmpty())
			return Collections.singletonList(DEFAULT_CRS);
		List<String> configuredCrs = new ArrayList<>();
		for (String queryCrs : queryCRSs) {
			configuredCrs.add(parseQueryCrs(queryCrs));
		}
		return configuredCrs;
	}

	private String parseQueryCrs(String queryCrs) throws InvalidConfigurationException {
		try {
			CRSManager.lookup(queryCrs);
			return queryCrs;
		}
		catch (UnknownCRSException e) {
			LOG.error("Configuration could not be parsed: ", e);
			throw new InvalidConfigurationException("Unknown CRS");
		}
	}

	private List<FeatureStore> retrieveFeatureStoreIds() throws InvalidConfigurationException {
		List<String> configuredFeatureStoreIds = config.getFeatureStoreId();
		if (configuredFeatureStoreIds.isEmpty())
			return workspace.getResourcesOfType(FeatureStoreProvider.class)
				.stream()
				.map(id -> workspace.getResource(FeatureStoreProvider.class, id.getId()))
				.collect(Collectors.toList());
		List<FeatureStore> featureStores = new ArrayList<>();
		for (String configuredFeatureStoreId : configuredFeatureStoreIds) {
			FeatureStore resource = workspace.getResource(FeatureStoreProvider.class, configuredFeatureStoreId);
			if (resource == null)
				throw new InvalidConfigurationException(
						"FeatureStore with ID " + configuredFeatureStoreId + " is not available");
			featureStores.add(resource);
		}
		return featureStores;
	}

	private void addFeatureTypesOfStore(OWSMetadataProvider metadata, Map<String, FeatureTypeMetadata> featureTypeNames,
			FeatureStore featureStore) throws InvalidConfigurationException {
		AppSchema schema = featureStore.getSchema();
		FeatureType[] featureTypes = schema.getFeatureTypes();
		for (FeatureType featureType : featureTypes) {
			QName name = featureType.getName();
			if (featureStore.isMapped(name)) {
				addFeatureType(metadata, featureTypeNames, featureStore, name);
			}
		}
	}

	private void addFeatureType(OWSMetadataProvider metadata, Map<String, FeatureTypeMetadata> featureTypeNames,
			FeatureStore featureStore, QName name) throws InvalidConfigurationException {
		if (!name.getNamespaceURI().equals(GMLNS) && !name.getNamespaceURI().equals(GML3_2_NS)) {
			try {
				QName dateTimeProperty = getDateTimeProperty(name);
				Pair<QName, Boolean> geometryProperty = getGeometryProperty(name);
				org.deegree.commons.ows.metadata.DatasetMetadata datasetMetadata = metadata != null
						? metadata.getDatasetMetadata(name) : null;
				FeatureTypeMetadata ftMetadata = createFeatureTypeMetadata(featureStore, name, dateTimeProperty,
						geometryProperty != null ? geometryProperty.getFirst() : null,
						geometryProperty != null ? geometryProperty.getSecond() : false, datasetMetadata);
				featureTypeNames.put(name.getLocalPart(), ftMetadata);
			}
			catch (FeatureStoreException e) {
				throw new InvalidConfigurationException("Feature type could not be parsed", e);
			}
		}
	}

	private FeatureTypeMetadata createFeatureTypeMetadata(FeatureStore featureStore, QName name, QName dateTimeProperty,
			QName geometryProperty, boolean skipExportAsWkt,
			org.deegree.commons.ows.metadata.DatasetMetadata datasetMetadata) throws FeatureStoreException {
		FeatureType featureType = featureStore.getSchema().getFeatureType(name);
		List<FilterProperty> filterProperties = parseFilterProperties(featureType);
		Extent extent = createExtent(featureStore, name, dateTimeProperty);
		String title = datasetMetadata != null ? asString(datasetMetadata.getTitle(null)) : name.getLocalPart();
		String description = datasetMetadata != null ? asString(datasetMetadata.getAbstract(null)) : null;
		List<MetadataUrl> metadataUrls = datasetMetadata != null && !datasetMetadata.getMetadataUrls().isEmpty()
				? datasetMetadata.getMetadataUrls() : Collections.emptyList();
		String[] storageCrsCodes = featureStore.getStorageCrs() != null
				? featureStore.getStorageCrs().getOrignalCodeStrings() : null;
		return new FeatureTypeMetadata(name).dateTimeProperty(dateTimeProperty)
			.geometryProperty(geometryProperty)
			.skipGeometryExportAsWkt(skipExportAsWkt)
			.extent(extent)
			.title(title)
			.description(description)
			.metadataUrls(metadataUrls)
			.filterProperties(filterProperties)
			.featureType(featureType)
			.featureStore(featureStore)
			.storageCrsCodes(storageCrsCodes != null ? Arrays.asList(storageCrsCodes) : null);
	}

	private List<FilterProperty> parseFilterProperties(FeatureType featureType) {
		List<FilterProperty> filterProperties = new ArrayList<>();

		List<PropertyType> propertyDeclarations = featureType.getPropertyDeclarations();
		propertyDeclarations.forEach(propertyDeclaration -> {
			if (propertyDeclaration instanceof SimplePropertyType) {
				PrimitiveType primitiveType = ((SimplePropertyType) propertyDeclaration).getPrimitiveType();
				BaseType baseType = primitiveType.getBaseType();
				QName propertyName = propertyDeclaration.getName();
				filterProperties.add(new FilterProperty(propertyName, baseType));
			}
		});
		return filterProperties;
	}

	private QName getDateTimeProperty(QName name) throws InvalidConfigurationException {
		DeegreeOAF.DateTimeProperties dateTimeProperties = config.getDateTimeProperties();
		if (dateTimeProperties == null)
			return null;
		List<DateTimePropertyType> configuredProperties = dateTimeProperties.getDateTimeProperty()
			.stream()
			.filter(dtp -> name.equals(dtp.getFeatureTypeName()))
			.collect(Collectors.toList());
		if (configuredProperties.isEmpty())
			return null;
		if (configuredProperties.size() > 1)
			throw new InvalidConfigurationException("Multiple datetime properties for feature type " + name
					+ " found. Currently only one datetime property per feature type is supported");
		return configuredProperties.get(0).getPropertyName();
	}

	private Pair<QName, Boolean> getGeometryProperty(QName name) throws InvalidConfigurationException {
		DeegreeOAF.GeometryProperties geometryProperties = config.getGeometryProperties();
		if (geometryProperties == null)
			return null;
		List<GeometryPropertyType> configuredProperties = geometryProperties.getGeometryProperty()
			.stream()
			.filter(dtp -> name.equals(dtp.getFeatureTypeName()))
			.collect(Collectors.toList());
		if (configuredProperties.isEmpty())
			return null;
		if (configuredProperties.size() > 1)
			throw new InvalidConfigurationException("Multiple geometry properties for feature type " + name
					+ " found. Currently only one geometry property per feature type is supported");
		return new Pair<>(configuredProperties.get(0).getPropertyName(),
				configuredProperties.get(0).isSkipExportAsWkt());
	}

	private Extent createExtent(FeatureStore featureStore, QName featureTypeName, QName dateTimeProperty)
			throws FeatureStoreException {
		Extent extent = new Extent();
		Spatial spatial = createSpatial(featureStore, featureTypeName);
		extent.setSpatial(spatial);
		Temporal temporal = createTemporal(featureStore, featureTypeName, dateTimeProperty);
		extent.setTemporal(temporal);
		return extent;
	}

	private Temporal createTemporal(FeatureStore featureStore, QName featureTypeName, QName dateTimeProperty)
			throws FeatureStoreException {
		if (dateTimeProperty == null)
			return null;
		Pair<Date, Date> temporalExtent = featureStore.getTemporalExtent(featureTypeName, dateTimeProperty);
		List<Date> interval = intervalFromExtent(temporalExtent);
		return new Temporal(interval, null);
	}

	private List<Date> intervalFromExtent(Pair<Date, Date> temporalExtent) {
		if (temporalExtent == null)
			return null;
		List<Date> interval = new ArrayList<>();
		interval.add(temporalExtent.first);
		interval.add(temporalExtent.second);
		return interval;
	}

	private Spatial createSpatial(FeatureStore featureStore, QName name) throws FeatureStoreException {
		Envelope envelope = featureStore.getEnvelope(name);
		if (envelope == null) {
			return null;
		}
		envelope = transformIfRequired(envelope);
		List<Double> bbox = new ArrayList<>();
		bbox.add(envelope.getMin().get0());
		bbox.add(envelope.getMin().get1());
		bbox.add(envelope.getMax().get0());
		bbox.add(envelope.getMax().get1());
		return new Spatial(Collections.singletonList(bbox), "http://www.opengis.net/def/crs/OGC/1.3/CRS84");
	}

	private Envelope transformIfRequired(Envelope envelope) throws FeatureStoreException {
		try {
			GeometryTransformer crs84 = new GeometryTransformer(CRSManager.lookup("urn:ogc:def:crs:OGC:1.3:CRS84"));
			return (Envelope) crs84.transform(envelope, false);
		}
		catch (UnknownCRSException | TransformationException e) {
			LOG.error("Could not transform envelope to CRS84", e);
			throw new FeatureStoreException("Envelope could not be transformed to CRS84");
		}
	}

	private String asString(LanguageString languageString) {
		return languageString != null ? languageString.getString() : null;
	}

}
