# devo-challenge
Devo challenge

## Rationale
This is my implementation of the Devo challenge. It has been developed in Java 8 using Spring Boot (2.4.0) and packaged as a Maven multi-module project.

## Project Structure
Each exercise in the challenge has been defined in its own Maven module and a separate executable jar can be used for testing the implementation.
For each module, Junit 5 unit tests have been defined grouped in a suite.

![doc1](images/doc1.png "Project structure"

## Buid instructions

Assuming Maven is installed execute the following command in the project root : 
```
mvn clean package
```
After succesful packaging the following structure is generated :
![doc2](images/doc2.png "Maven target structure"

Each module provides an executable jar with a simple tester application.
Refer to [running section](#Running the exercises) to find instructions on how to launch the exercise tester applications.

## Exercises implementation details
### Exercise 1 : Palindrome checker
The algorithm coded evaluates an input string to determine if it represents a valid palindrome or not, that is, the string matches its reverse form.
Example : "abba" is valid palindrome , "abc" is not

The implementation of the palindrome algorithm is as follows:
 - The string input is indexed through its front and end (i_front, i_end), initially
   i_front = 0 and i_end = length of input - 1
 - On a loop while the "middle" of the string is not reached, the front
   index is incremented and the end one is decremented.
 - When the comparison between indexed element at front and end does not
   match an invalid palindrome is detected and the process is stopped.
 - If the "middle" of the string is reached that is i_front>=i_end a valid
   palindrome is detected and the process is complete.

Since the algorithm has to consider at most half the elements of the array it has a time complexity of O(n/2) == O(n) discarding constants.

### Exercise 2 : K-Complementary finder
The exercise asks to find/count K-complementary pairs in a given array of integers.
Given an integer Array A, any pair (i, j) is K-complementary if K = A[i] + A[j];
Example : A = [ 1, 2, 2, 3 ] k = 4 contains 2 valid k-pairs {0,3} and {1,2}

Two different algorithms were provided : 
- Count k-pairs algorithm
- Find actual k-pairs algorithm

The implementation of the k-complementary count algorithm is as follows:
 - For each current value in the array, the "distance" i.e. the difference
 between k and the value (i.e. the k-complementary) is checked in the map ,
 if found, the pairs count is increased by the number of occurrences of
 that complementary element.
 - The element is put in the map with an initial counter if not present
 or increased by one if already there.

Since the check / insertions in the map have a time complexity of O(1) and the algorithm performs a full loop across the input array, the total
time complexity is considered O(n).

NOTE: This algorithm does not account for duplicates, i.e. the pairs (i,j) and (j,i) are considered equals.



## Running the exercises 

## Development environment
The different exercises where developed and tested using the following system environment :
- OS    : Mac OS X 10.15.7 x86_64
- JDK   : Amazon Corretto JDK 8 (1.8.0_171)
- Maven : 3.6.3 
