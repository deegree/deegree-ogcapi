package org.deegree.services.oaf.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
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
    @Operation(summary = "supported conformance classes", description = "Retrieves the supported conformance classes")
    @Tag(name = "Capabilities")
    public Response conformanceJson(
                    @Parameter(description = "The request output format.", style = ParameterStyle.FORM)
                    @QueryParam("f")
                                    String format )
                    throws InvalidParameterValue {
        return conformance( format, JSON );
    }

    @GET
    @Produces({ APPLICATION_XML })
    @Operation(summary = "supported conformance classes", description = "Retrieves the supported conformance classes")
    @Tag(name = "Capabilities")
    public Response conformanceXml(
                    @Parameter(description = "The request output format.", style = ParameterStyle.FORM)
                    @QueryParam("f")
                                    String format )
                    throws InvalidParameterValue {
        return conformance( format, XML );
    }

    @GET
    @Produces({ TEXT_HTML })
    @Operation(hidden = true)
    public Response conformanceHtml(
                    @Parameter(description = "The request output format.", style = ParameterStyle.FORM)
                    @QueryParam("f")
                                    String format )
                    throws InvalidParameterValue {
        return conformance( format, RequestFormat.HTML );
    }

    @GET
    @Operation(hidden = true)
    public Response conformanceOther(
                    @Parameter(description = "The request output format.", style = ParameterStyle.FORM)
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
        return conformsTo;
    }

}