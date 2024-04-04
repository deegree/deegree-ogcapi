package org.deegree.services.oaf.cql2;

import org.deegree.cs.coordinatesystems.ICRS;
import org.deegree.filter.Expression;
import org.deegree.filter.expression.ValueReference;
import org.deegree.filter.spatial.Intersects;
import org.deegree.filter.spatial.SpatialOperator;
import org.deegree.geometry.Geometry;
import org.deegree.geometry.GeometryFactory;
import org.deegree.geometry.primitive.LineString;
import org.deegree.geometry.primitive.LinearRing;
import org.deegree.geometry.primitive.Point;
import org.deegree.geometry.primitive.Polygon;
import org.deegree.geometry.primitive.Ring;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class Cql2FilterVisitor extends Cql2BaseVisitor {

	private final ICRS filterCrs;

	/**
	 * @param filterCrs never <code>null</code>
	 */
	public Cql2FilterVisitor(ICRS filterCrs) {
		this.filterCrs = filterCrs;
	}

	@Override
	public Object visitBooleanExpression(Cql2Parser.BooleanExpressionContext ctx) {
		int terms = ctx.booleanTerm().size();
		if (terms == 1) {
			return ctx.booleanTerm(0).accept(this);
		}
		throw new Cql2UnsupportedExpressionException("More than one booleanTerm are currently not supported.");
	}

	@Override
	public Object visitBooleanTerm(Cql2Parser.BooleanTermContext ctx) {
		int factors = ctx.booleanFactor().size();
		if (factors == 1) {
			return ctx.booleanFactor(0).accept(this);
		}
		throw new Cql2UnsupportedExpressionException("More than one booleanFactor are currently not supported.");
	}

	@Override
	public Object visitBooleanFactor(Cql2Parser.BooleanFactorContext ctx) {
		return ctx.booleanPrimary().accept(this);
	}

	@Override
	public Object visitBooleanPrimary(Cql2Parser.BooleanPrimaryContext ctx) {
		if (ctx.booleanExpression() != null)
			throw new Cql2UnsupportedExpressionException("booleanExpressions are currently not supported.");
		if (ctx.function() != null)
			throw new Cql2UnsupportedExpressionException("functions are currently not supported.");
		if (ctx.BooleanLiteral() != null)
			throw new Cql2UnsupportedExpressionException("BooleanLiterals are currently not supported.");
		return ctx.predicate().accept(this);
	}

	@Override
	public Object visitPredicate(Cql2Parser.PredicateContext ctx) {
		if (ctx.comparisonPredicate() != null)
			throw new Cql2UnsupportedExpressionException("comparisonPredicate are currently not supported.");
		if (ctx.temporalPredicate() != null)
			throw new Cql2UnsupportedExpressionException("temporalPredicates are currently not supported.");
		if (ctx.arrayPredicate() != null)
			throw new Cql2UnsupportedExpressionException("arrayPredicate are currently not supported.");
		return ctx.spatialPredicate().accept(this);
	}

	@Override
	public SpatialOperator visitSpatialPredicate(Cql2Parser.SpatialPredicateContext ctx) {
		String spatialFunctionType = ctx.SpatialFunction().getText().toUpperCase().substring(2);
		SpatialOperator.SubType type = SpatialOperator.SubType.valueOf(spatialFunctionType);
		switch (type) {
			case INTERSECTS:
				Expression propeName = (Expression) ctx.geomExpression().get(0).accept(this);
				Geometry geometry = (Geometry) ctx.geomExpression().get(1).accept(this);
				return new Intersects(propeName, geometry);
		}
		throw new Cql2UnsupportedExpressionException("Unsupported geometry type " + type);
	}

	@Override
	public Object visitPropertyName(Cql2Parser.PropertyNameContext ctx) {
		String text = ctx.getText();
		return new ValueReference(text, null);
	}

	@Override
	public Object visitGeomExpression(Cql2Parser.GeomExpressionContext ctx) {
		if (ctx.function() != null) {
			throw new Cql2UnsupportedExpressionException("functions are currently not supported as geomExpressions.");
		}
		if (ctx.propertyName() != null) {
			return ctx.propertyName().accept(this);
		}
		return ctx.spatialInstance().accept(this);
	}

	@Override
	public Point visitPointText(Cql2Parser.PointTextContext ctx) {
		return (Point) ctx.point().accept(this);
	}

	@Override
	public LineString visitLineStringText(Cql2Parser.LineStringTextContext ctx) {
		List<Point> points = new ArrayList<>();
		for (Cql2Parser.PointContext p : ctx.point()) {
			points.add((Point) p.accept(this));
		}
		GeometryFactory geometryFactory = new GeometryFactory();
		return geometryFactory.createLineString("ls", filterCrs, geometryFactory.createPoints(points));
	}

	@Override
	public LinearRing visitLinearRingText(Cql2Parser.LinearRingTextContext ctx) {
		List<Point> points = new ArrayList<>();
		for (Cql2Parser.PointContext p : ctx.point()) {
			points.add((Point) p.accept(this));
		}
		GeometryFactory geometryFactory = new GeometryFactory();
		return geometryFactory.createLinearRing("lr", filterCrs, geometryFactory.createPoints(points));
	}

	@Override
	public Object visitPolygonText(Cql2Parser.PolygonTextContext ctx) {
		LinearRing exteriorRing = null;
		List<Ring> interiorRings = new ArrayList<>();
		for (Cql2Parser.LinearRingTextContext linearRing : ctx.linearRingText()) {
			if (exteriorRing == null) {
				exteriorRing = (LinearRing) linearRing.accept(this);
			}
			else {
				interiorRings.add((LinearRing) linearRing.accept(this));
			}
		}
		GeometryFactory geometryFactory = new GeometryFactory();
		return geometryFactory.createPolygon("po", filterCrs, exteriorRing, interiorRings);
	}

	@Override
	public Object visitMultiPointText(Cql2Parser.MultiPointTextContext ctx) {
		List<Point> points = new ArrayList<>();
		for (Cql2Parser.PointTextContext pointTextContext : ctx.pointText()) {
			points.add((Point) pointTextContext.accept(this));
		}
		GeometryFactory geometryFactory = new GeometryFactory();
		return geometryFactory.createMultiPoint("mp", filterCrs, points);
	}

	@Override
	public Object visitMultiLineStringText(Cql2Parser.MultiLineStringTextContext ctx) {
		List<LineString> lineStrings = new ArrayList<>();
		for (Cql2Parser.LineStringTextContext lineStringTextContext : ctx.lineStringText()) {
			lineStrings.add((LineString) lineStringTextContext.accept(this));
		}
		GeometryFactory geometryFactory = new GeometryFactory();
		return geometryFactory.createMultiLineString("ml", filterCrs, lineStrings);
	}

	@Override
	public Object visitMultiPolygonText(Cql2Parser.MultiPolygonTextContext ctx) {
		List<Polygon> polygons = new ArrayList<>();
		for (Cql2Parser.PolygonTextContext polygonTextContext : ctx.polygonText()) {
			polygons.add((Polygon) polygonTextContext.accept(this));
		}
		GeometryFactory geometryFactory = new GeometryFactory();
		return geometryFactory.createMultiPolygon("mpol", filterCrs, polygons);
	}

	@Override
	public Object visitGeometryCollectionText(Cql2Parser.GeometryCollectionTextContext ctx) {
		List<Geometry> geometries = new ArrayList<>();
		for (Cql2Parser.GeometryLiteralContext geometryLiteralContext : ctx.geometryLiteral()) {
			geometries.add((Geometry) geometryLiteralContext.accept(this));
		}
		return geometries;
	}

	@Override
	public Object visitGeometryCollectionTaggedText(Cql2Parser.GeometryCollectionTaggedTextContext ctx) {
		List<Geometry> geometries = (List<Geometry>) ctx.geometryCollectionText().accept(this);
		GeometryFactory geometryFactory = new GeometryFactory();
		return geometryFactory.createMultiGeometry("gc", filterCrs, geometries);
	}

	@Override
	public Object visitBboxTaggedText(Cql2Parser.BboxTaggedTextContext ctx) {
		return ctx.bboxText().accept(this);
	}

	@Override
	public Object visitBboxText(Cql2Parser.BboxTextContext ctx) {
		Double minX = Double.valueOf(ctx.westBoundLon().getText());
		Double minY = Double.valueOf(ctx.southBoundLat().getText());
		Double maxX = Double.valueOf(ctx.eastBoundLon().getText());
		Double maxY = Double.valueOf(ctx.northBoundLat().getText());
		GeometryFactory geometryFactory = new GeometryFactory();
		return geometryFactory.createEnvelope(minX, minY, maxX, maxY, filterCrs);
	}

	@Override
	public Point visitPoint(Cql2Parser.PointContext ctx) {
		GeometryFactory geometryFactory = new GeometryFactory();
		double x = (double) ctx.xCoord().accept(this);
		double y = (double) ctx.yCoord().accept(this);
		double z = ctx.zCoord() != null ? (double) ctx.zCoord().accept(this) : 0;
		return geometryFactory.createPoint("p", x, y, z, filterCrs);
	}

	@Override
	public Double visitXCoord(Cql2Parser.XCoordContext ctx) {
		return Double.valueOf(ctx.getText());
	}

	@Override
	public Double visitYCoord(Cql2Parser.YCoordContext ctx) {
		return Double.valueOf(ctx.getText());
	}

	@Override
	public Double visitZCoord(Cql2Parser.ZCoordContext ctx) {
		return Double.valueOf(ctx.getText());
	}

}
