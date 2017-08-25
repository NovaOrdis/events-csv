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

import org.junit.Test;

import java.text.Format;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 2/6/16
 */
public class TimestampCSVFieldTest extends CSVFieldTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Overrides -------------------------------------------------------------------------------------------------------

    @Test
    @Override
    public void identity_NullType() throws Exception {

        //
        // noop, this does not make sense of TimestampCSVField
        //
    }

    @Test
    public void identity() throws Exception {

        TimestampCSVField f = new TimestampCSVField("something");

        assertEquals("something", f.getName());
        assertEquals(Long.class, f.getType());
        assertNull(f.getFormat());
        assertTrue(f.isTimestamp());
    }


    @Test
    @Override
    public void toProperty_Integer() throws Exception {

        //
        // noop, this does not make sense of TimestampCSVField
        //
    }

    @Test
    @Override
    public void toProperty_String() throws Exception {

        //
        // noop, this does not make sense of TimestampCSVField
        //
    }

    @Test
    @Override
    public void toProperty_Long() throws Exception {

        //
        // noop, this does not make sense of TimestampCSVField
        //
    }

    @Test
    @Override
    public void toProperty_Float() throws Exception {

        //
        // noop, this does not make sense of TimestampCSVField
        //
    }

    @Test
    @Override
    public void toProperty_Double() throws Exception {

        //
        // noop, this does not make sense of TimestampCSVField
        //
    }

    @Test
    @Override
    public void toProperty_Date() throws Exception {

        //
        // noop, this does not make sense of TimestampCSVField
        //
    }

    // getSpecification()------------------------------------------------------------------------------------------------

    @Test
    @Override
    public void toSpecification_String() throws Exception {

        //
        // noop, this does not make sense of TimestampCSVField
        //
    }

    @Test
    @Override
    public void toSpecification_Integer() throws Exception {

        //
        // noop, this does not make sense of TimestampCSVField
        //
    }

    @Test
    @Override
    public void toSpecification_Long() throws Exception {

        CSVField f = getCSVFieldToTest("test", Long.class);
        assertEquals("test(time)", f.getSpecification());
    }

    @Test
    @Override
    public void toSpecification_Float() throws Exception {

        //
        // noop, this does not make sense of TimestampCSVField
        //
    }

    @Test
    @Override
    public void toSpecification_Double() throws Exception {

        //
        // noop, this does not make sense of TimestampCSVField
        //
    }

    @Test
    @Override
    public void toSpecification_Time() throws Exception {

        //
        // noop, this does not make sense of TimestampCSVField
        //
    }

    @Test
    @Override
    public void toSpecification_Time_Format() throws Exception {

        //
        // noop, this does not make sense of TimestampCSVField
        //
    }

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void toSpecification_Timestamp_NoFormat() throws Exception {

        TimestampCSVField f = new TimestampCSVField();
        assertEquals("timestamp(time)", f.getSpecification());
    }

    @Test
    public void toSpecification_Timestamp_Format() throws Exception {

        SimpleDateFormat fmt = new SimpleDateFormat("yy/MM/dd hh:mm");
        TimestampCSVField f = new TimestampCSVField(fmt);
        assertEquals("timestamp(time:yy/MM/dd hh:mm)", f.getSpecification());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected TimestampCSVField getCSVFieldToTest(String name, Class type, Format format) throws Exception {

        if (!Long.class.equals(type)) {

            throw new RuntimeException("RETURN HERE, I am getting " + type);
        }

        return new TimestampCSVField(name, format);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
