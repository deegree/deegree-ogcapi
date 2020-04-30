package org.deegree.services.oaf.openapi;

import io.swagger.v3.core.filter.AbstractSpecFilter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.servers.Server;
import org.deegree.commons.tom.primitive.BaseType;
import org.deegree.services.oaf.exceptions.UnknownDatasetId;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.deegree.services.oaf.workspace.configuration.FeatureTypeMetadata;
import org.deegree.services.oaf.workspace.configuration.FilterProperty;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;
import org.deegree.services.oaf.workspace.configuration.OafDatasets;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class OafOpenApiFilter extends AbstractSpecFilter {

    private static final Logger LOG = getLogger( OafOpenApiFilter.class );

    private static final String COLLECTIONID = "collectionId";

    private static final String DATASETID = "datasetId";

    private static final String DATASETS_PATH = "/datasets";

    public static final String DATASET_PATH = "/datasets/{datasetId}";

    public static final String LANDINGPAGE_PATH = "/datasets/{datasetId}";

    public static final String API_PATH = "/datasets/{datasetId}/api";

    public static final String CONFORMANCE_PATH = "/datasets/{datasetId}/conformance";

    public static final String LICENSE_PATH = "/datasets/{datasetId}/license";

    private static final String COLLECTION_PATH = "/datasets/{datasetId}/collections/{collectionId}";

    private static final String FEATURES_PATH = "/datasets/{datasetId}/collections/{collectionId}/items";

    private static final String FEATURE_PATH = "/datasets/{datasetId}/collections/{collectionId}/items/{featureId}";

    private final String datasetId;

    private final OafDatasetConfiguration datasetConfiguration;

    public OafOpenApiFilter( String datasetId )
                    throws UnknownDatasetId {
        this.datasetId = datasetId;

        OafDatasets oafDatasets = DeegreeWorkspaceInitializer.getOafDatasets();
        this.datasetConfiguration = oafDatasets.getDataset( datasetId );
    }

    @Override
    public Optional<OpenAPI> filterOpenAPI( OpenAPI openAPI, Map<String, List<String>> params,
                                            Map<String, String> cookies, Map<String, List<String>> headers ) {
        filterServers( openAPI );
        filterPaths( openAPI );
        return super.filterOpenAPI( openAPI, params, cookies, headers );
    }

    private void filterServers( OpenAPI openAPI ) {
        if ( openAPI.getServers() == null || openAPI.getServers().isEmpty() )
            openAPI.addServersItem( new Server() );
        Server server = openAPI.getServers().get( 0 );
        StringBuilder url = new StringBuilder();
        if ( server.getUrl() != null )
            url.append( server.getUrl() );
        if ( server.getUrl() == null || !server.getUrl().endsWith( "/" ) )
            url.append( "/" );
        url.append( "datasets/" ).append( datasetId );
        server.setUrl( url.toString() );
    }

    private void filterPaths( OpenAPI openAPI ) {
        Paths paths = openAPI.getPaths();
        PathItem collection = paths.get( COLLECTION_PATH );
        PathItem features = paths.get( FEATURES_PATH );
        PathItem feature = paths.get( FEATURE_PATH );

        Map<String, FeatureTypeMetadata> featureTypeMetadatas = datasetConfiguration.getFeatureTypeMetadata();
        featureTypeMetadatas.entrySet().forEach( featureTypeMetadata -> {
            String key = featureTypeMetadata.getKey();
            FeatureTypeMetadata metadata = featureTypeMetadata.getValue();
            PathItem newCollectionPathItem = createNewPathItem( collection, key, metadata );
            paths.addPathItem( replaceCollectionId( COLLECTION_PATH, key ), newCollectionPathItem );

            PathItem newFeaturesPathItem = createNewPathItem( features, key, metadata );
            addFilterProperties( newFeaturesPathItem, metadata.getFilterProperties() );
            paths.addPathItem( replaceCollectionId( FEATURES_PATH, key ), newFeaturesPathItem );

            PathItem newFeaturePathItem = createNewPathItem( feature, key, metadata );
            paths.addPathItem( replaceCollectionId( FEATURE_PATH, key ), newFeaturePathItem );


        } );

        paths.remove( DATASETS_PATH );
        paths.remove( COLLECTION_PATH );
        paths.remove( FEATURES_PATH );
        paths.remove( FEATURE_PATH );

        Map<String, PathItem> pathItemsWithAdaptedPath = new HashMap<>(  );
        paths.entrySet().forEach( stringPathItemEntry -> {
            String replacedPath = createNewPath( stringPathItemEntry );
            PathItem value = addHtmlResource( stringPathItemEntry );
            removeDatasetParam( value );
            pathItemsWithAdaptedPath.put( replacedPath, value );
        } );
        paths.clear();
        paths.putAll( pathItemsWithAdaptedPath );
    }

    private PathItem addHtmlResource( Map.Entry<String, PathItem> stringPathItemEntry ) {
        PathItem value = stringPathItemEntry.getValue();
        if ( LICENSE_PATH.equals( stringPathItemEntry.getKey() ) )
            return value;
        Content content = value.getGet().getResponses().getDefault().getContent();
        MediaType mediaTypeHtml = new MediaType().schema( new Schema() );
        content.addMediaType( TEXT_HTML, mediaTypeHtml );
        return value;
    }

    private String createNewPath( Map.Entry<String, PathItem> stringPathItemEntry ) {
        String path = stringPathItemEntry.getKey();
        String replacedPath = path.replace( DATASET_PATH, "" );
        if ( replacedPath.isEmpty() )
            replacedPath = "/";
        return replacedPath;
    }

    private void addFilterProperties( PathItem pathItem, List<FilterProperty> filterProperties ) {
        if ( filterProperties == null )
            return;
        List<Parameter> parameters = pathItem.getGet().getParameters();
        filterProperties.forEach( filterProperty -> {
            Parameter filterParameter = new Parameter().name( filterProperty.getName().getLocalPart() ).in(
                            "query" ).style( Parameter.StyleEnum.FORM ).explode( false ).schema(
                            new Schema().type( mapToAllowedType( filterProperty ) ) );
            parameters.add( filterParameter );
        } );
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
        clonedOperation.setResponses( operation.getResponses() );
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

    private void removeDatasetParam( PathItem pathItem ) {
        if ( pathItem == null )
            return;
        ;
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

    private String mapToAllowedType( FilterProperty filterProperty ) {
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

}