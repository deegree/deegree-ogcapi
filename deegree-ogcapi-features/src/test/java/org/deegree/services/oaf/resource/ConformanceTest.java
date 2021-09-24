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
package org.deegree.services.oaf.resource;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.ws.rs.core.Application;
import java.util.HashMap;
import java.util.Map;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_ATOM_NS_URL;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_CORE_NS_URL;
import static org.deegree.services.oaf.domain.conformance.ConformanceClass.CORE;
import static org.deegree.services.oaf.domain.conformance.ConformanceClass.OPENAPI30;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.HasXPathMatcher.hasXPath;

public class ConformanceTest extends JerseyTest {

    @Override
    protected Application configure() {
        enable( TestProperties.LOG_TRAFFIC );
        return new ResourceConfig( Conformance.class );
    }

    @Test
    public void test_ConformanceDeclaration_Json_ShouldBeAvailable() {
        final String json = target( "/datasets/oaf/conformance" ).request( APPLICATION_JSON_TYPE ).get( String.class );

        assertThat( json, hasJsonPath( "$.conformsTo", hasItem( CORE.getConformanceClass() ) ) );
        assertThat( json, hasJsonPath( "$.conformsTo", hasItem( OPENAPI30.getConformanceClass() ) ) );
    }

    @Test
    public void test_ConformanceDeclaration_Xml_ShouldBeAvailable() {
        final String xml = target( "/datasets/oaf/conformance" ).request( APPLICATION_XML ).get( String.class );

        assertThat( xml,
                    hasXPath( "//core:ConformsTo/atom:link[@href = '" + CORE.getConformanceClass()
                              + "']" ).withNamespaceContext( nsContext() ) );
        assertThat( xml, hasXPath( "//core:ConformsTo/atom:link[@href = '" + OPENAPI30.getConformanceClass()
                                   + "']" ).withNamespaceContext( nsContext() ) );
    }

    private Map<String, String> nsContext() {
        Map<String, String> nsContext = new HashMap<>();
        nsContext.put( "core", XML_CORE_NS_URL );
        nsContext.put( "atom", XML_ATOM_NS_URL );
        return nsContext;
    }

}
