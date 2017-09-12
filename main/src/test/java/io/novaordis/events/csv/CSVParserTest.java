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
import io.novaordis.events.api.event.GenericEvent;
import io.novaordis.events.api.event.GenericTimedEvent;
import io.novaordis.events.api.event.IntegerProperty;
import io.novaordis.events.api.event.LongProperty;
import io.novaordis.events.api.event.Property;
import io.novaordis.events.api.event.PropertyFactory;
import io.novaordis.events.api.event.StringProperty;
import io.novaordis.events.api.event.TimedEvent;
import io.novaordis.events.api.event.TimestampProperty;
import io.novaordis.events.api.event.UndefinedTypeProperty;
import io.novaordis.utilities.parsing.ParsingException;
import io.novaordis.events.csv.event.CSVEvent;
import io.novaordis.events.csv.event.CSVHeaders;
import io.novaordis.events.csv.event.NonTimedCSVLine;
import io.novaordis.events.csv.event.TimedCSVLine;
import io.novaordis.events.csv.event.field.CSVField;
import io.novaordis.events.csv.event.field.CSVFieldImpl;
import io.novaordis.events.csv.event.field.TimestampCSVField;
import io.novaordis.events.csv.event.field.UTCMillisecondsLongTimestampFormat;
import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 2/6/16
 */
public class CSVParserTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // constructors ----------------------------------------------------------------------------------------------------

    @Test
    public void constructor_NoFormat() throws Exception {

        CSVParser p = new CSVParser();
        assertNull(p.getFormat());
    }

    @Test
    public void constructor() throws Exception {

        CSVParser p = new CSVParser("a, ");

        CSVFormat format = p.getFormat();

        List<CSVField> fields = format.getFields();
        assertEquals(1, fields.size());
        assertEquals("a", fields.get(0).getName());
    }

    @Test
    public void constructor2() throws Exception {

        CSVParser p = new CSVParser("a, something, something-else,");

        CSVFormat format = p.getFormat();

        List<CSVField> fields = format.getFields();
        assertEquals(3, fields.size());
        assertEquals("a", fields.get(0).getName());
        assertEquals("something", fields.get(1).getName());
        assertEquals("something-else", fields.get(2).getName());
    }

    @Test
    public void constructor4() throws Exception {

        CSVParser p = new CSVParser("a, b, c");
        CSVFormat format = p.getFormat();

        List<CSVField> fields = format.getFields();
        assertEquals(3, fields.size());
        assertEquals("a", fields.get(0).getName());
        assertEquals("b", fields.get(1).getName());
        assertEquals("c", fields.get(2).getName());
    }

    @Test
    public void constructor5() throws Exception {

        CSVParser p = new CSVParser("a");
        CSVFormat format = p.getFormat();

        List<CSVField> fields = format.getFields();
        assertEquals(1, fields.size());
        assertEquals("a", fields.get(0).getName());
    }

    @Test
    public void constructor_InvalidFormat_EmptyFields() throws Exception {

        try {

            new CSVParser(", ,");
            fail("should throw exception");
        }
        catch(CSVFormatException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid CSV format specification"));
            assertTrue(msg.contains("field 0 null"));
        }
    }

    @Test
    public void constructor_InvalidFormat_EmptyFields2() throws Exception {

        try {

            new CSVParser(",");
            fail("should throw exception");
        }
        catch(CSVFormatException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid CSV format specification"));
            assertTrue(msg.contains("field 0 null"));
        }
    }

    @Test
    public void constructor_InvalidFormat() throws Exception {

        try {

            new CSVParser("a(blah)");
            fail("should have thrown exception");
        }
        catch(CSVFormatException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid"));
        }
    }

    @Test
    public void constructor_Headers_NoTimestamp() throws Exception {

        CSVParser p = new CSVParser("a, b, c");

        List<CSVField> headers = p.getFormat().getFields();
        assertEquals(3, headers.size());

        CSVField h = headers.get(0);
        assertEquals("a", h.getName());
        assertTrue(String.class.equals(h.getType()));

        CSVField h2 = headers.get(1);
        assertEquals("b", h2.getName());
        assertTrue(String.class.equals(h2.getType()));

        CSVField h3 = headers.get(2);
        assertEquals("c", h3.getName());
        assertTrue(String.class.equals(h3.getType()));
    }

    @Test
    public void constructor_Headers_Timestamp() throws Exception {

        CSVParser p = new CSVParser("T(time:yyyy), b, c");

        List<CSVField> headers = p.getFormat().getFields();
        assertEquals(3, headers.size());

        CSVField h = headers.get(0);
        assertEquals("T", h.getName());
        assertTrue(Date.class.equals(h.getType()));
        assertTrue(h.getFormat() instanceof SimpleDateFormat);

        CSVField h2 = headers.get(1);
        assertEquals("b", h2.getName());
        assertTrue(String.class.equals(h2.getType()));

        CSVField h3 = headers.get(2);
        assertEquals("c", h3.getName());
        assertTrue(String.class.equals(h3.getType()));
    }

    // parse() ---------------------------------------------------------------------------------------------------------

    @Test
    public void parse_FormatPresent() throws Exception {

        CSVParser parser = new CSVParser("a, b, c");

        List<Event> result = parser.parse(7L, "A, B, C");
        assertEquals(1, result.size());

        GenericEvent event = (GenericEvent)result.get(0);

        assertNotNull(event);

        List<Property> properties = event.getProperties();

        assertEquals(4, properties.size());

        LongProperty p0 = (LongProperty)properties.get(0);
        assertEquals(Event.LINE_NUMBER_PROPERTY_NAME, p0.getName());
        assertEquals(7L, p0.getValue());

        StringProperty p = (StringProperty)properties.get(1);
        assertEquals("a", p.getName());
        assertEquals("A", p.getValue());

        StringProperty p2 = (StringProperty)properties.get(2);
        assertEquals("b", p2.getName());
        assertEquals("B", p2.getValue());

        StringProperty p3 = (StringProperty)properties.get(3);
        assertEquals("c", p3.getName());
        assertEquals("C", p3.getValue());
    }

    @Test
    public void parse_FormatPresent_LineLongerThanFormat() throws Exception {

        CSVParser parser = new CSVParser("a, b, c");

        List<Event> result = parser.parse(7L, "A, B, C, D");
        assertEquals(1, result.size());

        GenericEvent event = (GenericEvent)result.get(0);

        assertNotNull(event);

        List<Property> properties = event.getProperties();

        assertEquals(4, properties.size());

        LongProperty p0 = (LongProperty)properties.get(0);
        assertEquals(Event.LINE_NUMBER_PROPERTY_NAME, p0.getName());
        assertEquals(7L, p0.getValue());

        StringProperty p = (StringProperty)properties.get(1);
        assertEquals("a", p.getName());
        assertEquals("A", p.getValue());

        StringProperty p2 = (StringProperty)properties.get(2);
        assertEquals("b", p2.getName());
        assertEquals("B", p2.getValue());

        StringProperty p3 = (StringProperty)properties.get(3);
        assertEquals("c", p3.getName());
        assertEquals("C", p3.getValue());
    }

    @Test
    public void parse_FormatPresent_LineShorterThanFormat() throws Exception {

        CSVParser parser = new CSVParser("a, b, c");

        List<Event> result = parser.parse(1L, "A, B");
        assertEquals(1, result.size());

        GenericEvent event = (GenericEvent)result.get(0);
        assertNotNull(event);

        List<Property> properties = event.getProperties();

        assertEquals(3, properties.size());

        LongProperty p0 = (LongProperty)properties.get(0);
        assertEquals(Event.LINE_NUMBER_PROPERTY_NAME, p0.getName());
        assertEquals(1L, p0.getValue());

        StringProperty p = (StringProperty)properties.get(1);
        assertEquals("a", p.getName());
        assertEquals("A", p.getValue());

        StringProperty p2 = (StringProperty)properties.get(2);
        assertEquals("b", p2.getName());
        assertEquals("B", p2.getValue());
    }

    @Test
    public void parse_FormatPresent_NonTimedEvent() throws Exception {

        CSVParser parser = new CSVParser("brand(string), count(int)");

        List<Event> result = parser.parse(5L, "Audi, 5");
        assertEquals(1, result.size());

        GenericEvent event = (GenericEvent)result.get(0);

        assertNotNull(event);

        List<Property> properties = event.getProperties();

        assertEquals(3, properties.size());

        LongProperty p0 = (LongProperty)properties.get(0);
        assertEquals(Event.LINE_NUMBER_PROPERTY_NAME, p0.getName());
        assertEquals(5L, p0.getValue());

        StringProperty p = (StringProperty)properties.get(1);
        assertEquals("brand", p.getName());
        assertEquals("Audi", p.getValue());

        IntegerProperty p2 = (IntegerProperty)properties.get(2);
        assertEquals("count", p2.getName());
        assertEquals(5, p2.getValue());
    }

    @Test
    public void parse_FormatPresent_TimedEvent_TimestampFirstInLine() throws Exception {

        String format = "timestamp(time:MMM-dd yyyy HH:mm:ss), brand(string), count(int)";

        CSVParser parser = new CSVParser(format);

        List<Event> result = parser.parse(1L, "Jan-01 2016 12:01:01, BMW, 7");
        assertEquals(1, result.size());

        TimedCSVLine event = (TimedCSVLine)result.get(0);
        assertNotNull(event);

        Long timestamp = event.getTime();
        assertEquals(timestamp.longValue(),
                new SimpleDateFormat("yy/MM/dd HH:mm:ss").parse("16/01/01 12:01:01").getTime());

        List<Property> properties = event.getProperties();

        assertEquals(4, properties.size());

        TimestampProperty p = (TimestampProperty)properties.get(0);
        assertEquals("01/01/16 12:01:01", Constants.DEFAULT_TIMESTAMP_FORMAT.format(p.getValue()));

        LongProperty p2 = (LongProperty)properties.get(1);
        assertEquals(Event.LINE_NUMBER_PROPERTY_NAME, p2.getName());
        assertEquals(1L, p2.getValue());

        StringProperty p3 = (StringProperty)properties.get(2);
        assertEquals("brand", p3.getName());
        assertEquals("BMW", p3.getValue());

        IntegerProperty p4 = (IntegerProperty)properties.get(3);
        assertEquals("count", p4.getName());
        assertEquals(7, p4.getValue());
    }

    @Test
    public void parse_FormatPresent_TimedEvent_TimestampNotFirstInLine() throws Exception {

        CSVParser parser = new CSVParser("brand(string), timestamp(time:MMM-dd yyyy HH:mm:ss), count(int)");

        List<Event> result = parser.parse(1L, "BMW, Jan-01 2016 12:01:01, 7");
        assertEquals(1, result.size());

        GenericTimedEvent event = (GenericTimedEvent)result.get(0);
        assertNotNull(event);

        Long timestamp = event.getTime();
        assertEquals(timestamp.longValue(),
                new SimpleDateFormat("yy/MM/dd HH:mm:ss").parse("16/01/01 12:01:01").getTime());

        List<Property> properties = event.getProperties();

        assertEquals(4, properties.size());

        TimestampProperty p = (TimestampProperty)properties.get(0);
        assertEquals("01/01/16 12:01:01", Constants.DEFAULT_TIMESTAMP_FORMAT.format(p.getValue()));

        LongProperty p2 = (LongProperty)properties.get(1);
        assertEquals(Event.LINE_NUMBER_PROPERTY_NAME, p2.getName());
        assertEquals(1L, p2.getValue());

        StringProperty p3 = (StringProperty)properties.get(2);
        assertEquals("brand", p3.getName());
        assertEquals("BMW", p3.getValue());

        IntegerProperty p4 = (IntegerProperty)properties.get(3);
        assertEquals("count", p4.getName());
        assertEquals(7, p4.getValue());
    }

    @Test
    public void parse_FormatPresent_FieldContainsComma() throws Exception {

        CSVParser parser = new CSVParser("a, b");

        String line = "something, \"something, else\"";

        List<Event> result = parser.parse(77L, line);
        assertEquals(1, result.size());

        GenericEvent e = (GenericEvent)result.get(0);

        List<Property> props = e.getProperties();

        assertEquals(3, props.size());

        LongProperty p = (LongProperty)props.get(0);
        assertEquals(77L, p.getLong().longValue());

        StringProperty p2 = (StringProperty)props.get(1);
        assertEquals("something", p2.getString());

        StringProperty p3 = (StringProperty)props.get(2);
        assertEquals("something, else", p3.getString());
    }

    @Test
    public void parse_FormatPresent_QuotedFields() throws Exception {

        CSVParser parser = new CSVParser("a, b");

        String line = "\"blah\", \"blah blah\"";

        List<Event> result = parser.parse(77L, line);
        assertEquals(1, result.size());

        GenericEvent e = (GenericEvent)result.get(0);

        List<Property> props = e.getProperties();

        assertEquals(3, props.size());

        LongProperty p = (LongProperty)props.get(0);
        assertEquals(77L, p.getLong().longValue());

        StringProperty p2 = (StringProperty)props.get(1);
        assertEquals("blah", p2.getString());

        StringProperty p3 = (StringProperty)props.get(2);
        assertEquals("blah blah", p3.getString());
    }

    @Test
    public void parse_NoFormatPresent_NullLine() throws Exception {

        CSVParser parser = new CSVParser();

        List<Event> events = parser.parse(1L, null);
        assertTrue(events.isEmpty());
    }

    @Test
    public void parse_NoFormatPresent_EmptyLine() throws Exception {

        CSVParser parser = new CSVParser();

        List<Event> events = parser.parse(1L, "");
        assertTrue(events.isEmpty());
    }

    @Test
    public void parse_NoFormatPresent_BlankLine() throws Exception {

        CSVParser parser = new CSVParser();

        List<Event> events = parser.parse(1L, "    ");
        assertTrue(events.isEmpty());
    }

    @Test
    public void parse_NoFormatPresent_MissingValues() throws Exception {

        CSVParser parser = new CSVParser();

        List<Event> events = parser.parse(7L, "  ,  ");

        assertEquals(1, events.size());

        NonTimedCSVLine line = (NonTimedCSVLine)events.get(0);

        List<Property> properties = line.getProperties();
        assertEquals(3, properties.size());

        LongProperty p = (LongProperty)properties.get(0);
        assertEquals(Event.LINE_NUMBER_PROPERTY_NAME, p.getName());
        assertEquals(7L, p.getLong().longValue());

        UndefinedTypeProperty p2 = (UndefinedTypeProperty)properties.get(1);
        assertEquals("field_0", p2.getName());
        assertNull(p2.getValue()); // this means "missing value"

        UndefinedTypeProperty p3 = (UndefinedTypeProperty)properties.get(2);
        assertEquals("field_1", p3.getName());
        assertNull(p3.getValue()); // this means "missing value"
    }

    @Test
    public void parse_NoFormatPresent_MissingValues2() throws Exception {

        CSVParser parser = new CSVParser();

        List<Event> events = parser.parse(7L, "a,,b");

        assertEquals(1, events.size());

        NonTimedCSVLine line = (NonTimedCSVLine)events.get(0);

        List<Property> properties = line.getProperties();
        assertEquals(4, properties.size());

        LongProperty p = (LongProperty)properties.get(0);
        assertEquals(Event.LINE_NUMBER_PROPERTY_NAME, p.getName());
        assertEquals(7L, p.getLong().longValue());

        StringProperty p2 = (StringProperty)properties.get(1);
        assertEquals("field_0", p2.getName());
        assertEquals("a", p2.getValue());

        UndefinedTypeProperty p3 = (UndefinedTypeProperty)properties.get(2);
        assertEquals("field_1", p3.getName());
        assertNull(p3.getValue()); // this means "missing value"

        StringProperty p4 = (StringProperty)properties.get(3);
        assertEquals("field_2", p4.getName());
        assertEquals("b", p4.getValue());
    }

    @Test
    public void parse_NoFormatPresent_MissingValues3() throws Exception {

        CSVParser parser = new CSVParser();

        List<Event> events = parser.parse(7L, "a,,b,");

        assertEquals(1, events.size());

        NonTimedCSVLine line = (NonTimedCSVLine)events.get(0);

        List<Property> properties = line.getProperties();
        assertEquals(5, properties.size());

        LongProperty p = (LongProperty)properties.get(0);
        assertEquals(Event.LINE_NUMBER_PROPERTY_NAME, p.getName());
        assertEquals(7L, p.getLong().longValue());

        StringProperty p2 = (StringProperty)properties.get(1);
        assertEquals("field_0", p2.getName());
        assertEquals("a", p2.getValue());

        UndefinedTypeProperty p3 = (UndefinedTypeProperty)properties.get(2);
        assertEquals("field_1", p3.getName());
        assertNull(p3.getValue()); // this means "missing value"

        StringProperty p4 = (StringProperty)properties.get(3);
        assertEquals("field_2", p4.getName());
        assertEquals("b", p4.getValue());

        UndefinedTypeProperty p5 = (UndefinedTypeProperty)properties.get(4);
        assertEquals("field_3", p5.getName());
        assertNull(p5.getValue()); // this means "missing value"
    }

    @Test
    public void parse_NoFormatPresent_BlankField() throws Exception {

        CSVParser parser = new CSVParser();

        List<Event> events = parser.parse(7L, "   \"   \"");

        assertEquals(1, events.size());

        NonTimedCSVLine line = (NonTimedCSVLine)events.get(0);

        List<Property> properties = line.getProperties();
        assertEquals(2, properties.size());

        LongProperty p = (LongProperty)properties.get(0);
        assertEquals(Event.LINE_NUMBER_PROPERTY_NAME, p.getName());
        assertEquals(7L, p.getLong().longValue());

        StringProperty p2 = (StringProperty)properties.get(1);
        assertEquals("field_0", p2.getName());
        assertEquals("   ", p2.getValue());
    }

    @Test
    public void parse_NoFormatPresent_DataLineNoHeader() throws Exception {

        String[] content = new String[] {

                "12/25/16 13:00:00, blue,  10, , ",
        };

        CSVParser parser = new CSVParser();

        assertNull(parser.getFormat());

        List<Event> events = new ArrayList<>();

        for(int i = 0; i < content.length; i ++) {

            List<Event> es = parser.parse(i + 1, content[i]);
            events.addAll(es);
        }

        assertEquals(1, events.size());

        TimedCSVLine e = (TimedCSVLine)events.get(0);

        assertNotNull(e);

        List<Property> properties = e.getProperties();
        assertEquals(6, properties.size());

        TimestampProperty p = (TimestampProperty)properties.get(0);
        assertEquals(TimedEvent.TIMESTAMP_PROPERTY_NAME, p.getName());
        //
        // original format does not survive storage of the property in the event
        //
        assertNull(p.getFormat());
        assertEquals("12/25/16 13:00:00", Constants.DEFAULT_TIMESTAMP_FORMAT.format(p.getValue()));

        LongProperty p2 = (LongProperty)properties.get(1);
        assertEquals(Event.LINE_NUMBER_PROPERTY_NAME, p2.getName());
        assertEquals(1L, p2.getLong().longValue());

        StringProperty p3 = (StringProperty)properties.get(2);
        assertEquals("field_1", p3.getName());
        assertEquals("blue", p3.getValue());

        IntegerProperty p4 = (IntegerProperty)properties.get(3);
        assertEquals("field_2", p4.getName());
        assertEquals(10, p4.getValue());

        UndefinedTypeProperty p5 = (UndefinedTypeProperty)properties.get(4);
        assertEquals("field_3", p5.getName());
        assertNull(p5.getValue());

        UndefinedTypeProperty p6 = (UndefinedTypeProperty)properties.get(5);
        assertEquals("field_4", p6.getName());
        assertNull(p6.getValue());
    }

    @Test
    public void parse_NoFormatPresent_HeaderLine_HeaderParsingFailure() throws Exception {

        CSVParser parser = new CSVParser();

        //
        // unbalanced parantheses, must throw exception
        //
        String headerLine = "# a(";

        try {

            parser.parse(7L, headerLine);
            fail("should have thrown exception");
        }
        catch(ParsingException e) {

            long lineNumber = e.getLineNumber();
            assertEquals(7L, lineNumber);

            CSVFormatException cause = (CSVFormatException)e.getCause();
            String msg = cause.getMessage();
            assertTrue(msg.contains("unbalanced parentheses"));
        }
    }

    @Test
    public void parse_NoFormatInitiallyPresent_HeaderLine() throws Exception {

        CSVParser parser = new CSVParser();

        //
        // no format yet
        //

        assertNull(parser.getFormat());

        String headerLine = "# timestamp, blue, green(int)";

        List<Event> events = parser.parse(7L, headerLine);

        assertEquals(1, events.size());

        CSVHeaders csvHeaders = (CSVHeaders)events.get(0);

        List<Property> properties = csvHeaders.getProperties();
        assertEquals(4, properties.size());

        LongProperty p = (LongProperty)properties.get(0);
        assertEquals(7L, p.getLong().longValue());

        StringProperty p2 = (StringProperty)properties.get(1);
        assertEquals(CSVHeaders.HEADER_NAME_PREFIX + 0, p2.getName());
        assertTrue(p2.getString().contains("timestamp"));

        StringProperty p3 = (StringProperty)properties.get(2);
        assertEquals(CSVHeaders.HEADER_NAME_PREFIX + 1, p3.getName());
        assertTrue(p3.getString().contains("blue"));
        assertTrue(p3.getString().contains("string"));

        StringProperty p4 = (StringProperty)properties.get(3);
        assertEquals(CSVHeaders.HEADER_NAME_PREFIX + 2, p4.getName());
        assertTrue(p4.getString().contains("green"));
        assertTrue(p4.getString().contains("int"));

        //
        // the header installed a format
        //

        CSVFormat format = parser.getFormat();
        assertNotNull(format);

        //
        // the CSV fields must match
        //

        List<CSVField> fields = format.getFields();
        assertEquals(3, fields.size());

        CSVField field = fields.get(0);
        assertTrue(field.isTimestamp());

        CSVField field2 = fields.get(1);
        assertFalse(field2.isTimestamp());
        assertEquals("blue", field2.getName());
        assertEquals(String.class, field2.getType());

        CSVField field3 = fields.get(2);
        assertFalse(field3.isTimestamp());
        assertEquals("green", field3.getName());
        assertEquals(Integer.class, field3.getType());
    }

    @Test
    public void parse_NoFormatInitiallyPresent_HeaderLineFollowedByDataLine() throws Exception {

        String[] content = new String[] {

                "# timestamp(MM/dd/yy HH:mm:ss),       color, size(int)",
                "12/25/16 13:00:00, blue,  10",
        };

        CSVParser parser = new CSVParser();

        List<Event> events = new ArrayList<>();

        for(int i = 0; i < content.length; i ++) {

            List<Event> es = parser.parse(i + 1, content[i]);
            events.addAll(es);
        }

        assertEquals(2, events.size());

        CSVHeaders e = (CSVHeaders)events.get(0);

        List<CSVField> fields = e.getFields();
        assertEquals(3, fields.size());
        assertTrue(fields.get(0).isTimestamp());
        assertEquals("color(string)", fields.get(1).getSpecification());
        assertEquals("size(int)", fields.get(2).getSpecification());

        TimedCSVLine e2 = (TimedCSVLine)events.get(1);

        assertNotNull(e2);

        Long time = e2.getTime();
        assertEquals(Constants.DEFAULT_TIMESTAMP_FORMAT.parse("12/25/16 13:00:00").getTime(), time.longValue());

        assertEquals("blue", e2.getStringProperty("color").getString());
        assertEquals(10, e2.getIntegerProperty("size").getInteger().intValue());
    }

    /**
     * The goal of this test is to make sure that a change in headers influences the data line parsing algoritm.
     * @throws Exception
     */
    @Test
    public void parse_NoFormatInitiallyPresent_HeaderLineFollowedByDataLineFollowedByAnotherHeaderFollowedByAnotherDataLine()
            throws Exception {

        String[] content = new String[] {

                "# timestamp(MM/dd/yy HH:mm:ss),       color, size(int)",
                "12/25/16 13:00:01, blue,  10",
                "",
                "# weight(float), index(int), something, timestamp(yy/MM/dd HH:mm:ss)",
                "11.11, 10, nine, 15/12/30 14:15:16, this will be discarded",
        };

        CSVParser parser = new CSVParser();

        List<Event> events = new ArrayList<>();

        for(int i = 0; i < content.length; i ++) {

            List<Event> es = parser.parse(i + 1, content[i]);
            events.addAll(es);
        }

        assertEquals(4, events.size());

        CSVHeaders e = (CSVHeaders)events.get(0);
        List<CSVField> fields = e.getFields();
        assertEquals(3, fields.size());
        assertTrue(fields.get(0).isTimestamp());
        assertEquals("color(string)", fields.get(1).getSpecification());
        assertEquals("size(int)", fields.get(2).getSpecification());

        TimedCSVLine e2 = (TimedCSVLine)events.get(1);
        assertEquals(4, e2.getProperties().size()); // includes line number
        Long time = e2.getTime();
        assertEquals(Constants.DEFAULT_TIMESTAMP_FORMAT.parse("12/25/16 13:00:01").getTime(), time.longValue());
        assertEquals("blue", e2.getStringProperty("color").getString());
        assertEquals(10, e2.getIntegerProperty("size").getInteger().intValue());
        assertEquals(2, e2.getLineNumber().longValue());

        CSVHeaders e3 = (CSVHeaders)events.get(2);
        List<CSVField> fields2 = e3.getFields();
        assertEquals(4, fields2.size());
        assertEquals("weight(float)", fields2.get(0).getSpecification());
        assertEquals("index(int)", fields2.get(1).getSpecification());
        assertEquals("something(string)", fields2.get(2).getSpecification());
        assertTrue(fields2.get(3).isTimestamp());

        TimedCSVLine e4 = (TimedCSVLine)events.get(3);
        assertEquals(5, e4.getProperties().size()); // includes line number
        assertEquals(11.11f, e4.getFloatProperty("weight").getFloat().floatValue(), 0.00001);
        assertEquals(10, e4.getIntegerProperty("index").getInteger().intValue());
        assertEquals("nine", e4.getStringProperty("something").getString());
        Long time2 = e4.getTime();
        assertEquals(Constants.DEFAULT_TIMESTAMP_FORMAT.parse("12/30/15 14:15:16").getTime(), time2.longValue());
        assertEquals(5, e4.getLineNumber().longValue());
    }

    // setFormat() -----------------------------------------------------------------------------------------------------

    @Test
    public void setFormat() throws Exception {

        CSVParser p = new CSVParser();

        assertNull(p.getFormat());

        CSVFormat format = new CSVFormat("a, timestamp, b");

        p.setFormat(format);

        assertEquals(format, p.getFormat());

        //
        // check the headers
        //

        List<CSVField> headers = p.getFormat().getFields();

        assertEquals(3, headers.size());

        CSVField f = headers.get(0);
        assertEquals("a", f.getName());
        assertEquals(String.class, f.getType());

        TimestampCSVField f2 = (TimestampCSVField)headers.get(1);
        assertEquals(TimedEvent.TIMESTAMP_PROPERTY_NAME, f2.getName());
        assertEquals(Long.class, f2.getType());

        CSVField f3 = headers.get(2);
        assertEquals("b", f3.getName());
        assertEquals(String.class, f3.getType());
    }

    @Test
    public void setFormat_Null() throws Exception {

        CSVParser p = new CSVParser();

        CSVFormat format = new CSVFormat("a, timestamp, b");
        p.setFormat(format);
        assertEquals(format, p.getFormat());

        //
        // nullify
        //

        p.setFormat(null);

        assertNull(p.getFormat());
        assertNull(p.getFormat());
    }

    // close() ---------------------------------------------------------------------------------------------------------

    @Test
    public void close() throws Exception {

        CSVParser parser = new CSVParser("a, b, c");
        List<Event> result = parser.close(1L);
        assertTrue(result.isEmpty());
    }

    // propertyListToCSVEvent() ----------------------------------------------------------------------------------------

    @Test
    public void propertyListToCSVEvent_NullList() throws Exception {

        try {

            CSVParser.propertyListToCSVEvent(new MutableBoolean(false), null);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("null property list"));
        }
    }

    @Test
    public void propertyListToCSVEvent_NoTimestamp_EmptyList() throws Exception {

        NonTimedCSVLine csvLine = (NonTimedCSVLine)CSVParser.
                propertyListToCSVEvent(new MutableBoolean(false), Collections.emptyList());
        assertNotNull(csvLine);
    }

    @Test
    public void propertyListToCSVEvent_Timestamp_EmptyList() throws Exception {

        try {

            CSVParser.propertyListToCSVEvent(new MutableBoolean(true), Collections.emptyList());
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("no timestamp property found"));
        }
    }

    @Test
    public void propertyListToCSVEvent_NoTimestamp() throws Exception {

        List<Property> properties = Arrays.asList(
                new LongProperty(Event.LINE_NUMBER_PROPERTY_NAME, 7L), new StringProperty("field_0", "something"));

        NonTimedCSVLine csvLine = (NonTimedCSVLine)CSVParser.
                propertyListToCSVEvent(new MutableBoolean(false), properties);

        assertNotNull(csvLine);

        List<Property> properties2 = csvLine.getProperties();
        assertEquals(2, properties2.size());

        LongProperty p = (LongProperty)properties2.get(0);
        assertEquals(Event.LINE_NUMBER_PROPERTY_NAME, p.getName());
        assertEquals(7L, p.getValue());

        StringProperty p2 = (StringProperty)properties2.get(1);
        assertEquals("field_0", p2.getName());
        assertEquals("something", p2.getValue());
    }

    // buildAndStoreProperty() -----------------------------------------------------------------------------------------

    @Test
    public void buildAndStoreProperty_ApparentlyTimestamp_NoCSVHeader() throws Exception {

        List<Property> properties = new ArrayList<>();

        int index = 7;

        PropertyFactory f = new PropertyFactory();

        CSVParser.buildAndStoreProperty(f, "12/25/16 13:00:00", index, null, new MutableBoolean(false), properties);

        //
        // since there is no format guidance, we rely on internal PropertyFactory heuristics, which will convert it
        // into a TimestampProperty.
        //

        assertEquals(1, properties.size());

        Property p = properties.get(0);

        TimestampProperty tp = (TimestampProperty)p;
        assertEquals(TimedEvent.TIMESTAMP_PROPERTY_NAME, tp.getName());
        assertEquals("12/25/16 13:00:00", tp.getFormat().format(tp.getValue()));
    }

    @Test
    public void buildAndStoreProperty_Integer_NoCSVHeader() throws Exception {

        List<Property> properties = new ArrayList<>();

        int index = 7;

        PropertyFactory f = new PropertyFactory();

        CSVParser.buildAndStoreProperty(f, "11", index, null, new MutableBoolean(false), properties);

        //
        // since there is no format guidance, we rely on internal PropertyFactory heuristics, which will convert it
        // into a IntegerProperty.
        //

        assertEquals(1, properties.size());

        Property p = properties.get(0);

        IntegerProperty ip = (IntegerProperty)p;
        Assert.assertEquals(CSVEvent.GENERIC_FIELD_NAME_PREFIX + index, ip.getName());
        assertEquals(11, ip.getInteger().intValue());
    }

    @Test
    public void buildAndStoreProperty_String_NoCSVHeader() throws Exception {

        List<Property> properties = new ArrayList<>();

        int index = 7;

        PropertyFactory f = new PropertyFactory();

        CSVParser.buildAndStoreProperty(f, "something", index, null, new MutableBoolean(false), properties);

        //
        // since there is no format guidance, we rely on internal PropertyFactory heuristics, which will convert it
        // into a StringProperty.
        //

        assertEquals(1, properties.size());

        Property p = properties.get(0);

        StringProperty sp = (StringProperty)p;
        assertEquals(CSVEvent.GENERIC_FIELD_NAME_PREFIX + index, sp.getName());
        assertEquals("something", sp.getValue());
    }

    @Test
    public void buildAndStoreProperty_NullMissingValue_NoHeader() throws Exception {

        List<Property> properties = new ArrayList<>();

        int index = 7;

        PropertyFactory f = new PropertyFactory();

        CSVParser.buildAndStoreProperty(f, null, index, null, new MutableBoolean(false), properties);

        assertEquals(1, properties.size());

        UndefinedTypeProperty p = (UndefinedTypeProperty)properties.get(0);
        assertEquals(CSVEvent.GENERIC_FIELD_NAME_PREFIX + index, p.getName());
        assertNull(p.getValue());
    }

    @Test
    public void buildAndStoreProperty_NullMissingValue_TypedHeader() throws Exception {

        List<Property> properties = new ArrayList<>();

        CSVField field = new CSVFieldImpl("something", String.class);
        int index = 7;

        PropertyFactory f = new PropertyFactory();

        CSVParser.buildAndStoreProperty(f, null, index, field, new MutableBoolean(false), properties);

        assertEquals(1, properties.size());

        StringProperty p = (StringProperty)properties.get(0);
        assertEquals("something", p.getName());
        assertNull(p.getValue());
    }

    @Test
    public void buildAndStoreProperty_TimestampLong() throws Exception {

        List<Property> properties = new ArrayList<>();

        PropertyFactory f = new PropertyFactory();

        int index = 7;

        CSVField header = new TimestampCSVField("timestamp", new UTCMillisecondsLongTimestampFormat());

        long value = 1503522092L;
        String token = Long.toString(value);

        MutableBoolean mb = new MutableBoolean(false);

        CSVParser.buildAndStoreProperty(f, token, index, header, mb, properties);

        assertEquals(1, properties.size());

        Property p = properties.get(0);

        TimestampProperty tp = (TimestampProperty)p;
        assertEquals(TimedEvent.TIMESTAMP_PROPERTY_NAME, tp.getName());

        assertEquals(value, tp.getValue());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
