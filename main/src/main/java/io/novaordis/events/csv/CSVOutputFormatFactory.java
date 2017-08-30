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

import io.novaordis.events.processing.output.DefaultOutputFormatFactory;
import io.novaordis.events.processing.output.OutputFormat;
import io.novaordis.events.processing.output.OutputFormatFactory;

import java.util.List;

/**
 * A lightweight factory that delegates all the relevant work to the "processing" DefaultOutputFormatFactory and simply
 * wraps the result into a CSVOutputFormat, which can then introduce CSV-specific behavior in top of the defaul
 * formatting.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/30/17
 */
public class CSVOutputFormatFactory implements OutputFormatFactory {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private DefaultOutputFormatFactory delegate;

    // Constructors ----------------------------------------------------------------------------------------------------

    public CSVOutputFormatFactory() {

        this.delegate = new DefaultOutputFormatFactory();
    }

    // OutputFormatFactory implementation ------------------------------------------------------------------------------

    @Override
    public CSVOutputFormat fromArguments(List<String> mutableCommandLineArguments) {

        OutputFormat delegate = this.delegate.fromArguments(mutableCommandLineArguments);

        //
        // wrap it in our own type
        //

        return new CSVOutputFormat(delegate);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public OutputFormatFactory getDelegate() {

        return delegate;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
