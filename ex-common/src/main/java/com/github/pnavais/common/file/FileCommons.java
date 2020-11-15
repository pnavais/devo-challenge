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

package com.github.pnavais.common.file;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Contains utility classes for file operations.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileCommons {

    /**
     * Checks whether a path is a directory or not
     *
     * @param path the path to check
     * @return true if the path represents a directory, false otherwise
     */
    public static boolean isDirectory(@NonNull Path path) {
        boolean isDirectory;
        try {
            isDirectory = path.toFile().isDirectory();
        } catch (Exception e) {
            isDirectory = Files.isDirectory(path);
        }
        return isDirectory;
    }

}
