package gov.nih.ncats.molwitch.renderer;

import gov.nih.ncats.molwitch.Chemical;
import org.junit.Test;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class TestBracketIonSeparation {
    @Test
    public void ionsInAnotherBracketedGroupStayWithTheirBrackets() throws Exception {
        Chemical chemical = Chemical.parseMol(new File(getClass().getResource(
                "/bracket-issues-20261123/TR11HQD73G.mol").toURI()));
        Font font = new Font("SansSerif", Font.PLAIN, 30);
        FontRenderContext context = new FontRenderContext(null, true, true);
        NchemicalRenderer.DisplayLabel potassium = new NchemicalRenderer.DisplayLabel("K+", font, 0, 30, null);
        NchemicalRenderer.DisplayLabel cyanide = new NchemicalRenderer.DisplayLabel("CN", font, 20, 30, null);
        potassium.atomGroup = chemical.getAtom(4);
        cyanide.atomGroup = chemical.getAtom(2);

        NchemicalRenderer.separateExternalIonLabels(chemical, Arrays.asList(potassium, cyanide), context, 2D);

        assertEquals("Keep bracketed potassium at its atom position", 0F, potassium.x, 0F);
        assertEquals(20F, cyanide.x, 0F);
    }

    @Test
    public void magnesiumChargeAndMethylLabelLeaveRoomForOpeningBracket() throws Exception {
        Chemical chemical = Chemical.parseMol(new File(getClass().getResource("/IRT310VU4Z.mol").toURI()));
        Font font = new Font("SansSerif", Font.PLAIN, 30);
        FontRenderContext context = new FontRenderContext(null, true, true);
        NchemicalRenderer.DisplayLabel magnesium = new NchemicalRenderer.DisplayLabel("Mg", font, 0, 30, null);
        NchemicalRenderer.DisplayLabel charge = new NchemicalRenderer.DisplayLabel("\u00b2\u207a", font, 45, 30, null);
        NchemicalRenderer.DisplayLabel methyl = new NchemicalRenderer.DisplayLabel("H\u2083C", font, 65, 70, null);
        NchemicalRenderer.DisplayLabel oxygen = new NchemicalRenderer.DisplayLabel("O", font, 130, 20, null);
        magnesium.atomGroup = charge.atomGroup = chemical.getAtom(0);
        methyl.atomGroup = chemical.getAtom(1);
        oxygen.atomGroup = chemical.getAtom(4);
        assertFalse(chemical.getSGroups().get(0).getAtoms().anyMatch(magnesium.atomGroup::equals));

        NchemicalRenderer.separateExternalIonLabels(chemical, Arrays.asList(magnesium, charge, methyl, oxygen), context, 2D);

        // The ion and methyl can occupy different rows but the vertical bracket must clear both.
        Rectangle2D ionBounds = ink(magnesium, context).createUnion(ink(charge, context));
        Rectangle2D methylBounds = ink(methyl, context);
        assertTrue("Move the whole ion label together", magnesium.x < 0F);
        assertEquals(45F, charge.x - magnesium.x, 0.001F);
        assertEquals("Keep the SGroup label in place", 65F, methyl.x, 0F);
        Rectangle2D bracket = NchemicalRenderer.separateBracketFromExternalIonLabels(
                new Rectangle2D.Double(0, 0, 150, 100), Collections.singletonList(methylBounds),
                Arrays.asList(ink(magnesium, context), ink(charge, context)), 2D);
        assertTrue("Mg including its charge stays outside", bracket.getMinX() > ionBounds.getMaxX() + 2D);
        assertTrue("Methyl label stays inside", bracket.getMinX() < methylBounds.getMinX() - 2D);
    }

    @Test
    public void distantOrVerticallySeparateIonDoesNotMoveBracket() {
        Rectangle2D bracket = new Rectangle2D.Double(20, 0, 100, 100);
        Rectangle2D actual = NchemicalRenderer.separateBracketFromExternalIonLabels(bracket,
                Collections.singletonList(new Rectangle2D.Double(30, 20, 60, 30)),
                Arrays.asList(new Rectangle2D.Double(0, 20, 10, 20),
                        new Rectangle2D.Double(22, 120, 5, 10)), 2D);
        assertEquals(bracket, actual);
    }

    private Rectangle2D ink(NchemicalRenderer.DisplayLabel label, FontRenderContext context) {
        Rectangle2D ink = label.dfont.createGlyphVector(context, label.lab).getVisualBounds();
        return new Rectangle2D.Double(label.x + ink.getX(), label.y + ink.getY(), ink.getWidth(), ink.getHeight());
    }
}
