package org.deegree.services.oaf.workspace.configuration;

import org.deegree.commons.ows.metadata.MetadataUrl;
import org.deegree.feature.types.FeatureType;
import org.deegree.services.oaf.domain.collections.Extent;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class FeatureTypeMetadata {

    private QName name;

    private QName dateTimeProperty;

    private Extent extent;

    private String title;

    private String description;

    private List<MetadataUrl> metadataUrls;

    private List<FilterProperty> filterProperties;

    private FeatureType featureType;

    public FeatureTypeMetadata( QName featureTypeName ) {
        this.name = featureTypeName;
    }

    public FeatureTypeMetadata dateTimeProperty( QName dateTimeProperty ) {
        this.dateTimeProperty = dateTimeProperty;
        return this;
    }

    public FeatureTypeMetadata extent( Extent extent ) {
        this.extent = extent;
        return this;
    }

    public FeatureTypeMetadata title( String title ) {
        this.title = title;
        return this;
    }

    public FeatureTypeMetadata description( String description ) {
        this.description = description;
        return this;
    }

    public FeatureTypeMetadata metadataUrls( List<MetadataUrl> metadataUrls ) {
        this.metadataUrls = metadataUrls;
        return this;

    }

    public FeatureTypeMetadata filterProperties( List<FilterProperty> filterProperties ) {
        this.filterProperties = filterProperties;
        return this;
    }

    public FeatureTypeMetadata featureType( FeatureType featureType ) {
        this.featureType = featureType;
        return this;
    }

    public QName getName() {
        return name;
    }

    public Extent getExtent() {
        return extent;
    }

    public QName getDateTimeProperty() {
        return dateTimeProperty;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<MetadataUrl> getMetadataUrls() {
        return metadataUrls;
    }

    public List<FilterProperty> getFilterProperties() {
        return filterProperties;
    }

    public FeatureType getFeatureType() {
        return featureType;
    }
}