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

package com.github.pnavais.ex3.reader;

import com.github.pnavais.ex3.api.exception.FileParsingException;
import com.github.pnavais.ex3.api.reader.DocumentReader;
import com.github.pnavais.ex3.index.DocTerm;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An implementation of the Document reader using a Stream to read file lines.
 */
@Slf4j
public class BufferedDocumentReader implements DocumentReader {

    /** The stream of lines */
    private BufferedReader reader;

    /**
     * Creates the buffered reader of data to process from the given file.
     *
     * @param filePath the complete file path
     * @throws FileParsingException on I/O errors
     */
    public void openFile(Path filePath) throws FileParsingException {
        try  {
            this.reader = new BufferedReader(new FileReader(filePath.toFile()));
        } catch (Exception e) {
            throw new FileParsingException("Error processing file "+filePath, e);
        }
    }

    /**
     * Closes the buffered reader
     */
    public void closeFile() {
        if (this.reader != null) {
            try {
                this.reader.close();
            } catch (IOException e) {
                log.error("Error closing file", e);
            }
        }
    }

    /**
     * Read the next line of the document
     * retrieving the list of its contained words
     * using a given token separator.
     *
     * @return the list of contained words or null if no more lines
     */
    @Override
    public Optional<String[]> getNextWords(String tokenSeparator) {
        Optional<String[]> words = Optional.empty();
        if (reader != null) {
            try {
                String line = reader.readLine();
                if (line != null) {
                    words = Optional.of(line.split(tokenSeparator));
                }
            } catch (IOException e) {
                log.error("Error reading from file", e);
            }
        }
        return words;
    }

    /**
     * Reads a given document storing the statistics for the
     * given set of terms.
     *
     * @param doc the document to read
     * @param terms the list of terms
     * @return the statistics for the terms in the document
     */
    @Override
    public List<DocTerm> processDocTerms(Path doc, Set<String> terms) throws FileParsingException {
        // Open the file for reading
        openFile(doc);

        // Build the initial terms information
        Map<String, DocTerm> docTermMap = terms.stream().collect(Collectors.toMap(s -> s, s -> DocTerm.of(s, doc)));

        // Update document statistics
        long wordCount = computeTermsStats(docTermMap);
        docTermMap.values().forEach(docTerm -> docTerm.setWordCount(wordCount));

        // Close file
        closeFile();

        // Build the list of terms statistics
        return new ArrayList<>(docTermMap.values());
    }

    /**
     * Read the whole file line by line storing the statistics of
     * the required terms and retrieving the word count of the file.
     *
     * @param docTermMap the map storing terms statistics
     * @return the word count of the document
     */
    private long computeTermsStats(Map<String, DocTerm> docTermMap) {
        long wordCount = 0;

        for (;;) {
            Optional<String[]> nextWords = getNextWords();
            if (nextWords.isPresent()) {
                String[] words = nextWords.get();
                wordCount+= words.length;
                Stream.of(words).forEachOrdered(w -> {
                    // Process only words matching the terms
                    if (docTermMap.containsKey(w)) {
                        DocTerm docTerm = docTermMap.get(w);
                        docTerm.increaseCount();
                    }
                });
            } else {
                break;
            }
        }

        return wordCount;
    }
}
