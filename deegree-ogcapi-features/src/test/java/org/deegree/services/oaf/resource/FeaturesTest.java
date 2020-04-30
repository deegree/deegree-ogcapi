package org.deegree.services.oaf.resource;

import org.deegree.feature.stream.EmptyFeatureInputStream;
import org.deegree.services.oaf.domain.collections.Collection;
import org.deegree.services.oaf.domain.collections.Collections;
import org.deegree.services.oaf.domain.collections.Extent;
import org.deegree.services.oaf.feature.FeatureResponse;
import org.deegree.services.oaf.feature.FeatureResponseGmlWriter;
import org.deegree.services.oaf.feature.FeaturesRequest;
import org.deegree.services.oaf.link.Link;
import org.deegree.services.oaf.link.LinkBuilder;
import org.deegree.services.oaf.workspace.DataAccess;
import org.deegree.services.oaf.workspace.DataAccessFactory;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;
import org.deegree.services.oaf.workspace.configuration.OafDatasets;
import org.deegree.services.oaf.workspace.configuration.ServiceMetadata;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.deegree.services.oaf.OgcApiFeaturesConstants.HEADER_Link;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.HEADER_NUMBER_MATCHED;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.HEADER_NUMBER_RETURNED;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.HEADER_TIMESTAMP;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GEOJSON;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ DataAccessFactory.class, DeegreeWorkspaceInitializer.class })
public class FeaturesTest extends JerseyTest {

    @Override
    protected Application configure() {
        enable( TestProperties.LOG_TRAFFIC );
        return new ResourceConfig( Features.class, FeatureResponseGmlWriter.class );
    }

    @Test
    public void test_FeaturesDeclarationJsonShouldBeAvailable()
                    throws Exception {
        mock();
        int statusCode = target( "/datasets/oaf/collections/test/items" ).request(
                        APPLICATION_GEOJSON ).get().getStatus();
        assertThat( statusCode, is( 200 ) );
    }

    @Test
    public void test_FeaturesDeclarationGmlShouldBeAvailable()
                    throws Exception {
        mock();
        Response response = target( "/datasets/oaf/collections/test/items" ).request( APPLICATION_GML ).get();
        assertThat( response.getStatus(), is( 200 ) );
        MultivaluedMap<String, Object> headers = response.getHeaders();
        assertThat( headers.get( HEADER_TIMESTAMP ).get( 0 ), is( notNullValue() ) );
        assertThat( headers.get( HEADER_NUMBER_RETURNED ).get( 0 ), is( "10" ) );
        assertThat( headers.get( HEADER_NUMBER_MATCHED ).get( 0 ), is( "100" ) );
        assertThat( headers.get( HEADER_Link ).size(), is( 1 ) );
    }

    private void mock()
                    throws Exception {
        mockDataAccess();
        mockWorkspace();
    }

    private void mockWorkspace() {
        PowerMockito.mockStatic( DeegreeWorkspaceInitializer.class );
        OafDatasetConfiguration oafConfiguration = Mockito.mock( OafDatasetConfiguration.class );
        ServiceMetadata serviceMetadata = Mockito.mock( ServiceMetadata.class );
        when( oafConfiguration.getServiceMetadata() ).thenReturn( serviceMetadata );
        OafDatasets oafDatasets = new OafDatasets();
        oafDatasets.addDataset( "oaf", oafConfiguration );
        when( DeegreeWorkspaceInitializer.getOafDatasets() ).thenReturn( oafDatasets );
    }

    private void mockDataAccess()
                    throws Exception {
        PowerMockito.mockStatic( DataAccessFactory.class );
        DataAccess testFactory = Mockito.mock( DataAccess.class );
        Collection collection = createCollection();
        Collections testCollection = createCollections( collection );
        when( testFactory.createCollections( eq( "oaf" ), any( LinkBuilder.class ) ) ).thenReturn( testCollection );
        when( testFactory.createCollection( eq( "oaf" ), eq( "test" ), any( LinkBuilder.class ) ) ).thenReturn(
                        collection );
        when( testFactory.retrieveFeatures( eq( "oaf" ), eq( "test" ), any( FeaturesRequest.class ),
                                            any( LinkBuilder.class ) ) ).thenReturn( features() );
        when( testFactory.retrieveFeature( eq( "oaf" ), eq( "test" ), eq( "42" ), isNull(),
                                           any( LinkBuilder.class ) ) ).thenReturn( feature() );
        when( DataAccessFactory.getInstance() ).thenReturn( testFactory );
    }

    private FeatureResponse features() {
        List<Link> links = jsonLink( "http://self", "self", "title" );
        EmptyFeatureInputStream features = new EmptyFeatureInputStream();
        return new FeatureResponse( features, 10, 100, 0, links, false, null );
    }

    private FeatureResponse feature() {
        List<Link> links = jsonLink( "http://self", "self", "title" );
        EmptyFeatureInputStream features = new EmptyFeatureInputStream();
        return new FeatureResponse( features, 1, 1, 0, links, false, null );
    }

    private Collections createCollections( Collection collection ) {
        List<Link> links = jsonLink( "http://self", "self", "title" );
        List<Collection> collectionList = java.util.Collections.singletonList( collection );
        return new Collections( links, collectionList );
    }

    private Collection createCollection() {
        Extent extent = null;
        List<String> crs = java.util.Collections.singletonList( "EPSG:25832" );
        return new Collection( "test", null, null,
                               jsonLink( "http://self/collections/test/", "items", "collection test" ), extent, crs );
    }

    private List<Link> jsonLink( String href, String self, String title ) {
        return java.util.Collections.singletonList( new Link( href, self, "application/json", title ) );
    }

}
