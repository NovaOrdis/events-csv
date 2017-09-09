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
import io.novaordis.events.csv.event.field.CSVFieldImpl;

import java.util.Iterator;
import java.util.List;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/8/17
 */
public class NonTimedCSVLine extends GenericEvent implements CSVEvent {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public NonTimedCSVLine() {

        this(null);
    }

    public NonTimedCSVLine(List<Property> properties) {

        super(properties);
    }

    // Overrides -------------------------------------------------------------------------------------------------------

    /**
     * Display the value corresponding to all properties (including the line number, if available), in order they are
     * stored in the event representation.
     */
    @Override
    public String getPreferredRepresentation(String fieldSeparator) {

        String s = "";

        for(Iterator<Property> pi = getProperties().iterator(); pi.hasNext(); ) {

            Property p = pi.next();

            s += p.getValue();

            if (pi.hasNext()) {

                s += fieldSeparator + " ";
            }
        }

        return s;
    }

    /**
     * Display the description of all properties (including the line number property, if available), in order they are
     * stored in the event representation.
     */
    @Override
    public String getPreferredRepresentationHeader(String fieldSeparator) {

        String s = "";

        for(Iterator<Property> pi = getProperties().iterator(); pi.hasNext(); ) {

            Property p = pi.next();

            s += p.getName() + CSVFieldImpl.typeToCommandLineLiteral(p.getType(), p.getFormat());

            if (pi.hasNext()) {

                s += fieldSeparator + " ";
            }
        }

        return s;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
