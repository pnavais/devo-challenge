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

package com.github.pnavais.launcher;

import com.github.pnavais.common.cli.ShortErrorMessageHandler;
import com.github.pnavais.common.output.Colorize;
import com.github.pnavais.ex1.PalindromeCheckerApp;
import com.github.pnavais.ex2.KComplementaryFinderApp;
import com.github.pnavais.ex3.TfIdfServerApp;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

/**
 * A simple command line interface to launch the challenge exercices
 */
@Command(name = "Launcher", version = "0.1.0", mixinStandardHelpOptions = true, sortOptions = false,
        showEndOfOptionsDelimiterInUsageHelp = true,
        headerHeading = "Usage:%n%n",
        synopsisHeading = "%n",
        descriptionHeading = "%nDescription:%n%n",
        parameterListHeading = "%nParameters:%n",
        optionListHeading = "%nOptions:%n",
        header = "Launch the exercises.",
        description = "Launch one of the three exercises [ex1, ex2, ex3].",
        footer = "Copyright(c) 2020")
public class Launcher implements Callable<Integer> {

    private static String[] args;

    @Option(names = { "-e", "--exercise" }, required = true, paramLabel = "number", description = "the number of the exercise to launch")
    private Integer exNumber;

    @Parameters(description = "This option can be used to separate command-line options for the selected exercise")
    String[] params;

    @Override
    public Integer call() {
        int exitCode = 0;
        switch (exNumber) {
            case 1:
                PalindromeCheckerApp.main(params);
                break;
            case 2:
                KComplementaryFinderApp.main(params);
                break;
            case 3:
                TfIdfServerApp.main(params);
                break;
            default:
                System.err.println(Colorize.fail("Invalid exercise number. Expected value in [1-3]"));
                exitCode = 1;
                break;
        }

        return exitCode;
    }

    /**
     * Main entry point command line interface.
     * The program is expected to be executed with the following program arguments :
     *  -e [EX_NUMBER] -- [EX_OPTIONS]
     *  where :
     *  - EX_NUMBER: The number of the exercise from the range [1-3]
     *  - EX_OPTIONS: The rest of options of the given exercise. Put --help to list the available options.
     *
     *  Example:
     *  To launch the first exercise (Palindrome checker) supplying a given term :
     *  -e 1 -- -i abba
     *
     * @param args the command line args
     */
    public static void main(String[] args) {
        Launcher.args = args;
        int exitCode = new CommandLine(new Launcher())
                .setParameterExceptionHandler(new ShortErrorMessageHandler())
                .execute(args);
        System.exit(exitCode);
    }


}
