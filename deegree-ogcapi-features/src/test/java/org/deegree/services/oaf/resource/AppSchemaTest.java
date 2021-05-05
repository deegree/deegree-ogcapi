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

import org.deegree.services.oaf.schema.SchemaResponseGmlWriter;
import org.deegree.services.oaf.workspace.DataAccess;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static org.deegree.services.oaf.TestData.mockDataAccess;
import static org.deegree.services.oaf.TestData.mockWorkspaceInitializer;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.HasXPathMatcher.hasXPath;

public class AppSchemaTest extends JerseyTest {

    @Override
    protected Application configure() {
        enable( TestProperties.LOG_TRAFFIC );
        ResourceConfig resourceConfig = new ResourceConfig( Appschema.class, SchemaResponseGmlWriter.class );
        resourceConfig.register( new AbstractBinder() {
            @Override
            protected void configure() {
                bind( mockDataAccess() ).to( DataAccess.class );
                bind( mockWorkspaceInitializer( new QName( "http://www.deegree.org/app", "KitaEinrichtungen" ) ) ).to(
                                DeegreeWorkspaceInitializer.class );
            }
        } );
        return resourceConfig;
    }

    @Test
    public void test_AppSchemaDeclaration_Xml_ShouldBeAvailable() {
        final String xml = target( "/datasets/oaf/collections/KitaEinrichtungen/appschema" ).request(
                        APPLICATION_XML ).get(
                        String.class );

        assertThat( xml,
                    hasXPath( "//xs:schema/xs:import[@namespace = 'http://www.opengis.net/gml/3.2' and @schemaLocation='http://schemas.opengis.net/gml/3.2.1/gml.xsd']" ).withNamespaceContext(
                                    nsContext() ) );
    }

    private Map<String, String> nsContext() {
        Map<String, String> nsContext = new HashMap<>();
        nsContext.put( "xs", "http://www.w3.org/2001/XMLSchema" );
        return nsContext;
    }

}
