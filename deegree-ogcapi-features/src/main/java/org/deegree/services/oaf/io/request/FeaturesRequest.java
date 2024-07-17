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
package org.deegree.services.oaf.io.request;

import org.deegree.services.oaf.workspace.configuration.FilterProperty;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class FeaturesRequest {

    private final String collectionId;

    private final int limit;

    private final int offset;

    private final boolean isBulkUpload;

    private final List<Double> bbox;

    private final String bboxCrs;

    private final String datetime;

    private final String responseCrs;

    private final Map<FilterProperty, List<String>> filterRequestProperties;

    private final String filter;

    private final String filterCrs;

    public FeaturesRequest( String collectionId, int limit, int offset, boolean isBulkUpload, List<Double> bbox,
                            String bboxCrs, String datetime, String responseCrs,
                            Map<FilterProperty, List<String>> filterRequestProperties, String filter, String filterCrs ) {
        this.collectionId = collectionId;
        this.limit = limit;
        this.offset = offset;
        this.bbox = bbox;
        this.bboxCrs = bboxCrs;
        this.datetime = datetime;
        this.responseCrs = responseCrs;
        this.isBulkUpload = isBulkUpload;
        this.filterRequestProperties = filterRequestProperties;
        this.filter = filter;
        this.filterCrs = filterCrs;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public int getLimit() {
        return this.limit;
    }

    public int getOffset() {
        return this.offset;
    }

    public boolean isBulkUpload() {
        return isBulkUpload;
    }

    public List<Double> getBbox() {
        return this.bbox;
    }

    public String getBboxCrs() {
        return this.bboxCrs;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getResponseCrs() {
        return responseCrs;
    }

    public Map<FilterProperty, List<String>> getFilterRequestProperties() {
        return filterRequestProperties;
    }

    public String getFilter() {
        return filter;
    }

	public String getFilterCrs() {
		return filterCrs;
	}
}
