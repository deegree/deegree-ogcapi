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
package org.deegree.services.oaf.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.deegree.feature.persistence.FeatureStore;
import org.deegree.feature.types.AppSchema;
import org.deegree.feature.types.FeatureType;
import org.deegree.services.oaf.exceptions.UnknownCollectionId;
import org.deegree.services.oaf.exceptions.UnknownDatasetId;
import org.deegree.services.oaf.link.LinkBuilder;
import org.deegree.services.oaf.schema.SchemaResponse;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.deegree.services.oaf.workspace.configuration.FeatureTypeMetadata;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;
import org.deegree.services.oaf.workspace.configuration.OafDatasets;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static org.deegree.gml.GMLVersion.GML_32;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Path("/datasets/{datasetId}/collections/{collectionId}/appschema")
public class Appschema {

    @Inject
    private DeegreeWorkspaceInitializer deegreeWorkspaceInitializer;

    @GET
    @Produces({ APPLICATION_XML })
    @Operation(operationId = "appschema",
                    summary = "retrieve application schema of collection {collectionId}",
                    description = "Retrieves the application schema of the collection with the id {collectionId}")
    @Tag(name = "Schema")
    public Response appschema(
                    @Context UriInfo uriInfo,
                    @PathParam("datasetId") String datasetId,
                    @PathParam("collectionId") String collectionId )
                    throws UnknownDatasetId, UnknownCollectionId {
        LinkBuilder linkBuilder = new LinkBuilder( uriInfo );
        OafDatasets oafDatasets = deegreeWorkspaceInitializer.getOafDatasets();
        OafDatasetConfiguration dataset = oafDatasets.getDataset( datasetId );
        FeatureTypeMetadata featureTypeMetadata = dataset.getFeatureTypeMetadata( collectionId );
        FeatureStore featureStore = dataset.getFeatureStore( featureTypeMetadata.getName(), collectionId );
        SchemaResponse schemaResponse = createSchemaResponse( featureStore, featureTypeMetadata, datasetId,
                                                              linkBuilder );
        return Response.ok( schemaResponse ).build();
    }

    private SchemaResponse createSchemaResponse( FeatureStore featureStore, FeatureTypeMetadata featureTypeMetadata,
                                                 String datasetId,
                                                 LinkBuilder linkBuilder ) {
        AppSchema schema = featureStore.getSchema();
        FeatureType featureType = schema.getFeatureType( featureTypeMetadata.getName() );
        String featureTypeNamespaceURI = featureType.getName().getNamespaceURI();
        Map<String, String> nsToSchemaLocation = buildNsToSchemaLocations( schema, featureTypeNamespaceURI,
                                                                           datasetId, linkBuilder );
        Map<String, String> prefixToNs = schema.getNamespaceBindings().entrySet().stream().collect(
                        toMap( e -> e.getKey(), e -> e.getValue() ) );
        return new SchemaResponse( featureType, nsToSchemaLocation, prefixToNs );
    }

    private Map<String, String> buildNsToSchemaLocations( AppSchema schema, String featureTypeNamespaceURI,
                                                          String datasetId,
                                                          LinkBuilder linkBuilder ) {
        List<String> additionalNamespaceUris = schema.getNamespacesDependencies( featureTypeNamespaceURI );

        Map<String, String> nsToSchemaLocation = new HashMap<>();
        for ( String ns : additionalNamespaceUris ) {
            Optional<QName> featureTypeName = findFeatureTypeNameByNamespace( schema, ns );
            if ( featureTypeName.isPresent() ) {
                String schemaLocation = linkBuilder.createSchemaLink( datasetId, featureTypeName.get().getLocalPart() );
                nsToSchemaLocation.put( ns, schemaLocation );
            }
        }
        return nsToSchemaLocation;
    }

    private Optional<QName> findFeatureTypeNameByNamespace( AppSchema schema, String namespaceUrl ) {
        if ( GML_32.getNamespace().equals( namespaceUrl ) )
            return Optional.empty();
        FeatureType[] featureTypes = schema.getFeatureTypes();
        return Arrays.stream( featureTypes ).filter(
                        featureType -> namespaceUrl.equals( featureType.getName().getNamespaceURI() ) ).map(
                        featureType -> featureType.getName() ).findFirst();
    }

}
