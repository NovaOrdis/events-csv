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

package io.novaordis.events.csv.procedures.headers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.novaordis.events.api.event.EndOfStreamEvent;
import io.novaordis.events.api.event.Event;
import io.novaordis.events.api.event.Property;
import io.novaordis.events.api.event.TimedEvent;
import io.novaordis.events.csv.Constants;
import io.novaordis.events.csv.event.CSVHeaders;
import io.novaordis.events.processing.EventProcessingException;
import io.novaordis.events.processing.TextOutputProcedure;
import io.novaordis.utilities.UserErrorException;

/**
 * The default implementation displays all headers (^ *#.+) as they are identified in the CSV stream.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/7/17
 */
public class Headers extends TextOutputProcedure {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(Headers.class);

    public static final String[] COMMAND_LINE_LABELS = { "header", "headers" };

    public static final String FIRST_COMMAND_LINE_MODIFIER = "--first";
    public static final String LAST_COMMAND_LINE_MODIFIER = "--last";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean exitLoop;

    //
    // whether the procedure was configured to render only the first encountered header
    //

    private boolean first;

    //
    // whether the procedure was configured to render only the last encountered header
    //

    private boolean last;

    // maintains the last seen header, if we need it
    private CSVHeaders lastHeader;

    private String regularExpressionLiteral;
    private Pattern regularExpressionPattern;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * Instantiates and configures the procedure based on the command line content.
     *
     * @param from the index of the first argument to be examined in the argument list. All arguments with an index
     *             equal to 'from' and higher can be interpreted as procedure arguments. The arguments that are
     *             recognized as procedure arguments must be removed from the argument list, otherwise they may confuse
     *             other subsystems that get to process the argument list after this.
     *
     * @param commandLineArguments the command line argument list. The list may contain possible arguments for the
     *                             procedure. The list must be mutable. The arguments that are recognized as procedure
     *                             arguments are removed from the list, otherwise they may confuse other subsystems that
     *                             get to process the argument list after this.
     *
     * @exception UserErrorException on command line configuration errors.
     */
    public Headers(int from, List<String> commandLineArguments, OutputStream os) throws UserErrorException {

        super(os);

        this.exitLoop = false;
        this.first = false;
        this.last = false;

        configure(from, commandLineArguments);
    }

    // Procedure implementation ----------------------------------------------------------------------------------------

    @Override
    public List<String> getCommandLineLabels() {

        return Arrays.asList(COMMAND_LINE_LABELS);
    }

    // ProcedureBase overrides -----------------------------------------------------------------------------------------

    @Override
    protected void process(AtomicLong invocationCount, Event e) throws EventProcessingException {

        if (log.isDebugEnabled()) {

            log.debug(this + " processing line " + invocationCount.get() + ": " + e);
        }

        if (e instanceof EndOfStreamEvent) {

            //
            // this has a special significance when "last" is in effect, we need to render the last identified header
            //

            render(lastHeader);

            //
            // this is somewhat superfluous but we want follow conventions
            //
            exitLoop = true;

            return;
        }

        if (!(e instanceof CSVHeaders)) {

            //
            // not a header line
            //
            return;
        }

        if (log.isDebugEnabled()) {

            log.debug(this + " identified CSV header event: " + e);
        }

        CSVHeaders h = (CSVHeaders) e;

        if (first) {

            render(h);

            //
            // exit the loop
            //
            exitLoop = true;
        }
        else if (last) {

            //
            // keep the last header and only render it when we're at the end of the stream, but don't render
            //

            lastHeader = h;
        }
        else {

            render(h);
        }
    }

    @Override
    public boolean isExitLoop() {

        return exitLoop;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return true if the procedure was configured to render only the first encountered header.
     */
    public boolean isFirst() {

        return first;
    }

    /**
     * @return true if the procedure was configured to render only the last encountered header.
     */
    public boolean isLast() {

        return last;
    }

    /**
     * @return the regular expression literal, if configured, or null. Once configured, it is guaranteed to be
     * a valid regular expression
     */
    public String getRegularExpressionLiteral() {

        return regularExpressionLiteral;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    /**
     * @exception IllegalArgumentException on null literal
     * @exception PatternSyntaxException on invalid regular expression.
     */
    void setRegularExpressionLiteral(String regexLiteral) {

        if (regexLiteral == null) {

            throw new IllegalArgumentException("null regular expression literal");
        }

        this.regularExpressionPattern = Pattern.compile(regexLiteral);
        this.regularExpressionLiteral = regexLiteral;
    }

    /**
     * May return null if no regular expression was installed.
     */
    Pattern getRegularExpressionPattern() {

        return regularExpressionPattern;
    }

    // Protected static ------------------------------------------------------------------------------------------------

    /**
     * Given the complete list of header property, the method calculates the indexes that can be used to pull the
     * corresponding data properties from subsequent events associated with this header. We rely on the value of the
     * index encoded in the header name, and also on the fact that the headers and the data properties are stored in
     * the same order in their corresponding events.
     *
     * @exception IllegalArgumentException on various inconsistencies.
     */
    static List<PropertyInfo> toCorrespondingPropertyInfo(List<Property> headersProperties) {

        if (headersProperties == null) {

            throw new IllegalArgumentException("null header properties");
        }

        List<PropertyInfo> pis = new ArrayList<>();

        int i = -1;
        int offset = 0;
        int timestampIndex = -1;
        int effectiveIndex;

        for(Property headerProperty: headersProperties) {

            i ++;

            String headerName = headerProperty.getName();

            if (!headerName.startsWith(CSVHeaders.HEADER_NAME_PREFIX)) {

                offset ++;

                continue;
            }

            String s = headerName.substring(CSVHeaders.HEADER_NAME_PREFIX.length());

            int index;

            try {

                index = Integer.parseInt(s);
            }
            catch (NumberFormatException e) {

                throw new IllegalArgumentException("header name does not contain a valid integer index: " + s, e);
            }

            String fieldSpecification = (String)headerProperty.getValue();
            String propertyName;

            int j = fieldSpecification.indexOf("(");

            if (j == -1) {

                propertyName = fieldSpecification;
            }
            else {

                propertyName = fieldSpecification.substring(0, j);
            }

            if (TimedEvent.TIMESTAMP_PROPERTY_NAME.equals(propertyName)) {

                //
                // timestamp property - it will aways be on the first position in storage in the data events
                //

                effectiveIndex = 0;
                timestampIndex = i;
                offset --;
            }
            else {

                effectiveIndex = offset + index;
            }

            PropertyInfo pi = new PropertyInfo(effectiveIndex, propertyName, fieldSpecification);
            pis.add(pi);
        }

        //
        // post-processing, if a timestamp header was encountered on another position but 0
        //

        if (timestampIndex > 0) {

            //noinspection Convert2streamapi
            for(PropertyInfo pi: pis) {

                if (!pi.getPropertyName().equals(TimedEvent.TIMESTAMP_PROPERTY_NAME)) {

                    pi.incrementIndex();
                }
            }
        }

        return pis;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    /**
     * @param from the index of the first argument to be examined in the argument list. All arguments with an index
     *             equal to 'from' and higher can be interpreted as procedure arguments. The arguments that are
     *             recognized as procedure arguments must be removed from the argument list, otherwise they may confuse
     *             other subsystems that get to process the argument list after this.
     *
     * @param commandLineArguments the command line argument list. The list may contain possible arguments for the
     *                             procedure. The list must be mutable. The arguments that are recognized as procedure
     *                             arguments are removed from the list, otherwise they may confuse other subsystems that
     *                             get to process the argument list after this.
     *
     * @exception UserErrorException on command line configuration errors.
     */
    private void configure(int from, List<String> commandLineArguments) throws UserErrorException {

        if (commandLineArguments == null) {

            throw new IllegalArgumentException("null command line argument list");
        }

        for(int i = from; i < commandLineArguments.size(); i ++) {

            String arg = commandLineArguments.get(i);

            if (FIRST_COMMAND_LINE_MODIFIER.equals(arg)) {

                this.first = true;
                commandLineArguments.remove(i --);
            }
            else if (LAST_COMMAND_LINE_MODIFIER.equals(arg)) {

                this.last = true;
                commandLineArguments.remove(i --);
            }
            else if (regularExpressionLiteral == null) {

                try {

                    setRegularExpressionLiteral(arg);
                    commandLineArguments.remove(i--);
                }
                catch(PatternSyntaxException e) {

                    String msg = "invalid regular expression: '" + arg + "'";

                    String msg2 = e.getMessage();

                    if (msg2 != null) {

                        msg += ": " + msg2;
                    }

                    throw new UserErrorException(msg);
                }
            }
        }

        if (first && last) {

            throw new UserErrorException(
                    FIRST_COMMAND_LINE_MODIFIER + " and " + LAST_COMMAND_LINE_MODIFIER +
                            " cannot be used at the same time");
        }
    }

    /**
     * Sends the header rendering to the output stream. If the last header is null, should be a noop
     */
    private void render(CSVHeaders h) throws EventProcessingException{

        if (h == null) {

            //
            // noop
            //
            return;
        }

        List<Property> headersProperties = h.getProperties();

        int width = 3 + (int)Math.log10(headersProperties.size());

        try {

            String prefixLine = "line " + h.getLineNumber() + " header";

            Long nextTimedEventTimestamp = h.getNextTimedEventTimestamp();

            if (nextTimedEventTimestamp != null) {

                prefixLine +=
                        ", applies to events recorded on " +
                                Constants.getDefaultTimestampFormat().format(nextTimedEventTimestamp) +
                                " and after:";
            }
            else {

                prefixLine += ":";
            }

            print(prefixLine);

            List<PropertyInfo> propertyInfo =  toCorrespondingPropertyInfo(headersProperties);

            boolean first = true;
            boolean matched = false;

            for (PropertyInfo pi : propertyInfo) {

                String fieldName = pi.getPropertyName();

                //
                // if there's a regular expression installed, check whether it matches and skip headers that don't;
                // note that the field name, and not the field specification is matched
                //

                if (regularExpressionPattern != null) {

                    if (!regularExpressionPattern.matcher(fieldName).find()) {

                        continue;
                    }
                }

                if (first) {

                    first = false;
                    println();

                }

                matched = true;
                printf("%" + width + "s: ", pi.getIndex());
                println(pi.getFieldSpecification());
            }

            if (!matched) {

                //
                // the user may be wondering why they don't see anything, so make it clear why
                //

                println(" no header name matched regular expression '" + regularExpressionLiteral + "'");
            }
        }
        catch(IOException ioe) {

            throw new EventProcessingException(ioe);
        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
