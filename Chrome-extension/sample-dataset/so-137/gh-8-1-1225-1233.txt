package org.cugos.parboiledwkt;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;
import org.cugos.parboiledwkt.WKB.Endian;
import org.cugos.parboiledwkt.WKB.Type;
import org.cugos.parboiledwkt.WKB.GeometryType;
import org.cugos.parboiledwkt.WKB.GeometryTypeFlag;

/**
 * A Well Known Binary Writer.
 * @author Jared Erickson
 */
public class WKBWriter {

    /**
     * The WKB Type (WKB or EWKB)
     */
    private final Type wkbType;

    /**
     * Whether the byte order is big or little endian
     */
    private final Endian endian;

    /**
     * Create a WKBWriter with Type WKB and Big Endian byte order
     */
    public WKBWriter() {
        this(Type.WKB, Endian.Big);
    }

    /**
     * Create a WKBWriter
     * @param wkbType The WKB.Type standard
     * @param edian The WKB.Endian byte order
     */
    public WKBWriter(Type wkbType, Endian edian) {
        this.endian = edian;
        this.wkbType = wkbType;
    }

    /**
     * Write the Geometry to a hex String
     * @param geometry The Geometry
     * @return A WKB hex String
     */
    public String writeToHex(Geometry geometry) {
        return toHex(write(geometry));
    }

    /**
     * Write a Geometry to an array of bytes
     * @param g The Geometry
     * @return An array of bytes
     */
    public byte[] write(Geometry g) {
        if (g instanceof Point) {
            return write((Point) g);
        } else if (g instanceof LineString) {
            return write((LineString) g);
        } else if (g instanceof LinearRing) {
            return write((LinearRing) g);
        } else if (g instanceof Triangle) {
            return write((Triangle) g);
        } else if (g instanceof Polygon) {
            return write((Polygon) g);
        } else if (g instanceof MultiPoint) {
            return write((MultiPoint) g);
        } else if (g instanceof MultiLineString) {
            return write((MultiLineString) g);
        } else if (g instanceof MultiPolygon) {
            return write((MultiPolygon) g);
        } else if (g instanceof GeometryCollection) {
            return write((GeometryCollection) g);
        } else if (g instanceof CircularString) {
            return write((CircularString) g);
        } else if (g instanceof CurvePolygon) {
            return write((CurvePolygon) g);
        } else if (g instanceof CompoundCurve) {
            return write((CompoundCurve) g);
        } else if (g instanceof MultiCurve) {
            return write((MultiCurve) g);
        } else if (g instanceof PolyHedralSurface) {
            return write((PolyHedralSurface) g);
        } else if (g instanceof MultiSurface) {
            return write((MultiSurface) g);
        } else if (g instanceof Tin) {
            return write((Tin) g);
        } else {
            throw new IllegalArgumentException("Unsupported Geometry! " + g.getClass().getName());
        }
    }

    /**
     * Calculate the number of bytes for a given Geometry.
     * @param g The Geometry
     * @return The number of bytes necessary for the given Geometry
     */
    private int calculateNumberOfBytes(Geometry g) {
        if (g instanceof Point) {
            return calculateNumberOfBytes((Point) g);
        } else if (g instanceof LineString) {
            return calculateNumberOfBytes((LineString) g);
        } else if (g instanceof LinearRing) {
            return calculateNumberOfBytes((LinearRing) g);
        } else if (g instanceof Triangle) {
            return calculateNumberOfBytes((Triangle) g);
        } else if (g instanceof Polygon) {
            return calculateNumberOfBytes((Polygon) g);
        } else if (g instanceof MultiPoint) {
            return calculateNumberOfBytes((MultiPoint) g);
        } else if (g instanceof MultiLineString) {
            return calculateNumberOfBytes((MultiLineString) g);
        } else if (g instanceof MultiPolygon) {
            return calculateNumberOfBytes((MultiPolygon) g);
        } else if (g instanceof GeometryCollection) {
            return calculateNumberOfBytes((GeometryCollection) g);
        } else if (g instanceof CircularString) {
            return calculateNumberOfBytes((CircularString) g);
        } else if (g instanceof CompoundCurve) {
            return calculateNumberOfBytes((CompoundCurve) g);
        }  else if (g instanceof CurvePolygon) {
            return calculateNumberOfBytes((CurvePolygon) g);
        } else if (g instanceof MultiCurve) {
            return calculateNumberOfBytes((MultiCurve) g);
        } else if (g instanceof PolyHedralSurface) {
            return calculateNumberOfBytes((PolyHedralSurface) g);
        } else if (g instanceof MultiSurface) {
            return calculateNumberOfBytes((MultiSurface) g);
        } else if (g instanceof Tin) {
            return calculateNumberOfBytes((Tin) g);
        } else {
            throw new IllegalArgumentException("Unsupported Geometry! " + g.getClass().getName());
        }
    }

    /**
     * Write the Geometry into the ByteBuffer
     * @param buffer The ByteBuffer
     * @param g The Geometry
     */
    private void putGeometry(ByteBuffer buffer, Geometry g) {
        if (g instanceof Point) {
            putPoint(buffer, (Point) g);
        } else if (g instanceof LineString) {
            putLineString(buffer, (LineString) g);
        } else if (g instanceof LinearRing) {
            putLinearRing(buffer, (LinearRing) g);
        } else if (g instanceof Triangle) {
            putTriangle(buffer, (Triangle) g);
        } else if (g instanceof Polygon) {
            putPolygon(buffer, (Polygon) g);
        } else if (g instanceof MultiPoint) {
            putMultiPoint(buffer, (MultiPoint) g);
        } else if (g instanceof MultiLineString) {
            putMultiLineString(buffer, (MultiLineString) g);
        } else if (g instanceof MultiPolygon) {
            putMultiPolygon(buffer, (MultiPolygon) g);
        } else if (g instanceof GeometryCollection) {
            putGeometryCollection(buffer, (GeometryCollection) g);
        } else if (g instanceof CircularString) {
            putCircularString(buffer, (CircularString) g);
        } else if (g instanceof CompoundCurve) {
            putCompoundCurve(buffer, (CompoundCurve) g);
        }  else if (g instanceof CurvePolygon) {
            putCurvePolygon(buffer, (CurvePolygon) g);
        } else if (g instanceof MultiCurve) {
           putMultiCurve(buffer, (MultiCurve) g);
        } else if (g instanceof PolyHedralSurface) {
            putPolyHedralSurface(buffer, (PolyHedralSurface) g);
        } else if (g instanceof MultiSurface) {
            putMultiSurface(buffer, (MultiSurface) g);
        } else if (g instanceof Tin) {
            putTin(buffer, (Tin) g);
        } else {
            throw new IllegalArgumentException("Unsupported Geometry! " + g.getClass().getName());
        }
    }

    /**
     * Calculate the number of bytes for header including an optional SRID.
     * This includes the byte order and geometry type, so minimum of 5.
     * @param srid The optional SRID
     * @return
     */
    private int calculateNumberOfBytes(String srid) {
        int numberOfBytes = 1 + 4;
        if (wkbType == Type.EWKB && srid != null) {
            numberOfBytes += 4;
        }
        return numberOfBytes;
    }

    /**
     * Calculate the number of bytes for the given number of Coordinates with the given Dimension
     * @param numberOfCoordinates The number of Coordinates
     * @param dimension The Dimension of the Coordinates
     * @return The number of bytes
     */
    private int calculateNumberOfBytes(int numberOfCoordinates, Dimension dimension) {
        int multiplier = getMultiplier(dimension);
        return 8 * (numberOfCoordinates * multiplier);
    }

    /**
     * Get the multiplier for a Coordinate based on the Dimension.
     * @param dimension The Dimension
     * @return A multiplier
     */
    private int getMultiplier(Dimension dimension) {
        if (dimension == Dimension.Two) {
            return 2;
        } else if (dimension == Dimension.TwoMeasured || dimension == Dimension.Three) {
            return 3;
        } else if (dimension == Dimension.ThreeMeasured) {
            return 4;
        } else {
            return 2;
        }
    }

    /**
     * Write the byte order into the ByteBuffer and order the ByteBuffer
     * @param buffer The ByteBuffer
     */
    private void putByteOrder(ByteBuffer buffer) {
        if (endian == Endian.Big) {
            buffer.order(ByteOrder.BIG_ENDIAN);
        } else {
            buffer.order(ByteOrder.LITTLE_ENDIAN);
        }
        buffer.put((byte) endian.getValue());
    }

    /**
     * Write the geometry type into the ByteBuffer
     * @param buffer The ByteBuffer
     * @param geometryType The WKB.GeometryType
     * @param dimension The Dimension
     * @param srid The SRID
     */
    private void putGeometryType(ByteBuffer buffer, GeometryType geometryType, Dimension dimension, String srid) {
        int b = geometryType.getValue();
        if (wkbType == Type.EWKB) {
            if (dimension == Dimension.Three || dimension == Dimension.ThreeMeasured) {
                b = b | GeometryTypeFlag.Z.getValue();
            }
            if (dimension == Dimension.TwoMeasured || dimension == Dimension.ThreeMeasured) {
                b = b | GeometryTypeFlag.M.getValue();
            }
            if (srid != null) {
                b = b | GeometryTypeFlag.SRID.getValue();
            }
        }
        buffer.putInt(b);
    }

    /**
     * Write an SRID into the ByteBuffer
     * @param buffer The ByteBuffer
     * @param srid The SRID
     */
    private void putSrid(ByteBuffer buffer, String srid) {
        if (wkbType == Type.EWKB && srid != null) {
            buffer.putInt(Integer.parseInt(srid));
        }
    }

    /**
     * Write a Coordinate into the ByteBuffer
     * @param buffer The ByteBuffer
     * @param coord The Coordinates
     */
    private void putCoordinate(ByteBuffer buffer, Coordinate coord) {
        Dimension dimension = coord.getDimension();
        buffer.putDouble(coord.getX());
        buffer.putDouble(coord.getY());
        if (dimension == Dimension.Three || dimension == Dimension.ThreeMeasured) {
            buffer.putDouble(coord.getZ());
        }
        if (dimension == Dimension.TwoMeasured || dimension == Dimension.ThreeMeasured) {
            buffer.putDouble(coord.getM());
        }
    }

    /**
     * Write a List of Coordinates into the ByteBuffer
     * @param buffer The ByteBuffer
     * @param coords The List of Coordinates
     */
    private void putCoordinates(ByteBuffer buffer, List<Coordinate> coords) {
        buffer.putInt(coords.size());
        for(Coordinate coord : coords) {
            putCoordinate(buffer, coord);
        }
    }

    // Point

    /**
     * Write a Point to a hex String
     * @param point The Point
     * @return A hex String
     */
    public String writeToHex(Point point) {
        return toHex(write(point));
    }

    /**
     * Write a Point to a byte array
     * @param point The Point
     * @return A byte array
     */
    public byte[] write(Point point) {
        ByteBuffer buffer = ByteBuffer.allocate(calculateNumberOfBytes(point));
        putPoint(buffer, point);
        return buffer.array();
    }

    /**
     * Calculate the number of bytes necessary for the Point
     * @param point The Point
     * @return The number of bytes necessary for the Point
     */
    private int calculateNumberOfBytes(Point point) {
        return calculateNumberOfBytes(point.getSrid()) +
                calculateNumberOfBytes(point.getNumberOfCoordinates(), point.getDimension());
    }

    /**
     * Write the Point into the ByteBuffer
     * @param buffer The ByteBuffer
     * @param point The Point
     */
    private void putPoint(ByteBuffer buffer, Point point) {
        putByteOrder(buffer);
        putGeometryType(buffer, GeometryType.Point, point.getDimension(), point.getSrid());
        putSrid(buffer, point.getSrid());
        if (!point.isEmpty()) {
            putCoordinate(buffer, point.getCoordinate());
        }
    }

    // LinearRing

    /**
     * Write a LinearRing to a hex String
     * @param linearRing The LinearRing
     * @return A hex String
     */
    public String writeToHex(LinearRing linearRing) {
        return toHex(write(linearRing));
    }

    /**
     * Write a LinearRing to a byte array
     * @param linearRing The LinearRing
     * @return A byte array
     */
    public byte[] write(LinearRing linearRing) {
        ByteBuffer buffer = ByteBuffer.allocate(calculateNumberOfBytes(linearRing));
        putLineString(buffer, linearRing);
        return buffer.array();
    }

    /**
     * Calculate the number of bytes necessary for the LinearRing
     * @param linearRing The LinearRing
     * @return The number of bytes necessary for the LinearRing
     */
    private int calculateNumberOfBytes(LinearRing linearRing) {
        return 4 + calculateNumberOfBytes(linearRing.getSrid()) +
                calculateNumberOfBytes(linearRing.getNumberOfCoordinates(), linearRing.getDimension());
    }

    /**
     * Write the LinearRing into the ByteBuffer
     * @param buffer The ByteBuffer
     * @param linearRing The LinearRing
     */
    private void putLinearRing(ByteBuffer buffer, LinearRing linearRing) {
        putByteOrder(buffer);
        putGeometryType(buffer, GeometryType.LineString, linearRing.getDimension(), linearRing.getSrid());
        putSrid(buffer, linearRing.getSrid());
        if (!linearRing.isEmpty()) {
            putCoordinates(buffer, linearRing.getCoordinates());
        }
    }

    // LineString

    /**
     * Write a LineString to a hex String
     * @param lineString The LineString
     * @return A hex String
     */
    public String writeToHex(LineString lineString) {
        return toHex(write(lineString));
    }

    /**
     * Write a LineString to a byte array
     * @param lineString The LineString
     * @return A byte array
     */
    public byte[] write(LineString lineString) {
        ByteBuffer buffer = ByteBuffer.allocate(calculateNumberOfBytes(lineString));
        putLineString(buffer, lineString);
        return buffer.array();
    }

    /**
     * Calculate the number of bytes necessary for the LineString
     * @param lineString The LineString
     * @return The number of bytes necessary for the LineString
     */
    private int calculateNumberOfBytes(LineString lineString) {
        return 4 + calculateNumberOfBytes(lineString.getSrid()) +
                calculateNumberOfBytes(lineString.getNumberOfCoordinates(), lineString.getDimension());
    }

    /**
     * Write the LineString into the ByteBuffer
     * @param buffer The ByteBuffer
     * @param lineString The LineString
     */
    private void putLineString(ByteBuffer buffer, LineString lineString) {
        putByteOrder(buffer);
        putGeometryType(buffer, GeometryType.LineString, lineString.getDimension(), lineString.getSrid());
        putSrid(buffer, lineString.getSrid());
        if (!lineString.isEmpty()) {
            putCoordinates(buffer, lineString.getCoordinates());
        }
    }

    // Polygon

    /**
     * Write a Polygon to a hex String
     * @param polygon The Polygon
     * @return A hex String
     */
    public String writeToHex(Polygon polygon) {
        return toHex(write(polygon));
    }

    /**
     * Write a Polygon to a byte array
     * @param polygon The Polygon
     * @return A byte array
     */
    public byte[] write(Polygon polygon) {
        ByteBuffer buffer = ByteBuffer.allocate(calculateNumberOfBytes(polygon));
        putPolygon(buffer, polygon);
        return buffer.array();
    }

    /**
     * Calculate the number of bytes necessary for the Polygon
     * @param polygon The Polygon
     * @return The number of bytes necessary for the Polygon
     */
    private int calculateNumberOfBytes(Polygon polygon) {
        int numberOfRings = 0;
        if (!polygon.isEmpty()) {
            numberOfRings = 1 + polygon.getInnerLinearRings().size();
        }
        return 4 +
            numberOfRings * 4 + // Number of coordinate for each ring
            calculateNumberOfBytes(polygon.getSrid()) +
            calculateNumberOfBytes(polygon.getNumberOfCoordinates(), polygon.getDimension());
    }

    /**
     * Write the Polygon into the ByteBuffer
     * @param buffer The ByteBuffer
     * @param polygon The Polygon
     */
    private void putPolygon(ByteBuffer buffer, Polygon polygon) {
        putByteOrder(buffer);
        putGeometryType(buffer, GeometryType.Polygon, polygon.getDimension(), polygon.getSrid());
        putSrid(buffer, polygon.getSrid());
        // Number of Rings
        int numberOfRings = 0;
        if (!polygon.isEmpty()) {
            numberOfRings = 1 + polygon.getInnerLinearRings().size();
        }
        buffer.putInt(numberOfRings);
        // Rings
        if (!polygon.isEmpty()) {
            putCoordinates(buffer, polygon.getOuterLinearRing().getCoordinates());
            for(LinearRing ring : polygon.getInnerLinearRings()) {
                putCoordinates(buffer, ring.getCoordinates());
            }
        }
    }

    // MultiPoint

    /**
     * Write a MultiPoint to a hex String
     * @param multiPoint The MultiPoint
     * @return A hex String
     */
    public String writeToHex(MultiPoint multiPoint) {
        return toHex(write(multiPoint));
    }

    /**
     * Write a MultiPoint to a byte array
     * @param multiPoint The MultiPoint
     * @return A byte array
     */
    public byte[] write(MultiPoint multiPoint) {
        ByteBuffer buffer = ByteBuffer.allocate(calculateNumberOfBytes(multiPoint));
        putMultiPoint(buffer, multiPoint);
        return buffer.array();
    }

    /**
     * Calculate the number of bytes necessary for the MultiPoint
     * @param multiPoint The MultiPoint
     * @return The number of bytes necessary for the MultiPoint
     */
    private int calculateNumberOfBytes(MultiPoint multiPoint) {
        return 4 +
            // Number of coordinates
            calculateNumberOfBytes(multiPoint.getSrid()) +
            // Points
            ((calculateNumberOfBytes(multiPoint.getSrid()) + calculateNumberOfBytes(1, multiPoint.getDimension())) * multiPoint.getPoints().size());
    }

    /**
     * Write the MultiPoint into the ByteBuffer
     * @param buffer The ByteBuffer
     * @param multiPoint The MultiPoint
     */
    private void putMultiPoint(ByteBuffer buffer, MultiPoint multiPoint) {
        putByteOrder(buffer);
        putGeometryType(buffer, GeometryType.MultiPoint, multiPoint.getDimension(), multiPoint.getSrid());
        putSrid(buffer, multiPoint.getSrid());
        buffer.putInt(multiPoint.getNumberOfCoordinates());
        if (!multiPoint.isEmpty()) {
            for (Point pt : multiPoint.getPoints()) {
                putPoint(buffer, pt);
            }
        }
    }

    // MultiLineString

    /**
     * Write a MultiLineString to a hex String
     * @param multiLineString The MultiLineString
     * @return A hex String
     */
    public String writeToHex(MultiLineString multiLineString) {
        return toHex(write(multiLineString));
    }

    /**
     * Write a MultiLineString to a byte array
     * @param multiLineString The MultiLineString
     * @return A byte array
     */
    public byte[] write(MultiLineString multiLineString) {
        ByteBuffer buffer = ByteBuffer.allocate(calculateNumberOfBytes(multiLineString));
        putMultiLineString(buffer, multiLineString);
        return buffer.array();
    }

    /**
     * Calculate the number of bytes necessary for the MultiLineString
     * @param multiLineString The MultiLineString
     * @return The number of bytes necessary for the MultiLineString
     */
    private int calculateNumberOfBytes(MultiLineString multiLineString) {
        int numberOfBytes = 4 + calculateNumberOfBytes(multiLineString.getSrid());
        for(LineString lineString : multiLineString.getLineStrings()) {
            numberOfBytes += calculateNumberOfBytes(lineString);
        }
        return numberOfBytes;
    }

    /**
     * Write the MultiLineString into the ByteBuffer
     * @param buffer The ByteBuffer
     * @param multiLineString The MultiLineString
     */
    private void putMultiLineString(ByteBuffer buffer, MultiLineString multiLineString) {
        putByteOrder(buffer);
        putGeometryType(buffer, GeometryType.MultiLineString, multiLineString.getDimension(), multiLineString.getSrid());
        putSrid(buffer, multiLineString.getSrid());
        buffer.putInt(multiLineString.getLineStrings().size());
        if (!multiLineString.isEmpty()) {
            for (LineString lineString : multiLineString.getLineStrings()) {
                putLineString(buffer, lineString);
            }
        }
    }

    // MultiPolygon

    /**
     * Write a MultiPolygon to a hex String
     * @param multiPolygon The MultiPolygon
     * @return A hex String
     */
    public String writeToHex(MultiPolygon multiPolygon) {
        return toHex(write(multiPolygon));
    }

    /**
     * Write a MultiPolygon to a byte array
     * @param multiPolygon The MultiPolygon
     * @return A byte array
     */
    public byte[] write(MultiPolygon multiPolygon) {
        ByteBuffer buffer = ByteBuffer.allocate(calculateNumberOfBytes(multiPolygon));
        putMultiPolygon(buffer, multiPolygon);
        return buffer.array();
    }

    /**
     * Calculate the number of bytes necessary for the MultiPolygon
     * @param multiPolygon The MultiPolygon
     * @return The number of bytes necessary for the MultiPolygon
     */
    private int calculateNumberOfBytes(MultiPolygon multiPolygon) {
        int numberOfBytes = 4 + calculateNumberOfBytes(multiPolygon.getSrid());
        for(Polygon polygon : multiPolygon.getPolygons()) {
            numberOfBytes += calculateNumberOfBytes(polygon);
        }
        return numberOfBytes;
    }

    /**
     * Write the MultiPolygon into the ByteBuffer
     * @param buffer The ByteBuffer
     * @param multiPolygon The MultiPolygon
     */
    private void putMultiPolygon(ByteBuffer buffer, MultiPolygon multiPolygon) {
        putByteOrder(buffer);
        putGeometryType(buffer, GeometryType.MultiPolygon, multiPolygon.getDimension(), multiPolygon.getSrid());
        putSrid(buffer, multiPolygon.getSrid());
        buffer.putInt(multiPolygon.getPolygons().size());
        if (!multiPolygon.isEmpty()) {
            for (Polygon polygon: multiPolygon.getPolygons()) {
                putPolygon(buffer, polygon);
            }
        }
    }

    // GeometryCollection

    /**
     * Write a GeometryCollection to a hex String
     * @param geometryCollection The GeometryCollection
     * @return A hex String
     */
    public String writeToHex(GeometryCollection geometryCollection) {
        return toHex(write(geometryCollection));
    }

    /**
     * Write a GeometryCollection to a byte array
     * @param geometryCollection The GeometryCollection
     * @return A byte array
     */
    public byte[] write(GeometryCollection geometryCollection) {
        ByteBuffer buffer = ByteBuffer.allocate(calculateNumberOfBytes(geometryCollection));
        putGeometryCollection(buffer, geometryCollection);
        return buffer.array();
    }

    /**
     * Calculate the number of bytes necessary for the GeometryCollection
     * @param geometryCollection The GeometryCollection
     * @return The number of bytes necessary for the GeometryCollection
     */
    private int calculateNumberOfBytes(GeometryCollection geometryCollection) {
        int numberOfBytes = 4 + calculateNumberOfBytes(geometryCollection.getSrid());
        for(Geometry geometry : geometryCollection.getGeometries()) {
            numberOfBytes += calculateNumberOfBytes(geometry);
        }
        return numberOfBytes;
    }

    /**
     * Write the GeometryCollection into the ByteBuffer
     * @param buffer The ByteBuffer
     * @param geometryCollection The GeometryCollection
     */
    private void putGeometryCollection(ByteBuffer buffer, GeometryCollection geometryCollection) {
        putByteOrder(buffer);
        putGeometryType(buffer, GeometryType.GeometryCollection, geometryCollection.getDimension(), geometryCollection.getSrid());
        putSrid(buffer, geometryCollection.getSrid());
        buffer.putInt(geometryCollection.getGeometries().size());
        if (!geometryCollection.isEmpty()) {
            for (Geometry geometry : geometryCollection.getGeometries()) {
                putGeometry(buffer, geometry);
            }
        }
    }

    // CircularString

    /**
     * Write a CircularString to a hex String
     * @param circularString The CircularString
     * @return A hex String
     */
    public String writeToHex(CircularString circularString) {
        return toHex(write(circularString));
    }

    /**
     * Write a CircularString to a byte array
     * @param circularString The CircularString
     * @return A byte array
     */
    public byte[] write(CircularString circularString) {
        ByteBuffer buffer = ByteBuffer.allocate(calculateNumberOfBytes(circularString));
        putCircularString(buffer, circularString);
        return buffer.array();
    }

    /**
     * Calculate the number of bytes necessary for the CircularString
     * @param circularString The CircularString
     * @return The number of bytes necessary for the CircularString
     */
    private int calculateNumberOfBytes(CircularString circularString) {
        return 4 + calculateNumberOfBytes(circularString.getSrid()) +
                calculateNumberOfBytes(circularString.getNumberOfCoordinates(), circularString.getDimension());
    }

    /**
     * Write the CircularString into the ByteBuffer
     * @param buffer The ByteBuffer
     * @param circularString The CircularString
     */
    private void putCircularString(ByteBuffer buffer, CircularString circularString) {
        putByteOrder(buffer);
        putGeometryType(buffer, GeometryType.CircularString, circularString.getDimension(), circularString.getSrid());
        putSrid(buffer, circularString.getSrid());
        if (!circularString.isEmpty()) {
            putCoordinates(buffer, circularString.getCoordinates());
        }
    }

    // Curve

    /**
     * Calculate the number of bytes necessary for the Curve
     * @param curve The GeometryCollection
     * @return The number of bytes necessary for the Curve
     */
    private int calculateNumberOfBytes(Curve curve) {
        if (curve instanceof LineString) {
            return calculateNumberOfBytes((LineString) curve);
        } else if (curve instanceof CircularString) {
            return calculateNumberOfBytes((CircularString) curve);
        } else if (curve instanceof CompoundCurve) {
            return calculateNumberOfBytes((CompoundCurve) curve);
        } else {
            throw new IllegalArgumentException("Unsupported Curve! " + curve.getClass().getName());
        }
    }

    /**
     * Write the Curve into the ByteBuffer
     * @param buffer The ByteBuffer
     * @param curve The Curve
     */
    private void putCurve(ByteBuffer buffer, Curve curve) {
        if (curve instanceof LineString) {
            putLineString(buffer, (LineString) curve);
        } else if (curve instanceof CircularString) {
            putCircularString(buffer, (CircularString) curve);
        } else if (curve instanceof CompoundCurve) {
            putCompoundCurve(buffer, (CompoundCurve) curve);
        } else {
            throw new IllegalArgumentException("Unsupported Curve! " + curve.getClass().getName());
        }
    }

    // CompoundCurve

    /**
     * Write a CompoundCurve to a hex String
     * @param compoundCurve The CompoundCurve
     * @return A hex String
     */
    public String writeToHex(CompoundCurve compoundCurve) {
        return toHex(write(compoundCurve));
    }

    /**
     * Write a CompoundCurve to a byte array
     * @param compoundCurve The CompoundCurve
     * @return A byte array
     */
    public byte[] write(CompoundCurve compoundCurve) {
        ByteBuffer buffer = ByteBuffer.allocate(calculateNumberOfBytes(compoundCurve));
        putCompoundCurve(buffer, compoundCurve);
        return buffer.array();
    }

    /**
     * Calculate the number of bytes necessary for the CompoundCurve
     * @param compoundCurve The CompoundCurve
     * @return The number of bytes necessary for the CompoundCurve
     */
    private int calculateNumberOfBytes(CompoundCurve compoundCurve) {
        int numberOfBytes = 4 + calculateNumberOfBytes(compoundCurve.getSrid());
        for (Curve curve : compoundCurve.getCurves()) {
            numberOfBytes += calculateNumberOfBytes(curve);
        }
        return  numberOfBytes;
    }

    /**
     * Write the CompoundCurve into the ByteBuffer
     * @param buffer The ByteBuffer
     * @param compoundCurve The CompoundCurve
     */
    private void putCompoundCurve(ByteBuffer buffer, CompoundCurve compoundCurve) {
        putByteOrder(buffer);
        putGeometryType(buffer, GeometryType.CompoundCurve, compoundCurve.getDimension(), compoundCurve.getSrid());
        putSrid(buffer, compoundCurve.getSrid());
        buffer.putInt(compoundCurve.getCurves().size());
        if (!compoundCurve.isEmpty()) {
            for (Curve curve : compoundCurve.getCurves()) {
                putCurve(buffer, curve);
            }
        }
    }

    // CurvePolygon

    /**
     * Write a CurvePolygon to a hex String
     * @param curvePolygon The CurvePolygon
     * @return A hex String
     */
    public String writeToHex(CurvePolygon curvePolygon) {
        return toHex(write(curvePolygon));
    }

    /**
     * Write a CurvePolygon to a byte array
     * @param curvePolygon The CurvePolygon
     * @return A byte array
     */
    public byte[] write(CurvePolygon curvePolygon) {
        ByteBuffer buffer = ByteBuffer.allocate(calculateNumberOfBytes(curvePolygon));
        putCurvePolygon(buffer, curvePolygon);
        return buffer.array();
    }

    /**
     * Calculate the number of bytes necessary for the CurvePolygon
     * @param curvePolygon The CurvePolygon
     * @return The number of bytes necessary for the CurvePolygon
     */
    private int calculateNumberOfBytes(CurvePolygon curvePolygon) {
        int numberOfBytes = 4 + calculateNumberOfBytes(curvePolygon.getSrid());
        numberOfBytes += calculateNumberOfBytes(curvePolygon.getOuterCurve());
        for (Curve curve : curvePolygon.getInnerCurves()) {
            numberOfBytes += calculateNumberOfBytes(curve);
        }
        return  numberOfBytes;
    }

    /**
     * Write the CurvePolygon into the ByteBuffer
     * @param buffer The ByteBuffer
     * @param curvePolygon The CurvePolygon
     */
    private void putCurvePolygon(ByteBuffer buffer, CurvePolygon curvePolygon) {
        putByteOrder(buffer);
        putGeometryType(buffer, GeometryType.CurvePolygon, curvePolygon.getDimension(), curvePolygon.getSrid());
        putSrid(buffer, curvePolygon.getSrid());
        int numberOfCurves = 0;
        if (!curvePolygon.isEmpty()) {
            numberOfCurves += 1 + curvePolygon.getInnerCurves().size();
        }
        buffer.putInt(numberOfCurves);
        if (!curvePolygon.isEmpty()) {
            putCurve(buffer, curvePolygon.getOuterCurve());
            for (Curve curve : curvePolygon.getInnerCurves()) {
                putCurve(buffer, curve);
            }
        }
    }

    // MultiCurve

    /**
     * Write a MultiCurve to a hex String
     * @param multiCurve The MultiCurve
     * @return A hex String
     */
    public String writeToHex(MultiCurve multiCurve) {
        return toHex(write(multiCurve));
    }

    /**
     * Write a MultiCurve to a byte array
     * @param multiCurve The MultiCurve
     * @return A byte array
     */
    public byte[] write(MultiCurve multiCurve) {
        ByteBuffer buffer = ByteBuffer.allocate(calculateNumberOfBytes(multiCurve));
        putMultiCurve(buffer, multiCurve);
        return buffer.array();
    }

    /**
     * Calculate the number of bytes necessary for the MultiCurve
     * @param multiCurve The MultiCurve
     * @return The number of bytes necessary for the MultiCurve
     */
    private int calculateNumberOfBytes(MultiCurve multiCurve) {
        int numberOfBytes = 4 + calculateNumberOfBytes(multiCurve.getSrid());
        for (Curve curve : multiCurve.getCurves()) {
            numberOfBytes += calculateNumberOfBytes(curve);
        }
        return  numberOfBytes;
    }

    /**
     * Write the MultiCurve into the ByteBuffer
     * @param buffer The ByteBuffer
     * @param multiCurve The MultiCurve
     */
    private void putMultiCurve(ByteBuffer buffer, MultiCurve multiCurve) {
        putByteOrder(buffer);
        putGeometryType(buffer, GeometryType.MultiCurve, multiCurve.getDimension(), multiCurve.getSrid());
        putSrid(buffer, multiCurve.getSrid());
        buffer.putInt(multiCurve.getCurves().size());
        if (!multiCurve.isEmpty()) {
            for (Curve curve : multiCurve.getCurves()) {
                putCurve(buffer, curve);
            }
        }
    }

    // Surface

    /**
     * Calculate the number of bytes necessary for the Surface
     * @param surface The Surface
     * @return The number of bytes necessary for the Surface
     */
    private int calculateNumberOfBytes(Surface surface) {
        if (surface instanceof Triangle) {
            return calculateNumberOfBytes((Triangle) surface);
        } else if (surface instanceof Polygon) {
            return calculateNumberOfBytes((Polygon) surface);
        } else if (surface instanceof PolyHedralSurface) {
            return calculateNumberOfBytes((PolyHedralSurface) surface);
        } else if (surface instanceof Tin) {
            return calculateNumberOfBytes((Tin) surface);
        } else if (surface instanceof CurvePolygon) {
            return calculateNumberOfBytes((CurvePolygon) surface);
        } else {
            throw new IllegalArgumentException("Unknown Surface: " + surface.getClass().getName());
        }
    }

    /**
     * Write the Surface into the ByteBuffer
     * @param buffer The ByteBuffer
     * @param surface The Surface
     */
    private void putSurface(ByteBuffer buffer, Surface surface) {
        if (surface instanceof Triangle) {
            putTriangle(buffer, (Triangle) surface);
        } else if (surface instanceof Polygon) {
            putPolygon(buffer, (Polygon) surface);
        } else if (surface instanceof PolyHedralSurface) {
            putPolyHedralSurface(buffer, (PolyHedralSurface) surface);
        } else if (surface instanceof Tin) {
            putTin(buffer, (Tin) surface);
        } else if (surface instanceof CurvePolygon) {
            putCurvePolygon(buffer, (CurvePolygon) surface);
        } else {
            throw new IllegalArgumentException("Unknown Surface: " + surface.getClass().getName());
        }
    }

    // MultiSurface

    /**
     * Write a MultiSurface to a hex String
     * @param multiSurface The MultiSurface
     * @return A hex String
     */
    public String writeToHex(MultiSurface multiSurface) {
        return toHex(write(multiSurface));
    }

    /**
     * Write a MultiSurface to a byte array
     * @param multiSurface The MultiSurface
     * @return A byte array
     */
    public byte[] write(MultiSurface multiSurface) {
        ByteBuffer buffer = ByteBuffer.allocate(calculateNumberOfBytes(multiSurface));
        putMultiSurface(buffer, multiSurface);
        return buffer.array();
    }

    /**
     * Calculate the number of bytes necessary for the MultiSurface
     * @param multiSurface The MultiSurface
     * @return The number of bytes necessary for the MultiSurface
     */
    private int calculateNumberOfBytes(MultiSurface multiSurface) {
        int numberOfBytes = 4 + calculateNumberOfBytes(multiSurface.getSrid());
        for (Surface surface : multiSurface.getSurfaces()) {
            numberOfBytes += calculateNumberOfBytes(surface);
        }
        return  numberOfBytes;
    }

    /**
     * Write the MultiSurface into the ByteBuffer
     * @param buffer The ByteBuffer
     * @param multiSurface The MultiSurface
     */
    private void putMultiSurface(ByteBuffer buffer, MultiSurface multiSurface) {
        putByteOrder(buffer);
        putGeometryType(buffer, GeometryType.MultiSurface, multiSurface.getDimension(), multiSurface.getSrid());
        putSrid(buffer, multiSurface.getSrid());
        buffer.putInt(multiSurface.getSurfaces().size());
        if (!multiSurface.isEmpty()) {
            for (Surface surface : multiSurface.getSurfaces()) {
                putSurface(buffer, surface);
            }
        }
    }

    // Tin

    /**
     * Write a Tin to a hex String
     * @param tin The Tin
     * @return A hex String
     */
    public String writeToHex(Tin tin) {
        return toHex(write(tin));
    }

    /**
     * Write a Tin to a byte array
     * @param tin The Tin
     * @return A byte array
     */
    public byte[] write(Tin tin) {
        ByteBuffer buffer = ByteBuffer.allocate(calculateNumberOfBytes(tin));
        putTin(buffer, tin);
        return buffer.array();
    }

    /**
     * Calculate the number of bytes necessary for the Tin
     * @param tin The Tin
     * @return The number of bytes necessary for the Tin
     */
    private int calculateNumberOfBytes(Tin tin) {
        int numberOfBytes = 4 + calculateNumberOfBytes(tin.getSrid());
        for(Triangle triangle : tin.getTriangles()) {
            numberOfBytes += calculateNumberOfBytes(triangle);
        }
        return  numberOfBytes;
    }

    /**
     * Write the Tin into the ByteBuffer
     * @param buffer The ByteBuffer
     * @param tin The Tin
     */
    private void putTin(ByteBuffer buffer, Tin tin) {
        putByteOrder(buffer);
        putGeometryType(buffer, GeometryType.Tin, tin.getDimension(), tin.getSrid());
        putSrid(buffer, tin.getSrid());
        buffer.putInt(tin.getTriangles().size());
        if (!tin.isEmpty()) {
            for(Triangle triangle : tin.getTriangles()) {
                putTriangle(buffer, triangle);
            }
        }
    }

    // Triangle

    /**
     * Write a Triangle to a hex String
     * @param triangle The Triangle
     * @return A hex String
     */
    public String writeToHex(Triangle triangle) {
        return toHex(write(triangle));
    }

    /**
     * Write a Triangle to a byte array
     * @param triangle The Triangle
     * @return A byte array
     */
    public byte[] write(Triangle triangle) {
        ByteBuffer buffer = ByteBuffer.allocate(calculateNumberOfBytes(triangle));
        putTriangle(buffer, triangle);
        return buffer.array();
    }

    /**
     * Calculate the number of bytes necessary for the Triangle
     * @param triangle The Triangle
     * @return The number of bytes necessary for the Triangle
     */
    private int calculateNumberOfBytes(Triangle triangle) {
        int numberOfRings = 0;
        if (!triangle.isEmpty()) {
            numberOfRings = 1 + triangle.getInnerLinearRings().size();
        }
        return 4 +
                numberOfRings * 4 + // Number of coordinate for each ring
                calculateNumberOfBytes(triangle.getSrid()) +
                calculateNumberOfBytes(triangle.getNumberOfCoordinates(), triangle.getDimension());
    }

    /**
     * Write the Triangle into the ByteBuffer
     * @param buffer The ByteBuffer
     * @param triangle The Triangle
     */
    private void putTriangle(ByteBuffer buffer, Triangle triangle) {
        putByteOrder(buffer);
        putGeometryType(buffer, GeometryType.Triangle, triangle.getDimension(), triangle.getSrid());
        putSrid(buffer, triangle.getSrid());
        // Number of Rings
        int numberOfRings = 0;
        if (!triangle.isEmpty()) {
            numberOfRings = 1 + triangle.getInnerLinearRings().size();
        }
        buffer.putInt(numberOfRings);
        // Rings
        if (!triangle.isEmpty()) {
            putCoordinates(buffer, triangle.getOuterLinearRing().getCoordinates());
            for(LinearRing ring : triangle.getInnerLinearRings()) {
                putCoordinates(buffer, ring.getCoordinates());
            }
        }
    }

    // PolyhedralSurface

    /**
     * Write a PolyhedralSurface to a hex String
     * @param polyHedralSurface The PolyhedralSurface
     * @return A hex String
     */
    public String writeToHex(PolyHedralSurface polyHedralSurface) {
        return toHex(write(polyHedralSurface));
    }

    /**
     * Write a PolyhedralSurface to a byte array
     * @param polyHedralSurface The PolyhedralSurface
     * @return A byte array
     */
    public byte[] write(PolyHedralSurface polyHedralSurface) {
        ByteBuffer buffer = ByteBuffer.allocate(calculateNumberOfBytes(polyHedralSurface));
        putPolyHedralSurface(buffer, polyHedralSurface);
        return buffer.array();
    }

    /**
     * Calculate the number of bytes necessary for the PolyhedralSurface
     * @param polyHedralSurface The PolyhedralSurface
     * @return The number of bytes necessary for the PolyhedralSurface
     */
    private int calculateNumberOfBytes(PolyHedralSurface polyHedralSurface) {
        int numberOfBytes = 4 + calculateNumberOfBytes(polyHedralSurface.getSrid());
        for (Polygon polygon : polyHedralSurface.getPolygons()) {
            numberOfBytes += calculateNumberOfBytes(polygon);
        }
        return  numberOfBytes;
    }

    /**
     * Write the PolyhedralSurface into the ByteBuffer
     * @param buffer The ByteBuffer
     * @param polyHedralSurface The PolyhedralSurface
     */
    private void putPolyHedralSurface(ByteBuffer buffer, PolyHedralSurface polyHedralSurface) {
        putByteOrder(buffer);
        putGeometryType(buffer, GeometryType.PolyHedralSurface, polyHedralSurface.getDimension(), polyHedralSurface.getSrid());
        putSrid(buffer, polyHedralSurface.getSrid());
        buffer.putInt(polyHedralSurface.getPolygons().size());
        if (!polyHedralSurface.isEmpty()) {
            for (Polygon polygon : polyHedralSurface.getPolygons()) {
                putPolygon(buffer, polygon);
            }
        }
    }

    /**
     * The hex array
     */
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * Convert an array of bytes to a hex String.
     * @param bytes The array of bytes
     * @return The hex String
     */
    private static String toHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
