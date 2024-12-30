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

import org.apache.commons.io.FilenameUtils;
import org.deegree.commons.config.DeegreeWorkspace;
import org.deegree.ogcapi.config.exceptions.UploadException;
import org.deegree.services.controller.OGCFrontController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;
import static org.apache.commons.io.IOUtils.closeQuietly;

/**
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * @version $Revision$, $Date$
 */
public class Upload {

	public static String upload(String path, HttpServletRequest req) throws IOException, UploadException {
		ServletInputStream in = null;
		try {
			in = req.getInputStream();
			DeegreeWorkspace workspace = OGCFrontController.getServiceWorkspace();
			File workspaceDir = workspace.getLocation();
			File dest = new File(workspaceDir, path);
			if (!FilenameUtils.directoryContains(workspaceDir.getCanonicalPath(), dest.getCanonicalPath())) {
				throw new UploadException("Unable to upload file: " + path + ".");
			}
			if (!dest.getParentFile().exists() && !dest.getParentFile().mkdirs()) {
				throw new UploadException("Unable to create parent directory for upload.");
			}
			copyInputStreamToFile(in, dest);
			return dest.getName() + " uploaded.";
		}
		finally {
			closeQuietly(in);
		}
	}

}
