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

import gov.nih.ncats.molwitch.*;
import gov.nih.ncats.molwitch.SGroup.SGroupBracket;
import gov.nih.ncats.molwitch.SGroup.SGroupType;
import gov.nih.ncats.molwitch.Bond.BondType;
import gov.nih.ncats.molwitch.renderer.Graphics2DParent.*;
import gov.nih.ncats.molwitch.renderer.RendererOptions.DrawOptions;
import gov.nih.ncats.molwitch.renderer.RendererOptions.DrawProperties;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

//import java.awt.image.BufferedImage;
/**
 * 
 * @author peryeata A reference implementation of the ChemicalRenderer.
 * @author katzelda Refactored + ported over for Chemkit
 */
class NchemicalRenderer extends AbstractChemicalRenderer {
	public static final ColorParent transparent = new ColorParent(0, 0, 0, 0);
	private String protProperty = "AMINO_ACID_SEQUENCE";
	private static Font defaultFont;

	static {
		try {

			InputStream is = NchemicalRenderer.class.getResourceAsStream("/DejaVuSans.ttf");
			// Scanner cns = new Scanner(is);
			// URL defaultImage =
			// NchemicalRenderer.class.getResource("/gov/nih/ncgc/nchemical/DejaVuSans.ttf");
			// File fontFile = new File(defaultImage.toURI());
			defaultFont = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (Exception e) {
			e.printStackTrace();
			defaultFont = new Font("Calibri", Font.BOLD, 25);
		}
		// defaultFont=new Font("Arial", Font.PLAIN,25);
	}

	private static Set<String> FORCE_LEFT_HYDROGEN = new HashSet<String>();

	private static Map<String, ColorParent> atomColors = new HashMap<String, ColorParent>();
	static {
		atomColors.put("Cl", new ColorParent(54, 180, 73, 255));
		atomColors.put("F", new ColorParent(54, 180, 73, 255));
		atomColors.put("P", new ColorParent(230, 219, 69, 255));
		atomColors.put("S", new ColorParent(143, 160, 48, 255));
		atomColors.put("Br", new ColorParent(115, 84, 35, 255));
		atomColors.put("C", new ColorParent(58, 58, 58, 255));
		atomColors.put("N", new ColorParent(93, 69, 230, 255));
		atomColors.put("O", new ColorParent(230, 93, 69, 255));
		atomColors.put("H", new ColorParent(58, 58, 58, 255));
		atomColors.put("Na", new ColorParent(48, 143, 160, 255));
		atomColors.put("I", new ColorParent(230, 69, 205, 255));
		/*
		 * 
		 * atomColors.put("C", Color.DARK_GRAY); atomColors.put("H", Color.DARK_GRAY);
		 * atomColors.put("O", Color.red); atomColors.put("N", Color.blue);
		 * atomColors.put("S", Color.yellow.darker()); atomColors.put("P",
		 * Color.ORANGE); atomColors.put("Cl", new Color(0, 200, 0));
		 * atomColors.put("F", new Color(0, 200, 0)); atomColors.put("I",
		 * Color.MAGENTA); atomColors.put("Na", Color.cyan.darker());
		 * atomColors.put("Br", new Color(128, 60, 0)); float[] hsb = new float[3]; for
		 * (String key : atomColors.keySet()) { Color oldC = atomColors.get(key);
		 * Color.RGBtoHSB(oldC.getRed(), oldC.getGreen(), oldC.getBlue(), hsb); oldC =
		 * Color .getHSBColor(hsb[0] + .025f, hsb[1] * .7f, hsb[2] * .9f);
		 * atomColors.put(key, oldC); System.out.println("atomColors.put(\"" + key +
		 * "\", new Color(" + oldC.getRed() +","+ oldC.getGreen() +","+
		 * oldC.getBlue()+","+ oldC.getAlpha() +"));"); }
		 */
		FORCE_LEFT_HYDROGEN.add("O");
		FORCE_LEFT_HYDROGEN.add("Cl");
		FORCE_LEFT_HYDROGEN.add("S");
		FORCE_LEFT_HYDROGEN.add("Br");
		FORCE_LEFT_HYDROGEN.add("I");
		FORCE_LEFT_HYDROGEN.add("F");

	}
	private static Map<Integer, String> subScripts = new HashMap<Integer, String>();
	private static Set<String> subScriptSet = new HashSet<String>();
	static {
		subScripts.put(2, "\u2082");
		subScripts.put(3, "\u2083");
		subScripts.put(4, "\u2084");
		subScripts.put(5, "\u2085");
		subScripts.put(6, "\u2086");
		subScripts.put(7, "\u2087");
		subScripts.put(8, "\u2088");
		subScripts.put(9, "\u2089");
		subScripts.put(0, "\u2080");

		subScriptSet.addAll(subScripts.values());
	}

	private ColorParent drawColor = atomColors.get("C");// new Color (45, 45, 45);
	private RendererOptions displayParams;

	public NchemicalRenderer(RendererOptions options) {
		displayParams = Objects.requireNonNull(options);
	}

	public NchemicalRenderer() {
		this(RendererOptions.createDefault());
	}

	public RendererOptions getOptions() {
		return displayParams;
	}
	/**
	 * 
	 * @param pprop
	 * 
	 *            if this property is n
	 * 
	 */
	public void setSequenceProperty(String pprop) {
		protProperty = pprop;
	}

	public void renderProt(Graphics2DTemp g2, String seq, int x, int y, int width, int height) {
		g2.setColor(new ColorParent(0, 0, 0, 255));
		Font f = new Font("Monospaced", Font.PLAIN, 12);
		g2.setFont(f);
		FontMetrics fm = g2.getFontMetrics();
		int w = fm.stringWidth("........... ...........");

		float rat = ((float) width) / ((float) w);

		Font f2 = f.deriveFont(12 * rat);
		g2.setFont(f2);
		float h = (float) g2.getFontMetrics().getStringBounds("#", g2._delagate).getHeight();
		float wi = (float) g2.getFontMetrics().getStringBounds("#", g2._delagate).getWidth();

		// g2.
		seq = seq.replaceAll("(.{10})", "$1 ");
		seq = seq.replaceAll("([^ ]{10} [^ ]*) ", "$1|");

		int i = 1;
		for (String s : seq.split("\\|")) {
			drawString(g2, s, x + wi, y + i++ * h * 1.2f);

		}
	}

	public static GeomGenerator ggen = new Graphics2DTemp.AWTGeomGenerator();

	/**
	 * @author peryeata
	 * 
	 *         Rendering service Layered approach: 1) Gather Atoms 2) Gather Bonds
	 *         3) Draw bonds, given atom constraints 4) Draw atoms, given bond
	 *         constraints TODO: Plenty.
	 * 
	 */
	@Override
	public void renderChem(Graphics2D g9, Chemical c, int x, int y, int width, int height) {

		Graphics2DTemp g2 = new Graphics2DTemp(g9);
		String s = c.getProperty(protProperty);
		if (s != null) {
			if (!s.trim().equals("")) {
				renderProt(g2, s, x, y, width, height);
				return;
			}
		}
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		// System.out.println("There are " + c.getSGroupCount() + " sgroups");

		ArrayList<double[]> toAdd = new ArrayList<>();

		boolean skeleton = false;

		boolean assumeRelative = displayParams.getDrawOption(DrawOptions.DRAW_STEREO_LABELS_AS_RELATIVE);
				
		boolean assumeStarRelative = displayParams.getDrawOption(DrawOptions.DRAW_STEREO_LABELS_AS_STARRED);
		
		boolean drawStereoParentheses = displayParams.getDrawOption(DrawOptions.DRAW_STEREO_LABELS_PARENTHESES);
		

		boolean drawBonds = displayParams.getDrawOption(DrawOptions.DRAW_BONDS);
				
		boolean drawSymbols = displayParams.getDrawOption(DrawOptions.DRAW_SYMBOLS);
				
		boolean drawCarbon = displayParams.getDrawOption(DrawOptions.DRAW_CARBON);
				
		boolean centerAllDoubleBonds = displayParams.getDrawOption(DrawOptions.DRAW_CENTER_ALL_DOUBLE_BONDS);
		
		boolean drawStereo = displayParams.getDrawOption(DrawOptions.DRAW_STEREO_BONDS);
				
		boolean drawProportion = displayParams.getDrawOption(DrawOptions.DRAW_PROPORTIONAL_ATOM_RADIUS);
		
		boolean drawResize = displayParams.getDrawOption(DrawOptions.DRAW_PROPORTION_AVERAGE_BOND_LENGTH);
				
		boolean halfColoredBonds = displayParams.getDrawOption(DrawOptions.DRAW_ATOM_COLOR_ON_BONDS);
				
		boolean PROP_DASH_SPACING = displayParams.getDrawOption(DrawOptions.DRAW_CONSTANT_DASH_WIDTH);
				
		boolean DrawDashWedge = displayParams.getDrawOption(DrawOptions.DRAW_STEREO_DASH_AS_WEDGE);
				
		boolean centerNonRingDoubleBonds = displayParams.getDrawOption(DrawOptions.DRAW_CENTER_NONRING_DOUBLE_BONDS);
		
		boolean drawTerminalHydrogens = displayParams.getDrawOption(DrawOptions.DRAW_IMPLICIT_HYDROGEN);
		
		boolean drawTerminalCarbons = displayParams.getDrawOption(DrawOptions.DRAW_TERMINAL_CARBON);
	

		boolean drawColorScheme = !displayParams.getDrawOption(DrawOptions.DRAW_GREYSCALE);
				
		boolean drawStereoLabels = displayParams.getDrawOption(DrawOptions.DRAW_STEREO_LABELS);
			
		boolean stereoFromMap = displayParams.getDrawOption(DrawOptions.DRAW_STEREO_GIVEN_BY_MAP);
				
		boolean highlightMapAtoms = displayParams.getDrawOption(DrawOptions.DRAW_HIGHLIGHT_MAPPED);
				
		boolean highlightHalo = displayParams.getDrawOption(DrawOptions.DRAW_HIGHLIGHT_WITH_HALO);
				
		boolean highlightMonochromatic = displayParams.getDrawOption(DrawOptions.DRAW_HIGHLIGHT_MONOCHROMATIC);

		boolean highlightShowAtom = displayParams.getDrawOption(DrawOptions.DRAW_HIGHLIGHT_SHOW_ATOM);
		
		boolean stereoReplace = displayParams.getDrawOption(DrawOptions.DRAW_STEREO_LABELS_AS_ATOMS);
		
		boolean forceStereomono = displayParams.getDrawOption(DrawOptions.DRAW_STEREO_FORCE_MONOCHROMATIC);
				

		boolean drawAlleneCarbon = true;
		boolean stereoColoring = true;

		// boolean drawCarbon= true;
		boolean showMappedNumbers = displayParams.getDrawOption(DrawOptions.DRAW_SHOW_MAPPED);
			

		boolean drawRadius = false;
		if (!drawSymbols)
			drawRadius = true;
		// drawTerminalHydrogens
		ColorParent STEREO_COLOR_UNKNOWN = new ColorParent(255, 0, 0, 255);
		ColorParent STEREO_COLOR_KNOWN = new ColorParent(0, 178, 0, 255);

		/*
		 * PROP_KEYS_VALUES.put( PROP_KEY_BOND_EXPECTED_LENGTH,DEF_BOND_AVG );
			 PROP_KEYS_VALUES.put( PROP_KEY_BOND_STROKE_WIDTH_FRACTION,DEF_STROKE_PERCENT );
			 PROP_KEYS_VALUES.put( PROP_KEY_ATOM_LABEL_FONT_FRACTION,DEF_FONT_PERCENT );
			 PROP_KEYS_VALUES.put( PROP_KEY_BOND_DOUBLE_GAP_FRACTION,DEF_DBL_BOND_GAP );
			 PROP_KEYS_VALUES.put( PROP_KEY_BOND_DOUBLE_LENGTH_FRACTION,DEF_DBL_BOND_DISTANCE );
			 PROP_KEYS_VALUES.put( PROP_KEY_ATOM_LABEL_BOND_GAP_FRACTION,DEF_FONT_GAP_PERCENT );
			 PROP_KEYS_VALUES.put( PROP_KEY_BOND_STEREO_WEDGE_ANGLE,DEF_WEDGE_ANG );
			 PROP_KEYS_VALUES.put( PROP_KEY_BOND_OVERLAP_SPACING_FRACTION,DEF_SPLIT_RATIO );
			 PROP_KEYS_VALUES.put( PROP_KEY_BOND_STEREO_DASH_NUMBER,DEF_NUM_DASH );
		 */
		final float DEF_BOND_AVG = (float) this.displayParams.getDrawPropertyValue(DrawProperties.BOND_EXPECTED_LENGTH);
		final float DEF_STROKE_PERCENT = (float) this.displayParams.getDrawPropertyValue(DrawProperties.BOND_STROKE_WIDTH_FRACTION);
//		this.displayParams.DEF_STROKE_PERCENT;
		final float DEF_FONT_PERCENT = (float) this.displayParams.getDrawPropertyValue(DrawProperties.ATOM_LABEL_FONT_FRACTION);
//		this.displayParams.DEF_FONT_PERCENT;
		final float DEF_DBL_BOND_GAP = (float) this.displayParams.getDrawPropertyValue(DrawProperties.BOND_DOUBLE_GAP_FRACTION);
//		this.displayParams.DEF_DBL_BOND_GAP;
		final float DEF_DBL_BOND_DISTANCE = (float) this.displayParams.getDrawPropertyValue(DrawProperties.BOND_DOUBLE_LENGTH_FRACTION);
		
//				this.displayParams.DEF_DBL_BOND_DISTANCE;
		final float DEF_FONT_GAP_PERCENT = (float) this.displayParams.getDrawPropertyValue(DrawProperties.ATOM_LABEL_BOND_GAP_FRACTION);
//		this.displayParams.DEF_FONT_GAP_PERCENT;
		final float DEF_WEDGE_ANG = (float) this.displayParams.getDrawPropertyValue(DrawProperties.BOND_STEREO_WEDGE_ANGLE);
//		this.displayParams.DEF_WEDGE_ANG;
		final float DEF_SPLIT_RATIO = (float) this.displayParams.getDrawPropertyValue(DrawProperties.BOND_OVERLAP_SPACING_FRACTION);
//		this.displayParams.DEF_SPLIT_RATIO;
		final int DEF_NUM_DASH =  (int) this.displayParams.getDrawPropertyValue(DrawProperties.BOND_STEREO_DASH_NUMBER);
//		this.displayParams.DEF_NUM_DASH;

		final ColorParent[] pallete = this.displayParams.getHighlightColors().stream().toArray(i-> new ColorParent[i]);

		final float HALO_RADIUS_MULTIPLY = .20f;
		final float HALO_RADIUS_FUDGE = .5f;

		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		float hMarge = Math.max(3, width * 0.25f);
		float wMarge = Math.max(3, height * 0.25f);
		if (this.getBorderVisible()) {
			hMarge += 3;
			wMarge += 3;
		}

		g2.setColor(drawColor);

		float BONDAVG = 0f;
		int bcount = 0;
		Rectangle2D boundingBox = BoundingBox.computeBoundingBoxFor(c);
		
		minX = boundingBox.getMinX();
		maxX = boundingBox.getMaxX();
		minY = boundingBox.getMinY();
		maxY = boundingBox.getMaxY();

		for (Bond cb : c.getBonds()) {
			bcount++;
			Atom[] ca = new Atom[] { cb.getAtom1(), cb.getAtom2() };
			
			double length = Math.sqrt(ca[0].getAtomCoordinates().distanceSquaredTo(ca[1].getAtomCoordinates()));
			BONDAVG += length;
			float nx = 0;
			float ny = 0;
			int bondType = cb.getBondType().getOrder();
			Bond.Stereo sType = cb.getStereo();
			if (sType != null && drawStereo) {
				switch (sType) {
				case DOWN:
					bondType = 5;
					break;
				case UP:
					bondType = 6;
					break;
				default:
				}
			}
			if (bondType == Bond.BondType.DOUBLE.getOrder() || bondType == Bond.BondType.AROMATIC.getOrder()) {
				if (ca[0].getBondCount() == 1 || ca[1].getBondCount() == 1)
					bondType = -1;
				else {
					int bondCount = 0;
					boolean ringBond = false;
					for (int i = 0; i < 2; i++) {
						for (Bond cab : ca[i].getBonds()) {

							Atom can = cab.getOtherAtom(ca[i]);
							int weight = 1;
							if (cab.isInRing()) {
								weight *= 4;
								ringBond = true;
							}
							// Don't know what the point of this is ...
							// It has something to do with deciding which side
							// an asymmetrical double bond will go to
							// It SHOULD favor the side INSIDE a ring, if one
							// exists
							// This is approximated by finding a weighted
							// average of the neighbor atoms coordinates
							// where ring-bonds are weighed more heavily.
							for (Bond b : can.getBonds()) {

								if (b.isInRing()) {
									switch (b.getBondType()) {
									case DOUBLE:
									case AROMATIC:
										weight *= 1.75;
										break;
									default:
										break;
									}
								}
							}
							AtomCoordinates coords = can.getAtomCoordinates();
							nx += coords.getX() * weight;
							ny += coords.getY() * weight;
							bondCount += weight;
						}
					}
					nx /= (float) bondCount;
					ny /= (float) bondCount;
					ringBond = ringBond && cb.isInRing();
					if (!ringBond && centerNonRingDoubleBonds) {
						bondType = -1;
					}
				}
			}
			AtomCoordinates c1 =cb.getAtom1().getAtomCoordinates();
			AtomCoordinates c2 =cb.getAtom2().getAtomCoordinates();
			toAdd.add(new double[] { c1.getX(), c1.getY(), c2.getX(), c2.getY(), bondType, nx, ny, bcount - 1 });
			// g2.drawLine((int)c1[0]*dist,(int)c1[1]*dist, (int)c2[0]*dist,
			// (int)c2[1]*dist);
		}
		if (BONDAVG == 0 || !drawResize) {
			BONDAVG = DEF_BOND_AVG;
		} else {
			BONDAVG /= bcount;
		}
		double maxW = BONDAVG * Math.tan(DEF_WEDGE_ANG);

		float defWidth = 3;
		float defHeight = 3;
		double centerX = (maxX + minX) / 2f;
		double centerY = (maxY + minY) / 2f;
		float ncenterX = width / 2f+x;
		float ncenterY = height / 2f+y;
		double cwidth = (maxX - minX);
		double cheight = (maxY - minY);

		if (cwidth <= 0.1) {
			cwidth = defWidth;
		}
		if (cheight <= 0.1) {
			cheight = defHeight;
		}
		// System.out.println("Size:" + cwidth + "," + cheight);

		double adjW = Math.max((width - wMarge) / cwidth, 1);
		double adjH = Math.max((height - hMarge) / cheight, 1);
		double resize = Math.min(adjW, adjH);
		int newMarge = Math.max(
				g2.getFontMetrics(defaultFont.deriveFont((float) (DEF_FONT_PERCENT * resize * BONDAVG))).getHeight(),
				0);

		adjW = (width - wMarge - newMarge) / cwidth;
		adjH = (height - hMarge - newMarge) / cheight;
		resize = Math.min(adjW, adjH);

		AffineTransformParent centerTransform = ggen.makeAffineTransform();

		centerTransform.translate(ncenterX, ncenterY);
		// centerTransform.rotate(Math.PI/9);
		centerTransform.scale(resize, -resize);
		centerTransform.translate(-centerX, -centerY);

		float bondWidth = (float) (DEF_STROKE_PERCENT * resize * BONDAVG);
		float braketFrac = 0.7f;
		float braketWidth = (float) (DEF_STROKE_PERCENT * resize * BONDAVG * braketFrac);
		BasicStroke solid = new BasicStroke(bondWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		BasicStroke solidHalo = new BasicStroke((float) (bondWidth + HALO_RADIUS_MULTIPLY * resize * BONDAVG),
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		BasicStroke solidThin = new BasicStroke(braketWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		float dash[] = { 2 * bondWidth };
		BasicStroke dashed = new BasicStroke(bondWidth / 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, bondWidth,
				dash, 0.0f);
		BasicStroke solidREC = new BasicStroke(bondWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
		float fsize = (float) (DEF_FONT_PERCENT * resize * BONDAVG);
		Font setfont = defaultFont.deriveFont(fsize);
		Font brafont = defaultFont.deriveFont(fsize * braketFrac);
		g2.setFont(setfont);
		g2.setStroke(solid);

		/*
		 * 
		 * if(fsize<8){ drawSymbols=false; }else{ }
		 */

		FontMetrics fm = g2.getFontMetrics();

		ArrayList<DisplayLabel> toAddLabelsD = new ArrayList<DisplayLabel>();

		Map<Atom, AtomDrawProps> atompDProps = new HashMap<>();
		Map<Atom, Chirality> stereoMap = null;
		if (drawStereoLabels) {
			
			if(stereoFromMap) {
				stereoMap = new HashMap<>();
				for(Atom ca : c.getAtoms()) {
					//dkatzel 11/ 2018
					//this is to make it the same as the old ncgc renderer
					//which any atom map value not set to 1 or 2 is considered either (3)
					//even 0 !!!
					int value = ca.getAtomToAtomMap().orElse(3);
					stereoMap.put(ca, Chirality.valueByParity(value));
				}
			}else {
				stereoMap = c.getAllStereocenters().stream().filter(Stereocenter::isDefined).map(Stereocenter::getCenterAtom).filter(a -> a.getChirality() != null)
					.collect(Collectors.toMap(Function.identity(), a -> a.getChirality()));
			}

		}

		if (highlightMapAtoms) {
			 if (c.hasAtomToAtomMappings()) {
				 highlightMapAtoms = true;
			 } else {
				 highlightMapAtoms = false;
			 }
		}
		int atomIndex = -1;
		Chemical.OpticalActivity opticalActivity = c.computeOpticalActivity().orElse(null);
		Chemical.StereochemistryType stereochemistryType = c.computeStereochemistryType().orElse(null);

		for (Atom ca : c.getAtoms()) {
			atomIndex++;
			boolean drawHydrogens = true;
			boolean forceDraw = false;
			boolean highlighted = false;
			boolean fakeAtom = false;
			boolean isCarbon = (ca.getSymbol().equals("C"));
			boolean isStereo = false;
			boolean forceHalo = false;

			String sm = ca.getSymbol();
			ColorParent col = drawColor;
			ColorParent hcol = transparent;
			List<String> attachments = new ArrayList<String>();
			List<Integer> attachmentLOC = new ArrayList<Integer>();
			List<Float> attachmentSIZE = new ArrayList<Float>();
			List<ColorParent> attachmentCOL = new ArrayList<ColorParent>();

			// TODO ignore alias
			/*
			 * String sm = ca.getAlias(); if (!sm.equals(ca.getSymbol())) { fakeAtom = true;
			 * }
			 * 
			 */
			// have to make a normal if statement
			// because can't set effectively final variable sm
			// in a lambda
			OptionalInt rGroupIndex = ca.getRGroupIndex();

			if (rGroupIndex.isPresent()) {
				sm = this.getRGroupText(rGroupIndex.getAsInt());
			}

			/*
			 * //A is query atom means "any?" //mol file standard can alias the atom //is
			 * list is for query atoms if (ca.getSymbol().equals("A") &&
			 * ca.getAlias().equals("A")) { sm = "*"; } ColorParent col = drawColor;
			 * ColorParent hcol = transparent; List<String> attachments = new
			 * ArrayList<String>(); List<Integer> attachmentLOC = new ArrayList<Integer>();
			 * List<Float> attachmentSIZE = new ArrayList<Float>(); List<ColorParent>
			 * attachmentCOL = new ArrayList<ColorParent>(); if(ca.isList()){ int[]
			 * l=ca.getAtomList(); if(l!=null){ String at="["; for(int i=0;i<l.length;i++){
			 * if(i>0){ at+=","; } at+=Chemical.atomSymbols[l[i]-1]; } at+="]";
			 * attachments.add(at); attachmentLOC.add(1 | 2 | 4 | 8);
			 * attachmentSIZE.add(0.4f); attachmentCOL.add(col); } }
			 */
			if (highlightMapAtoms) {
				col = drawColor;
//				System.out.println("hightlight map atoms = " + highlightMapAtoms);
//				System.out.println("highlightMonochromatic = " + highlightMonochromatic);
//				
				int map = ca.getAtomToAtomMap().orElse(0);
				if(map >0) {
//					System.out.println("map > 0 = " + map);
					
					if (!highlightMonochromatic) { 
						col= pallete[map%pallete.length]; 
						} else {
							col = pallete[2]; 
							} 
					if(highlightShowAtom) {
						forceDraw = true; 
					}
					highlighted = true;
				}
				
				 
			} else {
				if (drawColorScheme) {
					col = getColorForSymbol(sm);
				}
			}
			if (drawStereoLabels) {
				if (forceStereomono) {
					col = drawColor;
				}

				String attach2 = null;
				ColorParent ncol = col;
				Chirality chirality = stereoMap.get(ca);
				if(chirality !=null) {
					switch (chirality) {
						case R:
							attach2 = "(R)";
							ncol = STEREO_COLOR_KNOWN;
							if (assumeStarRelative) {
								attach2 = "(R*)";
							}
							if (!assumeRelative)
								break;
						case Unknown:
						case Parity_Either:
							//from TP: The big thing is that if the molecule is marked as racemic or (+/-) stereochemistry,
							// //then it's definitely either R or S
							if (Chemical.StereochemistryType.RACEMIC.equals(stereochemistryType) || Chemical.OpticalActivity.PLUS_MINUS.equals(opticalActivity)) {
								attach2 = "(RS)";
								ncol = STEREO_COLOR_KNOWN;
							} else {
								attach2 = "(*)";
								ncol = STEREO_COLOR_UNKNOWN;
							}
							break;
						case S:
							attach2 = "(S)";
							ncol = STEREO_COLOR_KNOWN;
							if (assumeStarRelative) {
								attach2 = "(S*)";
							}
							if (!assumeRelative) {
								break;
							}
							attach2 = "(*)";
							ncol = STEREO_COLOR_UNKNOWN;
							// case ChemicalAtom.STEREO_SR:
							// attach2 = "(SR)";
							// ncol = STEREO_COLOR_KNOWN;
							// break;

						default:
					}
				}
				if (attach2 != null) {
					if (!drawStereoParentheses) {
						attach2 = attach2.replace("(", "").replace(")", "");
					}
					isStereo = true;
					if (stereoReplace) {
						if (stereoColoring) {
							col = ncol;
						}
						if (sm.equals("C")) {
							sm = attach2;
						} else {
							sm = sm + attach2;
						}
						forceDraw = true;
						g2.setFont(defaultFont.deriveFont(fsize * 0.7f));
						fm = g2.getFontMetrics();
					} else {
						// should restrict full atom highlight for
						// small images
						if (stereoColoring) {
							if (highlightShowAtom)
								forceDraw = true;
							highlighted = true;
							int[] cc = ncol.getRGB();
							col = new ColorParent(cc[0], cc[1], cc[2], 55);
							forceHalo = true;
						}
						attachments.add(attach2);
						// attachmentLOC.add(1|2|4|8);
						attachmentLOC.add(-1);
						attachmentSIZE.add(.7f);
						if (stereoColoring) {
							attachmentCOL.add(ncol);
						} else {
							attachmentCOL.add(col);
						}
						g2.setFont(defaultFont.deriveFont(Font.BOLD, fsize * 1.0f));
						fm = g2.getFontMetrics();
					}
				}
			}
			if(drawTerminalCarbons){
				// String attatch="";
				if (drawCarbon) {
					drawHydrogens = true;
				} else if (isCarbon) {
					if ((forceDraw && !isStereo) || ca.getBondCount() < 2 || ca.getCharge() != 0 || ca.getRadicalValue() != 0
							|| ca.isIsotope()) {
						drawHydrogens = true;
					} else {
						drawHydrogens = false;
					}
				}
				if (drawHydrogens) {
					drawHydrogens = drawTerminalHydrogens && !fakeAtom;
				}
			}else{
				//don't draw terminal carbons
				if(isCarbon){
					if(ca.getBondCount() < 2){
						
						//terminal C
						drawCarbon = forceDraw || ca.getCharge() != 0 || ca.getRadicalValue() != 0 || ca.isIsotope();
					}else{
						drawCarbon=false;
					}
					drawHydrogens = drawCarbon;
				}
//				drawHydrogens = drawTerminalHydrogens;
			}
			String attatch = "";
			
			float[] p = new float[2];
			centerTransform.transform(ca.getAtomCoordinates().xy(), 0, p, 0, 1);
			// tyler says it's not worth drawing to proportion
			// since we don't draw atom balls to scale
			// not worth implementing.

			/*
			 * if (drawProportion) { float atomRad = (ca.getAtomNo() <
			 * Chemical.atomRadius.length && drawProportion) ? (float)
			 * Chemical.atomRadius[ca .getAtomNo()] : 1f;
			 * g2.setFont(defaultFont.deriveFont(fsize * atomRad)); fm =
			 * g2.getFontMetrics(); }
			 */
			float w = fm.stringWidth(sm) / 2;
			Rectangle2D _ERASE_ME = fm.getStringBounds(sm, g2._delagate);
			Rectangle2DParent rect = ggen.makeRectangle(_ERASE_ME.getX(), _ERASE_ME.getY(), _ERASE_ME.getWidth(),
					_ERASE_ME.getHeight());
			w = (float) rect.getWidth() / 2;
			float h = (float) rect.getHeight() / 3;
			g2.setBackground(transparent);
			float radius = Math.max(w, h * 6 / 5) + 2 * w / 10;
			boolean drawHalo = (highlightHalo && highlighted) || forceHalo;

			if (drawHalo) {
				g2.setColor(col);
				float prad = radius;
				radius *= HALO_RADIUS_FUDGE;
				radius += HALO_RADIUS_MULTIPLY * resize * BONDAVG;
				g2.fillP(ggen.makeEllipse(p[0] - radius, p[1] - radius, radius * 2, radius * 2));
				radius = prad;
				hcol = col;
				col = drawColor;
			}

			if (ca.isIsotope()) {
				String attatch2 = "";
				attatch2 = getSuperScriptString(ca.getMassNumber());
				if (!attatch2.equals("")) {
					attachments.add(attatch2);
					attachmentLOC.add(4);
					attachmentSIZE.add(1f);
					attachmentCOL.add(col);
					forceDraw = true;
				}
			}
			if (ca.getImplicitHCount() > 0) {
				if (drawHydrogens) {
					attatch += "H";
					int hcount = ca.getImplicitHCount();
					String ss = subScripts.get(hcount);
					if (ss != null) {
						attatch += ss;
					}
				}
			}

			if (ca.getCharge() != 0) {
				String attatch2 = "";
				int mag = Math.abs(ca.getCharge());

				if (mag > 1) {
					attatch2 = getSuperScriptString(mag);
				}

				if (ca.getCharge() > 0) {
					attatch2 += "\u207A";
				} else {
					attatch2 += "\u207B";
				}
				if (!attatch2.equals("")) {
					attachments.add(attatch2);
					attachmentLOC.add(1);
					attachmentSIZE.add(1f);
					attachmentCOL.add(col);
					forceDraw = true;
				}
			}

			if (!attatch.equals("")) {
				attachments.add(attatch);
				if (ca.getBondCount() == 0) {
					if (FORCE_LEFT_HYDROGEN.contains(ca.getSymbol()))
						attachmentLOC.add(4);
					else
						attachmentLOC.add(1);
				} else if (ca.getBondCount() == 1) {
					attachmentLOC.add(1 | 4);
				} else {
					attachmentLOC.add(1 | 2 | 4 | 8);
				}
				attachmentCOL.add(col);
				attachmentSIZE.add(1f);
				forceDraw = true;
			}
			int radicalValue = ca.getRadicalValue();
			if (radicalValue != 0) {
				String attatch2 = "";
				switch (radicalValue) {
				// NOTE CDK can't distinguish between divalent singlets and triplets
				// so pretend they're both divalent singlets for now
				// because that's what CDK's mol writer does....
				case 1:
					attatch2 = ".";
					break;
				case 2:
					attatch2 = "\u200A.";
					break;
				// NOTE the attach2s for these was commented out already
				// case ChemicalAtom.ATOM_RADICAL_2_SINGLET:
				// attatch2 = "\u200A.";
				// break;
				// case ChemicalAtom.ATOM_RADICAL_2_TRIPLET:
				// attatch2 = "\u200A...";
				// break;
				default:
				}
				if (!attatch2.equals("")) {
					attachments.add(attatch2);
					attachmentLOC.add(2 | 8);
					attachmentCOL.add(col);
					attachmentSIZE.add(1f);
					forceDraw = true;
				}
			}
			
			if (showMappedNumbers) {
				int amap = ca.getAtomToAtomMap().orElse(0);

				if (amap != 0) {
					String attatch2 = "";
					attatch2 = getSuperScriptString(amap) + "";
					if (!attatch2.equals("")) {
						attachments.add(attatch2);
						attachmentLOC.add(1 | 2 | 4 | 8);
						attachmentCOL.add(col);
						attachmentSIZE.add(1f);
					}
				}
			}
			 
			boolean drawThisAtom = forceDraw
					|| ((!isCarbon || drawCarbon) && (!isCarbon || drawTerminalCarbons || drawCarbon));
			boolean drawAttachments = attachments.size() > 0;
			if (ca.getBondCount() == 2) {
				if (isCarbon) {

					boolean onlyDoubleBonds = !ca.getBonds().stream()
							.filter(cab -> cab.getBondType() != Bond.BondType.DOUBLE).findAny().isPresent();
					if (onlyDoubleBonds) {
						drawThisAtom = drawAlleneCarbon;
					}
					// boolean leave = false;
					// for (int i = 0; i < ca.getBondCount(); i++) {
					//
					// switch (ca.getBond(i).getType()) {
					// case ChemicalBond.BOND_TYPE_DOUBLE:
					// break;
					// default:
					// leave = true;
					// }
					// if (leave)
					// break;
					// }
					// if (!leave) {
					// drawThisAtom = drawAlleneCarbon;
					// }
				}
			}

			if (drawRadius) {
				if (!drawSymbols) {
					if (drawThisAtom) {
						g2.setColor(col);
						g2.fillP(ggen.makeEllipse(p[0] - radius, p[1] - radius, radius * 2, radius * 2));
					} else {
						radius = 0;
					}
				}
			}
			if (drawSymbols) {
				if (!drawThisAtom) {
					radius = 0;
				}
				if (drawAttachments) {
					int used = 0;
					for (int j = 0; j < attachments.size(); j++) {
						String att = attachments.get(j);
						float size = attachmentSIZE.get(j);
						ColorParent acol = attachmentCOL.get(j);
						int supported = attachmentLOC.get(j);
						int cardPos = supported;

						float[] nv = new float[] { 0, 1 };

						if (supported != -1) {
							int avail = supported & (~used);
							cardPos = getAttachCardPos(ca, att, avail);
							used = used | (1 << cardPos);
						} else {
							nv = getNormVecAway(ca);

						}

						Font ofont = g2.getFont();
						Font fnt2 = ofont.deriveFont(fsize * size);
						g2.setFont(fnt2);
						FontMetrics fm2 = g2.getFontMetrics();
						g2.setFont(ofont);

						Collection<Entry<String, float[]>> smap = getAttachPos(att, w, h, p, fm2, g2, cardPos, nv);
						if (smap != null) {
							for (Entry<String, float[]> ent : smap) {
								DisplayLabel dl = new DisplayLabel(ent.getKey(), fnt2, ent.getValue()[0],
										ent.getValue()[1], acol);
								toAddLabelsD.add(dl);
								dl.atomGroup = ca;

								// toAddLabels.add(ent.getKey());
								// toAddLabelsPos.add(ent.getValue());
								// toAddLabelsColor.add(acol);
								// toAddLabelsFont.add(fnt2);
							}
						}
					}
				}
				if (drawThisAtom) {
					DisplayLabel dl = new DisplayLabel(sm, g2.getFont(), p[0] - w, p[1] + h, drawColor);
					toAddLabelsD.add(dl);
					dl.atomGroup = ca;
					// toAddLabels.add(sm);
					// toAddLabelsPos.add(new float[] { p[0] - w, p[1] + h });
					if (highlighted && drawRadius) {
						dl.c = drawColor;
					} else {
						dl.c = col;
					}

				}
			}

			radius = radius * DEF_FONT_GAP_PERCENT;

			atompDProps.put(ca, new AtomDrawProps());
			atompDProps.get(ca).highlight = highlighted;
			atompDProps.get(ca).dcolor = col;
			atompDProps.get(ca).hcolor = hcol;
			atompDProps.get(ca).radius = radius;
			g2.setFont(defaultFont.deriveFont(fsize));
			fm = g2.getFontMetrics();
		}
		g2.setColor(drawColor);
		if (drawBonds) {
			BondProps bp = new BondProps();

			bp.DEF_DBL_BOND_GAP = DEF_DBL_BOND_GAP;
			bp.DEF_DBL_BOND_DISTANCE = DEF_DBL_BOND_DISTANCE;
			bp.BONDAVG = BONDAVG;
			bp.DEF_NUM_DASH = DEF_NUM_DASH;
			bp.bondWidth = bondWidth;
			bp.DEF_SPLIT_RATIO = DEF_SPLIT_RATIO;
			bp.DrawDashWedge = DrawDashWedge;
			bp.PROP_DASH_SPACING = PROP_DASH_SPACING;
			bp.centerAllDoubleBonds = centerAllDoubleBonds;
			bp.halfColoredBonds = halfColoredBonds;
			bp.solidREC = solidREC;
			bp.maxWedgeWidth = maxW;

			if (highlightHalo) {
				bp.highlightHalo = true;
				bp.drawBonds(g2, c, toAdd, solidHalo, dashed, centerTransform, atompDProps);
			}
			bp.highlightHalo = false;
			bp.drawBonds(g2, c, toAdd, solid, dashed, centerTransform, atompDProps);

		}

		g2.setStroke(solid);

		if (!skeleton && drawSymbols) {
			for (int i = 0; i < toAddLabelsD.size(); i++) {
				DisplayLabel dl = toAddLabelsD.get(i);
				g2.setFont(dl.dfont);
				g2.setColor(dl.c);
				Rectangle2D s333 = drawString(g2, dl.lab, dl.x, dl.y);
				// g2.drawd(s333);
				dl.bbox = s333;
			}
		}
		g2.setFont(setfont);
		g2.setStroke(solid);

		g2.setColor(drawColor);

//		System.out.println("Before sgroup call BoundingBox = " + BoundingBox.computeBoundingBoxFor(c));
		List<SGroup> cgs = c.getSGroups();
//		System.out.println("sgroups = " + cgs);
//		System.out.println("after sgroup call BoundingBox = " + BoundingBox.computeBoundingBoxFor(c));
//
		if (cgs != null && !cgs.isEmpty()) {
			g2.setFont(brafont);
			for (SGroup cg : cgs) {
				if(cg == null){
					continue;
				}
				Rectangle2D.Float rect = computeBracketCoordsFor(cg);
				if(rect == null){
					continue;
				}
				float[] coord = new float[] { 	rect.x, rect.y, rect.x, 
						rect.y + rect.height, rect.x + rect.width,
						rect.y, rect.x + rect.width, 
						rect.y + rect.height };
				float[] ncoord = new float[8];
			
				
				
//				rect.tra
//				centerTransform.createTransformedShape(pSrc)
				centerTransform.transform(coord, 0, ncoord, 0, 4);
//				System.out.println("atom coords = " + Arrays.toString(coord));
//				System.out.println("transformed coords = " + Arrays.toString(ncoord));
//				System.out.println(cg.getType());
				
				Rectangle2D.Float nrect = new Rectangle2D.Float(ncoord[0], ncoord[1], 
						Math.abs(ncoord[4]-ncoord[0]), Math.abs(ncoord[3]-ncoord[1]));
				// Multiple groups need more padding
//				System.out.println("SgroupType = " + cg.getType());
//				if (cg.getType() == SGroupType.MULTIPLE) {
//					Rectangle2D rc = new Rectangle2D.Double(ncoord[2], ncoord[3], ncoord[4] - ncoord[0],
//							ncoord[5] - ncoord[1]);
//					rc = getBoundsForGroup(toAddLabelsD, cg.getAtoms()::iterator, rc);
//					ncoord[0] = (float) rc.getMinX();
//					ncoord[1] = (float) rc.getMinY();
//					ncoord[2] = (float) rc.getMinX();
//					ncoord[3] = (float) (rc.getMaxY());
//					//TODO dkatzel shouldn't 5 and 7 be switched?
//					ncoord[4] = (float) (rc.getMaxX());
//					ncoord[5] = (float) (rc.getMaxY());
//					ncoord[6] = (float) rc.getMaxX();
//					ncoord[7] = (float) (rc.getMinY());
//
//				}
				g2.setStroke(solidThin);
				
				float bracketWidth= nrect.width/10;
//				g2.drawP(ggen.makeLine(nrect.getX(), nrect.getY(), nrect.getX(), nrect.getMaxY()));
//				g2.drawP(ggen.makeLine(nrect.getX(), nrect.getY(), nrect.getX()+bracketWidth, nrect.getY()));
//				g2.drawP(ggen.makeLine(nrect.getX(), nrect.getMaxY(), nrect.getX()+bracketWidth, nrect.getMaxY()));
//				
//				g2.drawP(ggen.makeLine(nrect.getMaxX(), nrect.getY(), nrect.getMaxX(), nrect.getMaxY()));
//				g2.drawP(ggen.makeLine(nrect.getMaxX(), nrect.getY(), nrect.getMaxX()-bracketWidth, nrect.getY()));
//				g2.drawP(ggen.makeLine(nrect.getMaxX(), nrect.getMaxY(), nrect.getMaxX()-bracketWidth, nrect.getMaxY()));
//				
				
				float len1 = (ncoord[0] - ncoord[6]) * (ncoord[0] - ncoord[6]); // +
																				// (ncoord[1]
																				// -
																				// ncoord[7])
																				// *
																				// (ncoord[1]
																				// -
																				// ncoord[7]);

				len1 = (float) Math.sqrt(len1) / 2;
				float len2 = (ncoord[6] - ncoord[4]) * (ncoord[6] - ncoord[4])
						+ (ncoord[5] - ncoord[7]) * (ncoord[5] - ncoord[7]);
				len2 = (float) Math.sqrt(len2);
				float bsize = .2f;
				
				
				g2.drawP(ggen.makeLine(ncoord[0], ncoord[1], ncoord[2], ncoord[3]));
				g2.drawP(ggen.makeLine(ncoord[0], ncoord[1], ncoord[0] + len1 * bsize, ncoord[1]));
				g2.drawP(ggen.makeLine(ncoord[2], ncoord[3], ncoord[2] + len1 * bsize, ncoord[3]));
				g2.drawP(ggen.makeLine(ncoord[4], ncoord[5], ncoord[6], ncoord[7]));
				g2.drawP(ggen.makeLine(ncoord[4], ncoord[5], ncoord[4] - len1 * bsize, ncoord[5]));
				g2.drawP(ggen.makeLine(ncoord[6], ncoord[7], ncoord[6] - len1 * bsize, ncoord[7]));

				
//				System.out.println("minX " + minX + " minY = " + minY + "maxX = " + maxX + " maxY" + maxY);
				float[] transformed = new float[4];
				
				centerTransform.transform(new float[] {(float)minX,  (float) minY, (float) maxX, (float) maxY}, 0, transformed,0, 2);
//				System.out.println("transformed mins = " + Arrays.toString(transformed));
				
				Optional<String> subs = cg.getSubscript();
				Optional<String> supsOpt = cg.getSuperscript();
				
//				System.out.println("subs = " + subs);
//				System.out.println("sups = " + supsOpt);
				if(supsOpt.isPresent() && (cg.getType() == SGroupType.MULTIPLE || supsOpt.get().equals("eu")) ){
					supsOpt = Optional.empty();
					
				}
				
				if(supsOpt.isPresent() || subs.isPresent()){
					g2.setFont(defaultFont.deriveFont(fsize * 0.7f));
					fm = g2.getFontMetrics();
	
					if(subs.isPresent()){
						float h2 = (float) (fm.getStringBounds(subs.get(), g2._delagate).getHeight());
		
						drawString(g2, " " + subs.get(), ncoord[4], ncoord[5] + h2 * .33f);
					}
					if(supsOpt.isPresent()){
						String sups = supsOpt.get();
						float h1 = (float) (fm.getStringBounds(sups, g2._delagate).getHeight());
						drawString(g2, " " + sups, ncoord[6], ncoord[7] + h1 * .33f);
					}
				}
			}
		}
		 
	}

	private static Rectangle2D.Float computeBracketCoordsFor(SGroup cg){
		Rectangle2D rt;
		if(cg.bracketsSupported()){
			
			//framework implementation supports brackets so use those
			if(!cg.hasBrackets()){
				return null;
			}
			if(cg.bracketsTrusted()) {
				List<AtomCoordinates> coords = new ArrayList<>(4);
				for(SGroupBracket b: cg.getBrackets()){
					coords.add(b.getPoint1());
					
					coords.add(b.getPoint2());
					
				}
	//			System.out.println("bracket cords = " + coords);
				rt =  BoundingBox.computePaddedBoundingBoxForCoordinates(coords, 0);
//				System.out.println("bounding box = " + rt);
				Rectangle2D.Float r = new Rectangle2D.Float((float) rt.getX(),
						(float) rt.getY(), (float) rt.getWidth(),
						(float) rt.getHeight());
				return r;
			}
		}
		//fall through
		
		//brackets not supported or trusted
		//compute using bounding box
		rt = BoundingBox.computePaddedBoundingBoxFor(cg.getAtoms()::iterator, .5f);

		
		Rectangle2D.Float r = new Rectangle2D.Float((float) rt.getX(),
				(float) rt.getY(), (float) rt.getWidth(),
				(float) rt.getHeight());
//		System.out.println(r);
		return r;
	}
	private static Rectangle2D getBoundsForGroup(List<DisplayLabel> dlist, Iterable<Atom> atoms, Rectangle2D start) {
		Set<Atom> cas = new HashSet<Atom>();
		for (Atom ca : atoms) {
			cas.add(ca);
		}
		for (DisplayLabel dl : dlist) {
			if (cas.contains(dl.atomGroup)) {
				start.add(dl.bbox);
			}
		}
		return start;
	}

	public static class DisplayLabel {
		public Rectangle2D bbox;
		Font dfont;
		String lab;
		float x;
		float y;
		ColorParent c;
		Atom atomGroup = null;

		public DisplayLabel(String lab, Font f, float x, float y, ColorParent c) {
			this.dfont = f;
			this.lab = lab;
			this.x = x;
			this.y = y;
			this.c = c;

		}
	}

	private static Rectangle2D drawString(Graphics2DTemp g2, String s, float x, float y) {
		boolean glyph = true;
		if (glyph) {
			GlyphVector gv = g2.getFont().createGlyphVector(g2.getFontRenderContext(), s.toCharArray());
			Rectangle2D r2 = gv.getLogicalBounds();
			g2.drawGlyphVector(gv, x, y);
			return new Rectangle2D.Double(r2.getMinX() + x, r2.getMinY() + y, r2.getWidth(), r2.getHeight());
			// r2=g2.getTransform().createTransformedShape(r2).getBounds2D();
			// r2
			// return r2;

		} else {
			g2.drawString(s, x, y);
			return null;
		}
	}

	private static String getSuperScriptChar(int i) {

		switch (i) {
		case 0:
			return "\u2070";
		case 1:
			return "\u00B9";
		case 2:
			return "\u00B2";
		case 3:
			return "\u00B3";
		case 4:
			return "\u2074";
		case 5:
			return "\u2075";
		case 6:
			return "\u2076";
		case 7:
			return "\u2077";
		case 8:
			return "\u2078";
		case 9:
			return "\u2079";
		}
		return "";
	}

	private static class AtomDrawProps {
		float radius;
		ColorParent hcolor;
		ColorParent dcolor;
		boolean highlight;

	}

	private static class BondProps {
		public double maxWedgeWidth;
		float DEF_DBL_BOND_GAP;
		float DEF_DBL_BOND_DISTANCE;
		float BONDAVG;
		float DEF_NUM_DASH;
		float bondWidth;
		float DEF_SPLIT_RATIO;

		boolean DrawDashWedge;
		boolean PROP_DASH_SPACING;
		boolean centerAllDoubleBonds;
		boolean highlightHalo;
		boolean halfColoredBonds;

		Stroke solidREC;

		private void drawBonds(Graphics2DTemp g2, Chemical c, List<double[]> toAdd, Stroke solid, Stroke dashed,
				AffineTransformParent centerTransform, Map<Atom, AtomDrawProps> cprops) {
			ColorParent drawColor = g2.getColor();
			float resize = (float) Math.abs(centerTransform.getScaleX());

			List<LineParent> paintedLines = new ArrayList<LineParent>();

			for (int k = 0; k < toAdd.size(); k++) {
				double[] xy = toAdd.get(k);// toAdd.size()-k-1)

				Bond cb = c.getBond((int) xy[7]);
				float wid = (float) Math.atan2(maxWedgeWidth, cb.getBondLength());

				float[] rads = new float[2];
				AtomDrawProps caprop1 = cprops.get(cb.getAtom1());
				AtomDrawProps caprop2 = cprops.get(cb.getAtom2());
				rads[0] = caprop1.radius;
				rads[1] = caprop2.radius;

				float[] p1 = new float[2];
				float[] p2 = new float[2];
				centerTransform.transform(xy, 0, p1, 0, 1);
				centerTransform.transform(xy, 2, p2, 0, 1);
				float dx = (p1[0] - p2[0]);
				float dy = (p1[1] - p2[1]);
				float[] avpt1 = new float[] { p1[0], p1[1], rads[0] };
				float[] avpt2 = new float[] { p2[0], p2[1], rads[1] };

				float dxdbl = dx / 4.f;
				float dydbl = dy / 4.f;

				float[] doubleBPos = new float[2];
				centerTransform.transform(xy, 5, doubleBPos, 0, 1);
				// is this the same as double either?
				if (cb.getBondType() == BondType.SINGLE_OR_DOUBLE) {
					xy[4] = -1;
				}

				// norm: magnitude of vector from center of single bond to
				// center of double bond
				float norm = DEF_DBL_BOND_GAP * resize * BONDAVG / (float) Math.sqrt((dxdbl * dxdbl + dydbl * dydbl));
				if ((int) xy[4] == -1 || (centerAllDoubleBonds && (int) xy[4] == 2)) {
					norm *= .5;
					xy[4] = -1;
				}
				float dbcx[] = new float[2]; // double bond center x
				float dbcy[] = new float[2]; // double bond center y

				dbcx[0] = ((p1[0] - dydbl * norm) + (p2[0] - dydbl * norm)) / 2;
				dbcy[0] = ((p1[1] + dxdbl * norm) + (p2[1] + dxdbl * norm)) / 2;
				dbcx[1] = ((p1[0] + dydbl * norm) + (p2[0] + dydbl * norm)) / 2;
				dbcy[1] = ((p1[1] - dxdbl * norm) + (p2[1] - dxdbl * norm)) / 2;
				if (sqrDistance(dbcx[0], dbcy[0], doubleBPos[0], doubleBPos[1]) > sqrDistance(dbcx[1], dbcy[1],
						doubleBPos[0], doubleBPos[1])) {
					float tx;
					tx = dbcx[0];
					dbcx[0] = dbcx[1];
					dbcx[1] = tx;
					tx = dbcy[0];
					dbcy[0] = dbcy[1];
					dbcy[1] = tx;
				}
				float rat = DEF_DBL_BOND_DISTANCE * 2;
				/*
				 * boolean highlightbond = false; if (highlightHalo) { if (caprop1.highlight) {
				 * if (caprop2.highlight) { highlightbond = true; } } }
				 */

				if ((int) xy[4] != Bond.BondType.AROMATIC.ordinal()) {
					g2.setStroke(solid);
				} else {
					g2.setStroke(dashed);
					xy[4] = Bond.BondType.DOUBLE.getOrder();
				}

				ColorParent fromCol = drawColor;
				ColorParent toCol = drawColor;
				int typ = (int) xy[4];
				if (halfColoredBonds) {
					if (highlightHalo) {
						fromCol = caprop2.hcolor;
						toCol = caprop1.hcolor;
						if (!caprop2.highlight || !caprop1.highlight) {
							fromCol = transparent;
							toCol = transparent;
						}
						typ = 1;
						// xy[4]=1;
					} else {
						fromCol = caprop2.dcolor;
						toCol = caprop1.dcolor;

					}
				}

				switch (typ) {
				case 5:
					if (DrawDashWedge) {
						drawDash(g2, ggen.makeLine(p1[0], p1[1], p2[0], p2[1]), avpt1, avpt2, wid, (int) (DEF_NUM_DASH),
								PROP_DASH_SPACING, fromCol, toCol);
					} else {
						drawDashLine(g2, ggen.makeLine(p1[0], p1[1], p2[0], p2[1]), avpt1, avpt2, (int) (DEF_NUM_DASH),
								PROP_DASH_SPACING, fromCol, toCol);
					}
					break;
				case 6:

					drawWedge(g2, ggen.makeLine(p1[0], p1[1], p2[0], p2[1]), avpt1, avpt2, wid, fromCol, toCol);

					break;

				case -1:
					rat = 2f;

				case 3:
					LineParent line;
					// is this the same as double either?
					if (cb.getBondType() == BondType.SINGLE_OR_DOUBLE) {

						line = ggen.makeLine((dbcx[1] - rat * dxdbl), // x3
								(dbcy[1] - rat * dydbl), // y3
								(dbcx[0] + rat * dxdbl), // x2
								(dbcy[0] + rat * dydbl)); // y2
					} else {
						line = ggen.makeLine((dbcx[1] - rat * dxdbl), // x3
								(dbcy[1] - rat * dydbl), // y3
								(dbcx[1] + rat * dxdbl), // x4
								(dbcy[1] + rat * dydbl)); // y4
					}

					drawLine(g2, line, avpt1, avpt2, fromCol, toCol);
				case 2:
					LineParent lineb;
					// is this the same as double either?
					if (cb.getBondType() == BondType.SINGLE_OR_DOUBLE) {

						lineb = ggen.makeLine((dbcx[0] - rat * dxdbl), // x1
								(dbcy[0] - rat * dydbl), // y1
								(dbcx[1] + rat * dxdbl), // x4
								(dbcy[1] + rat * dydbl)); // y4
					} else {
						lineb = ggen.makeLine((dbcx[0] - rat * dxdbl), // x1
								(dbcy[0] - rat * dydbl), // y1
								(dbcx[0] + rat * dxdbl), // x2
								(dbcy[0] + rat * dydbl)); // y2
					}

					drawLine(g2, lineb, avpt1, avpt2, fromCol, toCol);

				case 1:
					g2.setStroke(solid);
					if ((int) xy[4] != -1 || highlightHalo) {
						LineParent linec = ggen.makeLine(p1[0], p1[1], p2[0], p2[1]);
						LineParent[] newLines = getSplitLines(linec, paintedLines, bondWidth * DEF_SPLIT_RATIO);
						if (newLines.length > 1) {
							BasicStroke pstr = (BasicStroke) g2.getStroke();
							g2.setStroke(solidREC);
							for (LineParent lined : newLines) {
								drawLine(g2, lined, avpt1, avpt2, toCol, fromCol);
								// paintedLines.add(drawLine(g2,lined,avpt1,avpt2,toCol,fromCol));
							}
							g2.setStroke(pstr);
						} else {
							LineParent dLine = drawLine(g2, linec, avpt1, avpt2, toCol, fromCol);
							if (dLine != null)
								paintedLines.add(dLine);
						}
					}

					break;

				default:
				}
			}
		}
	}

	private static String getSuperScriptString(int i) {
		String s = i + "";
		String ret = "";
		for (char c : s.toCharArray()) {
			ret += getSuperScriptChar(Integer.parseInt(c + ""));
		}
		return ret;
	}

	private static int getAttachCardPos(Atom ca, String attatch, int DMASK) {
		int CARD = 0; // ENWS
		AtomCoordinates coords = ca.getAtomCoordinates();
		if (!attatch.equals("")) {
			double ax = 0;
			double ay = 0;
			int abcount = ca.getBondCount();
			if (abcount > 0) {
				for (Atom cnei : ca.getNeighbors()) {
					AtomCoordinates coords2 = cnei.getAtomCoordinates();
					ax += coords2.getX();
					ay += coords2.getY();
				}
				ax /= (abcount);
				ay /= (abcount);
			} else {
				
				ax = coords.getX();
				ay = coords.getY();
			}
			double[] dv = normVec(new double[] { ax - coords.getX(), ay - coords.getY() }, 1);

			dv[0] = dv[0] - 1f / 24f;
			List<float[]> cardDir = new ArrayList<float[]>();
			cardDir.add(new float[] { -1, 0 });
			cardDir.add(new float[] { 0, 1 });
			cardDir.add(new float[] { 1, 0 });
			cardDir.add(new float[] { 0, -1 });
			double minDist = 50000;
			for (int i = 0; i < cardDir.size(); i++) {
				if (((1 << i) & DMASK) != 0) {
					float[] testDir = cardDir.get(i);
					double dx = (dv[0] - testDir[0]);
					double dy = (dv[1] - testDir[1]);
					double dist = dx * dx + dy * dy;

					if (dist <= minDist) {
						minDist = dist;
						CARD = i;
					}
				}

			}
		}
		return CARD;
	}

	private static float[] getNormVecAway(Atom ca) {

		int abcount = ca.getBondCount();

		List<double[]> angs = new ArrayList<double[]>();

		AtomCoordinates coords = ca.getAtomCoordinates();
		if (abcount > 0) {

			int i = 0;
			for (Atom cnei : ca.getNeighbors()) {
				AtomCoordinates cneiCoords = cnei.getAtomCoordinates();
				angs.add(new double[] { i, coords.angleTo(cneiCoords) });
				i++;
			}
			if (abcount == 1) {
				double ang = angs.get(0)[1];
				return new float[] { (float) Math.cos(ang), (float) Math.sin(ang) };
			}
			Collections.sort(angs, new Comparator<double[]>() {
				@Override
				public int compare(double[] arg0, double[] arg1) {
					return Double.compare(arg0[1], arg1[1]);
				}
			});
			double maxDtheta = 0;
			double bestAng = 0;
			for (i = 0; i < angs.size(); i++) {
				double[] first = angs.get(i);
				double[] second = angs.get((i + 1) % angs.size());
				double ang1 = first[1];
				double ang2 = second[1];
//				System.out.println(ang2 / Math.PI * 180);
				if (ang1 > ang2) {
					ang2 = ang2 + Math.PI * 2;
				}
				double da = ang2 - ang1;

				if (da > maxDtheta) {

					maxDtheta = da;
					bestAng = (ang2 + ang1) / 2;
				}
			}
			return new float[] { (float) Math.cos(bestAng + Math.PI), (float) Math.sin(bestAng + Math.PI) };
		}
		return new float[] { 1, 0 };
	}

	private static Collection<Entry<String, float[]>> getAttachPos(String attatch, float w, float h, float[] p,
			FontMetrics fm, Graphics2DTemp g2, int CARD, float[] nv) {
		if (!attatch.equals("")) {
			List<Entry<String, float[]>> entryList = new ArrayList<Entry<String, float[]>>();

			float dv[] = new float[2];
			switch (CARD) {
			// RIGHT
			case 0:
				dv[0] = -2 * w;
				dv[1] = 0;
				break;
			// TOP
			case 1:
				dv[0] = 0;
				dv[1] = 2 * h + w / 10;
				break;
			// LEFT
			case 2:
				dv[0] = (float) fm.getStringBounds(attatch, g2._delagate).getWidth();
				dv[1] = 0;
				break;
			// BOTTOM
			case 3:
				dv[0] = 0;
				dv[1] = -2 * h - w / 10;
				break;
			default:
			// non-cardinal
			{
				double maxx = fm.getStringBounds(attatch, g2._delagate).getWidth();
				double minx = -2 * w;
				double miny = -2 * h - w / 10;
				double maxy = 2 * h + w / 10;
				double cx = (maxx + minx) / 2;
				double cy = (maxy + miny) / 2;
				double radx = (maxx - minx) / 2;
				double rady = (maxy - miny) / 2;
				dv[0] = (float) (cx + nv[0] * radx);
				dv[1] = (float) (cy + nv[1] * rady);
			}

			}

			// float[] aP = new float[] { (p[0] - dv[0]) - w,(p[1] + dv[1]) + h
			// };
			char[] res = attatch.toCharArray();
			float disp = 0;
			for (int i = 0; i < res.length; i++) {
				float ydisp = 0;

				if (subScriptSet.contains(res[i] + "")) {
					ydisp = (float) fm.getStringBounds(res[i] + "", g2._delagate).getHeight() / 5;
				}
				float[] aP1 = new float[] { (p[0] - dv[0]) - w + disp, (p[1] + dv[1]) + h + ydisp };
				disp += (float) fm.getStringBounds(res[i] + "", g2._delagate).getWidth();
				Entry<String, float[]> ent = new MyEntry<String, float[]>(res[i] + "", aP1);
				entryList.add(ent);
				// smap.
				// smap.put(res[i]+"", aP1);
			}
			return entryList;

		}
		return null;
	}

	private static class MyEntry<K, V> implements Entry<K, V> {
		K key;
		V val;

		public MyEntry(K key, V val) {
			this.key = key;
			this.val = val;

		}

		@Override
		public K getKey() {
			// TODO Auto-generated method stub
			return key;
		}

		@Override
		public V getValue() {
			// TODO Auto-generated method stub
			return val;
		}

		@Override
		public V setValue(V value) {
			val = value;
			return val;
		}
	}

	private static LineParent[] getSplitLines(LineParent startLine, Collection<LineParent> olines, double width) {
		double s2dx = startLine.getX2() - startLine.getX1();
		double s2dy = startLine.getY2() - startLine.getY1();
		double s2x = startLine.getX1();
		double s2y = startLine.getY1();
		double pcwidth = width / Math.sqrt(s2dx * s2dx + s2dy * s2dy);

		// (sx + sdx*t,sy+sdy*t) 0<t<1
		// (s2x + s2dx*t,s2y+s2dy*t) 0<t<1
		// s1x+s1dx*t1=s2x+s2dx*t2
		// s1y+s1dy*t1=s2y+s2dy*t2
		// s1x+s1dx*t1-s2x-s2dx*t2 = 0
		// s1y+s1dy*t1-s2y-s2dy*t2 = 0
		// s1dx*t1-s2dx*t2 = s2x - s1x
		// s1dy*t1-s2dy*t2 = s2y - s1y
		// [A B C D][t1 t2]'
		// t1 = (s2dy*t2 + s2y - s1y)/s1dy;
		// t2 = (s1dx*t1 + s2x - s1x)/s2dx;
		// t1 = (s2dx*t2 + s2x - s1x)/s1dx;
		// (s2dy*t2 + s2y - s1y)/s1dy = (s2dx*t2 + s2x - s1x)/s1dx;
		// s1dy*(s2dx*t2 + s2x - s1x) = s1dx*(s2dy*t2 + s2y - s1y)
		// s1dy*s2dx*t2 + s1dy*s2x - s1dy*s1x = s1dx*s2dy*t2 + s1dx*s2y -
		// s1dx*s1y
		// s1dy*s2dx*t2 - s1dx*s2dy*t2 = s1dx*s2y - s1dx*s1y - s1dy*s2x +
		// s1dy*s1x
		// t2 = (s1dx*s2y - s1dx*s1y - s1dy*s2x + s1dy*s1x)/(s1dy*s2dx -
		// s1dx*s2dy)

		for (LineParent oline : olines) {
			if (oline.intersectsLine(startLine)) {
				if (!((startLine.getX1() == oline.getX1() && startLine.getY1() == oline.getY1())
						|| (startLine.getX2() == oline.getX1() && startLine.getY2() == oline.getY1())
						|| (startLine.getX2() == oline.getX2() && startLine.getY2() == oline.getY2())
						|| (startLine.getX1() == oline.getX2() && startLine.getY1() == oline.getY2()))) {
					double s1dx = oline.getX2() - oline.getX1();
					double s1dy = oline.getY2() - oline.getY1();
					double s1x = oline.getX1();
					double s1y = oline.getY1();
					double t2 = (s1dx * s2y - s1dx * s1y - s1dy * s2x + s1dy * s1x) / (s1dy * s2dx - s1dx * s2dy);

					t2 = t2 - pcwidth;
					double px = s2x + s2dx * t2;
					double py = s2y + s2dy * t2;
					LineParent newline1 = ggen.makeLine(s2x, s2y, px, py);
					t2 = t2 + 2 * pcwidth;
					px = s2x + s2dx * t2;
					py = s2y + s2dy * t2;
					LineParent newline2 = ggen.makeLine(px, py, s2x + s2dx, s2y + s2dy);
					return new LineParent[] { newline1, newline2 };
				}
			}
		}
		return new LineParent[] { startLine };
	}

	private static boolean pointInCircle(double px, double py, float cir1[]) {
		double dx = cir1[0] - px;
		double dy = cir1[1] - py;
		if (dx * dx + dy * dy < cir1[2]) {
			return true;
		}
		return false;
	}

	private static LineParent getBoundedLine(LineParent line, float pt1[], float pt2[]) {
		LineParent nline1 = lineCircleIntersections(line, pt1[0], pt1[1], pt1[2]);
		nline1 = lineCircleIntersections(nline1, pt2[0], pt2[1], pt2[2]);
		if (pointInCircle(nline1.getX1(), nline1.getY1(), pt1)) {
			return null;
		}
		if (pointInCircle(nline1.getX2(), nline1.getY2(), pt2)) {
			return null;
		}
		return nline1;
	}

	private static LineParent drawLine(Graphics2DTemp g, LineParent line, float pt1[], float pt2[], ColorParent c1,
			ColorParent c2) {

		line = getBoundedLine(line, pt1, pt2);
		ColorParent c = g.getColor();
		if (line != null) {
			if (c1.equals(c2)) {
				g.setColor(c1);
				g.drawP(line);
			} else {
				BasicStroke s = (BasicStroke) g.getStroke();
				BasicStroke solidREC = new BasicStroke(s.getLineWidth(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
				LineParent[] splits = splitLine(line);
				if (c1.hashCode() < c2.hashCode()) {
					LineParent tmp = splits[0];
					splits[0] = splits[1];
					splits[1] = tmp;
					ColorParent tcol = c2;
					c2 = c1;
					c1 = tcol;
				}
				g.setColor(c1);
				g.drawP(splits[0]);
				g.setColor(c2);
				g.drawP(splits[1]);
				g.setStroke(solidREC);
				// line1 = new
				// LineParent.Double(line.getX1(),line.getY1(),line.getX1()+dx/2f,line.getY1()+dy/2f);
				g.setColor(c1);
				g.drawP(splits[0]);
				g.setStroke(s);

			}
		}
		g.setColor(c);
		return line;
	}

	private static LineParent[] splitLine(LineParent line) {
		double dx = line.getX2() - line.getX1();
		double dy = line.getY2() - line.getY1();
		LineParent line1 = ggen.makeLine(line.getX1(), line.getY1(), line.getX1() + dx / 2, line.getY1() + dy / 2);
		LineParent line2 = ggen.makeLine(line.getX2(), line.getY2(), line.getX1() + dx / 2, line.getY1() + dy / 2);
		return new LineParent[] { line1, line2 };
	}

	private static LineParent drawLine(Graphics2DTemp g, LineParent line, float pt1[], float pt2[]) {
		line = getBoundedLine(line, pt1, pt2);
		if (line != null)
			g.drawP(line);
		return line;
	}

	private static Point2DParent[] wedgeAngle(LineParent line, float ang) {
		double dx = line.getX2() - line.getX1();
		double dy = line.getY2() - line.getY1();
		double newmag = Math.sqrt(dx * dx + dy * dy);
		double mag = newmag / Math.cos(ang);
		double theta = Math.atan2(dy, dx);
		return new Point2DParent[] {
				ggen.makePoint(line.getX1() + mag * Math.cos(theta + ang), line.getY1() + mag * Math.sin(theta + ang)),
				ggen.makePoint(line.getX1() + mag * Math.cos(theta - ang),
						line.getY1() + mag * Math.sin(theta - ang)) };
	}

	private static void drawWedge(Graphics2DTemp g, LineParent line, float pt1[], float pt2[], float ang) {
		drawWedge(g, line, pt1, pt2, ang);

	}

	private static void drawWedge(Graphics2DTemp g, LineParent line, float pt1[], float pt2[], float ang,
			ColorParent c1, ColorParent c2) {
		ColorParent c = g.getColor();
		g.setColor(c1);
		line = getBoundedLine(line, pt1, pt2);
		if (line == null)
			return;
		boolean split = false;
		if (c1 != null && c2 != null) {
			if (!c1.equals(c2)) {

				split = true;
				//
			}
		}
		GeneralPathParent gp = ggen.makeGeneralPath();
		Point2DParent[] ptd = wedgeAngle(line, ang);

		gp.moveTo(line.getX1(), line.getY1());
		gp.lineTo(ptd[0].getX(), ptd[0].getY());
		gp.lineTo(ptd[1].getX(), ptd[1].getY());
		gp.closePath();
		g.fillP(gp);
		if (split) {
			g.setColor(c2);
			LineParent[] splits = splitLine(line);
			ptd = wedgeAngle(splits[0], ang);
			gp = ggen.makeGeneralPath();
			gp.moveTo(line.getX1(), line.getY1());
			gp.lineTo(ptd[0].getX(), ptd[0].getY());
			gp.lineTo(ptd[1].getX(), ptd[1].getY());
			gp.closePath();
			g.fillP(gp);
		}
		g.setColor(c);

	}

	private static void drawDash(Graphics2DTemp g, LineParent line, float pt1[], float pt2[], float ang, int NUMLINE,
			boolean prop, ColorParent c1, ColorParent c2) {
		line = getBoundedLine(line, pt1, pt2);
		ColorParent c = g.getColor();
		boolean split = false;
		// if(c1!=null && c2!=null){
		// System.out.println(c1 + " ?=" + c2);
		// System.out.println(c1);
		g.setColor(c2);
		if (!c1.equals(c2)) {
			split = true;
		}
		// }
		if (line == null)
			return;
		if (prop) {
			double dx = pt2[0] - pt1[0];
			double dy = pt2[1] - pt1[1];
			double dx2 = line.getX2() - line.getX1();
			double dy2 = line.getY2() - line.getY1();
			double l1 = Math.sqrt(dx * dx + dy * dy);
			double l2 = Math.sqrt(dx2 * dx2 + dy2 * dy2);
			NUMLINE = (int) ((NUMLINE * l2) / l1);
		}

		Point2DParent[] ptd = wedgeAngle(line, ang);

		for (int i = 0; i <= NUMLINE; i++) {
			if (split) {
				if (i > NUMLINE / 2) {
					g.setColor(c1);
				}
			}
			LineParent lineb = ggen.makeLine(((line.getX1() * (NUMLINE - i) + ptd[0].getX() * i) / NUMLINE),
					((line.getY1() * (NUMLINE - i) + ptd[0].getY() * i) / NUMLINE),
					((line.getX1() * (NUMLINE - i) + ptd[1].getX() * i) / NUMLINE),
					((line.getY1() * (NUMLINE - i) + ptd[1].getY() * i) / NUMLINE));
			g.drawP(lineb);
			// awLine(g, lineb, pt1, pt2);
		}
		g.setColor(c);
	}

	private static void drawDash(Graphics2DTemp g, LineParent line, float pt1[], float pt2[], float ang, int NUMLINE,
			boolean prop) {
		drawDash(g, line, pt1, pt2, ang, NUMLINE, prop, null, null);
	}

	private static void drawDashLine(Graphics2DTemp g, LineParent line, float pt1[], float pt2[], int NUMLINE,
			boolean prop, ColorParent c1, ColorParent c2) {
		line = getBoundedLine(line, pt1, pt2);
		// System.out.println("LINE DASH");
		ColorParent c = g.getColor();
		g.setColor(c2);

		if (line == null)
			return;
		boolean split = false;

		if (!c1.equals(c2)) {

			split = true;
		}

		if (prop) {
			double dx = pt2[0] - pt1[0];
			double dy = pt2[1] - pt1[1];
			double dx2 = line.getX2() - line.getX1();
			double dy2 = line.getY2() - line.getY1();
			double l1 = Math.sqrt(dx * dx + dy * dy);
			double l2 = Math.sqrt(dx2 * dx2 + dy2 * dy2);
			NUMLINE = (int) ((NUMLINE * l2) / l1);
		}
		// NUMLINE=2;

		for (int i = 0; i < NUMLINE * 2; i += 2) {
			if (split) {
				if (i == NUMLINE) {
					if (c1 != null)
						g.setColor(c1);
				}
			}
			LineParent lineb = ggen.makeLine(
					(line.getX1() * (NUMLINE * 2 - (i + .75)) + line.getX2() * (i + .75)) / (NUMLINE * 2),
					(line.getY1() * (NUMLINE * 2 - (i + .75)) + line.getY2() * (i + .75)) / (NUMLINE * 2),
					(line.getX1() * (NUMLINE * 2 - (i + 1.25)) + line.getX2() * (i + 1.25)) / (NUMLINE * 2),
					(line.getY1() * (NUMLINE * 2 - (i + 1.25)) + line.getY2() * (i + 1.25)) / (NUMLINE * 2));
			g.drawP(lineb);
			// drawLine(g, lineb, pt1, pt2);
		}
		g.setColor(c);
	}

	private ColorParent getColorForSymbol(String sym) {
		ColorParent col = atomColors.get(sym);
		if (col == null)
			col = this.drawColor;
		return col;
	}

	private static LineParent lineCircleIntersections(LineParent line, float Cx, float Cy, float R) {
		// if(true)return line;
		if (line == null)
			return null;
		double[] line2 = lineCircleIntersections(line.getX1(), line.getY1(), line.getX2(), line.getY2(), Cx, Cy, R);
		if (line2 == null)
			return null;
		return ggen.makeLine(line2[0], line2[1], line2[2], line2[3]);
	}

	private static double[] lineCircleIntersections(double Ax, double Ay, double Bx, double By, double Cx, double Cy,
			double R) {
		double[] is1 = new double[2];
		double[] is2 = new double[2];
		boolean achange = false;
		boolean bchange = false;
		double nax = Ax;
		double nay = Ay;
		double nbx = Bx;
		double nby = By;

		double LAB = (double) Math.sqrt((Bx - Ax) * (Bx - Ax) + (By - Ay) * (By - Ay));

		// compute the direction vector D from A to B
		double Dx = (Bx - Ax) / LAB;
		double Dy = (By - Ay) / LAB;

		// Now the line equation is x = Dx*t + Ax, y = Dy*t + Ay with 0 <= t <=
		// 1.

		// compute the value t of the closest point to the circle center (Cx,
		// Cy)
		double t = Dx * (Cx - Ax) + Dy * (Cy - Ay);

		// This is the projection of C on the line from A to B.

		// compute the coordinates of the point E on line and closest to C
		double Ex = t * Dx + Ax;
		double Ey = t * Dy + Ay;

		// compute the euclidean distance from E to C
		double LEC = (double) Math.sqrt((Ex - Cx) * (Ex - Cx) + (Ey - Cy) * (Ey - Cy));

		// test if the line intersects the circle
		if (LEC < R) {
			// compute distance from t to circle intersection point
			double dt = (double) Math.sqrt(R * R - LEC * LEC);
			is1[0] = Ex + dt * Dx;
			is1[1] = Ey + dt * Dy;

			if (between(is1[0], Ax, Bx) && between(is1[1], Ay, By)) {
				double dax = Cx - Ax;
				double day = Cy - Ay;
				double dbx = Cx - Bx;
				double dby = Cy - By;
				double da = dax * dax + day * day;
				double db = dbx * dbx + dby * dby;
				if (da < db) {
					achange = true;
					nax = is1[0];
					nay = is1[1];
				} else {
					bchange = true;
					nbx = is1[0];
					nby = is1[1];
				}
			}
			is2[0] = Ex - dt * Dx;
			is2[1] = Ey - dt * Dy;
			if (between(is2[0], Ax, Bx) && between(is2[1], Ay, By)) {
				if (achange) {
					bchange = true;
					nbx = is2[0];
					nby = is2[1];
				} else if (bchange) {
					achange = true;
					nax = is2[0];
					nay = is2[1];
				} else {
					double dax = Cx - Ax;
					double day = Cy - Ay;
					double dbx = Cx - Bx;
					double dby = Cy - By;
					double da = dax * dax + day * day;
					double db = dbx * dbx + dby * dby;

					if (da < db) {
						achange = true;
						nax = is2[0];
						nay = is2[1];
					} else {
						bchange = true;
						nbx = is2[0];
						nby = is2[1];
					}
				}
			}
			// if(!achange && !bchange)
			// return null;
		}

		// else test if the line is tangent to circle
		else if (LEC == R) {
			// tangent point to circle is E

		} else {
			// line doesn't touch circle
		}
		// if(achange && bchange)
		// return null;

		return new double[] { nax, nay, nbx, nby };

	}

	private static boolean between(double x, double a, double b) {
		return (x >= a && x <= b) || (x <= a && x >= b);
	}

	private static double[] normVec(double[] v, double L) {
		double len = v[0] * v[0] + v[1] * v[1];
		if (len == 0) {
			return new double[] { L, 0 };
		}
		double mul = (L / Math.sqrt(len));
		return new double[] { v[0] * mul, v[1] * mul };
	}

	private static double sqrDistance(double c1, double c12, double c2, double c22) {
		return (c1 - c2) * (c1 - c2) + (c12 - c22) * (c12 - c22);
	}

	private String getRGroupText(int i) {
		switch (i) {
		case 90:
			return "[B]";
		case 91:
			return "5'";
		case 92:
			return "3'";
		case 93:
			return "{C-TERM}";
		case 94:
			return "{N-TERM}";
		default:
			return "R" + i;
		}
	}


}
