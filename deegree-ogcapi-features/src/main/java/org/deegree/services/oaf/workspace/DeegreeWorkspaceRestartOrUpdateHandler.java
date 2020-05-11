package org.deegree.services.oaf.workspace;

import org.deegree.ogcapi.config.resource.RestartOrUpdateHandler;

import javax.inject.Inject;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class DeegreeWorkspaceRestartOrUpdateHandler implements RestartOrUpdateHandler {

    @Inject
    private DeegreeWorkspaceInitializer deegreeWorkspaceInitializer;

    @Override
    public void afterRestartOrUpdate() {
        deegreeWorkspaceInitializer.reinitialize();
    }

}
