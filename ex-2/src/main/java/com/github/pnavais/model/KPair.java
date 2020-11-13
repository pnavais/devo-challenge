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

package com.github.pnavais.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A simple class to store K-Pair indexes.
 */
@Getter
@Setter
@AllArgsConstructor(staticName = "of")
public class KPair {

    /**
     * The first index
     */
    private int firstIndex;

    /**
     * The second index
     */
    private int secondIndex;

    /**
     * Creates a List of K-Pairs from a given pair array (matrix)
     *
     * @param pairs the K-pairs array
     * @return the list of K-pairs
     */
    public static List<KPair> asList(@NonNull int[][] pairs) {
        return Stream.of(pairs).map(pair -> KPair.of(pair[0], pair[1])).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "[" + firstIndex + "," + secondIndex + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KPair kPair = (KPair) o;
        return (firstIndex == kPair.firstIndex && secondIndex == kPair.secondIndex)
                || (firstIndex == kPair.secondIndex && secondIndex == kPair.firstIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstIndex, secondIndex);
    }
}
