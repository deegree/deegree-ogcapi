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
package org.deegree.services.oaf;

import org.deegree.cs.exceptions.UnknownCRSException;
import org.deegree.feature.Feature;
import org.deegree.feature.FeatureCollection;
import org.deegree.feature.persistence.FeatureStore;
import org.deegree.feature.stream.EmptyFeatureInputStream;
import org.deegree.feature.stream.FeatureInputStream;
import org.deegree.feature.stream.MemoryFeatureInputStream;
import org.deegree.feature.types.AppSchema;
import org.deegree.feature.types.FeatureType;
import org.deegree.gml.GMLInputFactory;
import org.deegree.gml.GMLStreamReader;
import org.deegree.gml.schema.GMLAppSchemaReader;
import org.deegree.services.oaf.domain.collections.Collection;
import org.deegree.services.oaf.domain.collections.Collections;
import org.deegree.services.oaf.domain.collections.Extent;
import org.deegree.services.oaf.domain.collections.Spatial;
import org.deegree.services.oaf.domain.collections.Temporal;
import org.deegree.services.oaf.exceptions.UnknownCollectionId;
import org.deegree.services.oaf.io.response.FeatureResponse;
import org.deegree.services.oaf.io.response.FeaturesResponse;
import org.deegree.services.oaf.io.response.FeaturesResponseBuilder;
import org.deegree.services.oaf.link.Link;
import org.deegree.services.oaf.link.LinkBuilder;
import org.deegree.services.oaf.workspace.DataAccess;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.deegree.services.oaf.workspace.configuration.DatasetMetadata;
import org.deegree.services.oaf.workspace.configuration.FeatureTypeMetadata;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;
import org.deegree.services.oaf.workspace.configuration.OafDatasets;
import org.joda.time.DateTime;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.deegree.gml.GMLVersion.GML_32;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.DEFAULT_CRS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class TestData {

    public static DataAccess mockDataAccess() {
        DataAccess testFactory = mock( DataAccess.class );
        Collection collection = createCollection();
        Collections testCollection = createCollections( collection );
        try {
            when( testFactory.createCollections( any( OafDatasetConfiguration.class ),
                                                 any( LinkBuilder.class ) ) ).thenReturn( testCollection );
            when( testFactory.createCollection( any( OafDatasetConfiguration.class ), eq( "test" ),
                                                any( LinkBuilder.class ) ) ).thenReturn( collection );
        } catch ( UnknownCollectionId e ) {
            e.printStackTrace();
        }
        return testFactory;
    }

    public static DeegreeWorkspaceInitializer mockWorkspaceInitializer() {
        QName featureTypeName = new QName( "http://www.deegree.org/app", "strassenbaumkataster" );
        return mockWorkspaceInitializer( featureTypeName );
    }

    public static DeegreeWorkspaceInitializer mockWorkspaceInitializer( QName featureTypeName ) {
        return mockWorkspaceInitializer( featureTypeName, null );
    }

    public static DeegreeWorkspaceInitializer mockWorkspaceInitializer( QName featureTypeName, Path pathToXsd ) {
        try {
            OafDatasetConfiguration oafConfiguration = mock( OafDatasetConfiguration.class );
            DatasetMetadata serviceMetadata = mock( DatasetMetadata.class );
            when( oafConfiguration.getServiceMetadata() ).thenReturn( serviceMetadata );

            Map<String, FeatureTypeMetadata> featureTypeMetadata = new HashMap<>();
            FeatureTypeMetadata ftm = new FeatureTypeMetadata( featureTypeName );

            FeatureType featureType = getFeatureType( featureTypeName, "io/schema/strassenbaumkataster.xsd" );
            if ( featureType == null )
                featureType = getFeatureType( featureTypeName, "io/schema/micado_kennzahlen_v1_2.xsd" );
            if ( featureType == null )
                featureType = getFeatureType( featureTypeName, "io/schema/kita.xsd" );
            if ( featureType == null )
                throw new IllegalArgumentException( "FeatureType with name " + featureTypeName + " is not known" );
            ftm.featureType( featureType );

            featureTypeMetadata.put( featureTypeName.getLocalPart(), ftm );
            when( oafConfiguration.getFeatureTypeMetadata() ).thenReturn( featureTypeMetadata );
            when( oafConfiguration.getFeatureTypeMetadata( eq( featureTypeName.getLocalPart() ) ) ).thenReturn( ftm );
            FeatureStore featureStore = mock( FeatureStore.class );
            when( featureStore.getSchema() ).thenReturn( featureType.getSchema() );
            when( oafConfiguration.getFeatureStore( eq( featureTypeName ),
                                                    eq( featureTypeName.getLocalPart() ) ) ).thenReturn( featureStore );

            OafDatasets oafDatasets = new OafDatasets();
            oafDatasets.addDataset( "oaf", oafConfiguration );
            DeegreeWorkspaceInitializer deegreeWorkspaceInitializer = mock( DeegreeWorkspaceInitializer.class );
            when( deegreeWorkspaceInitializer.getAppschemaFile( anyString() ) ).thenReturn( pathToXsd );
            when( deegreeWorkspaceInitializer.getOafDatasets() ).thenReturn( oafDatasets );
            return deegreeWorkspaceInitializer;
        } catch ( Exception e ) {
            e.printStackTrace();
            return null;
        }
    }

    private static FeatureType getFeatureType( QName featureTypeName, String applicationschema )
                    throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        String schemaURL = TestData.class.getResource( applicationschema ).toString();
        GMLAppSchemaReader xsdAdapter = new GMLAppSchemaReader( GML_32, null, schemaURL );
        AppSchema schema = xsdAdapter.extractAppSchema();
        return schema.getFeatureType( featureTypeName );
    }

    public static FeaturesResponse features() {
        Link link = new Link( "http://self", "self", "application/json", "title" );
        EmptyFeatureInputStream features = new EmptyFeatureInputStream();
        Map<String, String> featureTypeNsPrefixes = java.util.Collections.emptyMap();
        return new FeaturesResponseBuilder( features ).withFeatureTypeNsPrefixes(
                        featureTypeNsPrefixes ).withNumberOfFeatures( 10 ).withNumberOfFeaturesMatched(
                        100 ).withStartIndex( 0 ).withLinks(
                        java.util.Collections.singletonList( link ) ).withMaxFeaturesAndStartIndexApplicable(
                        false ).withResponseCrsName( DEFAULT_CRS ).buildFeaturesResponse();
    }

    public static FeatureResponse feature()
                    throws XMLStreamException, IOException, UnknownCRSException {
        Link link = new Link( "http://self", "self", "application/json", "title" );
        Feature feature = readFeature();
        Map<String, String> featureTypeNsPrefixes = java.util.Collections.emptyMap();
        return new FeaturesResponseBuilder( feature ).withFeatureTypeNsPrefixes(
                        featureTypeNsPrefixes ).withLinks(
                        java.util.Collections.singletonList( link ) ).withResponseCrsName(
                        DEFAULT_CRS ).buildFeatureResponse();
    }

    public static Collections createCollections() {
        Link link = new Link( "http://link.de/collections", "self", "application/json", "collectionsTitle" );
        Collection collection = createCollection();
        return new Collections( java.util.Collections.singletonList( link ),
                                java.util.Collections.singletonList( collection ) );
    }

    public static Collections createCollections( Collection collection ) {
        Link link = new Link( "http://link.de/collections", "self", "application/json", "collectionsTitle" );
        return new Collections( java.util.Collections.singletonList( link ),
                                java.util.Collections.singletonList( collection ) );
    }

    public static Collection createCollection() {
        Link collectionLink = new Link( "http://link.de/testcollection", "self", "application/json",
                                        "collectionTitle" );
        Extent extent = new Extent();
        extent.setSpatial( createSpatial() );
        extent.setTemporal( createTemporal() );
        List<Link> links = java.util.Collections.singletonList( collectionLink );
        List<String> crs = java.util.Collections.singletonList( "EPSG:4326" );
        return new Collection( "testId", "testTitle", "testDesc", links, extent, crs, DEFAULT_CRS );
    }

    private static Spatial createSpatial() {
        List<Double> bbox = new ArrayList<>();
        bbox.add( 10.3 );
        bbox.add( 48.4 );
        bbox.add( 10.5 );
        bbox.add( 48.6 );
        return new Spatial( bbox, DEFAULT_CRS );
    }

    private static Temporal createTemporal() {
        List<Date> interval = new ArrayList<>();
        DateTime end = new DateTime( 2020, 4, 22, 12, 0, 0, 0 );
        DateTime begin = new DateTime( 2020, 4, 15, 12, 0, 0, 0 );
        interval.add( end.toDate() );
        interval.add( begin.toDate() );
        return new Temporal( interval, null );
    }

    public static Feature readFeature()
                    throws XMLStreamException, IOException, UnknownCRSException {
        GMLStreamReader gmlReader = GMLInputFactory.createGMLStreamReader( GML_32,
                                                                           TestData.class.getResource(
                                                                                           "io/strassenbaumkataster-oneFeature.gml" ) );
        FeatureCollection featureCollection = gmlReader.readFeatureCollection();
        FeatureInputStream featureStream = new MemoryFeatureInputStream( featureCollection );
        Feature feature = featureStream.iterator().next();
        return feature;
    }
}
