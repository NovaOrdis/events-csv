/*
 * Copyright (c) 2016 Nova Ordis LLC
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

package io.novaordis.events.csv;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Test;

import io.novaordis.events.api.event.Event;
import io.novaordis.events.api.event.FaultEvent;
import io.novaordis.events.api.event.GenericEvent;
import io.novaordis.events.api.event.GenericTimedEvent;
import io.novaordis.events.api.event.MapProperty;
import io.novaordis.events.api.event.MockProperty;
import io.novaordis.events.api.event.PropertyFactory;
import io.novaordis.events.api.metric.MockAddress;
import io.novaordis.events.csv.event.field.CSVField;
import io.novaordis.events.csv.event.field.CSVFieldFactory;
import io.novaordis.events.csv.event.field.MetricDefinitionBasedCSVField;
import io.novaordis.utilities.address.Address;
import io.novaordis.utilities.address.AddressImpl;
import io.novaordis.utilities.time.Timestamp;
import io.novaordis.utilities.time.TimestampImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 2/2/16
 */
public class CSVFormatterTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // format() --------------------------------------------------------------------------------------------------------

    @Test
    public void format_WeDumpTheFaultOnFaultEvent() throws Exception {

        CSVFormatter c = new CSVFormatter();

        assertFalse(c.isHeaderOn());

        Event event = new FaultEvent("test message", new RuntimeException("SYNTHETIC"));

        String s = c.format(event);

        //
        // TODO we may want to consider to send the fault events to stderr
        //

        assertEquals(event.toString() + "\n", s);
    }

    @Test
    public void format_RegularNonTimedEvent_NoConfiguredOutputFormat() throws Exception {

        CSVFormatter c = new CSVFormatter();

        assertFalse(c.isHeaderOn());

        //
        // make sure no output format is configured, the default formatter provided by the sub-class may come with
        // an output format on its own
        //
        c.setFormat(null);

        GenericEvent me = new GenericEvent();

        // priority inverse to the name order
        me.setProperty(new MockProperty("A", "A value", 3));
        me.setProperty(new MockProperty("B", "B value", 2));
        me.setProperty(new MockProperty("C", "C value", 1));

        String s = c.format(me);

        assertEquals("C value, B value, A value\n", s);
    }

    @Test
    public void format_RegularTimedEvent_NoConfiguredOutputFormat() throws Exception {

        CSVFormatter c = new CSVFormatter();

        assertFalse(c.isHeaderOn());

        //
        // make sure no output format is configured, the default formatter provided by the sub-class may come with
        // an output format on its own
        //
        c.setFormat(null);

        Date d = new SimpleDateFormat("MM/yy/dd HH:mm:ss").parse("01/16/01 01:01:01");

        GenericTimedEvent me = new GenericTimedEvent(d.getTime());

        // priority inverse to the name order
        me.setProperty(new MockProperty("A", "A value", 3));
        me.setProperty(new MockProperty("B", "B value", 2));
        me.setProperty(new MockProperty("C", "C value", 1));

        String s = c.format(me);

        String expected = Constants.getDefaultTimestampFormat().format(d) + ", C value, B value, A value\n";
        assertEquals(expected, s);
    }

    @Test
    public void format_RegularNonTimedEvent_WithConfiguredOutputFormat() throws Exception {

        CSVFormatter c = new CSVFormatter();

        assertFalse(c.isHeaderOn());

        CSVFormat format = new CSVFormat();
        format.addField("B");
        format.addField("no-such-property");
        format.addField("C");

        c.setFormat(format);

        GenericEvent me = new GenericEvent();

        // priority inverse to the name order
        me.setProperty(new MockProperty("A", "A value", 3));
        me.setProperty(new MockProperty("B", "B value", 2));
        me.setProperty(new MockProperty("C", "C value", 1));

        String s = c.format(me);

        assertEquals("B value, , C value\n", s);
    }

    @Test
    public void format_RegularTimedEvent_WithConfiguredOutputFormat() throws Exception {

        CSVFormatter c = new CSVFormatter();

        assertFalse(c.isHeaderOn());

        CSVFormat format = new CSVFormat();
        format.addField("B");
        format.addField("no-such-property");
        format.addTimestampField();
        format.addField("C");

        c.setFormat(format);

        long ts = System.currentTimeMillis();

        GenericTimedEvent me = new GenericTimedEvent(ts);

        // priority inverse to the name order
        me.setProperty(new MockProperty("A", "A value", 3));
        me.setProperty(new MockProperty("B", "B value", 2));
        me.setProperty(new MockProperty("C", "C value", 1));

        String s = c.format(me);

        String expected = "B value, , " + Constants.getDefaultTimestampFormat().format(ts) + ", C value\n";
        assertEquals(expected, s);
    }

    @Test
    public void format_TimestampHasTimezoneOffsetInfo() throws Exception {

        CSVFormatter c = new CSVFormatter();

        assertFalse(c.isHeaderOn());

        CSVFormat format = new CSVFormat();
        format.addTimestampField();

        c.setFormat(format);

        DateFormat sourceDateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss Z");
        Timestamp ts = new TimestampImpl("07/01/16 10:00:00 +1100", sourceDateFormat);
        assertEquals("+1100", ts.getTimeOffset().toRFC822String());

        int ourOffset =
                (TimeZone.getDefault().getDSTSavings() + TimeZone.getDefault().getRawOffset()) / (3600 * 1000);
        assertTrue(ourOffset != 11);


        GenericTimedEvent mte = new GenericTimedEvent(ts);

        String s = c.format(mte);

        assertEquals("07/01/16 10:00:00\n", s);
    }

    @Test
    public void format_FieldNameContainsDot() throws Exception {

        CSVFormatter c = new CSVFormatter();

        CSVFormat format = new CSVFormat();
        format.addField("some.name.with.dots");

        c.setFormat(format);

        GenericTimedEvent mte = new GenericTimedEvent(System.currentTimeMillis());
        mte.setLongProperty("some.name.with.dots", 1000L);

        String s = c.format(mte);

        assertEquals("1000\n", s);
    }

    // toString(Event) -------------------------------------------------------------------------------------------------

    @Test
    public void toStringEvent_MapProperty() throws Exception {

        CSVFormatter c = new CSVFormatter();

        assertFalse(c.isHeaderOn());
        
        CSVFormat format = new CSVFormat();
        format.addField(CSVFieldFactory.fromSpecification("test-map-property-name.test-map-key"));
        
        c.setFormat(format);

        GenericTimedEvent e = new GenericTimedEvent(new TimestampImpl(1L));

        Map<String, Object> mapContent = new HashMap<>();
        mapContent.put("test-map-key", "TEST-VALUE");
        e.setProperty(new MapProperty("test-map-property-name", mapContent));

        String result = c.toString(e);
        assertEquals("TEST-VALUE", result);
    }

    @Test
    public void toStringEvent_TimestampHasTimezoneOffsetInfo() throws Exception {

        CSVFormatter c = new CSVFormatter();
        assertFalse(c.isHeaderOn());

        CSVFormat format = new CSVFormat();
        format.addTimestampField();

        c.setFormat(format);

        DateFormat sourceDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss Z");
        Timestamp ts = new TimestampImpl("01/07/16 10:00:00 +1100", sourceDateFormat);
        assertEquals("+1100", ts.getTimeOffset().toRFC822String());

        int ourOffset =
                (TimeZone.getDefault().getDSTSavings() + TimeZone.getDefault().getRawOffset()) / (3600 * 1000);
        assertTrue(ourOffset != 11);

        GenericTimedEvent mte = new GenericTimedEvent(ts);

        String result = c.toString(mte);

        assertEquals("07/01/16 10:00:00", result);
    }

    @Test
    public void toStringEvent_TimestampDoesNOTHaveTimezoneOffsetInfo() throws Exception {

        CSVFormatter c = new CSVFormatter();
        assertFalse(c.isHeaderOn());

        CSVFormat format = new CSVFormat();
        format.addTimestampField();

        c.setFormat(format);

        DateFormat sourceDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Timestamp ts = new TimestampImpl("01/07/16 10:00:00", sourceDateFormat);

        GenericTimedEvent mte = new GenericTimedEvent(ts);

        String result = c.toString(mte);

        assertEquals("07/01/16 10:00:00", result);
    }

    @Test
    public void toStringEvent_NoFormat_Introspection_EmptyTimedEvent() throws Exception {

        CSVFormatter c = new CSVFormatter();
        assertFalse(c.isHeaderOn());
        assertNull(c.getFormat());

        GenericTimedEvent e = new GenericTimedEvent(1001L);

        String expected = Constants.getDefaultTimestampFormat().format(1001L);
        String result = c.toString(e);
        assertEquals(expected, result);
    }

    @Test
    public void toStringEvent_NoFormat_Introspection_NonEmptyTimedEvent() throws Exception {

        CSVFormatter c = new CSVFormatter();
        assertFalse(c.isHeaderOn());
        assertNull(c.getFormat());

        GenericTimedEvent e = new GenericTimedEvent(1001L);
        e.setStringProperty("something", "something else");

        String expected = Constants.getDefaultTimestampFormat().format(1001L) + ", something else";
        String result = c.toString(e);
        assertEquals(expected, result);
    }

    // setFormat() -----------------------------------------------------------------------------------------------

    @Test
    public void setFormat_Null() throws Exception {

        CSVFormatter o = new CSVFormatter();
        o.setFormat(null);
        assertNull(o.getFormat());
    }

    @Test
    public void setFormat_OneField() throws Exception {

        CSVFormatter o = new CSVFormatter();

        CSVFormat format = new CSVFormat();
        format.addField("a");

        o.setFormat(format);

        CSVFormat format2 = o.getFormat();
        
        assertEquals(1, format2.getFields().size());
        
        CSVField f = format2.getFields().get(0);
        assertEquals("a", f.getName());
    }

    @Test
    public void setFormat_TwoFields() throws Exception {

        CSVFormatter o = new CSVFormatter();

        CSVFormat format = new CSVFormat();
        format.addField("a");
        format.addField("b");

        o.setFormat(format);

        CSVFormat format2 = o.getFormat();

        assertEquals(2, format2.getFields().size());

        CSVField f = format2.getFields().get(0);
        assertEquals("a", f.getName());
        CSVField f2 = format2.getFields().get(1);
        assertEquals("b", f2.getName());
    }

    // header line -----------------------------------------------------------------------------------------------------

    @Test
    public void outputHeader_OutputFormatSet() throws Exception {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yy/dd HH:mm:ss");

        CSVFormatter c = new CSVFormatter();
        assertFalse(c.isHeaderOn());

        CSVFormat format = new CSVFormat();
        format.addTimestampField();
        format.addField("field-1");

        Date eventTime = dateFormat.parse("01/16/01 01:01:01");
        GenericTimedEvent me = new GenericTimedEvent(eventTime.getTime());
        me.setProperty(new MockProperty("field-1", "XXX"));

        String output = c.format(me);

        String expected = Constants.getDefaultTimestampFormat().format(eventTime) + ", XXX\n";
        assertEquals(expected, output);

        //
        // turn on header generation
        //

        c.setHeaderOn();
        assertTrue(c.isHeaderOn());

        eventTime = dateFormat.parse("01/16/01 01:01:02");
        me = new GenericTimedEvent(eventTime.getTime());
        me.setProperty(new MockProperty("field-1", "YYY"));

        assertTrue(c.isHeaderOn());

        output = c.format(me);

        expected = "# time, field-1\n" + Constants.getDefaultTimestampFormat().format(eventTime) + ", YYY\n";

        assertEquals(expected, output);

        //
        // make sure the header generation turns off automatically
        //

        eventTime = dateFormat.parse("01/16/01 01:01:03");
        me = new GenericTimedEvent(eventTime.getTime());
        me.setProperty(new MockProperty("field-1", "ZZZ"));

        output = c.format(me);

        expected = Constants.getDefaultTimestampFormat().format(eventTime) + ", ZZZ\n";
        assertEquals(expected, output);
    }

    @Test
    public void outputHeader_OutputFormatNotSet() throws Exception {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yy/dd HH:mm:ss");

        CSVFormatter c = new CSVFormatter();

        assertFalse(c.isHeaderOn());

        assertNull(c.getFormat());

        Date eventTime = dateFormat.parse("01/16/01 01:01:01");
        GenericTimedEvent me = new GenericTimedEvent(eventTime.getTime());
        me.setProperty(new MockProperty("field-1", "XXX"));

        String output = c.format(me);

        String expected = Constants.getDefaultTimestampFormat().format(eventTime) + ", XXX\n";
        assertEquals(expected, output);

        //
        // turn on header generation
        //

        c.setHeaderOn();
        assertTrue(c.isHeaderOn());

        eventTime = dateFormat.parse("01/16/01 01:01:02");
        me = new GenericTimedEvent(eventTime.getTime());
        me.setProperty(new MockProperty("field-1", "YYY"));

        assertTrue(c.isHeaderOn());

        output = c.format(me);

        expected =  "# time, field-1\n" + Constants.getDefaultTimestampFormat().format(eventTime) + ", YYY\n";

        assertEquals(expected, output);

        //
        // make sure the header generation turns off automatically
        //

        eventTime = dateFormat.parse("01/16/01 01:01:03");
        me = new GenericTimedEvent(eventTime.getTime());
        me.setProperty(new MockProperty("field-1", "ZZZ"));

        output = c.format(me);

        expected = Constants.getDefaultTimestampFormat().format(eventTime) + ", ZZZ\n";
        assertEquals(expected, output);
    }

    // outputFormatToHeader() ------------------------------------------------------------------------------------------

    @Test
    public void outputFormatToHeader() throws Exception {

        CSVFormat format = new CSVFormat();
        format.addField("a");
        format.addField("b");
        format.addField("c");

        String header = CSVFormatter.outputFormatToHeader(format);

        assertEquals("# a, b, c", header);
    }

    @Test
    public void outputFormatToHeader_KnownMetric() throws Exception {

        MockAddress ma = new MockAddress("mock://mock-host:1000");
        PropertyFactory f = new PropertyFactory();
        MockMetricDefinition mmd = new MockMetricDefinition(f, ma, "mock-metric-id", Long.class);
        mmd.setSimpleLabel("TEST-SIMPLE-LABEL");

        CSVFormat format = new CSVFormat();

        format.addField(mmd);

        String header = CSVFormatter.outputFormatToHeader(format);

        String expected = "# TEST-SIMPLE-LABEL";

        assertEquals(expected, header);
    }

    @Test
    public void outputFormatToHeader_FieldNameContainsCommas() throws Exception {

        CSVFormat format = new CSVFormat();

        format.addField("this, field, contains, commas, in, its name");

        String header = CSVFormatter.outputFormatToHeader(format);

        assertEquals("# \"this, field, contains, commas, in, its name\"", header);
    }

    @Test
    public void outputFormatToHeader_FieldNameContainsCommas2() throws Exception {

        CSVFormat format = new CSVFormat();

        format.addField("A");
        format.addField("this, field, contains, commas, in, its name");

        String header = CSVFormatter.outputFormatToHeader(format);

        assertEquals("# A, \"this, field, contains, commas, in, its name\"", header);
    }

    @Test
    public void outputFormatToHeader_FieldNameContainsDots() throws Exception {

        CSVFormat format = new CSVFormat();

        format.addField("this.field.contains.dots.in.its.name");

        String header = CSVFormatter.outputFormatToHeader(format);

        assertEquals("# \"this.field.contains.dots.in.its.name\"", header);
    }

    @Test
    public void outputFormatToHeader_FieldNameContainsDots2() throws Exception {

        CSVFormat format = new CSVFormat();

        format.addField("A");
        format.addField("this.field.contains.dots.in.its.name");

        String header = CSVFormatter.outputFormatToHeader(format);

        assertEquals("# A, \"this.field.contains.dots.in.its.name\"", header);
    }

    // extractValueForMetricDefinitionBasedCSVField() ------------------------------------------------------------------

    @Test
    public void extractValueForMetricDefinitionBasedCSVField_NullEvent() throws Exception {

        PropertyFactory pf = new PropertyFactory();

        MetricDefinitionBasedCSVField f =
                new MetricDefinitionBasedCSVField(new MockMetricDefinition(pf, new AddressImpl("test")));

        try {

            CSVFormatter.extractValueForMetricDefinitionBasedCSVField(null, f);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("null event"));
        }
    }

    @Test
    public void extractValueForMetricDefinitionBasedCSVField_NullField() throws Exception {

        GenericTimedEvent e = new GenericTimedEvent();

        try {

            CSVFormatter.extractValueForMetricDefinitionBasedCSVField(e, null);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException iae) {

            String msg = iae.getMessage();
            assertTrue(msg.contains("null CSV field"));
        }
    }

    @Test
    public void extractValueForMetricDefinitionBasedCSVField_NoEventPropertyForAddress() throws Exception {

        PropertyFactory pf = new PropertyFactory();

        Address a = new AddressImpl("address-1");
        MockMetricDefinition mmd = new MockMetricDefinition(pf, a, "does-not-matter");
        MetricDefinitionBasedCSVField f = new MetricDefinitionBasedCSVField(mmd);

        GenericTimedEvent e = new GenericTimedEvent();

        //
        // no event property corresponding to "address-1"
        //

        String addressLiteral = a.getLiteral();
        assertNull(e.getProperty(addressLiteral));

        String result = CSVFormatter.extractValueForMetricDefinitionBasedCSVField(e, f);
        assertNull(result);
    }

    @Test
    public void extractValueForMetricDefinitionBasedCSVField_NoSecondLevelPropertyForMetricID() throws Exception {

        PropertyFactory pf = new PropertyFactory();

        Address a = new AddressImpl("address-1");
        MockMetricDefinition mmd = new MockMetricDefinition(pf, a, "no-such-metric-id");
        MetricDefinitionBasedCSVField f = new MetricDefinitionBasedCSVField(mmd);

        GenericTimedEvent topLevelEvent = new GenericTimedEvent(1L);

        //
        // we load the top-level event with an event property that carries a second-level event. However, the
        // second-level event has a property for "metric-1", but not "metric-2"
        //

        GenericTimedEvent secondLevelEvent = new GenericTimedEvent(2L);
        secondLevelEvent.setStringProperty("metric-1", "something");

        String addressLiteral = a.getLiteral();

        topLevelEvent.setEventProperty(addressLiteral, secondLevelEvent);

        String result = CSVFormatter.extractValueForMetricDefinitionBasedCSVField(topLevelEvent, f);

        assertNull(result);
    }

    @Test
    public void extractValueForMetricDefinitionBasedCSVField() throws Exception {

        PropertyFactory pf = new PropertyFactory();

        Address a = new AddressImpl("address-1");
        MockMetricDefinition mmd = new MockMetricDefinition(pf, a, "metric-1");
        MetricDefinitionBasedCSVField f = new MetricDefinitionBasedCSVField(mmd);

        GenericTimedEvent topLevelEvent = new GenericTimedEvent(1L);

        GenericTimedEvent secondLevelEvent = new GenericTimedEvent(2L);
        secondLevelEvent.setStringProperty("metric-1", "something");

        String addressLiteral = a.getLiteral();

        topLevelEvent.setEventProperty(addressLiteral, secondLevelEvent);

        String result = CSVFormatter.extractValueForMetricDefinitionBasedCSVField(topLevelEvent, f);

        assertEquals("something", result);
    }

    // extractValueForPropertyWithGivenName() --------------------------------------------------------------------------

    @Test
    public void extractValueForPropertyWithGivenName_NullEvent() throws Exception {

        try {

            CSVFormatter.extractValueForPropertyWithGivenName(null, "something");
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("null event"));
        }
    }

    @Test
    public void extractValueForPropertyWithGivenName_NullName() throws Exception {

        GenericTimedEvent e = new GenericTimedEvent();

        try {

            CSVFormatter.extractValueForPropertyWithGivenName(e, null);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException iae) {

            String msg = iae.getMessage();
            assertTrue(msg.contains("null property name"));
        }
    }

    @Test
    public void extractValueForPropertyWithGivenName_NoPropertyWithSuchName() throws Exception {

        GenericTimedEvent e = new GenericTimedEvent();

        String result = CSVFormatter.extractValueForPropertyWithGivenName(e, "no-such-name");

        assertNull(result);
    }

    @Test
    public void extractValueForPropertyWithGivenName() throws Exception {

        GenericTimedEvent e = new GenericTimedEvent(1L);

        e.setStringProperty("property-name-1", "something");

        String result = CSVFormatter.extractValueForPropertyWithGivenName(e, "property-name-1");

        assertEquals("something", result);
    }

    // setIgnoreFaults() -----------------------------------------------------------------------------------------------

    @Test
    public void setIgnoreFaults() throws Exception {

        CSVFormatter f = new CSVFormatter();

        assertFalse(f.isIgnoreFaults());

        f.setIgnoreFaults(true);

        assertTrue(f.isIgnoreFaults());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
