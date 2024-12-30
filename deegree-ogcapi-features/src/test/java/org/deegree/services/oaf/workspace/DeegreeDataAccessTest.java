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
package org.deegree.services.oaf.workspace;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.deegree.cs.coordinatesystems.ICRS;
import org.deegree.cs.exceptions.UnknownCRSException;
import org.deegree.cs.persistence.CRSManager;
import org.junit.Test;

public class DeegreeDataAccessTest {

	@Test
	public void test_selectCrsCode_ETRS89() throws UnknownCRSException {
		String lookupCode = "http://www.opengis.net/def/crs/EPSG/0/4258";
		String code = selectCode(lookupCode, Arrays.asList(lookupCode));
		assertThat(code, is(lookupCode));
	}

	@Test
	public void test_selectCrsCode_WGS84() throws UnknownCRSException {
		String lookupCode = "http://www.opengis.net/def/crs/EPSG/0/4326";
		String code = selectCode(lookupCode, Arrays.asList(lookupCode));
		assertThat(code, is(lookupCode));
	}

	@Test
	public void test_selectCrsCode_ETRS89_AlternateCode() throws UnknownCRSException {
		String lookupCode = "urn:ogc:def:crs:EPSG::4258";
		String allowedCode = "http://www.opengis.net/def/crs/EPSG/0/4258";
		String code = selectCode(lookupCode, Arrays.asList(allowedCode));
		assertThat(code, is(allowedCode));
	}

	@Test
	public void test_selectCrsCode_NoMatch() throws UnknownCRSException {
		String lookupCode = "http://www.opengis.net/def/crs/EPSG/0/4326";
		ICRS crs = CRSManager.lookup(lookupCode);
		String code = selectCode(lookupCode, Arrays.asList("EPSG:12345"));
		assertThat(code, is(crs.getCode().getOriginal()));
	}

	private String selectCode(String crsCode, List<String> supportedCodes) throws UnknownCRSException {
		ICRS crs = CRSManager.lookup(crsCode);
		return DeegreeDataAccess.selectCrsCode(Arrays.asList(crs.getOrignalCodeStrings()), supportedCodes);
	}

}
