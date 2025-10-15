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
package org.deegree.services.oaf.io.response;

import org.deegree.feature.FeatureCollection;
import org.deegree.feature.stream.EmptyFeatureInputStream;
import org.deegree.feature.stream.FeatureInputStream;
import org.deegree.feature.stream.MemoryFeatureInputStream;
import org.deegree.gml.GMLInputFactory;
import org.deegree.gml.GMLStreamReader;
import org.deegree.services.oaf.OgcApiFeaturesConstants;
import org.deegree.services.oaf.io.response.geojson.FeaturesResponseGeoJsonWriter;
import org.deegree.services.oaf.link.Link;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasNoJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static org.deegree.gml.GMLVersion.GML_32;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
class FeaturesResponseGeoJsonWriterTest {

	@Test
	void writeTo() throws Exception {
		FeaturesResponseGeoJsonWriter featureResponeWriter = new FeaturesResponseGeoJsonWriter();
		FeaturesResponse featureResponse = createFeatureResponse();
		OutputStream bos = new ByteArrayOutputStream();
		featureResponeWriter.writeTo(featureResponse, null, null, null, null, null, bos);

		String json = bos.toString();

		assertThat(json, isJson());
		assertThat(json, hasJsonPath("$.type", equalTo("FeatureCollection")));
		assertThat(json, hasJsonPath("$.features"));
		assertThat(json, hasJsonPath("$.links", Matchers.hasSize(1)));
		assertThat(json, hasJsonPath("$.crs", equalTo(OgcApiFeaturesConstants.DEFAULT_CRS)));
	}

	@Test
	void writeToEmptyFeatureResponse() {
		FeaturesResponseGeoJsonWriter featureResponeWriter = new FeaturesResponseGeoJsonWriter();
		FeaturesResponse featureResponse = createEmptyFeaturesResponse();
		OutputStream bos = new ByteArrayOutputStream();
		featureResponeWriter.writeTo(featureResponse, null, null, null, null, null, bos);

		String json = bos.toString();

		assertThat(json, isJson());
		assertThat(json, hasJsonPath("$.type", equalTo("FeatureCollection")));
		assertThat(json, hasJsonPath("$.features", Matchers.hasSize(0)));
		assertThat(json, hasJsonPath("$.links", Matchers.hasSize(1)));
		assertThat(json, hasNoJsonPath("$.crs"));
	}

	private FeaturesResponse createEmptyFeaturesResponse() {
		List<Link> links = java.util.Collections
			.singletonList(new Link("http://self", "self", "application/json", "title"));
		FeatureInputStream featureStream = new EmptyFeatureInputStream();
		Map<String, String> featureTypeNsPrefixes = Collections.emptyMap();
		return new FeaturesResponseBuilder(featureStream).withFeatureTypeNsPrefixes(featureTypeNsPrefixes)
			.withNumberOfFeatures(10)
			.withNumberOfFeaturesMatched(100)
			.withStartIndex(0)
			.withLinks(links)
			.withMaxFeaturesAndStartIndexApplicable(false)
			.buildFeaturesResponse();
	}

	private FeaturesResponse createFeatureResponse() throws Exception {
		List<Link> links = java.util.Collections
			.singletonList(new Link("http://self", "self", "application/json", "title"));
		GMLStreamReader gmlReader = GMLInputFactory.createGMLStreamReader(GML_32,
				getClass().getResource("../strassenbaumkataster.gml"));
		FeatureCollection featureCollection = gmlReader.readFeatureCollection();
		FeatureInputStream featureStream = new MemoryFeatureInputStream(featureCollection);
		Map<String, String> featureTypeNsPrefixes = Collections.emptyMap();
		return new FeaturesResponseBuilder(featureStream).withFeatureTypeNsPrefixes(featureTypeNsPrefixes)
			.withNumberOfFeatures(10)
			.withNumberOfFeaturesMatched(100)
			.withStartIndex(0)
			.withLinks(links)
			.withMaxFeaturesAndStartIndexApplicable(false)
			.withResponseCrsName(OgcApiFeaturesConstants.DEFAULT_CRS)
			.buildFeaturesResponse();
	}

}
