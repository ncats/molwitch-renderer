/*
 * NCATS-MOLWITCH-RENDERER
 *
 * Copyright 2022 NIH/NCATS
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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.*;
import gov.nih.ncats.molwitch.Atom;
import gov.nih.ncats.molwitch.Bond;
import gov.nih.ncats.molwitch.Chemical;

public class ChemicalRenderer {
    @JsonIgnore
	private final NchemicalRenderer renderer;
	
	public ChemicalRenderer() {
		this(RendererOptions.createDefault());
	}
    @JsonCreator
	public ChemicalRenderer(@JsonProperty("options") RendererOptions options){
		this.renderer = new NchemicalRenderer(Objects.requireNonNull(options));
	}
    @JsonGetter("color-bg")
	public ARGBColor getBackgroundColorARGB(){
	    return renderer.getBackgroundColor();
    }

    public ChemicalRenderer copy(){
		ChemicalRenderer copy = new ChemicalRenderer(renderer.getOptions().copy());
		copy.setBorderVisible(renderer.getBorderVisible());
		copy.setShadowVisible(renderer.getShadowVisible());
		copy.setBackgroundColor(renderer.getBackgroundColor().asColor());
		copy.setBorderColor(renderer.getBorderColor().asColor());
		return copy;
	}
    @JsonIgnore
    public ARGBColor getBackgroundColor(){
	    return renderer.getBackgroundColor();
    }
	@JsonSetter("color-bg")
    public ChemicalRenderer setBackgroundColorARGB(String argbHex) {
        return setBackgroundColor(fromArgb(argbHex));
    }

    private static Color fromArgb(String argbHex) {
	    if(argbHex ==null){
	        return null;
        }
        return new Color(Integer.parseUnsignedInt(argbHex, 16),true);
    }
    @JsonIgnore
    public ChemicalRenderer setBackgroundColor(Color bg) {
		this.renderer.setBackgroundColor(new ARGBColor(bg));
		return this;
	}
    @JsonGetter("color-border")
    public ARGBColor getBorderColorARGB(){
        return renderer.getBorderColor();
    }
    @JsonIgnore
	public ARGBColor getBorderColor(){
        return renderer.getBorderColor();
    }
    @JsonSetter("color-border")
    public ChemicalRenderer setBorderColorARGB(String argbHex){
        return setBorderColor(fromArgb(argbHex));
    }
    @JsonIgnore
	public ChemicalRenderer setBorderColor(Color bg) {
		this.renderer.setBorderColor(new ARGBColor(bg));
		return this;
	}
	@JsonGetter("options")
	public RendererOptions getOptions() {
		return renderer.getOptions();
	}

	@JsonGetter("add-shadow")
	public boolean isShadowVisible(){
	    return renderer.getShadowVisible();
    }
    @JsonSetter("add-shadow")
	public ChemicalRenderer setShadowVisible(boolean visible) {
		this.renderer.setShadowVisible(visible);
		return this;
	}
    @JsonGetter("add-border")
    public boolean isBorderVisible(){
        return renderer.getShadowVisible();
    }
    @JsonSetter("add-border")
	public ChemicalRenderer setBorderVisible(boolean visible) {
		this.renderer.setBorderVisible(visible);
		return this;
	}
	public void render(Graphics2D g2d, File inputMol, int x, int y, int width, int height, boolean round) throws IOException{
		render(g2d, Chemical.parseMol(inputMol), x,y, width, height, round);
	}
	public void render(Graphics2D g2d, String inputMol, int x, int y, int width, int height, boolean round) throws IOException{
		render(g2d, Chemical.parseMol(inputMol), x,y, width, height, round);
	}
	public void render(Graphics2D g2d, Chemical c, int x, int y, int width, int height, boolean round) {
		renderer.renderChem (g2d, c, x,y, width, height, round);
		getOptions().captionBottom(c)
				.ifPresent(caption ->renderer.drawText(g2d,x,y,width,height,caption,1)); // 1 is bottom, 0 is top);;
		getOptions().captionTop(c)
		.ifPresent(caption ->renderer.drawText(g2d,x,y,width,height,caption,0)); // 1 is bottom, 0 is top);;

	}
	public BufferedImage createImage (String inputMol, int size) throws IOException{
		return createImage (Chemical.parse(inputMol), size, size, true);
	}
	public BufferedImage createImage (File inputMol, int size) throws IOException{
		return createImage (Chemical.parseMol(inputMol), size, size, true);
	} 
	public BufferedImage createImage (Chemical c, int size) {
    		return createImage (c, size, size, true);
    }
	public BufferedImage createImageAutoAdjust (Chemical c, int size) {
		double totalBondLength=0.0;
		Optional<Double> averageBondLength=computeAverageBondLength(c);

		for(Bond bond : c.getBonds()){
			totalBondLength+= bond.getBondLength();
		}
		double avgBondLength= totalBondLength/c.getBondCount();
		//System.out.printf("average bond length: %f\n", avgBondLength);
		Rectangle2D.Double bounds = computeAtomicCoordinateBounds(c);
		double xSpread = bounds.getWidth();
		double ySpread = bounds.getHeight();
		//System.out.printf("xSpread: %f .  ySpread: %f \n", xSpread, ySpread);
		double averageSpread = (xSpread+ySpread)/2;
		int width= (int) Math.round( size * xSpread/averageSpread);
		if(width<10) width=size;
		int height= (int) Math.round(size * ySpread/averageSpread);
		if(height<10) height= size;
		//System.out.printf("width: %d; height: %d\n", width, height);
		return createImage (c, width, height,false);
	}

	public BufferedImage createImageAutoAdjust (Chemical c, int maxWidth, int minWidth, int maxHeight, int minHeight,
												double requestedAverageBondLength) {

		Rectangle2D.Double rectangle= getApproximateBoundsFor(c, maxWidth, minWidth, maxHeight, minHeight, requestedAverageBondLength);
		int width= (int) Math.round(rectangle.getWidth());
		int height= (int) Math.round(rectangle.getHeight());
		//System.out.printf("final width: %d; height: %d.\n", width, height);
		return createImage (c, width, height, false);
	}

	public Rectangle2D.Double getApproximateBoundsFor (Chemical c, int maxWidth, int minWidth, int maxHeight, int minHeight,
												double requestedAverageBondLength) {
		Optional<Double> foundAverageBondLength = computeAverageBondLength(c);
		//System.out.printf("average bond length: %f\n", foundAverageBondLength.isPresent() ? foundAverageBondLength.get() : 0.0);
		double scaleFactor =1.0;
		if( foundAverageBondLength.isPresent() && foundAverageBondLength.get()> 0) {
			scaleFactor= requestedAverageBondLength / foundAverageBondLength.get();
		}
		Rectangle2D.Double atomicCoordinateBounds = computeAtomicCoordinateBounds(c);
		double xSpread0 = atomicCoordinateBounds.getWidth();
		double ySpread0 = atomicCoordinateBounds.getHeight();
		//System.out.printf("xSpread0: %f.  ySpread0: %f \n", xSpread0, ySpread0);
		double xSpread= scaleFactor*xSpread0;
		double ySpread= scaleFactor*ySpread0;
		//System.out.printf("scaled xSpread: %f.  ySpread: %f \n", xSpread, ySpread);
		int width=  (int) Math.round( xSpread);
		int height=(int) Math.round(ySpread);
		//System.out.printf("initial width: %d; height: %d\n", width, height);
		if(width<minWidth) width=minWidth;
		if(width>maxWidth) width=maxWidth;

		if(height<minHeight) height= minHeight;
		if(height>maxHeight) height=maxHeight;

		//When there is one heavy atom, we see overflows.  Prevent that by checking for *spread<=0
		if(xSpread<= 0) xSpread=1;
		if(ySpread<= 0) ySpread=1;
		double xScale =  width/xSpread; //xSpread==1 ? 1.0 :
		double yScale =  height/ySpread;//ySpread==1 ? 1.0 :
		double scaleFinal=Math.min(xScale,yScale);
		width= (int) Math.round(scaleFinal*xSpread);
		height=(int) Math.round(scaleFinal*ySpread);

		//one last check
		if(height< minHeight) height= minHeight;
		if(width< minWidth) width=minWidth;
		//System.out.printf("final width: %d; height: %d. scaleFinal: %f\n", width, height, scaleFinal);
		return new Rectangle2D.Double(0, 0, width, height );
	}

	public BufferedImage createImage (File inputMol, int width, int height, boolean round) throws IOException{
	    return createImage(Chemical.parseMol(inputMol), width, height, round);
	}
	public BufferedImage createImage (String inputMol, int width, int height, boolean round) throws IOException{
	    return createImage(Chemical.parse(inputMol), width, height, round);
	}
	public BufferedImage createImage (Chemical c, int width, int height, boolean round) {
        BufferedImage img = new BufferedImage (width, height, BufferedImage.TYPE_INT_ARGB);
            
        Graphics2D g2 = img.createGraphics();
        render (g2, c,0,0, width, height, round);
        g2.dispose();
    

         return img;
    } 


	public static double computeAverageInterAtomDistance(Chemical c){
		int totalDistances = 0;
		double totalDistance =0.0d;
		for(int i=0; i<c.getAtomCount();i++){
			for(int j=i+1; j<c.getAtomCount();j++){
				double firstAtomX= c.getAtom(i).getAtomCoordinates().getX();
				double firstAtomY= c.getAtom(i).getAtomCoordinates().getY();
				double secondAtomX= c.getAtom(j).getAtomCoordinates().getX();
				double secondAtomY= c.getAtom(j).getAtomCoordinates().getY();

				double distance = Math.sqrt(Math.pow( (secondAtomX-firstAtomX), 2) + Math.pow((secondAtomY-firstAtomY),2));
				//System.out.printf("atom 1: %d; atom 2: %d; distance: %f\n", c.getAtom(i).getAtomicNumber(), c.getAtom(j).getAtomicNumber(), distance);
				totalDistance+= distance;
				totalDistances++;
			}
		}
		return totalDistance/totalDistances;
	}

	public static Optional<Double> computeLowestInterAtomDistance(Chemical c){
		Double lowest =null;
		for(int i=0; i<c.getAtomCount();i++){
			for(int j=i+1; j<c.getAtomCount();j++){
				double firstAtomX= c.getAtom(i).getAtomCoordinates().getX();
				double firstAtomY= c.getAtom(i).getAtomCoordinates().getY();
				double secondAtomX= c.getAtom(j).getAtomCoordinates().getX();
				double secondAtomY= c.getAtom(j).getAtomCoordinates().getY();

				double distance = Math.sqrt(Math.pow( (secondAtomX-firstAtomX), 2) + Math.pow((secondAtomY-firstAtomY),2));
				/*System.out.printf("atom 1: %d x: %f, y: %f; atom 2: %d x: %f, y: %f; distance: %f\n",
						c.getAtom(i).getAtomicNumber(), c.getAtom(i).getAtomCoordinates().getX(), c.getAtom(i).getAtomCoordinates().getY(),
						c.getAtom(j).getAtomicNumber(), c.getAtom(j).getAtomCoordinates().getX(), c.getAtom(j).getAtomCoordinates().getY(),
						distance);*/
				if( distance > 0 && (lowest==null || distance< lowest)) {
					lowest = distance;
				}
			}
		}
		return lowest>0 ? Optional.of(lowest) : Optional.empty();
	}

	public static Optional<Double> computeAverageBondLength(Chemical c) {
		double totalBondLength=0.0;
		if( c.getBondCount()>0) {
			for (Bond bond : c.getBonds()) {
				totalBondLength += bond.getBondLength();
			}
			return Optional.of( totalBondLength / c.getBondCount());
		}
		return Optional.empty();
	}

	public static Rectangle2D.Double computeAtomicCoordinateBounds(Chemical chemical) {
		double minX = Integer.MAX_VALUE;
		double minY = Integer.MAX_VALUE;
		double maxX = Integer.MIN_VALUE;
		double maxY = Integer.MIN_VALUE;
		for(Atom atom : chemical.getAtoms()) {
			if( atom.getAtomCoordinates().getX() > maxX) {
				maxX= atom.getAtomCoordinates().getX();
			}
			if( atom.getAtomCoordinates().getY() > maxY) {
				maxY= atom.getAtomCoordinates().getY();
			}
			if( atom.getAtomCoordinates().getX() < minX) {
				minX= atom.getAtomCoordinates().getX();
			}
			if( atom.getAtomCoordinates().getY() < minY) {
				minY= atom.getAtomCoordinates().getY();
			}
		}
		double xSpread = maxX - minX;
		double ySpread = maxY - minY;
		/*System.out.printf("xSpread: %f (minX: %f, maxX: %f).  ySpread: %f (minY: %f, maxY: %f)\n", xSpread, minX, maxX,
				ySpread, minY, maxY);*/
		return new Rectangle2D.Double(minX, minY, xSpread, ySpread);
	}
}
