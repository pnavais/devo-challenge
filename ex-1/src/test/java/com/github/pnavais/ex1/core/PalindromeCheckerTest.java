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

package com.github.pnavais.ex1.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link PalindromeChecker} class
 */
@DisplayName("Unit tests for the PalindromeChecker class")
public class PalindromeCheckerTest {

    @Test
    @DisplayName("Check valid palindromes")
    void checkValidPalindromeTest() {
        Arrays.asList("aba", "abba", "abccba", "123321", "1234321")
                .parallelStream()
                .forEach(s -> assertTrue(PalindromeChecker.isPalindrome(s)));
    }

    @Test
    @DisplayName("Check long valid palindromes")
    void checkLongPalindromeTest() {
        Arrays.asList("amanaplanacanalpanama", "wasitacaroracatisaw")
                .parallelStream()
                .forEach(s -> assertTrue(PalindromeChecker.isPalindrome(s)));
    }

    @Test
    @DisplayName("Check invalid palindromes")
    void checkInvalidPalindromeTest() {
        Arrays.asList("ab", "abbc", "abcdba", "12345321", "Abba", "abBa")
                .parallelStream()
                .forEach(s -> assertFalse(PalindromeChecker.isPalindrome(s)));
    }

    @Test
    @DisplayName("Check single letter palindromes")
    void checkSingleLetterPalindromeTest() {
        // Check letters from [a-z]
        IntStream.range(0, 26).forEachOrdered( i -> assertTrue(PalindromeChecker.isPalindrome(String.valueOf((char)('a'+i)))));
    }

    @Test
    @DisplayName("Check empty strings")
    void checkEmptyStringPalindromeTest() {
        assertTrue(PalindromeChecker.isPalindrome(""));
    }

    @Test
    @DisplayName("Check null palindromes")
    void checkNullStringPalindromeTest() {
        assertThrows(NullPointerException.class, () -> PalindromeChecker.isPalindrome(null));
    }

    @Test
    @DisplayName("Compare with StringBuilder reverse check")
    void checkStringReverseTest() {
        Consumer<String> compareWithStringBuilder = s -> assertEquals(PalindromeChecker.isPalindrome(s), s.equals(new StringBuilder(s).reverse().toString()));
        // Valid palindromes
        Arrays.asList("", "a", "aba", "abba", "abccba", "123321", "1234321")
                .parallelStream().forEach(compareWithStringBuilder);
        // Invalid palindromes
        Arrays.asList("ab", "abbc", "abcdba", "12345321", "Abba", "abBa")
                .parallelStream().forEach(compareWithStringBuilder);
    }

}
