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

package io.novaordis.events.csv.procedures.headers;

import io.novaordis.events.api.event.GenericEvent;
import io.novaordis.events.csv.event.CSVHeaders;
import io.novaordis.events.csv.procedures.CSVProcedureFactory;
import io.novaordis.events.csv.procedures.ProcedureTest;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/7/17
 */
public class HeadersTest extends ProcedureTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Overrides -------------------------------------------------------------------------------------------------------

    @Test
    @Override
    public void procedureFactoryFind() throws Exception {

        CSVProcedureFactory f = new CSVProcedureFactory();

        List<String> extraArguments = Arrays.asList("arg1", "arg2", "arg3");

        Headers h = (Headers)f.find(Headers.COMMAND_LINE_LABEL, 0, extraArguments);

        assertNotNull(h);

        assertEquals(3, extraArguments.size());
        assertEquals("arg1", extraArguments.get(0));
        assertEquals("arg2", extraArguments.get(1));
        assertEquals("arg3", extraArguments.get(2));

        //
        // make sure the procedure is initialized
        //
        assertTrue(System.out.equals(h.getOutputStream()));
    }

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void process_NonHeaderGoesThrough() throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Headers h = new Headers(baos);

        h.process(new GenericEvent());

        assertEquals(0, baos.toByteArray().length);

        assertFalse(h.isExitLoop());
    }

    @Test
    public void process_HeaderGeneratesOutputAndBreaksTheLoop() throws Exception {

        String header = "timestamp, A, B, C";

        Headers headersProcedure = new Headers(new ByteArrayOutputStream());

        CSVHeaders headersEvent = new CSVHeaders();

        headersEvent.load(7L, header);

        headersProcedure.process(headersEvent);

        String expected =
                "1: timestamp\n" +
                        "2: A\n" +
                        "3: B\n" +
                        "4: C\n";

        String actual = new String(((ByteArrayOutputStream)headersProcedure.getOutputStream()).toByteArray());

        assertEquals(expected, actual);

        assertTrue(headersProcedure.isExitLoop());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected Headers getProcedureToTest() throws Exception {

        return new Headers(System.out);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
