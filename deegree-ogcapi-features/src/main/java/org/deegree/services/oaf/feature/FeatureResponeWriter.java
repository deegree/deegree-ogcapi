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
package org.deegree.services.oaf.feature;

import org.deegree.commons.tom.datetime.ISO8601Converter;
import org.deegree.cs.coordinatesystems.ICRS;
import org.deegree.cs.exceptions.TransformationException;
import org.deegree.cs.exceptions.UnknownCRSException;
import org.deegree.cs.persistence.CRSManager;
import org.deegree.cs.refs.coordinatesystem.CRSRef;
import org.deegree.feature.Feature;
import org.deegree.feature.stream.FeatureInputStream;
import org.deegree.geojson.GeoJsonWriter;
import org.deegree.services.oaf.link.Link;
import org.slf4j.Logger;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Provider
@Produces("application/geo+json")
public class FeatureResponeWriter implements MessageBodyWriter<FeatureResponse> {

    private static final Logger LOG = getLogger( FeatureResponeWriter.class );

    @Override
    public boolean isWriteable( Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType ) {
        return FeatureResponse.class == type;
    }

    @Override
    public long getSize( FeatureResponse features, Class<?> type, Type genericType, Annotation[] annotations,
                         MediaType mediaType ) {
        // deprecated by JAX-RS 2.0 and ignored by Jersey runtime
        return 0;
    }

    @Override
    public void writeTo( FeatureResponse features, Class<?> type, Type genericType, Annotation[] annotations,
                         MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream out )
                    throws WebApplicationException {
        try (
                        Writer writer = new PrintWriter( out );
                        GeoJsonWriter geoJsonStreamWriter = new GeoJsonWriter( writer, asCrs( features ) ) ) {
            geoJsonStreamWriter.startFeatureCollection();
            int numberReturned = writeFeatures( features, geoJsonStreamWriter );
            writeLinks( features.getLinks(), geoJsonStreamWriter );
            writeNumberMatched( features.getNumberOfFeaturesMatched(), geoJsonStreamWriter );
            writeNumberReturned( numberReturned, geoJsonStreamWriter );
            writeTimeStamp( geoJsonStreamWriter );
            writeCrs( features.getResponseCrsName(), geoJsonStreamWriter );
            // Closes the feature array as well as the object. Links could not be written later.
            // geoJsonStreamWriter.endFeatureCollection();
            geoJsonStreamWriter.endObject();
        } catch ( Exception e ) {
            LOG.error( "Writing response failed", e );
            throw new WebApplicationException( e );
        }
    }

    private ICRS asCrs( FeatureResponse features )
                    throws UnknownCRSException {
        if ( features.getResponseCrsName() != null ) {
            CRSRef ref = CRSManager.getCRSRef( features.getResponseCrsName() );
            ref.getReferencedObject(); // test if exists
            return ref;
        }
        return null;
    }

    private int writeFeatures( FeatureResponse features, GeoJsonWriter writer )
                    throws IOException, TransformationException, UnknownCRSException {
        if ( features.isMaxFeaturesAndStartIndexApplicable() ) {
            return writeAllReturnedFeatures( features, writer );
        } else {
            return writeFeaturesAndApplyMaxFeaturesAndStartIndex( features, writer );
        }
    }

    private int writeAllReturnedFeatures( FeatureResponse features, GeoJsonWriter writer )
                    throws IOException, TransformationException, UnknownCRSException {
        int writtenFeatures = 0;
        FeatureInputStream featureInputStream = features.getFeatures();
        try {
            for ( Feature feature : featureInputStream ) {
                writer.write( feature );
                writtenFeatures++;
            }
            if ( writtenFeatures > 0 )
                writer.endArray();
            return writtenFeatures;
        } finally {
            featureInputStream.close();
        }
    }

    private int writeFeaturesAndApplyMaxFeaturesAndStartIndex( FeatureResponse features, GeoJsonWriter writer )
                    throws IOException, TransformationException, UnknownCRSException {
        int maxFeatures = features.getNumberOfFeatures();
        int startIndex = features.getStartIndex();
        int featuresAdded = 0;
        int featuresSkipped = 0;
        FeatureInputStream featureInputStream = features.getFeatures();
        try {
            for ( Feature feature : featureInputStream ) {
                if ( featuresAdded == maxFeatures ) {
                    // limit the number of features written to maxfeatures
                    break;
                }
                if ( featuresSkipped < startIndex ) {
                    featuresSkipped++;
                } else {
                    writer.write( feature );
                    featuresAdded++;
                }
            }
            if ( featuresAdded > 0 )
                writer.endArray();
            return featuresAdded;
        } finally {
            featureInputStream.close();
        }
    }

    private void writeLinks( List<Link> links, GeoJsonWriter writer )
                    throws IOException {
        if ( links != null && !links.isEmpty() ) {
            writer.name( "links" ).beginArray();
            for ( Link link : links ) {
                writer.beginObject();
                writeLink( writer, link );
                writer.endObject();
            }
            writer.endArray();
        }
    }

    private void writeNumberMatched( int numberOfFeatures, GeoJsonWriter writer )
                    throws IOException {
        writer.name( "numberMatched" ).value( numberOfFeatures );
    }

    private void writeNumberReturned( int numberOfFeatures, GeoJsonWriter writer )
                    throws IOException {
        writer.name( "numberReturned" ).value( numberOfFeatures );
    }

    private void writeTimeStamp( GeoJsonWriter writer )
                    throws IOException {
        String now = ISO8601Converter.formatDateTime( new Date() );
        writer.name( "timeStamp" ).value( now );
    }


    private void writeLink( GeoJsonWriter writer, Link link )
                    throws IOException {
        writer.name( "href" ).value( link.getHref() );
        if ( link.getRel() != null )
            writer.name( "rel" ).value( link.getRel() );
        if ( link.getType() != null )
            writer.name( "type" ).value( link.getType() );
        if ( link.getTitle() != null )
            writer.name( "title" ).value( link.getTitle() );
    }

    private void writeCrs( String crs, GeoJsonWriter writer )
                    throws IOException {
        if ( crs != null )
            writer.name( "crs" ).value( crs );
    }

}
