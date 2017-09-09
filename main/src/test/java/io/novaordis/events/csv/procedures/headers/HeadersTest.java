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
import io.novaordis.events.api.event.TimedEvent;
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
import static org.junit.Assert.fail;

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

    // process() -------------------------------------------------------------------------------------------------------

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
                        "  0: timestamp(time:MM/dd/yy HH:mm:ss)\n" +
                        "  1: A(string)\n" +
                        "  2: B(string)\n" +
                        "  3: C(string)\n";

        String actual = new String(((ByteArrayOutputStream)headersProcedure.getOutputStream()).toByteArray());

        assertEquals(expected, actual);

        assertFalse(headersProcedure.isExitLoop());
    }

    @Test
    public void process_TimestampNotOnTheFirstPosition() throws Exception {

        Headers headersProcedure = new Headers(new ByteArrayOutputStream());

        List<CSVField> fields = new CSVFormat("A, B, timestamp, C").getFields();

        CSVHeaders headersEvent = new CSVHeaders(777L, fields);

        headersProcedure.process(headersEvent);

        //
        // the timestamp is available implicitly on position 0, and explicitly on position 3
        //
        String expected =
                "line 777 header:\n" +
                        "  0: A(string)\n" +
                        "  1: B(string)\n" +
                        "  2: timestamp(time:MM/dd/yy HH:mm:ss)\n" +
                        "  3: C(string)\n";

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
                        "  0: timestamp(time:MM/dd/yy HH:mm:ss)\n" +
                        "  1: A(string)\n" +
                        "  2: B(string)\n" +
                        "  3: C(string)\n" +
                "line 888 header:\n" +
                        "  0: X(string)\n" +
                        "  1: Y(int)\n" +
                        "  2: Z(string)\n";

        String actual = new String(((ByteArrayOutputStream)headersProcedure.getOutputStream()).toByteArray());

        assertEquals(expected, actual);

        assertFalse(headersProcedure.isExitLoop());
    }

    // indexFromHeaderName() -------------------------------------------------------------------------------------------

    @Test
    public void indexFromHeaderName() throws Exception {

        Integer i = Headers.indexFromHeaderName(CSVHeaders.HEADER_NAME_PREFIX + 10, "does-not-matter");
        assertNotNull(i);
        assertEquals(10, i.intValue());
    }

    @Test
    public void indexFromHeaderName_Timestamp() throws Exception {

        Integer i = Headers.indexFromHeaderName(CSVHeaders.HEADER_NAME_PREFIX + 10, TimedEvent.TIMESTAMP_PROPERTY_NAME);

        assertEquals(10, i.intValue());
    }

    @Test
    public void indexFromHeaderName_TimestampAndFormat() throws Exception {

        Integer i = Headers.indexFromHeaderName(
                CSVHeaders.HEADER_NAME_PREFIX + 10, TimedEvent.TIMESTAMP_PROPERTY_NAME + "(something)");

        assertEquals(10, i.intValue());
    }

    @Test
    public void indexFromHeaderName_NotExactlyATimestamp() throws Exception {

        Integer i = Headers.indexFromHeaderName(CSVHeaders.HEADER_NAME_PREFIX + 10, "timestamps");
        assertNotNull(i);
        assertEquals(10, i.intValue());
    }

    @Test
    public void indexFromHeaderName_Null() throws Exception {

        try {

            Headers.indexFromHeaderName(null, "does-not-matter");
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("null header name"));
        }
    }

    @Test
    public void indexFromHeaderName_InvalidPrefix() throws Exception {

        try {

            Headers.indexFromHeaderName("something", "does-not-matter");
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("header name does not start with a valid prefix"));
            assertTrue(msg.contains(CSVHeaders.HEADER_NAME_PREFIX));
        }
    }

    @Test
    public void indexFromHeaderName_InvalidPostfix() throws Exception {

        try {

            Headers.indexFromHeaderName(CSVHeaders.HEADER_NAME_PREFIX + "blah", "does-not-matter");
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("header name does not contain a valid integer index"));
            assertTrue(msg.contains("blah"));
        }
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
