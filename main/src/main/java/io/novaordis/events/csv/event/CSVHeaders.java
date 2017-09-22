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
import io.novaordis.events.api.event.LongProperty;
import io.novaordis.events.api.event.Property;
import io.novaordis.events.csv.CSVFormatException;
import io.novaordis.events.csv.event.field.CSVField;
import io.novaordis.events.csv.event.field.CSVFieldFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A non-timed event that signals a header change in an event stream generated from CSV text.
 *
 * The headers are maintained as StringProperty instances, whose names are indexed "header_" strings, and values are
 * the string specification of the corresponding CSVField.
 *
 * For more details see https://kb.novaordis.com/index.php/Events-csv_Concepts#Headers
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/8/17
 */
public class CSVHeaders extends GenericEvent implements CSVEvent {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final String HEADER_NAME_PREFIX = "header_";
    public static final String NEXT_TIMED_EVENT_TIMESTAMP_PROPERTY_NAME = "next-timed-event-timestamp";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public CSVHeaders() {

        this(null, null);
    }

    /**
     * @param lineNumber may be null.
     *
     * @param csvFields may be null.
     */
    public CSVHeaders(Long lineNumber, List<CSVField> csvFields) {

        super(lineNumber);

        if (csvFields != null) {

            setCSVFields(csvFields);
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * The implementation recreates the CSVFields from their internal representation and may throw an
     * IllegalStateException if restoring the fields fails.
     *
     * @return the header fields, in order.
     *
     * @exception IllegalStateException if restoration of the CSV fields from internal representation fails.
     */
    public List<CSVField> getFields() throws IllegalStateException {

        //
        // recreate the CSVField instances from the property content
        //

        List<CSVField> result = new ArrayList<>();

        List<Property> properties = getProperties();

        int nextIndex = 0;

        for(Property p: properties) {

            String name = p.getName();

            if (name.startsWith(HEADER_NAME_PREFIX)) {

                String index = name.substring(HEADER_NAME_PREFIX.length());

                int i;

                try {

                    i = Integer.parseInt(index);
                }
                catch(Exception e) {

                    throw new IllegalStateException("invalid header property name: " + name);
                }

                if (nextIndex != i) {

                    throw new IllegalStateException("CSV header out of sequence: " + name);
                }

                Object o = p.getValue();

                if (o == null) {

                    throw new IllegalStateException("null CSV header specification");
                }

                String csvFieldSpecification = o.toString();

                try {

                    CSVField f = CSVFieldFactory.fromSpecification(csvFieldSpecification);

                    result.add(f);
                }
                catch(CSVFormatException e) {

                    throw new IllegalStateException("invalid CSV header specification: " + csvFieldSpecification, e);
                }

                nextIndex ++;
            }
        }

        return result;
    }

    /**
     * @return the next timed event timestamp, in millisecond POSIX time, or null, if the information is not available.
     */
    public Long getNextTimedEventTimestamp() {

        LongProperty p = getLongProperty(NEXT_TIMED_EVENT_TIMESTAMP_PROPERTY_NAME);

        if (p == null) {

            return null;
        }

        return p.getLong();
    }

    public void setNextTimedEventTimestamp(Long l) {

        setLongProperty(NEXT_TIMED_EVENT_TIMESTAMP_PROPERTY_NAME, l);
    }

    @Override
    public String toString() {

        List<Property> properties = getProperties();

        if (properties == null) {

            return "UNINITIALIZED";
        }

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

    void setCSVFields(List<CSVField> csvFields) {

        int index = 0;

        for (CSVField f : csvFields) {

            if (f == null) {

                throw new IllegalArgumentException("null CSV field on position " + index);
            }

            String propertyName = HEADER_NAME_PREFIX + index;
            String csvFieldSpecification = f.getSpecification();
            setStringProperty(propertyName, csvFieldSpecification);
            index++;
        }
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
