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
import io.novaordis.events.api.event.PropertyFactory;
import io.novaordis.events.api.metric.MetricDefinition;
import io.novaordis.events.api.metric.MockAddress;
import io.novaordis.events.csv.CSVFormat;
import io.novaordis.events.csv.CSVFormatter;
import io.novaordis.events.csv.MockMetricDefinition;
import io.novaordis.utilities.address.Address;
import io.novaordis.utilities.address.AddressImpl;
import org.junit.Test;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    // Overrides -------------------------------------------------------------------------------------------------------

    @Test
    @Override
    public void toSpecification_String() throws Exception {

        CSVField f = getCSVFieldToTest("test", String.class);
        assertEquals("mock://mock-host:1000/test(string)", f.getSpecification());
    }

    @Test
    @Override
    public void toSpecification_Integer() throws Exception {

        CSVField f = getCSVFieldToTest("test", Integer.class);
        assertEquals("mock://mock-host:1000/test(int)", f.getSpecification());
    }

    @Test
    @Override
    public void toSpecification_Long() throws Exception {

        CSVField f = getCSVFieldToTest("test", Long.class);
        assertEquals("mock://mock-host:1000/test(long)", f.getSpecification());
    }

    @Test
    @Override
    public void toSpecification_Float() throws Exception {

        CSVField f = getCSVFieldToTest("test", Float.class);
        assertEquals("mock://mock-host:1000/test(float)", f.getSpecification());
    }

    @Test
    @Override
    public void toSpecification_Double() throws Exception {

        CSVField f = getCSVFieldToTest("test", Double.class);
        assertEquals("mock://mock-host:1000/test(double)", f.getSpecification());
    }

    @Test
    @Override
    public void toSpecification_Time() throws Exception {

        CSVField f = getCSVFieldToTest("test", Date.class);
        assertEquals("mock://mock-host:1000/test(time)", f.getSpecification());
    }

    @Test
    @Override
    public void toSpecification_Time_Format() throws Exception {

        CSVField f = getCSVFieldToTest("test", Date.class, new SimpleDateFormat("yy/MM/dd"));
        assertEquals("mock://mock-host:1000/test(time:yy/MM/dd)", f.getSpecification());
    }

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

        PropertyFactory pf = new PropertyFactory();

        MockAddress ma = new MockAddress("mock://mock-server:1000");
        MockMetricDefinition md = new MockMetricDefinition(pf, ma, "test-metric", Float.class);

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

        PropertyFactory pf = new PropertyFactory();

        //
        // logically, we model "address-1.metric-1"
        //

        Address a = new AddressImpl("address-1");
        MetricDefinition md = new MockMetricDefinition(pf, a, "metric-1");
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
    protected MetricDefinitionBasedCSVField getCSVFieldToTest(String name, Class type, Format format) throws Exception {

        MockAddress ma = new MockAddress("mock://mock-host:1000");
        PropertyFactory pf = new PropertyFactory();
        MockMetricDefinition mmd = new MockMetricDefinition(pf, ma, name, type);
        return new MetricDefinitionBasedCSVField(mmd, format);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
