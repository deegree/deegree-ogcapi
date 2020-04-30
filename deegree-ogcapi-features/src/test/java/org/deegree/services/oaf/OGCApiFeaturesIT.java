package org.deegree.services.oaf;

import io.restassured.http.ContentType;
import org.junit.Ignore;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

public class OGCApiFeaturesIT {

    @Ignore("Webapp cannot be started without workspace")
    @Test
    public void test_ResponseHeaderForStatusCode_OpenApiVersionShouldBeCorrect() {
        given().
                when().
                get("http://localhost:8002/api.json").
                then().
                assertThat().
                statusCode(200).
                and().
                contentType(ContentType.JSON).
                and().
                body("openapi",equalTo("3.0.1"));
    }
}
