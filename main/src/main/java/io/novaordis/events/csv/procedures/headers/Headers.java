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
import io.novaordis.events.api.event.TimedEvent;
import io.novaordis.events.csv.event.CSVHeaders;
import io.novaordis.events.processing.EventProcessingException;
import io.novaordis.events.processing.TextOutputProcedure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
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

        List<Property> headersProperties = headers.getProperties();

        int width = 3 + (int)Math.log10(headersProperties.size());

        try {

            println("line " + e.getLineNumber() + " header:");

            List<PropertyInfo> propertyInfo =  toCorrespondingPropertyInfo(headersProperties);

            for (PropertyInfo pi : propertyInfo) {

                printf("%" + width + "s: ", pi.getIndex());
                println(pi.getFieldSpecification());
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

    /**
     * Given the complete list of header property, the method calculates the indexes that can be used to pull the
     * corresponding data properties from subsequent events associated with this header. We rely on the value of the
     * index encoded in the header name, and also on the fact that the headers and the data properties are stored in
     * the same order in their corresponding events.
     *
     * @exception IllegalArgumentException on various inconsistencies.
     */
    static List<PropertyInfo> toCorrespondingPropertyInfo(List<Property> headersProperties) {

        if (headersProperties == null) {

            throw new IllegalArgumentException("null header properties");
        }

        List<PropertyInfo> pis = new ArrayList<>();

        int i = -1;
        int offset = 0;
        int timestampIndex = -1;
        int effectiveIndex = 0;

        for(Property headerProperty: headersProperties) {

            i ++;

            String headerName = headerProperty.getName();

            if (!headerName.startsWith(CSVHeaders.HEADER_NAME_PREFIX)) {

                offset ++;

                continue;
            }

            String s = headerName.substring(CSVHeaders.HEADER_NAME_PREFIX.length());

            int index;

            try {

                index = Integer.parseInt(s);
            }
            catch (NumberFormatException e) {

                throw new IllegalArgumentException("header name does not contain a valid integer index: " + s, e);
            }

            String fieldSpecification = (String)headerProperty.getValue();
            String propertyName;

            int j = fieldSpecification.indexOf("(");

            if (j == -1) {

                propertyName = fieldSpecification;
            }
            else {

                propertyName = fieldSpecification.substring(0, j);
            }

            if (TimedEvent.TIMESTAMP_PROPERTY_NAME.equals(propertyName)) {

                //
                // timestamp property - it will aways be on the first position in storage in the data events
                //

                effectiveIndex = 0;
                timestampIndex = i;
                offset --;
            }
            else {

                effectiveIndex = offset + index;
            }

            PropertyInfo pi = new PropertyInfo(effectiveIndex, propertyName, fieldSpecification);
            pis.add(pi);
        }

        //
        // post-processing, if a timestamp header was encountered on another position but 0
        //

        if (timestampIndex > 0) {

            for(PropertyInfo pi: pis) {

                if (!pi.getPropertyName().equals(TimedEvent.TIMESTAMP_PROPERTY_NAME)) {

                    pi.incrementIndex();
                }
            }
        }

        return pis;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
