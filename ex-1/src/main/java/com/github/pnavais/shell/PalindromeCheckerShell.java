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

package com.github.pnavais.shell;

import com.github.common.cli.ShortErrorMessageHandler;
import com.github.common.output.Colorize;
import com.github.pnavais.core.PalindromeChecker;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.Objects;
import java.util.Scanner;

/**
 * A simple command line interface to test palindrome strings
 */
@Command(name = "PalindromeChecker", version = "0.1.0", mixinStandardHelpOptions = true, sortOptions = false,
        headerHeading = "Usage:%n%n",
        synopsisHeading = "%n",
        descriptionHeading = "%nDescription:%n%n",
        parameterListHeading = "%nParameters:%n",
        optionListHeading = "%nOptions:%n",
        header = "Check palindrome strings.",
        description = "Checks an input string to verify whether it represents a valid palindrome or not.",
        footer = "Copyright(c) 2020")
public class PalindromeCheckerShell implements Runnable {

    @Option(names = { "-i", "--input" }, paramLabel = "STRING", description = "the input string to test")
    private String input;

    @Override
    public void run() {
        if (Objects.isNull(input)) {
            System.out.print("Input a string : ");
            Scanner scanner = new Scanner(System.in);
            this.input = scanner.nextLine();
        }

        boolean isPalindrome = PalindromeChecker.isPalindrome(this.input);
        System.out.printf("The string \"%s\" is %s %s",
                Colorize.info(this.input), (isPalindrome ? "a" : "not a"), Colorize.okOrFail(isPalindrome, "palindrome"));
    }

    /**
     * Entry point of the command line interface.
     *
     * Supports the following arguments :
     * -i, --input <input> : where input is a palindrome string
     *
     * Returns to standard output whether the string is
     * a valid palindrome or not.
     *
     * If no palindrome is supplied it will be queried to
     * the user.
     *
     * @param args the arguments.
     */
    public static void main(String[] args) {
        int exitCode = new CommandLine(new PalindromeCheckerShell())
                .setParameterExceptionHandler(new ShortErrorMessageHandler())
                .execute(args);
        System.exit(exitCode);
    }

}
