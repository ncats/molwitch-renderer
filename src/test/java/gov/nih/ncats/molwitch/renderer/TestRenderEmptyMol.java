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

import gov.nih.ncats.molwitch.Chemical;
import gov.nih.ncats.molwitch.MolWitch;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class TestRenderEmptyMol {

    @Test
    public void zeroAtomsInMol() throws Exception{
        String mol= "PITRAKINRA\n" +
                " G-SRS   Marvin  08062021082D          \n" +
                "\n" +
                "  0  0  0  0  0  0            999 V2000\n" +
                "M  END";

        ChemicalRenderer renderer = new ChemicalRenderer();
        renderer.setShadowVisible(false);

        Chemical c = Chemical.parseMol(mol);
//        c.generateCoordinates();
//        System.out.println(c.getFormula());
        BufferedImage actual = renderer.createImage(c, 200);

    }
}
