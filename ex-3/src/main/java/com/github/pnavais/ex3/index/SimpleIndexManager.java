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

package com.github.pnavais.ex3.index;

import com.github.pnavais.common.file.FileCommons;
import com.github.pnavais.ex3.api.exception.FileParsingException;
import com.github.pnavais.ex3.api.index.IndexManager;
import com.github.pnavais.ex3.api.reader.DocumentReader;
import com.github.pnavais.ex3.reader.DocumentReaderFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A basic implementation of the {@link IndexManager} interface allowing to keep track
 * of new files and building an index for the terms provided.
 * The implementation of the index is as follows :
 * - A map will contain as keys the terms to index
 * - The value of each key in the map consists of a set (avoiding duplicates) of the document tf statistic
 * for the term.
 * <p>
 * In order to compute the idf of a given term per document, it would simply suffice of :
 * - Looking for the term in the map (O(1) operation)
 * - Looking for the document in the secondary map (O(1) operation) and extracting its tf value from the recorded
 * value (DocTerm).
 * - Dividing the tf value found by the number of documents in the set for this terms. That is, only documents
 * containing the term will be present in the set.
 * <p>
 * The combined tf/idf for all the terms would consist of the average of all individual tf/idf term statistics.
 */
@Component
@Slf4j
public class SimpleIndexManager implements IndexManager {

    /**
     * The current set of documents with a flag
     * indicating if the file has been indexed or not
     */
    private final Map<Path, Boolean> documents;

    /**
     * The actual index.
     * It is implemented as a map as follows :
     * - Key: the term
     * - Value: A map composed of :
     * - Key: Path to the document
     * - Value: term statistics for the document
     */
    private final Map<String, Map<Path, DocTerm>> index;

    /**
     * Creates the instance initializing both
     * the index map and the document set (D)
     */
    @Autowired
    public SimpleIndexManager() {
        this.documents = new ConcurrentHashMap<>();
        this.index = new ConcurrentHashMap<>();
    }

    /**
     * The set of terms to index
     */
    @Setter
    @Getter
    private Set<String> terms;

    /**
     * Adds a new file to the index
     *
     * @param file the file to index
     */
    @Override
    public void addFile(Path file) {
        addFile(file, false);
    }

    /**
     * Adds a new file to the index with
     * a flag controlling whether the file
     * is indexed or not.
     *
     * @param file the file to index
     */
    private void addFile(Path file, boolean isIndexed) {
        log.debug("Adding file [{}] to the document set", file);
        documents.put(file, isIndexed);
    }

    /**
     * Loads the index with the documents
     * present in the given directory.
     *
     * @param directory the directory
     */
    @Override
    public void loadFrom(Path directory) {
        if (FileCommons.isDirectory(directory)) {
            try {
                Files.list(directory).forEachOrdered(this::buildDocIndex);
            } catch (IOException e) {
                log.error("Error loading documents from \"{}\".{}", directory, e.getMessage());
            }
        }
    }

    /**
     * Rebuild the index for missing/new files.
     */
    @Override
    public void refresh() {
        long newDocs = documents.values().stream().filter(Boolean.FALSE::equals).count();
        log.debug("Refreshing index with {} new document{}", newDocs, newDocs > 1 ? "s" : "");
        documents.keySet().parallelStream().forEach(this::buildDocIndex);
    }

    /**
     * Retrieve the TF/IDF for a given term
     * and document in the index.
     *
     * @param term the term
     * @param doc  the document
     * @return the TF/IDF statistic of the term
     */
    @Override
    public double getTermTfIdf(String term, Path doc) {
        double tf = 0.0;
        double idf = 0.0;

        Map<Path, DocTerm> termMap = index.get(term);
        // Compute idf
        if ((termMap != null) && (termMap.size() > 0) && (documents.size() > 0)) {
            idf = Math.log10((documents.size() * 1.0) / termMap.size());
            if (idf != 0) {
                // Retrieve tf when relevant
                DocTerm docTerm = termMap.get(doc);
                if (docTerm != null) {
                    tf = docTerm.computeTf();
                }
            }
        }

        return tf * idf;
    }

    /**
     * Compute the average TF/IDF statistic for
     * all the terms in the given document in the index.
     *
     * @param doc the document.
     * @return the average TF/IDF for all terms in the document
     */
    @Override
    public double getTermsTfIdfFor(Path doc) {
        return terms.stream().mapToDouble(term -> getTermTfIdf(term, doc)).average().orElse(0.0);
    }

    /**
     * Retrieves the number of documents in the index.
     *
     * @return the number of documents in the index
     */
    @Override
    public int size() {
        return documents.size();
    }

    /**
     * Retrieve the list of documents contained in the index
     *
     * @return the list of documents.
     */
    @Override
    public List<Path> getDocs() {
        return new ArrayList<>(documents.keySet());
    }

    /**
     * Removes the document set and the index values
     */
    @Override
    public void clear() {
        this.documents.clear();
        this.index.values().forEach(Map::clear);
        this.index.clear();
    }

    /**
     * Retrieves the document statistics for
     * the given term in the index.
     *
     * @param term the term
     * @return the list of document statistics
     */
    @Override
    public List<DocTerm> getDocTermsFor(String term) {
        List<DocTerm> docTermList = Collections.emptyList();
        Map<Path, DocTerm> termMap = index.get(term);
        if (termMap != null) {
            docTermList = new ArrayList<>(termMap.values());
        }

        return docTermList;
    }

    /**
     * Build the index for the document with respect to the
     * terms to search.
     *
     * @param doc the document to index.
     */
    private void buildDocIndex(Path doc) {
        if (Boolean.FALSE.equals(documents.getOrDefault(doc, false))) {
            CompletableFuture.runAsync(() -> {
                buildTermIndex(doc);
                addFile(doc, true);
            });
        }
    }

    /**
     * Build the terms index by reading the file and extracting
     * the terms document statistics.
     */
    private void buildTermIndex(Path doc) {
        log.debug("Building terms index for document [{}]", doc);
        DocumentReader documentReader = DocumentReaderFactory.getReader();
        try {
            documentReader.processDocTerms(doc, terms).forEach(docTerm -> {
                index.putIfAbsent(docTerm.getTerm(), new LinkedHashMap<>());
                index.computeIfPresent(docTerm.getTerm(), (key, map) -> {
                    // Only store matching documents
                    if (docTerm.getOccurrences()>0) {
                        map.put(docTerm.getDocument(), docTerm);
                    }
                    return map;
                });
            });
        } catch (FileParsingException e) {
            log.error("Error indexing file [{}]. Cause : {}", doc, e.getMessage());
        }
    }

}
