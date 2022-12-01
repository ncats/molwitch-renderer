/*
 * NCATS-MOLWITCH-RENDERER
 *
 * Copyright 2019 NIH/NCATS
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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.color.ColorSpace;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import gov.nih.ncats.molwitch.Chemical;




 abstract class AbstractChemicalRenderer {
	
	public static final String PROPERTY_NAME = "NAME_OF_CHEMICAL_4829103";
	public static final String PROPERTY_SMILES = "SMILES_OF_CHEMICAL_4829103";
	
	private static final int POSITION_TOP=0;
	private static final int POSITION_BOTTOM=1;
	
	
	
	 static final ConcurrentMap<Integer, BufferedImageOp> OperCache = 
		        new ConcurrentHashMap<Integer, BufferedImageOp>();
	 static final ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
	 static final ColorConvertOp GREYOP = new ColorConvertOp(cs, null);  
	 
	 boolean shadowViz=true;	 
	 float shadowRad=0.01f;
	 float shadowTrans=0.25f;
	 int shadowOff=5;
	 boolean borderViz=false;
     ARGBColor backgroundColor = new ARGBColor(0,0,0,0);
     ARGBColor borderColor=new ARGBColor(Color.black);
	 Set<String> _displayProperties = new LinkedHashSet<String>();

		
	  
	public abstract void renderChem (Graphics2D g2, Chemical c, int x, int y,int width, int height);
	
	
	public ARGBColor getBackgroundColor() {		return backgroundColor;	}
	public ARGBColor getBorderColor() {		return borderColor;	}
	public boolean getShadowVisible() {return shadowViz;}
	public float getShadowRadius() {return shadowRad;}
	public float getShadowTranslucency() {return shadowTrans;}
	public int getShadowOffset() {return shadowOff;}
	public boolean getNameVisible() {return _displayProperties.contains(PROPERTY_NAME);}
	public boolean getBorderVisible(){return borderViz;}
	public void setShadowVisible(boolean b) {shadowViz=b;}
	public void SetShadowRadius(float r) {shadowRad=r;}
	public void setShadowTranslucency(float t) {shadowTrans=t;}
	public void setShadowOffset(int o) {shadowOff=o;}
	
	public void setBorderVisible(boolean v){borderViz=v;}
	public void setBorderColor(ARGBColor c){borderColor = c;}
	public void setBackgroundColor(ARGBColor color){this.backgroundColor=color;}
	public void setNameVisible(boolean b) {
		if(b)
			_displayProperties.add(PROPERTY_NAME);
		else
			_displayProperties.remove(PROPERTY_NAME);
	}
	public void setDisplayProperties(Set<String> properties) {
		_displayProperties=properties;
		
	}
	public void addDisplayProperty(String prop){
		_displayProperties.add(prop);
	}
	public void removeDisplayProperty(String prop){
		_displayProperties.remove(prop);
	}
	
	
	public void renderChem (Graphics2D g2, Chemical c, 
			   int width, int height, boolean round) {
	renderChem (g2, c, 0, 0, width, height, round);
	}
	public void renderChem (Graphics2D g2, Chemical c, int x, int y,int width, int height, boolean round){
		System.out.printf("starting renderChem total atoms: %d; x: %d; y: %d, width: %d; height: %d; round: %b\n",
				c.getAtomCount(), x, y, width, height, round);
		renderBackground(g2,x,y,width,height,round);
		if(getShadowVisible())
			renderChemicalShadow(g2,c,x,y,width,height);
		renderChem(g2,c,x,y,width,height);
		int i=0;
		if(_displayProperties!=null){
			for(String s : _displayProperties){
				String prop = "";
				if(s.equals(this.PROPERTY_NAME)){
					prop = c.getName();
				}else if(s.equals(this.PROPERTY_SMILES)){
					try{
						prop = c.toSmiles();
					}catch(Exception e){
						e.printStackTrace();
					}
				}else{
					prop = c.getProperty(s);
				}
				if(prop !=null){
					prop=prop.trim();
					if(i==0)
						drawText(g2,x,y,width,height,prop,this.POSITION_TOP);
					if(i==1)
						drawText(g2,x,y,width,height,prop,this.POSITION_BOTTOM);
				}
				i++;
			}
		}
	}
        /**
	*
	*  Draw the supplied string to the graphics object at either the top or bottom 
        *  of the rectangle derived from x,y,x+width,y+height. The size of the font will
	*  be the width supplied/15. 
	*
	*  Returns the Rectangle bounds of where the string was drawn. 
	*/
	public Rectangle2D.Double drawText(Graphics2D g2, int x, int y, int width, int height,
			String text, int position) {
		return drawText(g2, x, y, width, height, text, position, false);
	}
	 
	/**
	*
	*  Draw the supplied string to the graphics object at either the top or bottom 
        *  of the rectangle derived from x,y,x+width,y+height. The size of the font will
	*  be the width supplied/15. 
	*
	*  Returns the Rectangle bounds of where the string was drawn. 
	*/
	public Rectangle2D.Double drawText(Graphics2D g2, int x, int y, int width, int height,
			String text, int position, boolean boundsOnly) {
		g2.setColor(Color.black);
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN, width / 15));
		FontMetrics fm = g2.getFontMetrics();
		float swidth = (float) fm.getStringBounds(text, g2).getWidth();
		float sheight = (float) fm.getStringBounds(text, g2).getHeight();
		if (swidth > width){
			text = text.substring(0,(int)((text.length()*width)/swidth)-3) + "...";
			swidth = fm.stringWidth(text);
		}
		double startingX = x + (width - swidth) / 2;
		double startingY=0;
		switch (position) {
			case POSITION_BOTTOM:				
				// bottom pixel - half the height of the font
				// this is because the y-position determines
				// the baseline of the font. This gives a half
				// font-height padding to the rendering on
				// bottom
				startingY = y + height - sheight / 2; 
				break;
			case POSITION_TOP:
				// top pixel + 3/2 the height of the font
				// this is because the y-position determines
				// the baseline of the font. This gives a half
				// font-height padding to the rendering on
				// top
				startingY = y + (sheight * 3) / 2; 
				break;
			default:
				break;
		}
		if(!boundsOnly){
			g2.drawString(text, startingX, startingY);
		}
                return new Rectangle2D.Double(startingX, startingY-sheight, swidth, sheight);
	}
	 
	 
	public void renderChemicalShadow(Graphics2D g2, Chemical c, int x, int y, int width, int height){
        BufferedImage tmpCanvas = new BufferedImage 
	    (width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = tmpCanvas.createGraphics();
        g.setBackground(new Color(0,0,0,0));
        g.setColor(Color.black);
        renderChem (g, c,x,y, width, height);
        int radius = (int)(getShadowRadius()*width + .5f);
        BufferedImage blur = createBlur (tmpCanvas, radius, getShadowTranslucency());
        g2.drawImage(blur, GREYOP, getShadowOffset(), getShadowOffset());
	}
	public void renderBackground(Graphics2D g,int x, int y,int wid,int hit,boolean round){            
       if(round){
    	   Shape shape = new RoundRectangle2D.Double
    	            (x, y, (double)wid, (double)hit, wid/4., hit/4.);
    	        //g.setComposite(AlphaComposite.Clear);
    	        //g.setPaint(new Color(0,0,0,0));
    	        //g.fillRect(x, y, wid, hit);
    	        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    			g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    	        
            //g.setComposite(AlphaComposite.Src);
            //g.fill(shape);
            g.setPaint(getBackgroundColor().asColor());
            //g.setComposite(AlphaComposite.SrcAtop);
            g.fill(shape);
	        if (this.getBorderVisible()) {
	            g.setPaint(this.getBorderColor().asColor());
	            g.setStroke(new BasicStroke ((float)wid/100,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
	            double arc = ((float)wid)/4. - (float)wid/50;
	            shape = new RoundRectangle2D.Double
	                ((float)wid/100, (float)wid/100, (double)wid-(float)wid/50, (double)hit-(float)wid/50, arc, arc);
	            g.draw(shape);
	        }
        }else {
        		g.setPaint(getBackgroundColor().asColor());
        		g.fillRect(x, y, wid, hit);
        }
	}
	public BufferedImage createImage (Chemical c, int size) {
    	return createImage (c, size, true);
    }    
	public BufferedImage createImage (Chemical c, int size, boolean round) {
        BufferedImage img;
        
            img = new BufferedImage (size, size, BufferedImage.TYPE_INT_ARGB);
            
            Graphics2D g2 = img.createGraphics();
            renderChem (g2, c, size, size, round);
            g2.dispose();
        

         return img;
    }
	
	static private BufferedImage createBlur(BufferedImage src, int rad, float opac){
		
		
    BufferedImage img = new BufferedImage
        (src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D gf =  img.createGraphics();

    BufferedImageOp blur = OperCache.get(rad);
    if (blur == null) {
        int fsize=(rad*2+1)*(rad*2+1);
        int fwid=(rad*2+1);
        float[] blurKernel= new float[fsize];
        float dist=1/(float)fsize;
        for(int i=0;i<fsize;i++){
            blurKernel[i]=dist;
        }

        blur = new ConvolveOp
            (new Kernel(fwid, fwid, blurKernel), 
             ConvolveOp.EDGE_NO_OP, gf.getRenderingHints());

        OperCache.putIfAbsent(rad, blur);
    }
			
  
    //theoretically, three box blurs are approximately
    //one gaussian.
    BufferedImage dst =blur.filter(
    			blur.filter(
    			blur.filter(src, null),
    			null), 
    			null);
    
    /* This makes the image greyscale.
     * Very slow right now
     * 
     */

    gf.setComposite(AlphaComposite.getInstance
                    (AlphaComposite.SRC_OVER, opac));

    gf.drawImage(dst, 0, 0, null);
    gf.dispose();

    return img;
}
	
}
