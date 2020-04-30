package org.deegree.services.oaf.feature;

import org.deegree.services.oaf.workspace.configuration.FilterProperty;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class FeaturesRequest {

    private final String collectionId;

    private final int limit;

    private final int offset;

    private final List<Double> bbox;

    private final String bboxCrs;

    private final String datetime;

    private final String responseCrs;

    private final Map<FilterProperty, List<String>> filterRequestProperties;

    public FeaturesRequest( String collectionId, int limit, int offset, List<Double> bbox, String bboxCrs,
                            String datetime, String responseCrs,
                            Map<FilterProperty, List<String>> filterRequestProperties ) {
        this.collectionId = collectionId;
        this.limit = limit;
        this.offset = offset;
        this.bbox = bbox;
        this.bboxCrs = bboxCrs;
        this.datetime = datetime;
        this.responseCrs = responseCrs;
        this.filterRequestProperties = filterRequestProperties;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public int getLimit() {
        return this.limit;
    }

    public int getOffset() {
        return this.offset;
    }

    public List<Double> getBbox() {
        return this.bbox;
    }

    public String getBboxCrs() {
        return this.bboxCrs;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getResponseCrs() {
        return responseCrs;
    }

    public Map<FilterProperty, List<String>> getFilterRequestProperties() {
        return filterRequestProperties;
    }
}