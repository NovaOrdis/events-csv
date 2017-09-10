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

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/9/17
 */
class PropertyInfo {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private int index;
    private String propertyName;
    private String fieldSpecification;

    // Constructors ----------------------------------------------------------------------------------------------------

    PropertyInfo(int index, String propertyName, String fieldSpecification) {

        this.index = index;
        this.propertyName = propertyName;
        this.fieldSpecification = fieldSpecification;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public int getIndex() {

        return index;
    }

    public String getPropertyName() {

        return propertyName;
    }

    public String getFieldSpecification() {

        return fieldSpecification;
    }


    public void incrementIndex() {

        index ++;
    }

    @Override
    public String toString() {

        return index + ":" + fieldSpecification;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
