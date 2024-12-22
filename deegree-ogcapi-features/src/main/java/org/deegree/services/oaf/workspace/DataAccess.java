/*-
 * #%L
 * deegree-ogcapi-features - OGC API Features (OAF) implementation - Querying and modifying of geospatial data objects
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
package org.deegree.services.oaf.workspace;

import org.deegree.services.oaf.domain.collections.Collection;
import org.deegree.services.oaf.domain.collections.Collections;
import org.deegree.services.oaf.exceptions.InternalQueryException;
import org.deegree.services.oaf.exceptions.InvalidParameterValue;
import org.deegree.services.oaf.exceptions.UnknownCollectionId;
import org.deegree.services.oaf.exceptions.UnknownFeatureId;
import org.deegree.services.oaf.io.response.FeatureResponse;
import org.deegree.services.oaf.io.response.FeaturesResponse;
import org.deegree.services.oaf.io.request.FeaturesRequest;
import org.deegree.services.oaf.link.LinkBuilder;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public interface DataAccess {

	Collections createCollections(OafDatasetConfiguration oafConfiguration, LinkBuilder linkBuilder);

	Collection createCollection(OafDatasetConfiguration oafConfiguration, String collectionId, LinkBuilder linkBuilder)
			throws UnknownCollectionId;

	FeaturesResponse retrieveFeatures(OafDatasetConfiguration oafConfiguration, String collectionId,
			FeaturesRequest featuresRequest, LinkBuilder linkBuilder)
			throws InternalQueryException, InvalidParameterValue, UnknownCollectionId;

	FeatureResponse retrieveFeature(OafDatasetConfiguration oafConfiguration, String collectionId, String featureId,
			String crs, LinkBuilder linkBuilder)
			throws InternalQueryException, InvalidParameterValue, UnknownCollectionId, UnknownFeatureId;

}
