package org.deegree.services.oaf.io.response;

import org.deegree.feature.stream.EmptyFeatureInputStream;
import org.deegree.feature.stream.FeatureInputStream;
import org.deegree.services.oaf.OgcApiFeaturesConstants;
import org.deegree.services.oaf.link.Link;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GEOJSON_TYPE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class FeaturesResponseCreatorTest {

    @Test
    public void createJsonResponseWithHeaders() {
        FeaturesResponseCreator responseCreator = new FeaturesResponseCreator();
        FeatureResponse featureResponse = createFeatureResponse();
        Response response = responseCreator.createJsonResponseWithHeaders( featureResponse );

        assertThat( response.getStatus(), equalTo( 200 ) );
        assertThat( response.getMediaType(), equalTo( APPLICATION_GEOJSON_TYPE.withCharset( UTF_8.name() ) ) );
    }

   private FeatureResponse createFeatureResponse() {
        List<Link> links = Collections.singletonList(
                new Link( "http://self", "self", "application/json", "title" ) );
        FeatureInputStream featureStream = new EmptyFeatureInputStream();
        Map<String, String> featureTypeNsPrefixes = Collections.emptyMap();
        return new FeaturesResponseBuilder( featureStream ).withFeatureTypeNsPrefixes(
                featureTypeNsPrefixes ).withLinks( links ).withResponseCrsName(
                OgcApiFeaturesConstants.DEFAULT_CRS ).buildFeatureResponse();
    }
}