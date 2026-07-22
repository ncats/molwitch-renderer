package gov.nih.ncats.molwitch.renderer;

import gov.nih.ncats.molwitch.Atom;
import gov.nih.ncats.molwitch.Chemical;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RParseTests {
    private static final Logger log = LoggerFactory.getLogger(RParseTests.class);

    @Test
    public void parseRSeries() throws IOException {
        Chemical c = Chemical.createFromSmiles ("C");
        Atom c1= c.getAtom(0);
        for(int rGroup=1; rGroup<=100; rGroup++) {
            c1.setRGroup(rGroup);
            log.trace("looking at r {}. result: {}", rGroup, c1);
            Assert.assertTrue(c1.isRGroupAtom());
        }
    }
}
