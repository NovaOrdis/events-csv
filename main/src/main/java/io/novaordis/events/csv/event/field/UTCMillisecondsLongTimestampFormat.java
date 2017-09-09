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

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;

/**
 * A format used to convert back and forth timestamps and their representation as UTC milliseconds longs.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/8/17
 */
public class UTCMillisecondsLongTimestampFormat extends DateFormat {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Format overrides ------------------------------------------------------------------------------------------------

    @Override
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        throw new RuntimeException("format() NOT YET IMPLEMENTED");
    }

    @Override
    public Date parse(String source, ParsePosition pos) {

        Date result = null;

        try {

            long l = Long.parseLong(source);

            result = new Date(l);

            pos.setIndex(source.length());
        }
        catch (Exception e) {

            //
            // noop - leaving pos unchange will trigger ParseException to be thrown by the upper layer
            //
        }

        return result;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
