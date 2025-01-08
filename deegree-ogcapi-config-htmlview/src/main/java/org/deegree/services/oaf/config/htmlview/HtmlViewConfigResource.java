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

import org.deegree.services.jaxb.ogcapi.htmlview.HtmlView;
import org.deegree.workspace.Resource;
import org.deegree.workspace.ResourceMetadata;
import org.deegree.workspace.Workspace;
import org.slf4j.Logger;

import java.io.File;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * {@link Resource} parsing the {@link HtmlViewConfiguration}.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class HtmlViewConfigResource implements Resource {

	private static final Logger LOG = getLogger(HtmlViewConfigResource.class);

	private final ResourceMetadata<HtmlViewConfigResource> metadata;

	private final Workspace workspace;

	private final HtmlView config;

	private HtmlViewConfiguration htmlViewConfiguration;

	public HtmlViewConfigResource(ResourceMetadata<HtmlViewConfigResource> metadata, Workspace workspace,
			HtmlView config) {
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
		htmlViewConfiguration = parseHtmlViewConfiguration();
		LOG.debug("Using HTML view configuration: " + htmlViewConfiguration);
	}

	@Override
	public void destroy() {

	}

	public HtmlViewConfiguration getHtmlViewConfiguration() {
		return htmlViewConfiguration;
	}

	private HtmlViewConfiguration parseHtmlViewConfiguration() {
		if (config == null)
			return null;
		File cssFile = null;
		String configuredCssFile = config.getCssFile();
		if (configuredCssFile != null) {
			cssFile = this.metadata.getLocation().resolveToFile(configuredCssFile);
			if (!cssFile.exists() || !cssFile.isFile()) {
				LOG.warn("Configured cssFile does not exist or is not a valid file");
			}
		}
		String wmsUrl = null;
		String wmsVersion = null;
		String wmsLayers = null;
		String crsCode = null;
		String crsProj4Definition = null;
		String source = null;

		HtmlView.Map map = config.getMap();
		if (map != null) {
			wmsUrl = map.getWMSUrl().getValue();
			wmsVersion = map.getWMSUrl().getVersion();
			wmsLayers = map.getWMSLayers();
			if (map.getCrsProj4Definition() != null) {
				crsCode = map.getCrsProj4Definition().getCode();
				crsProj4Definition = map.getCrsProj4Definition().getValue();
			}
			source = map.getSource();
		}
		return new HtmlViewConfiguration(cssFile, config.getLegalNoticeUrl(), config.getPrivacyPolicyUrl(),
				config.getDocumentationUrl(), wmsUrl, wmsVersion, wmsLayers, crsCode, crsProj4Definition, source);
	}

}
