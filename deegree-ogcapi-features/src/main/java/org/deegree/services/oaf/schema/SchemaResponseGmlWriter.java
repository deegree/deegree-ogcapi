package org.deegree.services.oaf.schema;

import org.deegree.feature.types.FeatureType;
import org.deegree.gml.schema.GMLAppSchemaWriter;

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
import java.util.Collections;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static org.deegree.gml.GMLVersion.GML_32;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Provider
@Produces({ APPLICATION_XML })
public class SchemaResponseGmlWriter implements MessageBodyWriter<SchemaResponse> {

    @Override
    public boolean isWriteable( Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType ) {
        return SchemaResponse.class == type;
    }

    @Override
    public long getSize( SchemaResponse schemaResponse, Class<?> type, Type genericType,
                         Annotation[] annotations, MediaType mediaType ) {
        // deprecated by JAX-RS 2.0 and ignored by Jersey runtime
        return 0;
    }

    @Override
    public void writeTo( SchemaResponse schemaResponse, Class<?> aClass, Type type, Annotation[] annotations,
                         MediaType mediaType, MultivaluedMap<String, Object> multivaluedMap,
                         OutputStream outputStream )
                    throws WebApplicationException {
        XMLStreamWriter writer = null;
        try {
            FeatureType featureType = schemaResponse.getFeatureType();
            String featureTypeNamespaceURI = featureType.getName().getNamespaceURI();
            GMLAppSchemaWriter exporter = new GMLAppSchemaWriter( GML_32, featureTypeNamespaceURI,
                                                                  schemaResponse.getNsToSchemaLocation(),
                                                                  schemaResponse.getPrefixToNs() );

            writer = XMLOutputFactory.newFactory().createXMLStreamWriter( outputStream );
            exporter.export( writer, Collections.singletonList( featureType ) );
        } catch ( Exception ex ) {
            throw new WebApplicationException( ex );
        } finally {
            if ( writer != null ) {
                try {
                    writer.close();
                } catch ( XMLStreamException e ) {
                    e.printStackTrace();
                }
            }
        }
    }
}
