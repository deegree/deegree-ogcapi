package org.deegree.services.oaf.link;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
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
    public void test_createFeaturesLinks()
                            throws URISyntaxException {
        String uri = "http://localhost:8081/deegree-services-oaf/datasets/oaf/collections/strassenbaumkataster/items";
        String path = "datasets/oaf/collections/strassenbaumkataster/items";

        LinkBuilder linkBuilder = new LinkBuilder( uriInfo( uri, path ) );
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

        LinkBuilder linkBuilder = new LinkBuilder( uriInfo( uri, path ) );
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

    @Test
    public void test_createSchemaLink()
                    throws URISyntaxException {
        String uri = "http://localhost:8081/deegree-services-oaf/datasets/oaf/collections/strassenbaumkataster/appschema";
        String path = "datasets/oaf/collections/strassenbaumkataster/appschema";

        LinkBuilder linkBuilder = new LinkBuilder( uriInfo( uri, path ) );
        String schemaLink = linkBuilder.createSchemaLink( "oaf", "otherFeatureType" );

        assertThat( schemaLink, is("http://localhost:8081/deegree-services-oaf/datasets/oaf/collections/otherFeatureType/appschema") );
    }


        private UriInfo uriInfo( String uri, String path )
                            throws URISyntaxException {
        UriInfo uriInfo = mock( UriInfo.class );
        when( uriInfo.getPath() ).thenReturn( path );
        UriBuilder baseUriBuilder = UriBuilder.fromUri( BASE_URI );
        when( uriInfo.getBaseUriBuilder() ).thenReturn( baseUriBuilder );
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