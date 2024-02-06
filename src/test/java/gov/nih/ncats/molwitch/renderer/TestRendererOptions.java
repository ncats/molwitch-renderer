/*
 * NCATS-MOLWITCH-RENDERER
 *
 * Copyright 2024 NIH/NCATS
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

import org.junit.Test;

import gov.nih.ncats.molwitch.renderer.RendererOptions.DrawOptions;
import gov.nih.ncats.molwitch.renderer.RendererOptions.DrawProperties;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

public class TestRendererOptions {

	@Test
	public void defaultUsesDefaultValuesFromEnum() {
		RendererOptions renderer =  RendererOptions.createDefault();
		
		for(DrawOptions opt : DrawOptions.values()) {
			assertEquals(opt.defaultValue(), renderer.getDrawOption(opt));
		}
		
		for(DrawProperties prop : DrawProperties.values()) {
			assertEquals(prop.defaultValue(), renderer.getDrawPropertyValue(prop), 0.001D);
		}
	}


	
	@Test
	public void overrideDrawOptions() {
		RendererOptions renderer =  RendererOptions.createDefault();
		
		for(DrawOptions opt : DrawOptions.values()) {
			renderer.setDrawOption(opt, !opt.defaultValue());
			assertEquals(!opt.defaultValue(), renderer.getDrawOption(opt));
		}
	}
	@Test
	public void overrideDrawProperties() {
		RendererOptions renderer =  RendererOptions.createDefault();
		
		for(DrawProperties opt : DrawProperties.values()) {
			renderer.setDrawPropertyValue(opt, opt.defaultValue()-1);
			assertEquals(opt.defaultValue()-1, renderer.getDrawPropertyValue(opt), 0.001D);
		}
	}
	@Test
	public void changeSettingsViaMapString() {
		RendererOptions renderer =  RendererOptions.createDefault();
		
		Map<String, Object> map = new HashMap<>();
		
		map.put(DrawOptions.DRAW_STEREO_BONDS.name(), Boolean.toString(!DrawOptions.DRAW_STEREO_BONDS.defaultValue()));
		
		map.put(DrawProperties.BOND_EXPECTED_LENGTH.name(), Double.toString(DrawProperties.BOND_EXPECTED_LENGTH.defaultValue() +1));
		
		
		renderer.changeSettings(map);
		
		assertEquals(!DrawOptions.DRAW_STEREO_BONDS.defaultValue(), renderer.getDrawOption(DrawOptions.DRAW_STEREO_BONDS));
	
		assertEquals(DrawProperties.BOND_EXPECTED_LENGTH.defaultValue() +1, renderer.getDrawPropertyValue(DrawProperties.BOND_EXPECTED_LENGTH), 0.001D);
	
	}
	
	@Test
	public void changeSettingsViaMapDoubleAndBoolean() {
		RendererOptions renderer =  RendererOptions.createDefault();
		
		Map<String, Object> map = new HashMap<>();
		
		map.put(DrawOptions.DRAW_STEREO_BONDS.name(), !DrawOptions.DRAW_STEREO_BONDS.defaultValue());
		
		map.put(DrawProperties.BOND_EXPECTED_LENGTH.name(), DrawProperties.BOND_EXPECTED_LENGTH.defaultValue() +1);
		
		
		renderer.changeSettings(map);
		
		assertEquals(!DrawOptions.DRAW_STEREO_BONDS.defaultValue(), renderer.getDrawOption(DrawOptions.DRAW_STEREO_BONDS));
	
		assertEquals(DrawProperties.BOND_EXPECTED_LENGTH.defaultValue() +1, renderer.getDrawPropertyValue(DrawProperties.BOND_EXPECTED_LENGTH), 0.001D);
	
	}
}
