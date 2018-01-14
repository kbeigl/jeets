package org.jeets.georouter;

import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JtsTest {

    private static final Logger LOG = LoggerFactory.getLogger(JtsTest.class);

    @Test
    public void testGeometryRelations() throws Exception {
        Polygon hvvPoly = (Polygon) createJtsGeometry("POLYGON ((9.989269158916906 53.57541694442838, 9.998318508390481 53.55786417233634, 10.037949531036517 53.562496767906936, 10.021498582439857 53.5451640563584, 10.00793733366056 53.54138991423747, 9.985634869615584 53.540103457327746, 9.970257661419355 53.54332802925086, 9.965966126995527 53.55373112988562, 9.989269158916906 53.57541694442838))");
        LOG.info(hvvPoly.toText());
        
        LineString track1 = (LineString) createJtsGeometry("LINESTRING (10.057684 53.56985, 10.046565 53.567647, 10.035504 53.564706)");
        LOG.info(track1.toText());
        relateGeometries(hvvPoly, track1);
        Assert.assertTrue(hvvPoly.disjoint(track1));
        
        LineString track2 = (LineString) createJtsGeometry("LINESTRING (10.027395 53.559529, 10.019024 53.556626, 10.009756 53.55206, 10.006214 53.549034, 10.000825 53.547669, 9.993471 53.552546)");
        LOG.info(track2.toText());
        relateGeometries(hvvPoly, track2);
        Assert.assertTrue(hvvPoly.contains(track2));
        
        LineString track3 = (LineString) createJtsGeometry("LINESTRING (9.989303 53.558853, 9.989055 53.572764, 9.988088 53.581794, 9.990741 53.588735)");
        LOG.info(track3.toText());
        relateGeometries(hvvPoly, track3);
        Assert.assertTrue(hvvPoly.intersects(track3));
    }

    private boolean relateGeometries(Geometry geoA, Geometry geoB) {
//        LOG.info("relate A " + geoA.toText());
//        LOG.info("    to B " + geoB.toText());
        LOG.info("           geoA       geoB");
        LOG.info("disjoint   " + geoA.disjoint(geoB)   + "\t" + geoB.disjoint(geoA));
        LOG.info("intersects " + geoA.intersects(geoB) + "\t" + geoB.intersects(geoA));
        LOG.info("contains   " + geoA.contains(geoB)   + "\t" + geoB.contains(geoA));
        LOG.info("touches    " + geoA.touches(geoB)    + "\t" + geoB.touches(geoA));
        LOG.info("crosses    " + geoA.crosses(geoB)    + "\t" + geoB.crosses(geoA));
        LOG.info("within     " + geoA.within(geoB)     + "\t" + geoB.within(geoA));
        LOG.info("overlaps   " + geoA.overlaps(geoB)   + "\t" + geoB.overlaps(geoA));
        LOG.info("covers     " + geoA.covers(geoB)     + "\t" + geoB.covers(geoA));
        LOG.info("coveredBy  " + geoA.coveredBy(geoB)  + "\t" + geoB.coveredBy(geoA));
//      relate(Geometry, String) - allows general check of relationship see dim9 page
//      relate(Geometry)
        return false;
    }

    private Geometry createJtsGeometry(String wktString) {
        Geometry geo = null;
        try {
            geo = new WKTReader().read(wktString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return geo;
    }
    
}
