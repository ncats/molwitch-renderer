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

import static gov.nih.ncats.molwitch.renderer.Graphics2DParent.*;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import java.util.Optional;

class Graphics2DTemp{
	
		private boolean _disabled=false;
	        private Rectangle2D.Double _bounds=null;
	
	
		public Graphics2D _delagate;
		public static class AWTGeomGenerator extends GeomGenerator{

			@Override
			public LineParent makeLine(double x1, double y1, double x2,
					double y2) {
				return new Line2DWrapper(new Line2D.Double(x1, y1, x2, y2));
			}

			@Override
			public Point2DParent makePoint(double x1, double y1) {
				return new Point2DWrapper(new Point2D.Double(x1, y1));
			}

			@Override
			public Rectangle2DParent makeRectangle(double x1, double y1,
					double w, double h) {
				return new Rectangle2DWrapper(new Rectangle2D.Double(x1, y1, w, h));
			}

			@Override
			public AffineTransformParent makeAffineTransform() {
				return new AffineTransformWrapper(new AffineTransform());
			}

			@Override
			public Ellipse2DParent makeEllipse(double x1, double y1, double w,
					double h) {
				return new Ellipse2DWrapper(new Ellipse2D.Double(x1, y1, w, h));
			}

			@Override
			public GeneralPathParent makeGeneralPath() {
				
				return new GeneralPathWrapper(new GeneralPath());
			}
			
		}
		public static class GeneralPathWrapper extends GeneralPathParent{
			GeneralPath gp;

			public GeneralPathWrapper(GeneralPath gp){
				this.gp=gp;
			}
			public final void append(PathIteratorParent pi, boolean connect) {
				gp.append(PathIteratorWrapper.toPathIterator(pi), connect);
			}

			public final void append(ShapeParent s, boolean connect) {
				gp.append(ShapeWrapper.toShape(s), connect);
			}

			public final Object clone() {
				return gp.clone();
			}

			public final void closePath() {
				gp.closePath();
			}

			public final boolean contains(double x, double y, double w, double h) {
				return gp.contains(x, y, w, h);
			}

			public final boolean contains(double x, double y) {
				return gp.contains(x, y);
			}

			public final Shape createTransformedShape(AffineTransform at) {
				return gp.createTransformedShape(at);
			}

			public final void curveTo(double x1, double y1, double x2,
					double y2, double x3, double y3) {
				gp.curveTo(x1, y1, x2, y2, x3, y3);
			}

			public final void curveTo(float x1, float y1, float x2, float y2,
					float x3, float y3) {
				gp.curveTo(x1, y1, x2, y2, x3, y3);
			}

			public boolean equals(Object obj) {
				return gp.equals(obj);
			}

			public final Rectangle2DParent getBounds() {
				return new Rectangle2DWrapper(gp.getBounds());
			}

			public final Rectangle2DParent getBounds2D() {
				return new Rectangle2DWrapper(gp.getBounds2D());
			}

			public final Point2DParent getCurrentPoint() {
				return new Point2DWrapper(gp.getCurrentPoint());
			}

			public PathIterator getPathIterator(AffineTransform at,
					double flatness) {
				return gp.getPathIterator(at, flatness);
			}

			public PathIterator getPathIterator(AffineTransform at) {
				return gp.getPathIterator(at);
			}

			public final int getWindingRule() {
				return gp.getWindingRule();
			}

			public int hashCode() {
				return gp.hashCode();
			}

			public final boolean intersects(double x, double y, double w,
					double h) {
				return gp.intersects(x, y, w, h);
			}

			public final boolean intersects(Rectangle2D r) {
				return gp.intersects(r);
			}

			public final void lineTo(double x, double y) {
				gp.lineTo(x, y);
			}

			public final void lineTo(float x, float y) {
				gp.lineTo(x, y);
			}

			public final void moveTo(double x, double y) {
				gp.moveTo(x, y);
			}

			public final void moveTo(float x, float y) {
				gp.moveTo(x, y);
			}

			public final void quadTo(double x1, double y1, double x2, double y2) {
				gp.quadTo(x1, y1, x2, y2);
			}

			public final void quadTo(float x1, float y1, float x2, float y2) {
				gp.quadTo(x1, y1, x2, y2);
			}

			public final void reset() {
				gp.reset();
			}

			public final void setWindingRule(int rule) {
				gp.setWindingRule(rule);
			}

			public String toString() {
				return gp.toString();
			}

			public final void transform(AffineTransform at) {
				gp.transform(at);
			}
			@Override
			public ShapeParent createTransformedShape(AffineTransformParent at) {
				return new ShapeWrapper(gp.createTransformedShape(AffineTransformWrapper.toAffineTransform(at)));
			}
			@Override
			public void transform(AffineTransformParent at) {
				gp.transform(AffineTransformWrapper.toAffineTransform(at));
			}
			@Override
			public PathIteratorParent getPathIterator(AffineTransformParent arg0) {
				return new PathIteratorWrapper(gp.getPathIterator(AffineTransformWrapper.toAffineTransform(arg0)));
			}
			@Override
			public PathIteratorParent getPathIterator(
					AffineTransformParent arg0, double arg1) {
				return new PathIteratorWrapper(gp.getPathIterator(AffineTransformWrapper.toAffineTransform(arg0),arg1));
			}

		}

		public static class Line2DWrapper extends LineParent{
			Line2D lin;

			public Line2DWrapper(Line2D l){
				lin=l;
			}
			public Object clone() {
				return new Line2DWrapper((Line2D) lin.clone());
			}

			public boolean contains(double x, double y, double w, double h) {
				return lin.contains(x, y, w, h);
			}

			public boolean contains(double x, double y) {
				return lin.contains(x, y);
			}

			public boolean contains(Point2D p) {
				return lin.contains(p);
			}

			public boolean contains(Rectangle2D r) {
				return lin.contains(r);
			}

			public boolean equals(Object obj) {
				return lin.equals(obj);
			}

			public Rectangle2DParent getBounds() {
				return new Rectangle2DWrapper(lin.getBounds());
			}

			public Rectangle2DParent getBounds2D() {
				return new Rectangle2DWrapper(lin.getBounds());
			}

			public Point2DParent getP1() {
				return new Point2DWrapper(lin.getP1());
			}

			public Point2DParent getP2() {
				return new Point2DWrapper(lin.getP2());
			}

			public PathIterator getPathIterator(AffineTransform at,
					double flatness) {
				return lin.getPathIterator(at, flatness);
			}

			public PathIterator getPathIterator(AffineTransform at) {
				return lin.getPathIterator(at);
			}

			public double getX1() {
				return lin.getX1();
			}

			public double getX2() {
				return lin.getX2();
			}

			public double getY1() {
				return lin.getY1();
			}

			public double getY2() {
				return lin.getY2();
			}

			public int hashCode() {
				return lin.hashCode();
			}

			public boolean intersects(double x, double y, double w, double h) {
				return lin.intersects(x, y, w, h);
			}

			public boolean intersects(Rectangle2D r) {
				return lin.intersects(r);
			}

			public boolean intersectsLine(double x1, double y1, double x2,
					double y2) {
				return lin.intersectsLine(x1, y1, x2, y2);
			}

			public boolean intersectsLine(Line2D l) {
				return lin.intersectsLine(l);
			}

			public double ptLineDist(double px, double py) {
				return lin.ptLineDist(px, py);
			}

			public double ptLineDist(Point2D pt) {
				return lin.ptLineDist(pt);
			}

			public double ptLineDistSq(double px, double py) {
				return lin.ptLineDistSq(px, py);
			}

			public double ptLineDistSq(Point2D pt) {
				return lin.ptLineDistSq(pt);
			}

			public double ptSegDist(double px, double py) {
				return lin.ptSegDist(px, py);
			}

			public double ptSegDist(Point2D pt) {
				return lin.ptSegDist(pt);
			}

			public double ptSegDistSq(double px, double py) {
				return lin.ptSegDistSq(px, py);
			}

			public double ptSegDistSq(Point2D pt) {
				return lin.ptSegDistSq(pt);
			}

			public int relativeCCW(double px, double py) {
				return lin.relativeCCW(px, py);
			}

			public int relativeCCW(Point2D p) {
				return lin.relativeCCW(p);
			}

			public void setLine(double x1, double y1, double x2, double y2) {
				lin.setLine(x1, y1, x2, y2);
			}

			public void setLine(Line2D l) {
				lin.setLine(l);
			}

			public void setLine(Point2D p1, Point2D p2) {
				lin.setLine(p1, p2);
			}

			public String toString() {
				return lin.toString();
			}

			@Override
			public PathIteratorParent getPathIterator(AffineTransformParent arg0) {
				
				return new PathIteratorWrapper(lin.getPathIterator(AffineTransformWrapper.toAffineTransform(arg0)));
			}

			@Override
			public PathIteratorParent getPathIterator(
					AffineTransformParent arg0, double arg1) {
				return new PathIteratorWrapper(lin.getPathIterator(AffineTransformWrapper.toAffineTransform(arg0),arg1));
			}
			@Override
			public boolean intersectsLine(LineParent startLine) {
				return lin.intersectsLine(startLine.getX1(), startLine.getY1(), startLine.getX2(), startLine.getY2());
			}
			
		}
		public static class Point2DWrapper extends Point2DParent{
			Point2D _pt;
			public Point2DWrapper (Point2D pt){
				_pt=pt;
			}
			public Object clone() {
				return new Point2DWrapper((Point2D)(_pt.clone()));
			}

			public double distance(double px, double py) {
				return _pt.distance(px, py);
			}

			public double distance(Point2D pt) {
				return pt.distance(pt);
			}

			public double distanceSq(double px, double py) {
				return _pt.distanceSq(px, py);
			}

			public double distanceSq(Point2D pt) {
				return pt.distanceSq(pt);
			}

			public boolean equals(Object obj) {
				return _pt.equals(obj);
			}

			public double getX() {
				return _pt.getX();
			}

			public double getY() {
				return _pt.getY();
			}

			public int hashCode() {
				return _pt.hashCode();
			}

			public void setLocation(double x, double y) {
				_pt.setLocation(x, y);
			}

			public void setLocation(Point2D p) {
				_pt.setLocation(p);
			}

			public String toString() {
				return _pt.toString();
			}
			
		}
		public static class Ellipse2DWrapper extends Ellipse2DParent{
			Ellipse2D _ellipse;

			public Ellipse2DWrapper(Ellipse2D el) {
				_ellipse=el;
			}

			public Object clone() {
				return _ellipse.clone();
			}

			public boolean contains(double x, double y, double w, double h) {
				return _ellipse.contains(x, y, w, h);
			}

			public boolean contains(double x, double y) {
				return _ellipse.contains(x, y);
			}

			public boolean contains(Point2D p) {
				return _ellipse.contains(p);
			}

			public boolean contains(Rectangle2D r) {
				return _ellipse.contains(r);
			}

			public boolean equals(Object obj) {
				return _ellipse.equals(obj);
			}

			public Rectangle2DParent getBounds() {
				return new Rectangle2DWrapper(_ellipse.getBounds());
			}

			public Rectangle2DParent getBounds2D() {
				return new Rectangle2DWrapper(_ellipse.getBounds2D());
			}

			public double getCenterX() {
				return _ellipse.getCenterX();
			}

			public double getCenterY() {
				return _ellipse.getCenterY();
			}

			public Rectangle2D getFrame() {
				return _ellipse.getFrame();
			}

			public double getHeight() {
				return _ellipse.getHeight();
			}

			public double getMaxX() {
				return _ellipse.getMaxX();
			}

			public double getMaxY() {
				return _ellipse.getMaxY();
			}

			public double getMinX() {
				return _ellipse.getMinX();
			}

			public double getMinY() {
				return _ellipse.getMinY();
			}

			
			public double getWidth() {
				return _ellipse.getWidth();
			}

			public double getX() {
				return _ellipse.getX();
			}

			public double getY() {
				return _ellipse.getY();
			}

			public int hashCode() {
				return _ellipse.hashCode();
			}

			public boolean intersects(double x, double y, double w, double h) {
				return _ellipse.intersects(x, y, w, h);
			}

			public boolean intersects(Rectangle2D r) {
				return _ellipse.intersects(r);
			}

			public boolean isEmpty() {
				return _ellipse.isEmpty();
			}

			public void setFrame(double x, double y, double w, double h) {
				_ellipse.setFrame(x, y, w, h);
			}

			public void setFrame(Point2D loc, Dimension2D size) {
				_ellipse.setFrame(loc, size);
			}

			public void setFrame(Rectangle2D r) {
				_ellipse.setFrame(r);
			}

			public void setFrameFromCenter(double centerX, double centerY,
					double cornerX, double cornerY) {
				_ellipse.setFrameFromCenter(centerX, centerY, cornerX, cornerY);
			}

			public void setFrameFromCenter(Point2D center, Point2D corner) {
				_ellipse.setFrameFromCenter(center, corner);
			}

			public void setFrameFromDiagonal(double x1, double y1, double x2,
					double y2) {
				_ellipse.setFrameFromDiagonal(x1, y1, x2, y2);
			}

			public void setFrameFromDiagonal(Point2D p1, Point2D p2) {
				_ellipse.setFrameFromDiagonal(p1, p2);
			}

			public String toString() {
				return _ellipse.toString();
			}

			@Override
			public Rectangle2DParent createIntersection(Rectangle2DParent r) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Rectangle2DParent createUnion(Rectangle2DParent r) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int outcode(double x, double y) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void setRect(double x, double y, double w, double h) {
				// TODO Auto-generated method stub
				
				
			}
			@Override
			public PathIteratorParent getPathIterator(AffineTransformParent arg0) {
				return new PathIteratorWrapper(this._ellipse.getPathIterator(AffineTransformWrapper.toAffineTransform(arg0)));
			}
			@Override
			public PathIteratorParent getPathIterator(
					AffineTransformParent arg0, double arg1) {
				return new PathIteratorWrapper(this._ellipse.getPathIterator(AffineTransformWrapper.toAffineTransform(arg0),arg1));
			}
		}
		public static class Rectangle2DWrapper extends Rectangle2DParent{
			Rectangle2D _rect;
			public Rectangle2DWrapper(Rectangle2D r){
				this._rect=r;
			}
			public void add(double newx, double newy) {
				_rect.add(newx, newy);
			}
			public void add(Point2D pt) {
				_rect.add(pt);
			}
			public void add(Rectangle2D r) {
				_rect.add(r);
			}

			public Object clone() {
				return _rect.clone();
			}

			public boolean contains(double x, double y, double w, double h) {
				return _rect.contains(x, y, w, h);
			}

			public boolean contains(double x, double y) {
				return _rect.contains(x, y);
			}

			public boolean contains(Point2D p) {
				return _rect.contains(p);
			}

			public boolean contains(Rectangle2D r) {
				return _rect.contains(r);
			}

			public Rectangle2D createIntersection(Rectangle2D r) {
				return _rect.createIntersection(r);
			}

			public Rectangle2D createUnion(Rectangle2D r) {
				return _rect.createUnion(r);
			}

			public boolean equals(Object obj) {
				return _rect.equals(obj);
			}

			public double getCenterX() {
				return _rect.getCenterX();
			}

			public double getCenterY() {
				return _rect.getCenterY();
			}

			public Rectangle2D getFrame() {
				return _rect.getFrame();
			}

			public double getHeight() {
				return _rect.getHeight();
			}

			public double getMaxX() {
				return _rect.getMaxX();
			}

			public double getMaxY() {
				return _rect.getMaxY();
			}

			public double getMinX() {
				return _rect.getMinX();
			}

			public double getMinY() {
				return _rect.getMinY();
			}

			public PathIterator getPathIterator(AffineTransform at,
					double flatness) {
				return _rect.getPathIterator(at, flatness);
			}

			public PathIterator getPathIterator(AffineTransform at) {
				return _rect.getPathIterator(at);
			}

			public double getWidth() {
				return _rect.getWidth();
			}

			public double getX() {
				return _rect.getX();
			}

			public double getY() {
				return _rect.getY();
			}

			public int hashCode() {
				return _rect.hashCode();
			}

			public boolean intersects(double x, double y, double w, double h) {
				return _rect.intersects(x, y, w, h);
			}

			public boolean intersects(Rectangle2D r) {
				return _rect.intersects(r);
			}

			public boolean intersectsLine(double x1, double y1, double x2,
					double y2) {
				return _rect.intersectsLine(x1, y1, x2, y2);
			}

			public boolean intersectsLine(Line2D l) {
				return _rect.intersectsLine(l);
			}

			public boolean isEmpty() {
				return _rect.isEmpty();
			}

			public int outcode(double x, double y) {
				return _rect.outcode(x, y);
			}

			public int outcode(Point2D p) {
				return _rect.outcode(p);
			}

			public void setFrame(double x, double y, double w, double h) {
				_rect.setFrame(x, y, w, h);
			}

			public void setFrame(Point2D loc, Dimension2D size) {
				_rect.setFrame(loc, size);
			}

			public void setFrame(Rectangle2D r) {
				_rect.setFrame(r);
			}

			public void setFrameFromCenter(double centerX, double centerY,
					double cornerX, double cornerY) {
				_rect.setFrameFromCenter(centerX, centerY, cornerX, cornerY);
			}

			public void setFrameFromCenter(Point2D center, Point2D corner) {
				_rect.setFrameFromCenter(center, corner);
			}

			public void setFrameFromDiagonal(double arg0, double arg1,
					double arg2, double arg3) {
				_rect.setFrameFromDiagonal(arg0, arg1, arg2, arg3);
			}

			public void setFrameFromDiagonal(Point2D p1, Point2D p2) {
				_rect.setFrameFromDiagonal(p1, p2);
			}

			public void setRect(double x, double y, double w, double h) {
				_rect.setRect(x, y, w, h);
			}

			public void setRect(Rectangle2D r) {
				_rect.setRect(r);
			}

			public String toString() {
				return _rect.toString();
			}
			@Override
			public Rectangle2DParent createIntersection(Rectangle2DParent r) {
				return new Rectangle2DWrapper(this._rect.createIntersection(toRectangle2D(r)));
			}
			@Override
			public Rectangle2DParent createUnion(Rectangle2DParent r) {
				return new Rectangle2DWrapper(this._rect.createUnion(toRectangle2D(r)));
			}
			@Override
			public Rectangle2DParent getBounds2D() {
				return new Rectangle2DWrapper(this._rect.getBounds2D());
			}
			@Override
			public PathIteratorParent getPathIterator(AffineTransformParent arg0) {
				return new PathIteratorWrapper(this._rect.getPathIterator(AffineTransformWrapper.toAffineTransform(arg0)));
			}
			@Override
			public PathIteratorParent getPathIterator(
					AffineTransformParent arg0, double arg1) {
				return new PathIteratorWrapper(this._rect.getPathIterator(AffineTransformWrapper.toAffineTransform(arg0),arg1));
			}
			public static Rectangle2D toRectangle2D(Rectangle2DParent rp){
				return new Rectangle2D.Double(rp.getX(), rp.getY(), rp.getWidth(), rp.getHeight());
			}
		}
		public static class PathIteratorWrapper extends PathIteratorParent{
			PathIterator _pi;
			public PathIteratorWrapper(PathIterator pi){
				this._pi=pi;
			}
			@Override
			public int currentSegment(double[] coords) {
				return _pi.currentSegment(coords);
			}
			@Override
			public int currentSegment(float[] coords) {
				return _pi.currentSegment(coords);
			}
			@Override
			public int getWindingRule() {
				return _pi.getWindingRule();
			}
			@Override
			public boolean isDone() {
				return _pi.isDone();
			}
			@Override
			public void next() {
				_pi.next();
			}
			public static PathIterator toPathIterator(
					PathIteratorParent pathIterator) {
				if(pathIterator instanceof PathIteratorWrapper){
					return ((PathIteratorWrapper)pathIterator)._pi;
				}else{
					return new PathIteratorINV(pathIterator);
				}
			}
			
			public static class PathIteratorINV implements PathIterator{
				PathIteratorParent _pip;
				public PathIteratorINV(PathIteratorParent pip){
					_pip=pip;
				}
				@Override
				public int currentSegment(float[] coords) {
					return _pip.currentSegment(coords);					
				}

				@Override
				public int currentSegment(double[] coords) {
					return _pip.currentSegment(coords);		
				}

				@Override
				public int getWindingRule() {
					return _pip.getWindingRule();		
				}

				@Override
				public boolean isDone() {
					return _pip.isDone();	
				}

				@Override
				public void next() {
					_pip.next();
				}
				
			}
			
		}
		public static class AffineTransformWrapper extends AffineTransformParent{
			AffineTransform _at;
			public Object clone() {
				return _at.clone();
			}
			public void concatenate(AffineTransform Tx) {
				_at.concatenate(Tx);
			}
			public AffineTransformWrapper createInverse() throws Exception{
				return new AffineTransformWrapper(_at.createInverse());
			}
			
			public void deltaTransform(double[] srcPts, int srcOff,
					double[] dstPts, int dstOff, int numPts) {
				_at.deltaTransform(srcPts, srcOff, dstPts, dstOff, numPts);
			}
			public Point2D deltaTransform(Point2D ptSrc, Point2D ptDst) {
				return _at.deltaTransform(ptSrc, ptDst);
			}
			public boolean equals(Object obj) {
				return _at.equals(obj);
			}
			public double getDeterminant() {
				return _at.getDeterminant();
			}
			public void getMatrix(double[] flatmatrix) {
				_at.getMatrix(flatmatrix);
			}
			public double getScaleX() {
				return _at.getScaleX();
			}
			public double getScaleY() {
				return _at.getScaleY();
			}
			public double getShearX() {
				return _at.getShearX();
			}
			public double getShearY() {
				return _at.getShearY();
			}
			public double getTranslateX() {
				return _at.getTranslateX();
			}
			public double getTranslateY() {
				return _at.getTranslateY();
			}
			public int getType() {
				return _at.getType();
			}
			public int hashCode() {
				return _at.hashCode();
			}
			public void inverseTransform(double[] srcPts, int srcOff,
					double[] dstPts, int dstOff, int numPts)
					throws NoninvertibleTransformException {
				_at.inverseTransform(srcPts, srcOff, dstPts, dstOff, numPts);
			}
			public Point2D inverseTransform(Point2D ptSrc, Point2D ptDst)
					throws NoninvertibleTransformException {
				return _at.inverseTransform(ptSrc, ptDst);
			}
			public void invert() throws NoninvertibleTransformException {
				_at.invert();
			}
			public boolean isIdentity() {
				return _at.isIdentity();
			}
			public void preConcatenate(AffineTransform Tx) {
				_at.preConcatenate(Tx);
			}
			public void quadrantRotate(int numquadrants, double anchorx,
					double anchory) {
				_at.quadrantRotate(numquadrants, anchorx, anchory);
			}
			public void quadrantRotate(int numquadrants) {
				_at.quadrantRotate(numquadrants);
			}
			public void rotate(double vecx, double vecy, double anchorx,
					double anchory) {
				_at.rotate(vecx, vecy, anchorx, anchory);
			}
			public void rotate(double theta, double anchorx, double anchory) {
				_at.rotate(theta, anchorx, anchory);
			}
			public void rotate(double vecx, double vecy) {
				_at.rotate(vecx, vecy);
			}
			public void rotate(double theta) {
				_at.rotate(theta);
			}
			public void scale(double sx, double sy) {
				_at.scale(sx, sy);
			}
			public void setToIdentity() {
				_at.setToIdentity();
			}
			public void setToQuadrantRotation(int numquadrants, double anchorx,
					double anchory) {
				_at.setToQuadrantRotation(numquadrants, anchorx, anchory);
			}
			public void setToQuadrantRotation(int numquadrants) {
				_at.setToQuadrantRotation(numquadrants);
			}
			public void setToRotation(double vecx, double vecy, double anchorx,
					double anchory) {
				_at.setToRotation(vecx, vecy, anchorx, anchory);
			}
			public void setToRotation(double theta, double anchorx,
					double anchory) {
				_at.setToRotation(theta, anchorx, anchory);
			}
			public void setToRotation(double vecx, double vecy) {
				_at.setToRotation(vecx, vecy);
			}
			public void setToRotation(double theta) {
				_at.setToRotation(theta);
			}
			public void setToScale(double sx, double sy) {
				_at.setToScale(sx, sy);
			}
			public void setToShear(double shx, double shy) {
				_at.setToShear(shx, shy);
			}
			public void setToTranslation(double tx, double ty) {
				_at.setToTranslation(tx, ty);
			}
			public void setTransform(AffineTransform Tx) {
				_at.setTransform(Tx);
			}
			public void setTransform(double m00, double m10, double m01,
					double m11, double m02, double m12) {
				_at.setTransform(m00, m10, m01, m11, m02, m12);
			}
			public void shear(double shx, double shy) {
				_at.shear(shx, shy);
			}
			public String toString() {
				return _at.toString();
			}
			public void transform(double[] srcPts, int srcOff, double[] dstPts,
					int dstOff, int numPts) {
				_at.transform(srcPts, srcOff, dstPts, dstOff, numPts);
			}
			public void transform(double[] srcPts, int srcOff, float[] dstPts,
					int dstOff, int numPts) {
				_at.transform(srcPts, srcOff, dstPts, dstOff, numPts);
			}
			public void transform(float[] srcPts, int srcOff, double[] dstPts,
					int dstOff, int numPts) {
				_at.transform(srcPts, srcOff, dstPts, dstOff, numPts);
			}
			public void transform(float[] srcPts, int srcOff, float[] dstPts,
					int dstOff, int numPts) {
				_at.transform(srcPts, srcOff, dstPts, dstOff, numPts);
			}
			public Point2D transform(Point2D ptSrc, Point2D ptDst) {
				return _at.transform(ptSrc, ptDst);
			}
			public void transform(Point2D[] ptSrc, int srcOff, Point2D[] ptDst,
					int dstOff, int numPts) {
				_at.transform(ptSrc, srcOff, ptDst, dstOff, numPts);
			}
			public void translate(double tx, double ty) {
				_at.translate(tx, ty);
			}
			public AffineTransformWrapper(AffineTransform at){
				this._at=at;
			}
			public static AffineTransform toAffineTransform(AffineTransformParent ap){
				if(ap instanceof AffineTransformWrapper){
					return ((AffineTransformWrapper)ap)._at;
				}
				double[] mat=new double[6];
				ap.getMatrix(mat);
				return new AffineTransform(mat);
			}
			@Override
			public void concatenate(AffineTransformParent Tx) {
				this._at.concatenate(toAffineTransform(Tx));
			}
			@Override
			public Point2DParent deltaTransform(Point2DParent ptSrc,
					Point2DParent ptDst) {
				// TODO Auto-generated method stub
				return null;
			}
			@Override
			public Point2DParent inverseTransform(Point2DParent arg0,
					Point2DParent arg1) {
				// TODO Auto-generated method stub
				return null;
			}
			@Override
			public void preConcatenate(AffineTransformParent Tx) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public Point2DParent transform(Point2DParent ptSrc,
					Point2DParent ptDst) {
				// TODO Auto-generated method stub
				return null;
			}
			@Override
			public void transform(Point2DParent[] arg0, int arg1,
					Point2DParent[] arg2, int arg3, int arg4) {
				// TODO Auto-generated method stub
				
			}
			public ShapeParent createTransformedShape(ShapeParent pSrc) {
				return new ShapeWrapper(_at.createTransformedShape(ShapeWrapper.toShape(pSrc)));
			}
		}
		//public class Point2DParent
		public static class ShapeWrapper extends ShapeParent{
			Shape _s;
			public ShapeWrapper(Shape s){
				this._s=s;
				
				
			}
			public boolean contains(double x, double y, double w, double h) {
				return _s.contains(x, y, w, h);
			}
			public boolean contains(double x, double y) {
				return _s.contains(x, y);
			}
			public Rectangle2DParent getBounds2D() {
				return new Rectangle2DWrapper(_s.getBounds2D());
			}
			public PathIteratorParent getPathIterator(AffineTransformParent at,
					double flatness) {
				
				return new PathIteratorWrapper(_s.getPathIterator(AffineTransformWrapper.toAffineTransform(at),flatness));
			}
			public PathIteratorWrapper getPathIterator(AffineTransformParent at) {
				return new PathIteratorWrapper(_s.getPathIterator(AffineTransformWrapper.toAffineTransform(at)));
			}
			public boolean intersects(double x, double y, double w, double h) {
				return _s.intersects(x, y, w, h);
			}
			public static Shape toShape(ShapeParent sp){
				if(sp instanceof ShapeWrapper){
					return ((ShapeWrapper) sp)._s;
				}else{
					return new ShapeInv(sp);
				}
			}
			public static class ShapeInv implements Shape{
				ShapeParent _sp;
				public ShapeInv(ShapeParent sp){
					_sp=sp;
				}
				@Override
				public boolean contains(Point2D p) {
					return _sp.contains(p.getX(),p.getY());
				}
				@Override
				public boolean contains(Rectangle2D r) {
					return _sp.contains(r.getX(),r.getY(),r.getWidth(),r.getHeight());
				}

				@Override
				public boolean contains(double x, double y) {
					return _sp.contains(x,y);
				}

				@Override
				public boolean contains(double x, double y, double w, double h) {
					return _sp.contains(x,y,w,h);
				}

				@Override
				public Rectangle getBounds() {
					return (Rectangle) Rectangle2DWrapper.toRectangle2D(_sp.getBounds());
				}

				@Override
				public Rectangle2D getBounds2D() {
					return Rectangle2DWrapper.toRectangle2D(_sp.getBounds());
				}

				@Override
				public PathIterator getPathIterator(AffineTransform at) {
					return PathIteratorWrapper.toPathIterator(_sp.getPathIterator(new AffineTransformWrapper(at)));
				}

				@Override
				public PathIterator getPathIterator(AffineTransform at,
						double flatness) {
					return PathIteratorWrapper.toPathIterator(_sp.getPathIterator(new AffineTransformWrapper(at),flatness));
				}

				@Override
				public boolean intersects(Rectangle2D r) {
					return _sp.intersects(new Rectangle2DWrapper(r));
				}

				@Override
				public boolean intersects(double x, double y, double w, double h) {
					return _sp.intersects(x, y, w, h);
				}
				
			}
		}
		public Graphics2DTemp(Graphics2D g2){
			_delagate=g2;
		}
	
	        public void disable(){
			this._disabled=true;
		}
		
	        public void enable(){
			this._disabled=false;
		}
		public void updateBounds(Rectangle2D r){
			Rectangle2D.Double rd=new Rectangle2D.Double(r.getX(),r.getY(),r.getWidth(),r.getHeight());
			if(_bounds==null){
				_bounds=rd;
			}else{
				//increase bounds
				_bounds.add(rd);
			}
		}
		public void clearBounds(){
			_bounds=null;
		}
		public Optional<Rectangle2D.Double> getBounds(){
			return Optional.ofNullable(_bounds);
		}
		
		public void addRenderingHints(Map<?, ?> hints) {
			_delagate.addRenderingHints(hints);
		}

		public void clearRect(int x, int y, int width, int height) {
			_delagate.clearRect(x, y, width, height);
		}

		public void clip(Shape s) {
			_delagate.clip(s);
		}

		public void clipRect(int x, int y, int width, int height) {
			_delagate.clipRect(x, y, width, height);
		}

		public void copyArea(int x, int y, int width, int height, int dx, int dy) {
			_delagate.copyArea(x, y, width, height, dx, dy);
		}

		public Graphics create() {
			return _delagate.create();
		}

		public Graphics create(int x, int y, int width, int height) {
			return _delagate.create(x, y, width, height);
		}

		public void dispose() {
			_delagate.dispose();
		}

		public void drawd(Shape s) {
		
			updateBounds(s.getBounds2D());
			if(!_disabled){
				_delagate.draw(s);
			}
		}
		public void drawP(ShapeParent s) {
			drawd(ShapeWrapper.toShape(s));
		}

		public void draw3DRect(int x, int y, int width, int height,
				boolean raised) {
			_delagate.draw3DRect(x, y, width, height, raised);
		}

		public void drawArc(int x, int y, int width, int height,
				int startAngle, int arcAngle) {
			_delagate.drawArc(x, y, width, height, startAngle, arcAngle);
		}

		public void drawBytes(byte[] data, int offset, int length, int x, int y) {
			_delagate.drawBytes(data, offset, length, x, y);
		}

		public void drawChars(char[] data, int offset, int length, int x, int y) {
			_delagate.drawChars(data, offset, length, x, y);
		}

		public void drawGlyphVector(GlyphVector g, float x, float y) {
			//updateBounds(g.getVisualBounds());
			Rectangle2D rr = g.getVisualBounds();
			updateBounds(new Rectangle2D.Double(rr.getMinX()+x, rr.getMinY()+y, rr.getWidth(), rr.getHeight()));
			if(!_disabled){
				_delagate.drawGlyphVector(g, x, y);
			}
		}

		public void drawImage(BufferedImage img, BufferedImageOp op, int x,
				int y) {
			if(!_disabled){
				_delagate.drawImage(img, op, x, y);
			}
		}

		public boolean drawImage(Image img, AffineTransform xform,
				ImageObserver obs) {
			if(!_disabled){
				return _delagate.drawImage(img, xform, obs);
			}
			return false;
		}

		public boolean drawImage(Image img, int x, int y, Color bgcolor,
				ImageObserver observer) {
			return _delagate.drawImage(img, x, y, bgcolor, observer);
		}

		public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
			return _delagate.drawImage(img, x, y, observer);
		}

		public boolean drawImage(Image img, int x, int y, int width,
				int height, Color bgcolor, ImageObserver observer) {
			return _delagate.drawImage(img, x, y, width, height, bgcolor,
					observer);
		}

		public boolean drawImage(Image img, int x, int y, int width,
				int height, ImageObserver observer) {
			return _delagate.drawImage(img, x, y, width, height, observer);
		}

		public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
				int sx1, int sy1, int sx2, int sy2, Color bgcolor,
				ImageObserver observer) {
			return _delagate.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2,
					sy2, bgcolor, observer);
		}

		public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
				int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
			return _delagate.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2,
					sy2, observer);
		}

		public void drawLine(int x1, int y1, int x2, int y2) {
			_delagate.drawLine(x1, y1, x2, y2);
		}

		public void drawOval(int x, int y, int width, int height) {
			_delagate.drawOval(x, y, width, height);
		}

		public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
			_delagate.drawPolygon(xPoints, yPoints, nPoints);
		}

		public void drawPolygon(Polygon p) {
			_delagate.drawPolygon(p);
		}

		public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
			_delagate.drawPolyline(xPoints, yPoints, nPoints);
		}

		public void drawRect(int x, int y, int width, int height) {
			_delagate.drawRect(x, y, width, height);
		}

		public void drawRenderableImage(RenderableImage img,
				AffineTransform xform) {
			_delagate.drawRenderableImage(img, xform);
		}

		public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
			_delagate.drawRenderedImage(img, xform);
		}

		public void drawRoundRect(int x, int y, int width, int height,
				int arcWidth, int arcHeight) {
			_delagate.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
		}

		public void drawString(AttributedCharacterIterator iterator, float x,
				float y) {
			_delagate.drawString(iterator, x, y);
		}

		public void drawString(AttributedCharacterIterator iterator, int x,
				int y) {
			_delagate.drawString(iterator, x, y);
		}

		public void drawString(String str, float x, float y) {
			_delagate.drawString(str, x, y);
		}

		public void drawString(String str, int x, int y) {
			_delagate.drawString(str, x, y);
		}

		public boolean equals(Object obj) {
			return _delagate.equals(obj);
		}

		public void fillP(ShapeParent s) {
			fill(ShapeWrapper.toShape(s));
		}
		private void fill(Shape s) {
			updateBounds(s.getBounds2D());
			if(!_disabled){
				_delagate.fill(s);
			}
		}

		public void fill3DRect(int x, int y, int width, int height,
				boolean raised) {
			_delagate.fill3DRect(x, y, width, height, raised);
		}

		public void fillArc(int x, int y, int width, int height,
				int startAngle, int arcAngle) {
			_delagate.fillArc(x, y, width, height, startAngle, arcAngle);
		}

		public void fillOval(int x, int y, int width, int height) {
			_delagate.fillOval(x, y, width, height);
		}

		public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
			_delagate.fillPolygon(xPoints, yPoints, nPoints);
		}

		public void fillPolygon(Polygon p) {
			_delagate.fillPolygon(p);
		}

		public void fillRect(int x, int y, int width, int height) {
			_delagate.fillRect(x, y, width, height);
		}

		public void fillRoundRect(int x, int y, int width, int height,
				int arcWidth, int arcHeight) {
			_delagate.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
		}

		public void finalize() {
			_delagate.finalize();
		}

		private Color getBackgroundG2D() {
			return _delagate.getBackground();
		}

		public Shape getClip() {
			return _delagate.getClip();
		}

		public Rectangle getClipBounds() {
			return _delagate.getClipBounds();
		}

		public Rectangle getClipBounds(Rectangle r) {
			return _delagate.getClipBounds(r);
		}

		public Rectangle getClipRect() {
			return _delagate.getClipRect();
		}

		private Color getColorG2D() {
			return _delagate.getColor();
		}
		

	public ARGBColor getARGBColor() {
		return new ARGBColor(this.getColorG2D());
	}
		
		public Composite getComposite() {
			return _delagate.getComposite();
		}

		public GraphicsConfiguration getDeviceConfiguration() {
			return _delagate.getDeviceConfiguration();
		}

		public Font getFont() {
			return _delagate.getFont();
		}

		public FontMetrics getFontMetrics() {
			return _delagate.getFontMetrics();
		}

		public FontMetrics getFontMetrics(Font f) {
			return _delagate.getFontMetrics(f);
		}

		public FontRenderContext getFontRenderContext() {
			return _delagate.getFontRenderContext();
		}

		public Paint getPaint() {
			return _delagate.getPaint();
		}

		public Object getRenderingHint(Key hintKey) {
			return _delagate.getRenderingHint(hintKey);
		}

		public RenderingHints getRenderingHints() {
			return _delagate.getRenderingHints();
		}

		public Stroke getStroke() {
			return _delagate.getStroke();
		}

		public AffineTransform getTransform() {
			return _delagate.getTransform();
		}

		public int hashCode() {
			return _delagate.hashCode();
		}

		public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
			return _delagate.hit(rect, s, onStroke);
		}

		public boolean hitClip(int x, int y, int width, int height) {
			return _delagate.hitClip(x, y, width, height);
		}

		public void rotate(double theta, double x, double y) {
			_delagate.rotate(theta, x, y);
		}

		public void rotate(double theta) {
			_delagate.rotate(theta);
		}

		public void scale(double sx, double sy) {
			_delagate.scale(sx, sy);
		}

		private void setBackgroundG2D(Color color) {
			_delagate.setBackground(color);
		}
		public void setBackground(ARGBColor c) {
				setBackgroundG2D(c.asColor());
		}


		public void setClip(int x, int y, int width, int height) {
			_delagate.setClip(x, y, width, height);
		}

		public void setClip(Shape clip) {
			_delagate.setClip(clip);
		}

		public void setColorG2D(Color c) {
			_delagate.setColor(c);
		}

		public void setColor(ARGBColor c) {
			setColorG2D(c.asColor());
		}


		public void setComposite(Composite comp) {
			_delagate.setComposite(comp);
		}

		public void setFont(Font font) {
			_delagate.setFont(font);
		}

		public void setPaint(Paint paint) {
			_delagate.setPaint(paint);
		}

		public void setPaintMode() {
			_delagate.setPaintMode();
		}

		public void setRenderingHint(Key hintKey, Object hintValue) {
			_delagate.setRenderingHint(hintKey, hintValue);
		}

		public void setRenderingHints(Map<?, ?> hints) {
			_delagate.setRenderingHints(hints);
		}

		public void setStroke(Stroke s) {
			_delagate.setStroke(s);
		}

		public void setTransform(AffineTransform Tx) {
			_delagate.setTransform(Tx);
		}

		public void setXORMode(Color c1) {
			_delagate.setXORMode(c1);
		}

		public void shear(double shx, double shy) {
			_delagate.shear(shx, shy);
		}

		public String toString() {
			return _delagate.toString();
		}

		public void transform(AffineTransform Tx) {
			_delagate.transform(Tx);
		}

		public void translate(double tx, double ty) {
			_delagate.translate(tx, ty);
		}

		public void translate(int x, int y) {
			_delagate.translate(x, y);
		}
		
	}
