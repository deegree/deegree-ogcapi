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
import org.deegree.ogcapi.config.exceptions.DeleteException;
import org.deegree.ogcapi.config.exceptions.InvalidPathException;
import org.deegree.services.controller.OGCFrontController;

import java.io.File;

import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.deegree.commons.config.DeegreeWorkspace.unregisterWorkspace;

/**
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * @version $Revision$, $Date$
 */
public class Delete {

    public static String delete()
                    throws DeleteException {
        DeegreeWorkspace workspace = OGCFrontController.getServiceWorkspace();
        File dir = workspace.getLocation();
        if ( !deleteQuietly( dir ) ) {
            unregisterWorkspace( workspace.getName() );
            throw new DeleteException( "Workspace deletion unsuccessful." );
        }
        unregisterWorkspace( workspace.getName() );
        return "Workspace deleted.";
    }

    public static String delete( String path )
                    throws DeleteException, InvalidPathException {
        DeegreeWorkspace workspace = OGCFrontController.getServiceWorkspace();
        File fileOrDir = new File( workspace.getLocation(), path );
        if ( !fileOrDir.exists() ) {
            throw new InvalidPathException( workspace.getName(), fileOrDir.getName() );
        }
        if ( !deleteQuietly( fileOrDir ) ) {
            throw new DeleteException( "Deletion unsuccessful." );
        }
        return fileOrDir.getName() + " deleted.";
    }

}
