package org.deegree.services.oaf.workspace;

import org.deegree.ogcapi.config.resource.RestartOrUpdateHandler;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class DeegreeWorkspaceRestartOrUpdateHandler implements RestartOrUpdateHandler {

    @Override
    public void afterRestartOrUpdate() {
        DeegreeWorkspaceInitializer.reinitialize();
    }

}
