/*
 *
 * Copyright 2020 Pablo Navais
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.github.pnavais.common.output;

import com.github.pnavais.common.system.SystemCommons;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * This class provides ANSI utilities to improve
 * text output transformations.
 */
public class Colorize {

    /**
     * Private constructor to avoid instantiation
     */
    private Colorize() {}

    /** The reset ANSI code*/
    public static final String ANSI_RESET  = "\u001B[0m";

    /** The black color ANSI code*/
    public static final String ANSI_BLACK  = "\u001B[30m";

    /** The red color ANSI code*/
    public static final String ANSI_RED    = "\u001B[31m";

    /** The green color ANSI code*/
    public static final String ANSI_GREEN  = "\u001B[32m";

    /** The yellow color ANSI code*/
    public static final String ANSI_YELLOW = "\u001B[33m";

    /** The blue color ANSI code*/
    public static final String ANSI_BLUE   = "\u001B[34m";

    /** The purple color ANSI code*/
    public static final String ANSI_PURPLE = "\u001B[35m";

    /** The cyan color ANSI code*/
    public static final String ANSI_CYAN   = "\u001B[36m";

    /** The white color ANSI code*/
    public static final String ANSI_WHITE  = "\u001B[37m";

    /** The check symbol */
    public static final String ANSI_CHECK  = "\u2714";

    /** The cross symbol */
    public static final String ANSI_CROSS  = "\u274C";

    /** The triple string output pattern */
    public static final String S_S_S = "%s%s%s";

    /** the double string output pattern */
    public static final String S_S = "%s %s";

    /**
     * Adds to the input text a green emphasis.
     *
     * @param input the input text
     * @return the text with green emphasis
     */
    public static String ok(@NonNull String input) {
        return format(input, Pattern.of(S_S_S, ANSI_GREEN, input, ANSI_RESET));
    }

    /**
     * Adds to the input text a blue emphasis.
     *
     * @param input the input text
     * @return the text with blue emphasis
     */
    public static String info(@NonNull String input) {
        return format(input, Pattern.of(S_S_S, ANSI_BLUE, input, ANSI_RESET));
    }

    /**
     * Adds to the input text a red emphasis.
     *
     * @param input the input text
     * @return the text with red emphasis
     */
    public static String fail(@NonNull String input) {
        return format(input, Pattern.of(S_S_S, ANSI_RED, input, ANSI_RESET));
    }

    /**
     * Adds to the input text a yellow emphasis.
     *
     * @param input the input text
     * @return the text with yellow emphasis
     */
    public static String warn(@NonNull String input) {
        return format(input, Pattern.of(S_S_S, ANSI_YELLOW, input, ANSI_RESET));
    }

    /**
     * Depending on the condition, retrieves an ok
     * or failure output string.
     *
     * @param condition the condition
     * @param input the input string
     * @return the text with ok/fail characteristics
     */
    public static String okOrFail(boolean condition, @NonNull String input) {
        return condition ? ok(input) : fail(input);
    }

    /**
     * Adds to the input the check symbol
     * @param input the input text
     * @return the text with check symbol
     */
    public static String checked(@NonNull String input) {
        return format(input, Pattern.of(S_S, input, ANSI_CHECK));
    }

    /**
     * Adds to the input the cross symbol
     * @param input the input text
     * @return the text with cross symbol
     */
    public static String crossed(@NonNull String input) {
        return format(input, Pattern.of(S_S, input, ANSI_CROSS));
    }

    /**
     * Depending on the condition, adds the cross or check
     * symbol to the output.
     *
     * @param condition the condition
     * @param input the input string
     * @return the text with cross/checked symbol
     */
    public static String checkOrCrossed(boolean condition, @NonNull String input) {
        return (condition) ? checked(input) : crossed(input);
    }

    /**
     * Performs ANSI transformations only for compatible systems.
     *
     * @param input the input string
     * @param pattern the pattern
     * @return the ANSI string or original if not compatible
     */
    private static String format(String input, Pattern pattern) {
        return (!SystemCommons.isWindows() ? String.format(pattern.getFormat(), pattern.getArgList()) : input);
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static class Pattern {

        private String format;
        private Object[] argList;

        public Pattern(String pattern, Object... args) {
            this.format = pattern;
            this.argList = args;
        }

        public static Pattern of(String pattern, Object... args) {
            return new Pattern(pattern, args);
        }

    }
}
