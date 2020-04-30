package org.deegree.services.oaf.resource;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static io.restassured.RestAssured.given;

import org.deegree.services.oaf.OgcApiFeaturesMediaType;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class OpenApiIT {

    // TODO
    @Ignore("paths '/' is missing")
    @Test
    public void test_PathsShouldBeAvailable() {
        given().
           accept( OgcApiFeaturesMediaType.APPLICATION_OPENAPI ).
         when().
           get("http://localhost:8002/api").
         then().
           assertThat().statusCode(200).
          and().
           body( hasJsonPath( "$.paths./" ) ).
          and().
           body( hasJsonPath( "$.paths./api" ) ).
          and().
           body( hasJsonPath( "$.paths./conformance" ) ).
          and().
           body( hasJsonPath( "$.paths./collections" ) ).
          and().
           body( hasJsonPath( "$.paths./collections/{collectionId}" ) ).
          and().
           body( hasJsonPath( "$.paths./collections/{collectionId}/items" ) ).
          and().
           body( hasJsonPath( "$.paths./collections/{collectionId}/items/{featureId}" ) );
    }

}
