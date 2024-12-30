/*-
 * #%L
 * deegree-ogcapi-datasets - OGC API Features (OAF) implementation - Configuration of Datasets
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
package org.deegree.services.oaf.config.datasets;

import org.deegree.services.jaxb.ogcapi.datasets.Datasets;
import org.deegree.workspace.Resource;
import org.deegree.workspace.ResourceMetadata;
import org.deegree.workspace.Workspace;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * {@link Resource} parsing the {@link DatasetsConfiguration}.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class DatasetsConfigResource implements Resource {

	private static final Logger LOG = getLogger(DatasetsConfigResource.class);

	private final ResourceMetadata<DatasetsConfigResource> metadata;

	private final Workspace workspace;

	private final Datasets config;

	private DatasetsConfiguration DatasetsConfiguration;

	public DatasetsConfigResource(ResourceMetadata<DatasetsConfigResource> metadata, Workspace workspace,
			Datasets config) {
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
		DatasetsConfiguration = parseDatasetsConfiguration();
	}

	@Override
	public void destroy() {

	}

	public DatasetsConfiguration getDatasetsConfiguration() {
		return DatasetsConfiguration;
	}

	private DatasetsConfiguration parseDatasetsConfiguration() {
		if (config == null)
			return null;
		String title = config.getTitle();
		String description = config.getDescription();
		Contact configuredContact = null;
		Datasets.Contact contact = config.getContact();

		if (contact != null) {
			configuredContact = new Contact(contact.getName(), contact.getEMail(), contact.getUrl());
		}
		return new DatasetsConfiguration(title, description, configuredContact);
	}

}
