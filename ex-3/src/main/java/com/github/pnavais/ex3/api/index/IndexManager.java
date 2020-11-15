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

package com.github.pnavais.ex3.api.index;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * Defines the methods allowing to manage
 * the set of documents (D) and the TF/IDF statistics
 * for all the terms.
 */
public interface IndexManager {

    /**
     * Adds the given file to the set of
     * documents (D) to index.
     *
     * @param file the file to index
     */
    public void addFile(Path file);

    /**
     * Establishes the terms to keep in the
     * index.
     * @param terms the terms to keep
     */
    void setTerms(Set<String> terms);

    /**
     * Retrieve the set of terms
     * in the index.
     *
     * @return the set of terms
     */
    Set<String> getTerms();

    /**
     * Recomputes the index with potential new
     * files added to it.
     */
    void refresh();

    /**
     * Loads the index with the documents
     * present in the given directory.
     *
     * @param directory the directory
     */
    void loadFrom(Path directory);

    /**
     * Retrieve the TF/IDF for a given term
     * and document in the index.
     *
     * @param term the term
     * @param doc the document
     * @return the TF/IDF statistic of the term
     */
    double getTermTfIdf(String term, Path doc);

    /**
     * Compute the average TF/IDF statistic for
     * all the terms in the given document in the index.
     *
     * @param doc the document.
     * @return the average TF/IDF for all terms in the document
     */
    double getTermsTfIdfFor(Path doc);

    /**
     * Retrieves the number of documents in the index.
     *
     * @return the number of documents in the index
     */
    int size();

    /**
     * Retrieve the list of documents contained in the index
     *
     * @return the list of documents.
     */
    List<Path> getDocs();

}
