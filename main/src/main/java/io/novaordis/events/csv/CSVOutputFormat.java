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
import io.novaordis.events.csv.event.CSVHeaders;
import io.novaordis.events.processing.output.OutputFormat;

import java.text.DateFormat;

/**
 * Lightweight delegation to the "processing" package OutputFormatImpl. The delegate does all that common work, and
 * the wrapper introduces CSV-specific behavior.

 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/30/17
 */
public class CSVOutputFormat implements OutputFormat {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private OutputFormat delegate;

    // Constructors ----------------------------------------------------------------------------------------------------

    public CSVOutputFormat(OutputFormat delegate) {

        if (delegate == null) {

            throw new IllegalArgumentException("null delegate");
        }

        this.delegate = delegate;
    }

    // OutputFormat implementation -------------------------------------------------------------------------------------

    @Override
    public String formatHeader(Event e) {

        return delegate.formatHeader(e);
    }

    @Override
    public String format(Event e) {

        //
        // we avoid headers
        //

        if (e instanceof CSVHeaders) {

            return null;
        }

        return delegate.format(e);
    }

    @Override
    public String getSeparator() {

        return delegate.getSeparator();
    }

    @Override
    public DateFormat getTimestampFormat() {

        return delegate.getTimestampFormat();
    }

    @Override
    public void setTimestampFormat(DateFormat df) {

        delegate.setTimestampFormat(df);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public OutputFormat getDelegate() {

        return delegate;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
