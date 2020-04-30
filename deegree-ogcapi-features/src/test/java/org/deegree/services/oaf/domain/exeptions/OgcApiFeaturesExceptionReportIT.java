package org.deegree.services.oaf.domain.exeptions;

import org.deegree.services.oaf.domain.exceptions.OgcApiFeaturesExceptionReport;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import java.io.ByteArrayOutputStream;
import java.net.URL;

import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_CORE_SCHEMA_URL;
import static org.junit.Assert.assertThat;
import static org.xmlmatchers.XmlMatchers.conformsTo;
import static org.xmlmatchers.transform.XmlConverters.the;
import static org.xmlmatchers.validation.SchemaFactory.w3cXmlSchemaFrom;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class OgcApiFeaturesExceptionReportIT {

    @Test
    public void testExceptionToXml()
                    throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance( OgcApiFeaturesExceptionReport.class );

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
        OgcApiFeaturesExceptionReport exception = new OgcApiFeaturesExceptionReport( "test", 404 );
        marshaller.marshal( exception, bos );

        Schema schema = w3cXmlSchemaFrom( new URL( XML_CORE_SCHEMA_URL ) );
        assertThat( the( bos.toString() ), conformsTo( schema ) );
    }

}