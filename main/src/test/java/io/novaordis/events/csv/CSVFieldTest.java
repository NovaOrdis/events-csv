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

import io.novaordis.events.api.event.DateProperty;
import io.novaordis.events.api.event.DoubleProperty;
import io.novaordis.events.api.event.FloatProperty;
import io.novaordis.events.api.event.IntegerProperty;
import io.novaordis.events.api.event.LongProperty;
import io.novaordis.events.api.event.StringProperty;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 6/20/17
 */
public abstract class CSVFieldTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void identity_NullType() throws Exception {

        CSVField f = getCSVFieldToTest("something", null);

        assertEquals("something", f.getName());
        assertNull(f.getType());
        assertNull(f.getFormat());
    }

    @Test
    public void identity() throws Exception {

        CSVField f = getCSVFieldToTest("something", String.class);

        assertEquals("something", f.getName());
        assertEquals(String.class, f.getType());
        assertNull(f.getFormat());
        assertFalse(f.isTimestamp());

    }

    // toProperty() ----------------------------------------------------------------------------------------------------

    @Test
    public void toProperty_Integer() throws Exception {

        CSVField f = getCSVFieldToTest("something", Integer.class);

        IntegerProperty p = (IntegerProperty)f.toProperty("7");

        assertNotNull(p);

        assertEquals(7, p.getInteger().intValue());
    }

    @Test
    public void toProperty_String() throws Exception {

        CSVField f = getCSVFieldToTest("something", String.class);

        StringProperty p = (StringProperty)f.toProperty("7");

        assertNotNull(p);

        assertEquals("7", p.getString());
    }

    @Test
    public void toProperty_Long() throws Exception {

        CSVField f = getCSVFieldToTest("something", Long.class);

        LongProperty p = (LongProperty)f.toProperty("7");

        assertNotNull(p);

        assertEquals(7L, p.getLong().longValue());
    }

    @Test
    public void toProperty_Float() throws Exception {

        CSVField f = getCSVFieldToTest("something", Float.class);

        FloatProperty p = (FloatProperty)f.toProperty("7");

        assertNotNull(p);

        assertEquals(7f, p.getFloat().floatValue(), 0.00001);
    }

    @Test
    public void toProperty_Double() throws Exception {

        CSVField f = getCSVFieldToTest("something", Double.class);

        DoubleProperty p = (DoubleProperty)f.toProperty("7");

        assertNotNull(p);

        assertEquals(7d, p.getDouble().doubleValue(), 0.00001);
    }

    @Test
    public void toProperty_Date() throws Exception {

        CSVField f = getCSVFieldToTest("something", Date.class);

        DateProperty p = (DateProperty)f.toProperty("7");

        assertNotNull(p);

        assertEquals(7L, p.getDate().getTime());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract CSVField getCSVFieldToTest(String name, Class type) throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
