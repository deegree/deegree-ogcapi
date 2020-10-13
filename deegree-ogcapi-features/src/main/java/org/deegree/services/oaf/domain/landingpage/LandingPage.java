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
package org.deegree.services.oaf.domain.landingpage;

import org.deegree.services.oaf.domain.OafDomainResource;
import org.deegree.services.oaf.link.Link;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_ATOM_NS_URL;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_CORE_NS_URL;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@XmlRootElement(name = "LandingPage", namespace = XML_CORE_NS_URL)
public class LandingPage extends OafDomainResource {

    @XmlElement(name = "Title", namespace = XML_CORE_NS_URL)
    private String title;

    @XmlElement(name = "Description", namespace = XML_CORE_NS_URL)
    private String description;

    @XmlTransient
    private Contact contact;

    @XmlElement(name = "link", namespace = XML_ATOM_NS_URL)
    private List<Link> links;

    public LandingPage() {
    }

    public LandingPage( String title, String description, List<Link> links ) {
        this.title = title;
        this.description = description;
        this.links = links;
    }

    public LandingPage( String title, String description, Contact contact, List<Link> links ) {
        this.title = title;
        this.description = description;
        this.contact = contact;
        this.links = links;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact( Contact contact ) {
        this.contact = contact;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks( List<Link> links ) {
        this.links = links;
    }
}
