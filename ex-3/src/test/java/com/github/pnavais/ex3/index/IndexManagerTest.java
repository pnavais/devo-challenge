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

import com.github.pnavais.ex3.api.index.IndexManager;
import com.github.pnavais.ex3.test.Ex3TestConfig;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import lombok.extern.java.Log;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link SimpleIndexManager}
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = Ex3TestConfig.class)
@DisplayName("Unit tests for the IndexManager class")
@Log
public class IndexManagerTest {

    @Autowired
    private IndexManager manager;

    /** In-memory filesystem */
    private static final FileSystem testFileSystem = Jimfs.newFileSystem(Configuration.unix());

    /** The in-memory test directory */
    private static Path testDir;

    /** The testing set of terms */
    private static final Set<String> DEFAULT_TERMS = ImmutableSet.of("Dummy", "test", "string");

    @BeforeAll
    static void setup() throws IOException {
        testDir = Files.createDirectory(testFileSystem.getPath("/test_files"));
    }

    @AfterEach
    void tearDown() throws IOException {
        manager.clear();
        Files.list(testDir).forEach(path -> {
            try {
                Files.delete(path);
            } catch (IOException e) {
                fail("Error cleaning test directory");
            }
        });
    }

    @Test
    @DisplayName("Test IndexManager injection")
    void managerInjectionTest() {
        assertNotNull(manager, "The index manager is null");
    }

    @Test
    @DisplayName("Fill the index with an in-memory directory containing test files")
    void fillIndexTest() {
        Path doc1 = writeTestFile(testDir, "doc1.txt", ImmutableList.of("Dummy string for test purposes"));
        Path doc2 = writeTestFile(testDir, "doc2.txt", ImmutableList.of("Dummy string"));

        fillIndex(DEFAULT_TERMS);
    }

    @Test
    @DisplayName("Retrieve and verify term statistics for single match")
    void retrieveSingleTermStatsTest() {
        Path doc1 = writeTestFile(testDir, "doc1.txt", ImmutableList.of("Dummy string for test purposes"));
        Path doc2 = writeTestFile(testDir, "doc2.txt", ImmutableList.of("Dummy string"));

        fillIndex(DEFAULT_TERMS);

        // Test retrieval of term statistics
        List<DocTerm> testData = manager.getDocTermsFor("test");
        assertNotNull(testData, "Error retrieving term statistics");
        assertEquals(1, testData.size(), "Term statistics size mismatch");
        DocTerm docTerm = testData.get(0);
        assertNotNull(docTerm, "Error retrieving doc term");
        assertEquals(doc1, docTerm.getDocument(), "Document mismatch");
        assertEquals(1, docTerm.getOccurrences(), "Incorrect number of occurrences found");
        assertEquals(5, docTerm.getWordCount(), "Incorrect total number of words found");
    }

    @Test
    @DisplayName("Retrieve and verify term statistics for multi match")
    void retrieveMultiTermStatsTest() {
        Path doc1 = writeTestFile(testDir, "doc1.txt", ImmutableList.of("Dummy string for test purposes"));
        Path doc2 = writeTestFile(testDir, "doc2.txt", ImmutableList.of("Dummy string"));

        fillIndex(DEFAULT_TERMS);

        // Test retrieval of term statistics
        List<DocTerm> testData = manager.getDocTermsFor("Dummy");
        assertNotNull(testData, "Error retrieving term statistics");
        assertEquals(2, testData.size(), "Term statistics size mismatch");
        assertThat("The document terms did not match", testData,
                containsInAnyOrder(DocTerm.of("Dummy", doc1), DocTerm.of("Dummy", doc2)));
    }

    @Test
    @DisplayName("Retrieve and verify term TF/IDF")
    void retrieveTermTFIDFFStatsTest() {
        Path doc1 = writeTestFile(testDir, "doc1.txt", ImmutableList.of("Dummy string for test purposes"));
        writeTestFile(testDir, "doc2.txt", ImmutableList.of("Dummy string"));

        fillIndex(DEFAULT_TERMS);

        // Test retrieval of term statistics
        assertEquals((1.0/5) * Math.log10(2.0/1), manager.getTermTfIdf("test", doc1), "Tf/Idf statistic mismatch");

        assertEquals(0, manager.getTermTfIdf("purposes", doc1), "Tf/Idf should be zero since not an indexed term");
        assertEquals(0, manager.getTermTfIdf("Dummy", doc1), "Tf/Idf should be zero since present in both documents");
    }

    @Test
    @DisplayName("Retrieve and verify term TF/IDF")
    void retrieveTermTFIDFFAverageStatsTest() {
        Path doc1 = writeTestFile(testDir, "doc1.txt", ImmutableList.of("Dummy string for test purposes"));
        Path doc2 = writeTestFile(testDir, "doc2.txt", ImmutableList.of("Dummy string"));

        fillIndex(DEFAULT_TERMS);

        // Test retrieval of term statistics
        assertEquals(manager.getTermTfIdf("test", doc1) / 3, manager.getTermsTfIdfFor(doc1),
                "Average Tf/Idf statistic mismatch");
    }

    @Test
    @DisplayName("Fill the index with no terms")
    void fillIndexWithNoTermsTest() {
        Path doc2 = writeTestFile(testDir, "doc1.txt", ImmutableList.of("Dummy string"));
        manager.loadFrom(testDir);

        await().atMost(5, SECONDS);
        assertEquals(0, manager.size(), "Index document size mismatch");
    }

    @Test
    @DisplayName("Fill the index with no terms")
    void fillIndexWithNoFilesTest() {
        manager.loadFrom(testDir);

        await().atMost(5, SECONDS);
        assertEquals(0, manager.size(), "Index document size mismatch");
    }

    /**
     * Fills the index with test documents and verify its
     * correct population asynchronously.
     */
    private void fillIndex(Set<String> terms) {
        manager.setTerms(terms);
        manager.loadFrom(testDir);

        await().atMost(5, SECONDS).until(() -> manager.size() != 0);
        assertEquals(2, manager.size(), "Index document size mismatch");
    }

    /**
     * Creates a dummy test file in the given directory.
     *
     * @param dir      the target directory
     * @param fileName the file to create
     */
    static Path writeTestFile(Path dir, String fileName, List<String> contents) {
        Path testFile = dir.resolve(fileName);
        try {
            Files.write(testFile, contents, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.throwing("IndexManagerTest", "writeTestFile", e);
        }
        return testFile;
    }

}
