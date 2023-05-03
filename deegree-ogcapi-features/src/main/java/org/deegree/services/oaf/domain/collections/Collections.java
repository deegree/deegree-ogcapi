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
package org.deegree.services.oaf.domain.collections;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.deegree.services.oaf.domain.OafDomainResource;
import org.deegree.services.oaf.link.Link;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_ATOM_NS_URL;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_CORE_NS_URL;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@XmlRootElement(name = "Collections", namespace = XML_CORE_NS_URL)
public class Collections extends OafDomainResource {

    @XmlElement(name = "link", namespace = XML_ATOM_NS_URL)
    @JsonProperty("links")
    private List<Link> links;

    @XmlElement(name = "Collection", namespace = XML_CORE_NS_URL)
    @JsonProperty("collections")
    private List<Collection> collections;

    public Collections() {
    }

    public Collections( List<Link> links, List<Collection> collections ) {
        this.links = links;
        this.collections = collections;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks( List<Link> links ) {
        this.links = links;
    }
    
    public void addAdditionalLinks( List<Link> links ) {
        this.links.addAll(links);
    }

    public List<Collection> getCollections() {
        return collections;
    }

    public void setCollections( List<Collection> collections ) {
        this.collections = collections;
    }

    public void addCollection( Collection collection ) {
        if ( collections == null )
            collections = new ArrayList<>();
        collections.add( collection );
    }
}
