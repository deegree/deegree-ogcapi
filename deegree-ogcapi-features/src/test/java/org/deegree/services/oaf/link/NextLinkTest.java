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
package org.deegree.services.oaf.link;

import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
class NextLinkTest {

	@Test
	void createLinkFirstPage() {
		NextLink nextLink = new NextLink(10, 2, 0);

		UriInfo uriInfo = createUriInfo("http://localhost:8080/oafcollections/buildings/items?limit=2");
		String uri = nextLink.createUri(uriInfo);

		assertThat(uri, is("http://localhost:8080/oafcollections/buildings/items?offset=2&limit=2"));
	}

	@Test
	void createLinkSecondPage() {
		NextLink nextLink = new NextLink(10, 2, 2);

		UriInfo uriInfo = createUriInfo("http://localhost:8080/oafcollections/buildings/items?limit=2&offset=2");
		String uri = nextLink.createUri(uriInfo);

		assertThat(uri, is("http://localhost:8080/oafcollections/buildings/items?offset=4&limit=2"));
	}

	@Test
	void createLinkLastPage() {
		NextLink nextLink = new NextLink(10, 2, 8);

		UriInfo uriInfo = createUriInfo("http://localhost:8080/oafcollections/buildings/items?limit=2&offset=8");
		String uri = nextLink.createUri(uriInfo);

		assertThat(uri, is(nullValue()));
	}

	private UriInfo createUriInfo(String fromUri) {
		UriInfo uriInfo = mock(UriInfo.class);
		UriBuilder uriBuilder = UriBuilder.fromUri(fromUri);
		when(uriInfo.getRequestUriBuilder()).thenReturn(uriBuilder);
		return uriInfo;
	}

}
