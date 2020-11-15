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

package com.github.pnavais.ex3.config;

import com.github.pnavais.common.output.Colorize;
import lombok.Getter;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Command(name = "TfIdfServer", version = "0.1.0", mixinStandardHelpOptions = true, sortOptions = false,
        headerHeading = "Usage:%n%n",
        synopsisHeading = "%n",
        descriptionHeading = "%nDescription:%n%n",
        parameterListHeading = "%nParameters:%n",
        optionListHeading = "%nOptions:%n",
        header = "Run a service to rank documents based on the tf/idf statistic.",
        description = "This application runs as a daemon/service that is watching for new documents " +
                "in a given directory and dynamically updates the computed tf/idf for each document " +
                "and the inferred ranking.",
        footer = "Copyright(c) 2020")
@Getter
public class TfIdfServerConfig implements Callable<Integer> {

    private static final int DEFAULT_MAX_RESULTS = 5;
    private static final int DEFAULT_PERIOD = 30;

    @Option(names = {"-d", "--directory"}, paramLabel = "DIRECTORY", description = "the input directory " +
            "where documents are stored")
    private Path input;

    @Option(names = {"-t", "--terms"}, paramLabel = "TERMS", description = "the terms to look for")
    private String termsSimple;

    private Set<String> terms;

    @Option(names = {"-n"}, paramLabel = "int", description = "maximum number of top results to show. Defaults to 5")
    private Integer maxResults = DEFAULT_MAX_RESULTS;

    @Option(names = {"-p", "--period"}, paramLabel = "int", description = "period of time in seconds to display the report. Defaults to 30s")
    private Integer period = DEFAULT_PERIOD;

    @Option(names = "-v", description = { "Specify multiple -v options to increase verbosity.",
            "For example, `-v -v -v` or `-vvv`"})
    boolean[] verbosity;

    /** The application to run */
    Consumer<TfIdfServerConfig> app;

    public TfIdfServerConfig(Consumer<TfIdfServerConfig> app) {
        this.app = app;
    }

    /**
     * Checks parameters and launch the Spring application if
     * all data is valid.
     *
     * @return the exit code, 1 in case of failures, 0 otherwise
     */
    @Override
    public Integer call() {
        int exitCode = 0;

        if (validateParams()) {
            defineLogLevel();
            this.app.accept(this);
        } else {
            exitCode = 1;
        }

        return exitCode;
    }

    /**
     * Check the program has needed parameters.
     *
     * @return true if parameters are fulfilled, false otherwise
     */
    private boolean validateParams() {
        boolean valid = false;

        if (Objects.isNull(input)) {
            System.err.println(Colorize.fail("No input directory supplied"));
        } else if (!Files.exists(input)) {
            System.err.println(Colorize.fail("Cannot access \""+input+"\" directory"));
        } else if (Objects.isNull(termsSimple)) {
            System.err.println(Colorize.fail("No terms supplied"));
        } else {
            this.terms = Stream.of(termsSimple.split(" ")).collect(Collectors.toCollection(LinkedHashSet::new));
            this.period = (this.period<=0) ? DEFAULT_PERIOD : this.period;
            this.maxResults = (this.maxResults<=0) ? DEFAULT_MAX_RESULTS : this.maxResults;
            valid = true;
        }

        return valid;
    }

    /**
     * Define the log level depending on the verbosity
     */
    private void defineLogLevel() {
        String level = "OFF";
        if (verbosity != null) {
            switch (verbosity.length) {
                case 1 : level="INFO"; break;
                case 2 : level="DEBUG"; break;
                case 3 :
                default : level="TRACE"; break;
            }
        }

        System.setProperty("APP_LOG_LEVEL", level);
    }
}