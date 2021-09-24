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
import org.deegree.workspace.ResourceBuilder;
import org.deegree.workspace.ResourceMetadata;
import org.deegree.workspace.Workspace;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class HtmlViewConfigBuilder implements ResourceBuilder<HtmlViewConfigResource> {

    private ResourceMetadata<HtmlViewConfigResource> metadata;

    private Workspace workspace;

    private HtmlView config;

    public HtmlViewConfigBuilder( ResourceMetadata<HtmlViewConfigResource> metadata, Workspace workspace,
                                  HtmlView config ) {
        this.metadata = metadata;
        this.workspace = workspace;
        this.config = config;
    }

    @Override
    public HtmlViewConfigResource build() {
        return new HtmlViewConfigResource( metadata, workspace, config );
    }
}
