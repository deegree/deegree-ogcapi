package org.deegree.services.oaf.converter;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Provider
public class DoubleListConverterProvider implements ParamConverterProvider {

    @Override
    public <T> ParamConverter<T> getConverter( Class<T> aClass, Type type, Annotation[] annotations ) {
        if ( isListWithDoubles( aClass, type ) ) {
            return (ParamConverter<T>) new DoubleListConverter();
        }
        return null;
    }

    private <T> boolean isListWithDoubles( Class<T> aClass, Type type ) {
        if ( List.class.isAssignableFrom( aClass ) && type instanceof ParameterizedType ) {
            Type[] actualTypeArguments = ( (ParameterizedType) type ).getActualTypeArguments();
            if ( actualTypeArguments.length == 1 ) {
                Type actualTypeArgument = actualTypeArguments[0];
                if ( actualTypeArgument instanceof Class && ( (Class) actualTypeArgument ).isAssignableFrom(
                                Double.class ) ) {
                    return true;
                }
            }
        }
        return false;
    }
}
