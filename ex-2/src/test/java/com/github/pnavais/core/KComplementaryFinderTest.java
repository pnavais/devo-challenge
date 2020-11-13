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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link KComplementaryFinder} class
 */
@DisplayName("Unit tests for the KComplementaryFinder class")
public class KComplementaryFinderTest {

    @Test
    @DisplayName("Count K-complementary pairs")
    void countPairsTest() {
        countKPairs(new int[] { 0, 0, 2 }, 1);
        countKPairs(new int[] { 1, 1 }, 2);
        countKPairs(new int[] { 1, 0 }, 1);
        countKPairs(new int[] { 1, 2, 2, 3 }, 3);
        countKPairs(new int[] { 2, 1, 3, 5, 4, 3, 0, 6}, 6);
        countKPairs(new int[] { 4, 4, 2, 3, 5, 4, 3, 0, 6}, 6);
    }

    @Test
    @DisplayName("Get valid K-complementary pairs")
    void getValidKComplementaryTest() {
        checkValidKPairs(new int[][] { {0,1} }, new int[] { 1, 0 }, 1);
        checkValidKPairs(new int[][] { {0,1} }, new int[] { 0, 0 }, 0);
        checkValidKPairs(new int[][] { {0,1}, {0,2} }, new int[] { 1, 2, 2, 3 }, 3);
        checkValidKPairs(new int[][] { {0,3}, {1,2} }, new int[] { 1, 2, 2, 3 }, 4);
        checkValidKPairs(new int[][] { {0,4}, {1,3}, {2,5} }, new int[] { 2, 1, 3, 5, 4, 3}, 6);
    }

    @Test
    @DisplayName("Get valid K-complementary pairs with negative numbers")
    void getValidKComplementaryWithNegativesTest() {
        checkValidKPairs(new int[][] { {0,1} }, new int[] { -5, -4 }, -9);
        checkValidKPairs(new int[][] { {1,3} }, new int[] { 0, -1, 3, -2, 8 }, -3);
        checkValidKPairs(new int[][] { {0, 3}, {1,6} }, new int[] { 0, 1, 3, -3, -2, 8 , -4 }, -3);
        checkValidKPairs(new int[][] { {0, 2}, {1, 3} }, new int[] { 0, 13, 3, -10, -2, 8 , -4 }, 3);
        checkValidKPairs(new int[][] { {0,5} }, new int[] { 0, -1, 3, -2, 8 , 0 , -2, -1 }, 0);
    }

    @Test
    @DisplayName("Get no valid K-complementary pairs")
    void getNoValidKComplementaryTest() {
        checkInValidKPairs(new int[] { 2, 3, 3, 5, 4, 3}, 10);
        checkInValidKPairs(new int[] { 2, 3, 11, 3, 5, 4, 3, 10}, 20);
        checkInValidKPairs(new int[] { 0 }, 5);
        checkInValidKPairs(new int[] { -1, -2 }, -4);
    }

    @Test
    @DisplayName("Check empty integer array input")
    void checkEmptyInputTest() {
        List<KPair> kPairList = KComplementaryFinder.findKComplementaryPairs(new int[] {}, 0);
        assertNotNull(kPairList, "The k-pair list should not be null");
        assertTrue(kPairList.isEmpty(), "The k-pair list should be empty");
    }

    @Test
    @DisplayName("Check null integer array input")
    void checkNullInputTest() {
        assertThrows(NullPointerException.class, () -> KComplementaryFinder.findKComplementaryPairs(null, 0));
    }

    @Test
    @DisplayName("Check K-complementary pairs with alternative algorithm")
    void checkAlternativeAlgorithmTest() {
        checkValidKPairsAlternative(new int[] { 1, 0 }, 1);
        checkValidKPairsAlternative(new int[] { 0, 0 }, 0);
        checkValidKPairsAlternative(new int[] { 1, 2, 2, 3 }, 3);
        checkValidKPairsAlternative(new int[] { 1, 2, 2, 3 }, 4);
        checkValidKPairsAlternative(new int[] { 2, 1, 3, 5, 4, 3}, 6);
    }

    /**
     * Checks that the size of the found k-complementary pairs
     * is equal to the count algorithm.
     *
     * @param input the input array
     * @param k the k value
     */
    private void countKPairs(int[] input, int k) {
        List<KPair> kComplementaryPairs = KComplementaryFinder.findKComplementaryPairs(input, k);
        assertNotNull(kComplementaryPairs, "K-complementary list should not be null");
        assertEquals(kComplementaryPairs.size(), KComplementaryFinder.countKComplementaryPairs(input, k));
    }

    /**
     * Compute the K-pairs for a given input array and k value
     * and compares against an expected List of K-Pairs
     *
     * @param expected the expected K-pairs array (matrix)
     * @param input the input array string
     * @param k the k value
     */
    private void checkValidKPairs(int[][] expected, int[] input, int k) {
        List<KPair> expectedKPairList = KPair.asList(expected);
        List<KPair> actualKPairList = KComplementaryFinder.findKComplementaryPairs(input, k);
        assertNotNull(actualKPairList, "The k-pair list should not be null");
        assertThat("The k-pair lists do not match", actualKPairList, containsInAnyOrder(expectedKPairList.toArray()));
    }

    /**
     * Compute the K-pairs for a given input array and k value
     * and checks that results in an empty List of K-Pairs
     *
     * @param input the input array string
     * @param k the k value
     */
    private void checkInValidKPairs(int[] input, int k) {
        List<KPair> actualKPairList = KComplementaryFinder.findKComplementaryPairs(input, k);
        assertNotNull(actualKPairList, "The k-pair list should not be null");
        assertEquals(Collections.emptyList(), actualKPairList, "The k-pair list should be empty");
    }

    /**
     * Compute the K-pairs for a given input array and k value
     * using both algorithms and compares their output.
     *
     * @param input the input K-pairs array (matrix)
     * @param k the k value
     */
    private void checkValidKPairsAlternative(int[] input, int k) {
        List<KPair> actualKPairList = KComplementaryFinder.findKComplementaryPairs(input, k);
        List<KPair> expectedKPairList = KComplementaryFinder.findKComplementaryPairsAlternative(input, k);
        assertThat("The k-pair lists do not match", actualKPairList, containsInAnyOrder(expectedKPairList.toArray()));
    }


}
