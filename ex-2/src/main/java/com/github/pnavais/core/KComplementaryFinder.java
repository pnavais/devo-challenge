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

package com.github.pnavais.core;

import com.github.pnavais.model.KPair;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * An implementation of a K-Complementary finder for integer arrays
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KComplementaryFinder
{

    /**
     * Find K-complementary pairs in a given array of integers.
     * Given an integer Array A, any pair (i, j) is K-complementary if K = A[i] + A[j];
     * Example : A = [ 1, 2, 2, 3 ] k = 4 contains the following valid k-pairs {0,3} and {1,2}
     *
     * The implementation of the k-complementary search algorithm is as follows:
     *  - The current "distance" from current value to k (i.e. k-complementary) is
     *  searched in a map keeping track of the indexes of each value.
     *  - If the value is found, we create a pair with every index present and keep it on the pairs list
     *  - If the current value is not present in the map the current index is added to an empty list, otherwise
     *  the index is added to the current list.
     *
     * Since time complexity of map insertions/lookups is O(1) and we loop through all elements of the input array
     * at once we have a time complexity of O(n).
     *
     * NOTE: This algorithm does not account for duplicates, in other words k-pairs (i,j) == (j,i).
     * Since the array may contain negative values, no optimization can be made
     * in terms of skipping current index values exceeding k.
     *
     * @param input the input array arguments
     * @return true if the input string is a palindrome, false otherwise
     */
    public static List<KPair> findKComplementaryPairs(@NonNull int[] input, int k)
    {
        List<KPair> pairs = new ArrayList<>();
        Map<Integer, List<Integer>> mapIndex = new LinkedHashMap<>();

        for (int i = 0; i < input.length; i++) {
            final int currentPos = i;
            if (mapIndex.containsKey(k - input[currentPos])) {
                mapIndex.get(k - input[currentPos]).forEach(idx -> pairs.add(KPair.of(idx, currentPos)));
            }
            mapIndex.merge(input[currentPos], new ArrayList<>(Collections.singletonList(currentPos)),
                    (key, list) -> { list.add(currentPos); return list; });
        }

        return pairs;
    }

    /**
     * Find K-complementary pairs in a given array of integers.
     * Given an integer Array A, any pair (i, j) is K-complementary if K = A[i] + A[j];
     * Example : A = [ 1, 2, 2, 3 ] k = 4 contains the following valid k-pairs {0,3} and {1,2}
     *
     * This implementation of the k-complementary search algorithm is as follows:
     *  - The input array is processed sequentially on an initial loop to get
     *  the value of the current index (index[i]).
     *  - In a second loop the successive and potentially unordered elements
     *  are added to the value of the current index (index[i]) to check if are k-complementary
     *  - In case of a match, the pair with both indexes is kept in the list
     *
     * Since we are traversing the input array twice we get a time complexity of O(n)*O(n-1) which is
     * equivalent to O(n)*O(n) = O(n^2).
     *
     * This algorithm has worst time complexity but a better space one since no other alternative data
     * structures are used in memory.
     *
     * NOTE: This algorithm does not account for duplicates, in other words k-pairs (i,j) == (j,i).
     * Since the array may contain negative values, no optimization can be made
     * in terms of skipping current index values exceeding k.
     *
     * @param input the input array arguments
     * @return true if the input string is a palindrome, false otherwise
     */
    public static List<KPair> findKComplementaryPairsAlternative(@NonNull int[] input, int k) {

        List<KPair> pairs = new ArrayList<>();

        for (int i = 0; i < input.length; i++) {
            for (int j = i + 1; j < input.length; j++) {
                if (input[i] + input[j] == k) {
                    pairs.add(KPair.of(i, j));
                }
            }
        }

        return pairs;
    }

    /**
     * Count K-complementary pairs in a given array of integers.
     * Given an integer Array A, any pair (i, j) is K-complementary if K = A[i] + A[j];
     * Example : A = [ 1, 2, 2, 3 ] k = 4 contains 2 valid k-pairs {0,3} and {1,2}
     *
     * The implementation of the k-complementary count algorithm is as follows:
     *  - For each current value in the array, the "distance" i.e. the difference
     *  between k and the value (i.e. the k-complementary) is checked in the map ,
     *  if found, the pairs count is increased by the number of occurrences of
     *  that complementary element.
     *  - The element is put in the map with an initial counter if not present
     *  or increased by one if already there.
     *
     * Since the check / insertions in the map have a time complexity of O(1)
     * and the algorithm performs a full loop across the input array, the total
     * time complexity is considered O(n).
     *
     * NOTE: This algorithm does not account for duplicates, i.e. the pairs
     * (i,j) and (j,i) are considered equals.
     *
     * @param input the input array arguments
     * @return true if the input string is a palindrome, false otherwise
     */
    public static int countKComplementaryPairs(@NonNull int[] input, int k)
    {
        int count = 0;
        Map<Integer, Integer> map = new HashMap<>();

        for (int item : input) {
            count += map.getOrDefault(k - item, 0);
            map.computeIfPresent(item, (key, value) -> value + 1);
            map.putIfAbsent(item, 1);
        }

        return count;
    }

}
