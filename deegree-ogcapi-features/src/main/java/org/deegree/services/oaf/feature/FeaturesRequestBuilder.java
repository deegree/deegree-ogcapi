package org.deegree.services.oaf.feature;

import org.deegree.cs.exceptions.UnknownCRSException;
import org.deegree.cs.persistence.CRSManager;
import org.deegree.services.oaf.exceptions.InvalidParameterValue;
import org.deegree.services.oaf.workspace.configuration.FilterProperty;

import java.util.List;
import java.util.Map;

import static org.deegree.services.oaf.OgcApiFeaturesConstants.DEFAULT_CRS;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class FeaturesRequestBuilder {

    private String collectionId;

    private int limit;

    private int offset;

    private List<Double> bbox;

    private String bboxCrs;

    private String datetime;

    private String responseCrs;

    private Map<FilterProperty, List<String>> filterRequestProperties;

    public FeaturesRequestBuilder( String collectionId ) {
        this.collectionId = collectionId;
    }

    public FeaturesRequestBuilder withLimit( int limit ) {
        if ( limit <= 0 )
            this.limit = 10;
        else if ( limit > 1000 )
            this.limit = 1000;
        else
            this.limit = limit;
        return this;
    }

    public FeaturesRequestBuilder withOffset( int offset ) {
        if ( offset <= 0 )
            this.offset = 0;
        else
            this.offset = offset;
        return this;
    }

    public FeaturesRequestBuilder withBbox( List<Double> bbox, String bboxCrs )
                    throws InvalidParameterValue {
        this.bbox = validateBbox( bbox );
        this.bboxCrs = validateAndRetrieveCrs( "bbox-crs", bboxCrs );
        return this;
    }

    public FeaturesRequestBuilder withDatetime( String datetime ) {
        this.datetime = datetime;
        return this;
    }

    public FeaturesRequestBuilder withResponseCrs( String responseCrs )
                    throws InvalidParameterValue {
        this.responseCrs = validateAndRetrieveCrs( "crs", responseCrs );
        return this;
    }

    public FeaturesRequestBuilder withFilterParameters( Map<FilterProperty, List<String>> filterRequestProperties ) {
        this.filterRequestProperties = filterRequestProperties;
        return this;
    }

    public FeaturesRequest build() {
        return new FeaturesRequest( this.collectionId, this.limit, this.offset, this.bbox, this.bboxCrs, this.datetime,
                                    this.responseCrs, this.filterRequestProperties );
    }

    private List<Double> validateBbox( List<Double> bbox )
                    throws InvalidParameterValue {
        if ( bbox == null || bbox.isEmpty() )
            return null;
        if ( bbox.size() != 4 )
            throw new InvalidParameterValue( "bbox", "does not have exact 4 numbers " );
        return bbox;
    }

    private String validateAndRetrieveCrs( String parameterName, String crs )
                    throws InvalidParameterValue {
        if ( crs == null || crs.isEmpty() )
            return DEFAULT_CRS;
        try {
            CRSManager.lookup( crs );
        } catch ( UnknownCRSException e ) {
            throw new InvalidParameterValue( parameterName, "Unknown CRS " + crs );
        }
        return crs;
    }
}
