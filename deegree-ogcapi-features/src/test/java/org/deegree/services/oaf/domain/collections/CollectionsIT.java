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
package org.deegree.services.oaf.domain.collections;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.xmlunit.matchers.ValidationMatcher;
import org.xmlunit.validation.Validator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_CORE_SCHEMA_URL;
import static org.deegree.services.oaf.TestData.createCollection;
import static org.deegree.services.oaf.TestData.createCollections;
import static org.junit.Assert.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;

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

        assertThat( bos.toString(), ValidationMatcher.valid( schemaFrom( XML_CORE_SCHEMA_URL ) ) );
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

    private StreamSource schemaFrom( String url )
                    throws IOException {
        return new StreamSource( new URL( url ).openStream() );
    }
}
