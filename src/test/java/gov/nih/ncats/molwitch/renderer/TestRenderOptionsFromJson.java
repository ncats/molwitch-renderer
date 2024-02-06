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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;
public class TestRenderOptionsFromJson {

    @Test
    public void sameObjectHasSameValues(){
        RendererOptions opts = new RendererOptions();
        assertValuesEqual(opts, opts);
    }
    @Test(expected = AssertionError.class)
    public void assertionFailsWhenAllTheSame(){
        RendererOptions opts = new RendererOptions();
        assertValuesNotEqual(opts, opts);
    }

    @Test
    public void uspIsNotDefault(){
        assertValuesNotEqual(RendererOptions.createDefault(), RendererOptions.createUSPLike());
    }

    @Test
    public void flippingBooleanNotEqual(){
        RendererOptions opts = new RendererOptions();
        for(RendererOptions.DrawOptions o : RendererOptions.DrawOptions.values()){
            RendererOptions other = new RendererOptions();
            other.setDrawOption(o, !o.defaultValue());
            assertValuesNotEqual(o.toString(), opts, other);
        }
    }
    @Test
    public void changingPropertyValueNotEqual(){
        RendererOptions opts = new RendererOptions();
        for(RendererOptions.DrawProperties o : RendererOptions.DrawProperties.values()){
            RendererOptions other = new RendererOptions();
            other.setDrawPropertyValue(o, o.defaultValue() -1);
            assertValuesNotEqual(o.toString(), opts, other);
        }
    }
    @Test
    public void createFromMap() {
        RendererOptions expected = RendererOptions.createUSPLike();
        Map<String, Object> map = expected.asMap();
        RendererOptions sut = RendererOptions.createFromMap(map);

        assertValuesEqual(expected, sut);
    }

    @Test
    public void createFromJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        RendererOptions expected = RendererOptions.createUSPLike();

        Map<String, Object> map = expected.asMap();
        String json = mapper.writer().writeValueAsString(map);


        RendererOptions sut = mapper.readValue(json, RendererOptions.class);

        assertValuesEqual(expected, sut);
    }
    @Test
    public void createFromJsonNonDefault() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        RendererOptions expected = RendererOptions.createUSPLike();

        Map<String, Object> map = expected.asNonDefaultMap();
        String json = mapper.writer().writeValueAsString(map);


        RendererOptions sut = mapper.readValue(json, RendererOptions.class);

        assertValuesEqual(expected, sut);
    }

    private void assertValuesEqual(RendererOptions expected, RendererOptions sut) {
        for (RendererOptions.DrawProperties v : RendererOptions.DrawProperties.values()) {
            assertEquals(expected.getDrawPropertyValue(v), sut.getDrawPropertyValue(v), .01D);
        }
        for (RendererOptions.DrawOptions v : RendererOptions.DrawOptions.values()) {
            assertEquals(expected.getDrawOption(v), sut.getDrawOption(v));

        }
    }
    private void assertValuesNotEqual(RendererOptions expected, RendererOptions sut) {
        assertValuesNotEqual(null, expected, sut);
    }
    private void assertValuesNotEqual(String message, RendererOptions expected, RendererOptions sut) {
        for (RendererOptions.DrawProperties v : RendererOptions.DrawProperties.values()) {
            if(Double.doubleToLongBits(expected.getDrawPropertyValue(v)) !=Double.doubleToLongBits(sut.getDrawPropertyValue(v))){
                //found something that doesn't match we're good!
                return;
            }
        }
        for (RendererOptions.DrawOptions v : RendererOptions.DrawOptions.values()) {
            if(expected.getDrawOption(v)!= sut.getDrawOption(v)){
                //found something that doesn't match we're good!
                return;
            }

        }
        throw new AssertionError(message==null?"values all equal": message);
    }

    @Test
    public void jacksonSerializer() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(RendererOptions.createUSPLike());
        System.out.println(json);
        RendererOptions actual = mapper.readValue(json, RendererOptions.class);
        assertValuesEqual(RendererOptions.createUSPLike(), actual);

    }
}
