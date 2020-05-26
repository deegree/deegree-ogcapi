package org.deegree.services.oaf;

import org.deegree.feature.stream.EmptyFeatureInputStream;
import org.deegree.feature.types.AppSchema;
import org.deegree.feature.types.FeatureType;
import org.deegree.gml.schema.GMLAppSchemaReader;
import org.deegree.services.oaf.domain.collections.Collection;
import org.deegree.services.oaf.domain.collections.Collections;
import org.deegree.services.oaf.domain.collections.Extent;
import org.deegree.services.oaf.domain.collections.Spatial;
import org.deegree.services.oaf.domain.collections.Temporal;
import org.deegree.services.oaf.exceptions.UnknownCollectionId;
import org.deegree.services.oaf.feature.FeatureResponse;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.deegree.gml.GMLVersion.GML_32;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.DEFAULT_CRS;
import static org.mockito.ArgumentMatchers.any;
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
                                                any( LinkBuilder.class ) ) ).thenReturn(
                            collection );
        } catch ( UnknownCollectionId e ) {
            e.printStackTrace();
        }
        return testFactory;
    }

    public static DeegreeWorkspaceInitializer mockWorkspaceInitializer() {
        QName featureTypeName = new QName( "http://www.deegree.org/app", "strassenbaumkataster" );
        return mockWorkspaceInitializer( featureTypeName );
    }

    public static DeegreeWorkspaceInitializer mockWorkspaceInitializer( String collectionId ) {
        QName featureTypeName = new QName( collectionId );
        return mockWorkspaceInitializer( featureTypeName );
    }

    private static DeegreeWorkspaceInitializer mockWorkspaceInitializer( QName featureTypeName ) {
        OafDatasetConfiguration oafConfiguration = mock( OafDatasetConfiguration.class );
        DatasetMetadata serviceMetadata = mock( DatasetMetadata.class );
        when( oafConfiguration.getServiceMetadata() ).thenReturn( serviceMetadata );

        Map<String, FeatureTypeMetadata> featureTypeMetadata = new HashMap<>();
        FeatureTypeMetadata ftm = new FeatureTypeMetadata( featureTypeName );

        try {
            String schemaURL = TestData.class.getResource( "feature/schema/strassenbaumkataster.xsd" ).toString();
            GMLAppSchemaReader xsdAdapter = new GMLAppSchemaReader( GML_32, null, schemaURL );
            AppSchema schema = xsdAdapter.extractAppSchema();
            FeatureType featureType = schema.getFeatureType( featureTypeName );
            if ( featureType != null )
                ftm.featureType( featureType );
        } catch ( Exception e ) {
        }

        featureTypeMetadata.put( featureTypeName.getLocalPart(), ftm );
        when( oafConfiguration.getFeatureTypeMetadata() ).thenReturn( featureTypeMetadata );

        OafDatasets oafDatasets = new OafDatasets();
        oafDatasets.addDataset( "oaf", oafConfiguration );
        DeegreeWorkspaceInitializer deegreeWorkspaceInitializer = mock( DeegreeWorkspaceInitializer.class );
        when( deegreeWorkspaceInitializer.getOafDatasets() ).thenReturn( oafDatasets );
        return deegreeWorkspaceInitializer;
    }

    public static FeatureResponse features() {
        Link link = new Link( "http://self", "self", "application/json", "title" );
        EmptyFeatureInputStream features = new EmptyFeatureInputStream();
        Map<String, String> featureTypeNsPrefixes = java.util.Collections.emptyMap();
        return new FeatureResponse( features, featureTypeNsPrefixes, 10, 100, 0,
                                    java.util.Collections.singletonList( link ), false, null );
    }

    public static FeatureResponse feature() {
        Link link = new Link( "http://self", "self", "application/json", "title" );
        EmptyFeatureInputStream features = new EmptyFeatureInputStream();
        Map<String, String> featureTypeNsPrefixes = java.util.Collections.emptyMap();
        return new FeatureResponse( features, featureTypeNsPrefixes, 1, 1, 0,
                                    java.util.Collections.singletonList( link ), false, null );
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
        return new Collection( "testId", "testTitle", "testDesc", links, extent, crs );
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

}