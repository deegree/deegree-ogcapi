/*-
 * #%L
 * deegree-ogcapi-config-htmlview - OGC API Features (OAF) implementation - Configuration of the HTML View
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
package org.deegree.services.oaf.config.htmlview;

import org.deegree.commons.xml.jaxb.JAXBUtils;
import org.deegree.services.jaxb.ogcapi.htmlview.HtmlView;
import org.deegree.workspace.ResourceBuilder;
import org.deegree.workspace.ResourceInitException;
import org.deegree.workspace.ResourceLocation;
import org.deegree.workspace.Workspace;
import org.deegree.workspace.standard.AbstractResourceMetadata;
import org.deegree.workspace.standard.AbstractResourceProvider;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class HtmlViewConfigMetadata extends AbstractResourceMetadata<HtmlViewConfigResource> {

	private static final String CONFIG_JAXB_PACKAGE = "org.deegree.services.jaxb.ogcapi.htmlview";

	public HtmlViewConfigMetadata(Workspace workspace, ResourceLocation<HtmlViewConfigResource> location,
			AbstractResourceProvider<HtmlViewConfigResource> provider) {
		super(workspace, location, provider);
	}

	@Override
	public ResourceBuilder<HtmlViewConfigResource> prepare() {
		try {
			HtmlView cfg = (HtmlView) JAXBUtils.unmarshall(CONFIG_JAXB_PACKAGE, provider.getSchema(),
					location.getAsStream(), workspace);
			return new HtmlViewConfigBuilder(this, workspace, cfg);
		}
		catch (Exception e) {
			throw new ResourceInitException(e.getLocalizedMessage(), e);
		}
	}

}
