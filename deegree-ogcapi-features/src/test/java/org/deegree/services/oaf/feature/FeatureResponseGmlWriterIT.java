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
package org.deegree.services.oaf.feature;

import org.deegree.commons.utils.CloseableIterator;
import org.deegree.feature.Feature;
import org.deegree.feature.FeatureCollection;
import org.deegree.feature.stream.EmptyFeatureInputStream;
import org.deegree.feature.stream.FeatureInputStream;
import org.deegree.feature.stream.IteratorFeatureInputStream;
import org.deegree.gml.GMLInputFactory;
import org.deegree.gml.GMLStreamReader;
import org.deegree.services.oaf.link.Link;
import org.junit.Test;
import org.xmlmatchers.namespace.SimpleNamespaceContext;
import org.xmlmatchers.xpath.XpathReturnType;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.deegree.gml.GMLVersion.GML_32;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_SF_NS_URL;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.XML_SF_SCHEMA_URL;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.xmlmatchers.XmlMatchers.conformsTo;
import static org.xmlmatchers.XmlMatchers.hasXPath;
import static org.xmlmatchers.transform.XmlConverters.the;
import static org.xmlmatchers.validation.SchemaFactory.w3cXmlSchemaFromUrl;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class FeatureResponseGmlWriterIT {

    private static final String NAMESPACE_URI = "http://deegree.org/app";
    private static final String SCHEMA_LOCATION = "http://schemalocation/datasets/dataset/collections/collection/appschema";

    @Test
    public void testWriteTo()
                            throws Exception {
        FeatureResponseGmlWriter featureResponeWriter = new FeatureResponseGmlWriter();
        FeatureResponse featureResponse = createFeatureResponse();
        OutputStream bos = new ByteArrayOutputStream();
        featureResponeWriter.writeTo( featureResponse, null, null, null, null, null, bos );
        assertThat( the( bos.toString() ),
                    hasXPath( "count(/sf:FeatureCollection/sf:featureMember/app:strassenbaumkataster)", nsContext(),
                              XpathReturnType.returningANumber(), is( 5.0 ) ) );
        assertThat( the( bos.toString() ),
                    hasXPath( "/sf:FeatureCollection/sf:featureMember/app:strassenbaumkataster[1]/app:geom/gml:Point/@srsName", nsContext(),
                              XpathReturnType.returningAString(), is( "urn:ogc:def:crs:EPSG::4258" ) ) );
        assertThat( the( bos.toString() ),
                    hasXPath( "/sf:FeatureCollection/@xsi:schemaLocation", nsContext(),
                              XpathReturnType.returningAString(),
                              is( String.format( "%s %s", NAMESPACE_URI, SCHEMA_LOCATION ) ) ) );

        // TODO: fails with [cvc-complex-type.2.4.a: Invalid content was found starting with element '{"http://www.deegree.org/app":strassenbaumkataster}'. One of '{"http://www.opengis.net/gml/3.2":AbstractFeature}' is expected. (line: -1 , column: -1)
        // Schema schema = w3cXmlSchemaFromUrl( XML_SF_SCHEMA_URL );
        //assertThat( the( bos.toString() ), conformsTo( schema ) );
    }

    @Test
    public void testWriteTo_EPSG25832()
                            throws Exception {
        String requestCrs = "EPSG:25832";
        FeatureResponseGmlWriter featureResponeWriter = new FeatureResponseGmlWriter();
        FeatureResponse featureResponse = createFeatureResponse( requestCrs );
        OutputStream bos = new ByteArrayOutputStream();
        featureResponeWriter.writeTo( featureResponse, null, null, null, null, null, bos );
        assertThat( the( bos.toString() ),
                    hasXPath( "count(/sf:FeatureCollection/sf:featureMember/app:strassenbaumkataster)", nsContext(),
                              XpathReturnType.returningANumber(), is( 5.0 ) ) );
        assertThat( the( bos.toString() ),
                    hasXPath( "/sf:FeatureCollection/sf:featureMember/app:strassenbaumkataster[1]/app:geom/gml:Point/@srsName",
                              nsContext(), XpathReturnType.returningAString(), is( requestCrs ) ) );

        // TODO: fails with [cvc-complex-type.2.4.a: Invalid content was found starting with element '{"http://www.deegree.org/app":strassenbaumkataster}'. One of '{"http://www.opengis.net/gml/3.2":AbstractFeature}' is expected. (line: -1 , column: -1)
        // Schema schema = w3cXmlSchemaFromUrl( XML_SF_SCHEMA_URL );
        //assertThat( the( bos.toString() ), conformsTo( schema ) );
    }

    @Test
    public void testWriteTo_EmptyFeatureResponse()
                    throws Exception {
        FeatureResponseGmlWriter featureResponeWriter = new FeatureResponseGmlWriter();
        FeatureResponse featureResponse = createEmptyFeatureResponse();
        OutputStream bos = new ByteArrayOutputStream();
        featureResponeWriter.writeTo( featureResponse, null, null, null, null, null, bos );

        Schema schema = w3cXmlSchemaFromUrl( XML_SF_SCHEMA_URL );
        assertThat( the( bos.toString() ), conformsTo( schema ) );
    }

    private NamespaceContext nsContext() {
        SimpleNamespaceContext nsContext = new SimpleNamespaceContext()
                                .withBinding( "sf", XML_SF_NS_URL )
                                .withBinding("app", "http://www.deegree.org/app" )
                                .withBinding("gml", "http://www.opengis.net/gml/3.2" )
                                .withBinding("xsi", "http://www.w3.org/2001/XMLSchema-instance" );
        return nsContext;
    }

    private FeatureResponse createFeatureResponse()
                            throws Exception {
        return createFeatureResponse( null );
    }

    private FeatureResponse createFeatureResponse( String crs )
                            throws Exception {
        List<Link> links = java.util.Collections.singletonList(
                                new Link( "http://self", "self", "application/json", "title" ) );
        GMLStreamReader gmlReader = GMLInputFactory.createGMLStreamReader( GML_32,
                                                                           getClass().getResource( "strassenbaumkataster.gml" ) );
        FeatureCollection featureCollection = gmlReader.readFeatureCollection();

        FeatureInputStream featureStream = new IteratorFeatureInputStream(
                                new ListCloseableIterator( featureCollection ) );
        Map<String, String> featureTypeNsPrefixes = new HashMap<>();
        QName name = featureCollection.getName();
        featureTypeNsPrefixes.put( name.getPrefix(), name.getNamespaceURI() );
        return new FeatureResponseBuilder( featureStream ).withFeatureTypeNsPrefixes(
                        featureTypeNsPrefixes ).withNumberOfFeatures(
                        featureCollection.size() ).withNumberOfFeaturesMatched(
                        featureCollection.size() ).withStartIndex( 0 ).withLinks(
                        links ).withMaxFeaturesAndStartIndexApplicable(
                        false ).withResponseCrsName( crs ).withSchemaLocation( NAMESPACE_URI, SCHEMA_LOCATION ).build();
    }

    private FeatureResponse createEmptyFeatureResponse() {
        List<Link> links = java.util.Collections.singletonList(
                        new Link( "http://self", "self", "application/json", "title" ) );
        FeatureInputStream featureStream = new EmptyFeatureInputStream();
        Map<String, String> featureTypeNsPrefixes = Collections.emptyMap();
        return new FeatureResponseBuilder( featureStream ).withFeatureTypeNsPrefixes(
                        featureTypeNsPrefixes ).withNumberOfFeatures( 10 ).withNumberOfFeaturesMatched(
                        100 ).withStartIndex( 0 ).withLinks(
                        links ).withMaxFeaturesAndStartIndexApplicable(
                        false ).withSchemaLocation( NAMESPACE_URI, SCHEMA_LOCATION ).build();
    }

    private class ListCloseableIterator implements CloseableIterator<Feature> {

        private final Iterator<Feature> iterator;

        private ListCloseableIterator( FeatureCollection features ) {
            this.iterator = features.iterator();
        }

        @Override
        public void close() {
        }

        @Override
        public List<Feature> getAsListAndClose() {
            return null;
        }

        @Override
        public Collection<Feature> getAsCollectionAndClose( Collection<Feature> collection ) {
            return null;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Feature next() {
            return iterator.next();
        }
    }

}
