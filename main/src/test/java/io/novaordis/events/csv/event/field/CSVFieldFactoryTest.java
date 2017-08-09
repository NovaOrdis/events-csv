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

    // fromFieldSpecification() ----------------------------------------------------------------------------------------

    @Test
    public void fromFieldSpecification_Null() throws Exception {

        try {

            CSVFieldFactory.fromFieldSpecification(null);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertEquals("null field specification", msg);
        }
    }

    @Test
    public void fromFieldSpecification_UnbalancedParantheses() throws Exception {

        try {

            CSVFieldFactory.fromFieldSpecification("something something else) and more");
            fail("should have thrown exception");
        }
        catch(CSVFormatException e) {

            String msg = e.getMessage();
            assertEquals("unbalanced parentheses", msg);
        }
    }

    @Test
    public void fromFieldSpecification_UnbalancedParantheses2() throws Exception {

        try {

            CSVFieldFactory.fromFieldSpecification("a)");
            fail("should throw exception");
        }
        catch(CSVFormatException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("unbalanced"));
        }
    }

    @Test
    public void fromFieldSpecification_NoTypeInformation() throws Exception {

        CSVField f = CSVFieldFactory.fromFieldSpecification("something");

        assertEquals("something", f.getName());
        assertEquals(String.class, f.getType());
        assertFalse(f.isTimestamp());
    }

    @Test
    public void fromFieldSpecification_NoTypeInformation_Timestamp() throws Exception {

        CSVField f = CSVFieldFactory.fromFieldSpecification("timestamp");

        assertEquals("timestamp", f.getName());
        assertEquals(Long.class, f.getType());
        assertTrue(f.isTimestamp());
    }

    @Test
    public void fromFieldSpecification_InvalidTypeSpecification() throws Exception {

        try {

            CSVFieldFactory.fromFieldSpecification("something(blah)");
            fail("should have thrown exception");
        }
        catch(CSVFormatException e) {

            String msg = e.getMessage();
            assertTrue(msg.startsWith("invalid field type specification"));
            assertTrue(msg.contains("blah"));
        }
    }

    @Test
    public void fromFieldSpecification_TypeInformation_String() throws Exception {

        CSVField f = CSVFieldFactory.fromFieldSpecification("something(string)");

        assertEquals("something", f.getName());
        assertEquals(String.class, f.getType());
        assertFalse(f.isTimestamp());
    }

    @Test
    public void fromFieldSpecification_TypeInformation_Integer() throws Exception {

        CSVField f = CSVFieldFactory.fromFieldSpecification("something(int)");

        assertEquals("something", f.getName());
        assertEquals(Integer.class, f.getType());
        assertFalse(f.isTimestamp());
    }

    @Test
    public void fromFieldSpecification_TypeInformation_Long() throws Exception {

        CSVField f = CSVFieldFactory.fromFieldSpecification("something(long)");

        assertEquals("something", f.getName());
        assertEquals(Long.class, f.getType());
        assertFalse(f.isTimestamp());
    }

    @Test
    public void fromFieldSpecification_TypeInformation_Float() throws Exception {

        CSVField f = CSVFieldFactory.fromFieldSpecification("something(float)");

        assertEquals("something", f.getName());
        assertEquals(Float.class, f.getType());
        assertFalse(f.isTimestamp());
    }

    @Test
    public void fromFieldSpecification_TypeInformation_Double() throws Exception {

        CSVField f = CSVFieldFactory.fromFieldSpecification("something(double)");

        assertEquals("something", f.getName());
        assertEquals(Double.class, f.getType());
        assertFalse(f.isTimestamp());
    }

    @Test
    public void fromFieldSpecification_TypeInformation_Time() throws Exception {

        CSVField f = CSVFieldFactory.fromFieldSpecification("something(time)");

        assertEquals("something", f.getName());
        assertEquals(Date.class, f.getType());
        assertFalse(f.isTimestamp());
    }

    @Test
    public void fromFieldSpecification_StringField() throws Exception {

        CSVField f = CSVFieldFactory.fromFieldSpecification("some-string(string)");

        assertEquals("some-string", f.getName());
        assertEquals(String.class, f.getType());
    }

    @Test
    public void fieldSpecificationParsing_SimpleString() throws Exception {

        CSVField f = CSVFieldFactory.fromFieldSpecification("some-string");

        assertEquals("some-string", f.getName());
        assertEquals(String.class, f.getType());
        assertFalse(f.isTimestamp());
    }

    @Test
    public void fieldSpecificationParsing_Time() throws Exception {

        CSVField f = CSVFieldFactory.fromFieldSpecification("timestamp(time:yy/MM/dd HH:mm:ss)");

        assertTrue(f.isTimestamp());

        assertEquals("timestamp", f.getName());
        assertEquals(Long.class, f.getType());

        Format format = f.getFormat();
        assertTrue(format instanceof SimpleDateFormat);
        SimpleDateFormat sdf = (SimpleDateFormat)format;

        assertEquals(sdf.parse("16/01/01 01:01:01"),
                new SimpleDateFormat("MM/dd/yy hh:mm:ss a").parse("01/01/16 01:01:01 AM"));
    }

    @Test
    public void fieldSpecificationParsing_Time_InvalidTimeFormatSpecification() throws Exception {

        try {

            CSVFieldFactory.fromFieldSpecification("timestamp(time:blah)");
        }
        catch(CSVFormatException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid timestamp format \"blah\""));
            IllegalArgumentException cause = (IllegalArgumentException)e.getCause();
            assertNotNull(cause);
        }
    }

    @Test
    public void fieldSpecificationParsing_Integer() throws Exception {

        CSVField f = CSVFieldFactory.fromFieldSpecification("a(int)");

        assertEquals("a", f.getName());
        assertEquals(Integer.class, f.getType());
    }

    @Test
    public void fieldSpecificationParsing_Integer_Space() throws Exception {

        CSVField f = CSVFieldFactory.fromFieldSpecification("something (int)");

        assertEquals("something", f.getName());
        assertEquals(Integer.class, f.getType());
    }

    @Test
    public void fieldSpecificationParsing_Long() throws Exception {

        CSVField f = CSVFieldFactory.fromFieldSpecification("a(long)");

        assertEquals("a", f.getName());
        assertEquals(Long.class, f.getType());
    }

    @Test
    public void fieldSpecificationParsing_Float() throws Exception {

        CSVField f = CSVFieldFactory.fromFieldSpecification("a(float)");

        assertEquals("a", f.getName());
        assertEquals(Float.class, f.getType());
    }

    @Test
    public void fieldSpecificationParsing_Double() throws Exception {

        CSVField f = CSVFieldFactory.fromFieldSpecification("a(double)");

        assertEquals("a", f.getName());
        assertEquals(Double.class, f.getType());
    }

    @Test
    public void fieldSpecificationParsing_InvalidType() throws Exception {

        try {

            CSVFieldFactory.fromFieldSpecification("fieldA(ms)");
            fail("should throw exception");
        }
        catch(CSVFormatException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid field type specification \"ms\""));
        }
    }

    // parseTimeSpecification() ----------------------------------------------------------------------------------------

    @Test
    public void parseTimeSpecification_Invalid() throws Exception {

        try {

            CSVFieldFactory.parseTimeSpecification("something");
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.startsWith("invalid time specification"));
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
            assertTrue(msg.startsWith("invalid time specification"));
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
            assertTrue(msg.startsWith("invalid timestamp format"));
        }
    }

    @Test
    public void parseTimeSpecification_Null() throws Exception {

        Format format = CSVFieldFactory.parseTimeSpecification("time");
        assertNull(format);
    }

    @Test
    public void parseTimeSpecification() throws Exception {

        Format format = CSVFieldFactory.parseTimeSpecification("time:MM/dd/YY HH:mm:ss");

        SimpleDateFormat sdf = (SimpleDateFormat)format;

        assertNotNull(sdf);
    }


    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
