package org.deegree.services.oaf.io.response.geojson;

import org.deegree.cs.coordinatesystems.ICRS;
import org.deegree.cs.exceptions.TransformationException;
import org.deegree.cs.exceptions.UnknownCRSException;
import org.deegree.cs.persistence.CRSManager;
import org.deegree.cs.refs.coordinatesystem.CRSRef;
import org.deegree.geojson.GeoJsonWriter;
import org.deegree.services.oaf.exceptions.UnknownFeatureId;
import org.deegree.services.oaf.io.response.AbstractFeatureResponse;
import org.deegree.services.oaf.link.Link;
import org.slf4j.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public abstract class AbstractFeatureResponseGeoJsonWriter<T extends AbstractFeatureResponse>
                implements MessageBodyWriter<T> {

    private static final Logger LOG = getLogger( FeaturesResponseGeoJsonWriter.class );

    @Override
    public long getSize( T features, Class<?> type, Type genericType, Annotation[] annotations,
                         MediaType mediaType ) {
        // deprecated by JAX-RS 2.0 and ignored by Jersey runtime
        return 0;
    }

    @Override
    public void writeTo( T feature, Class<?> type, Type genericType, Annotation[] annotations,
                         MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream out )
                    throws WebApplicationException {
        try (
                        Writer writer = new PrintWriter( out );
                        GeoJsonWriter geoJsonStreamWriter = new GeoJsonWriter( writer, asCrs( feature ) ) ) {
            writeContent( feature, geoJsonStreamWriter );
        } catch ( Exception e ) {
            LOG.error( "Writing response failed", e );
            throw new WebApplicationException( e );
        }
    }

    protected abstract void writeContent( T feature, GeoJsonWriter geoJsonStreamWriter )
                    throws IOException,
                    TransformationException, UnknownCRSException, UnknownFeatureId;

    protected ICRS asCrs( T feature ) {
        if ( feature.getResponseCrsName() != null ) {
            CRSRef ref = CRSManager.getCRSRef( feature.getResponseCrsName() );
            ref.getReferencedObject(); // test if exists
            return ref;
        }
        return null;
    }

    protected void writeLinks( List<Link> links, GeoJsonWriter writer )
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

    protected void writeCrs( String crs, GeoJsonWriter writer )
                    throws IOException {
        if ( crs != null )
            writer.name( "crs" ).value( crs );
    }
}
