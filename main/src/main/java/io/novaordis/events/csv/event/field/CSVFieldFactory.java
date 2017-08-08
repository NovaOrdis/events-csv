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

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/26/17
 */
public class CSVFieldFactory {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    public static CSVField fromFieldSpecification(String fieldSpecification) throws CSVFormatException {

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
            else if (typeSpecification.startsWith("time")) {

                type = Date.class;
                format = parseTimeSpecification(typeSpecification);
            }
            else {

                throw new CSVFormatException("invalid field type specification \"" + typeSpecification + "\"");
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
     * May return null if the format is not explicitly specified.
     */
    static Format parseTimeSpecification(String timeSpecification) throws CSVFormatException {

        //
        // must start with "time"
        //

        if (!timeSpecification.startsWith("time")) {

            throw new IllegalArgumentException("invalid time specification " + timeSpecification);
        }

        timeSpecification = timeSpecification.substring("time".length());

        if (timeSpecification.isEmpty()) {

            return null;
        }

        if (!timeSpecification.startsWith(":")) {

            throw new CSVFormatException("invalid time specification \"" + timeSpecification + "\", missing ':'");
        }

        timeSpecification = timeSpecification.substring(1);

        try {

            return new SimpleDateFormat(timeSpecification);
        }
        catch(Exception e) {

            throw new CSVFormatException("invalid timestamp format \"" + timeSpecification + "\"", e);
        }
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
