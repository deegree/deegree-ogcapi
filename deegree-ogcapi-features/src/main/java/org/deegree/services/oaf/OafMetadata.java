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

import org.deegree.commons.xml.jaxb.JAXBUtils;
import org.deegree.feature.persistence.FeatureStore;
import org.deegree.feature.persistence.FeatureStoreManager;
import org.deegree.feature.persistence.FeatureStoreProvider;
import org.deegree.services.metadata.OWSMetadataProvider;
import org.deegree.services.metadata.OWSMetadataProviderManager;
import org.deegree.services.oaf.config.htmlview.OgcApiConfigProvider;
import org.deegree.services.ogcapi.features.DeegreeOAF;
import org.deegree.workspace.Resource;
import org.deegree.workspace.ResourceBuilder;
import org.deegree.workspace.ResourceIdentifier;
import org.deegree.workspace.ResourceInitException;
import org.deegree.workspace.ResourceLocation;
import org.deegree.workspace.ResourceMetadata;
import org.deegree.workspace.Workspace;
import org.deegree.workspace.standard.AbstractResourceMetadata;
import org.deegree.workspace.standard.AbstractResourceProvider;
import org.deegree.workspace.standard.DefaultResourceIdentifier;

import java.util.List;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class OafMetadata extends AbstractResourceMetadata<Resource> {

	private static final String CONFIG_JAXB_PACKAGE = "org.deegree.services.ogcapi.features";

	public OafMetadata(Workspace workspace, ResourceLocation<Resource> location,
			AbstractResourceProvider<Resource> provider) {
		super(workspace, location, provider);
	}

	@Override
	public ResourceBuilder<Resource> prepare() {
		try {
			DeegreeOAF cfg = (DeegreeOAF) JAXBUtils.unmarshall(CONFIG_JAXB_PACKAGE, provider.getSchema(),
					location.getAsStream(), workspace);

			List<String> list = cfg.getFeatureStoreId();
			if (list != null && !list.isEmpty()) {
				for (String id : list) {
					dependencies.add(new DefaultResourceIdentifier<FeatureStore>(FeatureStoreProvider.class, id));
				}
			}
			else {
				FeatureStoreManager fmgr = workspace.getResourceManager(FeatureStoreManager.class);
				for (ResourceMetadata<FeatureStore> md : fmgr.getResourceMetadata()) {
					softDependencies.add(md.getIdentifier());
				}
			}

			OWSMetadataProviderManager mmgr = workspace.getResourceManager(OWSMetadataProviderManager.class);
			for (ResourceMetadata<OWSMetadataProvider> md : mmgr.getResourceMetadata()) {
				ResourceIdentifier<OWSMetadataProvider> id = md.getIdentifier();
				if (id.getId().equals(getIdentifier().getId() + "_metadata")) {
					softDependencies.add(id);
				}
			}

			String htmlViewId = cfg.getHtmlViewId();
			if (htmlViewId != null) {
				dependencies.add(new DefaultResourceIdentifier<>(OgcApiConfigProvider.class, htmlViewId));
			}
			else {
				softDependencies.add(new DefaultResourceIdentifier<>(OgcApiConfigProvider.class, "htnmlview"));
			}

			return new OafBuilder(this, workspace, cfg);
		}
		catch (Exception e) {
			throw new ResourceInitException(e.getLocalizedMessage(), e);
		}
	}

}
