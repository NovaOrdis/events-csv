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

package io.novaordis.events.csv.event.field;

import io.novaordis.events.api.event.TimedEvent;
import io.novaordis.events.csv.CSVFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A static class that builds CSVField instances from their string field specification. The reverse operation
 * - obtaining the field specification from a CSVField instance is CSVField.getSpecification().
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/26/17
 */
public class CSVFieldFactory {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(CSVFieldFactory.class);

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * Convert a string CSV field specification into the corresponding CSVField instance. It is the inverse operation
     * of CSVField.getSpecification().
     *
     * @see CSVField#getSpecification()
     */
    public static CSVField fromSpecification(String fieldSpecification) throws CSVFormatException {

        if (fieldSpecification == null) {

            throw new IllegalArgumentException("null field specification");
        }

        String name;
        Class type;
        Format format = null;

        int leftParenthesis = fieldSpecification.indexOf('(');
        int rightParenthesis = fieldSpecification.indexOf(')');

        if (leftParenthesis == -1) {

            //
            // no type information
            //
            if (rightParenthesis != -1) {

                throw new CSVFormatException("unbalanced parentheses");
            }

            name = fieldSpecification;
            type = String.class;
        }
        else {

            //
            // there is type information
            //

            if (rightParenthesis == -1) {

                throw new CSVFormatException("unbalanced parentheses");
            }

            name = fieldSpecification.substring(0, leftParenthesis).trim();

            String typeSpecification = fieldSpecification.substring(leftParenthesis + 1, rightParenthesis);

            if ("string".equals(typeSpecification)) {

                type = String.class;
            }
            else if ("int".equals(typeSpecification)) {

                type = Integer.class;
            }
            else if ("long".equals(typeSpecification)) {

                type = Long.class;
            }
            else if ("float".equals(typeSpecification)) {

                type = Float.class;
            }
            else if ("double".equals(typeSpecification)) {

                type = Double.class;
            }
            else {

                //
                // we attempt conversion to a time specification, since the date format may come in various
                // shapes
                //

                format = parseTimeSpecification(typeSpecification);

                if (format != null) {

                    type = Date.class;
                }
                else {

                    throw new CSVFormatException("invalid field type specification \"" + typeSpecification + "\"");
                }
            }
        }

        if (TimedEvent.TIMESTAMP_PROPERTY_NAME.equals(name)) {

            TimestampCSVField f = new TimestampCSVField(name);

            if (format != null) {

                f.setFormat(format);
            }

            return f;
        }
        else {

            return new CSVFieldImpl(name, type, format);
        }
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    /**
     * Time specification is [time:]<simple-date-format>.
     *
     * Will return null if no valid date format is identified.
     *
     * If the "time:" literal is identified, but no format, a CSVFormatException is thrown.
     */
    static Format parseTimeSpecification(String timeSpecification) throws CSVFormatException {

        if (timeSpecification.startsWith("time")) {

            String s = timeSpecification.substring("time".length()).trim();

            if (s.isEmpty()) {

                throw new CSVFormatException("invalid time specification: missing format");
            }


            if (s.charAt(0) != ':') {

                throw new CSVFormatException("invalid time specification: missing ':'");
            }


            s = s.substring(1);

            if (s.isEmpty()) {

                throw new CSVFormatException("invalid time specification: missing format");
            }

            try {

                return new SimpleDateFormat(s);
            }
            catch(Exception e) {

                throw new CSVFormatException("invalid time specification: " + s, e);
            }
        }
        else {

            //
            // attempt to convert to an acceptable SimpleDateFormat
            //

            try {

                return new SimpleDateFormat(timeSpecification);
            }
            catch(Exception e) {

                //
                // conversion failed
                //

                if (log.isDebugEnabled()) {

                    log.debug("no valid time specification found in \"" + timeSpecification + "\"");
                }

                return null;
            }
        }
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
