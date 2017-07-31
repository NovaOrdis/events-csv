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

package io.novaordis.events.csv;

import org.junit.Test;

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

    // Tests -----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected TimestampCSVField getCSVFieldToTest(String name, Class type) throws Exception {

        if (!Long.class.equals(type)) {

            throw new RuntimeException("RETURN HERE, I am getting " + type);
        }

        return new TimestampCSVField(name);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
