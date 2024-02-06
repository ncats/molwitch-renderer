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




import java.util.Map;


 abstract class Graphics2DParent{
	public static abstract class GeomGenerator{
		public abstract LineParent makeLine(double x1,double y1,double x2,double y2);
		public abstract Point2DParent makePoint(double x1,double y1);
		public abstract Rectangle2DParent makeRectangle(double x1,double y1, double w, double h);
		public abstract AffineTransformParent makeAffineTransform();
		public abstract Ellipse2DParent makeEllipse(double x1,double y1, double w, double h);
		public abstract GeneralPathParent makeGeneralPath();
	}
	
	public static abstract class GeneralPathParent extends ShapeParent{
		public abstract void append(PathIteratorParent pi, boolean connect) ;
		public abstract void append(ShapeParent s, boolean connect) ;
		public abstract void closePath() ;
		public abstract ShapeParent createTransformedShape(AffineTransformParent at) ;
		public abstract  void curveTo(double x1, double y1, double x2, double y2,
				double x3, double y3) ;
		public abstract boolean equals(Object obj) ;
		public abstract  Point2DParent getCurrentPoint() ;
		public abstract  int getWindingRule() ;
		public abstract int hashCode() ;
		public abstract  void lineTo(double x, double y) ;
		public abstract  void moveTo(double x, double y) ;
		public abstract  void quadTo(double x1, double y1, double x2, double y2) ;
		public abstract  void reset() ;
		public abstract  void setWindingRule(int rule) ;
		public abstract String toString() ;
		public abstract  void transform(AffineTransformParent at) ;		
	}
	public static abstract class Ellipse2DParent extends ShapeParent{
		
		public abstract Rectangle2DParent createIntersection(Rectangle2DParent r);
		public abstract Rectangle2DParent createUnion(Rectangle2DParent r);
		public abstract int outcode(double x, double y);
		public abstract void setRect(double x, double y, double w, double h);
		public abstract double getHeight();
		public abstract double getWidth();
		public abstract double getX();
		public abstract double getY();
		public abstract boolean isEmpty();
		
		
	}
	public static abstract class Point2DParent{
		public abstract double getX();
		public abstract double getY();
		public abstract void setLocation(double arg0, double arg1);
	}

	public static abstract class Rectangle2DParent extends ShapeParent {
		public abstract Rectangle2DParent createIntersection(Rectangle2DParent r);
		public abstract Rectangle2DParent createUnion(Rectangle2DParent r);
		public abstract int outcode(double x, double y);
		public abstract void setRect(double x, double y, double w, double h);
		public abstract double getHeight();
		public abstract double getWidth();
		public abstract double getX();
		public abstract double getY();
		public abstract boolean isEmpty();
	}
	public static abstract class LineParent extends ShapeParent{
		public abstract Point2DParent getP1() ;
		public abstract Point2DParent getP2() ;
		public abstract double getX1() ;
		public abstract double getX2() ;
		public abstract double getY1() ;
		public abstract double getY2() ;
		public abstract void setLine(double x1, double y1, double x2, double y2) ;
		public abstract boolean intersectsLine(LineParent startLine);
	}
	public static abstract class ShapeParent {
		public boolean contains(Point2DParent arg0){
			return this.contains(arg0.getX(),arg0.getY());
		}
		public boolean contains(Rectangle2DParent arg0){
			return this.contains(arg0.getX(),arg0.getY(),arg0.getWidth(),arg0.getHeight());
		}
		public abstract boolean contains(double arg0, double arg1);
		public abstract boolean contains(double arg0, double arg1, double arg2, double arg3);
		public Rectangle2DParent getBounds(){
			return this.getBounds2D();
		}
		public abstract Rectangle2DParent getBounds2D();
		public abstract PathIteratorParent getPathIterator(AffineTransformParent arg0);
		public abstract PathIteratorParent getPathIterator(AffineTransformParent arg0, double arg1);
		public boolean intersects(Rectangle2DParent arg0){
			return this.intersects(arg0.getX(),arg0.getY(),arg0.getWidth(),arg0.getHeight());
		}
		public abstract boolean intersects(double arg0, double arg1, double arg2,double arg3);

	}
	public static abstract class GlyphVectorParent{
		public abstract boolean equals(GlyphVectorParent set) ;
		public abstract FontParent getFont() ;
		public abstract FontRenderContextParent getFontRenderContext() ;
		public abstract int getGlyphCode(int glyphIndex) ;
		public abstract int[] getGlyphCodes(int beginGlyphIndex, int numEntries,
				int[] codeReturn) ;
		public abstract GlyphJustificationInfoParent getGlyphJustificationInfo(int glyphIndex) ;
		public abstract ShapeParent getGlyphLogicalBounds(int glyphIndex) ;
		public abstract GlyphMetricsParent getGlyphMetrics(int glyphIndex) ;
		public abstract ShapeParent getGlyphOutline(int glyphIndex) ;
		public abstract Point2DParent getGlyphPosition(int glyphIndex) ;
		public abstract float[] getGlyphPositions(int beginGlyphIndex, int numEntries,
				float[] positionReturn) ;
		public abstract AffineTransformParent getGlyphTransform(int glyphIndex) ;
		public abstract ShapeParent getGlyphVisualBounds(int glyphIndex) ;
		public abstract Rectangle2DParent getLogicalBounds() ;
		public abstract int getNumGlyphs() ;
		public abstract ShapeParent getOutline() ;
		public abstract ShapeParent getOutline(float x, float y) ;
		public abstract Rectangle2DParent getVisualBounds() ;
		public abstract void performDefaultLayout() ;
		public abstract void setGlyphPosition(int glyphIndex, Point2DParent newPos) ;
		public abstract void setGlyphTransform(int glyphIndex, AffineTransformParent newTX) ;
	}
	public abstract static class AffineTransformParent{
		//{ m00 m10 m01 m11 m02 m12 }.
		public abstract void getMatrix(double[] mat);
		public void setTransform(AffineTransformParent Tx){
			double[] mat=new double[6];
			Tx.getMatrix(mat);
			setTransform(mat[0],mat[1],mat[2],mat[3],mat[4],mat[5]);
		}
		private double _getMatrix(int o){
			double[] mat=new double[6];
			getMatrix(mat);
			return mat[o];
		}
		public double getScaleX(){
			return _getMatrix(0);
		}
		public double getScaleY(){
			return _getMatrix(3);
		}
		public double getShearX(){
			return _getMatrix(2);
		}
		public double getShearY(){
			return _getMatrix(1);
		}
		public double getTranslateX(){
			return _getMatrix(4);
		}
		public double getTranslateY(){
			return _getMatrix(5);
		}
		public abstract double getDeterminant() ;
		
		public abstract Object clone() ;
		public abstract boolean equals(Object obj) ;
		
		public abstract void concatenate(AffineTransformParent Tx) ;
		public abstract AffineTransformParent createInverse() throws Exception;
		public abstract ShapeParent createTransformedShape(ShapeParent pSrc) ;
		public abstract void deltaTransform(double[] arg0, int arg1, double[] arg2,	int arg3, int arg4) ;
		public abstract Point2DParent deltaTransform(Point2DParent ptSrc, Point2DParent ptDst) ;
		
		
		public abstract int getType() ;
		public abstract int hashCode() ;
		public abstract void inverseTransform(double[] arg0, int arg1, double[] arg2,
				int arg3, int arg4) throws Exception ;
		public abstract Point2DParent inverseTransform(Point2DParent arg0, Point2DParent arg1);
		public abstract void invert() throws Exception ;
		public abstract boolean isIdentity() ;
		public abstract void preConcatenate(AffineTransformParent Tx) ;
		public abstract void quadrantRotate(int numquadrants, double anchorx,
				double anchory) ;
		public abstract void quadrantRotate(int numquadrants) ;
		public abstract void rotate(double vecx, double vecy, double anchorx,
				double anchory) ;
		public abstract void rotate(double theta, double anchorx, double anchory) ;
		public abstract void rotate(double arg0, double arg1) ;
		public abstract void rotate(double arg0) ;
		public abstract void scale(double sx, double sy) ;
		public abstract void setToIdentity() ;
		public abstract void setToQuadrantRotation(int numquadrants, double anchorx,
				double anchory) ;
		public abstract void setToQuadrantRotation(int numquadrants) ;
		public abstract void setToRotation(double vecx, double vecy, double anchorx,
				double anchory) ;
		public abstract void setToRotation(double theta, double anchorx, double anchory) ;
		public abstract void setToRotation(double arg0, double arg1) ;
		public abstract void setToRotation(double arg0) ;
		public abstract void setToScale(double sx, double sy) ;
		public abstract void setToShear(double shx, double shy) ;
		public abstract void setToTranslation(double tx, double ty) ;
		public abstract void setTransform(double m00, double m10, double m01, double m11, double m02, double m12);
		public abstract void shear(double arg0, double arg1) ;
		public abstract String toString() ;
		public abstract void transform(double[] arg0, int arg1, double[] arg2, int arg3,
				int arg4) ;
		public abstract void transform(double[] arg0, int arg1, float[] arg2, int arg3,
				int arg4) ;
		public abstract void transform(float[] arg0, int arg1, double[] arg2, int arg3,
				int arg4) ;
		public abstract void transform(float[] arg0, int arg1, float[] arg2, int arg3,
				int arg4) ;
		public abstract Point2DParent transform(Point2DParent ptSrc, Point2DParent ptDst) ;
		public abstract void transform(Point2DParent[] arg0, int arg1, Point2DParent[] arg2,
				int arg3, int arg4) ;
		public abstract void translate(double tx, double ty);	
		
	}
	public static abstract class PathIteratorParent{
		public abstract int currentSegment(double[] coords) ;
		public abstract int currentSegment(float[] coords) ;
		public abstract int getWindingRule() ;
		public abstract boolean isDone() ;
		public abstract void next() ;		
	}
	public static class FontParent{

	}
	public static class FontRenderContextParent{
		
	}
	public static class FontMetricsParent{
		
	}
	public static class GlyphJustificationInfoParent{
		
	}
	public static class GlyphMetricsParent{
		
	}
	public static class ImageParent{
		
	}
	public static class BufferedImageParent{
		
	}
	public static class BufferedImageOpParent{
		
	}
	public static class RenderableImageParent{
		
	}
	public static class RenderedImageParent{
		
	}
	public static class ImageObserverParent{
		
	}
	public static class AttributedCharacterIteratorParent{
		
	}
	public static class ColorParent{
		int[] rgba=new int[4];
		public ColorParent(int r, int b, int g, int a){
			rgba[0]=r;
			rgba[1]=b;
			rgba[2]=g;
			rgba[3]=a;
		}
		public ColorParent(){
			
		}

		public ColorParent copy(){
			return new ColorParent(rgba[0],rgba[1],rgba[2],rgba[3]);
		}
		public int[] getRGB(){
			return new int[]{rgba[0],rgba[1],rgba[2],rgba[3]};
		}
		public int getRGBInt(){
			int[] rgb1=this.getRGB();
			return rgb1[0]+
				   (rgb1[1]<<8)+
				   (rgb1[2]<<16)+
				   (rgb1[3]<<24);
		}
		
		public boolean equals(ColorParent cp){
			//System.out.println("Testing:" + cp.getRGBInt() + "?=" + this.getRGBInt());
			return getRGBInt()==cp.getRGBInt();
		}
		
	}
	public static class CompositeParent{
		
	}
	public static class GraphicsConfigurationParent{
		
	}
	public static class PaintParent{
		
	}
	public static class KeyParent{
		
	}
	public static class RenderingHintsParent{
		
	}
	public static class StrokeParent{
		
	}
	public abstract void addRenderingHints(Map<?, ?> arg0);
	public abstract void clip(ShapeParent arg0);
	public abstract void draw(ShapeParent arg0);
	public abstract void drawGlyphVector(GlyphVectorParent arg0, float arg1, float arg2);
	public abstract boolean drawImage(ImageParent arg0, AffineTransformParent arg1,ImageObserverParent arg2);
	public abstract void drawImage(BufferedImageParent arg0, BufferedImageOpParent arg1, int arg2, int arg3);
	public abstract void drawRenderableImage(RenderableImageParent arg0, AffineTransformParent arg1);
	public abstract void drawRenderedImage(RenderableImageParent arg0, AffineTransformParent arg1) ;
	public abstract void drawString(String arg0, int arg1, int arg2) ;
	public abstract void drawString(String arg0, float arg1, float arg2) ;
	public abstract void drawString(AttributedCharacterIteratorParent arg0, int arg1, int arg2) ;
	public abstract void drawString(AttributedCharacterIteratorParent arg0, float arg1,
			float arg2) ;
	public abstract void fill(ShapeParent arg0) ;
	public abstract CompositeParent getComposite() ;
	public abstract GraphicsConfigurationParent getDeviceConfiguration() ;
	public abstract FontRenderContextParent getFontRenderContext() ;
	public abstract PaintParent getPaint() ;
	public abstract Object getRenderingHint(KeyParent arg0) ;
	public abstract RenderingHintsParent getRenderingHints() ;
	public abstract StrokeParent getStroke() ;
	public abstract AffineTransformParent getTransform() ;
	public abstract boolean hit(Rectangle2DParent arg0, ShapeParent arg1, boolean arg2) ;
	public abstract void rotate(double arg0) ;
	public abstract void rotate(double arg0, double arg1, double arg2) ;
	public abstract void scale(double arg0, double arg1) ;

	public abstract void setComposite(CompositeParent arg0) ;
	public abstract void setPaint(PaintParent arg0) ;
	public abstract void setRenderingHint(KeyParent arg0, Object arg1) ;
	public abstract void setRenderingHints(Map<?, ?> arg0) ;
	public abstract void setStroke(StrokeParent arg0) ;
	public abstract void setTransform(AffineTransformParent arg0) ;
	public abstract void shear(double arg0, double arg1) ;
	public abstract void transform(AffineTransformParent arg0) ;
	public abstract void translate(int arg0, int arg1) ;
	public abstract void translate(double arg0, double arg1) ;
	public abstract void clearRect(int arg0, int arg1, int arg2, int arg3) ;
	public abstract void clipRect(int arg0, int arg1, int arg2, int arg3) ;
	public abstract void copyArea(int arg0, int arg1, int arg2, int arg3, int arg4,
			int arg5) ;
	public abstract Graphics2DParent create() ;
	public abstract void dispose() ;
	public abstract void drawArc(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) ;
	public abstract boolean drawImage(ImageParent arg0, int arg1, int arg2, ImageObserverParent arg3) ;

	public abstract boolean drawImage(ImageParent arg0, int arg1, int arg2, int arg3,
			int arg4, ImageObserverParent arg5) ;

	public abstract boolean drawImage(ImageParent arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5, int arg6, int arg7, int arg8, ImageObserverParent arg9) ;
	public abstract boolean drawImage(ImageParent arg0, int arg1, int arg2, int arg3,
			ImageObserverParent arg10) ;
	public abstract void drawLine(int arg0, int arg1, int arg2, int arg3) ;
	public abstract void drawOval(int arg0, int arg1, int arg2, int arg3) ;
	public abstract void drawPolygon(int[] arg0, int[] arg1, int arg2) ;
	public abstract void drawPolyline(int[] arg0, int[] arg1, int arg2) ;
	public abstract void drawRoundRect(int arg0, int arg1, int arg2, int arg3, int arg4,
			int arg5) ;
	public abstract void fillArc(int arg0, int arg1, int arg2, int arg3, int arg4,
			int arg5) ;
	public abstract void fillOval(int arg0, int arg1, int arg2, int arg3) ;
	public abstract void fillPolygon(int[] arg0, int[] arg1, int arg2) ;
	public abstract void fillRect(int arg0, int arg1, int arg2, int arg3) ;
	public abstract void fillRoundRect(int arg0, int arg1, int arg2, int arg3, int arg4,
			int arg5) ;
	public abstract ShapeParent getClip() ;
	public abstract Rectangle2DParent getClipBounds() ;

	public abstract FontParent getFont() ;
	public abstract FontMetricsParent getFontMetrics(FontParent arg0) ;
	public abstract void setClip(ShapeParent arg0) ;
	public abstract void setClip(int arg0, int arg1, int arg2, int arg3) ;

	public abstract void setFont(FontParent arg0) ;
	public abstract void setPaintMode() ;


}
