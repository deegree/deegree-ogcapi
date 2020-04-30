package org.deegree.services.oaf;

import org.deegree.feature.stream.EmptyFeatureInputStream;
import org.deegree.services.oaf.domain.collections.Collection;
import org.deegree.services.oaf.domain.collections.Collections;
import org.deegree.services.oaf.domain.collections.Extent;
import org.deegree.services.oaf.domain.collections.Spatial;
import org.deegree.services.oaf.domain.collections.Temporal;
import org.deegree.services.oaf.feature.FeatureResponse;
import org.deegree.services.oaf.link.Link;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.deegree.services.oaf.OgcApiFeaturesConstants.DEFAULT_CRS;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class TestData {

    public static FeatureResponse features() {
        Link link = new Link( "http://self", "self", "application/json", "title" );
        EmptyFeatureInputStream features = new EmptyFeatureInputStream();
        return new FeatureResponse( features, 10, 100, 0, java.util.Collections.singletonList( link ), false, null );
    }

    public static FeatureResponse feature() {
        Link link = new Link( "http://self", "self", "application/json", "title" );
        EmptyFeatureInputStream features = new EmptyFeatureInputStream();
        return new FeatureResponse( features, 1, 1, 0, java.util.Collections.singletonList( link ), false, null );
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