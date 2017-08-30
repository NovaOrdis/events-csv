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

import io.novaordis.events.processing.output.DefaultOutputFormatFactory;
import io.novaordis.events.processing.output.OutputFormatImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/30/17
 */
public class CSVOutputFormatFactoryTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // fromArguments() -------------------------------------------------------------------------------------------------

    @Test
    public void fromArguments() throws Exception {

        CSVOutputFormatFactory f = new CSVOutputFormatFactory();

        DefaultOutputFormatFactory fDelegate = (DefaultOutputFormatFactory)f.getDelegate();
        assertNotNull(fDelegate);

        List<String> args = new ArrayList<>(Arrays.asList("to change this", "to change this"));
        CSVOutputFormat fmt = f.fromArguments(args);

        assertTrue(args.isEmpty());

        OutputFormatImpl delegate = (OutputFormatImpl)fmt.getDelegate();
        assertNotNull(delegate);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
