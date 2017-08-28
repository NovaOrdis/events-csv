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

import io.novaordis.events.csv.CSVFormatException;
import org.junit.Test;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/26/17
 */
public class CSVFieldFactoryTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // fromSpecification() ----------------------------------------------------------------------------------------

    @Test
    public void fromSpecification_Null() throws Exception {

        try {

            CSVFieldFactory.fromSpecification(null);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertEquals("null field specification", msg);
        }
    }

    @Test
    public void fromSpecification_UnbalancedParantheses() throws Exception {

        try {

            CSVFieldFactory.fromSpecification("something something else) and more");
            fail("should have thrown exception");
        }
        catch(CSVFormatException e) {

            String msg = e.getMessage();
            assertEquals("unbalanced parentheses", msg);
        }
    }

    @Test
    public void fromSpecification_UnbalancedParantheses2() throws Exception {

        try {

            CSVFieldFactory.fromSpecification("a)");
            fail("should throw exception");
        }
        catch(CSVFormatException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("unbalanced"));
        }
    }

    @Test
    public void fromSpecification_UnbalancedParantheses3() throws Exception {

        try {

            CSVFieldFactory.fromSpecification("a(");
            fail("should throw exception");
        }
        catch(CSVFormatException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("unbalanced parentheses"));
        }
    }

    @Test
    public void fromSpecification_NoTypeInformation() throws Exception {

        String specification = "something";

        CSVField f = CSVFieldFactory.fromSpecification(specification);

        assertEquals("something", f.getName());
        assertEquals(String.class, f.getType());
        assertFalse(f.isTimestamp());

        String specification2 = f.getSpecification();

        //
        // adds canonical type info
        //
        assertEquals(specification + "(string)", specification2);
    }

    @Test
    public void fromSpecification_NoTypeInformation_Timestamp() throws Exception {

        String specification = "timestamp";

        CSVField f = CSVFieldFactory.fromSpecification(specification);

        assertEquals("timestamp", f.getName());
        assertEquals(Long.class, f.getType());
        assertTrue(f.isTimestamp());

        String specification2 = f.getSpecification();

        //
        // adds canonical type info
        //
        assertEquals(specification + "(time:MM/dd/yy HH:mm:ss)", specification2);
    }

    @Test
    public void fromSpecification_InvalidTypeSpecification() throws Exception {

        try {

            CSVFieldFactory.fromSpecification("something(blah)");
            fail("should have thrown exception");
        }
        catch(CSVFormatException e) {

            String msg = e.getMessage();
            assertTrue(msg.startsWith("invalid field type specification"));
            assertTrue(msg.contains("blah"));
        }
    }

    @Test
    public void fromSpecification_TypeInformation_String() throws Exception {

        String specification = "something(string)";

        CSVField f = CSVFieldFactory.fromSpecification(specification);

        assertEquals("something", f.getName());
        assertEquals(String.class, f.getType());
        assertFalse(f.isTimestamp());

        String specification2 = f.getSpecification();
        assertEquals(specification, specification2);
    }

    @Test
    public void fromSpecification_TypeInformation_Integer() throws Exception {

        String specification = "something(int)";

        CSVField f = CSVFieldFactory.fromSpecification(specification);

        assertEquals("something", f.getName());
        assertEquals(Integer.class, f.getType());
        assertFalse(f.isTimestamp());

        String specification2 = f.getSpecification();
        assertEquals(specification, specification2);
    }

    @Test
    public void fromSpecification_TypeInformation_Long() throws Exception {

        String specification = "something(long)";

        CSVField f = CSVFieldFactory.fromSpecification(specification);

        assertEquals("something", f.getName());
        assertEquals(Long.class, f.getType());
        assertFalse(f.isTimestamp());

        String specification2 = f.getSpecification();
        assertEquals(specification, specification2);
    }

    @Test
    public void fromSpecification_TypeInformation_Float() throws Exception {

        String specification = "something(float)";

        CSVField f = CSVFieldFactory.fromSpecification(specification);

        assertEquals("something", f.getName());
        assertEquals(Float.class, f.getType());
        assertFalse(f.isTimestamp());

        String specification2 = f.getSpecification();
        assertEquals(specification, specification2);
    }

    @Test
    public void fromSpecification_TypeInformation_Double() throws Exception {

        String specification = "something(double)";

        CSVField f = CSVFieldFactory.fromSpecification(specification);

        assertEquals("something", f.getName());
        assertEquals(Double.class, f.getType());
        assertFalse(f.isTimestamp());

        String specification2 = f.getSpecification();
        assertEquals(specification, specification2);
    }

    @Test
    public void fromSpecification_TypeInformation_Time_MissingFormat() throws Exception {

        String specification = "something(time)";

        try {

            CSVFieldFactory.fromSpecification(specification);
        }
        catch(CSVFormatException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid time specification: missing format"));
        }
    }

    @Test
    public void fromSpecification_TypeInformation_Time() throws Exception {

        String specification = "something(HH:mm:ss)";

        CSVField f = CSVFieldFactory.fromSpecification(specification);

        assertEquals("something", f.getName());
        assertEquals(Date.class, f.getType());
        assertFalse(f.isTimestamp());

        assertEquals("HH:mm:ss", ((SimpleDateFormat)f.getFormat()).toPattern());

        String specification2 = f.getSpecification();
        assertEquals("something(time:HH:mm:ss)", specification2);
    }


    @Test
    public void fromSpecification_StringField() throws Exception {

        String specification = "some-string(string)";

        CSVField f = CSVFieldFactory.fromSpecification(specification);

        assertEquals("some-string", f.getName());
        assertEquals(String.class, f.getType());

        String specification2 = f.getSpecification();
        assertEquals(specification, specification2);
    }

    @Test
    public void fromSpecification_SimpleString() throws Exception {

        String specification = "some-string";

        CSVField f = CSVFieldFactory.fromSpecification(specification);

        assertEquals("some-string", f.getName());
        assertEquals(String.class, f.getType());
        assertFalse(f.isTimestamp());

        String specification2 = f.getSpecification();
        assertEquals(specification + "(string)", specification2);
    }

    @Test
    public void fromSpecification_Time_Timestamp() throws Exception {

        String specification = "timestamp(time:yy/MM/dd HH:mm:ss)";

        CSVField f = CSVFieldFactory.fromSpecification(specification);

        assertTrue(f.isTimestamp());

        assertEquals("timestamp", f.getName());
        assertEquals(Long.class, f.getType());

        Format format = f.getFormat();
        assertTrue(format instanceof SimpleDateFormat);
        SimpleDateFormat sdf = (SimpleDateFormat)format;

        assertEquals(sdf.parse("16/01/01 01:01:01"),
                new SimpleDateFormat("MM/dd/yy hh:mm:ss a").parse("01/01/16 01:01:01 AM"));

        String specification2 = f.getSpecification();
        assertEquals(specification, specification2);
    }

    @Test
    public void fromSpecification_Time_Timestamp2() throws Exception {

        String specification = "timestamp(yy/MM/dd HH:mm:ss)";

        CSVField f = CSVFieldFactory.fromSpecification(specification);

        assertTrue(f.isTimestamp());

        assertEquals("timestamp", f.getName());
        assertEquals(Long.class, f.getType());

        Format format = f.getFormat();
        assertTrue(format instanceof SimpleDateFormat);
        SimpleDateFormat sdf = (SimpleDateFormat)format;

        assertEquals(sdf.parse("16/01/01 01:01:01"),
                new SimpleDateFormat("MM/dd/yy hh:mm:ss a").parse("01/01/16 01:01:01 AM"));

        assertEquals("yy/MM/dd HH:mm:ss", sdf.toPattern());

        String specification2 = f.getSpecification();
        assertEquals("timestamp(time:yy/MM/dd HH:mm:ss)", specification2);
    }


    @Test
    public void fromSpecification_Time_NotTimestamp() throws Exception {

        String specification = "T(time:MMM-dd yyyy HH:mm:ss)";

        CSVField f = CSVFieldFactory.fromSpecification(specification);

        //
        // because the field name is not 'timestamp', the field won't be a TimestampCSVField, but just a regular
        // CSVFieldImpl
        //

        assertFalse(f.isTimestamp());

        assertEquals("T", f.getName());
        assertEquals(Date.class, f.getType());

        Format format = f.getFormat();
        assertTrue(format instanceof SimpleDateFormat);
        SimpleDateFormat sdf = (SimpleDateFormat)format;

        assertEquals(sdf.parse("Jun-01 2016 01:01:01"),
                new SimpleDateFormat("MM/dd/yy hh:mm:ss a").parse("06/01/16 01:01:01 AM"));

        String specification2 = f.getSpecification();
        assertEquals(specification, specification2);
    }

    @Test
    public void fromSpecification_Time_InvalidTimeFormatSpecification() throws Exception {

        try {

            CSVFieldFactory.fromSpecification("timestamp(time:blah)");
        }
        catch(CSVFormatException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid time specification"));
            assertTrue(msg.contains("blah"));
        }
    }

    @Test
    public void fromSpecification_Integer() throws Exception {

        String specification = "a(int)";

        CSVField f = CSVFieldFactory.fromSpecification(specification);

        assertEquals("a", f.getName());
        assertEquals(Integer.class, f.getType());

        String specification2 = f.getSpecification();
        assertEquals(specification, specification2);
    }

    @Test
    public void fromSpecification_Integer_Space() throws Exception {

        String specification = "something (int)";

        CSVField f = CSVFieldFactory.fromSpecification(specification);

        assertEquals("something", f.getName());
        assertEquals(Integer.class, f.getType());

        String specification2 = f.getSpecification();
        assertEquals("something(int)", specification2);
    }

    @Test
    public void fromSpecification_Long() throws Exception {

        String specification = "a(long)";

        CSVField f = CSVFieldFactory.fromSpecification(specification);

        assertEquals("a", f.getName());
        assertEquals(Long.class, f.getType());

        String specification2 = f.getSpecification();
        assertEquals(specification, specification2);
    }

    @Test
    public void fromSpecification_Float() throws Exception {

        String specification = "a(float)";

        CSVField f = CSVFieldFactory.fromSpecification(specification);

        assertEquals("a", f.getName());
        assertEquals(Float.class, f.getType());

        String specification2 = f.getSpecification();
        assertEquals(specification, specification2);
    }

    @Test
    public void fromSpecification_Double() throws Exception {

        String specification = "a(double)";


        CSVField f = CSVFieldFactory.fromSpecification(specification);

        assertEquals("a", f.getName());
        assertEquals(Double.class, f.getType());

        String specification2 = f.getSpecification();
        assertEquals(specification, specification2);
    }

    @Test
    public void fromSpecification_InvalidType() throws Exception {

        try {

            CSVFieldFactory.fromSpecification("fieldA(MB)");
            fail("should throw exception");
        }
        catch(CSVFormatException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid field type specification \"MB\""));
        }
    }

    // parseTimeSpecification() ----------------------------------------------------------------------------------------

    @Test
    public void parseTimeSpecification_NoRecognizableSimpleDateFormat() throws Exception {

        Format f = CSVFieldFactory.parseTimeSpecification("something");
        assertNull(f);
    }

    @Test
    public void parseTimeSpecification_NoRecognizableSimpleDateFormat2() throws Exception {

        Format format = CSVFieldFactory.parseTimeSpecification("something that won't trigger anything");
        assertNull(format);
    }

    @Test
    public void parseTimeSpecification_TimeLabelButNoFormat() throws Exception {

        try {

            CSVFieldFactory.parseTimeSpecification("time");
            fail("should have thrown exception");
        }
        catch(CSVFormatException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid time specification"));
            assertTrue(msg.contains("missing format"));
        }
    }

    @Test
    public void parseTimeSpecification_TimeLabelButNoFormat2() throws Exception {

        try {

            CSVFieldFactory.parseTimeSpecification("time:");
            fail("should have thrown exception");
        }
        catch(CSVFormatException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid time specification"));
            assertTrue(msg.contains("missing format"));
        }
    }

    @Test
    public void parseTimeSpecification_Invalid2() throws Exception {

        try {

            CSVFieldFactory.parseTimeSpecification("timeblahblah");
            fail("should have thrown exception");
        }
        catch(CSVFormatException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid time specification"));
            assertTrue(msg.contains("missing ':'"));
        }
    }

    @Test
    public void parseTimeSpecification_Invalid3() throws Exception {

        try {

            CSVFieldFactory.parseTimeSpecification("time:blahblah");
            fail("should have thrown exception");
        }
        catch(CSVFormatException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid time specification"));
            assertTrue(msg.contains("blahblah"));
        }
    }

    @Test
    public void parseTimeSpecification() throws Exception {

        Format format = CSVFieldFactory.parseTimeSpecification("time:MM/dd/YY HH:mm:ss");

        SimpleDateFormat sdf = (SimpleDateFormat)format;

        assertNotNull(sdf);

        assertEquals("MM/dd/YY HH:mm:ss", sdf.toPattern());
    }



    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
