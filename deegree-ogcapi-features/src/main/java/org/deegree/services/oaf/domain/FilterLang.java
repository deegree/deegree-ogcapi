/*-
 * #%L
 * deegree-ogcapi-features - OGC API Features (OAF) implementation - Querying and modifying of geospatial data objects
 * %%
 * Copyright (C) 2019 - 2024 lat/lon GmbH, info@lat-lon.de, www.lat-lon.de
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
package org.deegree.services.oaf.domain;

import org.deegree.services.oaf.exceptions.InvalidParameterValue;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public enum FilterLang {

	CQL2_TEXT("cql2-text");

	private final String type;

	FilterLang(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public static FilterLang fromType(String filterLang) throws InvalidParameterValue {
		if (filterLang == null)
			return null;
		for (FilterLang b : FilterLang.values()) {
			if (b.getType().equals(filterLang)) {
				return b;
			}
		}
		String allowedValues = Arrays.stream(FilterLang.values())
			.map(FilterLang::getType)
			.collect(Collectors.joining(", "));
		throw new InvalidParameterValue("filter-lang", "Supported values are: " + allowedValues);
	}

	@Override
	public String toString() {
		return type;
	}

}
