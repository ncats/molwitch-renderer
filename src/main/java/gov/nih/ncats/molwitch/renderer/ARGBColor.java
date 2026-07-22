package gov.nih.ncats.molwitch.renderer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.awt.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class ARGBColor {

    private static Map<Integer, Color> COLOR_CACHE = new ConcurrentHashMap<>();

    private final int argb;
    public ARGBColor(int r, int g, int b){
        this(r,g,b,255);
    }
    public ARGBColor(int r, int g, int b, int a){
        argb = b +
                (g<<8)+
                (r<<16)+
                (a<<24);
    }
    private ARGBColor(int argb){
        this.argb=argb;
    }
    public ARGBColor(Color color){
        this(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }
    @JsonValue
    public String asHex(){
        return Integer.toHexString(argb);
    }
    @JsonCreator
    public ARGBColor(String argbHex){
        argb = Integer.parseUnsignedInt(argbHex, 16);
    }

    public Color asColor(){
        return COLOR_CACHE.computeIfAbsent(argb, v-> new Color(v,true));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ARGBColor)) return false;
        ARGBColor argbColor = (ARGBColor) o;
        return argb == argbColor.argb;
    }

    @Override
    public int hashCode() {
        return Objects.hash(argb);
    }

    public ARGBColor withAlpha(int alpha) {

        return new ARGBColor(alpha << 24 | (argb & 0xffffff));
    }
}
