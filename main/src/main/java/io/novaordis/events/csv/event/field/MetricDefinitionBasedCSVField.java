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

package io.novaordis.events.csv.event.field;

import io.novaordis.events.api.event.Property;
import io.novaordis.events.api.metric.MetricDefinition;
import io.novaordis.events.api.metric.MetricException;

import java.text.Format;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 6/20/17
 */
public class MetricDefinitionBasedCSVField implements CSVField {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private MetricDefinition metricDefinition;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MetricDefinitionBasedCSVField(MetricDefinition md) {

        if (md == null) {

            throw new IllegalArgumentException("null metric definition");
        }

        this.metricDefinition = md;
    }

    // CSVField implementation -----------------------------------------------------------------------------------------

    @Override
    public String getName() {

        return metricDefinition.getId();
    }

    @Override
    public Class getType() {

        return metricDefinition.getType();
    }

    @Override
    public Format getFormat() {

        //
        // TODO shouldn't MetricDefinition maintain a format?
        //

        return null;
    }

    @Override
    public Property toProperty(String s) throws IllegalArgumentException {

        try {

            return metricDefinition.buildProperty(s);
        }
        catch(MetricException e) {

            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public boolean isTimestamp() {

        return false;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public MetricDefinition getMetricDefinition() {

        return metricDefinition;
    }

    @Override
    public String toString() {

        return "CSV Field (" + metricDefinition + ")";
    }


    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
