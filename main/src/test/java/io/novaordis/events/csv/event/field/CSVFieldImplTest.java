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

package io.novaordis.events.csv.event.field;

import io.novaordis.events.api.event.DateProperty;
import io.novaordis.events.api.event.DoubleProperty;
import io.novaordis.events.api.event.FloatProperty;
import io.novaordis.events.api.event.IntegerProperty;
import io.novaordis.events.api.event.LongProperty;
import io.novaordis.events.api.event.StringProperty;
import io.novaordis.events.api.event.TimedEvent;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 2/6/16
 */
public class CSVFieldImplTest extends CSVFieldTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(CSVFieldImplTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void stringField() throws Exception {

        CSVFieldImpl f = new CSVFieldImpl("some-string", String.class);

        assertEquals("some-string", f.getName());
        assertEquals(String.class, f.getType());
    }

    // timestamp handling ----------------------------------------------------------------------------------------------

    @Test
    public void csvFieldImplCannotBeUsedToRepresentTimestamps_SpecificationConstructor() throws Exception {

        try {

            new CSVFieldImpl(TimedEvent.TIMESTAMP_PROPERTY_NAME, Long.class);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertEquals("CSVFieldImpl cannot be used to represent timestamp fields, use TimestampCSVField", msg);
        }
    }

    @Test
    public void csvFieldImplCannotBeUsedToRepresentTimestamps_ComponentConstructor() throws Exception {

        try {

            new CSVFieldImpl(TimedEvent.TIMESTAMP_PROPERTY_NAME, Long.class);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertEquals("CSVFieldImpl cannot be used to represent timestamp fields, use TimestampCSVField", msg);
        }
    }

    // toProperty() ----------------------------------------------------------------------------------------------------

    @Test
    public void toProperty_String() throws Exception {

        CSVFieldImpl f = new CSVFieldImpl("test", String.class);
        StringProperty sp = (StringProperty)f.toProperty("blah");
        assertEquals("test", sp.getName());
        assertEquals("blah", sp.getValue());
    }

    @Test
    public void toProperty_Integer() throws Exception {

        CSVFieldImpl f = new CSVFieldImpl("test", Integer.class);
        IntegerProperty ip = (IntegerProperty)f.toProperty("1");
        assertEquals("test", ip.getName());
        assertEquals(1, ip.getInteger().intValue());
    }

    @Test
    public void toProperty_Integer_InvalidValue() throws Exception {

        CSVFieldImpl f = new CSVFieldImpl("test", Integer.class);

        try {

            f.toProperty("blah");
            fail("Should throw exception");
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
    }

    @Test
    public void toProperty_Long() throws Exception {

        CSVFieldImpl f = new CSVFieldImpl("test", Long.class);
        LongProperty lp = (LongProperty)f.toProperty("1");
        assertEquals("test", lp.getName());
        assertEquals(1, lp.getLong().longValue());
    }

    @Test
    public void toProperty_Long_InvalidValue() throws Exception {

        CSVFieldImpl f = new CSVFieldImpl("test", Long.class);

        try {
            f.toProperty("blah");
            fail("Should throw exception");
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
    }

    @Test
    public void toProperty_Float() throws Exception {

        CSVFieldImpl f = new CSVFieldImpl("test", Float.class);
        FloatProperty fp = (FloatProperty)f.toProperty("1.1");
        assertEquals("test", fp.getName());
        assertEquals(1.1f, fp.getFloat().floatValue(), 0.0001);
    }

    @Test
    public void toProperty_Float_InvalidValue() throws Exception {

        CSVFieldImpl f = new CSVFieldImpl("test", Float.class);

        try {

            f.toProperty("blah");
            fail("Should throw exception");
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
    }

    @Test
    public void toProperty_Double() throws Exception {

        CSVFieldImpl f = new CSVFieldImpl("test", Double.class);
        DoubleProperty fp = (DoubleProperty)f.toProperty("1.1");
        assertEquals("test", fp.getName());
        assertEquals(1.1d, fp.getDouble().doubleValue(), 0.0001);
    }

    @Test
    public void toProperty_Double_InvalidValue() throws Exception {

        CSVFieldImpl f = new CSVFieldImpl("test", Double.class);

        try {

            f.toProperty("blah");
            fail("Should throw exception");
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
    }

    @Test
    public void toProperty_Date() throws Exception {

        CSVFieldImpl f = new CSVFieldImpl("test", Date.class);
        f.setFormat(new SimpleDateFormat("yyyy"));
        DateProperty dp = (DateProperty)f.toProperty("2016");
        assertEquals("test", dp.getName());
        long time = dp.getDate().getTime();
        long reference = new SimpleDateFormat("yyyy").parse("2016").getTime();
        assertEquals(time, reference);
    }

    @Test
    public void toProperty_Date_InvalidValue() throws Exception {

        CSVFieldImpl f = new CSVFieldImpl("test", Date.class);
        f.setFormat(new SimpleDateFormat("yyyy"));

        try {

            f.toProperty("blah");
            fail("Should throw exception");
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
    }

    // typeToCommandLineLiteral() --------------------------------------------------------------------------------------

    @Test
    public void typeToCommandLineLiteral_UTCMillisecondsLongTimestampFormat() throws Exception {

        String s = CSVFieldImpl.typeToCommandLineLiteral(Date.class, new UTCMillisecondsLongTimestampFormat());
        assertEquals("(time:long)", s);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected CSVFieldImpl getCSVFieldToTest(String name, Class type, Format format) throws Exception {

        return new CSVFieldImpl(name, type, format);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
