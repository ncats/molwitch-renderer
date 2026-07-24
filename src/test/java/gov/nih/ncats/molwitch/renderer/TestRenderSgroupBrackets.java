package gov.nih.ncats.molwitch.renderer;

import gov.nih.ncats.molwitch.Chemical;
import gov.nih.ncats.molwitch.MolWitch;
import gov.nih.ncats.molwitch.SGroup;
import org.junit.Assert;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRenderSgroupBrackets {
    private static final Logger log = LoggerFactory.getLogger(TestRenderSgroupBrackets.class);

    @Test
    public void renderWithBracketsSet() {
        RendererOptions options = new RendererOptions();
        options.setDrawPropertyValue(RendererOptions.DrawProperties.BRACKET_POSITION_SLOPE, 0.01);
        options.setDrawPropertyValue(RendererOptions.DrawProperties.BRACKET_POSITION_INTERCEPT, 0.46);
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
                        log.trace("wrote file to {}", imageFile.getAbsolutePath());
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
        for(double slope : slopesToTest) {
            for (double intercept : interceptsToTest) {
                RendererOptions options = new RendererOptions();
                options.setDrawPropertyValue(RendererOptions.DrawProperties.BRACKET_POSITION_SLOPE, slope);
                options.setDrawPropertyValue(RendererOptions.DrawProperties.BRACKET_POSITION_INTERCEPT, intercept);
                NchemicalRenderer renderer = new NchemicalRenderer(options);

                List<String> chemicalNames = Arrays.asList("sodium_acetate", "potassium_acetate_hydrate");
                List<Boolean> results = chemicalNames.stream()
                        .map(n -> {
                            try {
                                String name = String.format("/%s.mol", n);
                                Chemical c = Chemical.parseMol(new File(getClass().getResource(name).getFile()));
                                Point2D.Double spread = NchemicalRenderer.getCoordinateSpread(c);
                                BufferedImage actual = renderer.createImage(c, 600);
                                Double lastUsed = renderer.getLastUsedFactor();
                                String imageFileName = String.format("images/%s_actual_%s_slope_%.2f_intercept_%.2f_factor_%.2f.png",
                                        MolWitch.getModuleName(), n, slope, intercept, lastUsed);
                                File imageFile = new File(imageFileName);
                                imageFile.getParentFile().mkdirs();
                                ImageIO.write(actual, "PNG", imageFile);
                                log.trace("wrote file to {} spread: {}", imageFile.getAbsolutePath(), spread.getX());
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
    public void largeCoordinateSpreadDoesNotOverPadHydrateSgroupBrackets() throws Exception {
        double slope = 0.01;
        double intercept = 0.455;

        BracketPadding sodiumWater = getBracketPadding("sodium_acetate", 0, slope, intercept);
        BracketPadding y3O2 = getBracketPadding("Y3NG9WF08W", 0, slope, intercept);
        BracketPadding y3Water = getBracketPadding("Y3NG9WF08W", 5, slope, intercept);

        Assert.assertEquals(0.9585, sodiumWater.left, 0.001);
        Assert.assertEquals(0.9600, y3O2.right, 0.001);
        Assert.assertEquals(0.9600, y3Water.left, 0.001);
        Assert.assertTrue("Y3 O2- right padding should stay comparable to sodium acetate hydrate left padding",
                y3O2.right <= sodiumWater.left + 0.01);
        Assert.assertTrue("Y3 H2O left padding should stay comparable to sodium acetate hydrate left padding",
                y3Water.left <= sodiumWater.left + 0.01);
    }

    @Test
    public void nestedY3SgroupRightBracketEdgesDoNotOverlap() throws Exception {
        double slope = 0.01;
        double intercept = 0.455;

        Rectangle2D.Float alOInner = getBracketRect("Y3NG9WF08W", 0, slope, intercept);
        Rectangle2D.Float alOOuter = getBracketRect("Y3NG9WF08W", 1, slope, intercept);
        Rectangle2D.Float siOInner = getBracketRect("Y3NG9WF08W", 2, slope, intercept);
        Rectangle2D.Float siOOuter = getBracketRect("Y3NG9WF08W", 3, slope, intercept);

        Assert.assertTrue("Nested Al/O right bracket edges should not overlap",
                alOOuter.getMaxX() - alOInner.getMaxX() >= 0.12);
        Assert.assertTrue("Nested Si/O right bracket edges should not overlap",
                siOOuter.getMaxX() - siOInner.getMaxX() >= 0.12);
    }

    @Test
    public void wideSingleSgroupBracketsClearNearestRepeatedAtoms() throws Exception {
        double slope = 0.01;
        double intercept = 0.455;

        assertBracketGapsAtLeast("14bb185c-ed7a-4b4f-b496-e085579aa4c0", 0, slope, intercept, 0.42F);
        assertLeftBracketGapAtLeastCoordinates("1680fcfc-81b9-486e-85aa-1e2cdb348d07", 0, slope, intercept, 0.42D);
        assertRightBracketGapAtLeast("1680fcfc-81b9-486e-85aa-1e2cdb348d07", 0, slope, intercept, 3D);
        assertBracketGapsAtLeast("04cb3ecb-9419-4b07-89e8-19ed0fbac5e6", 0, slope, intercept, 0.75F);

        Chemical sodiumAcetate = Chemical.parseMol(new File(getClass().getResource("/sodium_acetate.mol").getFile()));
        SGroup sodiumWater = sodiumAcetate.getSGroups().get(0);
        Rectangle2D.Float sodiumRect = getBracketRect(sodiumAcetate, sodiumWater, slope, intercept);
        float sodiumAtomGap = getRightAtomGap(sodiumWater, sodiumRect);

        Assert.assertEquals("Sodium acetate hydrate should keep its existing closing bracket atom gap",
                0.42F, sodiumAtomGap, 0.001F);
    }

    @Test
    public void leftSideImplicitHydrogenLabelsClearOpeningBracket() throws Exception {
        double slope = 0.01;
        double intercept = 0.46;

        Chemical chemical = Chemical.parseMol(new File(getClass().getResource("/KTD4ED4NYA.mol").getFile()));
        SGroup sgroup = chemical.getSGroups().get(0);
        Rectangle2D.Float rect = getBracketRect(chemical, sgroup, slope, intercept);
        double perChar = slope * Math.min(NchemicalRenderer.getCoordinateSpread(chemical).x, 2.5D) + intercept;

        Assert.assertTrue("Opening bracket should clear the terminal H3C label",
                getLeftAtomGap(sgroup, rect) >= (3D * perChar) - 0.001D);
        assertLeftBracketGapAtLeast("C1O32IJ4HS", 0, slope, intercept, 2D);
        assertLeftBracketGapAtLeast("17VU4Z4W88", 0, slope, intercept, 2D);
    }

    @Test
    public void broadMoleculeHydrateLabelsClearOpeningBracket() throws Exception {
        double slope = 0.01;
        double intercept = 0.46;

        assertLeftBracketGapAtLeast("overlapping_bracket_and_atom_3", 0, slope, intercept, 3.5D);
        assertLeftBracketGapAtLeast("overlapping_bracket_and_atom_4", 0, slope, intercept, 3.5D);
        assertLeftBracketGapAtLeast("R6DXU4WAY9", 0, slope, intercept, 3.5D);
    }

    @Test
    public void compactOxygenHydrogenLabelsClearBrackets() throws Exception {
        assertRightBracketGapAtLeast("J3OC7JVS54", 0, 0.01, 0.46, 2D);
        assertLeftImplicitHydrogenLabelGapAtLeast("J3OC7JVS54", 1, 0.01, 0.46, "O", 2D);
        assertRightBracketGapAtLeast("ZL7OV5621O", 0, 0.01, 0.46, 3D);
        assertLeftBracketGapAtLeastCoordinates("potassium_acetate_hydrate", 0, 0.01, 0.30, 0.95D);
        assertRightBracketGapAtMostCoordinates("potassium_acetate_hydrate", 0, 0.01, 0.46, 0.95D);
    }

    @Test
    public void nearbyExternalFragmentLimitsClosingBracketExpansion() throws Exception {
        Chemical chemical = Chemical.parseMol(new File(getClass().getResource("/14bb185c-ed7a-4b4f-b496-e085579aa4c0.mol").getFile()));
        SGroup sgroup = chemical.getSGroups().get(0);
        Rectangle2D.Float rect = getBracketRect(chemical, sgroup, 0.01, 0.46);
        float leftGap = getLeftAtomGap(sgroup, rect);
        float rightGap = getRightAtomGap(sgroup, rect);
        double perChar = 0.01 * Math.min(NchemicalRenderer.getCoordinateSpread(chemical).x, 2.5D) + 0.46;

        Assert.assertTrue("Opening bracket should retain base atom clearance", leftGap >= 0.42F - 0.001F);
        Assert.assertTrue("Opening bracket should not extend into the nearby left-hand fragment label", leftGap <= 0.43F);
        Assert.assertTrue("Closing bracket should clear the terminal methyl hydrogens",
                rightGap >= (2D * perChar) - 0.001D);
        Assert.assertTrue("Closing bracket should remain capped near the nearby right-hand fragment",
                rightGap <= (2D * perChar) + 0.001D);
    }

    @Test
    public void renderClosingBracketArmOverlapExamples() {
        RendererOptions rendererOptions = new RendererOptions();
        double slope = 0.01;
        double intercept = 0.455;
        rendererOptions.setDrawPropertyValue(RendererOptions.DrawProperties.BRACKET_POSITION_SLOPE, slope);
        rendererOptions.setDrawPropertyValue(RendererOptions.DrawProperties.BRACKET_POSITION_INTERCEPT, intercept);
        NchemicalRenderer renderer = new NchemicalRenderer(rendererOptions);
        List<String> chemicalNames = Arrays.asList("14bb185c-ed7a-4b4f-b496-e085579aa4c0",
                "1680fcfc-81b9-486e-85aa-1e2cdb348d07", "04cb3ecb-9419-4b07-89e8-19ed0fbac5e6");
        List<Boolean> results = chemicalNames.stream()
                .map(n -> {
                    try {
                        String name = String.format("/%s.mol", n);
                        Chemical c = Chemical.parseMol(new File(getClass().getResource(name).getFile()));
                        BufferedImage actual = renderer.createImage(c, 600);
                        Double lastUsed = renderer.getLastUsedFactor();
                        String imageFileName = String.format("images/%s_actual_%s_slope_%.2f_intercept_%.2f_factor_%.2f.png",
                                MolWitch.getModuleName(), n, slope, intercept, lastUsed);
                        File imageFile = new File(imageFileName);
                        imageFile.getParentFile().mkdirs();
                        ImageIO.write(actual, "PNG", imageFile);
                        log.info("wrote file to {}", imageFile.getAbsolutePath());
                        return imageFile.exists();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return false;
                })
                .collect(Collectors.toList());
        Assert.assertTrue(results.stream().allMatch(r -> r));
    }

    @Test
    public void renderWithBracketsCoordsOnOff() {
        RendererOptions rendererOptions = new RendererOptions();
        double slope =0.01;
        double intercept = 0.455;
        rendererOptions.setDrawPropertyValue(RendererOptions.DrawProperties.BRACKET_POSITION_SLOPE, slope);
        rendererOptions.setDrawPropertyValue(RendererOptions.DrawProperties.BRACKET_POSITION_INTERCEPT, intercept);
        NchemicalRenderer renderer = new NchemicalRenderer(rendererOptions);
        List<String> chemicalNames = Arrays.asList("sodium_acetate", "potassium_acetate_hydrate");
        List<Boolean> results = chemicalNames.stream()
                .map(n -> {
                    try {
                        String name = String.format("/%s.mol", n);
                        Chemical c = Chemical.parseMol(new File(getClass().getResource(name).getFile()));
                        Point2D.Double spread = NchemicalRenderer.getCoordinateSpread(c);
                        //renderer.setIncludeBracketCoordinates(true);
                        BufferedImage actual = renderer.createImage(c, 600);
                        Double lastUsed = renderer.getLastUsedFactor();
                        String imageFileName = String.format("images/%s_actual_%s_slope_%.2f_intercept_%.2f_factor_%.2f_on.png",
                                MolWitch.getModuleName(), n, slope, intercept, lastUsed);
                        File imageFile = new File(imageFileName);
                        imageFile.getParentFile().mkdirs();
                        ImageIO.write(actual, "PNG", imageFile);
                        log.info("wrote file to {} spread: {}", imageFile.getAbsolutePath(), spread.getX());
                        //renderer.setIncludeBracketCoordinates(false);
                        actual = renderer.createImage(c, 600);
                        String imageFileName2 = String.format("images/%s_actual_%s_slope_%.2f_intercept_%.2f_factor_%.2f_off.png",
                                MolWitch.getModuleName(), n, slope, intercept, lastUsed);
                        File imageFile2 = new File(imageFileName2);
                        ImageIO.write(actual, "PNG", imageFile2);
                        log.trace("wrote file to {} spread: {}", imageFile2.getAbsolutePath(), spread.getX());renderer.setIncludeBracketCoordinates(true);
                        return imageFile.exists();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return false;
                })
                .collect(Collectors.toList());
        Assert.assertTrue(results.stream().allMatch(r -> r));
    }

    @Test
    public void renderChallengeWithBrackets() {
        RendererOptions rendererOptions = new RendererOptions();
        double slope =0.01;
        double intercept = 0.455;
        rendererOptions.setDrawPropertyValue(RendererOptions.DrawProperties.BRACKET_POSITION_SLOPE, slope);
        rendererOptions.setDrawPropertyValue(RendererOptions.DrawProperties.BRACKET_POSITION_INTERCEPT, intercept);
        NchemicalRenderer renderer = new NchemicalRenderer(rendererOptions);
        List<String> chemicalNames = Arrays.asList("Y3NG9WF08W");
        List<Boolean> results = chemicalNames.stream()
                .map(n -> {
                    try {
                        String name = String.format("/%s.mol", n);
                        Chemical c = Chemical.parseMol(new File(getClass().getResource(name).getFile()));
                        Point2D.Double spread = NchemicalRenderer.getCoordinateSpread(c);
                        BufferedImage actual = renderer.createImage(c, 600);
                        Double lastUsed = renderer.getLastUsedFactor();
                        String imageFileName = String.format("images/%s_actual_%s_slope_%.2f_intercept_%.2f_factor_%.2f_on.png",
                                MolWitch.getModuleName(), n, slope, intercept, lastUsed);
                        File imageFile = new File(imageFileName);
                        imageFile.getParentFile().mkdirs();
                        ImageIO.write(actual, "PNG", imageFile);
                        log.info("wrote file to {} spread: {}", imageFile.getAbsolutePath(), spread.getX());
                                return imageFile.exists();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return false;
                })
                .collect(Collectors.toList());
        Assert.assertTrue(results.stream().allMatch(r -> r));
    }
    @Test
    public void renderWithBracketsMoleculeWithIssues() {
        RendererOptions rendererOptions = new RendererOptions();
        double slope =0.01;
        double intercept = 0.46;
        rendererOptions.setDrawPropertyValue(RendererOptions.DrawProperties.BRACKET_POSITION_SLOPE, slope);
        rendererOptions.setDrawPropertyValue(RendererOptions.DrawProperties.BRACKET_POSITION_INTERCEPT, intercept);
        NchemicalRenderer renderer = new NchemicalRenderer(rendererOptions);
        List<String> chemicalNames = Arrays.asList("overlapping_bracket_and_atom_3", "overlapping_bracket_and_atom_4");
        List<Boolean> results = chemicalNames.stream()
                .map(n -> {
                    try {
                        String name = String.format("/%s.mol", n);
                        Chemical c = Chemical.parseMol(new File(getClass().getResource(name).getFile()));
                        Point2D.Double spread = NchemicalRenderer.getCoordinateSpread(c);
                        BufferedImage actual = renderer.createImage(c, 600);
                        Double lastUsed = renderer.getLastUsedFactor();
                        String imageFileName = String.format("images/%s_actual_%s_slope_%.2f_intercept_%.2f_factor_%.2f_on.png",
                                MolWitch.getModuleName(), n, slope, intercept, lastUsed);
                        File imageFile = new File(imageFileName);
                        imageFile.getParentFile().mkdirs();
                        ImageIO.write(actual, "PNG", imageFile);
                        log.info("wrote file to {} spread: {}", imageFile.getAbsolutePath(), spread.getX());
                        return imageFile.exists();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return false;
                })
                .collect(Collectors.toList());
        Assert.assertTrue(results.stream().allMatch(r -> r));
    }

    @Test
    public void renderWithBrackets1MoleculeWithIssues() {
        RendererOptions rendererOptions = new RendererOptions();
        double slope =0.01;
        double intercept = 0.46;
        rendererOptions.setDrawPropertyValue(RendererOptions.DrawProperties.BRACKET_POSITION_SLOPE, slope);
        rendererOptions.setDrawPropertyValue(RendererOptions.DrawProperties.BRACKET_POSITION_INTERCEPT, intercept);
        NchemicalRenderer renderer = new NchemicalRenderer(rendererOptions);
        List<String> chemicalNames = Arrays.asList("R6DXU4WAY9");
        List<Boolean> results = chemicalNames.stream()
                .map(n -> {
                    try {
                        String name = String.format("/%s.mol", n);
                        Chemical c = Chemical.parseMol(new File(getClass().getResource(name).getFile()));
                        Point2D.Double spread = NchemicalRenderer.getCoordinateSpread(c);
                        BufferedImage actual = renderer.createImage(c, 600);
                        Double lastUsed = renderer.getLastUsedFactor();
                        String imageFileName = String.format("images/%s_actual_%s_slope_%.2f_intercept_%.2f_factor_%.2f.png",
                                MolWitch.getModuleName(), n, slope, intercept, lastUsed);
                        File imageFile = new File(imageFileName);
                        imageFile.getParentFile().mkdirs();
                        ImageIO.write(actual, "PNG", imageFile);
                        log.info("wrote file to {} spread: {}", imageFile.getAbsolutePath(), spread.getX());
                        return imageFile.exists();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return false;
                })
                .collect(Collectors.toList());
        Assert.assertTrue(results.stream().allMatch(r -> r));
    }

    @Test
    public void renderWithBrackets1MoleculeWithIssues2() {
        RendererOptions rendererOptions = new RendererOptions();
        double slope =0.01;
        double intercept = 0.46;
        rendererOptions.setDrawPropertyValue(RendererOptions.DrawProperties.BRACKET_POSITION_SLOPE, slope);
        rendererOptions.setDrawPropertyValue(RendererOptions.DrawProperties.BRACKET_POSITION_INTERCEPT, intercept);
        NchemicalRenderer renderer = new NchemicalRenderer(rendererOptions);
        List<String> chemicalNames = Arrays.asList("KTD4ED4NYA", "C1O32IJ4HS", "17VU4Z4W88");
        List<Boolean> results = chemicalNames.stream()
                .map(n -> {
                    try {
                        String name = String.format("/%s.mol", n);
                        Chemical c = Chemical.parseMol(new File(getClass().getResource(name).getFile()));
                        Point2D.Double spread = NchemicalRenderer.getCoordinateSpread(c);
                        BufferedImage actual = renderer.createImage(c, 600);
                        Double lastUsed = renderer.getLastUsedFactor();
                        String imageFileName = String.format("images/%s_actual_%s_slope_%.2f_intercept_%.2f_factor_%.2f.png",
                                MolWitch.getModuleName(), n, slope, intercept, lastUsed);
                        File imageFile = new File(imageFileName);
                        imageFile.getParentFile().mkdirs();
                        ImageIO.write(actual, "PNG", imageFile);
                        log.info("wrote file to {} spread: {}", imageFile.getAbsolutePath(), spread.getX());
                        return imageFile.exists();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return false;
                })
                .collect(Collectors.toList());
        Assert.assertTrue(results.stream().allMatch(r -> r));
    }

    @Test
    public void testGetBounds() throws IOException {
        List<String> molnames = Arrays.asList("4XYU5U00C4", "P88XT4IS4D");
        List<Double> expectedXSpreads = Arrays.asList(0.0, 9.725);
        for(int i = 0; i < molnames.size(); i++) {
            String name = String.format("/%s.mol", molnames.get(i));
            Chemical chemical = Chemical.parseMol(new File(getClass().getResource(name).getFile()));
            Point2D boundingBox = NchemicalRenderer.getCoordinateSpread(chemical);
            Assert.assertEquals(expectedXSpreads.get(i), boundingBox.getX(), 0.001);
         }
    }

    private void assertLeftBracketGapAtLeast(String resourceName, int sgroupIndex, double slope, double intercept,
            double expectedCharCount) throws Exception {
        Chemical chemical = Chemical.parseMol(new File(getClass().getResource("/" + resourceName + ".mol").getFile()));
        SGroup sgroup = chemical.getSGroups().get(sgroupIndex);
        Rectangle2D.Float rect = getBracketRect(chemical, sgroup, slope, intercept);
        double perChar = slope * Math.min(NchemicalRenderer.getCoordinateSpread(chemical).x, 2.5D) + intercept;

        Assert.assertTrue("Opening bracket should clear the rendered left-side label",
                getLeftAtomGap(sgroup, rect) >= (expectedCharCount * perChar) - 0.001D);
    }

    private void assertRightBracketGapAtLeast(String resourceName, int sgroupIndex, double slope, double intercept,
            double expectedCharCount) throws Exception {
        Chemical chemical = Chemical.parseMol(new File(getClass().getResource("/" + resourceName + ".mol").getFile()));
        SGroup sgroup = chemical.getSGroups().get(sgroupIndex);
        Rectangle2D.Float rect = getBracketRect(chemical, sgroup, slope, intercept);
        double perChar = slope * Math.min(NchemicalRenderer.getCoordinateSpread(chemical).x, 2.5D) + intercept;

        Assert.assertTrue("Closing bracket should clear the rendered right-side label",
                getRightAtomGap(sgroup, rect) >= (expectedCharCount * perChar) - 0.001D);
    }

    private void assertLeftBracketGapAtLeastCoordinates(String resourceName, int sgroupIndex, double slope,
            double intercept, double expectedGap) throws Exception {
        Chemical chemical = Chemical.parseMol(new File(getClass().getResource("/" + resourceName + ".mol").getFile()));
        SGroup sgroup = chemical.getSGroups().get(sgroupIndex);
        Rectangle2D.Float rect = getBracketRect(chemical, sgroup, slope, intercept);

        Assert.assertTrue("Opening bracket should clear the rendered left-side label",
                getLeftAtomGap(sgroup, rect) >= expectedGap - 0.001D);
    }

    private void assertRightBracketGapAtMostCoordinates(String resourceName, int sgroupIndex, double slope,
            double intercept, double expectedGap) throws Exception {
        Chemical chemical = Chemical.parseMol(new File(getClass().getResource("/" + resourceName + ".mol").getFile()));
        SGroup sgroup = chemical.getSGroups().get(sgroupIndex);
        Rectangle2D.Float rect = getBracketRect(chemical, sgroup, slope, intercept);

        Assert.assertTrue("Closing bracket should not keep unused right-side padding",
                getRightAtomGap(sgroup, rect) <= expectedGap + 0.001D);
    }

    private void assertLeftImplicitHydrogenLabelGapAtLeast(String resourceName, int sgroupIndex, double slope,
            double intercept, String atomSymbol, double expectedCharCount) throws Exception {
        Chemical chemical = Chemical.parseMol(new File(getClass().getResource("/" + resourceName + ".mol").getFile()));
        SGroup sgroup = chemical.getSGroups().get(sgroupIndex);
        Rectangle2D.Float rect = getBracketRect(chemical, sgroup, slope, intercept);
        double perChar = slope * Math.min(NchemicalRenderer.getCoordinateSpread(chemical).x, 2.5D) + intercept;
        double minLabelAtomGap = sgroup.getAtoms()
                .filter(atom -> atomSymbol.equals(atom.getSymbol()) && atom.getImplicitHCount() > 0)
                .mapToDouble(atom -> atom.getAtomCoordinates().getX() - rect.getX())
                .min()
                .orElseThrow(IllegalStateException::new);

        Assert.assertTrue("Opening bracket should clear the rendered left-side implicit hydrogen label",
                minLabelAtomGap >= (expectedCharCount * perChar) - 0.001D);
    }

    private void assertBracketGapsAtLeast(String resourceName, int sgroupIndex, double slope, double intercept,
            float expectedGap) throws Exception {
        Chemical chemical = Chemical.parseMol(new File(getClass().getResource("/" + resourceName + ".mol").getFile()));
        SGroup sgroup = chemical.getSGroups().get(sgroupIndex);
        Rectangle2D.Float rect = getBracketRect(chemical, sgroup, slope, intercept);
        float leftAtomGap = getLeftAtomGap(sgroup, rect);
        float rightAtomGap = getRightAtomGap(sgroup, rect);

        Assert.assertTrue("Wide single SGroup opening bracket should clear the nearest repeated atom",
                leftAtomGap >= expectedGap - 0.001F);
        Assert.assertTrue("Wide single SGroup closing bracket should clear the nearest repeated atom",
                rightAtomGap >= expectedGap - 0.001F);
    }

    private float getLeftAtomGap(SGroup sgroup, Rectangle2D.Float rect) {
        double minAtomX = sgroup.getAtoms()
                .mapToDouble(atom -> atom.getAtomCoordinates().getX())
                .min()
                .orElseThrow(IllegalStateException::new);
        return (float) (minAtomX - rect.getX());
    }

    private float getRightAtomGap(SGroup sgroup, Rectangle2D.Float rect) {
        double maxAtomX = sgroup.getAtoms()
                .mapToDouble(atom -> atom.getAtomCoordinates().getX())
                .max()
                .orElseThrow(IllegalStateException::new);
        return (float) (rect.getMaxX() - maxAtomX);
    }

    private BracketPadding getBracketPadding(String resourceName, int sgroupIndex, double slope, double intercept) throws Exception {
        Chemical chemical = Chemical.parseMol(new File(getClass().getResource("/" + resourceName + ".mol").getFile()));
        SGroup sgroup = chemical.getSGroups().get(sgroupIndex);
        Rectangle2D.Float rect = getBracketRect(chemical, sgroup, slope, intercept);
        double minAtomX = sgroup.getAtoms()
                .mapToDouble(atom -> atom.getAtomCoordinates().getX())
                .min()
                .orElseThrow(IllegalStateException::new);
        double maxAtomX = sgroup.getAtoms()
                .mapToDouble(atom -> atom.getAtomCoordinates().getX())
                .max()
                .orElseThrow(IllegalStateException::new);
        return new BracketPadding(minAtomX - rect.getX(), rect.getMaxX() - maxAtomX);
    }

    private Rectangle2D.Float getBracketRect(String resourceName, int sgroupIndex, double slope, double intercept) throws Exception {
        Chemical chemical = Chemical.parseMol(new File(getClass().getResource("/" + resourceName + ".mol").getFile()));
        return getBracketRect(chemical, chemical.getSGroups().get(sgroupIndex), slope, intercept);
    }

    private Rectangle2D.Float getBracketRect(Chemical chemical, SGroup sgroup, double slope, double intercept) throws Exception {
        RendererOptions options = new RendererOptions();
        options.setDrawPropertyValue(RendererOptions.DrawProperties.BRACKET_POSITION_SLOPE, slope);
        options.setDrawPropertyValue(RendererOptions.DrawProperties.BRACKET_POSITION_INTERCEPT, intercept);
        NchemicalRenderer renderer = new NchemicalRenderer(options);
        renderer.setBracketPositioningSlope(slope);
        renderer.setBracketPositioningIntercept(intercept);

        Method compute = NchemicalRenderer.class.getDeclaredMethod("computeBracketCoordsFor", SGroup.class, double.class, Chemical.class);
        compute.setAccessible(true);
        return (Rectangle2D.Float) compute.invoke(renderer, sgroup, 0D, chemical);
    }

    private static class BracketPadding {
        private final double left;
        private final double right;

        private BracketPadding(double left, double right) {
            this.left = left;
            this.right = right;
        }
    }

    //P88XT4IS4D
}
