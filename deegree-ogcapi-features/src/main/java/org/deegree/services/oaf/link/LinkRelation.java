package org.deegree.services.oaf.link;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public enum LinkRelation {

    ALTERNATE( "alternate" ), SELF( "self" ), SERVICE_DESC( "service-desc" ), SERVICE_DOC( "service-doc" ), CONFORMANCE(
                    "conformance" ), DATA( "data" ), COLLECTION( "collection" ), ITEMS( "items" ), NEXT(
                    "next" ), DESCRIBEDBY( "describedBy" ), LICENSE( "license" );

    private final String rel;

    LinkRelation( String rel ) {
        this.rel = rel;
    }

    public String getRel() {
        return rel;
    }
}
