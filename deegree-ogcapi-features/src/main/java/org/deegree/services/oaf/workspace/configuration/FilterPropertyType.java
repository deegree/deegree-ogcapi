package org.deegree.services.oaf.workspace.configuration;

import org.deegree.commons.tom.primitive.BaseType;

/**
 * Enhances {@link org.deegree.commons.tom.primitive.BaseType} by GEOMETRY
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public enum FilterPropertyType {

	STRING, BOOLEAN, DECIMAL, DOUBLE, INTEGER, DATE, DATE_TIME, TIME, GEOMETRY;

	/**
	 * @param baseType may be <code>null</code> (returns STRING)
	 * @return FilterPropertyType derived from BaseType, fallback to STRING
	 */
	public static FilterPropertyType fromBaseType(BaseType baseType) {
		try {
			return FilterPropertyType.valueOf(baseType.name());
		}
		catch (IllegalArgumentException e) {
			return STRING;
		}
	}

}
