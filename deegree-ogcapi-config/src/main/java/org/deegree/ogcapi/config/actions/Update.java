package org.deegree.ogcapi.config.actions;

import org.deegree.commons.config.DeegreeWorkspace;
import org.deegree.commons.utils.Pair;
import org.deegree.ogcapi.config.exceptions.UpdateException;
import org.deegree.services.controller.OGCFrontController;
import org.deegree.workspace.ResourceIdentifier;
import org.deegree.workspace.Workspace;
import org.deegree.workspace.WorkspaceUtils;

import java.util.List;

import static org.deegree.services.config.actions.Utils.getWorkspaceAndPath;

/**
 * @author <a href="mailto:markus@beefcafe.de">Markus Schneider</a>
 * @author last edited by: $Author$
 * @version $Revision$, $Date$
 */
public class Update {

    public static String update( String path )
                    throws UpdateException {
        Pair<DeegreeWorkspace, String> p = getWorkspaceAndPath( path );
        try {
            if ( p.second != null ) {
                Workspace ws = p.first.getNewWorkspace();
                List<ResourceIdentifier<?>> ids = WorkspaceUtils.getPossibleIdentifiers( ws, p.second );
                for ( ResourceIdentifier<?> id : ids ) {
                    WorkspaceUtils.reinitializeChain( ws, id );
                }
            } else {
                OGCFrontController fc = OGCFrontController.getInstance();
                fc.setActiveWorkspaceName( p.first.getName() );
                fc.update();
            }
            return "Update complete.";
        } catch ( Exception e ) {
            throw new UpdateException( e );
        }

    }

}
