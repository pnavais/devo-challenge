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

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Contains the statistics of the document for a given term.
 */
@Getter
@Setter
public class DocTerm {

    /** The document */
    private Path document;

    /** The term */
    private String term;

    /** The number of occurrences of the term in the document */
    private long occurrences;

    /** The number of words in the document */
    private long wordCount;

    /**
     * Constructor with mandatory items
     * @param term the term
     * @param document the document
     */
    public DocTerm(@NonNull String term, @NonNull Path document) {
        this.term = term;
        this.document = document;
    }

    /**
     * Static factory method to build the instance
     *
     * @param term the term
     * @param document the document
     * @return the instance
     */
    public static DocTerm of(String term, Path document) {
        return new DocTerm(term, document);
    }

    /**
     * Increases the number of occurrences of the term.
     */
    public void increaseCount() {
        this.occurrences++;
    }

    /**
     * Retrieves the computed term frequency (tf)
     *
     * @return the term frequency
     */
    public double computeTf() {
        return (occurrences*1.0)/wordCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocTerm docTerm = (DocTerm) o;
        return document.equals(docTerm.document) &&
                term.equals(docTerm.term);
    }

    @Override
    public int hashCode() {
        return Objects.hash(document, term);
    }

    @Override
    public String toString() {
        return "DocTerm {\n" +
                "\tdocument: [" + document + "]\n" +
                "\tterm: [\"" + term + "\"]\n" +
                "\toccurrences: " + occurrences +
                "\n\twordCount: " + wordCount +
                "\n}";
    }
}
