package gov.nih.ncats.molwitch.renderer;

import gov.nih.ncats.molwitch.Atom;
import gov.nih.ncats.molwitch.Chemical;
import gov.nih.ncats.molwitch.SGroup;
import org.junit.Assume;
import org.junit.Test;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BracketGeometryProbeTest {
    @Test
    public void dumpBracketGeometryForManualReview() throws Exception {
        Assume.assumeTrue("Set -Dmolwitch.bracket.probe=true to dump bracket geometry",
                Boolean.getBoolean("molwitch.bracket.probe"));

        List<String> lines = new ArrayList<>();
        lines.add(probe("overlapping_bracket_and_atom_3", 0, 0.01, 0.46));
        lines.add(probe("overlapping_bracket_and_atom_4", 0, 0.01, 0.46));
        lines.add(probe("R6DXU4WAY9", 0, 0.01, 0.46));
        lines.add(probe("J3OC7JVS54", 0, 0.01, 0.46));
        lines.add(probe("J3OC7JVS54", 1, 0.01, 0.46));
        lines.add(probe("ZL7OV5621O", 0, 0.01, 0.46));
        lines.add(probe("14bb185c-ed7a-4b4f-b496-e085579aa4c0", 0, 0.01, 0.46));
        lines.add(probe("1680fcfc-81b9-486e-85aa-1e2cdb348d07", 0, 0.01, 0.455));
        lines.add(probe("04cb3ecb-9419-4b07-89e8-19ed0fbac5e6", 0, 0.01, 0.455));
        lines.add(probe("potassium_acetate_hydrate", 0, 0.01, 0.46));
        lines.add(probe("sodium_acetate", 0, 0.01, 0.455));
        lines.add(probe("Y3NG9WF08W", 0, 0.01, 0.455));
        lines.add(probe("Y3NG9WF08W", 5, 0.01, 0.455));

        Path output = new File("target/bracket-geometry-probe.txt").toPath();
        Files.createDirectories(output.getParent());
        Files.write(output, lines, StandardCharsets.UTF_8);
        lines.forEach(System.out::println);
    }

    private String probe(String resourceName, int sgroupIndex, double slope, double intercept) throws Exception {
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
        double perChar = slope * Math.min(NchemicalRenderer.getCoordinateSpread(chemical).x, 2.5D) + intercept;
        return String.format("%s[%d] slope=%.3f intercept=%.3f perChar=%.6f rect=%.6f..%.6f atom=%.6f..%.6f gaps=%.6f/%.6f chars=%.3f/%.3f atoms=%s",
                resourceName, sgroupIndex, slope, intercept, perChar, rect.getX(), rect.getMaxX(),
                minAtomX, maxAtomX, minAtomX - rect.getX(), rect.getMaxX() - maxAtomX,
                (minAtomX - rect.getX()) / perChar, (rect.getMaxX() - maxAtomX) / perChar,
                describeAtoms(sgroup));
    }

    private String describeAtoms(SGroup sgroup) {
        List<String> atoms = sgroup.getAtoms()
                .map(this::describeAtom)
                .collect(Collectors.toList());
        return atoms.toString();
    }

    private String describeAtom(Atom atom) {
        return atom.getSymbol() + "@" + String.format("%.4f", atom.getAtomCoordinates().getX())
                + "/H" + atom.getImplicitHCount() + "/charge" + atom.getCharge();
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
}