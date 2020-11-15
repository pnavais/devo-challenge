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

package com.github.pnavais.ex3.api.reader;

import com.github.pnavais.ex3.api.exception.FileParsingException;
import com.github.pnavais.ex3.index.DocTerm;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Defines the methods allowing to read
 * a text document retrieving its contents.
 */
public interface DocumentReader {

    String DEFAULT_TOKEN_SEPARATOR = " ";

    /**
     * Read the next line of the document
     * retrieving the list of its contained words
     * using the configured token separator
     *
     * @return the list of contained words or null if no more lines
     */
    default Optional<String[]> getNextWords() {
        return getNextWords(DEFAULT_TOKEN_SEPARATOR);
    }

    /**
     * Read the next line of the document
     * retrieving the list of its contained words
     * using a given token separator.
     *
     * @return the list of contained words or null if no more lines
     */
    Optional<String[]> getNextWords(String tokenSeparator);

    /**
     * Reads a given document storing the statistics for the
     * given set of terms.
     *
     * @param doc the document to read
     * @param terms the list of terms
     * @return the statistics for the terms in the document
     */
    List<DocTerm> processDocTerms(Path doc, Set<String> terms) throws FileParsingException;
}
