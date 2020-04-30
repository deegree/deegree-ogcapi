package org.deegree.services.oaf.domain.collections;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.deegree.services.oaf.TestData;
import org.deegree.services.oaf.link.Link;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.deegree.services.oaf.OgcApiFeaturesConstants.DEFAULT_CRS;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_CORE_SCHEMA_URL;
import static org.deegree.services.oaf.TestData.createCollection;
import static org.deegree.services.oaf.TestData.createCollections;
import static org.junit.Assert.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;
import static org.xmlmatchers.XmlMatchers.conformsTo;
import static org.xmlmatchers.transform.XmlConverters.the;
import static org.xmlmatchers.validation.SchemaFactory.w3cXmlSchemaFrom;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class CollectionsIT {

    @Test
    public void testCollectionsToXml()
                    throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance( Collections.class );

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
        Collections collections = createCollections();
        marshaller.marshal( collections, bos );

        Schema schema = w3cXmlSchemaFrom( new URL( XML_CORE_SCHEMA_URL ) );
        assertThat( the( bos.toString() ), conformsTo( schema ) );
    }

    @Test
    public void testCollectionsToJson()
                    throws Exception {
        Collections collections = createCollections();
        ObjectMapper objectMapper = new ObjectMapper();
        String actual = objectMapper.writeValueAsString( collections );

        assertEquals( expected( "expectedCollections.json" ), actual, LENIENT );
    }

    @Test
    public void testCollectionToJson()
                    throws Exception {
        Collection collection = createCollection();
        ObjectMapper objectMapper = new ObjectMapper();
        String actual = objectMapper.writeValueAsString( collection );

        assertEquals( expected( "expectedCollection.json" ), actual, LENIENT );
    }

    private String expected( String s2 )
                    throws IOException {
        return IOUtils.toString( getClass().getResourceAsStream( s2 ), StandardCharsets.UTF_8 );
    }

}
