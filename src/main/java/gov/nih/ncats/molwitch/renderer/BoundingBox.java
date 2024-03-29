/*
 * NCATS-MOLWITCH-RENDERER
 *
 * Copyright 2024 NIH/NCATS
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package gov.nih.ncats.molwitch.renderer;

import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


import gov.nih.ncats.molwitch.Atom;
import gov.nih.ncats.molwitch.AtomCoordinates;
import gov.nih.ncats.molwitch.Chemical;
import gov.nih.ncats.molwitch.SGroup;

class BoundingBox {
    public static Rectangle2D computeBoundingBoxFor(Chemical c) {
        return computeBoundingBoxFor(c, 0);
    }
	public static Rectangle2D computeBoundingBoxFor(Chemical c, double padding) {
    	int atomCount = c.getAtomCount();
		List<Supplier<AtomCoordinates>> list = new ArrayList<>(atomCount);
    	if(atomCount ==1){
    		//GSRS-1712 add more fudge factor if there is only 1 atom
			//to deal with implicit Hs and charges

			Atom a = c.getAtom(0);
			list.add(a::getAtomCoordinates);
			AtomCoordinates coords = a.getAtomCoordinates();
			double x = coords.getX();
			double y = coords.getY();
			//adding 1 to all directions might be a little overkill
			//there is math done downstream to compute the center and do the affine transforms
			//so this should keep the center as the actual atom postion
			//while adding some padding for the image so we don't get too zoomed in.
			list.add(()-> AtomCoordinates.valueOf(x-1, y-1));
			list.add(()-> AtomCoordinates.valueOf(x+1, y+1));
		}else {

			for (Atom a : c.getAtoms()) {
				list.add(a::getAtomCoordinates);
			}
		}
        //if sgroups have brackets and we trust them add those too
        for(SGroup sgroup : c.getSGroups()){
            if(sgroup.hasBrackets() && sgroup.bracketsTrusted()){
                for(SGroup.SGroupBracket bracket : sgroup.getBrackets()){
                    list.add(bracket::getPoint1);
                    list.add(bracket::getPoint2);
                }
            }
        }
		return computePaddedBoundingBoxFromSuppliers(list, padding);
	}
	public static Rectangle2D computeBoundingBoxFor(Iterable<Atom> c) {
		return computePaddedBoundingBoxFor(c, 0);
	}
	public static Rectangle2D computePaddedBoundingBoxFor(Iterable<Atom> c, double padding) {
	    List<Supplier<AtomCoordinates>> list = new ArrayList<>();
	    for(Atom a: c){
	        list.add(a::getAtomCoordinates);
        }
        return computePaddedBoundingBoxFromSuppliers(list, padding);
    }

	private static Rectangle2D computePaddedBoundingBoxFromSuppliers(Iterable<Supplier<AtomCoordinates>> c, double padding) {
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;

		for(Supplier<AtomCoordinates> a :c) {
			AtomCoordinates coords = a.get();

			double x = coords.getX();
			double y = coords.getY();
			if(x < minX) {
				minX = x;
			}
			if(x > maxX) {
				maxX =x;
			}
			if( y < minY) {
				minY=y;
			}
			if( y > maxY) {
				maxY=y;
			}
		}
		double doublePadding = padding*2;
		return new Rectangle2D.Double(minX-padding, minY-padding, (maxX-minX)+doublePadding, (maxY-minY)+doublePadding);
	}
    /*private static Rectangle2D computePaddedBoundingBoxFromSuppliers(Iterable<Supplier<AtomCoordinates>> c, double padding) {
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		
		for(Supplier<AtomCoordinates> a :c) {
			AtomCoordinates coords = a.get();
			
			double x = coords.getX();
			double y = coords.getY();
			if(x < minX) {
				minX = x;
			}
			if(x > maxX) {
				maxX =x;
			}
			if( y < minY) {
				minY=y;
			}
			if( y > maxY) {
				maxY=y;
			}
		}
		double xSpread= maxX-minX;
		double ySpread= maxY-minY;
		double avgSpread = (xSpread+ySpread)/2;
		//System.out.printf("xSpread: %f, ySpread: %f, avg: %f\n", xSpread, ySpread, avgSpread);
		double xRatio = ySpread==0 ? 1 : xSpread/ySpread;
		double yRatio = xSpread==0 ? 1 : ySpread/xSpread;
		//System.out.printf("xRatio: %f, yRatio: %f\n", xRatio, yRatio);
		double scale = 0.7;
		xRatio = xRatio * scale;
		yRatio= yRatio * scale;

		double factor=1.1;
		double xPadding = Math.max(xRatio, 0.0); //(xSpread/avgSpread)*factor
		double yPadding = Math.max(yRatio, 0.0);//(ySpread/avgSpread)*factor
		//System.out.printf("xPadding: %f, yPadding: %f\n", xPadding, yPadding);

		double doublePadding = padding*2;
		double lowX =minX-xPadding;
		double lowY=minY-yPadding;
		double highX=(maxX-minX)+(2*xPadding);
		double highY=(maxY-minY)+(2*yPadding);
		return new Rectangle2D.Double(lowX, lowY, highX, highY);
	}*/

	public static Rectangle2D computePaddedBoundingBoxForCoordinates(Iterable<AtomCoordinates> c, double padding) {
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		
			for(AtomCoordinates coords : c){
			
			double x = coords.getX();
			double y = coords.getY();
			
			if(x < minX) {
				minX = x;
			}
			if(x > maxX) {
				maxX =x;
			}
			if( y < minY) {
				minY=y;
			}
			if( y > maxY) {
				maxY=y;
			}
		}
		double doublePadding = padding*2;
		return new Rectangle2D.Double(minX-padding, minY-padding,
				Math.abs((maxX-minX)+doublePadding), 
				Math.abs((maxY-minY)+doublePadding));
	}
	
	public static List<java.awt.geom.Point2D> computeConvexHullFor(Collection<java.awt.geom.Point2D> points){
		Point2D[] myPoints = points.stream()
								.filter(Objects::nonNull)
								.map(p-> new Point2D(p.getX(), p.getY()))
								.toArray(size-> new Point2D[size]);
		return convexHullFor(myPoints);
		
	}
	public static List<java.awt.geom.Point2D> computeConvexHullFor(Chemical c){
		Point2D[] points = new Point2D[c.getAtomCount()];
		int i=0;
		for(Atom a : c.getAtoms()) {
			AtomCoordinates coords = a.getAtomCoordinates();
			points[i++] = new Point2D(coords.getX(), coords.getY());
		}
		return convexHullFor(points);
		
	}

	private static List<java.awt.geom.Point2D> convexHullFor(Point2D[] points) {
		GrahamScan gs = new GrahamScan(points);
		return StreamSupport.stream(gs.hull().spliterator(), false)
						.map(p-> new java.awt.geom.Point2D.Double(p.x, p.y))
						.collect(Collectors.toList());
	}
	
	
	//The code below (with minor revisions) is from Algorithms 4th edition
	// by Segewick and Wayne
	
	/**
	 *  The {@code GrahamScan} data type provides methods for computing the 
	 *  convex hull of a set of <em>n</em> points in the plane.
	 *  <p>
	 *  The implementation uses the Graham-Scan convex hull algorithm.
	 *  It runs in O(<em>n</em> log <em>n</em>) time in the worst case
	 *  and uses O(<em>n</em>) extra memory.
	 *  <p>
	 *  For additional documentation, see <a href="https://algs4.cs.princeton.edu/99scientific">Section 9.9</a> of
	 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
	 *
	 *  @author Robert Sedgewick
	 *  @author Kevin Wayne
	 */
	private static class GrahamScan {
	    private Stack<Point2D> hull = new Stack<Point2D>();

	    /**
	     * Computes the convex hull of the specified array of points.
	     *
	     * @throws IllegalArgumentException if {@code points} is {@code null}
	     * @throws IllegalArgumentException if any entry in {@code points[]} is {@code null}
	     * @throws IllegalArgumentException if {@code points.length} is {@code 0}
	     */
	    public GrahamScan(Point2D[] a) {
	        if (a == null) throw new IllegalArgumentException("argument is null");
	        if (a.length == 0) throw new IllegalArgumentException("array is of length 0");

	        int n = a.length;
	        //katzelda- don't need a defensive copy
	        //since we just created it ourselves
	        // defensive copy
//	        
//	        Point2D[] a = new Point2D[n];
//	        for (int i = 0; i < n; i++) {
//	            if (points[i] == null)
//	                throw new IllegalArgumentException("points[" + i + "] is null");
//	            a[i] = points[i];
//	        }

	        // preprocess so that a[0] has lowest y-coordinate; break ties by x-coordinate
	        // a[0] is an extreme point of the convex hull
	        // (alternatively, could do easily in linear time)
	        Arrays.sort(a);

	        // sort by polar angle with respect to base point a[0],
	        // breaking ties by distance to a[0]
	        Arrays.sort(a, 1, n, a[0].polarOrder());

	        hull.push(a[0]);       // a[0] is first extreme point

	        // find index k1 of first point not equal to a[0]
	        int k1;
	        for (k1 = 1; k1 < n; k1++)
	            if (!a[0].equals(a[k1])) break;
	        if (k1 == n) return;        // all points equal

	        // find index k2 of first point not collinear with a[0] and a[k1]
	        int k2;
	        for (k2 = k1+1; k2 < n; k2++)
	            if (Point2D.ccw(a[0], a[k1], a[k2]) != 0) break;
	        hull.push(a[k2-1]);    // a[k2-1] is second extreme point

	        // Graham scan; note that a[n-1] is extreme point different from a[0]
	        for (int i = k2; i < n; i++) {
	            Point2D top = hull.pop();
	            while (Point2D.ccw(hull.peek(), top, a[i]) <= 0) {
	                top = hull.pop();
	            }
	            hull.push(top);
	            hull.push(a[i]);
	        }

	        assert isConvex();
	    }

	    /**
	     * Returns the extreme points on the convex hull in counterclockwise order.
	     *
	     * @return the extreme points on the convex hull in counterclockwise order
	     */
	    public Iterable<Point2D> hull() {
	        Stack<Point2D> s = new Stack<Point2D>();
	        for (Point2D p : hull) s.push(p);
	        return s;
	    }

	    // check that boundary of hull is strictly convex
	    private boolean isConvex() {
	        int n = hull.size();
	        if (n <= 2) return true;

	        Point2D[] points = new Point2D[n];
	        int k = 0;
	        for (Point2D p : hull()) {
	            points[k++] = p;
	        }

	        for (int i = 0; i < n; i++) {
	            if (Point2D.ccw(points[i], points[(i+1) % n], points[(i+2) % n]) <= 0) {
	                return false;
	            }
	        }
	        return true;
	    }
	}
	
	/******************************************************************************
	 *  Compilation:  javac Point2D.java
	 *  Execution:    java Point2D x0 y0 n
	 *  Dependencies: StdDraw.java StdRandom.java
	 *
	 *  Immutable point data type for points in the plane.
	 *
	 ******************************************************************************/



	/**
	 *  The {@code Point} class is an immutable data type to encapsulate a
	 *  two-dimensional point with real-value coordinates.
	 *  <p>
	 *  Note: in order to deal with the difference behavior of double and 
	 *  Double with respect to -0.0 and +0.0, the Point2D constructor converts
	 *  any coordinates that are -0.0 to +0.0.
	 *  <p>
	 *  For additional documentation, 
	 *  see <a href="https://algs4.cs.princeton.edu/12oop">Section 1.2</a> of 
	 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne. 
	 *
	 *  @author Robert Sedgewick
	 *  @author Kevin Wayne
	 */
	private static final class Point2D implements Comparable<Point2D> {

	    /**
	     * Compares two points by x-coordinate.
	     */
	    public static final Comparator<Point2D> X_ORDER = new XOrder();

	    /**
	     * Compares two points by y-coordinate.
	     */
	    public static final Comparator<Point2D> Y_ORDER = new YOrder();

	    /**
	     * Compares two points by polar radius.
	     */
	    public static final Comparator<Point2D> R_ORDER = new ROrder();

	    private final double x;    // x coordinate
	    private final double y;    // y coordinate

	    /**
	     * Initializes a new point (x, y).
	     * @param x the x-coordinate
	     * @param y the y-coordinate
	     * @throws IllegalArgumentException if either {@code x} or {@code y}
	     *    is {@code Double.NaN}, {@code Double.POSITIVE_INFINITY} or
	     *    {@code Double.NEGATIVE_INFINITY}
	     */
	    public Point2D(double x, double y) {
	        if (Double.isInfinite(x) || Double.isInfinite(y))
	            throw new IllegalArgumentException("Coordinates must be finite");
	        if (Double.isNaN(x) || Double.isNaN(y))
	            throw new IllegalArgumentException("Coordinates cannot be NaN");
	        if (x == 0.0) this.x = 0.0;  // convert -0.0 to +0.0
	        else          this.x = x;

	        if (y == 0.0) this.y = 0.0;  // convert -0.0 to +0.0
	        else          this.y = y;
	    }

	    /**
	     * Returns the x-coordinate.
	     * @return the x-coordinate
	     */
	    public double x() {
	        return x;
	    }

	    /**
	     * Returns the y-coordinate.
	     * @return the y-coordinate
	     */
	    public double y() {
	        return y;
	    }

	    /**
	     * Returns the polar radius of this point.
	     * @return the polar radius of this point in polar coordiantes: sqrt(x*x + y*y)
	     */
	    public double r() {
	        return Math.sqrt(x*x + y*y);
	    }

	    /**
	     * Returns the angle of this point in polar coordinates.
	     * @return the angle (in radians) of this point in polar coordiantes (between –&pi; and &pi;)
	     */
	    public double theta() {
	        return Math.atan2(y, x);
	    }

	    /**
	     * Returns the angle between this point and that point.
	     * @return the angle in radians (between –&pi; and &pi;) between this point and that point (0 if equal)
	     */
	    private double angleTo(Point2D that) {
	        double dx = that.x - this.x;
	        double dy = that.y - this.y;
	        return Math.atan2(dy, dx);
	    }

	    /**
	     * Returns true if a→b→c is a counterclockwise turn.
	     * @param a first point
	     * @param b second point
	     * @param c third point
	     * @return { -1, 0, +1 } if a→b→c is a { clockwise, collinear; counterclocwise } turn.
	     */
	    public static int ccw(Point2D a, Point2D b, Point2D c) {
	        double area2 = (b.x-a.x)*(c.y-a.y) - (b.y-a.y)*(c.x-a.x);
	        if      (area2 < 0) return -1;
	        else if (area2 > 0) return +1;
	        else                return  0;
	    }

	    /**
	     * Returns twice the signed area of the triangle a-b-c.
	     * @param a first point
	     * @param b second point
	     * @param c third point
	     * @return twice the signed area of the triangle a-b-c
	     */
	    public static double area2(Point2D a, Point2D b, Point2D c) {
	        return (b.x-a.x)*(c.y-a.y) - (b.y-a.y)*(c.x-a.x);
	    }

	    /**
	     * Returns the Euclidean distance between this point and that point.
	     * @param that the other point
	     * @return the Euclidean distance between this point and that point
	     */
	    public double distanceTo(Point2D that) {
	        double dx = this.x - that.x;
	        double dy = this.y - that.y;
	        return Math.sqrt(dx*dx + dy*dy);
	    }

	    /**
	     * Returns the square of the Euclidean distance between this point and that point.
	     * @param that the other point
	     * @return the square of the Euclidean distance between this point and that point
	     */
	    public double distanceSquaredTo(Point2D that) {
	        double dx = this.x - that.x;
	        double dy = this.y - that.y;
	        return dx*dx + dy*dy;
	    }

	    /**
	     * Compares two points by y-coordinate, breaking ties by x-coordinate.
	     * Formally, the invoking point (x0, y0) is less than the argument point (x1, y1)
	     * if and only if either {@code y0 < y1} or if {@code y0 == y1} and {@code x0 < x1}.
	     *
	     * @param  that the other point
	     * @return the value {@code 0} if this string is equal to the argument
	     *         string (precisely when {@code equals()} returns {@code true});
	     *         a negative integer if this point is less than the argument
	     *         point; and a positive integer if this point is greater than the
	     *         argument point
	     */
	    public int compareTo(Point2D that) {
	        if (this.y < that.y) return -1;
	        if (this.y > that.y) return +1;
	        if (this.x < that.x) return -1;
	        if (this.x > that.x) return +1;
	        return 0;
	    }

	    /**
	     * Compares two points by polar angle (between 0 and 2&pi;) with respect to this point.
	     *
	     * @return the comparator
	     */
	    public Comparator<Point2D> polarOrder() {
	        return new PolarOrder();
	    }

	    /**
	     * Compares two points by atan2() angle (between –&pi; and &pi;) with respect to this point.
	     *
	     * @return the comparator
	     */
	    public Comparator<Point2D> atan2Order() {
	        return new Atan2Order();
	    }

	    /**
	     * Compares two points by distance to this point.
	     *
	     * @return the comparator
	     */
	    public Comparator<Point2D> distanceToOrder() {
	        return new DistanceToOrder();
	    }

	    // compare points according to their x-coordinate
	    private static class XOrder implements Comparator<Point2D> {
	        public int compare(Point2D p, Point2D q) {
	            if (p.x < q.x) return -1;
	            if (p.x > q.x) return +1;
	            return 0;
	        }
	    }

	    // compare points according to their y-coordinate
	    private static class YOrder implements Comparator<Point2D> {
	        public int compare(Point2D p, Point2D q) {
	            if (p.y < q.y) return -1;
	            if (p.y > q.y) return +1;
	            return 0;
	        }
	    }

	    // compare points according to their polar radius
	    private static class ROrder implements Comparator<Point2D> {
	        public int compare(Point2D p, Point2D q) {
	            double delta = (p.x*p.x + p.y*p.y) - (q.x*q.x + q.y*q.y);
	            if (delta < 0) return -1;
	            if (delta > 0) return +1;
	            return 0;
	        }
	    }
	 
	    // compare other points relative to atan2 angle (bewteen -pi/2 and pi/2) they make with this Point
	    private class Atan2Order implements Comparator<Point2D> {
	        public int compare(Point2D q1, Point2D q2) {
	            double angle1 = angleTo(q1);
	            double angle2 = angleTo(q2);
	            if      (angle1 < angle2) return -1;
	            else if (angle1 > angle2) return +1;
	            else                      return  0;
	        }
	    }

	    // compare other points relative to polar angle (between 0 and 2pi) they make with this Point
	    private class PolarOrder implements Comparator<Point2D> {
	        public int compare(Point2D q1, Point2D q2) {
	            double dx1 = q1.x - x;
	            double dy1 = q1.y - y;
	            double dx2 = q2.x - x;
	            double dy2 = q2.y - y;

	            if      (dy1 >= 0 && dy2 < 0) return -1;    // q1 above; q2 below
	            else if (dy2 >= 0 && dy1 < 0) return +1;    // q1 below; q2 above
	            else if (dy1 == 0 && dy2 == 0) {            // 3-collinear and horizontal
	                if      (dx1 >= 0 && dx2 < 0) return -1;
	                else if (dx2 >= 0 && dx1 < 0) return +1;
	                else                          return  0;
	            }
	            else return -ccw(Point2D.this, q1, q2);     // both above or below

	            // Note: ccw() recomputes dx1, dy1, dx2, and dy2
	        }
	    }

	    // compare points according to their distance to this point
	    private class DistanceToOrder implements Comparator<Point2D> {
	        public int compare(Point2D p, Point2D q) {
	            double dist1 = distanceSquaredTo(p);
	            double dist2 = distanceSquaredTo(q);
	            if      (dist1 < dist2) return -1;
	            else if (dist1 > dist2) return +1;
	            else                    return  0;
	        }
	    }


	    /**       
	     * Compares this point to the specified point.
	     *       
	     * @param  other the other point
	     * @return {@code true} if this point equals {@code other};
	     *         {@code false} otherwise
	     */
	    @Override
	    public boolean equals(Object other) {
	        if (other == this) return true;
	        if (other == null) return false;
	        if (other.getClass() != this.getClass()) return false;
	        Point2D that = (Point2D) other;
	        return this.x == that.x && this.y == that.y;
	    }

	    /**
	     * Return a string representation of this point.
	     * @return a string representation of this point in the format (x, y)
	     */
	    @Override
	    public String toString() {
	        return "(" + x + ", " + y + ")";
	    }

	    /**
	     * Returns an integer hash code for this point.
	     * @return an integer hash code for this point
	     */
	    @Override
	    public int hashCode() {
	        int hashX = ((Double) x).hashCode();
	        int hashY = ((Double) y).hashCode();
	        return 31*hashX + hashY;
	    }

	   
	}
}
