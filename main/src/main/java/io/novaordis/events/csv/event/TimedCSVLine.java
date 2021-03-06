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

import java.util.Date;
import java.util.List;

import io.novaordis.events.api.event.GenericTimedEvent;
import io.novaordis.events.api.event.Property;
import io.novaordis.events.api.event.TimedEvent;
import io.novaordis.events.csv.Constants;
import io.novaordis.events.csv.event.field.CSVFieldImpl;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/8/17
 */
public class TimedCSVLine extends GenericTimedEvent implements CSVEvent {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public TimedCSVLine(Long timestamp) {

        super(timestamp);
    }

    /**
     * One of these properties MUST be a TimestampProperty, otherwise the constructor will throw an
     * IllegalArgumentException.
     *
     * @param properties must contain at least one TimestampProperty.
     *
     * @exception IllegalArgumentException if the properties do not include a TimestampProperty.
     */
    public TimedCSVLine(List<Property> properties) {

        super(properties);
    }

    // Overrides -------------------------------------------------------------------------------------------------------

    /**
     * Display the value corresponding to all properties (including the line number, if available), in order they are
     * stored in the event representation. The line always starts with the timestamp.
     */
    @Override
    public String getPreferredRepresentation(String fieldSeparator) {

        String s = Constants.getDefaultTimestampFormat().format(getTime());

        boolean first = true;

        List<Property> properties = getProperties();

        for(int i = 1;  i < properties.size(); i ++) {

            Property p = properties.get(i);

            if (first) {

                s += fieldSeparator + " ";

                first = false;
            }

            s += p.getValue();

            if (i < properties.size() - 1) {

                s += fieldSeparator + " ";
            }
        }

        return s;
    }

    /**
     * Display the description of all properties (including the line number property, if available), in order they are
     * stored in the event representation. The line always starts with the timestamp representation.
     */
    @Override
    public String getPreferredRepresentationHeader(String fieldSeparator) {

        String s = TimedEvent.TIME_PROPERTY_NAME +
                CSVFieldImpl.typeToCommandLineLiteral(Date.class, Constants.getDefaultTimestampFormat());

        boolean first = true;

        List<Property> properties = getProperties();

        for(int i = 1;  i < properties.size(); i ++) {

            Property p = properties.get(i);

            if (first) {

                s += fieldSeparator + " ";

                first = false;
            }

            s += p.getName() + CSVFieldImpl.typeToCommandLineLiteral(p.getType(), p.getFormat());

            if (i < properties.size() - 1) {

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
