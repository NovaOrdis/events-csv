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

package io.novaordis.events.csv.event;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import io.novaordis.events.api.event.Event;
import io.novaordis.events.api.event.GenericEvent;
import io.novaordis.events.api.event.LongProperty;
import io.novaordis.events.api.event.Property;
import io.novaordis.events.api.event.StringProperty;
import io.novaordis.events.api.event.TimedEvent;
import io.novaordis.events.csv.CSVFormat;
import io.novaordis.events.csv.CSVFormatException;
import io.novaordis.events.csv.Constants;
import io.novaordis.events.csv.event.field.CSVField;
import io.novaordis.events.csv.event.field.CSVFieldImpl;
import io.novaordis.events.csv.event.field.TimestampCSVField;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/8/17
 */
public class CSVHeadersTest extends CSVEventTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // constructor -----------------------------------------------------------------------------------------------------

    @Test
    public void constructor() throws Exception {

        CSVField field = new TimestampCSVField();
        CSVField field2 = new CSVFieldImpl("some string", String.class, null);
        CSVField field3 = new CSVFieldImpl("some int", Integer.class, null);
        CSVField field4 = new CSVFieldImpl("some long", Long.class, null);
        CSVField field5 = new CSVFieldImpl("some float", Float.class, null);

        CSVHeaders headers = new CSVHeaders(7L, Arrays.asList(field, field2, field3, field4, field5));

        List<Property> properties = headers.getProperties();
        assertEquals(6, properties.size());

        LongProperty p = (LongProperty)properties.get(0);
        assertEquals(GenericEvent.LINE_PROPERTY_NAME, p.getName());
        assertEquals(7L, p.getLong().longValue());

        StringProperty p2 = (StringProperty)properties.get(1);
        assertEquals(CSVHeaders.HEADER_NAME_PREFIX + "0", p2.getName());
        assertEquals(TimedEvent.TIME_PROPERTY_NAME + "(time:" + Constants.DEFAULT_TIMESTAMP_FORMAT_LITERAL + ")",
                p2.getString());

        StringProperty p3 = (StringProperty)properties.get(2);
        assertEquals(CSVHeaders.HEADER_NAME_PREFIX + "1", p3.getName());
        assertEquals("some string(string)", p3.getString());

        StringProperty p4 = (StringProperty)properties.get(3);
        assertEquals(CSVHeaders.HEADER_NAME_PREFIX + "2", p4.getName());
        assertEquals("some int(int)", p4.getString());

        StringProperty p5 = (StringProperty)properties.get(4);
        assertEquals(CSVHeaders.HEADER_NAME_PREFIX + "3", p5.getName());
        assertEquals("some long(long)", p5.getString());

        StringProperty p6 = (StringProperty)properties.get(5);
        assertEquals(CSVHeaders.HEADER_NAME_PREFIX + "4", p6.getName());
        assertEquals("some float(float)", p6.getString());
    }

    @Test
    public void constructor2() throws Exception {

        CSVFormat f = new CSVFormat("time, A, B");
        List<CSVField> fields = f.getFields();
        CSVHeaders h = new CSVHeaders(7L, fields);

        List<Property> properties = h.getProperties();

        assertEquals(4, properties.size());

        LongProperty p = (LongProperty)properties.get(0);
        assertEquals(Event.LINE_PROPERTY_NAME, p.getName());
        assertEquals(7L, p.getLong().longValue());

        StringProperty p2 = (StringProperty)properties.get(1);
        assertEquals(CSVHeaders.HEADER_NAME_PREFIX + "0", p2.getName());
        assertEquals(
                TimedEvent.TIME_PROPERTY_NAME + "(time:" + Constants.getDefaultTimestampFormat().toPattern() + ")",
                p2.getString());

        StringProperty p3 = (StringProperty)properties.get(2);
        assertEquals(CSVHeaders.HEADER_NAME_PREFIX + "1", p3.getName());
        assertEquals("A(string)", p3.getString());

        StringProperty p4 = (StringProperty)properties.get(3);
        assertEquals(CSVHeaders.HEADER_NAME_PREFIX + "2", p4.getName());
        assertEquals("B(string)", p4.getString());
    }

    @Test
    public void constructor3() throws Exception {

        //
        // convenience trailing missing field acceptable
        //
        CSVFormat f = new CSVFormat("time, A, ");
        List<CSVField> fields = f.getFields();
        CSVHeaders h = new CSVHeaders(7L, fields);

        List<Property> properties = h.getProperties();

        assertEquals(3, properties.size());

        LongProperty p = (LongProperty)properties.get(0);
        assertEquals(Event.LINE_PROPERTY_NAME, p.getName());
        assertEquals(7L, p.getLong().longValue());

        StringProperty p2 = (StringProperty)properties.get(1);
        assertEquals(CSVHeaders.HEADER_NAME_PREFIX + "0", p2.getName());
        assertEquals(
                TimedEvent.TIME_PROPERTY_NAME + "(time:" + Constants.getDefaultTimestampFormat().toPattern() + ")",
                p2.getString());

        StringProperty p3 = (StringProperty)properties.get(2);
        assertEquals(CSVHeaders.HEADER_NAME_PREFIX + "1", p3.getName());
        assertEquals("A(string)", p3.getString());
    }

    @Test
    public void constructor_NoTimestamp() throws Exception {

        CSVFormat f = new CSVFormat("A, B, C");
        List<CSVField> fields = f.getFields();
        CSVHeaders h = new CSVHeaders(7L, fields);

        List<Property> properties = h.getProperties();

        assertEquals(4, properties.size());

        LongProperty p = (LongProperty)properties.get(0);
        assertEquals(Event.LINE_PROPERTY_NAME, p.getName());
        assertEquals(7L, p.getLong().longValue());

        StringProperty p2 = (StringProperty)properties.get(1);
        assertEquals(CSVHeaders.HEADER_NAME_PREFIX + "0", p2.getName());
        assertEquals("A(string)", p2.getString());

        StringProperty p3 = (StringProperty)properties.get(2);
        assertEquals(CSVHeaders.HEADER_NAME_PREFIX + "1", p3.getName());
        assertEquals("B(string)", p3.getString());

        StringProperty p4 = (StringProperty)properties.get(3);
        assertEquals(CSVHeaders.HEADER_NAME_PREFIX + "2", p4.getName());
        assertEquals("C(string)", p4.getString());
    }

    @Test
    public void constructor_headerGaps() throws Exception {

        CSVFormat f = new CSVFormat("timestamp, B");
        List<CSVField> fields = f.getFields();

        //
        // insert a null in the middle
        //
        fields.add(1, null);

        try {


            new CSVHeaders(7L, fields);
            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("null CSV field"));
            assertTrue(msg.contains("position 1"));
        }
    }

    // getFields() -----------------------------------------------------------------------------------------------------

    @Test
    public void getFields() throws Exception {

        CSVField field = new TimestampCSVField();
        CSVField field2 = new CSVFieldImpl("some string", String.class, null);
        CSVField field3 = new CSVFieldImpl("some int", Integer.class, null);
        CSVField field4 = new CSVFieldImpl("some long", Long.class, null);
        CSVField field5 = new CSVFieldImpl("some float", Float.class, null);

        CSVHeaders headers = new CSVHeaders(7L, Arrays.asList(field, field2, field3, field4, field5));

        List<CSVField> csvFields = headers.getFields();

        assertEquals(5, csvFields.size());

        CSVField field6 = csvFields.get(0);
        assertEquals(TimedEvent.TIME_PROPERTY_NAME, field6.getName());
        assertEquals(Long.class, field6.getType());
        assertEquals(Constants.getDefaultTimestampFormat(), field6.getFormat());
        assertTrue(field6.isTimestamp());

        CSVField field7 = csvFields.get(1);
        assertEquals("some string", field7.getName());
        assertEquals(String.class, field7.getType());
        assertNull(field7.getFormat());
        assertFalse(field7.isTimestamp());

        CSVField field8 = csvFields.get(2);
        assertEquals("some int", field8.getName());
        assertEquals(Integer.class, field8.getType());
        assertNull(field8.getFormat());
        assertFalse(field8.isTimestamp());

        CSVField field9 = csvFields.get(3);
        assertEquals("some long", field9.getName());
        assertEquals(Long.class, field9.getType());
        assertNull(field9.getFormat());
        assertFalse(field9.isTimestamp());

        CSVField field10 = csvFields.get(4);
        assertEquals("some float", field10.getName());
        assertEquals(Float.class, field10.getType());
        assertNull(field10.getFormat());
        assertFalse(field10.isTimestamp());
    }

    @Test
    public void getFields_HeadersMixedWithOtherPropertiesButInSequence() throws Exception {

        CSVHeaders headers = new CSVHeaders();

        headers.setLongProperty("some-random-property-1", 1L);
        headers.setStringProperty(CSVHeaders.HEADER_NAME_PREFIX + 0, "time");
        headers.setLongProperty("some-random-property-2", 2L);
        headers.setStringProperty(CSVHeaders.HEADER_NAME_PREFIX + 1, "some string");
        headers.setLongProperty("some-random-property-3", 3L);
        headers.setLongProperty("some-random-property-4", 4L);
        headers.setStringProperty(CSVHeaders.HEADER_NAME_PREFIX + 2, "some int(int)");
        headers.setLongProperty("some-random-property-5", 5L);
        headers.setStringProperty(CSVHeaders.HEADER_NAME_PREFIX + 3, "some long(long)");
        headers.setLongProperty("some-random-property-6", 6L);
        headers.setStringProperty(CSVHeaders.HEADER_NAME_PREFIX + 4, "some float(float)");
        headers.setLongProperty("some-random-property-7", 7L);

        List<CSVField> csvFields = headers.getFields();

        assertEquals(5, csvFields.size());

        CSVField field6 = csvFields.get(0);
        assertEquals(TimedEvent.TIME_PROPERTY_NAME, field6.getName());
        assertTrue(field6.isTimestamp());

        CSVField field7 = csvFields.get(1);
        assertEquals("some string", field7.getName());

        CSVField field8 = csvFields.get(2);
        assertEquals("some int", field8.getName());

        CSVField field9 = csvFields.get(3);
        assertEquals("some long", field9.getName());

        CSVField field10 = csvFields.get(4);
        assertEquals("some float", field10.getName());
    }

    @Test
    public void getFields_HeadersOutOfSequence() throws Exception {

        CSVHeaders headers = new CSVHeaders();
        headers.setStringProperty(CSVHeaders.HEADER_NAME_PREFIX + 7, "does not matter");

        try {

            headers.getFields();
            fail("should have thrown exception");
        }
        catch(IllegalStateException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("CSV header out of sequence"));
            assertTrue(msg.contains(CSVHeaders.HEADER_NAME_PREFIX + 7));
        }
    }

    @Test
    public void getFields_HeadersOutOfSequence2() throws Exception {

        CSVHeaders headers = new CSVHeaders();

        headers.setStringProperty(CSVHeaders.HEADER_NAME_PREFIX + 0, "timestamp");
        headers.setStringProperty(CSVHeaders.HEADER_NAME_PREFIX + 1, "some string");
        headers.setStringProperty(CSVHeaders.HEADER_NAME_PREFIX + 3, "some string");

        try {

            headers.getFields();
            fail("should have thrown exception");
        }
        catch(IllegalStateException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("CSV header out of sequence"));
            assertTrue(msg.contains(CSVHeaders.HEADER_NAME_PREFIX + 3));
        }
    }

    @Test
    public void getFields_InvalidHeaderName() throws Exception {

        CSVHeaders headers = new CSVHeaders();

        headers.setStringProperty(CSVHeaders.HEADER_NAME_PREFIX + "blah", "timestamp");

        try {

            headers.getFields();
            fail("should have thrown exception");
        }
        catch(IllegalStateException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid header property name"));
            assertTrue(msg.contains(CSVHeaders.HEADER_NAME_PREFIX + "blah"));
        }
    }

    @Test
    public void getFields_InvalidHeaderSpecification() throws Exception {

        CSVHeaders headers = new CSVHeaders();

        headers.setStringProperty(CSVHeaders.HEADER_NAME_PREFIX + "0", "something(no-such-type)");

        try {

            headers.getFields();
            fail("should have thrown exception");
        }
        catch(IllegalStateException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid CSV header specification"));
            assertTrue(msg.contains("something(no-such-type)"));

            CSVFormatException cause = (CSVFormatException)e.getCause();
            assertNotNull(cause);
        }
    }

    // getNextTimedEventTimestamp() ------------------------------------------------------------------------------------

    @Test
    public void getNextTimedEventTimestamp() throws Exception {

        CSVHeaders h = getCSVEventToTest();

        assertNull(h.getNextTimedEventTimestamp());
    }

    @Test
    public void setNextTimedEventTimestamp() throws Exception {

        CSVHeaders h = getCSVEventToTest();

        h.setNextTimedEventTimestamp(7L);

        Long l = h.getNextTimedEventTimestamp();

        assertNotNull(l);

        assertEquals(7L, l.longValue());

        h.setNextTimedEventTimestamp(null);

        Long l2 = h.getNextTimedEventTimestamp();

        assertNull(l2);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected CSVHeaders getCSVEventToTest() throws Exception {

        return new CSVHeaders();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
