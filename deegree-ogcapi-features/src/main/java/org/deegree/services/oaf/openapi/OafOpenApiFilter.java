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
package org.deegree.services.oaf.openapi;

import io.swagger.v3.core.filter.AbstractSpecFilter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.deegree.commons.tom.gml.property.PropertyType;
import org.deegree.commons.tom.primitive.BaseType;
import org.deegree.commons.tom.primitive.PrimitiveType;
import org.deegree.feature.types.FeatureType;
import org.deegree.feature.types.property.CustomPropertyType;
import org.deegree.feature.types.property.GeometryPropertyType;
import org.deegree.feature.types.property.SimplePropertyType;
import org.deegree.gml.GMLVersion;
import org.deegree.services.oaf.exceptions.UnknownDatasetId;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.deegree.services.oaf.workspace.configuration.FeatureTypeMetadata;
import org.deegree.services.oaf.workspace.configuration.FilterProperty;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;
import org.deegree.services.oaf.workspace.configuration.OafDatasets;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static org.apache.xerces.xs.XSConstants.ELEMENT_DECLARATION;
import static org.apache.xerces.xs.XSConstants.MODEL_GROUP;
import static org.apache.xerces.xs.XSTypeDefinition.SIMPLE_TYPE;
import static org.apache.xerces.xs.XSTypeDefinition.COMPLEX_TYPE;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GEOJSON;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_32;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_SF0;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GML_SF2;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class OafOpenApiFilter extends AbstractSpecFilter {

    private static final Logger LOG = getLogger( OafOpenApiFilter.class );

    private static final String COLLECTIONID = "collectionId";

    private static final String DATASETID = "datasetId";

    public static final String CONFIG_PATH = "/config";

    public static final String DATASET_PREFIX = "/datasets/{datasetId}";

    public static final String LANDINGPAGE_PATH = "/datasets/{datasetId}";

    public static final String API_PATH = "/datasets/{datasetId}/api";

    public static final String CONFORMANCE_PATH = "/datasets/{datasetId}/conformance";

    private static final String COLLECTIONS_PATH = "/datasets/{datasetId}/collections";

    private static final String COLLECTION_PATH = "/datasets/{datasetId}/collections/{collectionId}";

    private static final String SCHEMA_PATH = "/datasets/{datasetId}/collections/{collectionId}/appschema";

    private static final String FEATURES_PATH = "/datasets/{datasetId}/collections/{collectionId}/items";

    private static final String FEATURE_PATH = "/datasets/{datasetId}/collections/{collectionId}/items/{featureId}";

    public static final List<String> SUPPPORTED_GEOM_TYPES = Arrays.asList( "Point", "LineString", "Polygon", "MultiPoint", "MultiLineString",
                                                                            "MultiPolygon" );

    public static final String GEOMETRY_PROPERTY_NAME = "geometry";

    private final UriInfo uriInfo;

    private final String datasetId;

    private final OafDatasetConfiguration datasetConfiguration;

    @Inject
    private DeegreeWorkspaceInitializer deegreeWorkspaceInitializer;

    public OafOpenApiFilter(UriInfo uriInfo, String datasetId,
			DeegreeWorkspaceInitializer deegreeWorkspaceInitializer) throws UnknownDatasetId {
        this.uriInfo = uriInfo;
        this.datasetId = datasetId;
        OafDatasets oafDatasets = deegreeWorkspaceInitializer.getOafDatasets();
        this.datasetConfiguration = oafDatasets.getDataset( datasetId );
    }

    @Override
    public Optional<OpenAPI> filterOpenAPI( OpenAPI openAPI, Map<String, List<String>> params,
                                            Map<String, String> cookies, Map<String, List<String>> headers ) {
        filterServers( openAPI );
        filterPaths( openAPI );
        return super.filterOpenAPI( openAPI, params, cookies, headers );
    }

	private void filterServers(OpenAPI openAPI) {
		if (openAPI.getServers() == null || openAPI.getServers().isEmpty())
			openAPI.addServersItem(new Server());
		Server server = openAPI.getServers().get(0);
        String relativePathToOpenApi = createRelativePathToOpenApi();
        server.setUrl(relativePathToOpenApi);
	}

	private String createRelativePathToOpenApi() {
		String baseUriPath = uriInfo.getBaseUri().getPath();
		if (!baseUriPath.endsWith("/"))
			baseUriPath = baseUriPath + "/";
		return baseUriPath + "datasets/" + datasetId;
	}

    private void filterPaths( OpenAPI openAPI ) {
        Paths paths = openAPI.getPaths();
        PathItem api = paths.get( API_PATH );
        PathItem landingPage = paths.get( LANDINGPAGE_PATH );
        PathItem conformance = paths.get( CONFORMANCE_PATH );
        PathItem collections = paths.get( COLLECTIONS_PATH );
        PathItem collection = paths.get( COLLECTION_PATH );
        PathItem schema = paths.get( SCHEMA_PATH );
        PathItem features = paths.get( FEATURES_PATH );
        PathItem feature = paths.get( FEATURE_PATH );

        addMediaTypes( api, TEXT_HTML );
        addMediaTypes( landingPage, APPLICATION_XML, TEXT_HTML );
        addMediaTypes( conformance, APPLICATION_XML, TEXT_HTML );
        addMediaTypes( collections, APPLICATION_XML, TEXT_HTML );
        addMediaTypes( collection, APPLICATION_XML, TEXT_HTML );
        addMediaTypes( features, APPLICATION_GML, APPLICATION_GML_32, APPLICATION_GML_SF0,
                       APPLICATION_GML_SF2, TEXT_HTML );
        addMediaTypes( feature, APPLICATION_GML, APPLICATION_GML_32, APPLICATION_GML_SF0,
                       APPLICATION_GML_SF2, TEXT_HTML );

        Map<String, FeatureTypeMetadata> featureTypeMetadatas = datasetConfiguration.getFeatureTypeMetadata();
        featureTypeMetadatas.entrySet().forEach( featureTypeMetadata -> {
            String key = featureTypeMetadata.getKey();
            FeatureTypeMetadata metadata = featureTypeMetadata.getValue();
            PathItem newCollectionPathItem = createNewPathItem( collection, key, metadata );
            paths.addPathItem( replaceCollectionId( COLLECTION_PATH, key ), newCollectionPathItem );

            PathItem newSchemaPathItem = createNewPathItem( schema, key, metadata );
            paths.addPathItem( replaceCollectionId( SCHEMA_PATH, key ), newSchemaPathItem );

            PathItem newFeaturesPathItem = createNewPathItem( features, key, metadata );
            addSchema( newFeaturesPathItem, metadata.getFeatureType() );
            addFilterProperties( newFeaturesPathItem, metadata.getFilterProperties() );
            paths.addPathItem( replaceCollectionId( FEATURES_PATH, key ), newFeaturesPathItem );

            PathItem newFeaturePathItem = createNewPathItem( feature, key, metadata );
            paths.addPathItem( replaceCollectionId( FEATURE_PATH, key ), newFeaturePathItem );

        } );

        paths.remove( COLLECTION_PATH );
        paths.remove( SCHEMA_PATH );
        paths.remove( FEATURES_PATH );
        paths.remove( FEATURE_PATH );

        Map<String, PathItem> pathItemsWithAdaptedPath = new HashMap<>();
        paths.entrySet().forEach( stringPathItemEntry -> {
            if ( !stringPathItemEntry.getKey().startsWith( CONFIG_PATH ) ) {
                String replacedPath = createNewPath( stringPathItemEntry );
                PathItem value = stringPathItemEntry.getValue();
                removeDatasetParam( value );
                pathItemsWithAdaptedPath.put( replacedPath, value );
            }
        } );
        paths.clear();
        paths.putAll( pathItemsWithAdaptedPath );
    }

    private void addMediaTypes( PathItem pathItem, String... mediaTypes ) {
        if ( pathItem == null )
            return;
        Content content = pathItem.getGet().getResponses().getDefault().getContent();
        for ( String mediaTypeString : mediaTypes ) {
            MediaType mediaType = new MediaType().schema( new Schema() );
            content.addMediaType( mediaTypeString, mediaType );
        }
    }

    private String createNewPath( Map.Entry<String, PathItem> stringPathItemEntry ) {
        String path = stringPathItemEntry.getKey();
        String replacedPath = path.replace( DATASET_PREFIX, "" );
        if ( replacedPath.isEmpty() )
            replacedPath = "/";
        return replacedPath;
    }

    private void addFilterProperties( PathItem pathItem, List<FilterProperty> filterProperties ) {
        if ( filterProperties == null )
            return;
        List<Parameter> parameters = pathItem.getGet().getParameters();
        filterProperties.forEach( filterProperty -> {
            Parameter filterParameter = new Parameter()
                            .name( filterProperty.getName().getLocalPart() )
                            .description( descriptionByParameterType( filterProperty ) )
                            .in( "query" )
                            .style( Parameter.StyleEnum.FORM )
                            .explode( false )
                            .schema( new Schema().type( mapToParameterType( filterProperty ) ) );
            parameters.add( filterParameter );
        } );
    }

    private void addSchema( PathItem pathItem, FeatureType featureType ) {
        Operation get = pathItem.getGet();
        ApiResponse response = get.getResponses().getDefault();
        MediaType mediaType = response.getContent().get( APPLICATION_GEOJSON );
        Schema schema = createFeatureResponseSchema( featureType );
        mediaType.setSchema( schema );
    }

    private Schema createFeatureResponseSchema( FeatureType featureType ) {
        Schema schema = new Schema();
        Schema type = new Schema()
                        .name( "type" )
                        .type( "string" );
        type.addEnumItemObject( "FeatureCollection" );
        Schema numberMatched = new Schema()
                        .name( "numberMatched" )
                        .type( "integer" )
                        .example( 178 );
        Schema numberReturned = new Schema()
                        .name( "numberReturned" )
                        .type( "integer" )
                        .example( 10 );
        Schema timeStamp = new Schema()
                        .name( "timeStamp" )
                        .type( "string" ).example( "2020-12-14T09:32:42.669Z" );
        Schema links = new ArraySchema()
                        .items( new Schema().$ref( "#/components/schemas/Link" ) )
                        .name( "links" );
        schema.addProperties( "type", type );
        schema.addProperties( "numberMatched", numberMatched );
        schema.addProperties( "numberReturned", numberReturned );
        schema.addProperties( "timeStamp", timeStamp );
        schema.addProperties( "links", links );
        schema.addProperties( "features", createFeatureTypeSchema( featureType ) );
        return schema;
    }

    private Schema createFeatureTypeSchema( FeatureType featureType ) {
        Schema<Object> schema = new Schema<>();
        Schema type = new Schema()
                                .name( "type" )
                                .type( "string" );
        type.addEnumItemObject( "Feature" );
        Schema id = new Schema()
                                .name( "id" )
                                .type( "string" )
                                .example( "ID_1" );
        schema.addProperties( "type", type );
        schema.addProperties( "id", id );
        addGeoemtrySchema( featureType, schema );
        addPropertiesSchema( featureType, schema );
        return schema;
    }

    private void addGeoemtrySchema( FeatureType featureType, Schema<Object> schema ) {
        if ( featureType.getDefaultGeometryPropertyDeclaration() != null ) {
            Schema geometryPropertySchema = createGeometryPropertySchema();
            schema.addProperties( GEOMETRY_PROPERTY_NAME, geometryPropertySchema );
        }
    }

    private void addPropertiesSchema( FeatureType featureType, Schema<Object> schema ) {
        Schema properties = new Schema()
                                .name( "properties" )
                                .type( "object" );
        featureType.getPropertyDeclarations().forEach( propertyType -> {
            Schema propertyItem = createSchemaForProperty( propertyType );
            if ( propertyItem != null ) {
                properties.addProperties( propertyType.getName().getLocalPart(), propertyItem );
            }
        } );
        schema.addProperties( "properties", properties );
    }

    private Schema createSchemaForProperty( PropertyType propertyType ) {
        if ( propertyType instanceof GeometryPropertyType ) {
            return null;
        }
        if ( propertyType instanceof SimplePropertyType && isMaxOccursGreaterThanOne( propertyType ) ) {
            return new ArraySchema().items( new Schema().type( mapToPropertyType( propertyType ) ) ).name(
                                    propertyType.getName().getLocalPart() ).type( "array" );
        }
        if ( propertyType instanceof CustomPropertyType && !isGmlProperty( propertyType ) ) {
            XSComplexTypeDefinition xsdValueType = ( (CustomPropertyType) propertyType ).getXSDValueType();
            XSParticle particle = xsdValueType.getParticle();

            if ( isMaxOccursGreaterThanOne( propertyType ) ) {
                Schema arraySchema = new ArraySchema().items(
                                        new Schema().type( mapToPropertyType( propertyType ) ) ).name(
                                        propertyType.getName().getLocalPart() ).type( "array" );
                addParticle( arraySchema, particle );
                return arraySchema;
            } else {
                Schema propertySchema = new Schema().name( propertyType.getName().getLocalPart() );
                addParticle( propertySchema, particle );
                return propertySchema;
            }
        }
        return new Schema().name( propertyType.getName().getLocalPart() ).type( mapToPropertyType( propertyType ) );
    }

    private Schema createGeometryPropertySchema() {
        Map<String, Schema> properties = new HashMap<>();
        Schema typeEnum = new Schema();
        typeEnum.setDescription( "Type of the geometry, one of " + SUPPPORTED_GEOM_TYPES.stream().collect(
                                Collectors.joining( ", " ) ) );
        typeEnum.setEnum( SUPPPORTED_GEOM_TYPES );
        properties.put( "type", typeEnum );
        Schema coordinatesSchema = new ArraySchema().items( new Schema().type( "number" ) ).description(
                                "An array of double values or an array of arrays of double values describing the geometry" );
        properties.put( "coordinates", coordinatesSchema );
        return new Schema().name( GEOMETRY_PROPERTY_NAME ).description(
                                "Geometry (" + SUPPPORTED_GEOM_TYPES.stream().collect( Collectors.joining( ", " ) )
                                + ") as specified in RFC 7946." ).type( "object" ).properties( properties );
    }

    private void addParticle( Schema schema, XSParticle particle ) {
        if ( particle != null ) {
            XSTerm term = particle.getTerm();
            switch ( term.getType() ) {
            case MODEL_GROUP: {
                XSModelGroup modelGroup = (XSModelGroup) term;
                addParticle( schema, modelGroup );
                break;
            }
            case ELEMENT_DECLARATION: {
                XSElementDeclaration elementDeclaration = (XSElementDeclaration) term;
                addParticle( schema, particle, elementDeclaration );
                break;
            }
            }
        }
    }

    private void addParticle( Schema schema, XSModelGroup modelGroup ) {
        XSObjectList particles = modelGroup.getParticles();
        for ( int particleIndex = 0; particleIndex < particles.getLength(); particleIndex++ ) {
            XSObject item = particles.item( particleIndex );
            if ( item instanceof XSParticle )
                addParticle( schema, (XSParticle) item );
        }
    }

    private void addParticle( Schema schema, XSParticle particle, XSElementDeclaration elementDeclaration ) {
        XSTypeDefinition typeDef = elementDeclaration.getTypeDefinition();
        XSTypeDefinition baseType = typeDef.getBaseType();

        switch ( baseType.getTypeCategory() ) {
        case SIMPLE_TYPE: {
            Schema simpleSchema = new Schema().name( elementDeclaration.getName() );
            simpleSchema.type( mapToPropertyType( (XSSimpleTypeDefinition) baseType ) );
            if ( particle.getMaxOccursUnbounded() ) {
                ArraySchema arraySchema = new ArraySchema().items( simpleSchema );
                schema.addProperties( elementDeclaration.getName(), arraySchema );
            } else {
                schema.addProperties( elementDeclaration.getName(), simpleSchema );
            }
            break;
        }
        case COMPLEX_TYPE: {
            XSComplexTypeDefinition complexTypeDef = (XSComplexTypeDefinition) typeDef;
            XSParticle complexParticle = complexTypeDef.getParticle();

            if ( particle.getMaxOccursUnbounded() ) {
                Schema itemSchema = new Schema();
                ArraySchema arraySchema = new ArraySchema().items( itemSchema );
                schema.addProperties( elementDeclaration.getName(), arraySchema );
                addParticle( itemSchema, complexParticle );
            } else {
                Schema newSchema = new Schema().name( elementDeclaration.getName() );
                schema.addProperties( elementDeclaration.getName(), newSchema );
                addParticle( newSchema, complexParticle );
            }
            break;
        }
        }
    }

    private boolean isGmlProperty( PropertyType propertyType ) {
        String namespaceURI = propertyType.getName().getNamespaceURI();
        for ( GMLVersion gmlVersion : GMLVersion.values() ) {
            if ( gmlVersion.getNamespace().equals( namespaceURI ) )
                return true;
        }
        return false;
    }

    private PathItem createNewPathItem( PathItem pathItemToClone, String name, FeatureTypeMetadata metadata ) {
        String title = metadata.getTitle();

        PathItem clonedPathItem = new PathItem();
        clonedPathItem.set$ref( pathItemToClone.get$ref() );
        clonedPathItem.setDescription( replaceCollectionId( pathItemToClone.getDescription(), title ) );
        clonedPathItem.setSummary( replaceCollectionId( pathItemToClone.getSummary(), title ) );

        clonedPathItem.setExtensions( pathItemToClone.getExtensions() );
        clonedPathItem.setParameters( pathItemToClone.getParameters() );
        clonedPathItem.setServers( pathItemToClone.getServers() );

        pathItemToClone.readOperationsMap().entrySet().forEach( method2operation -> {
            PathItem.HttpMethod method = method2operation.getKey();
            Operation operation = method2operation.getValue();
            Operation clonedOperation = createNewOperation( operation, name );

            clonedPathItem.operation( method, clonedOperation );

        } );

        return clonedPathItem;
    }

    private Operation createNewOperation( Operation operation, String name ) {
        Operation clonedOperation = new Operation();
        clonedOperation.setOperationId( operation.getOperationId() + "-" + name );
        clonedOperation.setSummary( operation.getSummary() );
        clonedOperation.setCallbacks( operation.getCallbacks() );
        clonedOperation.setDeprecated( operation.getDeprecated() );
        clonedOperation.setDescription( operation.getDescription() );
        clonedOperation.setExtensions( operation.getExtensions() );
        clonedOperation.setExternalDocs( operation.getExternalDocs() );
        List<Parameter> parameters = createNewParameters( operation );

        clonedOperation.setParameters( parameters );
        clonedOperation.setSecurity( operation.getSecurity() );
        clonedOperation.setServers( operation.getServers() );
        clonedOperation.setTags( operation.getTags() );
        ApiResponses responses = createNewResponses( operation.getResponses() );
        clonedOperation.setResponses( responses );
        return clonedOperation;
    }

    private List<Parameter> createNewParameters( Operation operation ) {
        List<Parameter> parameters = new ArrayList<>();
        operation.getParameters().forEach( parameter -> {
            if ( !COLLECTIONID.equals( parameter.getName() ) && !DATASETID.equals( parameter.getName() ) ) {
                parameters.add( parameter );
            }
        } );
        return parameters;
    }

    private ApiResponses createNewResponses( ApiResponses responses ) {
        if ( responses == null )
            return null;
        ApiResponses clonedResponses = new ApiResponses();
        clonedResponses.setDefault( createNewDefault( responses.getDefault() ) );
        clonedResponses.setExtensions( responses.getExtensions() );
        return clonedResponses;
    }

    private ApiResponse createNewDefault( ApiResponse response ) {
        if ( response == null )
            return null;
        ApiResponse clonedResponse = new ApiResponse();
        clonedResponse.setDescription( response.getDescription() );
        clonedResponse.set$ref( response.get$ref() );
        clonedResponse.setContent( createNewContent( response.getContent() ) );
        clonedResponse.setExtensions( response.getExtensions() );
        clonedResponse.setHeaders( response.getHeaders() );
        clonedResponse.setLinks( response.getLinks() );
        return clonedResponse;
    }

    private Content createNewContent( Content content ) {
        if ( content == null )
            return null;
        Content clonedContent = new Content();
        content.forEach( ( s, mediaType ) -> {
            clonedContent.addMediaType( s, createNewMediaType( mediaType ) );
        } );
        return clonedContent;
    }

    private MediaType createNewMediaType( MediaType mediaType ) {
        if ( mediaType == null )
            return null;
        MediaType clonedMediaType = new MediaType();
        clonedMediaType.setSchema( createNewSchema( mediaType.getSchema() ) );
        clonedMediaType.setEncoding( mediaType.getEncoding() );
        clonedMediaType.setExample( mediaType.getExample() );
        clonedMediaType.setExamples( mediaType.getExamples() );
        clonedMediaType.setExtensions( mediaType.getExtensions() );
        return clonedMediaType;
    }

    private Schema createNewSchema( Schema schema ) {
        if ( schema == null )
            return null;
        Schema clonedSchema = new Schema();
        clonedSchema.set$ref( schema.get$ref() );
        clonedSchema.setAdditionalProperties( schema.getAdditionalProperties() );
        clonedSchema.setDefault( schema.getAdditionalProperties() );
        clonedSchema.setDeprecated( schema.getDeprecated() );
        clonedSchema.setDescription( schema.getDescription() );
        clonedSchema.setDiscriminator( schema.getDiscriminator() );
        clonedSchema.setEnum( schema.getEnum() );
        clonedSchema.setExample( schema.getExample() );
        clonedSchema.setExclusiveMaximum( schema.getExclusiveMaximum() );
        clonedSchema.setExclusiveMinimum( schema.getExclusiveMinimum() );
        clonedSchema.setExtensions( schema.getExtensions() );
        clonedSchema.setExternalDocs( schema.getExternalDocs() );
        clonedSchema.setFormat( schema.getFormat() );
        clonedSchema.setMaximum( schema.getMaximum() );
        clonedSchema.setMaxItems( schema.getMaxItems() );
        clonedSchema.setMaxLength( schema.getMaxLength() );
        clonedSchema.setMaxProperties( schema.getMaxProperties() );
        clonedSchema.setMinProperties( schema.getMinProperties() );
        clonedSchema.setMultipleOf( schema.getMultipleOf() );
        clonedSchema.setName( schema.getName() );
        clonedSchema.setNot( schema.getNot() );
        clonedSchema.setNullable( schema.getNullable() );
        clonedSchema.setPattern( schema.getPattern() );
        clonedSchema.setProperties( schema.getProperties() );
        clonedSchema.setReadOnly( schema.getReadOnly() );
        clonedSchema.setRequired( schema.getRequired() );
        clonedSchema.setTitle( schema.getTitle() );
        clonedSchema.setType( schema.getType() );
        clonedSchema.setUniqueItems( schema.getUniqueItems() );
        clonedSchema.setXml( schema.getXml() );
        return clonedSchema;
    }

    private void removeDatasetParam( PathItem pathItem ) {
        if ( pathItem == null )
            return;
        pathItem.readOperationsMap().entrySet().forEach( method2operation -> {
            Operation operation = method2operation.getValue();
            Parameter datasetIdParameter = getDatasetIdParameter( operation );
            if ( datasetIdParameter != null )
                operation.getParameters().remove( datasetIdParameter );

        } );
    }

    private Parameter getDatasetIdParameter( Operation operation ) {
        if ( operation.getParameters() == null )
            return null;
        for ( Parameter parameter : operation.getParameters() ) {
            if ( DATASETID.equals( parameter.getName() ) ) {
                return parameter;
            }
        }
        return null;
    }

    private String replaceCollectionId( String toReplace, String value ) {
        if ( toReplace != null && value != null )
            return toReplace.replace( "{collectionId}", value );
        return toReplace;
    }

    private String mapToParameterType( FilterProperty filterProperty ) {
        BaseType type = filterProperty.getType();
        switch ( type ) {
        case DOUBLE:
        case DECIMAL:
            return "number";
        case INTEGER:
            return "integer";
        case BOOLEAN:
            return "boolean";
        case STRING:
        default:
            return "string";
        }
    }

    private String descriptionByParameterType( FilterProperty filterProperty ) {
        BaseType type = filterProperty.getType();
        switch ( type ) {
        case DOUBLE:
        case DECIMAL:
        case INTEGER:
            return "Use '<', '<=', '>' and '>=' for simple numeric comparison operations, e.g. 'param=<10'. Note: not working in OpenAPI 'Try it out' mode due to type validation.";
        case STRING:
            return "Use '*' as wildcard for search.";
        }
        return null;
    }

    private String mapToPropertyType( PropertyType propertyType ) {
        if ( propertyType instanceof SimplePropertyType ) {
            PrimitiveType primitiveType = ( (SimplePropertyType) propertyType ).getPrimitiveType();
            BaseType baseType = primitiveType.getBaseType();
            return mapToPropertyType( baseType );
        }
        return "object";
    }

    private String mapToPropertyType( XSSimpleTypeDefinition typeDef ) {
        try {
            BaseType baseType = BaseType.valueOf( typeDef );
            return mapToPropertyType( baseType );
        } catch ( IllegalArgumentException e ) {
            LOG.warn( "Could not find type", e );
            return "object";
        }
    }

    private String mapToPropertyType( BaseType baseType ) {
        switch ( baseType ) {
        case DOUBLE:
        case DECIMAL:
            return "number";
        case INTEGER:
            return "integer";
        case BOOLEAN:
            return "boolean";
        case DATE:
        case DATE_TIME:
        case TIME:
        case STRING:
            return "string";
        default:
            return "object";
        }
    }

    private boolean isMaxOccursGreaterThanOne( PropertyType propertyType ) {
        return propertyType.getMaxOccurs() == -1 || propertyType.getMaxOccurs() > 1;
    }

}
