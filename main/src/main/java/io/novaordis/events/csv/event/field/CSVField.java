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

import java.text.Format;

/**
 * A CSV field definition, used by CSVFormat.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 6/20/17
 */
public interface CSVField {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * Never returns null.
     */
    String getName();

    /**
     * May return null.
     */
    Class getType();

    /**
     * May return null.
     */
    Format getFormat();

    /**
     * @throws IllegalArgumentException if the argument cannot be converted to a property of the right type.
     */
    Property toProperty(String s) throws IllegalArgumentException;

    /**
     * Timestamp fields are handled differently, so we need a type-level designator for it, we don't want to rely
     * on the name heuristics.
     */
    boolean isTimestamp();

    /**
     * The string specification of this CSVField. The string specification can be converted back into a equal CSVField
     * via the CSVFieldFactory.fromSpecification() operation.
     *
     * Example: An integer CSV field named "example" is specified as "example(int)".
     *
     * @see CSVFieldFactory#fromSpecification(String)
     */
    String getSpecification();
}
