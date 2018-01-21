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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import io.novaordis.events.api.event.EndOfStreamEvent;
import io.novaordis.events.api.event.Event;
import io.novaordis.events.api.event.GenericEvent;
import io.novaordis.events.api.event.LongProperty;
import io.novaordis.events.api.event.Property;
import io.novaordis.events.api.event.StringProperty;
import io.novaordis.events.api.event.TimestampProperty;
import io.novaordis.events.csv.CSVFormat;
import io.novaordis.events.csv.event.CSVHeaders;
import io.novaordis.events.csv.event.TimedCSVLine;
import io.novaordis.events.csv.event.field.CSVField;
import io.novaordis.events.csv.procedures.CSVProcedureFactory;
import io.novaordis.events.csv.procedures.ProcedureTest;
import io.novaordis.utilities.UserErrorException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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

        List<String> extraArguments = new ArrayList<>(Arrays.asList("arg1", "arg2", "arg3"));

        Headers h = (Headers)f.find(Headers.COMMAND_LINE_LABELS[0], 0, extraArguments);

        assertNotNull(h);

        assertEquals("arg1", h.getRegularExpressionLiteral());

        assertEquals(2, extraArguments.size());
        assertEquals("arg2", extraArguments.get(0));
        assertEquals("arg3", extraArguments.get(1));

        //
        // make sure the procedure is initialized
        //
        assertTrue(System.out.equals(h.getOutputStream()));
    }

    @Test
    public void procedureFactoryFind_AlternativeName() throws Exception {

        CSVProcedureFactory f = new CSVProcedureFactory();

        List<String> extraArguments = new ArrayList<>(Arrays.asList("arg1", "arg2", "arg3"));

        Headers h = (Headers)f.find("headers", 0, extraArguments);

        assertNotNull(h);

        assertEquals("arg1", h.getRegularExpressionLiteral());

        assertEquals(2, extraArguments.size());
        assertEquals("arg2", extraArguments.get(0));
        assertEquals("arg3", extraArguments.get(1));

        //
        // make sure the procedure is initialized
        //
        assertTrue(System.out.equals(h.getOutputStream()));
    }

    @Test
    public void procedureFactoryFind_AlternativeName2() throws Exception {

        CSVProcedureFactory f = new CSVProcedureFactory();

        List<String> extraArguments = new ArrayList<>(Arrays.asList("arg1", "arg2", "arg3"));

        Headers h = (Headers)f.find("header", 0, extraArguments);

        assertNotNull(h);

        assertEquals("arg1", h.getRegularExpressionLiteral());

        assertEquals(2, extraArguments.size());
        assertEquals("arg2", extraArguments.get(0));
        assertEquals("arg3", extraArguments.get(1));

        //
        // make sure the procedure is initialized
        //
        assertTrue(System.out.equals(h.getOutputStream()));
    }

    // Tests -----------------------------------------------------------------------------------------------------------

    // constructors ----------------------------------------------------------------------------------------------------

    @Test
    public void constructor_Null() throws Exception {

        try {

            new Headers(0, null, System.out);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("null"));
            assertTrue(msg.contains("command line"));
        }

    }

    @Test
    public void constructor_NoCommandLineArguments() throws Exception {

        Headers h = new Headers(0, Collections.emptyList(), System.out);

        assertFalse(h.isExitLoop());
        assertFalse(h.isFirst());
        assertFalse(h.isLast());
        assertNull(h.getRegularExpressionLiteral());
        assertNull(h.getRegularExpressionPattern());
    }

    @Test
    public void constructor_IrrelevantCommandLineArguments() throws Exception {

        List<String> args = new ArrayList<>(Arrays.asList(
                "blue", "red", "yellow", "this-will-be-interpreted-as-regex", "black", "white"));

        Headers h = new Headers(3, args, System.out);

        assertFalse(h.isExitLoop());
        assertFalse(h.isFirst());
        assertFalse(h.isLast());
        assertEquals("this-will-be-interpreted-as-regex", h.getRegularExpressionLiteral());

        assertEquals(5, args.size());
        assertEquals("blue", args.get(0));
        assertEquals("red", args.get(1));
        assertEquals("yellow", args.get(2));
        assertEquals("black", args.get(3));
        assertEquals("white", args.get(4));
    }

    @Test
    public void constructor_First() throws Exception {

        List<String> args = new ArrayList<>(Arrays.asList("headers", "--first", "regex", "something else"));

        Headers h = new Headers(1, args, System.out);

        assertFalse(h.isExitLoop());
        assertTrue(h.isFirst());
        assertFalse(h.isLast());
        assertEquals("regex", h.getRegularExpressionLiteral());

        assertEquals(2, args.size());
        assertEquals("headers", args.get(0));
        assertEquals("something else", args.get(1));
    }

    @Test
    public void constructor_First2() throws Exception {

        List<String> args = new ArrayList<>(Arrays.asList("headers", "--first", "regex"));

        Headers h = new Headers(1, args, System.out);

        assertFalse(h.isExitLoop());
        assertTrue(h.isFirst());
        assertFalse(h.isLast());
        assertEquals("regex", h.getRegularExpressionLiteral());

        assertEquals(1, args.size());
        assertEquals("headers", args.get(0));
    }

    @Test
    public void constructor_First3() throws Exception {

        List<String> args = new ArrayList<>(Arrays.asList("headers", "--first"));

        Headers h = new Headers(1, args, System.out);

        assertFalse(h.isExitLoop());
        assertTrue(h.isFirst());
        assertFalse(h.isLast());

        assertEquals(1, args.size());
        assertEquals("headers", args.get(0));
    }

    @Test
    public void constructor_Last() throws Exception {

        List<String> args = new ArrayList<>(Arrays.asList("headers", "--last", "regex", "something else"));

        Headers h = new Headers(1, args, System.out);

        assertFalse(h.isExitLoop());
        assertFalse(h.isFirst());
        assertTrue(h.isLast());
        assertEquals("regex", h.getRegularExpressionLiteral());

        assertEquals(2, args.size());
        assertEquals("headers", args.get(0));
        assertEquals("something else", args.get(1));
    }

    @Test
    public void constructor_Last2() throws Exception {

        List<String> args = new ArrayList<>(Arrays.asList("headers", "--last", "regex"));

        Headers h = new Headers(1, args, System.out);

        assertFalse(h.isExitLoop());
        assertFalse(h.isFirst());
        assertTrue(h.isLast());
        assertEquals("regex", h.getRegularExpressionLiteral());

        assertEquals(1, args.size());
        assertEquals("headers", args.get(0));
    }

    @Test
    public void constructor_Last3() throws Exception {

        List<String> args = new ArrayList<>(Arrays.asList("headers", "--last"));

        Headers h = new Headers(1, args, System.out);

        assertFalse(h.isExitLoop());
        assertFalse(h.isFirst());
        assertTrue(h.isLast());

        assertEquals(1, args.size());
        assertEquals("headers", args.get(0));
    }

    @Test
    public void constructor_First_And_Last_AtTheSameTime() throws Exception {

        List<String> args = new ArrayList<>(Arrays.asList("headers", "--last", "--first"));

        try {

            new Headers(1, args, System.out);
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("--first"));
            assertTrue(msg.contains("--last"));
            assertTrue(msg.contains("cannot be used at the same time"));
        }
    }

    @Test
    public void constructor_RegularExpression() throws Exception {

        List<String> args = new ArrayList<>(Arrays.asList(

                "usually-the-procedure-name",
                "^something.*else$"
        ));

        Headers p = new Headers(1, args, new ByteArrayOutputStream());

        assertEquals("^something.*else$", p.getRegularExpressionLiteral());

        Pattern rp = p.getRegularExpressionPattern();
        Matcher m = rp.matcher("something really else");
        assertTrue(m.matches());

        assertFalse(p.isLast());
        assertFalse(p.isFirst());

        assertEquals(1, args.size());
        assertEquals("usually-the-procedure-name", args.get(0));
    }

    @Test
    public void constructor_RegularExpressionPrecededByLast() throws Exception {

        List<String> args = new ArrayList<>(Arrays.asList(

                "usually-the-procedure-name",
                "--last",
                "^something.*else$"
        ));

        Headers p = new Headers(1, args, new ByteArrayOutputStream());

        assertEquals("^something.*else$", p.getRegularExpressionLiteral());

        Pattern rp = p.getRegularExpressionPattern();
        Matcher m = rp.matcher("something really else");
        assertTrue(m.matches());

        assertTrue(p.isLast());
        assertFalse(p.isFirst());

        assertEquals(1, args.size());
        assertEquals("usually-the-procedure-name", args.get(0));
    }

    @Test
    public void constructor_RegularExpressionFollowedByLast() throws Exception {

        List<String> args = new ArrayList<>(Arrays.asList(

                "usually-the-procedure-name",
                "^something.*else$",
                "--last"
        ));

        Headers p = new Headers(1, args, new ByteArrayOutputStream());

        assertEquals("^something.*else$", p.getRegularExpressionLiteral());

        Pattern rp = p.getRegularExpressionPattern();
        Matcher m = rp.matcher("something really else");
        assertTrue(m.matches());

        assertTrue(p.isLast());
        assertFalse(p.isFirst());

        assertEquals(1, args.size());
        assertEquals("usually-the-procedure-name", args.get(0));
    }

    @Test
    public void constructor_InvalidRegularExpression() throws Exception {

        List<String> args = new ArrayList<>(Arrays.asList(

                "usually-the-procedure-name",
                "something(()"
        ));

        try {

            new Headers(1, args, new ByteArrayOutputStream());

            fail("should have thrown exeption");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid regular expression"));
            assertTrue(msg.contains("'something(()'"));
        }
    }

    // process() -------------------------------------------------------------------------------------------------------

    @Test
    public void process_NonHeaderGoesThrough() throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Headers h = new Headers(0, Collections.emptyList(), baos);

        h.process(new GenericEvent());

        assertEquals(0, baos.toByteArray().length);

        assertFalse(h.isExitLoop());
    }

    @Test
    public void process() throws Exception {

        Headers headersProcedure = new Headers(0, Collections.emptyList(), new ByteArrayOutputStream());

        List<CSVField> fields = new CSVFormat("time, A, B, C").getFields();

        CSVHeaders headersEvent = new CSVHeaders(777L, fields);

        headersProcedure.process(headersEvent);

        String expected =
                "line 777 header:\n" +
                        "  0: time(time:MM/dd/yy HH:mm:ss)\n" +
                        "  2: A(string)\n" +
                        "  3: B(string)\n" +
                        "  4: C(string)\n";

        String actual = new String(((ByteArrayOutputStream)headersProcedure.getOutputStream()).toByteArray());

        assertEquals(expected, actual);

        assertFalse(headersProcedure.isExitLoop());
    }

    @Test
    public void process_TimestampNotOnTheFirstPosition() throws Exception {

        Headers headersProcedure = new Headers(0, Collections.emptyList(), new ByteArrayOutputStream());

        List<CSVField> fields = new CSVFormat("A, B, time, C").getFields();

        CSVHeaders headersEvent = new CSVHeaders(777L, fields);

        headersProcedure.process(headersEvent);

        //
        // the timestamp is available implicitly on position 0, and explicitly on position 3
        //
        String expected =
                "line 777 header:\n" +
                        "  2: A(string)\n" +
                        "  3: B(string)\n" +
                        "  0: time(time:MM/dd/yy HH:mm:ss)\n" +
                        "  4: C(string)\n";

        String actual = new String(((ByteArrayOutputStream)headersProcedure.getOutputStream()).toByteArray());

        assertEquals(expected, actual);

        assertFalse(headersProcedure.isExitLoop());
    }

    @Test
    public void process_TwoHeaders() throws Exception {

        Headers headersProcedure = new Headers(0, Collections.emptyList(), new ByteArrayOutputStream());

        List<CSVField> fields = new CSVFormat("time, A, B, C").getFields();

        CSVHeaders headersEvent = new CSVHeaders(777L, fields);

        headersProcedure.process(headersEvent);

        List<CSVField> fields2 = new CSVFormat("X, Y(int), Z").getFields();

        CSVHeaders headersEvent2 = new CSVHeaders(888L, fields2);

        headersProcedure.process(headersEvent2);

        String expected =
                "line 777 header:\n" +
                        "  0: time(time:MM/dd/yy HH:mm:ss)\n" +
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

    @Test
    public void process_First() throws Exception {

        Headers procedure = new Headers(
                0, new ArrayList<>(Collections.singletonList("--first")), new ByteArrayOutputStream());

        assertTrue(procedure.isFirst());

        CSVHeaders e = new CSVHeaders(777L, new CSVFormat("time, A, B, C").getFields());

        assertFalse(procedure.isExitLoop());

        procedure.process(e);

        assertTrue(procedure.isExitLoop());

        String expected =
                "line 777 header:\n" +
                        "  0: time(time:MM/dd/yy HH:mm:ss)\n" +
                        "  2: A(string)\n" +
                        "  3: B(string)\n" +
                        "  4: C(string)\n";

        String actual = new String(((ByteArrayOutputStream) procedure.getOutputStream()).toByteArray());

        assertEquals(expected, actual);
    }

    @Test
    public void process_Last() throws Exception {

        Headers procedure = new Headers(
                0, new ArrayList<>(Collections.singletonList("--last")), new ByteArrayOutputStream());

        assertTrue(procedure.isLast());

        List<Event> events = Arrays.asList(

                new CSVHeaders(1L, new CSVFormat("A").getFields()),
                new TimedCSVLine(Arrays.asList(
                        new TimestampProperty(70L), new StringProperty("something", "something"))),
                new CSVHeaders(2L, new CSVFormat("B").getFields()),
                new TimedCSVLine(Arrays.asList(
                        new TimestampProperty(80L), new StringProperty("something", "something"))),
                new CSVHeaders(3L, new CSVFormat("C").getFields()),
                new TimedCSVLine(Arrays.asList(
                        new TimestampProperty(90L), new StringProperty("something", "something")))
        );

        for(Event e: events) {

            assertFalse(procedure.isExitLoop());
            procedure.process(e);
            assertFalse(procedure.isExitLoop());
            assertEquals(0, ((ByteArrayOutputStream) procedure.getOutputStream()).toByteArray().length);
        }

        procedure.process(new EndOfStreamEvent());

        assertTrue(procedure.isExitLoop());

        String expected = "line 3 header:\n  1: C(string)\n";

        String actual = new String(((ByteArrayOutputStream) procedure.getOutputStream()).toByteArray());

        assertEquals(expected, actual);
    }

    @Test
    public void process_RegularExpression_Match() throws Exception {

        Headers procedure = new Headers(
                0, new ArrayList<>(Collections.singletonList("black")), new ByteArrayOutputStream());

        CSVHeaders h = new CSVHeaders(1L, new CSVFormat("red, blue, black").getFields());

        procedure.process(h);

        String expected = "line 1 header:\n  3: black(string)\n";

        String actual = new String(((ByteArrayOutputStream) procedure.getOutputStream()).toByteArray());

        assertEquals(expected, actual);
    }

    @Test
    public void process_RegularExpression_NoMatch() throws Exception {

        Headers procedure = new Headers(
                0, new ArrayList<>(Collections.singletonList("black")), new ByteArrayOutputStream());

        CSVHeaders h = new CSVHeaders(1L, new CSVFormat("red, blue, green").getFields());

        procedure.process(h);

        String rendering = new String(((ByteArrayOutputStream) procedure.getOutputStream()).toByteArray());

        assertTrue(rendering.contains("line 1 header: no header name matched regular expression"));
        assertTrue(rendering.contains("'black'"));
    }

    @Test
    public void process_RegularExpression_ManyEvents() throws Exception {

        Headers procedure = new Headers(
                0, new ArrayList<>(Collections.singletonList("blue")), new ByteArrayOutputStream());

        List<Event> events = Arrays.asList(

                new CSVHeaders(1L, new CSVFormat("blue field, something red, this is also blue").getFields()),
                new TimedCSVLine(Arrays.asList(
                        new TimestampProperty(70L), new StringProperty("something", "something"))),
                new CSVHeaders(2L, new CSVFormat("green, yellow, black").getFields()),
                new TimedCSVLine(Arrays.asList(
                        new TimestampProperty(80L), new StringProperty("something", "something"))),
                new CSVHeaders(3L, new CSVFormat("red, blue, green").getFields()),
                new TimedCSVLine(Arrays.asList(
                        new TimestampProperty(90L), new StringProperty("something", "something")))
        );

        for(Event e: events) {

            procedure.process(e);
        }

        String expected =
                "line 1 header:\n  1: blue field(string)\n  3: this is also blue(string)\n" +
                "line 2 header: no header name matched regular expression 'blue'\n" +
                "line 3 header:\n  2: blue(string)\n";

        String actual = new String(((ByteArrayOutputStream) procedure.getOutputStream()).toByteArray());

        assertEquals(expected, actual);
    }

    @Test
    public void process_RegularExpression_FieldSpecificationDoesNotMatchButNameDoes() throws Exception {

        String regex = "red$";

        Headers procedure = new Headers(
                0, new ArrayList<>(Collections.singletonList(regex)), new ByteArrayOutputStream());

        CSVHeaders h = new CSVHeaders(1L, new CSVFormat("red").getFields());

        procedure.process(h);

        String expected = "line 1 header:\n  1: red(string)\n";

        String actual = new String(((ByteArrayOutputStream) procedure.getOutputStream()).toByteArray());

        assertEquals(expected, actual);
    }

    // toCorrespondingPropertyInfo() -----------------------------------------------------------------------------------

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
                new LongProperty(Event.LINE_PROPERTY_NAME, 1001L),
                new StringProperty("header_0", "time(time:long)"),
                new StringProperty("header_1", "name(string)"),
                new StringProperty("header_2", "counter(int)")
        );

        List<PropertyInfo> pis = Headers.toCorrespondingPropertyInfo(headerProperties);

        assertEquals(3, pis.size());

        PropertyInfo p = pis.get(0);
        assertEquals(0, p.getIndex());
        assertEquals("time", p.getPropertyName());
        assertEquals("time(time:long)", p.getFieldSpecification());

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
                new LongProperty(Event.LINE_PROPERTY_NAME, 1001L),
                new StringProperty("header_0", "itemid(string)"),
                new StringProperty("header_1", "ns(string)"),
                new StringProperty("header_2", "value(string)"),
                new StringProperty("header_3", "time(time:long)")
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
        assertEquals("time", p4.getPropertyName());
    }

    @Test
    public void toCorrespondingPropertyIndex_Production3() throws Exception {

        //
        // the timestamp property is present in the first column, and other properties follow
        //


        List<Property> headerProperties = Arrays.asList(
                new LongProperty(Event.LINE_PROPERTY_NAME, 1001L),
                new StringProperty("header_0", "itemid(string)"),
                new StringProperty("header_1", "ns(string)"),
                new StringProperty("header_2", "value(string)"),
                new StringProperty("header_3", "time(time:long)"),
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
        assertEquals("time", p4.getPropertyName());

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

    // setRegularExpressionLiteral() -----------------------------------------------------------------------------------

    @Test
    public void setRegularExpressionLiteral_Null() throws Exception {

        Headers h = getProcedureToTest();

        try {

            h.setRegularExpressionLiteral(null);

            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("null regular expression literal"));
        }
    }

    @Test
    public void setRegularExpressionLiteral_Invalid() throws Exception {

        Headers h = getProcedureToTest();

        try {

            h.setRegularExpressionLiteral("(");

            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("Unclosed group"));
        }
    }

    @Test
    public void setRegularExpressionLiteral_Valid() throws Exception {

        Headers h = getProcedureToTest();

        h.setRegularExpressionLiteral("^A.*Z$");

        assertEquals("^A.*Z$", h.getRegularExpressionLiteral());


    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected Headers getProcedureToTest() throws Exception {

        return new Headers(0, Collections.emptyList(), System.out);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
