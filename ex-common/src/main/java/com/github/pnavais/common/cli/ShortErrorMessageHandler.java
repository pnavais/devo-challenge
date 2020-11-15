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

package com.github.pnavais.common.cli;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.IParameterExceptionHandler;
import picocli.CommandLine.IExitCodeExceptionMapper;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Help.ColorScheme;

import java.io.PrintWriter;

/**
 * A simple parameter exception handler enabling/disabling displaying usage message
 * on errors.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShortErrorMessageHandler implements IParameterExceptionHandler {

    /** The flag controlling usage information display */
    private boolean showUsage = false;

    /** Handles a {@code ParameterException} that occurred while parsing the command
     * line arguments and returns an exit code suitable for returning from execute method.
     *
     * @param ex the ParameterException describing the problem that occurred while parsing the command line arguments,
     *           and the CommandLine representing the command or sub-command whose input was invalid
     * @param args the command line arguments that could not be parsed
     *
     * @return an exit code
     */
    @Override
    public int handleParseException(CommandLine.ParameterException ex, String[] args) throws Exception {
        CommandLine cmd = ex.getCommandLine();
        internalHandleParseException(ex, cmd.getErr(), cmd.getColorScheme());
        return mappedExitCode(ex, cmd.getExitCodeExceptionMapper(), cmd.getCommandSpec().exitCodeOnInvalidInput());
    }

    private void internalHandleParseException(ParameterException ex, PrintWriter writer, ColorScheme colorScheme) {
        writer.println(colorScheme.errorText(ex.getMessage()));
        if (!CommandLine.UnmatchedArgumentException.printSuggestions(ex, writer) && showUsage) {
            ex.getCommandLine().usage(writer, colorScheme);
        }
        // if tracing at DEBUG level, show the location of the issue
        if ("DEBUG".equalsIgnoreCase(System.getProperty("picocli.trace"))) {
            writer.println(colorScheme.stackTraceText(ex));
        }
    }

    /**
     * Maps the exit code for an exception using a give mapper.
     *
     * @param t the exception
     * @param mapper the mapper
     * @param defaultExitCode the default exit code
     *
     * @return the mapped exit code
     */
    private static int mappedExitCode(Throwable t, IExitCodeExceptionMapper mapper, int defaultExitCode) {
        try {
            return (mapper != null) ? mapper.getExitCode(t) : defaultExitCode;
        } catch (Exception ex) {
            ex.printStackTrace();
            return defaultExitCode;
        }
    }
}
