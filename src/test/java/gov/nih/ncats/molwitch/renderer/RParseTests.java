package gov.nih.ncats.molwitch.renderer;

import gov.nih.ncats.molwitch.Atom;
import gov.nih.ncats.molwitch.Chemical;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class RParseTests {

    @Test
    public void parseRseries() throws IOException {
        Chemical c = Chemical.createFromSmiles ("C");
        Atom c1= c.getAtom(0);
        for(int rGroup=1; rGroup<=100; rGroup++) {
            c1.setRGroup(rGroup);
            System.out.printf("looking at r %d. result: %s \n", rGroup, c1);
            Assert.assertTrue(c1.isRGroupAtom());
        }
    }
}
