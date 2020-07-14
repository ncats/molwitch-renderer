/*
 * NCATS-MOLWITCH-RENDERER
 *
 * Copyright 2020 NIH/NCATS
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

import org.junit.Test;

import java.io.IOException;

public class TestSingleAtomZeroCoords {

    @Test
    public void molWith1AtomAtOrigin() throws IOException {
        String mol="\n  Marvin  07102017122D          \n\n" +
                "  1  0  0  0  1  0            999 V2000\n" +
                "    0.0000    0.0000    0.0000 H   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "M  CHG  1   1   1\n" +
                "M  END";


        ChemicalRenderer renderer = new ChemicalRenderer();
        renderer.createImage(mol, 250);
    }

    @Test
    public void smilesWith1AtomAtOrigin() throws IOException {


        ChemicalRenderer renderer = new ChemicalRenderer();
        renderer.createImage("[H+]", 250);
    }
}
