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
package org.deegree.services.oaf.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.deegree.services.oaf.RequestFormat;
import org.deegree.services.oaf.exceptions.InvalidParameterValue;
import org.deegree.services.oaf.link.Link;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static org.deegree.services.oaf.RequestFormat.JSON;
import static org.deegree.services.oaf.RequestFormat.XML;
import static org.deegree.services.oaf.RequestFormat.byFormatParameter;
import static org.deegree.services.oaf.domain.conformance.ConformanceClass.CORE;
import static org.deegree.services.oaf.domain.conformance.ConformanceClass.CRS;
import static org.deegree.services.oaf.domain.conformance.ConformanceClass.GEOJSON;
import static org.deegree.services.oaf.domain.conformance.ConformanceClass.GMLSF0;
import static org.deegree.services.oaf.domain.conformance.ConformanceClass.GMLSF2;
import static org.deegree.services.oaf.domain.conformance.ConformanceClass.HTML;
import static org.deegree.services.oaf.domain.conformance.ConformanceClass.OPENAPI30;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Path("/datasets/{datasetId}/conformance")
public class Conformance {

    @GET
    @Produces({ APPLICATION_JSON })
    @Operation(operationId = "conformance", summary = "supported conformance classes", description = "Retrieves the supported conformance classes")
    @Tag(name = "Capabilities")
    @ApiResponse(description = "default response", content = @Content(schema = @Schema(implementation = org.deegree.services.oaf.domain.conformance.Conformance.class)))
    public Response conformanceJson(
                    @Parameter(description = "The request output format.", style = ParameterStyle.FORM,
                                    schema = @Schema (allowableValues =  {"json","html","xml"}))
                    @QueryParam("f")
                                    String format )
                    throws InvalidParameterValue {
        return conformance( format, JSON );
    }

    @GET
    @Produces({ APPLICATION_XML })
    @Operation(hidden = true)
    public Response conformanceXml(
                    @Parameter(description = "The request output format.", style = ParameterStyle.FORM,
                                    schema = @Schema (allowableValues =  {"json","html","xml"}))
                    @QueryParam("f")
                                    String format )
                    throws InvalidParameterValue {
        return conformance( format, XML );
    }

    @GET
    @Produces({ TEXT_HTML })
    @Operation(hidden = true)
    public Response conformanceHtml(
                    @Parameter(description = "The request output format.", style = ParameterStyle.FORM,
                                    schema = @Schema (allowableValues =  {"json","html","xml"}))
                    @QueryParam("f")
                                    String format )
                    throws InvalidParameterValue {
        return conformance( format, RequestFormat.HTML );
    }

    @GET
    @Operation(hidden = true)
    public Response conformanceOther(
                    @Parameter(description = "The request output format.", style = ParameterStyle.FORM,
                                    schema = @Schema (allowableValues =  {"json","html","xml"}))
                    @QueryParam("f")
                                    String format )
                    throws InvalidParameterValue {
        return conformance( format, JSON );
    }

    private Response conformance( String formatParamValue, RequestFormat defaultFormat )
                    throws InvalidParameterValue {
        RequestFormat requestFormat = byFormatParameter( formatParamValue, defaultFormat );
        if ( RequestFormat.HTML.equals( requestFormat ) ) {
            return Response.ok( getClass().getResourceAsStream( "/conformance.html" ), TEXT_HTML ).build();
        }
        List<Link> conformsTo = createConformsTo();
        org.deegree.services.oaf.domain.conformance.Conformance conformance = new org.deegree.services.oaf.domain.conformance.Conformance(
                        conformsTo );
        return Response.ok( conformance, mediaTypeFromRequestFormat( requestFormat ) ).build();
    }

    private String mediaTypeFromRequestFormat( RequestFormat requestFormat ) {
        return RequestFormat.XML.equals( requestFormat ) ? APPLICATION_XML : APPLICATION_JSON;
    }

    private List<Link> createConformsTo() {
        List<Link> conformsTo = new ArrayList<>();
        conformsTo.add( new Link( CORE.getConformanceClass() ) );
        conformsTo.add( new Link( OPENAPI30.getConformanceClass() ) );
        conformsTo.add( new Link( HTML.getConformanceClass() ) );
        conformsTo.add( new Link( GEOJSON.getConformanceClass() ) );
        conformsTo.add( new Link( GMLSF0.getConformanceClass() ) );
        conformsTo.add( new Link( GMLSF2.getConformanceClass() ) );
        conformsTo.add( new Link( CRS.getConformanceClass() ) );
        return conformsTo;
    }

}
