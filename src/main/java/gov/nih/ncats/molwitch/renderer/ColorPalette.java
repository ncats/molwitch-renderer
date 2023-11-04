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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.*;
import java.util.stream.Collectors;

public class ColorPalette {

    private static ARGBColor STEREO_COLOR_UNKNOWN = new ARGBColor(255, 0, 0, 255);
    private static ARGBColor  STEREO_COLOR_KNOWN = new ARGBColor(0, 178, 0, 255);

    private static final List<ARGBColor> DEFAULT_HIGHLIGHT_COLORS= Arrays.asList(
            new ARGBColor(255,179,179,255),
            new ARGBColor(194,255,179,255),
            new ARGBColor(179,209,255,255),
            new ARGBColor(255,179,224,255),
            new ARGBColor(240,255,179,255),
            new ARGBColor(179,255,255,255),
            new ARGBColor(240,179,255,255),
            new ARGBColor(255,224,179,255),
            new ARGBColor(179,255,209,255),
            new ARGBColor(194,179,255,255),
            new ARGBColor(255,179,179,255),
            new ARGBColor(194,255,179,255)
    );
    private static Map<String, ARGBColor> DEFAULT_ATOM_COLORS = new HashMap<>();
    static {
        DEFAULT_ATOM_COLORS.put("Cl", new ARGBColor(54, 180, 73, 255));
        DEFAULT_ATOM_COLORS.put("F", new ARGBColor(54, 180, 73, 255));
        DEFAULT_ATOM_COLORS.put("P", new ARGBColor(230, 219, 69, 255));
        DEFAULT_ATOM_COLORS.put("S", new ARGBColor(143, 160, 48, 255));
        DEFAULT_ATOM_COLORS.put("Br", new ARGBColor(115, 84, 35, 255));
        DEFAULT_ATOM_COLORS.put("C", new ARGBColor(58, 58, 58, 255));
        DEFAULT_ATOM_COLORS.put("N", new ARGBColor(93, 69, 230, 255));
        DEFAULT_ATOM_COLORS.put("O", new ARGBColor(230, 93, 69, 255));
        DEFAULT_ATOM_COLORS.put("H", new ARGBColor(58, 58, 58, 255));
        DEFAULT_ATOM_COLORS.put("Na", new ARGBColor(48, 143, 160, 255));
        DEFAULT_ATOM_COLORS.put("I", new ARGBColor(230, 69, 205, 255));


    }


    private Map<String, ARGBColor> atomColors;

    private ARGBColor stereoColorKnown;
    private ARGBColor stereoColorUnknown;

    private List<ARGBColor> highlightColors;

    public ColorPalette(){
        atomColors= new HashMap<>(DEFAULT_ATOM_COLORS);

        stereoColorKnown = STEREO_COLOR_KNOWN;
        stereoColorUnknown=STEREO_COLOR_UNKNOWN;

        highlightColors = new ArrayList<>(DEFAULT_HIGHLIGHT_COLORS);
    }

    @JsonCreator
    public static ColorPalette createFromMap(Map<String,?> map){
        ColorPalette palette = new ColorPalette();

        if(map.containsKey("atomColors")){
            Map<String,String> colorOverrides= (Map<String,String> )map.get("atomColors");
            for(Map.Entry<String,String> e : colorOverrides.entrySet()){
                palette.atomColors.put(e.getKey(), new ARGBColor(e.getValue()));
            }
        }
        if(map.containsKey("stereoColorKnown")){
            palette.stereoColorKnown = new ARGBColor((String)map.get("stereoColorKnown"));
        }
        if(map.containsKey("stereoColorUnknown")){
            palette.stereoColorUnknown = new ARGBColor((String) map.get("stereoColorUnknown"));
        }
        if(map.containsKey("highlightColors")){
            palette.highlightColors = ((List<String>) map.get("highlightColors")).stream()
                                        .map(v-> new ARGBColor(v))
                                        .collect(Collectors.toList());
        }
        return palette;
    }
    @JsonValue
    public Map<String, Object> asNonDefaultMap(){
        Map<String, Object> map =  new HashMap<>();

        if(!Objects.equals(DEFAULT_ATOM_COLORS, atomColors)){
            Map<String,String> atomMap = new HashMap<>();
            for(Map.Entry<String, ARGBColor> e: atomColors.entrySet()){
                atomMap.put(e.getKey(), e.getValue().asHex());
            }
            map.put("atomColors", atomMap);
        }
        if(!Objects.equals(STEREO_COLOR_KNOWN, stereoColorKnown)){
            map.put("stereoColorKnown", stereoColorKnown.asHex());
        }
        if(!Objects.equals(STEREO_COLOR_UNKNOWN, stereoColorUnknown)){
            map.put("stereoColorUnknown", stereoColorUnknown.asHex());
        }
        if(!Objects.equals(DEFAULT_HIGHLIGHT_COLORS, highlightColors)){
            map.put("highlightColors", highlightColors);
        }
        return map;
    }

    private ColorPalette(ColorPalette copy){
        this.atomColors = new HashMap<>(copy.atomColors);
        this.stereoColorUnknown = copy.stereoColorUnknown;
        this.stereoColorKnown = copy.stereoColorKnown;
        this.highlightColors = new ArrayList<>(copy.highlightColors);
    }

    public ColorPalette copy(){
        return new ColorPalette(this);
    }

    public void setHighlightColors(List<ARGBColor> colors) {
        Optional<?> nullColor= colors.stream().filter(Objects::isNull).findAny();
        if(nullColor.isPresent()) {
            throw new NullPointerException("color list can not contain nulls");
        }
        highlightColors = new ArrayList<>(colors);
    }
    public void addHighlightColor(int offset, ARGBColor color) {
        highlightColors.add(offset, Objects.requireNonNull(color));
    }
    public List<ARGBColor> getHighlightColors() {
        return new ArrayList<>(highlightColors);
    }
    public void addHighlightColor(ARGBColor color) {
        highlightColors.add(Objects.requireNonNull(color));
    }

    public int getNumberOfHighlightColors() {
        return highlightColors.size();
    }
    public ARGBColor getAtomColor(String symbol){
        ARGBColor color = atomColors.get(symbol);
        if(color !=null){
            return color;
        }
        return atomColors.get("C");
    }
    public void setAtomColor(String symbol, ARGBColor color){
        Objects.requireNonNull(symbol);

        ARGBColor colorToSet;
        if(color ==null){
            ARGBColor defaultColor = DEFAULT_ATOM_COLORS.get(symbol);
            if(defaultColor ==null){
                defaultColor = DEFAULT_ATOM_COLORS.get("C");
            }
            colorToSet=defaultColor;
        }else{
            colorToSet = color;
        }
        atomColors.put(symbol,Objects.requireNonNull(colorToSet) );
    }

    public ARGBColor getStereoColorUnknown(){
        return stereoColorUnknown;
    }
    public ARGBColor getStereoColorKnown(){
        return stereoColorKnown;
    }
    public void setStereoColorKnown(ARGBColor color){
        if(color ==null){
            stereoColorKnown = STEREO_COLOR_KNOWN;
        }else{
            stereoColorKnown = color;
        }
    }

    public void setStereoColorUnKnown(ARGBColor color){
        if(color ==null){
            stereoColorUnknown = STEREO_COLOR_UNKNOWN;
        }else{
            stereoColorUnknown = color;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ColorPalette)) return false;
        ColorPalette that = (ColorPalette) o;
        return Objects.equals(atomColors, that.atomColors) &&
                Objects.equals(stereoColorKnown, that.stereoColorKnown) &&
                Objects.equals(stereoColorUnknown, that.stereoColorUnknown) &&
                Objects.equals(highlightColors, that.highlightColors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(atomColors, stereoColorKnown, stereoColorUnknown, highlightColors);
    }
}
