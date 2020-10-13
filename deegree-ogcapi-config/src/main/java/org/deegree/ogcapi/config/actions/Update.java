/*-
 * #%L
 * deegree-ogcapi-config - OGC API Config implementation
 * %%
 * Copyright (C) 2019 - 2020 lat/lon GmbH, info@lat-lon.de, www.lat-lon.de
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package org.deegree.ogcapi.config.actions;

import org.deegree.commons.config.DeegreeWorkspace;
import org.deegree.commons.utils.Pair;
import org.deegree.ogcapi.config.exceptions.UpdateException;
import org.deegree.services.controller.OGCFrontController;
import org.deegree.workspace.ResourceIdentifier;
import org.deegree.workspace.Workspace;
import org.deegree.workspace.WorkspaceUtils;

import java.util.List;

/**
 * @author <a href="mailto:markus@beefcafe.de">Markus Schneider</a>
 * @author last edited by: $Author$
 * @version $Revision$, $Date$
 */
public class Update {

    public static String update( Pair<DeegreeWorkspace, String> p )
                    throws UpdateException {
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
