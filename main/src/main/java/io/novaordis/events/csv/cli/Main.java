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

import io.novaordis.events.cli.EventParserRuntime;
import io.novaordis.events.csv.CSVHeaderOutputStrategy;
import io.novaordis.events.csv.CSVParser;
import io.novaordis.events.csv.procedures.CSVProcedureFactory;
import io.novaordis.utilities.UserErrorException;
import io.novaordis.utilities.appspec.ApplicationSpecificBehavior;
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

            ApplicationSpecificBehavior apb = new ApplicationSpecificBehavior(
                    new CSVParser(),
                    new CSVProcedureFactory(),
                    new CSVHeaderOutputStrategy());

            EventParserRuntime runtime = new EventParserRuntime(args, APPLICATION_NAME, apb);

            if (runtime.getConfiguration().isHelp()) {

                displayHelpAndExit();
                return;
            }

            runtime.run();

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

    private static void displayHelpAndExit() throws UserErrorException {

        String content = InLineHelp.get();

        System.err.print(content);
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
