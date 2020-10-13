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
//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2010 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.ogcapi.config.actions;

import org.deegree.commons.config.DeegreeWorkspace;
import org.deegree.commons.utils.Pair;
import org.deegree.ogcapi.config.exceptions.RestartException;
import org.deegree.services.controller.OGCFrontController;
import org.deegree.workspace.ResourceIdentifier;
import org.deegree.workspace.Workspace;
import org.deegree.workspace.WorkspaceUtils;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * @version $Revision$, $Date$
 */
public class Restart {

    public static String restart( Pair<DeegreeWorkspace, String> p )
                    throws RestartException {
        try {
            DeegreeWorkspace workspace = p.first;
            if ( p.second == null ) {
                return restartWorkspace( workspace.getName() );
            }
            String resourcePath = p.second;
            return restartResource( workspace, resourcePath );
        } catch ( Exception e ) {
            throw new RestartException( e );
        }
    }

    private static String restartWorkspace( String workspaceName )
                    throws IOException, URISyntaxException, ServletException {
        OGCFrontController fc = OGCFrontController.getInstance();
        fc.setActiveWorkspaceName( workspaceName );
        fc.reload();
        return "Restart of workspace " + workspaceName + " completed.";
    }

    private static String restartResource( DeegreeWorkspace workspace, String path ) {
        List<String> initialisedIds = reinitializeChain( workspace, path );
        if ( initialisedIds.isEmpty() ) {
            return "Could not find a resource to restart in workspace " + workspace.getName();
        }
        StringBuilder sb = new StringBuilder();
        sb.append( "Restart of workspace " )
          .append( workspace.getName() )
          .append( " completed. Restarted resources:" );
        for ( String initialisedId : initialisedIds ) {
            sb.append( "\n" );
            sb.append( "   - " ).append( initialisedId );
        }
        return sb.toString();
    }

    private static List<String> reinitializeChain( DeegreeWorkspace workspace, String resourcePath ) {
        List<String> allInitialisedIds = new ArrayList<>();
        Workspace ws = workspace.getNewWorkspace();
        List<ResourceIdentifier<?>> ids = WorkspaceUtils.getPossibleIdentifiers( ws, resourcePath );
        for ( ResourceIdentifier<?> id : ids ) {
            List<String> initialisedIds = WorkspaceUtils.reinitializeChain( ws, id );
            allInitialisedIds.addAll( initialisedIds );
        }
        return allInitialisedIds;
    }

}
