package gov.nih.ncats.molwitch.renderer;

import gov.nih.ncats.molwitch.Chemical;
import gov.nih.ncats.molwitch.MolWitch;
import org.junit.Ignore;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

//doing RBG tests on buffers gives different results
@Ignore
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
