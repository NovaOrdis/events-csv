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

import java.util.List;

import org.junit.Test;

import io.novaordis.events.api.event.PropertyFactory;
import io.novaordis.events.api.event.TimedEvent;
import io.novaordis.events.api.metric.MockAddress;
import io.novaordis.events.csv.event.field.CSVField;
import io.novaordis.events.csv.event.field.CSVFieldFactory;
import io.novaordis.events.csv.event.field.CSVFieldImpl;
import io.novaordis.events.csv.event.field.TimestampCSVField;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 2/6/16
 */
public class CSVFormatTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // constructors ----------------------------------------------------------------------------------------------------

    @Test
    public void constructor() throws Exception {

        CSVFormat csvFormat = new CSVFormat("field1, field2, field3");

        List<CSVField> fields = csvFormat.getFields();

        assertEquals(3, fields.size());

        CSVField f = fields.get(0);
        assertEquals(String.class, f.getType());
        assertEquals("field1", f.getName());

        CSVField f2 = fields.get(1);
        assertEquals(String.class, f2.getType());
        assertEquals("field2", f2.getName());

        CSVField f3 = fields.get(2);
        assertEquals(String.class, f3.getType());
        assertEquals("field3", f3.getName());
    }

    @Test
    public void constructor_TrailingCommaIgnoredForConvenience() throws Exception {

        CSVFormat csvFormat = new CSVFormat("field1, field2, field3,");

        List<CSVField> fields = csvFormat.getFields();

        assertEquals(3, fields.size());

        CSVField f = fields.get(0);
        assertEquals(String.class, f.getType());
        assertEquals("field1", f.getName());

        CSVField f2 = fields.get(1);
        assertEquals(String.class, f2.getType());
        assertEquals("field2", f2.getName());

        CSVField f3 = fields.get(2);
        assertEquals(String.class, f3.getType());
        assertEquals("field3", f3.getName());
    }

    @Test
    public void constructor2() throws Exception {

        CSVFormat csvFormat = new CSVFormat("field1");
        List<CSVField> fields = csvFormat.getFields();
        assertEquals(1, fields.size());
        CSVField f = fields.get(0);
        assertEquals(String.class, f.getType());
        assertEquals("field1", f.getName());
    }

    @Test
    public void constructor2_TrailingCommaIgnoredForConvenience() throws Exception {

        CSVFormat csvFormat = new CSVFormat("field1,  ");
        List<CSVField> fields = csvFormat.getFields();
        assertEquals(1, fields.size());
        CSVField f = fields.get(0);
        assertEquals(String.class, f.getType());
        assertEquals("field1", f.getName());
    }

    @Test
    public void constructor3() throws Exception {

        CSVFormat csvFormat = new CSVFormat("a, b, c");

        List<CSVField> fields = csvFormat.getFields();

        assertEquals(3, fields.size());

        CSVField f = fields.get(0);
        assertEquals(String.class, f.getType());
        assertEquals("a", f.getName());

        CSVField f2 = fields.get(1);
        assertEquals(String.class, f2.getType());
        assertEquals("b", f2.getName());

        CSVField f3 = fields.get(2);
        assertEquals(String.class, f3.getType());
        assertEquals("c", f3.getName());
    }

    @Test
    public void constructor_typed() throws Exception {

        CSVFormat csvFormat = new CSVFormat("timestamp(time:yy/MM/dd HH:mm:ss), count(int), duration(long), path");

        List<CSVField> fields = csvFormat.getFields();

        assertEquals(4, fields.size());

        CSVField f = fields.get(0);
        assertEquals(Long.class, f.getType());
        assertEquals("timestamp", f.getName());

        CSVField f2 = fields.get(1);
        assertEquals(Integer.class, f2.getType());
        assertEquals("count", f2.getName());

        CSVField f3 = fields.get(2);
        assertEquals(Long.class, f3.getType());
        assertEquals("duration", f3.getName());

        CSVField f4 = fields.get(3);
        assertEquals(String.class, f4.getType());
        assertEquals("path", f4.getName());
    }

    @Test
    public void constructor_InvalidFormat() throws Exception {

        try {

            new CSVFormat(",");
            fail("should have thrown exception");
        }
        catch(CSVFormatException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid CSV format specification"));
            assertTrue(msg.contains("field 0 null"));
        }
    }

    @Test
    public void constructor_InvalidFormat2() throws Exception {

        try {

            new CSVFormat("   ,");
            fail("should have thrown exception");
        }
        catch(CSVFormatException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid CSV format specification"));
            assertTrue(msg.contains("field 0 null"));
        }
    }

    @Test
    public void constructor_InvalidFormat3() throws Exception {

        try {

            new CSVFormat("  \t \t  ,");
            fail("should have thrown exception");
        }
        catch(CSVFormatException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid CSV format specification"));
            assertTrue(msg.contains("field 0 null"));
        }
    }

    @Test
    public void constructor_InvalidFormat4() throws Exception {

        try {

            new CSVFormat(", ,");
            fail("should have thrown exception");
        }
        catch(CSVFormatException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid CSV format specification"));
            assertTrue(msg.contains("field 0 null"));
        }
    }

    @Test
    public void constructor_invalidType() throws Exception {

        try {

            new CSVFormat("duration(mb)");
            fail("should have thrown exception");
        }
        catch(CSVFormatException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid field type specification"));
            assertTrue(msg.contains("mb"));
        }
    }

    @Test
    public void constructor_NoCommas() throws Exception {

        CSVFormat f = new CSVFormat("test");
        assertEquals(1, f.getFields().size());

        CSVField fd = f.getFields().get(0);
        assertEquals("test", fd.getName());
        assertEquals(String.class, fd.getType());
        assertEquals(null, fd.getFormat());
    }

    @Test
    public void constructor_Timestamp() throws Exception {

        CSVFormat format = new CSVFormat("timestamp");

        List<CSVField> fields = format.getFields();

        assertEquals(1, fields.size());

        CSVField f = fields.get(0);

        assertTrue(f.isTimestamp());
    }

    @Test
    public void constructor_FieldSpecificationQuoted() throws Exception {

        CSVFormat format = new CSVFormat("\"something\"");

        List<CSVField> fields = format.getFields();

        assertEquals(1, fields.size());

        CSVField f = fields.get(0);

        assertEquals("something", f.getName());
        assertEquals(String.class, f.getType());
        assertFalse(f.isTimestamp());
    }

    @Test
    public void constructor_FieldSpecificationWithCommasQuoted() throws Exception {

        CSVFormat format = new CSVFormat("\"something, something else\"");

        List<CSVField> fields = format.getFields();

        assertEquals(1, fields.size());

        CSVField f = fields.get(0);

        assertEquals("something, something else", f.getName());
        assertEquals(String.class, f.getType());
        assertFalse(f.isTimestamp());
    }

    // addField() ------------------------------------------------------------------------------------------------------

    @Test
    public void addField_Specification_NoType() throws Exception {

        CSVFormat f = new CSVFormat();

        assertTrue(f.getFields().isEmpty());

        f.addField("something");

        List<CSVField> fields = f.getFields();

        assertEquals(1, fields.size());

        CSVField fd = fields.get(0);

        assertEquals("something", fd.getName());

        Class c = fd.getType();

        assertEquals(String.class, c);
    }

    @Test
    public void addField_Specification_Type() throws Exception {

        CSVFormat f = new CSVFormat();

        assertTrue(f.getFields().isEmpty());

        f.addField("something (int)");

        List<CSVField> fields = f.getFields();

        assertEquals(1, fields.size());

        CSVField fd = fields.get(0);

        assertEquals("something", fd.getName());

        Class c = fd.getType();

        assertEquals(Integer.class, c);
    }

    @Test
    public void addField_CSVField() throws Exception {

        CSVFormat f = new CSVFormat();

        assertTrue(f.getFields().isEmpty());

        CSVField fd = CSVFieldFactory.fromSpecification("something (int)");

        f.addField(fd);

        List<CSVField> fields = f.getFields();

        assertEquals(1, fields.size());

        CSVField fd2 = fields.get(0);

        assertEquals("something", fd2.getName());

        Class c = fd2.getType();

        assertEquals(Integer.class, c);
    }

    @Test
    public void addField_MetricDefinition() throws Exception {

        PropertyFactory pf = new PropertyFactory();

        CSVFormat f = new CSVFormat();

        assertTrue(f.getFields().isEmpty());

        MockAddress ma = new MockAddress("mock://mock-host:1000");
        MockMetricDefinition mmd = new MockMetricDefinition(pf, ma, "mock-metric", Long.class);

        f.addField(mmd);

        List<CSVField> fields = f.getFields();

        assertEquals(1, fields.size());

        CSVField fd2 = fields.get(0);

        assertEquals("mock-metric", fd2.getName());

        Class c = fd2.getType();

        assertEquals(Long.class, c);
    }

    // addTimestampField() ---------------------------------------------------------------------------------------------

    @Test
    public void addTimestampField() throws Exception {

        CSVFormat f = new CSVFormat();

        assertTrue(f.getFields().isEmpty());

        f.addTimestampField();

        List<CSVField> fields = f.getFields();

        assertEquals(1, fields.size());

        CSVField fd = fields.get(0);

        assertEquals(TimedEvent.TIME_PROPERTY_NAME, fd.getName());

        Class c = fd.getType();

        assertEquals(Long.class, c);
    }

    // toPattern() -----------------------------------------------------------------------------------------------------

    @Test
    public void toPattern_Empty() throws Exception {

        CSVFormat f = new CSVFormat();

        String s = f.toPattern();
        assertTrue(s.isEmpty());
    }

    @Test
    public void toPattern_One() throws Exception {

        CSVFormat f = new CSVFormat();

        f.addField(new CSVFieldImpl("something", String.class));

        assertEquals("something(string)", f.toPattern());
    }

    @Test
    public void toPattern_Many() throws Exception {

        CSVFormat f = new CSVFormat();

        f.addField(new TimestampCSVField());
        f.addField(new CSVFieldImpl("something", String.class));
        f.addField(new CSVFieldImpl("counter", Integer.class));

        assertEquals("timestamp(time:MM/dd/yy HH:mm:ss), something(string), counter(int)", f.toPattern());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
