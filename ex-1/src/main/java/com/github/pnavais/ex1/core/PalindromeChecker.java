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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * An implementation of a palindrome checker for input strings.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PalindromeChecker {

    /**
     * Evaluates an input string to determine if it represents
     * a valid palindrome or not, that is, the string matches its reverse
     * form.
     * Example : "abba" is valid palindrome , "abc" is not
     * The implementation of the palindrome algorithm is as follows:
     *  - The string input is indexed through its front and end (i_front, i_end), initially
     *    i_front = 0 and i_end = length of input - 1
     *  - On a loop while the "middle" of the string is not reached, the front
     *  index is incremented and the end one is decremented.
     *  - When the comparison between indexed element at front and end does not
     *  match an invalid palindrome is detected and the process is stopped.
     *  - If the "middle" of the string is reached that is i_front>=i_end a valid
     *  palindrome is detected and the process is complete.
     *
     *  Since the algorithm has to consider at most half the elements of the array
     *  it has a time complexity of O(n).
     *
     * @param input the input arguments
     * @return true if the input string is a palindrome, false otherwise
     */
    public static boolean isPalindrome(@NonNull String input) {
        boolean res = true;

        for (int i_front = 0, i_end = input.length()-1; i_front<i_end; i_front++, i_end--) {
            if (input.charAt(i_front) != input.charAt(i_end)) {
                res = false;
                break;
            }
        }

        return res;
    }

}

