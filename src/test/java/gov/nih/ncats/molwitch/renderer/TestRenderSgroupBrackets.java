package gov.nih.ncats.molwitch.renderer;

import gov.nih.ncats.molwitch.Chemical;
import gov.nih.ncats.molwitch.MolWitch;
import org.junit.Assert;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestRenderSgroupBrackets {

    @Test
    public void renderWithBracketsSet() {
        ChemicalRenderer renderer = new ChemicalRenderer();
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
    public void renderWithBracketsVaryFactors() throws Exception{
        List<Double> factorsToTest = Arrays.asList(0.2, 0.5, 0.9, 1.0, 1.3);
        List<Double> denominatorsToTest = Arrays.asList(5.0, 7.5, 10.0, 12.0, 15.0);
        NchemicalRenderer renderer = new NchemicalRenderer();
        for(double factor : factorsToTest) {
            for (double denominator : denominatorsToTest) {
                renderer.setBracketPositioningFactor(factor);
                List<String> chemicalNames = Arrays.asList("sodium_acetate", "potassium_acetate_hydrate");
                List<Boolean> results = chemicalNames.stream()
                        .map(n -> {
                            try {
                                String name = String.format("/%s.mol", n);
                                Chemical c = Chemical.parseMol(new File(getClass().getResource(name).getFile()));
                                BufferedImage actual = renderer.createImage(c, 600);
                                String imageFileName = String.format("images/%sactual_%s_%.2f_d%.2f.png", MolWitch.getModuleName(), n, factor, denominator);
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
                Assert.assertTrue(results.stream().allMatch(r -> r));
            }
        }
    }

}
