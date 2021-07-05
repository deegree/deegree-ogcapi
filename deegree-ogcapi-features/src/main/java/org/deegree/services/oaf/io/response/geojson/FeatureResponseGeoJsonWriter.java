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

import org.deegree.cs.exceptions.TransformationException;
import org.deegree.cs.exceptions.UnknownCRSException;
import org.deegree.feature.Feature;
import org.deegree.feature.stream.FeatureInputStream;
import org.deegree.geojson.GeoJsonWriter;
import org.deegree.services.oaf.io.response.FeatureResponse;
import org.slf4j.Logger;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Provider
@Produces("application/geo+json")
public class FeatureResponseGeoJsonWriter extends AbstractFeatureResponseGeoJsonWriter<FeatureResponse> {

    private static final Logger LOG = getLogger( FeatureResponseGeoJsonWriter.class );

    @Override
    public boolean isWriteable( Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType ) {
        return FeatureResponse.class == type;
    }

    @Override
    protected void writeContent( FeatureResponse feature, GeoJsonWriter geoJsonStreamWriter )
                    throws IOException, TransformationException, UnknownCRSException {
        writeFeature( feature, geoJsonStreamWriter );
        writeLinks( feature.getLinks(), geoJsonStreamWriter );
        writeCrs( feature.getResponseCrsName(), geoJsonStreamWriter );
    }

    private int writeFeature( FeatureResponse feature, GeoJsonWriter writer )
                    throws IOException, TransformationException, UnknownCRSException {
        int writtenFeatures = 0;
        FeatureInputStream featureInputStream = feature.getFeature();
        try {
            for ( Feature featureItem : featureInputStream ) {
                writer.write( featureItem );
                writtenFeatures++;
            }
            if ( writtenFeatures > 0 )
                writer.endArray();
            return writtenFeatures;
        } finally {
            featureInputStream.close();
        }
    }

}
