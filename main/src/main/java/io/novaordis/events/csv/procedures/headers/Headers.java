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
import io.novaordis.events.api.event.Property;
import io.novaordis.events.csv.event.CSVHeaders;
import io.novaordis.events.processing.EventProcessingException;
import io.novaordis.events.processing.TextOutputProcedure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The default implementation displays all headers (^ *#.+) as they are identified in the CSV stream.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/7/17
 */
public class Headers extends TextOutputProcedure {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(Headers.class);

    public static final String COMMAND_LINE_LABEL = "headers";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean exitLoop;

    // Constructors ----------------------------------------------------------------------------------------------------

    public Headers(OutputStream os) {

        super(os);

        this.exitLoop = false;
    }

    // Procedure implementation ----------------------------------------------------------------------------------------

    @Override
    public List<String> getCommandLineLabels() {

        return Collections.singletonList(COMMAND_LINE_LABEL);
    }

    // ProcedureBase overrides -----------------------------------------------------------------------------------------

    @Override
    protected void process(AtomicLong invocationCount, Event e) throws EventProcessingException {

        if (log.isDebugEnabled()) {

            log.debug(this + " processing line " + invocationCount.get() + ": " + e);
        }

        if (!(e instanceof CSVHeaders)) {

            //
            // not a header line
            //
            return;
        }

        if (log.isDebugEnabled()) {

            log.debug(this + " identified CSV header event: " + e);
        }

        //exitLoop = true;

        CSVHeaders headers = (CSVHeaders)e;

        List<Property> properties = headers.getProperties();

        int width = 3 + (int)Math.log10(properties.size());
        int offset = 1;

        try {

            println("line " + e.getLineNumber() + " header:");

            for (Property p : properties) {

                //
                // properties such as line number property, and other, may be present, filter them out, only use
                // "header_" properties
                //

                String name = p.getName();

                if (!name.startsWith(CSVHeaders.HEADER_NAME_PREFIX)) {

                    continue;
                }

                //
                // we rely on the fact that the headers in the header line and the properties associated with entries
                // on the data line are recorded in the same sequence, so we use the header name index and we add
                // or subtract an offset
                //

                int propertyIndex = indexFromHeaderName(name) + offset;

                printf("%" + width + "s: ", propertyIndex);
                println(p.getValue());
            }
        }
        catch(IOException ioe) {

            throw new EventProcessingException(ioe);
        }
    }

    @Override
    public boolean isExitLoop() {

        return exitLoop;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected static ------------------------------------------------------------------------------------------------

    static int indexFromHeaderName(String headerName) {

        if (headerName == null) {

            throw new IllegalArgumentException("null header name");
        }

        if (!headerName.startsWith(CSVHeaders.HEADER_NAME_PREFIX)) {

            throw new IllegalArgumentException(
                    "header name does not start with a valid prefix ('" + CSVHeaders.HEADER_NAME_PREFIX + "')");
        }

        String s = headerName.substring(CSVHeaders.HEADER_NAME_PREFIX.length());

        try {

            return Integer.parseInt(s);
        }
        catch(NumberFormatException e) {

            throw new IllegalArgumentException("header name does not contain a valid integer index: " + s, e);
        }
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
