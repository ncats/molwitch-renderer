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
