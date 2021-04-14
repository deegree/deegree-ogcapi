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
import org.deegree.cs.coordinatesystems.ICRS;
import org.deegree.cs.exceptions.UnknownCRSException;
import org.deegree.cs.persistence.CRSManager;
import org.deegree.feature.persistence.query.Query;
import org.deegree.filter.Expression;
import org.deegree.filter.Filter;
import org.deegree.filter.IdFilter;
import org.deegree.filter.Operator;
import org.deegree.filter.OperatorFilter;
import org.deegree.filter.comparison.ComparisonOperator;
import org.deegree.filter.comparison.PropertyIsEqualTo;
import org.deegree.filter.comparison.PropertyIsGreaterThan;
import org.deegree.filter.comparison.PropertyIsGreaterThanOrEqualTo;
import org.deegree.filter.comparison.PropertyIsLessThan;
import org.deegree.filter.comparison.PropertyIsLessThanOrEqualTo;
import org.deegree.filter.comparison.PropertyIsLike;
import org.deegree.filter.comparison.PropertyIsNull;
import org.deegree.filter.expression.Literal;
import org.deegree.filter.expression.ValueReference;
import org.deegree.filter.logical.And;
import org.deegree.filter.logical.Or;
import org.deegree.filter.spatial.BBOX;
import org.deegree.filter.temporal.After;
import org.deegree.filter.temporal.Before;
import org.deegree.filter.temporal.TEquals;
import org.deegree.filter.temporal.TemporalOperator;
import org.deegree.geometry.Envelope;
import org.deegree.geometry.SimpleGeometryFactory;
import org.deegree.protocol.wfs.getfeature.TypeName;
import org.deegree.services.oaf.exceptions.InternalQueryException;
import org.deegree.services.oaf.exceptions.InvalidConfigurationException;
import org.deegree.services.oaf.exceptions.InvalidParameterValue;
import org.deegree.services.oaf.feature.FeaturesRequest;
import org.deegree.services.oaf.workspace.configuration.FeatureTypeMetadata;
import org.deegree.services.oaf.workspace.configuration.FilterProperty;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.deegree.filter.MatchAction.ALL;
import static org.deegree.filter.MatchAction.ANY;

/**
 * Creates WFS-Queries out of {@link FeaturesRequest} and featureIds.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class DeegreeQueryBuilder {

    public static final int UNLIMITED = -1;

    public static final int FIRST = 0;

    private static final SimpleGeometryFactory simpleGeometryFactory = new SimpleGeometryFactory();

    private static final String WILD_CARD = "*";

    private static final String SINGLE_CHAR = "_";

    private static final String ESCAPE_CHAR = "/";

    private final OafDatasetConfiguration oafConfiguration;

    DeegreeQueryBuilder( OafDatasetConfiguration oafConfiguration ) {
        this.oafConfiguration = oafConfiguration;
    }

    /**
     * Creates a {@link Query} out of a {@link FeaturesRequest}.
     *
     * @param featureTypeMetadata
     *                 the feature type metadata, never <code>null</code>
     * @param featuresRequest
     *                 the request to map, never <code>null</code>
     * @return the created {@link Query}, never <code>null</code>
     * @throws InternalQueryException
     *                 if the featuresRequest could not be mapped
     */
    public Query createQuery( FeatureTypeMetadata featureTypeMetadata, FeaturesRequest featuresRequest )
                    throws InternalQueryException, InvalidParameterValue, InvalidConfigurationException {
        QName name = featureTypeMetadata.getName();
        TypeName[] typeNames = { new TypeName( name, null ) };
        Filter filter = createFilter( featureTypeMetadata, featuresRequest );
        int limit = featuresRequest.isBulkUpload() ? UNLIMITED : featuresRequest.getLimit();
        int offset = featuresRequest.isBulkUpload() ? FIRST : featuresRequest.getOffset();
        return new Query( typeNames, filter, null, limit, offset );
    }

    /**
     * Creates a {@link Query} by id.
     *
     * @param featureTypeName
     *                 the name of the feature type, never <code>null</code>
     * @param featureId
     *                 the id of the feature, never <code>null</code>
     * @return the created {@link Query}, never <code>null</code>
     */
    public Query createQueryById( QName featureTypeName, String featureId ) {
        Filter idFilter = new IdFilter( featureId );
        return new Query( featureTypeName, idFilter, -1, 1, -1 );
    }

    private Filter createFilter( FeatureTypeMetadata featureTypeMetadata, FeaturesRequest featuresRequest )
                    throws InternalQueryException, InvalidParameterValue, InvalidConfigurationException {
        List<Operator> operators = new ArrayList<>();
        operators.add( createBboxOperator( featuresRequest ) );
        operators.add( createDatetimeOperator( featureTypeMetadata, featuresRequest ) );
        operators.addAll( createFilterOperator( featuresRequest ) );
        return createFilter( operators );
    }

    private Filter createFilter( List<Operator> operators ) {
        List<Operator> allOperators = operators.stream().filter( o -> o != null ).collect( Collectors.toList() );
        if ( allOperators.isEmpty() )
            return null;
        if ( allOperators.size() == 1 )
            return new OperatorFilter( allOperators.get( 0 ) );
        And and = new And( allOperators.toArray( new Operator[] {} ) );
        return new OperatorFilter( and );
    }

    private List<Operator> createFilterOperator( FeaturesRequest featuresRequest ) {
        List<Operator> filterOperators = new ArrayList<>();
        Map<FilterProperty, List<String>> filterRequestProperties = featuresRequest.getFilterRequestProperties();
        if ( filterRequestProperties != null ) {
            filterRequestProperties.forEach( ( filterProperty, values ) -> {
                values.forEach( value -> {
                    ComparisonOperator filterOperator = createFilterOperator( filterProperty, value );
                    filterOperators.add( filterOperator );
                } );
            } );
        }
        return filterOperators;
    }

    private ComparisonOperator createFilterOperator( FilterProperty filterProperty,
                                                     String value ) {
        ValueReference valueReference = new ValueReference( filterProperty.getName() );
        if ( value != null && value.startsWith( ">=" ) ) {
            Literal literal = new Literal( value.substring( 2 ) );
            return new PropertyIsGreaterThanOrEqualTo( valueReference, literal, true,
                                                       ANY );
        } else if ( value != null && value.startsWith( "<=" ) ) {
            Literal literal = new Literal( value.substring( 2 ) );
            return new PropertyIsLessThanOrEqualTo( valueReference, literal, true,
                                                    ANY );
        } else if ( value != null && value.startsWith( ">" ) ) {
            Literal literal = new Literal( value.substring( 1 ) );
            return new PropertyIsGreaterThan( valueReference, literal, true,
                                              ANY );
        } else if ( value != null && value.startsWith( "<" ) ) {
            Literal literal = new Literal( value.substring( 1 ) );
            return new PropertyIsLessThan( valueReference, literal, true,
                                           ANY );
        } else if ( value != null && value.contains( "*" ) ) {
            Literal literal = new Literal( value );
            return new PropertyIsLike( valueReference, literal, WILD_CARD, SINGLE_CHAR, ESCAPE_CHAR, false,
                                       ANY );
        }
        Literal literal = new Literal( value );
        return new PropertyIsEqualTo( valueReference, literal, false,
                                      ANY );
    }

    /**
     * Syntax:
     * <li>
     * <ul>
     * datetime = date-time
     * </ul>
     * <ul>
     * interval-closed = date-time "/" date-time
     * </ul>
     * <ul>
     * interval-open-start = [".."] "/" date-time
     * </ul>
     * <ul>
     * interval-open-end = date-time "/" [".."]
     * </ul>
     * </li>
     */
    private Operator createDatetimeOperator( FeatureTypeMetadata featureTypeMetadata, FeaturesRequest featuresRequest )
                    throws InvalidParameterValue, InvalidConfigurationException {
        String collectionId = featuresRequest.getCollectionId();
        String datetime = featuresRequest.getDatetime();
        if ( datetime == null || datetime.isEmpty() )
            return null;
        Expression dateProperty = getDateProperty( collectionId, featureTypeMetadata );
        if ( dateProperty == null ) {
            return null;
        }
        Operator emptyDatetime = new PropertyIsNull( dateProperty, ALL );
        Operator datetimeOperator = createDatetimeOperator( dateProperty, datetime );
        return new Or( datetimeOperator, emptyDatetime );
    }

    private Operator createDatetimeOperator( Expression dateProperty, String datetime )
                    throws InvalidParameterValue {
        DatetimeInterval datetimeInterval = new DatetimeInterval( datetime );
        if ( datetimeInterval.isDatetime() ) {
            Literal date = asLiteral( datetimeInterval.datetime );
            return new TEquals( date, dateProperty );
        } else {
            if ( datetimeInterval.isFromOpen() ) {
                Literal toDate = asLiteral( datetimeInterval.to );
                Before before = new Before( dateProperty, toDate );
                return orEqualTo( dateProperty, toDate, before );
            } else if ( datetimeInterval.isToOpen() ) {
                Literal fromDate = asLiteral( datetimeInterval.from );
                After after = new After( dateProperty, fromDate );
                return orEqualTo( dateProperty, fromDate, after );
            } else {
                Literal fromDate = asLiteral( datetimeInterval.from );
                After after = new After( dateProperty, fromDate );
                Operator orEqualToAfter = orEqualTo( dateProperty, fromDate, after );

                Literal toDate = asLiteral( datetimeInterval.to );
                Before before = new Before( dateProperty, toDate );
                Operator orEqualToBefore = orEqualTo( dateProperty, toDate, before );
                return new And( orEqualToAfter, orEqualToBefore );
            }
        }
    }

    private Operator orEqualTo( Expression dateProperty, Literal toDate, TemporalOperator other ) {
        TEquals equals = new TEquals( toDate, dateProperty );
        return new Or( other, equals );
    }

    private Expression getDateProperty( String collectionId, FeatureTypeMetadata featureTypeMetadata )
                    throws InvalidConfigurationException {
        QName dateProperty = featureTypeMetadata.getDateTimeProperty();
        if ( dateProperty != null )
            return new ValueReference( dateProperty );
        throw new InvalidConfigurationException( "No datetime property available for collection " + collectionId );
    }

    private Literal<TypedObjectNode> asLiteral( String datetime ) {
        return new Literal<>( datetime );
    }

    private Operator createBboxOperator( FeaturesRequest featuresRequest )
                    throws InternalQueryException {
        Envelope envelope = asEnvelope( featuresRequest );
        if ( envelope != null ) {
            return new BBOX( envelope );
        }
        return null;
    }

    private Envelope asEnvelope( FeaturesRequest featuresRequest )
                    throws InternalQueryException {
        List<Double> bbox = featuresRequest.getBbox();
        if ( bbox == null )
            return null;
        String crsName = featuresRequest.getBboxCrs();
        try {
            ICRS crs = CRSManager.lookup( crsName );
            return simpleGeometryFactory.createEnvelope( bbox.get( 0 ), bbox.get( 1 ), bbox.get( 2 ), bbox.get( 3 ),
                                                         crs );
        } catch ( UnknownCRSException e ) {
            throw new InternalQueryException( "Unsupported CRS: " + crsName );
        }
    }

    private class DatetimeInterval {

        private String datetime;

        private String from;

        private String to;

        private DatetimeInterval( String datetime )
                        throws InvalidParameterValue {
            if ( datetime.contains( "/" ) ) {
                if ( datetime.endsWith( "/" ) ) {
                    this.from = datetime.substring( 0, datetime.length() - 1 );
                } else if ( datetime.startsWith( "/" ) ) {
                    this.to = datetime.substring( 1 );
                } else {
                    String[] split = datetime.split( "/" );
                    if ( split.length != 2 ) {
                        throw new InvalidParameterValue( "datetime",
                                                         "Interval must have exact 2 datetimes or open start or end" );
                    }
                    this.from = split[0];
                    this.to = split[1];
                }
            } else {
                this.datetime = datetime;
            }
        }

        private boolean isDatetime() {
            return datetime != null;
        }

        private boolean isFromOpen() {
            return isOpen( from );
        }

        private boolean isToOpen() {
            return isOpen( to );
        }

        private boolean isOpen( String fromOrTo ) {
            return fromOrTo == null || fromOrTo.isEmpty() || "..".equals( fromOrTo );
        }

    }

}
