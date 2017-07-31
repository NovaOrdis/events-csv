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

package io.novaordis.events.csv.cli;

import io.novaordis.events.processing.Procedure;
import io.novaordis.utilities.UserErrorException;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/31/17
 */
public class Configuration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Procedure procedure;

    // Constructors ----------------------------------------------------------------------------------------------------

    public Configuration(String[] args) throws UserErrorException {

        procedure = new Help();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public Procedure getProcedure() {

        return procedure;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
