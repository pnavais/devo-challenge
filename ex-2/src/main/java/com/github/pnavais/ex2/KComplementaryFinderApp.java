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

package com.github.pnavais.ex2;

import com.github.pnavais.common.cli.ShortErrorMessageHandler;
import com.github.pnavais.common.output.Colorize;
import com.github.pnavais.ex2.core.KComplementaryFinder;
import com.github.pnavais.ex2.model.KPair;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * A simple command line interface to test palindrome strings
 */
@Command(name = "K-ComplementaryFinder", version = "0.1.0", mixinStandardHelpOptions = true, sortOptions = false,
        headerHeading = "Usage:%n%n",
        synopsisHeading = "%n",
        descriptionHeading = "%nDescription:%n%n",
        parameterListHeading = "%nParameters:%n",
        optionListHeading = "%nOptions:%n",
        header = "Find K-complementary pairs within integer arrays.",
        description = "Finds K-complementary pairs in a given array of integers. Given Array A, pair (i, j) is K- complementary if K = A[i] + A[j]",
        footer = "Copyright(c) 2020")

public class KComplementaryFinderApp implements Runnable {

    @Option(names = { "-i", "--input" }, paramLabel = "integer", split = ",", description = "the integer array in comma separated format")
    private int[] input;

    @Option(names = { "-k" }, paramLabel = "integer", description = "the k value")
    private Integer k;

    @Override
    public void run() {
        retrieveParams();
        printResults(KComplementaryFinder.findKComplementaryPairs(input, k));
    }

    /**
     * Retrieve the parameters to run the algorithm if needed.
     */
    private void retrieveParams() {
        if (Objects.isNull(input) || Objects.isNull(k)) {
            Scanner scanner = new Scanner(System.in);

            while (Objects.isNull(this.input)) {
                System.out.print("Input a comma separated array of integers : ");
                String intList = scanner.nextLine();
                try {
                    this.input = Stream.of(intList.split(",")).mapToInt(s -> Integer.parseInt(s.trim())).toArray();
                } catch (Exception e) {
                    System.err.println(Colorize.fail("The specified array is not valid"));
                }
            }

            while (Objects.isNull(this.k)) {
                System.out.print("Input the value of k : ");
                try {
                    this.k = Integer.parseInt(scanner.nextLine());
                } catch (Exception e) {
                    System.err.println(Colorize.fail("The value is not a valid integer"));
                }
            }
            System.out.println();
        }
    }

    /**
     * Output the algorithm results
     *
     * @param kPairList the list of k-pairs to show
     */
    private void printResults(List<KPair> kPairList) {
        if (kPairList.isEmpty()) {
            System.out.println(Colorize.warn("No k-pairs found"));
        } else {
            System.out.printf("The following %s k-%s were found:%n", Colorize.info(String.valueOf(kPairList.size())), kPairList.size()>1 ? "pairs" : "pair");
            for (int idx = 0; idx<kPairList.size(); idx++) {
                KPair kPair = kPairList.get(idx);
                int i = kPair.getFirstIndex();
                int j = kPair.getSecondIndex();
                System.out.printf("%n\t- #%d %s (%d,%d)", idx+1, kPair, this.input[i], this.input[j]);
            }
        }
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
        int exitCode = new CommandLine(new KComplementaryFinderApp())
                .setParameterExceptionHandler(new ShortErrorMessageHandler())
                .execute(args);
        System.exit(exitCode);
    }

}
