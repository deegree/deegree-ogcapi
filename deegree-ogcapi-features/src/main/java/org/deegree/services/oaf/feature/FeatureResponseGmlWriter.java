package org.deegree.services.oaf.feature;

import org.deegree.cs.exceptions.TransformationException;
import org.deegree.cs.exceptions.UnknownCRSException;
import org.deegree.feature.Feature;
import org.deegree.feature.stream.FeatureInputStream;
import org.deegree.gml.GMLOutputFactory;
import org.deegree.gml.GMLStreamWriter;
import org.deegree.gml.GMLVersion;
import org.deegree.gml.feature.GMLFeatureWriter;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_SF_NS_URL;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Provider
@Produces({ APPLICATION_GML })
public class FeatureResponseGmlWriter implements MessageBodyWriter<FeatureResponse> {

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
        GMLStreamWriter gmlStreamWriter = null;
        try {
            XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newFactory().createXMLStreamWriter( out );
            gmlStreamWriter = GMLOutputFactory.createGMLStreamWriter( GMLVersion.GML_32,
                                                                      xmlStreamWriter );
            Map<String, String> prefixToNs = new HashMap<>();
            prefixToNs.putAll( features.getFeatureTypeNsPrefixes() );
            gmlStreamWriter.setNamespaceBindings( prefixToNs );
            GMLFeatureWriter featureWriter = new GMLFeatureWriter( gmlStreamWriter );

            xmlStreamWriter.writeStartElement( "sf", "FeatureCollection", XML_SF_NS_URL );
            xmlStreamWriter.writeNamespace( "sf", XML_SF_NS_URL );
            xmlStreamWriter.writeNamespace( "xsi", "http://www.w3.org/2001/XMLSchema-instance" );

            writeFeatures( features.getFeatures(), xmlStreamWriter, featureWriter );

            xmlStreamWriter.writeEndElement();

        } catch ( Exception ex ) {
            throw new WebApplicationException( ex );
        } finally {
            if ( gmlStreamWriter != null ) {
                try {
                    gmlStreamWriter.close();
                } catch ( XMLStreamException e ) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void writeFeatures( FeatureInputStream featureStream, XMLStreamWriter xmlStreamWriter,
                                GMLFeatureWriter featureWriter )
                    throws XMLStreamException, UnknownCRSException, TransformationException {
        try {
            for ( Feature feature : featureStream ) {
                xmlStreamWriter.writeStartElement( "sf", "featureMember", XML_SF_NS_URL );
                featureWriter.export( feature );
                xmlStreamWriter.writeEndElement();
            }
        } finally {
            featureStream.close();
        }
    }

}