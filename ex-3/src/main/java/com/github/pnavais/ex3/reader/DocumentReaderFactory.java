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

package com.github.pnavais.ex3.reader;

import com.github.pnavais.ex3.api.reader.DocumentReader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Supplier;

/**
 * A factory to create {@link DocumentReader} instances
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentReaderFactory {

    /** The default document reader supplier */
    private static Supplier<DocumentReader> readerSupplier = BufferedDocumentReader::new;

    /**
     * Retrieves a new document reader.
     *
     * @return the document reader
     */
    public static DocumentReader getReader() {
        return readerSupplier.get();
    }

    /**
     * Sets the supplier of the document reader instances.
     *
     * @param readerSupplier the reader supplier
     */
    public static void setReaderSupplier(Supplier<DocumentReader> readerSupplier) {
        DocumentReaderFactory.readerSupplier = readerSupplier;
    }
}
