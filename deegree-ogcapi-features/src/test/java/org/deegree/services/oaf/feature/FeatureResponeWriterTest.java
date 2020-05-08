package org.deegree.services.oaf.feature;

import org.deegree.feature.Feature;
import org.deegree.feature.FeatureCollection;
import org.deegree.feature.GenericFeatureCollection;
import org.deegree.feature.stream.EmptyFeatureInputStream;
import org.deegree.feature.stream.FeatureInputStream;
import org.deegree.services.oaf.link.Link;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasNoJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class FeatureResponeWriterTest {

    @Test
    public void testWriteTo_EmptyFeatureResponse() {
        FeatureResponeWriter featureResponeWriter = new FeatureResponeWriter();
        FeatureResponse featureResponse = createFeatureResponse();
        OutputStream bos = new ByteArrayOutputStream();
        featureResponeWriter.writeTo( featureResponse, null, null, null, null, null, bos );

        String json = bos.toString();

        assertThat( json, isJson() );
        assertThat( json, hasJsonPath( "$.type", equalTo( "FeatureCollection" ) ) );
        assertThat( json, hasNoJsonPath( "$.features" ) );
        assertThat( json, hasJsonPath( "$.links", Matchers.hasSize( 1 ) ) );
        assertThat( json, hasNoJsonPath( "$.crs" ) );
    }

    private FeatureResponse createFeatureResponse() {
        List<Link> links = java.util.Collections.singletonList(
                        new Link( "http://self", "self", "application/json", "title" ) );
        Collection<Feature> features = new ArrayList<>();
        final FeatureCollection featureCollection = new GenericFeatureCollection( "COL_1", features );
        FeatureInputStream featureStream = new EmptyFeatureInputStream();
        Map<String, String> featureTypeNsPrefixes = Collections.emptyMap();
        return new FeatureResponse( featureStream, featureTypeNsPrefixes, 10, 100, 0, links, false, null );
    }

}