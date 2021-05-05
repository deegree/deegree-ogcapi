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
package org.deegree.services.oaf.domain.conformance;

import org.deegree.services.oaf.link.Link;
import org.junit.Test;
import org.xmlunit.matchers.ValidationMatcher;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;

import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_CORE_SCHEMA_URL;
import static org.deegree.services.oaf.domain.conformance.ConformanceClass.HTML;
import static org.junit.Assert.assertThat;

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

        assertThat( bos.toString(), ValidationMatcher.valid( schemaFrom( XML_CORE_SCHEMA_URL ) ) );
    }

    private StreamSource schemaFrom( String url )
                    throws IOException {
        return new StreamSource( new URL( url ).openStream() );
    }

}
