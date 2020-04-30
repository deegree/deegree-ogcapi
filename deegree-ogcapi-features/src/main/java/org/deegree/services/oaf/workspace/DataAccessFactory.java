package org.deegree.services.oaf.workspace;

import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class DataAccessFactory {

    private static final Logger LOG = getLogger( DataAccessFactory.class );

    private static DataAccess instance;

    public static synchronized DataAccess getInstance() {
        if ( instance == null ) {
            instance = new DeegreeDataAccess();
        }
        return instance;
    }

}
