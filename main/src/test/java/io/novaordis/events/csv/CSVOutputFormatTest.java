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

import io.novaordis.events.api.event.StringProperty;
import io.novaordis.events.api.event.TimestampProperty;
import io.novaordis.events.csv.event.CSVHeaders;
import io.novaordis.events.csv.event.TimedCSVLine;
import io.novaordis.events.csv.event.field.CSVFieldImpl;
import io.novaordis.events.processing.output.OutputFormatImpl;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/30/17
 */
public class CSVOutputFormatTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // constructor -----------------------------------------------------------------------------------------------------

    @Test
    public void constructor_NullDelegate() throws Exception {

        try {

            new CSVOutputFormat(null);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("null delegate"));
        }
    }

    @Test
    public void format_CSVHeaders() throws Exception {

        OutputFormatImpl delegate = new OutputFormatImpl(0);

        CSVOutputFormat f = new CSVOutputFormat(delegate);

        //
        // insure we avoid CSVHeaders
        //

        CSVHeaders h = new CSVHeaders(7L, Collections.singletonList(new CSVFieldImpl("A", String.class)));

        String hs = f.format(h);

        assertNull(hs);

        TimedCSVLine e = new TimedCSVLine(Arrays.asList(new TimestampProperty(9L), new StringProperty("A", "a value")));

        String es = f.format(e);

        assertNotNull(es);

        assertTrue(es.contains("a value"));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
