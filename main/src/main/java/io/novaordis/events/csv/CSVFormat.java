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

import io.novaordis.events.api.event.TimedEvent;
import io.novaordis.events.api.metric.MetricDefinition;
import io.novaordis.utilities.parsing.ParsingException;
import io.novaordis.events.csv.event.field.CSVField;
import io.novaordis.events.csv.event.field.CSVFieldFactory;
import io.novaordis.events.csv.event.field.CSVFieldImpl;
import io.novaordis.events.csv.event.field.MetricDefinitionBasedCSVField;
import io.novaordis.events.csv.event.field.TimestampCSVField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The class encapsulates a CSV line format specification. Instances of this class are used by CSV parsers, which
 * turn CSV text lines into events, or CSV formatters, which turn events into CSV text lines. The parsing/formatting
 * is an external concern, this class' only responsibility is to maintain the format information in a way that is as
 * flexible and easy to use as possible.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 2/6/16
 */
public class CSVFormat {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(CSVFormat.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private int unnamedFieldCounter = 0;

    private List<CSVField> fields;

    // Constructors ----------------------------------------------------------------------------------------------------

    public CSVFormat() {

        fields = new ArrayList<>();
    }

    /**
     * NOTE: it does not contain logic to identify quoted tokens.
     *
     * @param formatSpecification - a comma-separated field specifications. Null is valid, and it will result in an
     *                            empty CSVFormat instance.
     *
     * @see CSVFieldImpl
     *
     * @throws IllegalArgumentException if the given format specification cannot be used to build a CSV format.
     *
     * @throws CSVFormatException we determined that the format specification <b>can</b> be used to build a CSV
     * format but we find an incorrectly specified field (example: invalid type, etc.)
     */
    public CSVFormat(String formatSpecification) throws IllegalArgumentException, CSVFormatException {

        this();

        if (formatSpecification == null) {

            return;
        }

        List<String> tokens;

        try {

            tokens = CSVTokenizer.split(null, formatSpecification, CSVParser.SEPARATOR);
        }
        catch(ParsingException e) {

            throw new CSVFormatException(e);
        }

        int i = 0;
        for(Iterator<String> ti = tokens.iterator(); ti.hasNext(); i ++) {

            String fieldSpecification = ti.next();

            if (fieldSpecification == null) {

                if (ti.hasNext()) {

                    //
                    // it does not make sense to specify a null header - or at least for the time being
                    // TODO: in the future we may want to allow for field placeholders, where we only want to
                    // express there's a field there, but we don't care about the name or the type
                    //
                    throw new CSVFormatException("invalid CSV format specification: field " + i + " null");
                }
                else {

                    //
                    // for convenience, ignore the last missing field, the situation is similar to allowing a comma after
                    // the last element of an array
                    //
                    break;
                }
            }

            CSVField field = CSVFieldFactory.fromSpecification(fieldSpecification);

            if (field.getName().length() == 0) {

                ((CSVFieldImpl)field).setName(nextUnnamedFieldName());
            }

            fields.add(field);
        }

        if (log.isDebugEnabled()) {

            log.debug(this + " constructed");
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * Add a field, extracted from its field specification.
     *
     * @see CSVFieldImpl
     */
    public void addField(String fieldSpecification) throws CSVFormatException {

        CSVField f = CSVFieldFactory.fromSpecification(fieldSpecification);
        addField(f);
    }

    /**
     * Add a field, preserving the relative order.
     */
    public void addField(CSVField field) {

        fields.add(field);
    }

    /**
     * Add a field, preserving the relative order, corresponding to the given metric definition.
     */
    public void addField(MetricDefinition md) {

        MetricDefinitionBasedCSVField f = new MetricDefinitionBasedCSVField(md);
        fields.add(f);
    }

    /**
     * Adds a timestamp field.
     */
    public void addTimestampField() {

        CSVField f = new TimestampCSVField(TimedEvent.TIME_PROPERTY_NAME);
        fields.add(f);
    }

    /**
     * @return the actual underlying storage so handle with care.
     */
    public List<CSVField> getFields() {

        return fields;
    }

    /**
     * Similar to SimpleDateFormat#toPattern(), returns the specification of this format, which consists in the
     * concatenation of its fields' specifications, in order.
     *
     * @see SimpleDateFormat#toPattern()
     */
    public String toPattern() {

        String s = "";

        for(int i = 0; i < fields.size(); i ++) {

            CSVField f = fields.get(i);

            s += f.getSpecification();

            if (i < fields.size() - 1) {

                s += ", ";
            }
        }
        return s;
    }

    @Override
    public String toString() {

        return "CSVFormat[" + Integer.toHexString(System.identityHashCode(this)) + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private String nextUnnamedFieldName() {

        int i = ++unnamedFieldCounter;
        return "CSVField" + (i < 10 ? "0" : "") + i;
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
