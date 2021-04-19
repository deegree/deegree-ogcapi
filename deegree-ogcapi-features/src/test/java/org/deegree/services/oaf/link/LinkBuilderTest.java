package org.deegree.services.oaf.link;

import org.deegree.services.oaf.TestData;
import org.deegree.services.oaf.exceptions.UnknownDatasetId;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.deegree.services.oaf.workspace.configuration.DatasetMetadata;
import org.glassfish.jersey.internal.util.collection.StringKeyIgnoreCaseMultivaluedMap;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GEOJSON;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_32;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_SF0;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_SF2;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class LinkBuilderTest {

    private String BASE_URI = "http://localhost:8081/deegree-services-oaf";

    @Test
    public void test_createCollectionsLinks()
                            throws URISyntaxException, UnknownDatasetId {
        String uri = "http://localhost:8081/deegree-services-oaf/datasets/oaf/collections";
        String path = "datasets/oaf/collections";

        LinkBuilder linkBuilder = new LinkBuilder( uriInfo( uri, path, "strassenbaumkataster" ) );
        DeegreeWorkspaceInitializer deegreeWorkspaceInitializer = TestData.mockWorkspaceInitializer();
        DatasetMetadata datasetMetadata = deegreeWorkspaceInitializer.getOafDatasets().getDataset( "oaf" ).getServiceMetadata();
        List<Link> collectionsLinks = linkBuilder.createCollectionsLinks("oaf", datasetMetadata );

        assertThat( collectionsLinks.size(), is( 3 ) );
        assertThat( collectionsLinks, hasLinkWith( "self", APPLICATION_JSON, uri ) );
        assertThat( collectionsLinks, hasLinkWith( "alternate", APPLICATION_XML, uri ) );
        assertThat( collectionsLinks, hasLinkWith( "alternate", TEXT_HTML, uri ) );
    }

    @Test
    public void test_createCollectionInCollectionsLinks()
                            throws URISyntaxException {
        String uri = "http://localhost:8081/deegree-services-oaf/datasets/oaf/collections";
        String path = "datasets/oaf/collections";

        LinkBuilder linkBuilder = new LinkBuilder( uriInfo( uri, path ) );
        List<Link> collectionLinks = linkBuilder.createCollectionLinks( "oaf", "strassenbaumkataster",
                                                                        Collections.emptyList());


        assertThat( collectionLinks.size(), is( 11 ) );
        String collectionUri = uri + "/strassenbaumkataster";
        assertThat( collectionLinks, hasLinkWith( "collection", APPLICATION_JSON, collectionUri ) );
        assertThat( collectionLinks, hasLinkWith( "collection", APPLICATION_XML, collectionUri ) );
        assertThat( collectionLinks, hasLinkWith( "collection", TEXT_HTML, collectionUri ) );

        String itemsUri = uri + "/strassenbaumkataster/items";
        assertThat( collectionLinks, hasLinkWith( "items", APPLICATION_GEOJSON, itemsUri ) );
        assertThat( collectionLinks, hasLinkWith( "items", APPLICATION_GML, itemsUri ) );
        assertThat( collectionLinks, hasLinkWith( "items", APPLICATION_GML_32, itemsUri ) );
        assertThat( collectionLinks, hasLinkWith( "items", APPLICATION_GML_SF0, itemsUri ) );
        assertThat( collectionLinks, hasLinkWith( "items", APPLICATION_GML_SF2, itemsUri ) );
        assertThat( collectionLinks, hasLinkWith( "items", TEXT_HTML, itemsUri ) );

        String enclosureUri = uri + "/strassenbaumkataster/items?bulk=true";
        assertThat( collectionLinks, hasLinkWith( "enclosure", APPLICATION_JSON, enclosureUri ) );
        assertThat( collectionLinks, hasLinkWith( "enclosure", APPLICATION_XML, enclosureUri ) );
    }

    @Test
    public void test_createCollectionLinks()
                            throws URISyntaxException {
        String uri = "http://localhost:8081/deegree-services-oaf/datasets/oaf/collections/strassenbaumkataster";
        String path = "datasets/oaf/collections/strassenbaumkataster";

        LinkBuilder linkBuilder = new LinkBuilder( uriInfo( uri, path ,"strassenbaumkataster" ) );
        List<Link> collectionLinks = linkBuilder.createCollectionLinks( "oaf", "strassenbaumkataster",
                                                                         Collections.emptyList());

        assertThat( collectionLinks.size(), is( 11 ) );
        assertThat( collectionLinks, hasLinkWith( "self", APPLICATION_JSON, uri ) );
        assertThat( collectionLinks, hasLinkWith( "alternate", APPLICATION_XML, uri ) );
        assertThat( collectionLinks, hasLinkWith( "alternate", TEXT_HTML, uri ) );

        String itemsUri = uri + "/items";
        assertThat( collectionLinks, hasLinkWith( "items", APPLICATION_GEOJSON, itemsUri ) );
        assertThat( collectionLinks, hasLinkWith( "items", APPLICATION_GML, itemsUri ) );
        assertThat( collectionLinks, hasLinkWith( "items", APPLICATION_GML_32, itemsUri ) );
        assertThat( collectionLinks, hasLinkWith( "items", APPLICATION_GML_SF0, itemsUri ) );
        assertThat( collectionLinks, hasLinkWith( "items", APPLICATION_GML_SF2, itemsUri ) );
        assertThat( collectionLinks, hasLinkWith( "items", TEXT_HTML, itemsUri ) );

        String enclosureUri = uri + "/items?bulk=true";
        assertThat( collectionLinks, hasLinkWith( "enclosure", APPLICATION_JSON, enclosureUri ) );
        assertThat( collectionLinks, hasLinkWith( "enclosure", APPLICATION_XML, enclosureUri ) );
    }

    @Test
    public void test_createFeaturesLinks()
                            throws URISyntaxException {
        String uri = "http://localhost:8081/deegree-services-oaf/datasets/oaf/collections/strassenbaumkataster/items";
        String path = "datasets/oaf/collections/strassenbaumkataster/items";

        LinkBuilder linkBuilder = new LinkBuilder( uriInfo( uri, path, "strassenbaumkataster" ) );
        NextLink nextLink = new NextLink( 1000, 10, 0 );
        List<Link> featuresLinks = linkBuilder.createFeaturesLinks( "oaf", "strassenbaumkataster", nextLink );

        assertThat( featuresLinks.size(), is( 10 ) );
        assertThat( featuresLinks, hasLinkWith( "self", APPLICATION_GEOJSON, uri ) );
        assertThat( featuresLinks, hasLinkWith( "alternate", APPLICATION_GML, uri ) );
        assertThat( featuresLinks, hasLinkWith( "alternate", APPLICATION_GML_32, uri ) );
        assertThat( featuresLinks, hasLinkWith( "alternate", APPLICATION_GML_SF0, uri ) );
        assertThat( featuresLinks, hasLinkWith( "alternate", APPLICATION_GML_SF2, uri ) );
        assertThat( featuresLinks, hasLinkWith( "alternate", TEXT_HTML, uri ) );
        assertThat( featuresLinks, hasLinkWith( "next", APPLICATION_GEOJSON, uri + "?offset=10&limit=10" ) );

        String collectionUri = uri.substring( 0, uri.lastIndexOf( "/" ) );
        assertThat( featuresLinks, hasLinkWith( "collection", APPLICATION_JSON, collectionUri ) );
        assertThat( featuresLinks, hasLinkWith( "collection", APPLICATION_XML, collectionUri ) );
        assertThat( featuresLinks, hasLinkWith( "collection", TEXT_HTML, collectionUri ) );
    }

    @Test
    public void test_createFeaturesLinksWithParams()
                            throws URISyntaxException {
        String uri = "http://localhost:8081/deegree-services-oaf/datasets/oaf/collections/strassenbaumkataster/items?offset=10&limit=10";
        String path = "datasets/oaf/collections/strassenbaumkataster/items";

        LinkBuilder linkBuilder = new LinkBuilder( uriInfo( uri, path, "strassenbaumkataster" ) );
        NextLink nextLink = new NextLink( 1000, 10, 10 );
        List<Link> featuresLinks = linkBuilder.createFeaturesLinks( "oaf", "strassenbaumkataster", nextLink );

        assertThat( featuresLinks.size(), is( 10 ) );
        assertThat( featuresLinks, hasLinkWith( "self", APPLICATION_GEOJSON, uri ) );
        assertThat( featuresLinks, hasLinkWith( "alternate", APPLICATION_GML, uri ) );
        assertThat( featuresLinks, hasLinkWith( "alternate", APPLICATION_GML_32, uri ) );
        assertThat( featuresLinks, hasLinkWith( "alternate", APPLICATION_GML_SF0, uri ) );
        assertThat( featuresLinks, hasLinkWith( "alternate", APPLICATION_GML_SF2, uri ) );
        assertThat( featuresLinks, hasLinkWith( "alternate", TEXT_HTML, uri ) );
        String nextUri = "http://localhost:8081/deegree-services-oaf/datasets/oaf/collections/strassenbaumkataster/items?offset=20&limit=10";
        assertThat( featuresLinks, hasLinkWith( "next", APPLICATION_GEOJSON, nextUri ) );

        String collectionUri = uri.substring( 0, uri.lastIndexOf( "/" ) );
        assertThat( featuresLinks, hasLinkWith( "collection", APPLICATION_JSON, collectionUri ) );
        assertThat( featuresLinks, hasLinkWith( "collection", APPLICATION_XML, collectionUri ) );
        assertThat( featuresLinks, hasLinkWith( "collection", TEXT_HTML, collectionUri ) );
    }

    private UriInfo uriInfo( String uri, String path )
                            throws URISyntaxException {
        return this.uriInfo( uri, path, null );
    }

    private UriInfo uriInfo( String uri, String path, String collectionId )
                            throws URISyntaxException {
        UriInfo uriInfo = mock( UriInfo.class );
        when( uriInfo.getPath() ).thenReturn( path );
        MultivaluedMap<String, String> pathParameters = new StringKeyIgnoreCaseMultivaluedMap<>();
        if ( collectionId != null )
            pathParameters.add( "collectionId", collectionId );
        when( uriInfo.getPathParameters() ).thenReturn( pathParameters );
        // One instance for each call (JavaDOc: Create a new instance initialized from an existing URI.)
        when( uriInfo.getBaseUriBuilder() ).thenReturn( UriBuilder.fromUri( BASE_URI ),
                                                        UriBuilder.fromUri( BASE_URI ),
                                                        UriBuilder.fromUri( BASE_URI ),
                                                        UriBuilder.fromUri( BASE_URI ) );
        UriBuilder requestUriBuilder = UriBuilder.fromUri( new URI( uri ) );
        when( uriInfo.getRequestUriBuilder() ).thenReturn( requestUriBuilder );
        return uriInfo;
    }

    private BaseMatcher<List<Link>> hasLinkWith( String rel, String type, String href ) {
        return new BaseMatcher() {
            @Override
            public boolean matches( Object o ) {
                List<Link> links = (List<Link>) o;
                long numberOfMatchingLinks = links.stream().filter(
                                        link -> rel.equals( link.getRel() ) && type.equals( link.getType() )
                                                && href.equals( link.getHref() ) ).count();
                return numberOfMatchingLinks > 0;
            }

            @Override
            public void describeTo( Description description ) {
                description.appendText( "Link with: rel=" ).appendValue( rel ).appendText( " type=" ).appendValue(
                                        type ).appendText( " href=" ).appendValue( href );
            }
        };
    }

}