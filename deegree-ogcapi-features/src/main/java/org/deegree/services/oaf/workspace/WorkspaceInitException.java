package org.deegree.services.oaf.workspace;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class WorkspaceInitException extends RuntimeException {

    public WorkspaceInitException( String msg ) {
        super( msg );
    }

    public WorkspaceInitException( Exception e ) {
        super( e );
    }

}