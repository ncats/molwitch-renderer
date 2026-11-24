package gov.nih.ncats.molwitch.renderer;

import gov.nih.ncats.molwitch.Chemical;
import org.junit.Test;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.List;
import java.util.Arrays;
import static org.junit.Assert.*;

public class TestReportedBracketLayouts {
    static final String[] EXAMPLES = {
        "4DNT29EC86", "6V475AX06U", "7793E20Q84", "IY3DKB96QW", "O181101B5G", "TR11HQD73G", "ZTT95BJ9Y0"
    };

    @Test
    public void renderReportedExamples() throws Exception {
        BufferedImage gallery = new BufferedImage(1400, 4 * 440, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = gallery.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, gallery.getWidth(), gallery.getHeight());
        File directory = new File("target/reported-brackets/" + System.getProperty("bracket.gallery", "after"));
        directory.mkdirs();
        try {
            for (int i = 0; i < EXAMPLES.length; i++) {
                String name = EXAMPLES[i];
                Chemical chemical = Chemical.parseMol(new File(getClass().getResource(
                        "/bracket-issues-20261123/" + name + ".mol").toURI()));
                RendererOptions options = new RendererOptions();
                options.setDrawPropertyValue(RendererOptions.DrawProperties.BRACKET_POSITION_SLOPE, 0.01);
                options.setDrawPropertyValue(RendererOptions.DrawProperties.BRACKET_POSITION_INTERCEPT, 0.46);
                CheckingRenderer renderer = new CheckingRenderer(options);
                BufferedImage image = renderer.createImage(chemical, 600);
                assertTrue("Use measured labels for " + name, renderer.fitted >= chemical.getSGroups().size());
                ImageIO.write(image, "png", new File(directory, name + ".png"));
                graphics.drawImage(image, i % 2 * 700, i / 2 * 440 + 35, 390, 390, null);
                graphics.setColor(Color.BLACK);
                graphics.drawString(name, i % 2 * 700 + 10, i / 2 * 440 + 22);

                CheckingRenderer defaultRenderer = new CheckingRenderer(new RendererOptions());
                ImageIO.write(defaultRenderer.createImage(chemical, 300), "png", new File(directory, name + "-default.png"));
                assertTrue("Default options also use measured labels for " + name,
                        defaultRenderer.fitted >= chemical.getSGroups().size());
            }
        } finally {
            graphics.dispose();
        }
        ImageIO.write(gallery, "png", new File(directory, "gallery.png"));
    }

    @Test
    public void measuredBoundsReplaceOversizedAndUndersizedEstimates() {
        NchemicalRenderer renderer = new NchemicalRenderer(new RendererOptions());
        List<Rectangle2D> ink = Arrays.asList(new Rectangle2D.Double(30, 40, 25, 30),
                new Rectangle2D.Double(56, 30, 15, 12)); // superscript charge
        for (Rectangle2D estimate : Arrays.asList(new Rectangle2D.Double(0, 20, 200, 80),
                new Rectangle2D.Double(40, 40, 5, 5))) {
            Rectangle2D fitted = renderer.fitBracketToContents(estimate, ink, 5);
            assertEquals(25, fitted.getMinX(), 0.001);
            assertEquals(76, fitted.getMaxX(), 0.001);
            assertTrue(fitted.getMinY() <= 25);
            assertTrue(fitted.getMaxY() >= 75);
        }
    }

    private static class CheckingRenderer extends NchemicalRenderer {
        int fitted;

        CheckingRenderer(RendererOptions options) {
            super(options);
        }

        @Override
        Rectangle2D fitBracketToContents(Rectangle2D bracket, List<Rectangle2D> contents, double gap) {
            Rectangle2D fittedBounds = super.fitBracketToContents(bracket, contents, gap);
            double left = contents.stream().mapToDouble(Rectangle2D::getMinX).min().getAsDouble();
            double right = contents.stream().mapToDouble(Rectangle2D::getMaxX).max().getAsDouble();
            assertEquals("Opening bracket follows visible contents", gap, left - fittedBounds.getMinX(), 0.001);
            assertEquals("Closing bracket follows visible contents", gap, fittedBounds.getMaxX() - right, 0.001);
            for (Rectangle2D content : contents) {
                assertTrue("Bracket clears charge and hydrogen ink",
                        fittedBounds.getMinY() <= content.getMinY() - gap + 0.001
                                && fittedBounds.getMaxY() >= content.getMaxY() + gap - 0.001);
            }
            fitted++;
            return fittedBounds;
        }
    }
}
