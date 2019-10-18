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

import gov.nih.ncats.molwitch.Chemical;
import gov.nih.ncats.molwitch.MolWitch;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.io.File;

public class TestRenderSuperAtoms {

    @Test
    public void renderWithSUPAtomLabels() throws Exception{
        ChemicalRenderer renderer = new ChemicalRenderer();
        Chemical c = Chemical.parseMol(new File(getClass().getResource("/hasSUPs.mol").getFile()));
        ImageIO.write(renderer.createImage(c, 600), "PNG", new File(MolWitch.getModuleName() +"_withSUP_withSUPTextReversed.png"));
    }

    @Test
    public void renderWithoutSUPAtomLabels() throws Exception{
        RendererOptions opts = new RendererOptions()
                                        .setDrawOption(RendererOptions.DrawOptions.DRAW_SUPERATOMS_AS_LABELS, false);
        ChemicalRenderer renderer = new ChemicalRenderer(opts);
        Chemical c = Chemical.parseMol(new File(getClass().getResource("/hasSUPs.mol").getFile()));
        ImageIO.write(renderer.createImage(c, 600), "PNG", new File(MolWitch.getModuleName() +"_withSUP_dontShowSUPLabel.png"));
    }
}
