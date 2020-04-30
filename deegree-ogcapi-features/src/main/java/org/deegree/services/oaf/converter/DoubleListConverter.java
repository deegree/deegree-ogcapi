package org.deegree.services.oaf.converter;

import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.ext.ParamConverter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class DoubleListConverter implements ParamConverter<List<Double>> {

    @Override
    public List<Double> fromString( final String value ) {
        if ( StringUtils.isBlank( value ) )
            return null;
        return Stream.of( value.split( "," ) ).map( Double::new ).collect( Collectors.toList() );
    }

    @Override
    public String toString( final List<Double> value ) {
        if ( value == null || value.isEmpty() )
            return null;
        return value.stream().map( o -> Double.toString( o ) ).map( Object::toString ).collect(
                        Collectors.joining( "," ) );
    }
}
