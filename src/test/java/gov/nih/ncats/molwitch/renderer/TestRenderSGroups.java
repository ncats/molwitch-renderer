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

import gov.nih.ncats.molwitch.Chemical;
import gov.nih.ncats.molwitch.MolWitch;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;

//doing RBG tests on buffers gives different results
@Disabled
public class TestRenderSGroups {


    @Test
    public void renderWithSUPAtomLabels() throws Exception{
        ChemicalRenderer renderer = new ChemicalRenderer();
        Chemical c = Chemical.parseMol(new File(getClass().getResource("/hasSUPs.mol").getFile()));

        BufferedImage actual = renderer.createImage(c, 600);
        ImageIO.write(actual, "PNG", new File(MolWitch.getModuleName() +"actual_withSUP_withSUPTextReversed.png"));
        ImageTestUtil.assertImageDataMatches(

                new File(getClass().getResource("/expected/SGroups/cdk_withSUP_withSUPTextReversed.png").getFile()),
                actual);
//        ImageIO.write(, "PNG", new File(MolWitch.getModuleName() +"_withSUP_withSUPTextReversed.png"));


    }

    @Test
    public void renderWithoutSUPAtomLabels() throws Exception{
        RendererOptions opts = new RendererOptions()
                                        .setDrawOption(RendererOptions.DrawOptions.DRAW_SUPERATOMS_AS_LABELS, false);
        ChemicalRenderer renderer = new ChemicalRenderer(opts);
        Chemical c = Chemical.parseMol(new File(getClass().getResource("/hasSUPs.mol").getFile()));
        ImageIO.write(renderer.createImage(c, 600), "PNG", new File(MolWitch.getModuleName() +"_withSUP_dontShowSUPLabel.png"));
    }

    @Test
    public void renderBrackets() throws Exception{
        ChemicalRenderer renderer = new ChemicalRenderer();
        Chemical c = Chemical.parseMol(new File(getClass().getResource("/polymer.mol").getFile()));
        ImageIO.write(renderer.createImage(c, 600), "PNG", new File(MolWitch.getModuleName() +"_polymerWithBrackets.png"));

    }

    @Test
    public void renderAminoAcid() throws Exception{
        ChemicalRenderer renderer = new ChemicalRenderer();
        Chemical c = Chemical.parseMol(new File(getClass().getResource("/aminoAcidAsSUP.mol").getFile()));
        ImageIO.write(renderer.createImage(c, 600), "PNG", new File(MolWitch.getModuleName() +"_aminoAcidAsSUP_drawThisAtom.png"));

    }
}
