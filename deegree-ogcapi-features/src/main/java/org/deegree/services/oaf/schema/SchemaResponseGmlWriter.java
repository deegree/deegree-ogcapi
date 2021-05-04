package org.deegree.services.oaf.schema;

import org.deegree.feature.types.FeatureType;
import org.deegree.gml.schema.GMLAppSchemaWriter;
import org.deegree.gml.schema.GMLSchemaInfoSet;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;

import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
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

    @Inject
    private DeegreeWorkspaceInitializer deegreeWorkspaceInitializer;

    @Context
    private UriInfo uriInfo;

    @Override
    public boolean isWriteable( Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType ) {
        return SchemaResponse.class.isAssignableFrom( aClass );
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
            writer = XMLOutputFactory.newFactory().createXMLStreamWriter( outputStream );
            FeatureType featureType = schemaResponse.getFeatureType();
            write( schemaResponse, writer, featureType );
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

    private void write( SchemaResponse schemaResponse, XMLStreamWriter writer, FeatureType featureType )
                    throws XMLStreamException, IOException {
        if ( schemaResponse instanceof GeneratedSchemaResponse ) {
            write( (GeneratedSchemaResponse) schemaResponse, writer, featureType );
        } else if ( schemaResponse instanceof ExistingSchemaResponse ) {
            write( (ExistingSchemaResponse) schemaResponse, writer, featureType );
        } else {
            throw new IllegalArgumentException( "SchemaResponse could not be handled" );
        }
    }

    private void write( GeneratedSchemaResponse schemaResponse, XMLStreamWriter writer, FeatureType featureType )
                    throws XMLStreamException {
        String featureTypeNamespaceURI = featureType.getName().getNamespaceURI();
        GMLAppSchemaWriter exporter = new GMLAppSchemaWriter( GML_32, featureTypeNamespaceURI,
                                                              schemaResponse.getNsToSchemaLocation(),
                                                              schemaResponse.getPrefixToNs() );

        exporter.export( writer, Collections.singletonList( featureType ) );
    }

    private void write( ExistingSchemaResponse schemaResponse, XMLStreamWriter writer, FeatureType featureType )
                    throws XMLStreamException, IOException {
        String featureTypeNamespaceURI = featureType.getName().getNamespaceURI();
        GMLSchemaInfoSet gmlSchema = schemaResponse.getGmlSchema();
        GMLAppSchemaWriter.export( writer, gmlSchema, featureTypeNamespaceURI, uri -> {
            String appschemaUri = deegreeWorkspaceInitializer.createAppschemaUrl( uriInfo, uri );
            if ( appschemaUri != null )
                return appschemaUri;
            return uri;
        } );
    }

}
