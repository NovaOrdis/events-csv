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
import io.novaordis.events.csv.event.field.CSVField;
import io.novaordis.events.csv.event.field.CSVFieldFactory;
import io.novaordis.events.csv.event.field.CSVFieldImpl;
import io.novaordis.events.csv.event.field.MetricDefinitionBasedCSVField;
import io.novaordis.events.csv.event.field.TimestampCSVField;

import java.util.ArrayList;
import java.util.List;

/**
 * The class encapsulates a CSV line format specification. Instances of this class are used by CSV parsers, which
 * turn CSV text lines into events, or CSV formatters, which turn events into CSV text lines. The parsing/formatting
 * is an external concern, this class' only responsibility is to maintain the format information in a way that is as
 * flexible as possible.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 2/6/16
 */
public class CSVFormat {

    // Constants -------------------------------------------------------------------------------------------------------

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

        for(int i = 0, j = formatSpecification.indexOf(',');
            i < formatSpecification.length();
            j = formatSpecification.indexOf(',', i)) {

            j = j == -1 ? formatSpecification.length() : j;

            String fieldSpec = formatSpecification.substring(i, j).trim();

            if (fieldSpec.isEmpty() && j >= formatSpecification.length()) {

                //
                // does not count
                //
                break;
            }

            CSVField field = CSVFieldFactory.fromSpecification(fieldSpec);

            if (field.getName().length() == 0) {

                ((CSVFieldImpl)field).setName(nextUnnamedFieldName());
            }

            fields.add(field);

            i = j + 1;
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

        CSVField f = new TimestampCSVField(TimedEvent.TIMESTAMP_PROPERTY_NAME);
        fields.add(f);
    }

    /**
     * @return the actual underlying storage so handle with care.
     */
    public List<CSVField> getFields() {

        return fields;
    }

    @Override
    public String toString() {

        String s = "";

        for(int i = 0; i < fields.size(); i ++) {

            s += fields.get(i);

            if (i < fields.size() - 1) {

                s += ", ";
            }
        }
        return s;
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
