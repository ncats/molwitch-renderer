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
        RendererOptions options = new RendererOptions();
        options.setDrawPropertyValue(RendererOptions.DrawProperties.BRACKET_POSITION_SLOPE, 0.03);
        options.setDrawPropertyValue(RendererOptions.DrawProperties.BRACKET_POSITION_INTERCEPT, 0.455);
        NchemicalRenderer renderer = new NchemicalRenderer(options);

        List<String> chemicalNames = Arrays.asList("sodium_acetate", "NFX970DSI2", "V341SPY84U", "J3OC7JVS54", "4VN69WUP7N",
                "4VN69WUP7N-dihydrate", "B37782955L","potassium_acetate_hydrate", "potassium_propanoate_hydrate",
                "ZL7OV5621O", "ZL7OV5621O_hydrate", "OC4598NZEQ"); //"F3LJ1K2O96",
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
    public void renderWithBrackets(){
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
        List<Double> slopesToTest = Arrays.asList(0.01, 0.0186, 0.02); //, 0.5, 0.9, 1.0, 1.3, 1.5
        List<Double> interceptsToTest =Arrays.asList(0.3, 0.4, 0.455, 0.6);// Arrays.asList(0.1, 0.2, 0.5, 0.7); //Arrays.asList(1.0, 5.0, 7.5, 10.0, 12.0, 15.0);//
        NchemicalRenderer renderer = new NchemicalRenderer();
        for(double slope : slopesToTest) {
            for (double intercept : interceptsToTest) {
                renderer.setBracketPositioningSlope(slope);
                List<String> chemicalNames = Arrays.asList("sodium_acetate", "potassium_acetate_hydrate", "egors_molecule4");
                List<Boolean> results = chemicalNames.stream()
                        .map(n -> {
                            try {
                                String name = String.format("/%s.mol", n);
                                Chemical c = Chemical.parseMol(new File(getClass().getResource(name).getFile()));
                                Point2D.Double spread = NchemicalRenderer.getBounds(c);
                                BufferedImage actual = renderer.createImage(c, 600);
                                Double lastUsed = renderer.getLastUsedFactor();
                                String imageFileName = String.format("images/%s_actual_%s_slope_%.2f_interacept_%.2f_factor_%.2f.png",
                                        MolWitch.getModuleName(), n, slope, intercept, lastUsed);
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

    @Test
    public void renderWithBracketsCoordsOnOff() {
        RendererOptions rendererOptions = new RendererOptions();
        double slope =0.0186;
        double intercept = 0.455;
        rendererOptions.setDrawPropertyValue(RendererOptions.DrawProperties.BRACKET_POSITION_SLOPE, slope);
        rendererOptions.setDrawPropertyValue(RendererOptions.DrawProperties.BRACKET_POSITION_INTERCEPT, intercept);
        NchemicalRenderer renderer = new NchemicalRenderer(rendererOptions);
        List<String> chemicalNames = Arrays.asList("sodium_acetate", "potassium_acetate_hydrate", "egors_molecule4");
        List<Boolean> results = chemicalNames.stream()
                .map(n -> {
                    try {
                        String name = String.format("/%s.mol", n);
                        Chemical c = Chemical.parseMol(new File(getClass().getResource(name).getFile()));
                        Point2D.Double spread = NchemicalRenderer.getBounds(c);
                        //renderer.setIncludeBracketCoordinates(true);
                        BufferedImage actual = renderer.createImage(c, 600);
                        Double lastUsed = renderer.getLastUsedFactor();
                        String imageFileName = String.format("images/%s_actual_%s_slope_%.2f_interacept_%.2f_factor_%.2f_on.png",
                                MolWitch.getModuleName(), n, slope, intercept, lastUsed);
                        File imageFile = new File(imageFileName);
                        imageFile.getParentFile().mkdirs();
                        ImageIO.write(actual, "PNG", imageFile);
                        System.out.printf("wrote file to %s spread: %.3f\n", imageFile.getAbsolutePath(), spread.getX());
                        //renderer.setIncludeBracketCoordinates(false);
                        actual = renderer.createImage(c, 600);
                        String imageFileName2 = String.format("images/%s_actual_%s_slope_%.2f_interacept_%.2f_factor_%.2f_off.png",
                                MolWitch.getModuleName(), n, slope, intercept, lastUsed);
                        File imageFile2 = new File(imageFileName2);
                        ImageIO.write(actual, "PNG", imageFile2);
                        //System.out.printf("wrote file to %s spread: %.3f\n", imageFile2.getAbsolutePath(), spread.getX());renderer.setIncludeBracketCoordinates(true);
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
