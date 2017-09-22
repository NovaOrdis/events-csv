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

package io.novaordis.events.csv;

import io.novaordis.events.api.event.Event;
import io.novaordis.events.processing.EventProcessingException;
import io.novaordis.events.processing.Procedure;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/22/17
 */
public class MockProcedure implements Procedure {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private List<Event> events;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockProcedure() {

        this.events = new ArrayList<>();
    }

    // Procedure implementation ----------------------------------------------------------------------------------------

    @Override
    public List<String> getCommandLineLabels() {
        throw new RuntimeException("getCommandLineLabels() NOT YET IMPLEMENTED");
    }

    @Override
    public void process(Event in) throws EventProcessingException {

        events.add(in);
    }

    @Override
    public void process(List<Event> in) throws EventProcessingException {

        events.addAll(in);
    }

    @Override
    public long getInvocationCount() {
        throw new RuntimeException("getInvocationCount() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean isExitLoop() {

        return false;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public List<Event> getEvents() {

        return events;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
