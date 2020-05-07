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
import org.deegree.ogcapi.config.exceptions.DeleteException;
import org.deegree.ogcapi.config.exceptions.InvalidPathException;

import java.io.File;

import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.deegree.commons.config.DeegreeWorkspace.unregisterWorkspace;
import static org.deegree.services.config.actions.Utils.getWorkspaceAndPath;

/**
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * @version $Revision$, $Date$
 */
public class Delete {

    public static String delete( String path )
                    throws DeleteException, InvalidPathException {
        Pair<DeegreeWorkspace, String> p = getWorkspaceAndPath( path );

        if ( p.second == null ) {
            File dir = p.first.getLocation();
            if ( !deleteQuietly( dir ) ) {
                unregisterWorkspace( p.first.getName() );
                throw new DeleteException( "Workspace deletion unsuccessful." );
            }
            unregisterWorkspace( p.first.getName() );
            return "Workspace deleted.";
        }
        File fileOrDir = new File( p.first.getLocation(), p.second );
        if ( !fileOrDir.exists() ) {
            throw new InvalidPathException( p.first.getName(), fileOrDir.getName() );
        }
        if ( !deleteQuietly( fileOrDir ) ) {
            throw new DeleteException( "Deletion unsuccessful." );
        }
        return fileOrDir.getName() + " deleted.";
    }

}
