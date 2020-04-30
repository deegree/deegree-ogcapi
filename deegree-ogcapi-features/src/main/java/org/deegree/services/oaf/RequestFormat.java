package org.deegree.services.oaf;

import org.deegree.services.oaf.exceptions.InvalidParameterValue;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public enum RequestFormat {

    JSON, XML, HTML;

    public static RequestFormat byFormatParameter( String format, RequestFormat defaultValue )
                    throws InvalidParameterValue {
        if ( format == null || format.isEmpty() )
            return defaultValue;
        for ( RequestFormat requestFormat : values() )
            if ( requestFormat.name().equalsIgnoreCase( format ) )
                return requestFormat;
        throw new InvalidParameterValue( "f", "Supported values are: JSON, HTML" );
    }

}