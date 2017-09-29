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

package io.novaordis.events.csv;

import java.text.SimpleDateFormat;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/26/17
 */
public class Constants {

    // Constants -------------------------------------------------------------------------------------------------------

    //
    // MM/dd/yy HH:mm:ss (07/25/16 14:00:00) is the default time format so it works straight away with Excel
    //

    public static final String DEFAULT_TIMESTAMP_FORMAT_LITERAL = "MM/dd/yy HH:mm:ss";

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * We create the instance every time we need it instead of relying on a constant because SimpleDateFormat is not
     * thread safe.
     */
    public static SimpleDateFormat getDefaultTimestampFormat() {

        return new SimpleDateFormat(DEFAULT_TIMESTAMP_FORMAT_LITERAL);
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    private Constants() {
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------


    // Inner classes ---------------------------------------------------------------------------------------------------

}
