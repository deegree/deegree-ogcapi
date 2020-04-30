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
import javax.xml.validation.Schema;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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

        // TODO: fails with [cvc-complex-type.2.4.a: Invalid content was found starting with element '{"http://www.deegree.org/app":strassenbaumkataster}'. One of '{"http://www.opengis.net/gml/3.2":AbstractFeature}' is expected. (line: -1 , column: -1)
        // Schema schema = w3cXmlSchemaFromUrl( XML_SF_SCHEMA_URL );
        //assertThat( the( bos.toString() ), conformsTo( schema ) );
    }

    private NamespaceContext nsContext() {
        SimpleNamespaceContext nsContext = new SimpleNamespaceContext().withBinding( "sf", XML_SF_NS_URL ).withBinding(
                        "app", "http://www.deegree.org/app" );
        return nsContext;
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

    private FeatureResponse createFeatureResponse()
                    throws Exception {
        List<Link> links = java.util.Collections.singletonList(
                        new Link( "http://self", "self", "application/json", "title" ) );
        GMLStreamReader gmlReader = GMLInputFactory.createGMLStreamReader( GML_32, getClass().getResource(
                        "strassenbaumkataster.gml" ) );
        FeatureCollection featureCollection = gmlReader.readFeatureCollection();

        FeatureInputStream featureStream = new IteratorFeatureInputStream(
                        new ListCloseableIterator( featureCollection ) );
        return new FeatureResponse( featureStream, featureCollection.size(), featureCollection.size(), 0, links, false,
                                    null );
    }

    private FeatureResponse createEmptyFeatureResponse() {
        List<Link> links = java.util.Collections.singletonList(
                        new Link( "http://self", "self", "application/json", "title" ) );
        FeatureInputStream featureStream = new EmptyFeatureInputStream();
        return new FeatureResponse( featureStream, 10, 100, 0, links, false, null );
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