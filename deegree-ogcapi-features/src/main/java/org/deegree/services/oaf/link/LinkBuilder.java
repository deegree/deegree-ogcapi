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
package org.deegree.services.oaf.link;

import org.deegree.commons.ows.metadata.MetadataUrl;
import org.deegree.services.oaf.domain.License;
import org.deegree.services.oaf.workspace.configuration.DatasetMetadata;

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
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_32;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_SF0;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_SF2;
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
        String selfUri = getSelfUri();
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
     * @param datasetId
     *                 id of the dataset, never <code>null</code>
     * @param metadata
     *                 describing this service, never <code>null</code>
     * @return list of links, never <code>null</code>
     */
    public List<Link> createLandingPageLinks( String datasetId, DatasetMetadata metadata ) {
        List<Link> links = new ArrayList<>();
        String selfUri = getSelfUri();
        addSelfAndAlternateLinks( links, selfUri );
        String apiHref = createBaseUriBuilder( datasetId )
                        .path( "api" )
                        .toString();
        addServiceDesc( links, apiHref );

        String conformanceHref = createBaseUriBuilder( datasetId )
                        .path( "conformance" )
                        .toString();
        addConformance( links, conformanceHref );

        String collectionsHref = createBaseUriBuilder( datasetId )
                        .path( "collections" )
                        .toString();
        addData( links, collectionsHref );
        addMetadataLinks( metadata, links );
        addLicenseLink( datasetId, metadata, links );
        return links;
    }

    public List<Link> createCollectionsLinks( String datasetId, DatasetMetadata metadata ) {
        ArrayList<Link> links = new ArrayList<>();
        String selfUri = getSelfUri();
        addSelfAndAlternateLinks( links, selfUri );
        addMetadataLinks( metadata, links );
        addLicenseLink( datasetId, metadata, links );
        return links;
    }

    public List<Link> createCollectionLinks( String datasetId, String collectionId, List<MetadataUrl> metadataUrls ) {
        ArrayList<Link> links = new ArrayList<>();
        String selfUri = getSelfUri();
        List<String> collectionsParams = uriInfo.getPathParameters().get( "collectionId" );
        boolean collectionRequested = collectionsParams != null && !collectionsParams.isEmpty();
        if ( collectionRequested ) {
            addSelfAndAlternateLinks( links, selfUri );
        } else {
            String collectionHref = createBaseUriBuilder( datasetId )
                            .path( "collections" )
                            .path( collectionId )
                            .toString();
            addCollection( links, collectionHref );
        }

        String itemsHref = createBaseUriBuilder( datasetId )
                        .path( "collections" )
                        .path( collectionId )
                        .path( "items" )
                        .toString();
        addItems( links, itemsHref );

        metadataUrls.forEach( metadataUrl -> {
            links.add( createMetadataLink( metadataUrl, "Metadata describing this Collection" ) );
        } );
        return links;
    }

    public List<Link> createFeaturesLinks( String datasetId, String collectionId, NextLink nextLink ) {
        List<Link> links = new ArrayList<>();
        String selfUri = getSelfUri();
        addSelfAndAlternateGeo( links, selfUri );
        String nextUri = nextLink.createUri( uriInfo );
        if ( nextUri != null )
            links.add( new Link( nextUri, NEXT.getRel(), APPLICATION_GEOJSON, "next page" ) );
        String collectionUri = createBaseUriBuilder( datasetId )
                        .path( "collections" )
                        .path( collectionId )
                        .toString();
        addCollection( links, collectionUri );
        return links;
    }

    public List<Link> createFeatureLinks( String datasetId, String collectionId, String featureId ) {
        List<Link> links = new ArrayList<>();
        String selfUri = getSelfUri();
        addSelfAndAlternateGeo( links, selfUri );
        String collectionUri = createBaseUriBuilder( datasetId )
                        .path( "collections" )
                        .path( collectionId )
                        .toString();
        addCollection( links, collectionUri );
        return links;
    }

    private void addSelfAndAlternateLinks( List<Link> links, String uri ) {
        links.add( new Link( uri, SELF.getRel(), APPLICATION_JSON, "this document as JSON" ) );
        links.add( new Link( uri, ALTERNATE.getRel(), TEXT_HTML, "this document as HTML" ) );
        links.add( new Link( uri, ALTERNATE.getRel(), APPLICATION_XML, "this document as XML" ) );
    }

    private void addSelfAndAlternateGeo( List<Link> links, String uri ) {
        links.add( new Link( uri, SELF.getRel(), APPLICATION_GEOJSON, "this document as JSON" ) );
        links.add( new Link( uri, ALTERNATE.getRel(), TEXT_HTML, "this document as HTML" ) );
        links.add( new Link( uri, ALTERNATE.getRel(), APPLICATION_GML, "this document as GML" ) );
        links.add( new Link( uri, ALTERNATE.getRel(), APPLICATION_GML_32, "this document as GML" ) );
        links.add( new Link( uri, ALTERNATE.getRel(), APPLICATION_GML_SF0, "this document as GML" ) );
        links.add( new Link( uri, ALTERNATE.getRel(), APPLICATION_GML_SF2, "this document as GML" ) );
    }

    private void addConformance( List<Link> links, String conformanceHref ) {
        links.add( new Link( conformanceHref, CONFORMANCE.getRel(), APPLICATION_JSON,
                             "OGC API conformance classes as Json" ) );
        links.add( new Link( conformanceHref, CONFORMANCE.getRel(), TEXT_HTML,
                             "OGC API conformance classes as HTML" ) );
        links.add( new Link( conformanceHref, CONFORMANCE.getRel(), APPLICATION_XML,
                             "OGC API conformance classes as XML" ) );
    }

    private void addServiceDesc( List<Link> links, String apiHref ) {
        links.add( new Link( apiHref, SERVICE_DESC.getRel(), APPLICATION_OPENAPI, "API definition" ) );
        links.add( new Link( apiHref, SERVICE_DESC.getRel(), TEXT_HTML, "API definition as HTML" ) );
    }

    private void addData( List<Link> links, String collectionsHref ) {
        links.add( new Link( collectionsHref, DATA.getRel(), APPLICATION_JSON,
                             "Supported Feature Collections as JSON" ) );
        links.add( new Link( collectionsHref, DATA.getRel(), TEXT_HTML, "Supported Feature Collections as HTML" ) );
        links.add( new Link( collectionsHref, DATA.getRel(), APPLICATION_XML,
                             "Supported Feature Collections as XML" ) );
    }

    private void addCollection( List<Link> links, String uri ) {
        links.add( new Link( uri, COLLECTION.getRel(), APPLICATION_JSON, "Collection as JSON" ) );
        links.add( new Link( uri, COLLECTION.getRel(), TEXT_HTML, "Collection as HTML" ) );
        links.add( new Link( uri, COLLECTION.getRel(), APPLICATION_XML, "Collection as XML" ) );
    }

    private void addItems( ArrayList<Link> links, String itemsHref ) {
        links.add( new Link( itemsHref, ITEMS.getRel(), APPLICATION_GEOJSON, "Features as GeoJson" ) );
        links.add( new Link( itemsHref, ITEMS.getRel(), TEXT_HTML, "Features as HTML" ) );
        links.add( new Link( itemsHref, ITEMS.getRel(), APPLICATION_GML, "Features as GML" ) );
        links.add( new Link( itemsHref, ITEMS.getRel(), APPLICATION_GML_32, "Features as GML" ) );
        links.add( new Link( itemsHref, ITEMS.getRel(), APPLICATION_GML_SF0, "Features as GML" ) );
        links.add( new Link( itemsHref, ITEMS.getRel(), APPLICATION_GML_SF2, "Features as GML" ) );
    }

    private void addMetadataLinks( DatasetMetadata metadata, List<Link> links ) {
        metadata.getMetadataUrls().forEach( metadataUrl -> {
            links.add( createMetadataLink( metadataUrl, "Metadata describing this dataset" ) );
        } );
    }

    private void addLicenseLink( String datasetId, DatasetMetadata metadata, List<Link> links ) {
        if ( metadata != null && metadata.getDatasetLicense() != null ) {
            License datasetLicense = metadata.getDatasetLicense();
            if ( datasetLicense.getUrl() != null ) {
                links.add( new Link( datasetLicense.getUrl(), LICENSE.getRel(), datasetLicense.getUrlFormat(),
                                     datasetLicense.getName() ) );
            } else {
                String licenseHref = createBaseUriBuilder( datasetId )
                                .path( "license" )
                                .path( "dataset" )
                                .toString();
                links.add( new Link( licenseHref, LICENSE.getRel(), TEXT_PLAIN, datasetLicense.getName() ) );
            }
        }
    }

    private String getSelfUri() {
        return uriInfo.getBaseUriBuilder().path(  uriInfo.getPath() ).toString();
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
