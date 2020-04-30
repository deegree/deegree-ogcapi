package org.deegree.services.oaf.workspace.configuration;

import org.deegree.commons.tom.primitive.BaseType;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class FilterProperty {

    private QName name;

    private BaseType type;

    public FilterProperty( QName name, BaseType type ) {
        this.name = name;
        this.type = type;
    }

    public QName getName() {
        return name;
    }

    public BaseType getType() {
        return type;
    }
}