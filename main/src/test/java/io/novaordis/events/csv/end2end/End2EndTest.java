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

package io.novaordis.events.csv.end2end;

import io.novaordis.events.api.event.EndOfStreamEvent;
import io.novaordis.events.api.event.Event;
import io.novaordis.events.cli.EventParserRuntime;
import io.novaordis.events.csv.CSVParser;
import io.novaordis.events.csv.Constants;
import io.novaordis.events.csv.MockProcedure;
import io.novaordis.events.csv.MockProcedureFactory;
import io.novaordis.events.csv.event.CSVHeaders;
import io.novaordis.events.csv.event.TimedCSVLine;
import io.novaordis.events.csv.event.field.CSVField;
import io.novaordis.utilities.appspec.ApplicationSpecificBehavior;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/22/17
 */
public class End2EndTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void endToEnd() throws Exception {

        File f = new File(System.getProperty("basedir"), "src/test/resources/data/reference-timed.csv");

        assertTrue(f.isFile());

        CSVParser parser = new CSVParser();

        //
        // a mock procedure that just collects events and offers them for inspection
        //

        MockProcedure mp = new MockProcedure();

        MockProcedureFactory mf = new MockProcedureFactory(mp);

        ApplicationSpecificBehavior asb = new ApplicationSpecificBehavior(parser, mf);

        String[] commandLine = {

                "mock-procedure",
                f.getPath()
        };

        EventParserRuntime r = new EventParserRuntime(commandLine, "test", asb);

        r.run();

        List<Event> allEvents = mp.getEvents();

        assertEquals(4, allEvents.size());

        //
        // # timestamp, color, size(int)
        //
        //12/01/16 00:00:00, blue, 10
        //
        //12/01/16 00:00:01, red, 20
        //

        CSVHeaders e = (CSVHeaders)allEvents.get(0);

        List<CSVField> fields = e.getFields();
        assertEquals(3, fields.size());
        assertEquals("timestamp", fields.get(0).getName());
        assertEquals(Long.class, fields.get(0).getType());
        assertEquals("color", fields.get(1).getName());
        assertEquals(String.class, fields.get(1).getType());
        assertEquals("size", fields.get(2).getName());
        assertEquals(Integer.class, fields.get(2).getType());

        TimedCSVLine e2 = (TimedCSVLine)allEvents.get(1);
        assertEquals(Constants.getDefaultTimestampFormat().parse("12/01/16 00:00:00").getTime(), e2.getTime().longValue());
        assertEquals("blue", e2.getProperty("color").getValue());
        assertEquals(10, e2.getProperty("size").getValue());

        TimedCSVLine e3 = (TimedCSVLine)allEvents.get(2);
        assertEquals(Constants.getDefaultTimestampFormat().parse("12/01/16 00:00:01").getTime(), e3.getTime().longValue());
        assertEquals("red", e3.getProperty("color").getValue());
        assertEquals(20, e3.getProperty("size").getValue());

        EndOfStreamEvent e4 = (EndOfStreamEvent)allEvents.get(3);
        assertNotNull(e4);
    }

    @Test
    public void endToEnd_OnlyHeader() throws Exception {

        File f = new File(System.getProperty("basedir"), "src/test/resources/data/only-header.csv");

        assertTrue(f.isFile());

        CSVParser parser = new CSVParser();

        //
        // a mock procedure that just collects events and offers them for inspection
        //

        MockProcedure mp = new MockProcedure();

        MockProcedureFactory mf = new MockProcedureFactory(mp);

        ApplicationSpecificBehavior asb = new ApplicationSpecificBehavior(parser, mf);

        String[] commandLine = {

                "mock-procedure",
                f.getPath()
        };

        EventParserRuntime r = new EventParserRuntime(commandLine, "test", asb);

        r.run();

        List<Event> allEvents = mp.getEvents();

        assertEquals(2, allEvents.size());

        //
        // # timestamp, color, size(int)
        //

        CSVHeaders e = (CSVHeaders)allEvents.get(0);

        List<CSVField> fields = e.getFields();
        assertEquals(3, fields.size());
        assertEquals("timestamp", fields.get(0).getName());
        assertEquals(Long.class, fields.get(0).getType());
        assertEquals("color", fields.get(1).getName());
        assertEquals(String.class, fields.get(1).getType());
        assertEquals("size", fields.get(2).getName());
        assertEquals(Integer.class, fields.get(2).getType());

        EndOfStreamEvent e2 = (EndOfStreamEvent)allEvents.get(1);
        assertNotNull(e2);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
