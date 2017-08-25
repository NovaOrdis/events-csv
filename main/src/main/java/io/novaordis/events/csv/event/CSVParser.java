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

package io.novaordis.events.csv.event;

import io.novaordis.events.api.event.Event;
import io.novaordis.events.api.event.GenericEvent;
import io.novaordis.events.api.event.GenericTimedEvent;
import io.novaordis.events.api.event.LongProperty;
import io.novaordis.events.api.event.Property;
import io.novaordis.events.api.parser.ParserBase;
import io.novaordis.events.api.parser.ParsingException;
import io.novaordis.events.csv.CSVFormat;
import io.novaordis.events.csv.CSVFormatException;
import io.novaordis.events.csv.event.field.CSVField;
import io.novaordis.events.csv.event.field.TimestampCSVField;
import io.novaordis.utilities.time.Timestamp;
import io.novaordis.utilities.time.TimestampImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 * A CSV parser.
 *
 * Works in three modes:
 *
 * 1. Introspection.
 * 2. Format-driven.
 * 3. Combined: detects header lines and adjusts internal format specification based on the header line content.
 *
 * The parser can dynamically switch from introspection mode to format-driven mode if it encounters a header line.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 2/6/16
 */
public class CSVParser extends ParserBase {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(CSVParser.class);

    private static final String COMMA = ",";
    private static final String DOUBLE_QUOTE = "\"";

    private static final List<Event> EMPTY_LIST = Collections.emptyList();

    private static final char HEADER_LEADER = '#';

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
     * A CSV parser that relies on instrospection and not on an externally specified format.
     *
     * @throws IllegalArgumentException
     * @throws CSVFormatException
     */
    public CSVParser() throws IllegalArgumentException, CSVFormatException {

        this(null);
    }

    /**
     * @param formatSpecification: a CSVFormat specification. May be null, in which case the parser will be
     *                           "format-less".
     *
     * @throws IllegalArgumentException if the given format specification cannot be used to build a CSV format.
     *
     * @throws CSVFormatException we determined that the format specification <b>can</b> be used to build a CSV
     * format but we find an incorrectly specified field (example: invalid type, etc.)
     *
     * @see CSVFormat
     */
    public CSVParser(String formatSpecification) throws IllegalArgumentException, CSVFormatException {

        if (formatSpecification != null) {

            CSVFormat f = new CSVFormat(formatSpecification);
            setFormat(f);
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * May return null if no format was installed. CSVParser will still function and parse CSV lines based on
     * introspection.
     */
    public CSVFormat getFormat() {

        return csvFormat;
    }

    /**
     * Installs a format. May be null, in which case re-sets the state and remove a previously installed format, if any.
     */
    public void setFormat(CSVFormat format) {

        if (log.isDebugEnabled()) {

            log.debug(this + " is installing CSV format \"" + format + "\"");
        }

        if (format == null) {

            timestampFieldIndex = -1;
            headers = null;
            csvFormat = null;
        }
        else {

            int i = 0;

            timestampFieldIndex = -1;

            headers = new ArrayList<>();

            for (CSVField f : format.getFields()) {

                headers.add(f);

                //
                // the first "Date" fields will be used as timestamp
                //
                if ((Date.class.equals(f.getType()) || f instanceof TimestampCSVField) && timestampFieldIndex == -1) {

                    //
                    // this is our timestamp
                    //
                    timestampFieldIndex = i;
                }

                i++;
            }

            csvFormat = format;
        }

    }

    @Override
    public String toString() {

        return "CSVParser[" + (csvFormat == null ? "NO FORMAT" : csvFormat) + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    /**
     * May return null if no headers were previously installed.
     */

    List<CSVField> getHeaders() {

        return headers;
    }

    /**
     * @return may return -1 if no timestamp field is known.
     */
    int getTimestampFieldIndex() {

        return timestampFieldIndex;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected List<Event> parse(long lineNumber, String line) throws ParsingException {

        if (line == null) {

            return EMPTY_LIST;

        }

        //
        // blank edges are ignored
        //

        line = line.trim();

        //
        // we ignore empty lines
        //
        if (line.isEmpty()) {

            return EMPTY_LIST;
        }

        List<Event> result;

        //
        // look for header lines, they always start with the header leader
        //

        if (line.charAt(0) == HEADER_LEADER) {

            //
            // header
            //

            try {

                CSVFormat f = new CSVFormat(line.substring(1));

                //
                // install the format ...
                //

                setFormat(f);
            }
            catch(CSVFormatException e) {

                throw new ParsingException(lineNumber, e);
            }

            //
            // ... and then issue the header
            //

            CSVHeaders event = new CSVHeaders(lineNumber, headers);

            if (log.isDebugEnabled()) {

                log.debug(this + " is issuing a header event: " + event);
            }

            result = Collections.singletonList(event);
        }
        else {

            Event event;

            if (timestampFieldIndex >= 0) {

                event = new GenericTimedEvent();
            } else {

                event = new GenericEvent();
            }

            event.setProperty(new LongProperty(Event.LINE_NUMBER_PROPERTY_NAME, lineNumber));

            int headerIndex = 0;

            String quotedField = null;
            String blank = null;

            for (StringTokenizer st = new StringTokenizer(line, ",\"", true);
                 st.hasMoreTokens() && headerIndex < headers.size(); ) {

                String tok = st.nextToken();

                if (COMMA.equals(tok)) {

                    if (blank != null) {

                        //
                        // empty field
                        //
                        insertNewEventProperty(event, headerIndex, blank);
                        headerIndex++;
                        blank = null;
                    } else if (quotedField != null) {

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
                    } else {
                        //
                        // end a quoted field
                        //
                        insertNewEventProperty(event, headerIndex, quotedField);
                        headerIndex++;
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
                headerIndex++;
            }

            result = Collections.singletonList(event);
        }

        return result;

    }

    @Override
    protected List<Event> close(long lineNumber) throws ParsingException {

        //
        // since we are strictly line-based, there's nothing to close
        //

        return Collections.emptyList();
    }

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
