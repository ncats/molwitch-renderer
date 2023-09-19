/*
 * NCATS-MOLWITCH-RENDERER
 *
 * Copyright 2023 NIH/NCATS
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package gov.nih.ncats.molwitch.renderer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import gov.nih.ncats.molwitch.Atom;
import gov.nih.ncats.molwitch.AtomCoordinates;
import gov.nih.ncats.molwitch.Chemical;
import org.junit.jupiter.api.Test;

public class TestBoundingBox {

	private static Chemical with2DAtoms(double...xys) {
		Chemical c = mock(Chemical.class);
		List<Atom> atoms = new ArrayList<>();
		for(int i=0; i< xys.length; i+=2) {
			Atom a = mock(Atom.class);
			when(a.getAtomCoordinates()).thenReturn(AtomCoordinates.valueOf(xys[i], xys[i+1]));
			atoms.add(a);
		}
		when(c.getAtoms()).thenReturn(atoms);
		when(c.getAtomCount()).thenReturn(xys.length/2);
		return c;
	}
	
	@Test
	public void simpleBoundingBox() {
		Chemical c = with2DAtoms(0,0, 2,0, 0,2, 2,2);
		
		Rectangle2D actual = BoundingBox.computeBoundingBoxFor(c);
		assertEquals(new Rectangle2D.Double(0,0,2,2), actual);
	}
	
	@Test
	public void molBoundingBox() {
		Chemical c = with2DAtoms(
				-3.4411,   -0.8871,
				-1.6363,    0.1230,
				1.7654,   -0.9142,
				0.8607 ,   0.6105,
				1.7899,    0.0910,
				3.5083,    0.1096,
				0.9975,   -1.5245,
				-2.4625,   -1.5103,
				2.5687,    0.5905,
				2.5225 ,  -1.4327,
				0.0000 ,   0.1068,
				4.2834,   -0.4470,
				3.4887,   -0.9815,
				-1.6511,   -0.8723,
				-0.9187,   -1.5279,
				-0.8303,    0.5858,
				-0.8540,    1.5279,
				-3.4224,    0.0551,
				-2.5454,    0.6312,
				-4.2834,   -0.3703		
				);
		
		Rectangle2D actual = BoundingBox.computeBoundingBoxFor(c);
		assertEquals(new Rectangle2D.Double(-4.2834,-1.5279,
				8.5668, 3.0558), actual);
	}
	
	@Test
	public void convexHull() {
		Chemical c = with2DAtoms(0, 3,
				2, 2, 
				1, 1,
				2, 1, 
                3, 0, 
                0, 0, 
                3, 3 );
		
		List<Point2D> hull = BoundingBox.computeConvexHullFor(c);
		List<Point2D> expected = Arrays.asList(
				new Point2D.Double(0, 0),
				new Point2D.Double(3, 0),
				new Point2D.Double(3, 3),
				new Point2D.Double(0, 3)
				);
		
		assertEquals(expected, hull);
	}
}
