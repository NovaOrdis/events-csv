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

package io.novaordis.events.csv.event;

import io.novaordis.events.api.event.Event;
import io.novaordis.events.api.event.GenericEvent;
import io.novaordis.events.api.event.GenericTimedEvent;
import io.novaordis.events.api.event.IntegerProperty;
import io.novaordis.events.api.event.LongProperty;
import io.novaordis.events.api.event.Property;
import io.novaordis.events.api.event.StringProperty;
import io.novaordis.events.api.event.TimedEvent;
import io.novaordis.events.api.parser.ParsingException;
import io.novaordis.events.csv.CSVFormat;
import io.novaordis.events.csv.CSVFormatException;
import io.novaordis.events.csv.event.field.CSVField;
import io.novaordis.events.csv.event.field.TimestampCSVField;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    public void constructor3_EmptyField() throws Exception {

        CSVParser p = new CSVParser(",");

        CSVFormat format = p.getFormat();

        List<CSVField> fields = format.getFields();
        assertEquals(1, fields.size());
        assertEquals("CSVField01", fields.get(0).getName());
    }

    @Test
    public void constructor3_EmptyFields() throws Exception {

        CSVParser p = new CSVParser(", ,");

        CSVFormat format = p.getFormat();

        List<CSVField> fields = format.getFields();
        assertEquals(2, fields.size());
        assertEquals("CSVField01", fields.get(0).getName());
        assertEquals("CSVField02", fields.get(1).getName());
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

        List<CSVField> headers = p.getHeaders();
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

        assertEquals(-1, p.getTimestampFieldIndex());
    }

    @Test
    public void constructor_Headers_Timestamp() throws Exception {

        CSVParser p = new CSVParser("T(time:yyyy), b, c");

        List<CSVField> headers = p.getHeaders();
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

        assertEquals(0, p.getTimestampFieldIndex());
    }

    // parse() ---------------------------------------------------------------------------------------------------------

    @Test
    public void parse() throws Exception {

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
    public void parse_LineLongerThanFormat() throws Exception {

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
    public void parse_LineShorterThanFormat() throws Exception {

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
    public void parse_UntimedEvent() throws Exception {

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
    public void parse_TimedEvent_TimestampFirstInLine() throws Exception {

        CSVParser parser = new CSVParser("T(time:MMM-dd yyyy HH:mm:ss), brand(string), count(int)");

        List<Event> result = parser.parse(1L, "Jan-01 2016 12:01:01, BMW, 7");
        assertEquals(1, result.size());

        GenericTimedEvent event = (GenericTimedEvent)result.get(0);
        assertNotNull(event);

        Long timestamp = event.getTime();
        assertEquals(timestamp.longValue(),
                new SimpleDateFormat("yy/MM/dd HH:mm:ss").parse("16/01/01 12:01:01").getTime());

        List<Property> properties = event.getProperties();

        assertEquals(3, properties.size());

        LongProperty p0 = (LongProperty)properties.get(0);
        assertEquals(Event.LINE_NUMBER_PROPERTY_NAME, p0.getName());
        assertEquals(1L, p0.getValue());

        StringProperty p = (StringProperty)properties.get(1);
        assertEquals("brand", p.getName());
        assertEquals("BMW", p.getValue());

        IntegerProperty p2 = (IntegerProperty)properties.get(2);
        assertEquals("count", p2.getName());
        assertEquals(7, p2.getValue());
    }

    @Test
    public void parse_TimedEvent_TimestampNotFirstInLine() throws Exception {

        CSVParser parser = new CSVParser("brand(string), T(time:MMM-dd yyyy HH:mm:ss), count(int)");

        List<Event> result = parser.parse(1L, "BMW, Jan-01 2016 12:01:01, 7");
        assertEquals(1, result.size());

        GenericTimedEvent event = (GenericTimedEvent)result.get(0);
        assertNotNull(event);

        Long timestamp = event.getTime();
        assertEquals(timestamp.longValue(),
                new SimpleDateFormat("yy/MM/dd HH:mm:ss").parse("16/01/01 12:01:01").getTime());

        List<Property> properties = event.getProperties();

        assertEquals(3, properties.size());

        LongProperty p0 = (LongProperty)properties.get(0);
        assertEquals(Event.LINE_NUMBER_PROPERTY_NAME, p0.getName());
        assertEquals(1L, p0.getValue());

        StringProperty p = (StringProperty)properties.get(1);
        assertEquals("brand", p.getName());
        assertEquals("BMW", p.getValue());

        IntegerProperty p2 = (IntegerProperty)properties.get(2);
        assertEquals("count", p2.getName());
        assertEquals(7, p2.getValue());
    }

    @Test
    public void parse_FieldContainsComma() throws Exception {

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
    public void parse_QuotedFields() throws Exception {

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
    public void parse_NullLine() throws Exception {

        CSVParser parser = new CSVParser();

        List<Event> events = parser.parse(1L, null);
        assertTrue(events.isEmpty());
    }

    @Test
    public void parse_EmptyLine() throws Exception {

        CSVParser parser = new CSVParser();

        List<Event> events = parser.parse(1L, "");
        assertTrue(events.isEmpty());
    }

    @Test
    public void parse_BlankLine() throws Exception {

        CSVParser parser = new CSVParser();

        List<Event> events = parser.parse(1L, "    ");
        assertTrue(events.isEmpty());
    }

    @Test
    public void parse_HeaderLine_HeaderParsingFailure() throws Exception {

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
    public void parse_HeaderLine() throws Exception {

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
    public void parse_DifferenceBetweenHeaderAndComment() throws Exception {

        fail("return here");
    }

    @Test
    public void parse_HeaderLineFollowedByDataLine() throws Exception {

        String[] content = new String[] {

                "# timestamp,       color, size(int)",
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

        CSVLine e2 = (CSVLine)events.get(1);

        assertNotNull(e2);

        assertEquals("blue", e2.getStringProperty("color").getString());
        assertEquals(10, e2.getIntegerProperty("size").getInteger().intValue());
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

        List<CSVField> headers = p.getHeaders();

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
        assertNull(p.getHeaders());
        assertEquals(-1, p.getTimestampFieldIndex());
    }

    // close() ---------------------------------------------------------------------------------------------------------

    @Test
    public void close() throws Exception {

        CSVParser parser = new CSVParser("a, b, c");
        List<Event> result = parser.close(1L);
        assertTrue(result.isEmpty());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
