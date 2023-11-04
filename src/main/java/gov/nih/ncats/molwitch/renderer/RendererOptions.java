/*
 * NCATS-MOLWITCH-RENDERER
 *
 * Copyright 2023 NIH/NCATS
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import gov.nih.ncats.molwitch.Chemical;

public class RendererOptions {

	public enum DrawOptions{
		DRAW_BONDS(true, "PROP_KEY_DRAW_BONDS"),
		DRAW_SYMBOLS(true, "PROP_KEY_DRAW_SYMBOLS"),
		DRAW_CARBON(false, "PROP_KEY_DRAW_CARBON"),
		DRAW_IMPLICIT_HYDROGEN(true, "PROP_KEY_DRAW_IMPLICIT_HYDROGEN"),
		DRAW_TERMINAL_CARBON(true, "PROP_KEY_DRAW_TERMINAL_CARBON"),
		DRAW_STEREO_BONDS(true, "PROP_KEY_DRAW_STEREO_BONDS"),
		DRAW_PROPORTIONAL_ATOM_RADIUS(false, "PROP_KEY_DRAW_PROPORTIONAL_ATOM_RADIUS"),
		DRAW_PROPORTION_AVERAGE_BOND_LENGTH(true, "PROP_KEY_DRAW_PROPORTION_AVERAGE_BOND_LENGTH"),
		DRAW_CENTER_ALL_DOUBLE_BONDS(false, "PROP_KEY_DRAW_CENTER_ALL_DOUBLE_BONDS"),
		DRAW_ATOM_COLOR_ON_BONDS(true, "PROP_KEY_DRAW_ATOM_COLOR_ON_BONDS"),
		DRAW_CONSTANT_DASH_WIDTH(true, "PROP_KEY_DRAW_CONSTANT_DASH_WIDTH"),
		DRAW_STEREO_DASH_AS_WEDGE(true, "PROP_KEY_DRAW_STEREO_DASH_AS_WEDGE"),
		/**
		 *  Show wedge bonds as originating from a small circle (of bond length)
		 *  instead of a point (which is the default).
		 *  Allowing this makes wedges more visible in some cases.
		 */
		DRAW_WEDGE_AS_POINT(true, "PROP_KEY_DRAW_WEDGE_AS_POINT"),
		/**
		 * Should renderer join wedges/single bonds into an extended stylistic form,
		 * which is often less jarring than the sudden sharp corners from bridgehead/chain wedges in other styles.
		 *
		 * <p>when set to {@code false} (default):</p>
		 * <img src="./doc-files/wedge_join_off.png" />
		 *
		 * <p>when set to {@code true} :</p>
		 * <img src="./doc-files/wedge_join_on.png"/>
		 *
		 */
		DRAW_STEREO_WEDGE_JOIN(false, "PROP_KEY_DRAW_STEREO_WEDGE_JOIN"),
		/**
		 * Show the final dash line on dash-wedges when not connecting to a symbol.
		 */
		DRAW_STEREO_LAST_DASH_ON_NON_SYMBOLS(true, "PROP_KEY_DRAW_STEREO_LAST_DASH_ON_NON_SYMBOLS"),
		
		DRAW_CENTER_NONRING_DOUBLE_BONDS(false, "PROP_KEY_DRAW_CENTER_NONRING_DOUBLE_BONDS"),
		
		
		
		DRAW_GREYSCALE(false, "PROP_KEY_DRAW_GREYSCALE"),
		DRAW_STEREO_LABELS(false, "PROP_KEY_DRAW_STEREO_LABELS"),
		DRAW_STEREO_GIVEN_BY_MAP(false, "PROP_KEY_DRAW_STEREO_GIVEN_BY_MAP"),
		DRAW_HIGHLIGHT_MAPPED(false, "PROP_KEY_DRAW_HIGHLIGHT_MAPPED"),
		DRAW_HIGHLIGHT_WITH_HALO(false, "PROP_KEY_DRAW_HIGHLIGHT_WITH_HALO"),
		DRAW_HIGHLIGHT_MONOCHROMATIC(true, "PROP_KEY_DRAW_HIGHLIGHT_MONOCHROMATIC"),
		DRAW_HIGHLIGHT_SHOW_ATOM(false, "PROP_KEY_DRAW_HIGHLIGHT_SHOW_ATOM"),
		DRAW_SHOW_MAPPED(false, "PROP_KEY_DRAW_SHOW_MAPPED"),
		DRAW_STEREO_LABELS_AS_ATOMS(false, "PROP_KEY_DRAW_STEREO_LABELS_AS_ATOMS"),
		DRAW_STEREO_LABELS_AS_RELATIVE(false, "PROP_KEY_DRAW_STEREO_LABELS_AS_RELATIVE"),
		DRAW_STEREO_LABELS_AS_STARRED(false, "PROP_KEY_DRAW_STEREO_LABELS_AS_STARRED"),
		DRAW_STEREO_LABELS_PARENTHESES(false, "PROP_KEY_DRAW_STEREO_LABELS_PARENTHESES"),
		DRAW_STEREO_FORCE_MONOCHROMATIC(false, "PROP_KEY_DRAW_STEREO_FORCE_MONOCHROMATIC"),

		DRAW_SUPERATOMS_AS_LABELS(true, "PROP_KEY_DRAW_SUPERATOMS_AS_LABELS");
		
		private final boolean defaultValue;
		
		/**
		 * This is the name from the old NChemicalRenderer DisplayParams
		 * that is sometimes used in legacy NCATS API calls
		 * to override rendering options.  This is used
		 * only in {@link #safeValueOf(String)}
		 * to maintain backwards compatibility.
		 */
		private final String legacyName;
		
		private static final Map<String, DrawOptions> nameMap;
		
		static {
			nameMap = new HashMap<>();
			for(DrawOptions opt : values()) {
				nameMap.put(opt.name(), opt);
				nameMap.put(opt.legacyName, opt);
			}
		}
		DrawOptions(boolean dv, String legacyName){
			defaultValue= dv;
			this.legacyName = legacyName;
		}
		/**
		 * This is a version of {@link #valueOf(String)}
		 * but if the given String input is not an enum value
		 * then it will return null instead of throwing an exception.
		 * 
		 * @param name the value to parse the Draw option name of.
		 * @return the {@link DrawOptions} for this name or {@code null}.
		 */
		public static DrawOptions safeValueOf(String name) {
			return nameMap.get(name);
		}
		public boolean defaultValue() {
			return defaultValue;
		}
	}
	
	public enum DrawProperties{
		ATOM_LABEL_FONT_FRACTION(2/3D, "DEF_FONT_PERCENT"),
		ATOM_LABEL_BOND_GAP_FRACTION(1, "DEF_FONT_GAP_PERCENT"),
		
		BOND_EXPECTED_LENGTH(1, "DEF_BOND_AVG"),
		BOND_STROKE_WIDTH_FRACTION(.095D, "DEF_STROKE_PERCENT"),
		BOND_DOUBLE_GAP_FRACTION(.30D, "DEF_DBL_BOND_GAP"),
		BOND_DOUBLE_LENGTH_FRACTION(2/3D, "DEF_DBL_BOND_DISTANCE"),
		BOND_STEREO_WEDGE_ANGLE( Math.PI/12, "DEF_WEDGE_ANG"),
		BOND_OVERLAP_SPACING_FRACTION(1.75D, "DEF_SPLIT_RATIO"),
		BOND_STEREO_DASH_NUMBER(6, "DEF_NUM_DASH"), 
		SUBSCRIPT_Y_DISPLACEMENT_FRACTION(0.2f, "SUBSCRIPT_Y_DISPLACEMENT_FRACTION");
		
		private final double defaultValue;
		/**
		 * This is the name from the old NChemicalRenderer DisplayParams
		 * that is sometimes used in legacy NCATS API calls
		 * to override renderering options.  This is used
		 * only in {@link #safeValueOf(String)}
		 * to maintain backwards compatibility.
		 */
		private final String legacyName;
		

		private static final Map<String, DrawProperties> nameMap;
		
		static {
			nameMap = new HashMap<>();
			for(DrawProperties opt : values()) {
				nameMap.put(opt.name(), opt);
				nameMap.put(opt.legacyName, opt);
			}
		}
		
		DrawProperties(double dv, String legacyName){
			defaultValue = dv;
			this.legacyName = legacyName;
		}
		
		/**
		 * This is a version of {@link #valueOf(String)}
		 * but if the given String input is not an enum value
		 * then it will return null instead of throwing an exception.
		 * 
		 *@param name the value to parse the draw property name of.
         *@return the {@link DrawProperties} for this name or {@code null}.
		 */
		public static DrawProperties safeValueOf(String name) {
			return nameMap.get(name);
		}
		public double defaultValue() {
			return defaultValue;
		}
	}
	

		
	 
	
	private  final EnumMap<DrawOptions, Boolean> drawOptions = new EnumMap<>(DrawOptions.class);
	
	private  final EnumMap<DrawProperties, Double> drawProps = new EnumMap<>(DrawProperties.class);
	
	private ColorPalette colorPalette = new ColorPalette();
	
	private Function<Chemical, String> bottomCaptionFunction=null;
	private Function<Chemical, String> topCaptionFunction=null;

	private List<RendererOptionChangeListener> changeListeners = new ArrayList<>();

	public RendererOptions() {
		_useDefauls();
	}

	public RendererOptions copy(){
		RendererOptions copy = RendererOptions.createFromMap(asNonDefaultMap());
		return copy;
	}

	@JsonCreator
	public static RendererOptions createFromMap(Map<String,?> map){
		RendererOptions opts = new RendererOptions();
		opts.changeSettings(map);
		return opts;
	}
	@JsonValue
	public Map<String, Object> asNonDefaultMap(){
		Map<String, Object> map = new HashMap<>();
		for(Entry<DrawProperties,Double> entry: drawProps.entrySet()){
			if(!Objects.equals(entry.getValue(), entry.getKey().defaultValue)) {
				map.put(entry.getKey().legacyName, entry.getValue());
			}
		}
		for(Entry<DrawOptions,Boolean> entry: drawOptions.entrySet()){
			if(entry.getKey().defaultValue != entry.getValue().booleanValue()) {
				map.put(entry.getKey().legacyName, entry.getValue());
			}
		}
		Map<String, Object> paletteMap = colorPalette.asNonDefaultMap();
		if(!paletteMap.isEmpty()){
			map.put("colorPalette", paletteMap);
		}
		return map;
	}


	public Map<String, Object> asMap(){
		Map<String, Object> map = new HashMap<>();
		for(Entry<DrawProperties,Double> entry: drawProps.entrySet()){
			map.put(entry.getKey().legacyName, entry.getValue());
		}
		for(Entry<DrawOptions,Boolean> entry: drawOptions.entrySet()){
			map.put(entry.getKey().legacyName, entry.getValue());
		}
		return map;
	}
	public void addChangeListener(RendererOptionChangeListener listener){
		changeListeners.add(Objects.requireNonNull(listener));
	}
	

	public ColorPalette getColorPalette(){
		return colorPalette;
	}

	private void fireChangeListeners(){
		changeListeners.forEach(l->l.optionChanged(null));
	}
	

	
	
	
	public boolean getDrawOption(DrawOptions o) {
		return drawOptions.get(o);
	}
	
	public RendererOptions setDrawOption(DrawOptions option, boolean value) {
		drawOptions.put(option, value);
		fireChangeListeners();
		return this;
	}
	
	public double getDrawPropertyValue(DrawProperties p) {
		return drawProps.get(p);
	}
	public RendererOptions captionBottom(Function<Chemical, String> captionFunction) {
		this.bottomCaptionFunction = captionFunction;
		fireChangeListeners();
		return this;
	}
	public RendererOptions captionTop(Function<Chemical, String> captionFunction) {
		this.topCaptionFunction = captionFunction;
		fireChangeListeners();
		return this;
	}
	public RendererOptions setDrawPropertyValue(DrawProperties p, double value) {
		drawProps.put(p, value);
		fireChangeListeners();
		return this;
	}
	public RendererOptions withSubstructureHighlight() {
		
		drawOptions.put(DrawOptions.DRAW_HIGHLIGHT_MAPPED, true);
		drawOptions.put(DrawOptions.DRAW_HIGHLIGHT_WITH_HALO, true);
		drawOptions.put(DrawOptions.DRAW_HIGHLIGHT_MONOCHROMATIC, true);
		fireChangeListeners();
		return this;
	}
	
	Optional<String> captionBottom(Chemical c){
		if(bottomCaptionFunction!=null) {
			return Optional.ofNullable(bottomCaptionFunction.apply(c));
		}
		return Optional.empty();
	}
	
	Optional<String> captionTop(Chemical c){
		if(topCaptionFunction!=null) {
			return Optional.ofNullable(topCaptionFunction.apply(c));
		}
		return Optional.empty();
	}
	public static RendererOptions createDefault() {
		return new RendererOptions();
	}
	
	public static RendererOptions createBallAndStick() {
		RendererOptions opts =new RendererOptions();
		
		opts.setDrawOption(DrawOptions.DRAW_BONDS, true);
		opts.setDrawOption(DrawOptions.DRAW_SYMBOLS, false);
		opts.setDrawOption(DrawOptions.DRAW_CARBON, true);
		opts.setDrawOption(DrawOptions.DRAW_CENTER_ALL_DOUBLE_BONDS, true);
		opts.setDrawOption(DrawOptions.DRAW_STEREO_BONDS, false);
		return opts;
	}
	
	public static RendererOptions createINNLike() {
		RendererOptions opts =new RendererOptions();
		
		opts.setDrawOption(DrawOptions.DRAW_GREYSCALE, true);
		opts.setDrawOption(DrawOptions.DRAW_STEREO_DASH_AS_WEDGE, false);
		
		opts.setDrawPropertyValue(DrawProperties.BOND_STEREO_WEDGE_ANGLE, Math.PI/12);
		opts.setDrawPropertyValue(DrawProperties.BOND_STROKE_WIDTH_FRACTION, 0.050D);
		opts.setDrawPropertyValue(DrawProperties.BOND_DOUBLE_GAP_FRACTION, .15D);
		opts.setDrawPropertyValue(DrawProperties.ATOM_LABEL_FONT_FRACTION,0.5D);
		return opts;
	}

	public static RendererOptions createUSPLike() {
		RendererOptions opts = new RendererOptions();
		opts.setDrawOption(DrawOptions.DRAW_GREYSCALE, true);
		opts.setDrawPropertyValue(DrawProperties.ATOM_LABEL_FONT_FRACTION,0.494D*0.97);
		opts.setDrawPropertyValue(DrawProperties.ATOM_LABEL_BOND_GAP_FRACTION,1.02D);
		opts.setDrawPropertyValue(DrawProperties.BOND_STROKE_WIDTH_FRACTION,0.04D*0.8);
		opts.setDrawPropertyValue(DrawProperties.BOND_DOUBLE_GAP_FRACTION,0.21D*0.95);
		opts.setDrawPropertyValue(DrawProperties.BOND_STEREO_WEDGE_ANGLE, Math.PI/25*0.92);
		opts.setDrawPropertyValue(DrawProperties.BOND_STEREO_DASH_NUMBER, 8);
		opts.setDrawPropertyValue(DrawProperties.SUBSCRIPT_Y_DISPLACEMENT_FRACTION, 0.17);
		
		opts.setDrawOption(DrawOptions.DRAW_WEDGE_AS_POINT, false);
		opts.setDrawOption(DrawOptions.DRAW_STEREO_WEDGE_JOIN, true);
		opts.setDrawOption(DrawOptions.DRAW_STEREO_LAST_DASH_ON_NON_SYMBOLS, false);

		return opts;
	}

	public static RendererOptions createUSPColorsLike() {
		RendererOptions opts = createUSPLike();

		opts.setDrawOption(DrawOptions.DRAW_GREYSCALE, false);
		return opts;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((drawOptions == null) ? 0 : drawOptions.hashCode());
		result = prime * result + ((drawProps == null) ? 0 : drawProps.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof RendererOptions)) {
			return false;
		}
		RendererOptions other = (RendererOptions) obj;
		if (drawOptions == null) {
			if (other.drawOptions != null) {
				return false;
			}
		} else if (!drawOptions.equals(other.drawOptions)) {
			return false;
		}
		if (drawProps == null) {
			if (other.drawProps != null) {
				return false;
			}
		} else if (!drawProps.equals(other.drawProps)) {
			return false;
		}
		return true;
	}

	private void _useDefauls() {
		for(DrawOptions o : DrawOptions.values()) {
			drawOptions.put(o, o.defaultValue());
		}
		
		for(DrawProperties o : DrawProperties.values()) {
			drawProps.put(o, o.defaultValue());
		}
		
		colorPalette = new ColorPalette();
	}
	
	public RendererOptions resetToDefaults() {
		_useDefauls();
		fireChangeListeners();
		return this;
	}
	
	public RendererOptions turnOffStereo() {
		setDrawOption(DrawOptions.DRAW_STEREO_LABELS, false);
		setDrawOption(DrawOptions.DRAW_STEREO_LABELS_PARENTHESES, false);
		fireChangeListeners();
		return this;
	}
	
	public RendererOptions turnOnStereo() {
		
		setDrawOption(DrawOptions.DRAW_STEREO_LABELS, true);
		setDrawOption(DrawOptions.DRAW_STEREO_LABELS_PARENTHESES, true);
		fireChangeListeners();
		return this;
	}

	public RendererOptions changeSettings(Map<String, ?> map) {
		for(Entry<String, ?> entry: map.entrySet()) {
			if("colorPalette".equals(entry.getKey())){
				this.colorPalette = ColorPalette.createFromMap((Map<String,Object>)entry.getValue());
				continue;
			}
			DrawOptions opts = DrawOptions.safeValueOf(entry.getKey());
			if(opts ==null) {
				DrawProperties props = DrawProperties.safeValueOf(entry.getKey());
				if(props !=null) {
					if(Number.class.isAssignableFrom(entry.getValue().getClass())){
						setDrawPropertyValue(props,((Number)entry.getValue()).doubleValue());
					}else if(String.class.isAssignableFrom(entry.getValue().getClass())){
						setDrawPropertyValue(props,Double.parseDouble((String)entry.getValue()));
					}
				}
			}else {

				if(Boolean.class.isAssignableFrom(entry.getValue().getClass())){
					setDrawOption(opts, ((Boolean) entry.getValue()).booleanValue());
				}else if(String.class.isAssignableFrom(entry.getValue().getClass())){
					setDrawOption(opts,Boolean.parseBoolean((String)entry.getValue()));
				}

				
			}
		}
		return this;
	}
}
