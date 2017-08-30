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

import io.novaordis.events.api.event.Event;
import io.novaordis.events.csv.event.NonTimedCSVLine;
import io.novaordis.events.csv.event.TimedCSVLine;
import io.novaordis.events.processing.output.HeaderOutputStrategy;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/30/17
 */
public class CSVHeaderOutputStrategy implements HeaderOutputStrategy {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean soleHeaderDisplayed = false;

    // Constructors ----------------------------------------------------------------------------------------------------

    // HeaderOutputStrategy implementation -----------------------------------------------------------------------------

    @Override
    public boolean shouldDisplayHeader(Event e) {

        if (soleHeaderDisplayed) {

            return false;
        }

        //
        // we ignore CSVHeader events and we extract the header info from the data events themselves.
        //

        return e instanceof TimedCSVLine || e instanceof NonTimedCSVLine;
    }

    @Override
    public void headerDisplayed(Event e) {

        soleHeaderDisplayed = true;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
