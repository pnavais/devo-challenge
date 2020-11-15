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

package com.github.pnavais.ex3.ranking;

import com.github.pnavais.ex3.api.index.IndexManager;
import com.github.pnavais.ex3.api.ranking.RankingManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.PrintStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An implementation of the {@link RankingManager} allowing
 * to display TF/IDF statistics of a given index.
 */
@Component
@Slf4j
public class TfIdfRankingManager implements RankingManager {

    @Setter
    private int topResults;

    /**
     * Writes the top results in terms of
     * generics statistics for the terms and documents
     * currently considered by the given index manager.
     *
     * @param manager the index manager
     * @param pw the print stream
     */
    @Override
    public void displayTopResults(@NonNull IndexManager manager, @NonNull PrintStream pw) {
        displayTopResults(topResults, manager, pw);
    }

    /**
     * Writes the top results in terms of
     * generics statistics for the terms and documents
     * currently considered by the given index manager.
     *
     * @param manager the index manager
     * @param pw the print stream
     */
    @Override
    public void displayTopResults(int n, @NonNull IndexManager manager, @NonNull PrintStream pw) {
        log.debug("Computing the TF/IDF top {} results : Terms {} (index size : {})", n, manager.getTerms(), manager.size());
        List<DocStat> docStats = manager.getDocs().stream()
                .map(doc -> DocStat.of(doc, manager.getTermsTfIdfFor(doc)))
                .sorted(Comparator.comparing(DocStat::getTfIdf).reversed())
                .limit(n)
                .collect(Collectors.toList());

        String header = String.format("Top %d TF/IDF results [%s]", n, LocalDateTime.now());
        String ruler = String.join("", Collections.nCopies(header.length(), "-"));
        pw.printf("%n%s%n%s%n", header, ruler);
        docStats.forEach(docStat -> pw.printf("[%s] %.4f\n", docStat.getDoc().getFileName(), docStat.getTfIdf()));
    }

    @Getter
    @Setter
    @AllArgsConstructor(staticName = "of")
    private static class DocStat {
        private Path doc;
        private Double tfIdf;
    }
}
