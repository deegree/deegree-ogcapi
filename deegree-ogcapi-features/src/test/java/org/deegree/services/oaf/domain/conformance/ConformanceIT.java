package org.deegree.services.oaf.domain.conformance;

import org.deegree.services.oaf.link.Link;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Collections;

import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_CORE_SCHEMA_URL;
import static org.deegree.services.oaf.domain.conformance.ConformanceClass.HTML;
import static org.junit.Assert.assertThat;
import static org.xmlmatchers.XmlMatchers.conformsTo;
import static org.xmlmatchers.transform.XmlConverters.the;
import static org.xmlmatchers.validation.SchemaFactory.w3cXmlSchemaFrom;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class ConformanceIT {

    @Test
    public void testConformanceToXml()
                    throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance( Conformance.class );

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
        Conformance conformance = new Conformance(
                        Collections.singletonList( new Link( HTML.getConformanceClass() ) ) );
        marshaller.marshal( conformance, bos );

        Schema schema = w3cXmlSchemaFrom( new URL( XML_CORE_SCHEMA_URL ) );
        assertThat( the( bos.toString() ), conformsTo( schema ) );
    }

}
