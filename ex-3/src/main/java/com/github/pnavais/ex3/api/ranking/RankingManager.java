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

package com.github.pnavais.ex3.api.ranking;

import com.github.pnavais.ex3.api.index.IndexManager;

import java.io.PrintStream;

/**
 * An interface defining the methods to display generic statistics
 * of the terms in a given index.
 */
public interface RankingManager {

    /**
     * Displays in stdout the top results in terms of
     * generic statistics for the terms and documents
     * currently considered by the given index manager.
     *
     * @param manager the index manager
     */
    default void displayTopResults(IndexManager manager) {
        displayTopResults(manager, System.out);
    }

    /**
     * Displays in stdout the top n results in terms of
     * generic statistics for the terms and documents
     * currently considered by the given index manager.
     *
     * @param n the maximum number of results
     * @param manager the index manager
     */
    default void displayTopResults(int n, IndexManager manager) {
        displayTopResults(n, manager, System.out);
    }

    /**
     * Writes the top results in terms of
     * generics statistics for the terms and documents
     * currently considered by the given index manager.
     *
     * @param manager the index manager
     * @param pw the print stream
     */
    void displayTopResults(IndexManager manager, PrintStream pw);

    /**
     * Writes the top n results in terms of
     * generics statistics for the terms and documents
     * currently considered by the given index manager.
     *
     * @param n the maximum number of results
     * @param manager the index manager
     * @param pw the print stream
     */
    void displayTopResults(int n, IndexManager manager, PrintStream pw);
}
