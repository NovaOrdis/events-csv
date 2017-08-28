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
import io.novaordis.events.csv.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A timestamp CSV field.
 *
 * The name is conventionally use TimedEvent.TIMESTAMP_PROPERTY_NAME and the type is Long.
 *
 * The format is never null.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 2/6/16
 */
public class TimestampCSVField extends CSVFieldImpl {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public TimestampCSVField() {

        super(TimedEvent.TIMESTAMP_PROPERTY_NAME, Long.class, Constants.DEFAULT_TIMESTAMP_FORMAT);
    }

    public TimestampCSVField(String name) {

        super(name, Long.class, Constants.DEFAULT_TIMESTAMP_FORMAT);
    }

    public TimestampCSVField(SimpleDateFormat format) {

        super(TimedEvent.TIMESTAMP_PROPERTY_NAME, Long.class, format);
    }

    public TimestampCSVField(String name, SimpleDateFormat format) {

        super(name, Long.class, format);
    }

    // CSVFieldImpl overrides ------------------------------------------------------------------------------------------

    @Override
    public boolean isTimestamp() {

        return true;
    }

    @Override
    public SimpleDateFormat getFormat() {

        return (SimpleDateFormat)super.getFormat();
    }


    @Override
    public String getSpecification() {

        return getName() + typeToCommandLineLiteral(Date.class, getFormat());
    }

    @Override
    public String toString() {

        return getSpecification();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
