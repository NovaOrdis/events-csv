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

import io.novaordis.events.api.event.TimedEvent;
import io.novaordis.events.api.metric.MockAddress;
import io.novaordis.events.csv.event.field.CSVField;
import io.novaordis.events.csv.event.field.CSVFieldFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 2/6/16
 */
public class CSVFormatTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(CSVFormatTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // constructors ----------------------------------------------------------------------------------------------------

    @Test
    public void constructor() throws Exception {

        CSVFormat csvFormat = new CSVFormat(",");
        List<CSVField> fields = csvFormat.getFields();
        assertEquals(1, fields.size());
        CSVField f = fields.get(0);
        assertNotNull(f);
        assertEquals(String.class, f.getType());
        assertEquals("CSVField01", f.getName());

        log.debug("constructor");
    }

    @Test
    public void constructor2() throws Exception {

        CSVFormat csvFormat = new CSVFormat("   ,");
        List<CSVField> fields = csvFormat.getFields();
        assertEquals(1, fields.size());
        CSVField f = fields.get(0);
        assertNotNull(f);
        assertEquals(String.class, f.getType());
        assertEquals("CSVField01", f.getName());
    }

    @Test
    public void constructor3() throws Exception {

        CSVFormat csvFormat = new CSVFormat("  \t \t  ,");
        List<CSVField> fields = csvFormat.getFields();
        assertEquals(1, fields.size());
        CSVField f = fields.get(0);
        assertNotNull(f);
        assertEquals(String.class, f.getType());
        assertEquals("CSVField01", f.getName());
    }

    @Test
    public void constructor4() throws Exception {

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
    public void constructor5() throws Exception {

        CSVFormat csvFormat = new CSVFormat("field1,  ");
        List<CSVField> fields = csvFormat.getFields();
        assertEquals(1, fields.size());
        CSVField f = fields.get(0);
        assertEquals(String.class, f.getType());
        assertEquals("field1", f.getName());
    }

    @Test
    public void constructor6() throws Exception {

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
    public void constructor7() throws Exception {

        CSVFormat csvFormat = new CSVFormat(", ,");

        List<CSVField> fields = csvFormat.getFields();

        assertEquals(2, fields.size());

        CSVField f = fields.get(0);
        assertEquals(String.class, f.getType());
        assertEquals("CSVField01", f.getName());

        CSVField f2 = fields.get(1);
        assertEquals(String.class, f2.getType());
        assertEquals("CSVField02", f2.getName());
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

        CSVFormat f = new CSVFormat();

        assertTrue(f.getFields().isEmpty());

        MockAddress ma = new MockAddress("mock://mock-host:1000");
        MockMetricDefinition mmd = new MockMetricDefinition(ma, "mock-metric", Long.class);

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

        assertEquals(TimedEvent.TIMESTAMP_PROPERTY_NAME, fd.getName());

        Class c = fd.getType();

        assertEquals(Long.class, c);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
