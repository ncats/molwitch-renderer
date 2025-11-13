package gov.nih.ncats.molwitch.renderer;

import gov.nih.ncats.molwitch.Chemical;
import gov.nih.ncats.molwitch.MolWitch;
import org.junit.Assert;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestRenderSgroupBrackets {

    @Test
    public void renderWithBracketsSet() {
        NchemicalRenderer renderer = new NchemicalRenderer();
        renderer.setBracketPositioningIntercept(0.455);
        renderer.setBracketPositioningSlope(0.0186);
        List<String> chemicalNames = Arrays.asList("sodium_acetate", "NFX970DSI2", "V341SPY84U", "J3OC7JVS54", "4VN69WUP7N",
                "4VN69WUP7N-dihydrate", "B37782955L","potassium_acetate_hydrate", "potassium_propanoate_hydrate",
                "ZL7OV5621O", "ZL7OV5621O_hydrate"); //"F3LJ1K2O96",
        List<Boolean> results= chemicalNames.stream()
                .map(n->{
                    try {
                        String name =String.format("/%s.mol", n);
                        Chemical c = Chemical.parseMol(new File(getClass().getResource(name).getFile()));
                        BufferedImage actual = renderer.createImage(c, 600);
                        String imageFileName =String.format("images/%sactual_%s.png", MolWitch.getModuleName(), n);
                        File imageFile = new File(imageFileName);
                        imageFile.getParentFile().mkdirs();
                        ImageIO.write(actual, "PNG", imageFile);
                        System.out.println("wrote file to " + imageFile.getAbsolutePath());
                        return imageFile.exists();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return false;
                })
                .collect(Collectors.toList());


        Assert.assertTrue(results.stream().allMatch(r->r));
    }

    @Test
    public void renderWithBrackets() throws Exception{
        ChemicalRenderer renderer = new ChemicalRenderer();
        List<String> chemicalNames = Arrays.asList("egors_molecule4"); //"ZL7OV5621O_hydrate",
        List<Boolean> results= chemicalNames.stream()
                .map(n->{
                    try {
                        String name =String.format("/%s.mol", n);
                        Chemical c = Chemical.parseMol(new File(getClass().getResource(name).getFile()));
                        BufferedImage actual = renderer.createImage(c, 600);
                        String imageFileName =String.format("images/%sactual_%s.png", MolWitch.getModuleName(), n);
                        File imageFile = new File(imageFileName);
                        imageFile.getParentFile().mkdirs();
                        ImageIO.write(actual, "PNG", imageFile);
                        System.out.println("wrote file to " + imageFile.getAbsolutePath());
                        return imageFile.exists();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return false;
                })
                .collect(Collectors.toList());


        Assert.assertTrue(results.stream().allMatch(r->r));
    }

    @Test
    public void renderWithBracketsVaryingFactors() {
        List<Double> factorsToTest = Arrays.asList(0.0186); //, 0.5, 0.9, 1.0, 1.3, 1.5
        List<Double> denominatorsToTest =Arrays.asList(0.455);// Arrays.asList(0.1, 0.2, 0.5, 0.7); //Arrays.asList(1.0, 5.0, 7.5, 10.0, 12.0, 15.0);//
        NchemicalRenderer renderer = new NchemicalRenderer();
        for(double factor : factorsToTest) {
            for (double denominator : denominatorsToTest) {
                renderer.setBracketPositioningSlope(factor);
                List<String> chemicalNames = Arrays.asList("sodium_acetate", "potassium_acetate_hydrate", "egors_molecule4");
                List<Boolean> results = chemicalNames.stream()
                        .map(n -> {
                            try {
                                String name = String.format("/%s.mol", n);
                                Chemical c = Chemical.parseMol(new File(getClass().getResource(name).getFile()));
                                Point2D.Double spread = NchemicalRenderer.getBounds(c);
                                BufferedImage actual = renderer.createImage(c, 600);
                                Double lastUsed = renderer.getLastUsedFactor();
                                String imageFileName = String.format("images/%s_actual_%s_%.2f_d%.2f_l%.2f.png",
                                        MolWitch.getModuleName(), n, factor, denominator, lastUsed);
                                File imageFile = new File(imageFileName);
                                imageFile.getParentFile().mkdirs();
                                ImageIO.write(actual, "PNG", imageFile);
                                System.out.printf("wrote file to %s spread: %.3f\n", imageFile.getAbsolutePath(), spread.getX());
                                return imageFile.exists();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return false;
                        })
                        .collect(Collectors.toList());
                Assert.assertTrue(results.stream().allMatch(r -> r));
            }
        }
    }

}
