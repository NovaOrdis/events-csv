/*
 * Copyright (c) 2016 Nova Ordis LLC
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
import io.novaordis.events.api.event.GenericEvent;
import io.novaordis.events.api.event.GenericTimedEvent;
import io.novaordis.events.api.event.LongProperty;
import io.novaordis.events.api.event.Property;
import io.novaordis.events.api.parser.ParsingException;
import io.novaordis.utilities.time.Timestamp;
import io.novaordis.utilities.time.TimestampImpl;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 2/6/16
 */
public class CSVParser {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final String COMMA = ",";
    private static final String DOUBLE_QUOTE = "\"";

    // Static ----------------------------------------------------------------------------------------------------------

    // Package Protected Static ----------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private CSVFormat csvFormat;

    //
    // we maintain a header different from the line format because this allows us to discover the structure of a
    // CSV file dynamically, even without the presence of a line format specification
    //
    private List<CSVField> headers;

    private int timestampFieldIndex;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param formatSpecification: a CSVFormat specification.
     *
     * @throws IllegalArgumentException if the given format specification cannot be used to build a CSV format.
     *
     * @throws CSVFormatException we determined that the format specification <b>can</b> be used to build a CSV
     * format but we find an incorrectly specified field (example: invalid type, etc.)
     *
     * @see CSVFormat
     */
    public CSVParser(String formatSpecification) throws IllegalArgumentException, CSVFormatException {

        csvFormat = new CSVFormat(formatSpecification);

        int i = 0;
        timestampFieldIndex = -1;
        headers = new ArrayList<>();
        for(CSVField f: csvFormat.getFields()) {

            headers.add(f);

            //
            // the first "Date" fields will be used as timestamp
            //
            if (Date.class.equals(f.getType()) && timestampFieldIndex == -1) {
                // this is our timestamp
                timestampFieldIndex = i;
            }

            i++;
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public CSVFormat getFormat() {

        return csvFormat;
    }

    /**
     * @param lineNumber may be null, in case the line number information is not available.
     */
    public Event parse(Long lineNumber, String line) throws ParsingException {

        //
        // we ignore empty lines
        //
        if (line == null || line.trim().length() == 0) {
            return null;
        }

        Event event;

        if (timestampFieldIndex >= 0) {

            event = new GenericTimedEvent();
        }
        else {

            event = new GenericEvent();
        }

        event.setProperty(new LongProperty(Event.LINE_NUMBER_PROPERTY_NAME, lineNumber));

        int headerIndex = 0;

        String quotedField = null;
        String blank = null;

        for(StringTokenizer st = new StringTokenizer(line, ",\"", true);
            st.hasMoreTokens() && headerIndex < headers.size(); ) {

            String tok = st.nextToken();

            if (COMMA.equals(tok)) {

                if (blank != null) {

                    //
                    // empty field
                    //
                    insertNewEventProperty(event, headerIndex, blank);
                    headerIndex ++;
                    blank = null;
                }
                else if (quotedField != null) {

                    quotedField += tok;
                }

                continue;
            }

            if (DOUBLE_QUOTE.equals(tok)) {

                if (blank != null) {
                    //
                    // blank between comma and double quote, ignore it ...
                    //
                    blank = null;
                }

                if (quotedField == null) {
                    //
                    // start a quoted field
                    //
                    quotedField = "";
                    continue;
                }
                else {
                    //
                    // end a quoted field
                    //
                    insertNewEventProperty(event, headerIndex, quotedField);
                    headerIndex ++;
                    quotedField = null;
                    continue;
                }
            }

            if (quotedField != null) {

                //
                // append to the quoted field, do not insert a property yet
                //
                quotedField += tok;
                continue;
            }

            if (tok.trim().length() == 0) {

                blank = tok;
                continue;
            }

            insertNewEventProperty(event, headerIndex, tok);
            headerIndex ++;
        }

        return event;
    }

    @Override
    public String toString() {

        return "CSVParser[format: " + csvFormat + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    List<CSVField> getHeaders() {
        return headers;
    }

    int getTimestampFieldIndex() {
        return timestampFieldIndex;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private void insertNewEventProperty(Event event, int headerIndex, String tok) throws ParsingException {

        tok = tok.trim();

        CSVField header = headers.get(headerIndex);

        if (headerIndex == timestampFieldIndex) {

            //
            // this is our timestamp, set the timed event timestamp, and not a regular property
            //

            Timestamp t;

            try {

                t = new TimestampImpl(tok, (DateFormat)header.getFormat());
            }
            catch(Exception e) {

                throw new ParsingException(
                        "invalid timestamp value \"" + tok + "\", does not match the required timestamp format", e);
            }

            ((GenericTimedEvent)event).setTimestamp(t);
        }
        else {

            Property p = header.toProperty(tok);
            event.setProperty(p);
        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
