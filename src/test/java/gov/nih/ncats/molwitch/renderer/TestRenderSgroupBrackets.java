package gov.nih.ncats.molwitch.renderer;

import gov.nih.ncats.molwitch.Chemical;
import gov.nih.ncats.molwitch.MolWitch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Assertions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

class TestRenderSgroupBrackets {

    @ParameterizedTest
    @ValueSource(strings = {"sodium_acetate", "NFX970DSI2", "V341SPY84U", "J3OC7JVS54", "4VN69WUP7N",
            "4VN69WUP7N-dihydrate", "B37782955L", "potassium_acetate_hydrate", "potassium_propanoate_hydrate",
            "ZL7OV5621O", "ZL7OV5621O_hydrate", "potassium_propanoate_hydrate_charge_o", "potassium_propanoate_hydrate_radical_o",
            "4A5X8M1PEE_hydrate", "4A5X8M1PEE_hydrochloride", "sodium_acetate2"})
    void renderWithBracketsSet(String molName) {
        ChemicalRenderer renderer = new ChemicalRenderer();
        try {
            String name = String.format("/%s.mol", molName);
            Chemical c = Chemical.parseMol(new File(Objects.requireNonNull(getClass().getResource(name)).getFile()));
            BufferedImage actual = renderer.createImage(c, 600);
            String imageFileName = String.format("images/%sactual_%s.png", MolWitch.getModuleName(), molName);
            File imageFile = new File(imageFileName);
            ImageIO.write(actual, "PNG", imageFile);
            System.out.println("wrote file to " + imageFile.getAbsolutePath());
            Assertions.assertTrue(imageFile.exists());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @ParameterizedTest
    @ValueSource(strings = {"NG5NG5733T_sru", "polystyrene"})
    void testSruRendering(String molName) throws IOException {
        String molfileName = "/" + molName + ".mol";
        Chemical c = Chemical.parseMol(new File(Objects.requireNonNull(getClass().getResource(molfileName)).getFile()));
        ChemicalRenderer renderer = new ChemicalRenderer();
        BufferedImage actual = renderer.createImage(c, 600);
        String imageFileName = String.format("images/%sactual_%s.png", MolWitch.getModuleName(), molName);
        File imageFile = new File(imageFileName);
        ImageIO.write(actual, "PNG", imageFile);
        System.out.println("wrote file to " + imageFile.getAbsolutePath());
        Assertions.assertTrue(imageFile.exists());
    }

    @ParameterizedTest
    @ValueSource(strings = {"mutigroup1", "mutigroup1funky"})
    void testMultiGroupRendering(String molName) throws IOException {
        String molfileName = "/" + molName + ".mol";
        Chemical c = Chemical.parseMol(new File(Objects.requireNonNull(getClass().getResource(molfileName)).getFile()));
        ChemicalRenderer renderer = new ChemicalRenderer();
        BufferedImage actual = renderer.createImage(c, 600);
        String imageFileName = String.format("images/%sactual_%s.png", MolWitch.getModuleName(), molName);
        File imageFile = new File(imageFileName);
        ImageIO.write(actual, "PNG", imageFile);
        System.out.println("wrote file to " + imageFile.getAbsolutePath());
        Assertions.assertTrue(imageFile.exists());
    }

    @Test
    void renderOneWithBracketsSet() {
        String molName = "potassium_propanoate_hydrate_scaled";
        ChemicalRenderer renderer = new ChemicalRenderer();
        try {
            String name = String.format("/%s.mol", molName);
            Chemical c = Chemical.parseMol(new File(Objects.requireNonNull(getClass().getResource(name)).getFile()));
            BufferedImage actual = renderer.createImage(c, 600);
            String imageFileName = String.format("images/%sactual_%s.png", MolWitch.getModuleName(), molName);
            File imageFile = new File(imageFileName);
            ImageIO.write(actual, "PNG", imageFile);
            System.out.println("wrote file to " + imageFile.getAbsolutePath());
            Assertions.assertTrue(imageFile.exists());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
