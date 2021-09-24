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
package org.deegree.services.oaf.domain.dataset;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.deegree.services.oaf.config.datasets.DatasetsConfiguration;
import org.deegree.services.oaf.domain.landingpage.Contact;
import org.deegree.services.oaf.link.Link;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class Datasets {

    @JsonInclude(NON_NULL)
    private String title;

    @JsonInclude(NON_NULL)
    private String description;

    @JsonInclude(NON_NULL)
    private Contact contact;

    private List<Link> links;

    private List<Dataset> datasets;

    public Datasets() {
        this.links = links;
        this.datasets = datasets;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Contact getContact() {
        return contact;
    }

    public List<Link> getLinks() {
        return links;
    }

    public List<Dataset> getDatasets() {
        return datasets;
    }

    public Datasets withLinks( List<Link> links ) {
        this.links = links;
        return this;
    }

    public Datasets withDatasets( List<Dataset> datasets ) {
        this.datasets = datasets;
        return this;
    }

    public Datasets withDatasetsConfiguration( DatasetsConfiguration datasetsConfiguration ) {
        if ( datasetsConfiguration != null ) {
            this.title = datasetsConfiguration.getTitle();
            this.description = datasetsConfiguration.getDescription();
            org.deegree.services.oaf.config.datasets.Contact contact = datasetsConfiguration.getContact();
            if ( contact != null ) {
                this.contact = new Contact( contact.getName(), contact.getUrl(), contact.getEmail() );
            }
        }
        return this;
    }
}
