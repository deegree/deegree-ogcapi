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
package org.deegree.services.oaf.io.response.geojson;

import org.deegree.commons.tom.datetime.ISO8601Converter;
import org.deegree.cs.exceptions.TransformationException;
import org.deegree.cs.exceptions.UnknownCRSException;
import org.deegree.feature.Feature;
import org.deegree.feature.stream.FeatureInputStream;
import org.deegree.geojson.GeoJsonWriter;
import org.deegree.services.oaf.exceptions.UnknownFeatureId;
import org.deegree.services.oaf.io.response.FeaturesResponse;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Date;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Provider
@Produces("application/geo+json")
public class FeaturesResponseGeoJsonWriter extends AbstractFeatureResponseGeoJsonWriter<FeaturesResponse> {

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return FeaturesResponse.class == type;
	}

	protected void writeContent(FeaturesResponse features, GeoJsonWriter geoJsonStreamWriter)
			throws IOException, TransformationException, UnknownCRSException, UnknownFeatureId {
		geoJsonStreamWriter.startFeatureCollection();
		int numberReturned = writeFeatures(features, geoJsonStreamWriter);
		writeLinks(features.getLinks(), geoJsonStreamWriter);
		writeNumberMatched(features.getNumberOfFeaturesMatched(), geoJsonStreamWriter);
		writeNumberReturned(numberReturned, geoJsonStreamWriter);
		writeTimeStamp(geoJsonStreamWriter);
		writeCrs(features.getResponseCrsName(), geoJsonStreamWriter);
		if (numberReturned == 0) {
			geoJsonStreamWriter.name("features").beginArray().endArray();
		}
		// Closes the feature collection. Used instead of
		// geoJsonStreamWriter.endFeatureCollection().
		// The features array is already closed as links could not be written later.
		geoJsonStreamWriter.endObject();
	}

	private int writeFeatures(FeaturesResponse features, GeoJsonWriter writer)
			throws IOException, TransformationException, UnknownCRSException {
		if (features.isMaxFeaturesAndStartIndexApplicable()) {
			return writeAllReturnedFeatures(features, writer);
		}
		else {
			return writeFeaturesAndApplyMaxFeaturesAndStartIndex(features, writer);
		}
	}

	private int writeAllReturnedFeatures(FeaturesResponse features, GeoJsonWriter writer)
			throws IOException, TransformationException, UnknownCRSException {
		int writtenFeatures = 0;
		FeatureInputStream featureInputStream = features.getFeatures();
		try {
			for (Feature feature : featureInputStream) {
				writer.write(feature);
				writtenFeatures++;
			}
			if (writtenFeatures > 0)
				writer.endArray();
			return writtenFeatures;
		}
		finally {
			featureInputStream.close();
		}
	}

	private int writeFeaturesAndApplyMaxFeaturesAndStartIndex(FeaturesResponse features, GeoJsonWriter writer)
			throws IOException, TransformationException, UnknownCRSException {
		int maxFeatures = features.getNumberOfFeatures();
		int startIndex = features.getStartIndex();
		int featuresAdded = 0;
		int featuresSkipped = 0;
		FeatureInputStream featureInputStream = features.getFeatures();
		try {
			for (Feature feature : featureInputStream) {
				if (featuresAdded == maxFeatures) {
					// limit the number of features written to maxfeatures
					break;
				}
				if (featuresSkipped < startIndex) {
					featuresSkipped++;
				}
				else {
					writer.write(feature);
					featuresAdded++;
				}
			}
			if (featuresAdded > 0)
				writer.endArray();
			return featuresAdded;
		}
		finally {
			featureInputStream.close();
		}
	}

	private void writeNumberMatched(int numberOfFeatures, GeoJsonWriter writer) throws IOException {
		writer.name("numberMatched").value(numberOfFeatures);
	}

	private void writeNumberReturned(int numberOfFeatures, GeoJsonWriter writer) throws IOException {
		writer.name("numberReturned").value(numberOfFeatures);
	}

	private void writeTimeStamp(GeoJsonWriter writer) throws IOException {
		String now = ISO8601Converter.formatDateTime(new Date());
		writer.name("timeStamp").value(now);
	}

}
