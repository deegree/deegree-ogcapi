package org.deegree.services.oaf.link;

import org.junit.Test;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class NextLinkTest {

    @Test
    public void testCreateLink_firstPage() {
        NextLink nextLink = new NextLink( 10, 2, 0 );

        UriInfo uriInfo = createUriInfo( "http://localhost:8080/oafcollections/buildings/items?limit=2" );
        String uri = nextLink.createUri( uriInfo );

        assertThat( uri, is( "http://localhost:8080/oafcollections/buildings/items?offset=2&limit=2" ) );
    }

    @Test
    public void testCreateLink_secondPage() {
        NextLink nextLink = new NextLink( 10, 2, 2 );

        UriInfo uriInfo = createUriInfo( "http://localhost:8080/oafcollections/buildings/items?limit=2&offset=2" );
        String uri = nextLink.createUri( uriInfo );

        assertThat( uri, is( "http://localhost:8080/oafcollections/buildings/items?offset=4&limit=2" ) );
    }

    @Test
    public void testCreateLink_lastPage() {
        NextLink nextLink = new NextLink( 10, 2, 8 );

        UriInfo uriInfo = createUriInfo( "http://localhost:8080/oafcollections/buildings/items?limit=2&offset=8" );
        String uri = nextLink.createUri( uriInfo );

        assertThat( uri, is( nullValue() ) );
    }

    private UriInfo createUriInfo( String fromUri ) {
        UriInfo uriInfo = mock( UriInfo.class );
        UriBuilder uriBuilder = UriBuilder.fromUri( fromUri );
        when( uriInfo.getRequestUriBuilder() ).thenReturn( uriBuilder );
        return uriInfo;
    }
}
