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

import io.novaordis.events.api.measure.MeasureUnit;
import io.novaordis.events.api.metric.MetricDefinitionBase;
import io.novaordis.utilities.address.Address;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/3/16
 */
public class MockMetricDefinition extends MetricDefinitionBase {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final String ID = MockMetricDefinition.class.getSimpleName();

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String simpleLabel;
    private Class type;
    private MeasureUnit baseUnit;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockMetricDefinition(Address metricSourceAddress) {

        this(metricSourceAddress, ID);
    }

    public MockMetricDefinition(Address metricSourceAddress, String id) {

        this(metricSourceAddress, id, null);
    }

    public MockMetricDefinition(Address metricSourceAddress, String id, Class type) {

        super(metricSourceAddress);
        setId(id);
        this.type = type;
    }

    // MetricDefinition implementation ---------------------------------------------------------------------------------

    @Override
    public String getSimpleLabel() {

        return simpleLabel;
    }

    @Override
    public Class getType() {

        return type;
    }

    @Override
    public MeasureUnit getBaseUnit() {

        return baseUnit;
    }

    @Override
    public String getDescription() {

        throw new RuntimeException("getDescription() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setSimpleLabel(String s) {

        this.simpleLabel = s;
    }

    @Override
    public String toString() {

        return "" + getId();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
