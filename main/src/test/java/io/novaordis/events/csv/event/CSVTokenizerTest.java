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

package io.novaordis.events.csv.event;

import io.novaordis.events.api.parser.ParsingException;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/8/17
 */
public class CSVTokenizerTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void tokenize_NullInputString() throws Exception {

        try {

            CSVTokenizer.split(7L, null, ',');
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {
            
            String msg = e.getMessage();
            assertTrue(msg.contains("null input string"));
        }
    }

    @Test
    public void tokenize_EmptyString() throws Exception {

        List<String> tokens = CSVTokenizer.split(7L, "", ',');

        assertEquals(1, tokens.size());
        assertNull(tokens.get(0));
    }

    @Test
    public void tokenize_BlankString() throws Exception {

        List<String> tokens = CSVTokenizer.split(7L, " ", ',');

        assertEquals(1, tokens.size());
        assertNull(tokens.get(0));
    }

    @Test
    public void tokenize_QuotedEmptyString() throws Exception {

        List<String> tokens = CSVTokenizer.split(7L, "\"\"", ',');

        assertEquals(1, tokens.size());
        assertEquals("", tokens.get(0));
    }

    @Test
    public void tokenize_QuotedEmptyString2() throws Exception {

        List<String> tokens = CSVTokenizer.split(7L, "    \"\"      ", ',');

        assertEquals(1, tokens.size());
        assertEquals("", tokens.get(0));
    }

    @Test
    public void tokenize_QuotedBlankString() throws Exception {

        List<String> tokens = CSVTokenizer.split(7L, "\" \"", ',');

        assertEquals(1, tokens.size());
        assertEquals(" ", tokens.get(0));
    }

    @Test
    public void tokenize_QuotedStringThatContainsSeparators() throws Exception {

        List<String> tokens = CSVTokenizer.split(7L, "\" , \"", ',');

        assertEquals(1, tokens.size());
        assertEquals(" , ", tokens.get(0));
    }

    @Test
    public void tokenize_String() throws Exception {

        List<String> tokens = CSVTokenizer.split(7L, "something", ',');

        assertEquals(1, tokens.size());
        assertEquals("something", tokens.get(0));
    }

    @Test
    public void tokenize_MultipleStrings_NoTrailingSeparator() throws Exception {

        List<String> tokens = CSVTokenizer.split(7L, "A, B ", ',');

        assertEquals(2, tokens.size());
        assertEquals("A", tokens.get(0));
        assertEquals("B", tokens.get(1));
    }

    @Test
    public void tokenize_MultipleStrings_TrailingSeparator_NoSpace() throws Exception {

        List<String> tokens = CSVTokenizer.split(7L, "A, B,", ',');

        assertEquals(3, tokens.size());
        assertEquals("A", tokens.get(0));
        assertEquals("B", tokens.get(1));
        assertNull(tokens.get(2));
    }

    @Test
    public void tokenize_MultipleStrings_TrailingSeparator_Space() throws Exception {

        List<String> tokens = CSVTokenizer.split(7L, "A, B,   ", ',');

        assertEquals(3, tokens.size());
        assertEquals("A", tokens.get(0));
        assertEquals("B", tokens.get(1));
        assertNull(tokens.get(2));
    }

    @Test
    public void tokenize_QuotedString() throws Exception {

        List<String> tokens = CSVTokenizer.split(7L, "\"something\"", ',');

        assertEquals(1, tokens.size());
        assertEquals("something", tokens.get(0));
    }

    @Test
    public void tokenize_OneSeparator_EmptyString() throws Exception {

        List<String> tokens = CSVTokenizer.split(7L, ",", ',');

        assertEquals(2, tokens.size());
        assertNull(tokens.get(0));
        assertNull(tokens.get(1));
    }

    @Test
    public void tokenize_OneSeparator_BlankString() throws Exception {

        List<String> tokens = CSVTokenizer.split(7L, " , ", ',');

        assertEquals(2, tokens.size());
        assertNull(tokens.get(0));
        assertNull(tokens.get(1));
    }

    @Test
    public void tokenize_OneSeparator_QuotedEmptyString() throws Exception {

        List<String> tokens = CSVTokenizer.split(7L, "\"\",\"\"", ',');

        assertEquals(2, tokens.size());
        assertEquals("", tokens.get(0));
        assertEquals("", tokens.get(1));
    }

    @Test
    public void tokenize_OneSeparator_QuotedEmptyString_EmptyString() throws Exception {

        List<String> tokens = CSVTokenizer.split(7L, "\"\",", ',');

        assertEquals(2, tokens.size());
        assertEquals("", tokens.get(0));
        assertNull(tokens.get(1));
    }

    @Test
    public void tokenize_OneSeparator_QuotedBlankString() throws Exception {

        List<String> tokens = CSVTokenizer.split(7L, "\" \",\"    \"", ',');

        assertEquals(2, tokens.size());
        assertEquals(" ", tokens.get(0));
        assertEquals("    ", tokens.get(1));
    }

    @Test
    public void tokenize_OneSeparator_QuotedStringThatContainsSeparators() throws Exception {

        List<String> tokens = CSVTokenizer.split(7L, "\" , \",\"  ,  \"", ',');

        assertEquals(2, tokens.size());
        assertEquals(" , ", tokens.get(0));
        assertEquals("  ,  ", tokens.get(1));
    }

    @Test
    public void tokenize_OneSeparator_String() throws Exception {

        List<String> tokens = CSVTokenizer.split(7L, "something, something-else", ',');

        assertEquals(2, tokens.size());
        assertEquals("something", tokens.get(0));
        assertEquals("something-else", tokens.get(1));
    }

    @Test
    public void tokenize_OneSeparator_QuotedString() throws Exception {

        List<String> tokens = CSVTokenizer.split(7L, "\"something\",\" something else \"", ',');

        assertEquals(2, tokens.size());
        assertEquals("something", tokens.get(0));
        assertEquals(" something else ", tokens.get(1));
    }

    @Test
    public void tokenize_UnbalancedQuotes() throws Exception {

        try {

            CSVTokenizer.split(7L, "\"A", ',');
            fail("should throw exception");
        }
        catch(ParsingException e) {

            String msg = e.getMessage();
            assertEquals("unbalanced quotes", msg);
            assertEquals(7L, e.getLineNumber().longValue());
            assertEquals(0, e.getPositionInLine().intValue());
        }
    }

    @Test
    public void tokenize_1() throws Exception {

        List<String> tokens = CSVTokenizer.split(7L, "A", ',');
        assertEquals(1, tokens.size());
        assertEquals("A", tokens.get(0));
    }

    @Test
    public void tokenize_2() throws Exception {

        List<String> tokens = CSVTokenizer.split(7L, "A,", ',');
        assertEquals(2, tokens.size());
        assertEquals("A", tokens.get(0));
        assertNull(tokens.get(1));
    }

    @Test
    public void tokenize_3() throws Exception {

        List<String> tokens = CSVTokenizer.split(7L, "A,B", ',');
        assertEquals(2, tokens.size());
        assertEquals("A", tokens.get(0));
        assertEquals("B", tokens.get(1));
    }

    @Test
    public void tokenize_4() throws Exception {

        List<String> tokens = CSVTokenizer.split(7L, ",A", ',');
        assertEquals(2, tokens.size());
        assertNull(tokens.get(0));
        assertEquals("A", tokens.get(1));
    }

    // processQuotes() -------------------------------------------------------------------------------------------------

    @Test
    public void processQuotes_Null() throws Exception {

        try {

            CSVTokenizer.processToken(7L, null, 0, 1);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("null line"));
        }
    }

    @Test
    public void processQuotes_OverlappingIndices() throws Exception {

        try {

            CSVTokenizer.processToken(7L, "something", 4, 3);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("inconsistent indices"));
        }
    }

    @Test
    public void processQuotes_InvalidFrom() throws Exception {

        try {

            CSVTokenizer.processToken(7L, "something", -1, 3);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("inconsistent indices"));
        }
    }

    @Test
    public void processQuotes_InvalidTo() throws Exception {

        try {

            CSVTokenizer.processToken(7L, "A", 0, 2);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("inconsistent indices"));
        }
    }


    @Test
    public void processQuotes_UnquotedEmptyString() throws Exception {

        String line = "";
        String s = CSVTokenizer.processToken(7L, line, 0, line.length());
        assertNull(s);
    }

    @Test
    public void processQuotes_EmptyString() throws Exception {

        String line = "\"\"";
        String s = CSVTokenizer.processToken(7L, line, 0, line.length());
        assertEquals("", s);
    }

    @Test
    public void processQuotes_BlankString() throws Exception {

        String line = "\"    \"";
        String s = CSVTokenizer.processToken(7L, line, 0, line.length());
        assertEquals("    ", s);
    }

    @Test
    public void processQuotes_QuotedNonQuotedCombination_BothSides() throws Exception {

        String line = "  something \"A\" something else   ";
        String s = CSVTokenizer.processToken(7L, line, 0, line.length());
        assertEquals("something \"A\" something else", s);
    }

    @Test
    public void processQuotes_QuotedNonQuotedCombination_End() throws Exception {

        String line ="\"A\" this should be fine   ";
        String s = CSVTokenizer.processToken(7L, line, 0, line.length());
        assertEquals("\"A\" this should be fine", s);
    }

    @Test
    public void processQuotes_QuotedNonQuotedCombination_Beginning() throws Exception {

        String line = " X \"A\"   ";
        String s = CSVTokenizer.processToken(7L, line, 0, line.length());
        assertEquals("X \"A\"", s);
    }

    @Test
    public void processQuotes_BlankCharactersAfterQuotedString() throws Exception {

        String line = "\"A\"    ";
        String s = CSVTokenizer.processToken(7L, line, 0, line.length());
        assertEquals("A", s);
    }

    @Test
    public void processQuotes_BlankCharactersBeforeQuotedString() throws Exception {

        String line = "    \"A\"";
        String s = CSVTokenizer.processToken(7L, line, 0, line.length());
        assertEquals("A", s);
    }

    @Test
    public void processQuotes_BlankCharactersBeforeAndAfterQuotedString() throws Exception {

        String line = "    \"A\"     ";
        String s = CSVTokenizer.processToken(7L, line, 0, line.length());
        assertEquals("A", s);
    }

    @Test
    public void processQuotes_NoQuotes() throws Exception {

        String line = "something";
        String s = CSVTokenizer.processToken(7L, line, 0, line.length());
        assertEquals("something", s);
    }

    @Test
    public void processQuotes_NoQuotes_LeadingBlanks() throws Exception {

        String line = "   something";
        String s = CSVTokenizer.processToken(7L, line, 0, line.length());
        assertEquals("something", s);
    }

    @Test
    public void processQuotes_NoQuotes_TrailingBlanks() throws Exception {

        String line = "something      ";
        String s = CSVTokenizer.processToken(7L, line, 0, line.length());
        assertEquals("something", s);
    }

    @Test
    public void processQuotes_NoQuotes_LeadingAndTrailingBlanks() throws Exception {

        String line = "     something      ";
        String s = CSVTokenizer.processToken(7L, line, 0, line.length());
        assertEquals("something", s);
    }

    @Test
    public void processQuotes_NoQuotes_AllBlanks() throws Exception {

        String line = "         ";
        String s = CSVTokenizer.processToken(7L, line, 0, line.length());
        assertNull(s);
    }

    @Test
    public void processQuotes_1() throws Exception {

        String line = "\"\"";
        String s = CSVTokenizer.processToken(7L, line, 0, line.length());
        assertEquals("", s);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
