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

import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/8/17
 */
public class UTCMillisecondsLongTimestampFormatTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // parse() ---------------------------------------------------------------------------------------------------------

    @Test
    public void parse() throws Exception {

        UTCMillisecondsLongTimestampFormat f = new UTCMillisecondsLongTimestampFormat();

        long time = 1503522092L;

        String s = Long.toString(time);

        Date d = f.parse(s);

        long time2 = d.getTime();

        assertEquals(time, time2);
    }

    // parse() ---------------------------------------------------------------------------------------------------------

    @Test
    public void parse_NonParseable() throws Exception {

        UTCMillisecondsLongTimestampFormat f = new UTCMillisecondsLongTimestampFormat();

        try {

            f.parse("blah");
            fail("should have thrown exception");
        }
        catch(ParseException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("blah"));
            assertTrue(msg.contains("Unparseable"));
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
