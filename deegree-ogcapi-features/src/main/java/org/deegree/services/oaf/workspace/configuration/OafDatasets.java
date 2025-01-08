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

import org.deegree.services.oaf.exceptions.UnknownDatasetId;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates all datasets configurations.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class OafDatasets {

	private Map<String, OafDatasetConfiguration> datasetConfigurations = new HashMap<>();

	/**
	 * Add a new dataset.
	 * @param id id of the dataset, never <code>null</code>
	 * @param datasetConfiguration the configuration of the dataset, never
	 * <code>null</code>
	 */
	public void addDataset(String id, OafDatasetConfiguration datasetConfiguration) {
		this.datasetConfigurations.put(id, datasetConfiguration);
	}

	public Map<String, OafDatasetConfiguration> getDatasets() {
		return datasetConfigurations;
	}

	public OafDatasetConfiguration getDataset(String datasetId) throws UnknownDatasetId {
		if (!datasetConfigurations.containsKey(datasetId)) {
			throw new UnknownDatasetId(datasetId);
		}
		return datasetConfigurations.get(datasetId);
	}

}
