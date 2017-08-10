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

package io.novaordis.events.csv.event;

import io.novaordis.events.api.event.GenericEvent;
import io.novaordis.events.api.event.Property;
import io.novaordis.events.api.event.TimedEvent;
import io.novaordis.events.api.parser.ParsingException;
import io.novaordis.events.csv.event.field.CSVField;

import java.util.Iterator;
import java.util.List;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/8/17
 */
public class CSVHeaders extends GenericEvent implements CSVEvent {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final String HEADER_NAME_PREFIX = "header_";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return the header fields, in order.
     */
    public List<CSVField> getFields() {

        throw new RuntimeException("NYE");
    }

    /**
     * Initializes the event from the content of a comma-separated header line. The header line must be stripped off of
     * its leading comment by the upper layer.
     */
    public void load(Long lineNumber, String commaSeparatedHeaderLine) throws ParsingException {

        List<String> tokens = CSVTokenizer.split(lineNumber, commaSeparatedHeaderLine, SEPARATOR);

        int index = 0;

        for(String token: tokens) {

            if (token == null) {

                throw new ParsingException("missing header", lineNumber);
            }
            else if (TimedEvent.TIMESTAMP_PROPERTY_NAME.equals(token)) {

                setStringProperty(HEADER_NAME_PREFIX + index, TimedEvent.TIMESTAMP_PROPERTY_NAME);
            }
            else {

                setStringProperty(HEADER_NAME_PREFIX + index, token);
            }

            index ++;
        }
    }

    @Override
    public String toString() {

        List<Property> properties = getProperties();

        String s = "";

        for(Iterator<Property> i = properties.iterator(); i.hasNext(); ) {

            Property p = i.next();

            s += p.getValue();

            if (i.hasNext()) {

                s += ", ";
            }
        }

        return s;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
