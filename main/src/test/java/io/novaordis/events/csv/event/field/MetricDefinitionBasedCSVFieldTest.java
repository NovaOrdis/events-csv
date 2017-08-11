/*
 * Copyright (c) 2017 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.novaordis.events.csv.event.field;

import io.novaordis.events.api.event.FloatProperty;
import io.novaordis.events.api.event.GenericTimedEvent;
import io.novaordis.events.api.metric.MetricDefinition;
import io.novaordis.events.api.metric.MockAddress;
import io.novaordis.events.csv.CSVFormat;
import io.novaordis.events.csv.CSVFormatter;
import io.novaordis.events.csv.MockMetricDefinition;
import io.novaordis.utilities.address.Address;
import io.novaordis.utilities.address.AddressImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 2/6/16
 */
public class MetricDefinitionBasedCSVFieldTest extends CSVFieldTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void constructor_NullMetricDefinition() throws Exception {

        try {

            new MetricDefinitionBasedCSVField(null);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("null metric definition"));
        }
    }

    @Test
    public void constructor() throws Exception {

        MockAddress ma = new MockAddress("mock://mock-server:1000");
        MockMetricDefinition md = new MockMetricDefinition(ma, "test-metric", Float.class);

        MetricDefinitionBasedCSVField f = new MetricDefinitionBasedCSVField(md);

        assertEquals("test-metric", f.getName());
        assertEquals(Float.class, f.getType());
        assertNull(f.getFormat());
        FloatProperty fp = (FloatProperty)f.toProperty("1.2f");
        assertEquals(1.2f, fp.getFloat(), 0.00001);

        assertEquals(md, f.getMetricDefinition());
    }

    // format() --------------------------------------------------------------------------------------------------------

    @Test
    public void csvFormatter_format() throws Exception {

        //
        // logically, we model "address-1.metric-1"
        //

        Address a = new AddressImpl("address-1");
        MetricDefinition md = new MockMetricDefinition(a, "metric-1");
        MetricDefinitionBasedCSVField mdbf = new MetricDefinitionBasedCSVField(md);

        CSVFormat format = new CSVFormat();
        format.addField(mdbf);

        //
        // two-level event hierarchy
        //

        GenericTimedEvent topLevelEvent = new GenericTimedEvent(1L);
        GenericTimedEvent secondLevelEvent = new GenericTimedEvent(2L);
        secondLevelEvent.setStringProperty("metric-1", "blah");

        topLevelEvent.setEventProperty(a.getLiteral(), secondLevelEvent);

        CSVFormatter formatter = new CSVFormatter();
        formatter.setFormat(format);

        String result = formatter.format(topLevelEvent);

        assertEquals("blah\n", result);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected MetricDefinitionBasedCSVField   getCSVFieldToTest(String name, Class type) throws Exception {

        MockAddress ma = new MockAddress("mock://mock-host:1000");
        MockMetricDefinition mmd = new MockMetricDefinition(ma, name, type);
        return new MetricDefinitionBasedCSVField(mmd);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
