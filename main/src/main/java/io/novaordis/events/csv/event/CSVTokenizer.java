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

import java.util.ArrayList;
import java.util.List;

/**
 * Basic tokenization that handles separators, quotes, etc.
 *
 * Because it returns lists in a special format (they may contain nulls), it is intended to be used only by classes
 * belonging to this package, who presumably know what they are doing.
 *
 * As a general rule, an empty or blank token between two separators, and beyond the last separator, is stored as null
 * (the associated semantics is that there is no value there), unless the empty or blank token is quoted.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/8/17
 */
class CSVTokenizer {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final char DOUBLE_QUOTE = '"';

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * Basic tokenization that handles separators, quotes, etc. It does strip off property balanced quotes.
     *
     * Returns null list elements if it encounters empty tokens (separator-bounded space).
     *
     * @throws ParsingException
     */
    static List<String> split(Long lineNumber, String s, char separator) throws ParsingException {

        if (s == null) {

            throw new IllegalArgumentException("null input string");
        }

        List<String> result = new ArrayList<>();

        int i = 0;
        int quotedStart = -1;
        int tokenStart = 0;
        String token;

        if (s.isEmpty()) {

            result.add(null);
            return result;
        }

        for(;;i++) {

            char crt = s.charAt(i);

            if (quotedStart != -1) {

                //
                // quoted sequence, keep accumulating, unless it's the end of it
                //

                if (crt == DOUBLE_QUOTE) {

                    //
                    // end of the quoted sequence
                    //
                    quotedStart = -1;
                }
            }
            else if (crt == separator) {

                token = processToken(lineNumber, s, tokenStart, i);

                result.add(token);

                tokenStart = i + 1;
            }
            else if (crt == DOUBLE_QUOTE) {

                //
                // begin quoted sequence
                //

                quotedStart = i;
            }

            if (i == s.length() - 1) {

                //
                // last character of the string
                //

                if (quotedStart != -1) {

                    //
                    // unterminated quoted sequence
                    //

                    throw new ParsingException(lineNumber, quotedStart, "unbalanced quotes");
                }

                if (crt == separator) {

                    result.add(null);
                }

                if (tokenStart <= i) {

                    token = processToken(lineNumber, s, tokenStart, i + 1);
                    result.add(token);
                }

                break;
            }
        }


        return result;
    }

    /**
     * Processes a token that was previously extracted between two successive separators. Discards leading and trailing
     * empty space and strips paired quotes single pair of quotes per token. If a combination of quoted and non-quoted
     * strings is found, it is trimmed of leading and trailing blanks and returned as such.
     *
     * If unquoted blank or empty space is found, null is returned.
     */
    static String processToken(Long lineNumber, String s, int from, int to) throws ParsingException {

        if (s == null) {

            throw new IllegalArgumentException("null line");
        }

        if (from > to || from < 0 || to > s.length()) {

            throw new IllegalArgumentException((lineNumber == null ? "": lineNumber + ": ") + "inconsistent indices");
        }

        for(; from < to; from ++) {

            if (s.charAt(from) != ' ') {

                break;
            }
        }

        for(; to > from; to --) {

            if (s.charAt(to - 1) != ' ') {

                break;
            }
        }

        if (from == to) {

            return null;
        }

        //
        // look for pairing quotes
        //

        if (s.charAt(from) == DOUBLE_QUOTE && s.charAt(to - 1) == DOUBLE_QUOTE) {

            if (to - from == 2) {

                return "";
            }

            return s.substring(from + 1, to - 1);
        }
        else {

            s = s.substring(from, to);

            if (s.isEmpty()) {

                return null;
            }

            return s;
        }
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
