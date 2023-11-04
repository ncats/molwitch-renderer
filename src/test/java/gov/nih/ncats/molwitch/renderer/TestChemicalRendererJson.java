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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.awt.*;
import static org.junit.Assert.*;
public class TestChemicalRendererJson {

    @Test
    public void serialize() throws Exception{
        ChemicalRenderer renderer = new ChemicalRenderer(RendererOptions.createUSPLike());
        renderer.setShadowVisible(false);
        renderer.setBackgroundColor(Color.red);

        renderer.getOptions().getColorPalette().setAtomColor("S", new ARGBColor(45,45,67));
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(renderer);

        System.out.println(json);

        ChemicalRenderer sut = mapper.readValue(json, ChemicalRenderer.class);

        System.out.println("========\n"+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(renderer));
        assertEquals(renderer.getOptions().asNonDefaultMap(), sut.getOptions().asNonDefaultMap());

        assertEquals(renderer.isBorderVisible(), sut.isBorderVisible());
        assertEquals(renderer.isShadowVisible(), sut.isShadowVisible());
        assertEquals(renderer.getBackgroundColorARGB(), sut.getBackgroundColorARGB());
        assertEquals(renderer.getBorderColorARGB(), sut.getBorderColorARGB());
    }
    
    @Test
    public void testCopy() throws Exception{
        ChemicalRenderer renderer = new ChemicalRenderer(RendererOptions.createUSPLike());
        renderer.setShadowVisible(false);
        renderer.setBackgroundColor(Color.red);

        renderer.getOptions().getColorPalette().setAtomColor("S", new ARGBColor(45,45,67));
        
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(renderer.getOptions());
        
        RendererOptions ropt= renderer.getOptions().copy();
        String json2 = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ropt);
        
        assertEquals(json,json2);
        renderer = new ChemicalRenderer(ropt); //just checking if it can also instantiate        
        assertNotNull(renderer);
    }
}
