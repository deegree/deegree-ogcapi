package org.deegree.services.oaf.link;

import org.deegree.commons.ows.metadata.MetadataUrl;
import org.deegree.services.oaf.workspace.configuration.ServiceMetadata;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GEOJSON;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_OPENAPI;
import static org.deegree.services.oaf.link.LinkRelation.ALTERNATE;
import static org.deegree.services.oaf.link.LinkRelation.COLLECTION;
import static org.deegree.services.oaf.link.LinkRelation.CONFORMANCE;
import static org.deegree.services.oaf.link.LinkRelation.DATA;
import static org.deegree.services.oaf.link.LinkRelation.DESCRIBEDBY;
import static org.deegree.services.oaf.link.LinkRelation.ITEMS;
import static org.deegree.services.oaf.link.LinkRelation.LICENSE;
import static org.deegree.services.oaf.link.LinkRelation.NEXT;
import static org.deegree.services.oaf.link.LinkRelation.SELF;
import static org.deegree.services.oaf.link.LinkRelation.SERVICE_DESC;
import static org.deegree.services.oaf.link.LinkRelation.SERVICE_DOC;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class LinkBuilder {

    private final UriInfo uriInfo;

    public LinkBuilder( UriInfo uriInfo ) {
        this.uriInfo = uriInfo;
    }

    /**
     * @return list of links, never <code>null</code>
     */
    public List<Link> createDatasetsLinks() {
        List<Link> links = new ArrayList<>();
        String selfUri = uriInfo.getRequestUri().toString();
        links.add( new Link( selfUri, SELF.getRel(), APPLICATION_JSON, "this document" ) );
        links.add( new Link( selfUri, ALTERNATE.getRel(), TEXT_HTML, "this document as HTML" ) );
        return links;
    }

    /**
     * @param datasetId
     *                 id of the dataset, never <code>null</code>
     * @return list of links, never <code>null</code>
     */
    public List<Link> createDatasetLinks( String datasetId ) {
        List<Link> links = new ArrayList<>();
        String datasetHref = createBaseUriBuilder( datasetId )
                        .toString();
        links.add( new Link( datasetHref, SERVICE_DOC.getRel(), APPLICATION_JSON, "Landing Page" ) );
        links.add( new Link( datasetHref, SERVICE_DOC.getRel(), TEXT_HTML, "Landing Page as HTML" ) );
        return links;
    }

    /**
     *
     * @param datasetId
     * @param metadataUrls
     *                 list of metadata URLs describing this service, never <code>null</code>
     * @return list of links, never <code>null</code>
     */
    public List<Link> createLandingPageLinks( String datasetId, List<MetadataUrl> metadataUrls ) {
        List<Link> links = new ArrayList<>();
        String selfUri = uriInfo.getRequestUri().toString();
        links.add( new Link( selfUri, SELF.getRel(), APPLICATION_JSON, "this document as JSON" ) );
        links.add( new Link( selfUri, ALTERNATE.getRel(), TEXT_HTML, "this document as HTML" ) );
        links.add( new Link( selfUri, ALTERNATE.getRel(), APPLICATION_XML, "this document as XML" ) );

        String apiHref = createBaseUriBuilder( datasetId )
                                    .path( "api" )
                                    .toString();
        links.add( new Link( apiHref, SERVICE_DESC.getRel(), APPLICATION_OPENAPI, "API definition" ) );
        links.add( new Link( apiHref, SERVICE_DESC.getRel(), TEXT_HTML, "API definition as HTML" ) );

        String conformanceHref = createBaseUriBuilder( datasetId )
                                .path( "conformance" )
                                .toString();
        links.add( new Link( conformanceHref, CONFORMANCE.getRel(), APPLICATION_JSON,
                             "OGC API conformance classes as Json" ) );
        links.add( new Link( conformanceHref, CONFORMANCE.getRel(), TEXT_HTML,
                             "OGC API conformance classes as HTML" ) );
        links.add( new Link( conformanceHref, CONFORMANCE.getRel(), APPLICATION_XML,
                             "OGC API conformance classes as XML" ) );

        String collectionsHref = createBaseUriBuilder( datasetId )
                                        .path( "collections" )
                                        .toString();
        links.add( new Link( collectionsHref, DATA.getRel(), APPLICATION_JSON,
                             "Supported Feature Collections as JSON" ) );
        links.add( new Link( collectionsHref, DATA.getRel(), TEXT_HTML, "Supported Feature Collections as HTML" ) );
        links.add( new Link( collectionsHref, DATA.getRel(), APPLICATION_XML,
                             "Supported Feature Collections as XML" ) );
        metadataUrls.forEach( metadataUrl -> {
            links.add( createMetadataLink( metadataUrl, "Metadata describing this dataset" ) );
        } );
        return links;
    }

    public List<Link> createCollectionsLinks( String datasetId, ServiceMetadata serviceMetadata ) {
        ArrayList<Link> links = new ArrayList<>();
        String selfUri = uriInfo.getRequestUri().toString();
        links.add( new Link( selfUri, SELF.getRel(), APPLICATION_JSON, "this document as JSON" ) );
        links.add( new Link( selfUri, ALTERNATE.getRel(), TEXT_HTML, "this document as HTML" ) );
        links.add( new Link( selfUri, ALTERNATE.getRel(), APPLICATION_XML, "this document as XML" ) );
        if ( serviceMetadata != null && serviceMetadata.hasLicense() ) {
            String licenseHref = createBaseUriBuilder( datasetId )
                                           .path( "license" )
                                           .toString();
            links.add( new Link( licenseHref, LICENSE.getRel(), TEXT_PLAIN, "the license of the feature collections" ) );
        }
        return links;
    }

    public List<Link> createCollectionLinks( String datasetId, String collectionId, List<MetadataUrl> metadataUrls ) {
        ArrayList<Link> links = new ArrayList<>();
        String selfUri = uriInfo.getRequestUri().toString();
        List<String> collectionsParams = uriInfo.getPathParameters().get( "collectionId" );
        boolean collectionRequested = collectionsParams != null && !collectionsParams.isEmpty();
        if ( collectionRequested ) {
            links.add( new Link( selfUri, SELF.getRel(), APPLICATION_JSON, "this document as JSON" ) );
            links.add( new Link( selfUri, ALTERNATE.getRel(), TEXT_HTML, "this document as HTML" ) );
            links.add( new Link( selfUri, ALTERNATE.getRel(), APPLICATION_XML, "this document as XML" ) );
        } else {
            String collectionHref = createBaseUriBuilder( datasetId )
                            .path( "collections" )
                            .path( collectionId )
                            .toString();
            links.add( new Link( collectionHref, COLLECTION.getRel(), APPLICATION_JSON, "Collection as GeoJson" ) );
            links.add( new Link( collectionHref, COLLECTION.getRel(), TEXT_HTML, "Collection as HTML" ) );
            links.add( new Link( collectionHref, COLLECTION.getRel(), APPLICATION_XML, "Collection as XML" ) );
        }

        String itemsHref = createBaseUriBuilder( datasetId )
                                  .path( "collections" )
                                  .path( collectionId )
                                  .path( "items" )
                                  .toString();
        links.add( new Link( itemsHref, ITEMS.getRel(), APPLICATION_GEOJSON, "Features as GeoJson" ) );
        links.add( new Link( itemsHref, ITEMS.getRel(), TEXT_HTML, "Features as HTML" ) );
        links.add( new Link( itemsHref, ITEMS.getRel(), APPLICATION_GML, "Features as GML" ) );

        metadataUrls.forEach( metadataUrl -> {
            links.add( createMetadataLink( metadataUrl, "Metadata describing this Collection" ) );
        } );
        return links;
    }

    public ArrayList<Link> createFeaturesLinks( String datasetId, String collectionId, NextLink nextLink ) {
        ArrayList<Link> links = new ArrayList<>();
        String selfUri = uriInfo.getRequestUri().toString();
        links.add( new Link( selfUri, SELF.getRel(), APPLICATION_GEOJSON, "this document as GeoJson" ) );
        links.add( new Link( selfUri, ALTERNATE.getRel(), TEXT_HTML, "this document as HTML" ) );
        links.add( new Link( selfUri, ALTERNATE.getRel(), APPLICATION_GML, "this document as GML" ) );
        String nextUri = nextLink.createUri( uriInfo );
        if ( nextUri != null )
            links.add( new Link( nextUri, NEXT.getRel(), APPLICATION_GEOJSON, "next page" ) );
        String collectionUri = createBaseUriBuilder( datasetId )
                                      .path( "collections" )
                                      .path( collectionId )
                                      .toString();
        links.add( new Link( collectionUri, COLLECTION.getRel(), APPLICATION_JSON, "Collection as JSON" ) );
        links.add( new Link( collectionUri, COLLECTION.getRel(), TEXT_HTML, "Collection as HTML" ) );
        links.add( new Link( collectionUri, COLLECTION.getRel(), APPLICATION_XML, "Collection as XML" ) );
        return links;
    }

    public ArrayList<Link> createFeatureLinks( String datasetId, String collectionId, String featureId ) {
        ArrayList<Link> links = new ArrayList<>();
        String selfUri = uriInfo.getRequestUri().toString();
        links.add( new Link( selfUri, SELF.getRel(), APPLICATION_GEOJSON, "this document as JSON" ) );
        links.add( new Link( selfUri, ALTERNATE.getRel(), TEXT_HTML, "this document as HTML" ) );
        links.add( new Link( selfUri, ALTERNATE.getRel(), APPLICATION_GML, "this document as GML" ) );
        String collectionUri = createBaseUriBuilder( datasetId )
                                      .path( "collections" )
                                      .path( collectionId )
                                      .toString();
        links.add( new Link( collectionUri, COLLECTION.getRel(), APPLICATION_JSON, "Collection as JSON" ) );
        links.add( new Link( collectionUri, COLLECTION.getRel(), TEXT_HTML, "Collection as HTML" ) );
        links.add( new Link( collectionUri, COLLECTION.getRel(), APPLICATION_XML, "Collection as XML" ) );
        return links;
    }


    private UriBuilder createBaseUriBuilder( String datasetId ) {
        return uriInfo.getBaseUriBuilder()
                      .path( "datasets" )
                      .path( datasetId );
    }

    private Link createMetadataLink( MetadataUrl metadataUrl, String title ) {
        String type = metadataUrl.getFormat() != null ? metadataUrl.getFormat() : APPLICATION_XML;
        return new Link( metadataUrl.getUrl(), DESCRIBEDBY.getRel(), type, title );
    }

}