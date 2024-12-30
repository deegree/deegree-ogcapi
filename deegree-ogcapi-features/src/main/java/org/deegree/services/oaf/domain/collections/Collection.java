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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.deegree.services.oaf.link.Link;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_ATOM_NS_URL;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_CORE_NS_URL;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@XmlRootElement(name = "Collection", namespace = XML_CORE_NS_URL)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonPropertyOrder({ "id", "title", "description", "links", "extent" })
public class Collection {

	@XmlElement(name = "Id", namespace = XML_CORE_NS_URL)
	@JsonProperty("id")
	private String id;

	@XmlElement(name = "Title", namespace = XML_CORE_NS_URL)
	@JsonProperty("title")
	@JsonInclude(NON_NULL)
	private String title;

	@XmlElement(name = "Description", namespace = XML_CORE_NS_URL)
	@JsonProperty("description")
	@JsonInclude(NON_NULL)
	private String description;

	@XmlElement(name = "link", namespace = XML_ATOM_NS_URL)
	@JsonProperty("links")
	private List<Link> links;

	@XmlElement(name = "Extent", namespace = XML_CORE_NS_URL)
	@JsonInclude(NON_NULL)
	private Extent extent;

	@JsonProperty("itemType")
	@XmlTransient
	private String itemType = "feature";

	@JsonInclude(NON_NULL)
	@JsonProperty("crs")
	@XmlTransient
	private List<String> crs;

	@JsonInclude(NON_NULL)
	@JsonProperty("storageCrs")
	@XmlTransient
	private String storageCrs;

	public Collection() {
	}

	public Collection(String id, String title, String description, List<Link> links, Extent extent, List<String> crs,
			String storageCrs) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.links = links;
		this.extent = extent;
		this.crs = crs;
		this.storageCrs = storageCrs;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Extent getExtent() {
		return extent;
	}

	public void setExtent(Extent extent) {
		this.extent = extent;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public void addAdditionalLinks(List<Link> links) {
		this.links.addAll(links);
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public List<String> getCrs() {
		return crs;
	}

	public void setCrs(List<String> crs) {
		this.crs = crs;
	}

	public String getStorageCrs() {
		return storageCrs;
	}

	public void setStorageCrs(String storageCrs) {
		this.storageCrs = storageCrs;
	}

}
