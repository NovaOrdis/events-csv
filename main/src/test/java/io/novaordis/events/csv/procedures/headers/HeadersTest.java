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
import io.novaordis.events.csv.CSVFormat;
import io.novaordis.events.csv.event.CSVHeaders;
import io.novaordis.events.csv.event.field.CSVField;
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
    public void process() throws Exception {

        Headers headersProcedure = new Headers(new ByteArrayOutputStream());

        List<CSVField> fields = new CSVFormat("timestamp, A, B, C").getFields();

        CSVHeaders headersEvent = new CSVHeaders(777L, fields);

        headersProcedure.process(headersEvent);

        String expected =
                "line 777 header:\n" +
                        "  1: timestamp(time:MM/dd/yy HH:mm:ss)\n" +
                        "  2: A(string)\n" +
                        "  3: B(string)\n" +
                        "  4: C(string)\n";

        String actual = new String(((ByteArrayOutputStream)headersProcedure.getOutputStream()).toByteArray());

        assertEquals(expected, actual);

        assertFalse(headersProcedure.isExitLoop());
    }

    @Test
    public void process_TwoHeaders() throws Exception {

        Headers headersProcedure = new Headers(new ByteArrayOutputStream());

        List<CSVField> fields = new CSVFormat("timestamp, A, B, C").getFields();

        CSVHeaders headersEvent = new CSVHeaders(777L, fields);

        headersProcedure.process(headersEvent);

        List<CSVField> fields2 = new CSVFormat("X, Y(int), Z").getFields();

        CSVHeaders headersEvent2 = new CSVHeaders(888L, fields2);

        headersProcedure.process(headersEvent2);

        String expected =
                "line 777 header:\n" +
                        "  1: timestamp(time:MM/dd/yy HH:mm:ss)\n" +
                        "  2: A(string)\n" +
                        "  3: B(string)\n" +
                        "  4: C(string)\n" +
                "line 888 header:\n" +
                        "  1: X(string)\n" +
                        "  2: Y(int)\n" +
                        "  3: Z(string)\n";

        String actual = new String(((ByteArrayOutputStream)headersProcedure.getOutputStream()).toByteArray());

        assertEquals(expected, actual);

        assertFalse(headersProcedure.isExitLoop());
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
