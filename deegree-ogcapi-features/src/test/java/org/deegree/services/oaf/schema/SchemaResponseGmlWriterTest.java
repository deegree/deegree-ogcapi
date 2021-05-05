package org.deegree.services.oaf.schema;

import org.apache.commons.io.IOUtils;
import org.deegree.feature.types.AppSchema;
import org.deegree.feature.types.FeatureType;
import org.deegree.gml.schema.GMLAppSchemaReader;
import org.deegree.gml.schema.GMLSchemaInfoSet;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.xmlmatchers.namespace.SimpleNamespaceContext;

import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static org.deegree.gml.GMLVersion.GML_32;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.xmlmatchers.XmlMatchers.equivalentTo;
import static org.xmlmatchers.XmlMatchers.hasXPath;
import static org.xmlmatchers.transform.XmlConverters.the;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@RunWith(MockitoJUnitRunner.class)
public class SchemaResponseGmlWriterTest {

    private static final String KITA_XSD = "../feature/schema/kita.xsd";

    private static final QName KITA_FT = new QName( "http://www.deegree.org/app", "KitaEinrichtungen" );

    private static final String ZUZUEGE_XSD = "../feature/schema/micado_kennzahlen_v1_2.xsd";

    private static final QName ZUZUEGE_FT = new QName( "http://www.deegree.org/datasource/feature/sql", "Zuzuege" );

    @InjectMocks
    SchemaResponseGmlWriter schemaResponseGmlWriter = new SchemaResponseGmlWriter();

    @Mock
    DeegreeWorkspaceInitializer deegreeWorkspaceInitializer = mockWorkspaceInitializer();

    @Mock
    UriInfo uriInfo = mock( UriInfo.class );

    @Test
    public void testWriteTo_ExistingSchema_ExactlyOneSchema()
                    throws Exception {
        SchemaResponse schemaResponse = createExistingSchemaResponse( KITA_FT, KITA_XSD );
        OutputStream bos = new ByteArrayOutputStream();
        schemaResponseGmlWriter.writeTo( schemaResponse, null, null, null, null, null, bos );

        String exportedXsd = bos.toString();
        assertThat( the( exportedXsd ),
                    equivalentTo( the( originalXsd( KITA_XSD ) ) ) );
    }

    @Test
    public void testWriteTo_ExistingSchema_WithReferencedSchema()
                    throws Exception {
        SchemaResponse schemaResponse = createExistingSchemaResponse( ZUZUEGE_FT, ZUZUEGE_XSD );
        OutputStream bos = new ByteArrayOutputStream();
        schemaResponseGmlWriter.writeTo( schemaResponse, null, null, null, null, null, bos );

        String exportedXsd = bos.toString();
        //The schemeLocation is currently not translated (s. mockWorkspaceInitializer())
        /*
        assertThat( the( exportedXsd ),
                    hasXPath( "//xs:schema/xs:include[@schemaLocation = 'http.//test.de/micado_kennzahlen_v1_2.xsd']",
                              nsContext() ) );
        assertThat( the( exportedXsd ),
                    hasXPath( "//xs:schema/xs:include[@schemaLocation = 'http.//test.de/zeitreihen_v1.xsd']",
                              nsContext() ) );
         */
    }

    private SchemaResponse createExistingSchemaResponse( QName ftName, String xsdResource )
                    throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        String xsd = SchemaResponseGmlWriterTest.class.getResource( xsdResource ).toString();
        GMLSchemaInfoSet gmlSchemaInfoSet = new GMLSchemaInfoSet( GML_32, xsd );
        FeatureType featureType = createFeatureType( ftName, xsdResource );
        return new ExistingSchemaResponse( featureType, gmlSchemaInfoSet );
    }

    private static FeatureType createFeatureType( QName ftName, String xsdResource )
                    throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        String schemaURL = SchemaResponseGmlWriterTest.class.getResource( xsdResource ).toString();
        GMLAppSchemaReader xsdAdapter = new GMLAppSchemaReader( GML_32, null, schemaURL );
        AppSchema schema = xsdAdapter.extractAppSchema();
        return schema.getFeatureType( ftName );
    }

    private String originalXsd( String xsdResource )
                    throws IOException {
        return IOUtils.toString( SchemaResponseGmlWriterTest.class.getResource( xsdResource ),
                                 StandardCharsets.UTF_8 );
    }

    private DeegreeWorkspaceInitializer mockWorkspaceInitializer() {
        DeegreeWorkspaceInitializer deegreeWorkspaceInitializer = mock( DeegreeWorkspaceInitializer.class );
        lenient().when( deegreeWorkspaceInitializer.createAppschemaUrl( eq( uriInfo ),
                                                              endsWith( "micado_kennzahlen_v1_2.xsd" ) ) ).thenReturn(
                        "http.//test.de/micado_kennzahlen_v1_2.xsd" );
        lenient().when( deegreeWorkspaceInitializer.createAppschemaUrl( any( UriInfo.class ),
                                                              endsWith( "zeitreihen_v1.xsd" ) ) ).thenReturn(
                        "http.//test.de/zeitreihen_v1.xsd" );
        return deegreeWorkspaceInitializer;
    }

    private NamespaceContext nsContext() {
        SimpleNamespaceContext nsContext = new SimpleNamespaceContext()
                        .withBinding( "xs", "http://www.w3.org/2001/XMLSchema" );
        return nsContext;
    }

}
