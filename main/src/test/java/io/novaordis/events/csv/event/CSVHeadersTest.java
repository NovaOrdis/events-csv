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

import io.novaordis.events.api.event.Property;
import io.novaordis.events.api.event.StringProperty;
import io.novaordis.events.api.event.TimedEvent;
import io.novaordis.events.api.parser.ParsingException;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
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

    // load() ----------------------------------------------------------------------------------------------------------

    @Test
    public void load() throws Exception {

        CSVHeaders h = getCSVEventToTest();

        h.load(7L, "timestamp, A, B");

        List<Property> properties = h.getProperties();

        assertEquals(3, properties.size());

        StringProperty p = (StringProperty)properties.get(0);
        assertEquals(CSVHeaders.HEADER_NAME_PREFIX + "0", p.getName());
        assertEquals(TimedEvent.TIMESTAMP_PROPERTY_NAME, p.getString());

        StringProperty p2 = (StringProperty)properties.get(1);
        assertEquals(CSVHeaders.HEADER_NAME_PREFIX + "1", p2.getName());
        assertEquals("A", p2.getString());

        StringProperty p3 = (StringProperty)properties.get(2);
        assertEquals(CSVHeaders.HEADER_NAME_PREFIX + "2", p3.getName());
        assertEquals("B", p3.getString());
    }

    @Test
    public void load_headerGaps() throws Exception {

        CSVHeaders h = getCSVEventToTest();

        try {

            h.load(7L, "timestamp, , B");
            fail("should have thrown exception");
        }
        catch (ParsingException e) {

            assertEquals(7L, e.getLineNumber().longValue());
            assertNull(e.getPositionInLine());
            String msg = e.getMessage();
            assertTrue(msg.contains("missing header"));
        }
    }

    @Test
    public void load_headerGaps2() throws Exception {

        CSVHeaders h = getCSVEventToTest();

        try {

            h.load(7L, "timestamp, A, ");
            fail("should have thrown exception");
        }
        catch (ParsingException e) {

            assertEquals(7L, e.getLineNumber().longValue());
            assertNull(e.getPositionInLine());
            String msg = e.getMessage();
            assertTrue(msg.contains("missing header"));
        }
    }

    @Test
    public void load_NoTimestamp() throws Exception {

        CSVHeaders h = getCSVEventToTest();

        // we accept CSV lines with no timestamp

        h.load(7L, "A, B, C");

        List<Property> properties = h.getProperties();

        assertEquals(3, properties.size());

        StringProperty p = (StringProperty)properties.get(0);
        assertEquals(CSVHeaders.HEADER_NAME_PREFIX + "0", p.getName());
        assertEquals("A", p.getString());

        StringProperty p2 = (StringProperty)properties.get(1);
        assertEquals(CSVHeaders.HEADER_NAME_PREFIX + "1", p2.getName());
        assertEquals("B", p2.getString());

        StringProperty p3 = (StringProperty)properties.get(2);
        assertEquals(CSVHeaders.HEADER_NAME_PREFIX + "2", p3.getName());
        assertEquals("C", p3.getString());
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
