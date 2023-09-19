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

package gov.nih.ncats.molwitch.renderer;import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class ImageTestUtil {

    private ImageTestUtil(){
        //can not instantiate
    }
    public static void assertImageDataMatches(File expected, File actual) throws IOException {
        assertImageDataMatches(ImageIO.read(expected), ImageIO.read(actual));
    }
    public static void assertImageDataMatches(File expected, BufferedImage actual) throws IOException {
        assertImageDataMatches(ImageIO.read(expected), actual);
    }
    public static void assertImageDataMatches(BufferedImage expected, BufferedImage actual){

        int[][] expectedArray = convertTo2DUsingGetRGB(expected);
        int[][] actualArray = convertTo2DUsingGetRGB(actual);
        assertEquals(expectedArray.length, actualArray.length);
        for(int i=0; i< expectedArray.length; i++){
            assertArrayEquals(expectedArray[i], actualArray[i]);
        }
//        assertArrayEquals( ((DataBufferByte) expected.getData().getDataBuffer()).getData(),
//                ( actual.getData().getDataBuffer().).getData()
//                );

    }

    private static int[][] convertTo2DUsingGetRGB(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] result = new int[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                result[row][col] = image.getRGB(col, row);
            }
        }

        return result;
    }
}
