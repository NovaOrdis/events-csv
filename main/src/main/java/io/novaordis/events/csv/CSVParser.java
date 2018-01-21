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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.novaordis.events.api.event.Event;
import io.novaordis.events.api.event.LongProperty;
import io.novaordis.events.api.event.Property;
import io.novaordis.events.api.event.PropertyFactory;
import io.novaordis.events.api.event.TimedEvent;
import io.novaordis.events.api.event.TimestampProperty;
import io.novaordis.events.api.parser.ParserBase;
import io.novaordis.events.csv.event.CSVEvent;
import io.novaordis.events.csv.event.CSVHeaders;
import io.novaordis.events.csv.event.NonTimedCSVLine;
import io.novaordis.events.csv.event.TimedCSVLine;
import io.novaordis.events.csv.event.field.CSVField;
import io.novaordis.events.query.Query;
import io.novaordis.utilities.parsing.ParsingException;
import io.novaordis.utilities.time.Timestamp;
import io.novaordis.utilities.time.TimestampImpl;

/**
 * A CSV parser.
 *
 * Works in three modes:
 *
 * 1. Introspection (no previously installed format).
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

    public static final char SEPARATOR = ',';

    private static final Logger log = LoggerFactory.getLogger(CSVParser.class);

    private static final List<Event> EMPTY_LIST = Collections.emptyList();

    private static final char HEADER_LEADER = '#';

    // Static ----------------------------------------------------------------------------------------------------------

    // Package Protected Static ----------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private CSVFormat format;

    private PropertyFactory propertyFactory;

    //
    // null most of the time, maintains the reference of the last header, but only until a new CSV line is encountered
    //
    private CSVHeaders header;

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

        this.propertyFactory = new PropertyFactory();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * May return null if no format was installed. CSVParser will still function and parse CSV lines based on
     * introspection.
     */
    public CSVFormat getFormat() {

        return format;
    }

    /**
     * Installs a format. May be null, in which case re-sets the state and remove a previously installed format, if any.
     */
    public void setFormat(CSVFormat format) {

        this.format = format;

        if (log.isDebugEnabled()) {

            log.debug(this + " installed format " +
                    (format == null ? "null" : format + ": \"" + format.toPattern() + "\""));
        }
    }

    @Override
    public String toString() {

        return "CSVParser[" + (format == null ? "HEURISTIC" : format) + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected List<Event> parse(long lineNumber, String line, Query query) throws ParsingException {

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

        //
        // look for header lines, they always start with the header leader
        //

        if (line.charAt(0) == HEADER_LEADER) {

            //
            // header
            //

            try {

                if (log.isDebugEnabled()) {

                    log.debug("detected header line");
                }

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

            CSVHeaders event = new CSVHeaders(lineNumber, format == null ? null : format.getFields());

            if (log.isDebugEnabled()) {

                log.debug(this + " is issuing a header event: " + event);
            }

            //
            // we dont return the header event just yet, we wait for the next event to try to figure out whether we can
            // extract a timestamp - if we can, we'll inject it into the header as "next-timed-event-timestamp"
            //

            this.header = event;

            return EMPTY_LIST;
        }

        //
        // regular CSV line (or empty line) - we proceed differently if a format instance is installed or not
        //

        final List<Property> properties = new ArrayList<>();

        properties.add(new LongProperty(Event.LINE_PROPERTY_NAME, lineNumber));

        //
        // we're prepared to handle the situation when no format was installed (no header line detected so far)
        //

        List<String> tokens = CSVTokenizer.split(lineNumber, line, SEPARATOR);

        final MutableBoolean timestampFound = new MutableBoolean(false);

        List<CSVField> headers = format == null ? null : format.getFields();

        int fieldCount = headers == null ? Integer.MAX_VALUE : headers.size();

        for(int i = 0; i < Math.min(fieldCount, tokens.size()); i ++) {

            String token = tokens.get(i);

            CSVField header = headers == null ? null : headers.get(i);

            buildAndStoreProperty(propertyFactory, token, i, header, timestampFound, properties);
        }

        CSVEvent dataLineEvent = propertyListToCSVEvent(timestampFound, properties);

        List<Event> result = new ArrayList<>(2);

        if (header != null) {

            //
            // inject the timestamp of this event into the header, so the header has this information
            //

            if (dataLineEvent.isTimed()) {

                long time = ((TimedEvent)dataLineEvent).getTime();
                header.setNextTimedEventTimestamp(time);
            }

            result.add(header);

            header = null;
        }

        result.add(dataLineEvent);

        return result;
    }

    @Override
    protected List<Event> close(long lineNumber) throws ParsingException {


        if (header == null) {

            return EMPTY_LIST;
        }

        List<Event> result = Collections.singletonList(header);

        header = null;

        return result;
    }

    // Static package protected ----------------------------------------------------------------------------------------

    /**
     * @param timestampFound the calling layer already has this information, so we use it to avoid a redundant loop over
     *                     properties.
     *
     * @param properties the properties to install into event. If the timestamp flag is true, then one of the
     *                   properties must be a timestamp property, otherwise the method will throw an
     *                   IllegalArgumentException.
     *
     * @exception IllegalArgumentException
     */
    static CSVEvent propertyListToCSVEvent(MutableBoolean timestampFound, List<Property> properties) {

        if (properties == null) {

            throw new IllegalArgumentException("null property list");
        }

        CSVEvent dataLineEvent;

        if (timestampFound.isTrue()) {

            dataLineEvent = new TimedCSVLine(properties);
        }
        else {

            dataLineEvent = new NonTimedCSVLine(properties);
        }

        return dataLineEvent;
    }

    /**
     * Builds the appropriate property instance and update the stack state.
     *
     * @param tok - may be null for "missing values".
     * @param header may be null if no format was installed.
     * @param timestampCreated a wrapper for a boolean that says whether the unique timestamp was already identified
     *                         or not. Updated by the method if the timestamp is identified.
     */
    static void buildAndStoreProperty(
            PropertyFactory propertyFactory, String tok, int index, CSVField header,
            MutableBoolean timestampCreated, List<Property> properties)
            throws ParsingException {

        //
        // do not trim, trimming is done by the upper layer
        //

        //
        // only the first timestamp header triggers timestamp creation
        //

        boolean mustBeTimestamp = header != null && header.isTimestamp() && timestampCreated.isFalse();

        Property p;

        if (mustBeTimestamp) {

            //
            // this is our timestamp, produce a timestamp property, and not a regular property
            //

            Timestamp t;

            try {

                t = new TimestampImpl(tok, (DateFormat)header.getFormat());
            }
            catch(Exception e) {

                throw new ParsingException(
                        "invalid timestamp value \"" + tok + "\", does not match the required timestamp format", e);
            }

            p = new TimestampProperty(t.getTime());

            timestampCreated.setTrue();
        }

        else if (header != null) {

            //
            // delegate the task of creating the corresponding property to the header
            //

            p = header.toProperty(tok);
        }
        else {

            //
            // no header, we use the type heuristics built into PropertyFactory
            //

            //noinspection UnnecessaryLocalVariable
            p = propertyFactory.createInstance(CSVEvent.GENERIC_FIELD_NAME_PREFIX + index, null, tok, null);

            if (p instanceof TimestampProperty) {

                if (timestampCreated.isTrue()) {

                    //
                    // more than one timestamp, how do we handle that?
                    //
                    throw new RuntimeException("NOT YET IMPLEMENTED");
                }
                else {

                    timestampCreated.setTrue();

                    TimestampProperty tp = (TimestampProperty)p;

                    //
                    // normalize timestamp property name, needed in case of CSV introspection
                    //

                    tp.setName(TimedEvent.TIME_PROPERTY_NAME);
                }
            }
        }

        properties.add(p);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
