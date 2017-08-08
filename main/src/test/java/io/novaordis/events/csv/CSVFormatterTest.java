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

import io.novaordis.events.api.event.Event;
import io.novaordis.events.api.event.FaultEvent;
import io.novaordis.events.api.event.GenericEvent;
import io.novaordis.events.api.event.GenericTimedEvent;
import io.novaordis.events.api.event.MapProperty;
import io.novaordis.events.api.event.MockProperty;
import io.novaordis.events.api.metric.MockAddress;
import io.novaordis.events.csv.event.field.CSVField;
import io.novaordis.events.csv.event.field.CSVFieldFactory;
import io.novaordis.utilities.time.Timestamp;
import io.novaordis.utilities.time.TimestampImpl;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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

        String expected = CSVFormatter.DEFAULT_TIMESTAMP_FORMAT.format(d) + ", C value, B value, A value\n";
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

        String expected = "B value, , " + CSVFormatter.DEFAULT_TIMESTAMP_FORMAT.format(ts) + ", C value\n";
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
        mte.setLongProperty("some.name.with.dots", 1000);

        String s = c.format(mte);

        assertEquals("1000\n", s);
    }

    // toString(Event) -------------------------------------------------------------------------------------------------

    @Test
    public void toStringEvent_MapProperty() throws Exception {

        CSVFormatter c = new CSVFormatter();

        assertFalse(c.isHeaderOn());
        
        CSVFormat format = new CSVFormat();
        format.addField(CSVFieldFactory.fromFieldSpecification("test-map-property-name.test-map-key"));
        
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

        String expected = CSVFormatter.DEFAULT_TIMESTAMP_FORMAT.format(eventTime) + ", XXX\n";
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

        expected =
                "# timestamp, field-1\n" +
                        CSVFormatter.DEFAULT_TIMESTAMP_FORMAT.format(eventTime) + ", YYY\n";

        assertEquals(expected, output);

        //
        // make sure the header generation turns off automatically
        //

        eventTime = dateFormat.parse("01/16/01 01:01:03");
        me = new GenericTimedEvent(eventTime.getTime());
        me.setProperty(new MockProperty("field-1", "ZZZ"));

        output = c.format(me);

        expected = CSVFormatter.DEFAULT_TIMESTAMP_FORMAT.format(eventTime) + ", ZZZ\n";
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

        String expected = CSVFormatter.DEFAULT_TIMESTAMP_FORMAT.format(eventTime) + ", XXX\n";
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

        expected =
                "# timestamp, field-1\n" +
                        CSVFormatter.DEFAULT_TIMESTAMP_FORMAT.format(eventTime) + ", YYY\n";

        assertEquals(expected, output);

        //
        // make sure the header generation turns off automatically
        //

        eventTime = dateFormat.parse("01/16/01 01:01:03");
        me = new GenericTimedEvent(eventTime.getTime());
        me.setProperty(new MockProperty("field-1", "ZZZ"));

        output = c.format(me);

        expected = CSVFormatter.DEFAULT_TIMESTAMP_FORMAT.format(eventTime) + ", ZZZ\n";
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
        MockMetricDefinition mmd = new MockMetricDefinition(ma, "mock-metric-id", Long.class);
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

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
