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
import io.novaordis.utilities.help.InLineHelp;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/31/17
 */
public class Main {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final String APPLICATION_NAME = "csv";

    // Static ----------------------------------------------------------------------------------------------------------

    public static void main(String[] args) throws Exception {


        try {

            Configuration c = new Configuration(args);

            Procedure p = c.getProcedure();

            if (p instanceof Help) {

                String s = InLineHelp.get(APPLICATION_NAME);
                System.out.println(s);
            }


        }
        catch(UserErrorException e) {

            System.err.println(e.getMessage());
        }

    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
