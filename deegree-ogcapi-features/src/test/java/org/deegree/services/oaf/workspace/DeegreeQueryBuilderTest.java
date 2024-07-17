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
package org.deegree.services.oaf.workspace;

import org.deegree.commons.tom.TypedObjectNode;
import org.deegree.commons.tom.primitive.BaseType;
import org.deegree.feature.persistence.query.Query;
import org.deegree.filter.Filter;
import org.deegree.filter.Operator;
import org.deegree.filter.OperatorFilter;
import org.deegree.filter.comparison.PropertyIsEqualTo;
import org.deegree.filter.comparison.PropertyIsGreaterThan;
import org.deegree.filter.comparison.PropertyIsGreaterThanOrEqualTo;
import org.deegree.filter.comparison.PropertyIsLessThan;
import org.deegree.filter.comparison.PropertyIsLessThanOrEqualTo;
import org.deegree.filter.comparison.PropertyIsLike;
import org.deegree.filter.expression.Literal;
import org.deegree.filter.logical.And;
import org.deegree.filter.logical.Or;
import org.deegree.services.oaf.exceptions.InvalidConfigurationException;
import org.deegree.services.oaf.io.request.FeaturesRequest;
import org.deegree.services.oaf.io.request.FeaturesRequestBuilder;
import org.deegree.services.oaf.workspace.configuration.FeatureTypeMetadata;
import org.deegree.services.oaf.workspace.configuration.FilterProperty;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.deegree.filter.Operator.Type.COMPARISON;
import static org.deegree.filter.Operator.Type.LOGICAL;
import static org.deegree.filter.Operator.Type.TEMPORAL;
import static org.deegree.services.oaf.OgcApiFeaturesConstants.DEFAULT_CRS;
import static org.deegree.services.oaf.workspace.DeegreeQueryBuilder.FIRST;
import static org.deegree.services.oaf.workspace.DeegreeQueryBuilder.UNLIMITED;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class DeegreeQueryBuilderTest {

    private static final QName FT_NAME = new QName( "test" );

    private static final QName DT_PROP_NAME = new QName( "datetime" );

    private static final FeatureTypeMetadata FT_METADATA = new FeatureTypeMetadata( FT_NAME ).dateTimeProperty(
                    DT_PROP_NAME );

    private static final FeatureTypeMetadata FT_METADATA_NODATETIME = new FeatureTypeMetadata( FT_NAME );

    private static final String COLLECTION_ID = "collectionid";

    @Test
    public void test_CreateQueryWithEmptyRequest()
                    throws Exception {
        DeegreeQueryBuilder deegreeQueryBuilder = new DeegreeQueryBuilder( mockOafConfiguration() );
        FeaturesRequest featureRequest = new FeaturesRequestBuilder( COLLECTION_ID ).build();
        Query query = deegreeQueryBuilder.createQuery( FT_METADATA, featureRequest );
        Filter filter = query.getFilter();

        assertThat( query.getTypeNames()[0].getFeatureTypeName(), is( FT_NAME ) );
        assertThat( filter, is( nullValue() ) );
    }

    @Test public void test_CreateQueryWithBulkOverridingLimitAndOffset()
                            throws Exception {
        DeegreeQueryBuilder deegreeQueryBuilder = new DeegreeQueryBuilder( mockOafConfiguration() );
        FeaturesRequest featureRequest = new FeaturesRequestBuilder( COLLECTION_ID ).withLimit( 10 ).withOffset(
                                10 ).withBulkUpload( true ).build();
        Query query = deegreeQueryBuilder.createQuery( FT_METADATA, featureRequest );

        assertThat( query.getMaxFeatures(), is( UNLIMITED ) );
        assertThat( query.getStartIndex(), is( FIRST ) );
    }

    @Test
    public void test_CreateQueryWithBBoxParameter()
                    throws Exception {
        DeegreeQueryBuilder deegreeQueryBuilder = new DeegreeQueryBuilder( mockOafConfiguration() );
        List<Double> bbox = createBbox();
        FeaturesRequest featureRequest = new FeaturesRequestBuilder( COLLECTION_ID ).withBbox( bbox, null ).build();
        Query query = deegreeQueryBuilder.createQuery( FT_METADATA, featureRequest );
        OperatorFilter filter = (OperatorFilter) query.getFilter();

        assertThat( query.getTypeNames()[0].getFeatureTypeName(), is( FT_NAME ) );
        assertThat( filter.getOperator().getType(), is( Operator.Type.SPATIAL ) );
    }

    @Test
    public void test_CreateQueryWithBBoxAndDatetimeParameter()
                    throws Exception {
        DeegreeQueryBuilder deegreeQueryBuilder = new DeegreeQueryBuilder( mockOafConfiguration() );
        List<Double> bbox = createBbox();
        String datetime = "2019-10-08T10:42:52Z";
        FeaturesRequest featureRequest = new FeaturesRequestBuilder( COLLECTION_ID ).withBbox( bbox,
                                                                                               DEFAULT_CRS ).withDatetime(
                        datetime ).build();
        Query query = deegreeQueryBuilder.createQuery( FT_METADATA, featureRequest );
        OperatorFilter filter = (OperatorFilter) query.getFilter();

        assertThat( query.getTypeNames()[0].getFeatureTypeName(), is( FT_NAME ) );
        assertThat( filter.getOperator().getType(), is( LOGICAL ) );
    }

    @Test
    public void test_CreateQueryWithDatetimeParameter_Datetime()
                    throws Exception {
        DeegreeQueryBuilder deegreeQueryBuilder = new DeegreeQueryBuilder( mockOafConfiguration() );
        String datetime = "2019-10-08T10:42:52Z";
        FeaturesRequest featureRequest = new FeaturesRequestBuilder( COLLECTION_ID ).withDatetime( datetime ).build();
        Query query = deegreeQueryBuilder.createQuery( FT_METADATA, featureRequest );
        OperatorFilter filter = (OperatorFilter) query.getFilter();

        assertThat( query.getTypeNames()[0].getFeatureTypeName(), is( FT_NAME ) );
        assertThat( filter.getOperator().getType(), is( LOGICAL ) );

        Operator firstLevelFirst = ( (Or) filter.getOperator() ).getParameter( 0 );
        assertThat( firstLevelFirst.getType(), is( TEMPORAL ) );

        Operator firstLevelSecond = ( (Or) filter.getOperator() ).getParameter( 1 );
        assertThat( firstLevelSecond.getType(), is( COMPARISON ) );
    }

    @Test
    public void test_CreateQueryWithDatetimeParameter_Interval()
                    throws Exception {
        DeegreeQueryBuilder deegreeQueryBuilder = new DeegreeQueryBuilder( mockOafConfiguration() );
        String datetime = "2019-10-08T10:42:52Z/2019-10-10T10:42:52Z";
        FeaturesRequest featureRequest = new FeaturesRequestBuilder( COLLECTION_ID ).withDatetime( datetime ).build();
        Query query = deegreeQueryBuilder.createQuery( FT_METADATA, featureRequest );
        OperatorFilter filter = (OperatorFilter) query.getFilter();

        assertThat( query.getTypeNames()[0].getFeatureTypeName(), is( FT_NAME ) );
        assertThat( filter.getOperator().getType(), is( LOGICAL ) );
    }

    @Test
    public void test_CreateQueryWithDatetimeParameter_IntervalOpenEnd()
                    throws Exception {
        DeegreeQueryBuilder deegreeQueryBuilder = new DeegreeQueryBuilder( mockOafConfiguration() );
        String datetime = "2019-10-08T10:42:52Z/";
        FeaturesRequest featureRequest = new FeaturesRequestBuilder( COLLECTION_ID ).withDatetime( datetime ).build();
        Query query = deegreeQueryBuilder.createQuery( FT_METADATA, featureRequest );
        OperatorFilter filter = (OperatorFilter) query.getFilter();

        assertThat( query.getTypeNames()[0].getFeatureTypeName(), is( FT_NAME ) );
        assertThat( filter.getOperator().getType(), is( LOGICAL ) );

        Operator firstLevelFirst = ( (Or) filter.getOperator() ).getParameter( 0 );
        assertThat( firstLevelFirst.getType(), is( LOGICAL ) );

        Operator secondLevelFirst = ( (Or) firstLevelFirst ).getParameter( 0 );
        assertThat( secondLevelFirst.getType(), is( TEMPORAL ) );
        Operator secondLevelSecond = ( (Or) firstLevelFirst ).getParameter( 1 );
        assertThat( secondLevelSecond.getType(), is( TEMPORAL ) );

        Operator firstLevelSecond = ( (Or) filter.getOperator() ).getParameter( 1 );
        assertThat( firstLevelSecond.getType(), is( COMPARISON ) );
    }

    @Test
    public void test_CreateQueryWithDatetimeParameter_IntervalOpenStart()
                    throws Exception {
        DeegreeQueryBuilder deegreeQueryBuilder = new DeegreeQueryBuilder( mockOafConfiguration() );
        String datetime = "../2019-10-10T10:42:52Z";
        FeaturesRequest featureRequest = new FeaturesRequestBuilder( COLLECTION_ID ).withDatetime( datetime ).build();
        Query query = deegreeQueryBuilder.createQuery( FT_METADATA, featureRequest );
        OperatorFilter filter = (OperatorFilter) query.getFilter();

        assertThat( query.getTypeNames()[0].getFeatureTypeName(), is( FT_NAME ) );
        assertThat( filter.getOperator().getType(), is( LOGICAL ) );

        Operator firstLevelFirst = ( (Or) filter.getOperator() ).getParameter( 0 );
        assertThat( firstLevelFirst.getType(), is( LOGICAL ) );

        Operator secondLevelFirst = ( (Or) firstLevelFirst ).getParameter( 0 );
        assertThat( secondLevelFirst.getType(), is( TEMPORAL ) );
        Operator secondLevelSecond = ( (Or) firstLevelFirst ).getParameter( 1 );
        assertThat( secondLevelSecond.getType(), is( TEMPORAL ) );

        Operator firstLevelSecond = ( (Or) filter.getOperator() ).getParameter( 1 );
        assertThat( firstLevelSecond.getType(), is( COMPARISON ) );
    }

    @Test(expected = InvalidConfigurationException.class)
    public void test_CreateQueryWithBBoxAndDatetimeParameter_NoDatetimeConfigured()
                    throws Exception {
        DeegreeQueryBuilder deegreeQueryBuilder = new DeegreeQueryBuilder( mockOafConfigurationWithoutDatetime() );
        String datetime = "2019-10-08T10:42:52Z";
        FeaturesRequest featureRequest = new FeaturesRequestBuilder( COLLECTION_ID ).withDatetime( datetime ).build();
        deegreeQueryBuilder.createQuery( FT_METADATA_NODATETIME, featureRequest );
    }

    @Test
    public void test_CreateQueryWithSingleFilter()
                    throws Exception {
        DeegreeQueryBuilder deegreeQueryBuilder = new DeegreeQueryBuilder( mockOafConfiguration() );
        Map<FilterProperty, List<String>> filterParameters = createSingleFilterParams( BaseType.STRING, "value" );
        FeaturesRequest featureRequest = new FeaturesRequestBuilder( COLLECTION_ID )
                        .withQueryableParameters( filterParameters ).build();
        Query query = deegreeQueryBuilder.createQuery( FT_METADATA, featureRequest );
        OperatorFilter filter = (OperatorFilter) query.getFilter();

        assertThat( query.getTypeNames()[0].getFeatureTypeName(), is( FT_NAME ) );
        assertThat( filter.getOperator(), is( CoreMatchers.instanceOf( PropertyIsEqualTo.class ) ) );
    }

    @Test
    public void test_CreateQueryWithSingleFilter_Wildcard()
                    throws Exception {
        DeegreeQueryBuilder deegreeQueryBuilder = new DeegreeQueryBuilder( mockOafConfiguration() );
        Map<FilterProperty, List<String>> filterParameters = createSingleFilterParams( BaseType.STRING, "value*" );
        FeaturesRequest featureRequest = new FeaturesRequestBuilder( COLLECTION_ID )
                        .withQueryableParameters( filterParameters ).build();
        Query query = deegreeQueryBuilder.createQuery( FT_METADATA, featureRequest );
        OperatorFilter filter = (OperatorFilter) query.getFilter();

        assertThat( query.getTypeNames()[0].getFeatureTypeName(), is( FT_NAME ) );
        assertThat( filter.getOperator(), is( CoreMatchers.instanceOf( PropertyIsLike.class ) ) );
    }

    @Test
    public void test_CreateQueryWithSingleFilter_IntegerEqualTo()
                    throws Exception {
        DeegreeQueryBuilder deegreeQueryBuilder = new DeegreeQueryBuilder( mockOafConfiguration() );
        Map<FilterProperty, List<String>> filterParameters = createSingleFilterParams( BaseType.INTEGER, "10" );
        FeaturesRequest featureRequest = new FeaturesRequestBuilder( COLLECTION_ID )
                        .withQueryableParameters( filterParameters ).build();
        Query query = deegreeQueryBuilder.createQuery( FT_METADATA, featureRequest );
        OperatorFilter filter = (OperatorFilter) query.getFilter();

        assertThat( query.getTypeNames()[0].getFeatureTypeName(), is( FT_NAME ) );
        assertThat( filter.getOperator(), is( CoreMatchers.instanceOf( PropertyIsEqualTo.class ) ) );
        TypedObjectNode valueParam2 = ( (Literal) ( (PropertyIsEqualTo) filter.getOperator() ).getParameter2() ).getValue();
        assertThat( valueParam2, is( 10.0 ) );
    }

    @Test
    public void test_CreateQueryWithSingleFilter_DecimalGreaterThan()
                    throws Exception {
        DeegreeQueryBuilder deegreeQueryBuilder = new DeegreeQueryBuilder( mockOafConfiguration() );
        Map<FilterProperty, List<String>> filterParameters = createSingleFilterParams( BaseType.DECIMAL, ">9.56" );
        FeaturesRequest featureRequest = new FeaturesRequestBuilder( COLLECTION_ID )
                        .withQueryableParameters( filterParameters ).build();
        Query query = deegreeQueryBuilder.createQuery( FT_METADATA, featureRequest );
        OperatorFilter filter = (OperatorFilter) query.getFilter();

        assertThat( query.getTypeNames()[0].getFeatureTypeName(), is( FT_NAME ) );
        assertThat( filter.getOperator(), is( CoreMatchers.instanceOf( PropertyIsGreaterThan.class ) ) );
        TypedObjectNode valueParam2 = ( (Literal) ( (PropertyIsGreaterThan) filter.getOperator() ).getParameter2() ).getValue();
        assertThat( valueParam2, is( 9.56 ) );
    }

    @Test
    public void test_CreateQueryWithSingleFilter_DoubleLessThan()
                    throws Exception {
        DeegreeQueryBuilder deegreeQueryBuilder = new DeegreeQueryBuilder( mockOafConfiguration() );
        Map<FilterProperty, List<String>> filterParameters = createSingleFilterParams( BaseType.DOUBLE, "<5.89" );
        FeaturesRequest featureRequest = new FeaturesRequestBuilder( COLLECTION_ID )
                        .withQueryableParameters( filterParameters ).build();
        Query query = deegreeQueryBuilder.createQuery( FT_METADATA, featureRequest );
        OperatorFilter filter = (OperatorFilter) query.getFilter();

        assertThat( query.getTypeNames()[0].getFeatureTypeName(), is( FT_NAME ) );
        assertThat( filter.getOperator(), is( CoreMatchers.instanceOf( PropertyIsLessThan.class ) ) );
        TypedObjectNode valueParam2 = ( (Literal) ( (PropertyIsLessThan) filter.getOperator() ).getParameter2() ).getValue();
        assertThat( valueParam2, is( 5.89 ) );
    }

    @Test
    public void test_CreateQueryWithSingleFilter_IntegerGreaterThanOrEqualTo()
                    throws Exception {
        DeegreeQueryBuilder deegreeQueryBuilder = new DeegreeQueryBuilder( mockOafConfiguration() );
        Map<FilterProperty, List<String>> filterParameters = createSingleFilterParams( BaseType.INTEGER, ">=10" );
        FeaturesRequest featureRequest = new FeaturesRequestBuilder( COLLECTION_ID )
                        .withQueryableParameters( filterParameters ).build();
        Query query = deegreeQueryBuilder.createQuery( FT_METADATA, featureRequest );
        OperatorFilter filter = (OperatorFilter) query.getFilter();

        assertThat( query.getTypeNames()[0].getFeatureTypeName(), is( FT_NAME ) );
        assertThat( filter.getOperator(), is( CoreMatchers.instanceOf( PropertyIsGreaterThanOrEqualTo.class ) ) );
        TypedObjectNode valueParam2 = ( (Literal) ( (PropertyIsGreaterThanOrEqualTo) filter.getOperator() ).getParameter2() ).getValue();
        assertThat( valueParam2, is( 10.0 ) );
    }

    @Test
    public void test_CreateQueryWithSingleFilter_IntegerLessThanOrEqualTo()
                    throws Exception {
        DeegreeQueryBuilder deegreeQueryBuilder = new DeegreeQueryBuilder( mockOafConfiguration() );
        Map<FilterProperty, List<String>> filterParameters = createSingleFilterParams( BaseType.INTEGER, "<=10" );
        FeaturesRequest featureRequest = new FeaturesRequestBuilder( COLLECTION_ID )
                        .withQueryableParameters( filterParameters ).build();
        Query query = deegreeQueryBuilder.createQuery( FT_METADATA, featureRequest );
        OperatorFilter filter = (OperatorFilter) query.getFilter();

        assertThat( query.getTypeNames()[0].getFeatureTypeName(), is( FT_NAME ) );
        assertThat( filter.getOperator(), is( CoreMatchers.instanceOf( PropertyIsLessThanOrEqualTo.class ) ) );
        TypedObjectNode valueParam2 = ( (Literal) ( (PropertyIsLessThanOrEqualTo) filter.getOperator() ).getParameter2() ).getValue();
        assertThat( valueParam2, is( 10.0 ) );
    }

    @Test
    public void test_CreateQueryWithSingleFilterMultipleValues()
                    throws Exception {
        DeegreeQueryBuilder deegreeQueryBuilder = new DeegreeQueryBuilder( mockOafConfiguration() );
        Map<FilterProperty, List<String>> filterParameters = new MultivaluedHashMap<>();
        filterParameters.put( new FilterProperty( new QName( "http://deegree.org/oaf", "name" ), BaseType.STRING ),
                              Arrays.asList( "value1", "value2" ) );
        FeaturesRequest featureRequest = new FeaturesRequestBuilder( COLLECTION_ID ).withQueryableParameters(
                        filterParameters ).build();
        Query query = deegreeQueryBuilder.createQuery( FT_METADATA, featureRequest );
        OperatorFilter filter = (OperatorFilter) query.getFilter();

        assertThat( query.getTypeNames()[0].getFeatureTypeName(), is( FT_NAME ) );
        assertThat( filter.getOperator().getType(), is( LOGICAL ) );

        Operator first = ( (And) filter.getOperator() ).getParameter( 0 );
        assertThat( first, is( CoreMatchers.instanceOf( PropertyIsEqualTo.class ) ) );

        Operator second = ( (And) filter.getOperator() ).getParameter( 1 );
        assertThat( second, is( CoreMatchers.instanceOf( PropertyIsEqualTo.class ) ) );
    }

    @Test
    public void test_CreateQueryWithMultipleFilter()
                    throws Exception {
        DeegreeQueryBuilder deegreeQueryBuilder = new DeegreeQueryBuilder( mockOafConfiguration() );
        Map<FilterProperty, List<String>> filterParameters = createSingleFilterParams( BaseType.STRING, "value" );
        filterParameters.put( new FilterProperty( new QName( "http://deegree.org/oaf", "age" ), BaseType.INTEGER ),
                              Collections.singletonList( "15" ) );
        FeaturesRequest featureRequest = new FeaturesRequestBuilder( COLLECTION_ID ).withQueryableParameters(
                        filterParameters ).build();
        Query query = deegreeQueryBuilder.createQuery( FT_METADATA, featureRequest );
        OperatorFilter filter = (OperatorFilter) query.getFilter();

        assertThat( query.getTypeNames()[0].getFeatureTypeName(), is( FT_NAME ) );
        assertThat( filter.getOperator().getType(), is( LOGICAL ) );

        Operator first = ( (And) filter.getOperator() ).getParameter( 0 );
        assertThat( first, is( CoreMatchers.instanceOf( PropertyIsEqualTo.class ) ) );

        Operator second = ( (And) filter.getOperator() ).getParameter( 1 );
        assertThat( second, is( CoreMatchers.instanceOf( PropertyIsEqualTo.class ) ) );
    }

    private Map<FilterProperty, List<String>> createSingleFilterParams( BaseType type, String value ) {
        Map<FilterProperty, List<String>> filterParameters = new MultivaluedHashMap<>();
        QName name = new QName( "http://deegree.org/oaf", "name" );
        FilterProperty filterProperty = new FilterProperty( name, type );
        filterParameters.put( filterProperty, Collections.singletonList( value ) );
        return filterParameters;
    }

    private OafDatasetConfiguration mockOafConfiguration() {
        OafDatasetConfiguration mock = mock( OafDatasetConfiguration.class );
        Map<String, FeatureTypeMetadata> ftNames = Collections.singletonMap( FT_NAME.getLocalPart(), FT_METADATA );
        doReturn( ftNames ).when( mock ).getFeatureTypeMetadata();
        return mock;
    }

    private OafDatasetConfiguration mockOafConfigurationWithoutDatetime() {
        OafDatasetConfiguration mock = mock( OafDatasetConfiguration.class );
        Map<String, FeatureTypeMetadata> ftNames = Collections.singletonMap( FT_NAME.getLocalPart(),
                                                                             FT_METADATA_NODATETIME );
        doReturn( ftNames ).when( mock ).getFeatureTypeMetadata();
        return mock;
    }

    private List<Double> createBbox() {
        List<Double> bbox = new ArrayList<>();
        bbox.add( 14.0 );
        bbox.add( 15.0 );
        bbox.add( 52.0 );
        bbox.add( 53.0 );
        return bbox;
    }

}
