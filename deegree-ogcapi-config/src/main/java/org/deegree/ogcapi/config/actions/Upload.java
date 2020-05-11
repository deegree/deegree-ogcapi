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

import org.apache.commons.io.FilenameUtils;
import org.deegree.commons.config.DeegreeWorkspace;
import org.deegree.commons.utils.Pair;
import org.deegree.ogcapi.config.exceptions.UploadException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.deegree.commons.config.DeegreeWorkspace.getWorkspaceRoot;
import static org.deegree.commons.config.DeegreeWorkspace.isWorkspace;
import static org.deegree.commons.utils.io.Zip.unzip;
import static org.deegree.services.config.actions.Utils.getWorkspaceAndPath;

/**
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * @version $Revision$, $Date$
 */
public class Upload {

    public static String upload( Pair<DeegreeWorkspace, String> p, HttpServletRequest req )
                    throws IOException, UploadException {
        if ( p.second == null ) {
            throw new UploadException( "No file name given." );
        }

        boolean isZip = p.second.endsWith( ".zip" ) || req.getContentType() != null
                                                       && req.getContentType().equals( "application/zip" );

        ServletInputStream in = null;
        try {
            in = req.getInputStream();
            if ( isZip ) {
                // unzip a workspace
                String wsName = p.second.substring( 0, p.second.length() - 4 );
                String dirName = p.second.endsWith( ".zip" ) ? wsName : p.second;
                File workspaceRoot = new File( getWorkspaceRoot() );
                File dir = new File( workspaceRoot, dirName );
                if ( !FilenameUtils.directoryContains( workspaceRoot.getCanonicalPath(), dir.getCanonicalPath() ) ) {
                    throw new UploadException( "Workspace " + wsName + " invalid." );
                } else if ( isWorkspace( dirName ) ) {
                    throw new UploadException( "Workspace " + wsName + " exists." );
                }
                unzip( in, dir );
                return "Workspace " + wsName + " uploaded.";
            } else {
                File workspaceDir = p.first.getLocation();
                File dest = new File( workspaceDir, p.second );
                if ( !FilenameUtils.directoryContains( workspaceDir.getCanonicalPath(), dest.getCanonicalPath() ) ) {
                    throw new UploadException( "Unable to upload file: " + p.second + "." );
                }
                if ( !dest.getParentFile().exists() && !dest.getParentFile().mkdirs() ) {
                    throw new UploadException( "Unable to create parent directory for upload." );
                }
                copyInputStreamToFile( in, dest );
                return dest.getName() + " uploaded.";
            }
        } finally {
            closeQuietly( in );
        }
    }

}