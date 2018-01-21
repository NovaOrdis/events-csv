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

import org.junit.Test;

import io.novaordis.events.api.event.Event;

import static org.junit.Assert.assertEquals;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/8/17
 */
public class NonTimedCSVLineTest extends CSVEventTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // preferred representation ----------------------------------------------------------------------------------------

    @Test
    public void getPreferredRepresentation() throws Exception {

        NonTimedCSVLine e = new NonTimedCSVLine();

        e.setLongProperty(Event.LINE_PROPERTY_NAME, 1001L);
        e.setStringProperty("A", "something");
        e.setIntegerProperty("B", 1);
        e.setLongProperty("C", 2L);
        e.setFloatProperty("D", 3.3f);
        e.setBooleanProperty("E", true);

        String line = e.getPreferredRepresentation(",");

        String expected = "1001, something, 1, 2, 3.3, true";

        assertEquals(expected, line);
    }

    @Test
    public void getPreferredRepresentationHeader() throws Exception {

        NonTimedCSVLine e = new NonTimedCSVLine();

        e.setLongProperty(Event.LINE_PROPERTY_NAME, 1001L);
        e.setStringProperty("A", "something");
        e.setIntegerProperty("B", 1);
        e.setLongProperty("C", 2L);
        e.setFloatProperty("D", 3.3f);
        e.setBooleanProperty("E", true);

        String line = e.getPreferredRepresentationHeader(",");

        String expected = "line-number(long), A(string), B(int), C(long), D(float), E(boolean)";

        assertEquals(expected, line);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected NonTimedCSVLine getCSVEventToTest() throws Exception {

        return new NonTimedCSVLine();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
