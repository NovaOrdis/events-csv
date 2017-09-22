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

import io.novaordis.events.api.event.Event;
import io.novaordis.events.api.event.GenericEvent;
import io.novaordis.events.api.event.LongProperty;
import io.novaordis.events.api.event.Property;
import io.novaordis.events.api.event.StringProperty;
import io.novaordis.events.csv.CSVFormat;
import io.novaordis.events.csv.event.CSVHeaders;
import io.novaordis.events.csv.event.field.CSVField;
import io.novaordis.events.csv.procedures.CSVProcedureFactory;
import io.novaordis.events.csv.procedures.ProcedureTest;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collections;
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

        Headers h = (Headers)f.find(Headers.COMMAND_LINE_LABELS[0], 0, extraArguments);

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

    @Test
    public void procedureFactoryFind_AlternativeName() throws Exception {

        CSVProcedureFactory f = new CSVProcedureFactory();

        List<String> extraArguments = Arrays.asList("arg1", "arg2", "arg3");

        Headers h = (Headers)f.find("headers", 0, extraArguments);

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

    @Test
    public void procedureFactoryFind_AlternativeName2() throws Exception {

        CSVProcedureFactory f = new CSVProcedureFactory();

        List<String> extraArguments = Arrays.asList("arg1", "arg2", "arg3");

        Headers h = (Headers)f.find("header", 0, extraArguments);

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
                        "  2: A(string)\n" +
                        "  3: B(string)\n" +
                        "  4: C(string)\n";

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
                        "  2: A(string)\n" +
                        "  3: B(string)\n" +
                        "  0: timestamp(time:MM/dd/yy HH:mm:ss)\n" +
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
                        "  0: timestamp(time:MM/dd/yy HH:mm:ss)\n" +
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

    // toCorrespondingPropertyInfo() ------------------------------------------------------------------------------------------------

    @Test
    public void toPropertyInfo_Null() throws Exception {

        try {

            Headers.toCorrespondingPropertyInfo(null);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("null header properties"));
        }
    }

    @Test
    public void toPropertyInfo_InvalidIndexInHeaderPropertyName() throws Exception {

        List<Property> headerProperties =
                Collections.singletonList(new StringProperty(CSVHeaders.HEADER_NAME_PREFIX + "blah"));

        try {

            Headers.toCorrespondingPropertyInfo(headerProperties);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("header name does not contain a valid integer index"));
            assertTrue(msg.contains("blah"));
        }
    }

    @Test
    public void toCorrespondingPropertyIndex_Production() throws Exception {

        List<Property> headerProperties = Arrays.asList(
                new LongProperty(Event.LINE_NUMBER_PROPERTY_NAME, 1001L),
                new StringProperty("header_0", "timestamp(time:long)"),
                new StringProperty("header_1", "name(string)"),
                new StringProperty("header_2", "counter(int)")
        );

        List<PropertyInfo> pis = Headers.toCorrespondingPropertyInfo(headerProperties);

        assertEquals(3, pis.size());

        PropertyInfo p = pis.get(0);
        assertEquals(0, p.getIndex());
        assertEquals("timestamp", p.getPropertyName());
        assertEquals("timestamp(time:long)", p.getFieldSpecification());

        PropertyInfo p2 = pis.get(1);
        assertEquals(2, p2.getIndex());
        assertEquals("name", p2.getPropertyName());
        assertEquals("name(string)", p2.getFieldSpecification());

        PropertyInfo p3 = pis.get(2);
        assertEquals(3, p3.getIndex());
        assertEquals("counter", p3.getPropertyName());
        assertEquals("counter(int)", p3.getFieldSpecification());
    }

    @Test
    public void toCorrespondingPropertyIndex_Production2() throws Exception {

        //
        // the timestamp property is present in the first column
        //

        List<Property> headerProperties = Arrays.asList(
                new LongProperty(Event.LINE_NUMBER_PROPERTY_NAME, 1001L),
                new StringProperty("header_0", "itemid(string)"),
                new StringProperty("header_1", "ns(string)"),
                new StringProperty("header_2", "value(string)"),
                new StringProperty("header_3", "timestamp(time:long)")
        );

        List<PropertyInfo> pis = Headers.toCorrespondingPropertyInfo(headerProperties);

        assertEquals(4, pis.size());

        PropertyInfo p = pis.get(0);
        assertEquals(2, p.getIndex());
        assertEquals("itemid", p.getPropertyName());

        PropertyInfo p2 = pis.get(1);
        assertEquals(3, p2.getIndex());
        assertEquals("ns", p2.getPropertyName());

        PropertyInfo p3 = pis.get(2);
        assertEquals(4, p3.getIndex());
        assertEquals("value", p3.getPropertyName());

        PropertyInfo p4 = pis.get(3);
        assertEquals(0, p4.getIndex());
        assertEquals("timestamp", p4.getPropertyName());
    }

    @Test
    public void toCorrespondingPropertyIndex_Production3() throws Exception {

        //
        // the timestamp property is present in the first column, and other properties follow
        //


        List<Property> headerProperties = Arrays.asList(
                new LongProperty(Event.LINE_NUMBER_PROPERTY_NAME, 1001L),
                new StringProperty("header_0", "itemid(string)"),
                new StringProperty("header_1", "ns(string)"),
                new StringProperty("header_2", "value(string)"),
                new StringProperty("header_3", "timestamp(time:long)"),
                new StringProperty("header_4", "count(int)")
        );

        List<PropertyInfo> pis = Headers.toCorrespondingPropertyInfo(headerProperties);

        assertEquals(5, pis.size());

        PropertyInfo p = pis.get(0);
        assertEquals(2, p.getIndex());
        assertEquals("itemid", p.getPropertyName());

        PropertyInfo p2 = pis.get(1);
        assertEquals(3, p2.getIndex());
        assertEquals("ns", p2.getPropertyName());

        PropertyInfo p3 = pis.get(2);
        assertEquals(4, p3.getIndex());
        assertEquals("value", p3.getPropertyName());

        PropertyInfo p4 = pis.get(3);
        assertEquals(0, p4.getIndex());
        assertEquals("timestamp", p4.getPropertyName());

        PropertyInfo p5 = pis.get(4);
        assertEquals(5, p5.getIndex());
        assertEquals("count", p5.getPropertyName());
    }

    // getCommandLineLabels() ------------------------------------------------------------------------------------------

    @Test
    public void getCommandLineLabels() throws Exception {

        Headers h = getProcedureToTest();

        List<String> labels = h.getCommandLineLabels();
        assertEquals(2, labels.size());
        assertEquals("header", labels.get(0));
        assertEquals("headers", labels.get(1));
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
