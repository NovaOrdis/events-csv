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

import io.novaordis.events.api.event.LongProperty;
import io.novaordis.events.api.event.Property;
import io.novaordis.events.api.event.StringProperty;
import io.novaordis.events.api.event.TimestampProperty;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/8/17
 */
public class TimedCSVLineTest extends CSVEventTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void constructor_NoTimestampProperty() throws Exception {

        try {

            new TimedCSVLine(Collections.emptyList());
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("no timestamp property found"));
        }
    }

    @Test
    public void constructor_NoTimestampProperty2() throws Exception {

        List<Property> properties = Arrays.asList(
                new StringProperty("something", "something else"),
                new LongProperty("might be interpreted as timestamp", 1L)
        );

        try {

            new TimedCSVLine(properties);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("no timestamp property found"));
        }
    }

    @Test
    public void constructor() throws Exception {

        List<Property> properties = Arrays.asList(
                new TimestampProperty(7L),
                new LongProperty("might be interpreted as timestamp", 8L),
                new StringProperty("something", "something else")
        );

        TimedCSVLine e = new TimedCSVLine(properties);

        assertEquals(7L, e.getTime().longValue());

        List<Property> properties2 = e.getProperties();
        assertEquals(3, properties2.size());

        TimestampProperty p = (TimestampProperty)properties2.get(0);
        assertEquals("timestamp", p.getName());
        assertEquals(7L, ((Long)p.getValue()).longValue());

        LongProperty p2 = (LongProperty)properties2.get(1);
        assertEquals("might be interpreted as timestamp", p2.getName());
        assertEquals(8L, ((Long)p2.getValue()).longValue());

        StringProperty p3 = (StringProperty)properties2.get(2);
        assertEquals("something", p3.getName());
        assertEquals("something else", p3.getValue());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected TimedCSVLine getCSVEventToTest() throws Exception {

        return new TimedCSVLine(7L);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
