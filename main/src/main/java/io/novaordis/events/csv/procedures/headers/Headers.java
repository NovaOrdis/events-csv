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

        List<Property> properties = e.getProperties();

        int count = properties.size();
        int width = 5 - (int)Math.log10(count);

        try {

            for (Property p : properties) {

                //
                // properties such as line number property, and other, may be present, filter them out, only use
                // "header_" properties
                //

                String name = p.getName();

                if (!name.startsWith(CSVHeaders.HEADER_NAME_PREFIX)) {

                    continue;
                }

                String index = name.substring(CSVHeaders.HEADER_NAME_PREFIX.length());

                printf("%" + width + "s: ", index);
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

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
