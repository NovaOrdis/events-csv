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

import io.novaordis.events.csv.event.CSVHeaders;
import io.novaordis.events.csv.event.NonTimedCSVLine;
import io.novaordis.events.csv.event.TimedCSVLine;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/30/17
 */
public class CSVHeaderOutputStrategyTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void sequence() throws Exception {

        CSVHeaderOutputStrategy s = new CSVHeaderOutputStrategy();

        CSVHeaders e = new CSVHeaders();

        assertFalse(s.shouldDisplayHeader(e));

        CSVHeaders e2 = new CSVHeaders();

        assertFalse(s.shouldDisplayHeader(e2));

        TimedCSVLine e3 = new TimedCSVLine(1L);

        assertTrue(s.shouldDisplayHeader(e3));

        s.headerDisplayed(e3);

        TimedCSVLine e4 = new TimedCSVLine(1L);

        assertFalse(s.shouldDisplayHeader(e4));

        CSVHeaders e5 = new CSVHeaders();

        assertFalse(s.shouldDisplayHeader(e5));
    }

    @Test
    public void sequence2() throws Exception {

        CSVHeaderOutputStrategy s = new CSVHeaderOutputStrategy();

        CSVHeaders e = new CSVHeaders();

        assertFalse(s.shouldDisplayHeader(e));

        CSVHeaders e2 = new CSVHeaders();

        assertFalse(s.shouldDisplayHeader(e2));

        NonTimedCSVLine e3 = new NonTimedCSVLine();

        assertTrue(s.shouldDisplayHeader(e3));

        s.headerDisplayed(e3);

        NonTimedCSVLine e4 = new NonTimedCSVLine();

        assertFalse(s.shouldDisplayHeader(e4));

        CSVHeaders e5 = new CSVHeaders();

        assertFalse(s.shouldDisplayHeader(e5));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
