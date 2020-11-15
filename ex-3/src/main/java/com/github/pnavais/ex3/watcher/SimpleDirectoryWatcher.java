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

package com.github.pnavais.ex3.watcher;

import com.github.pnavais.ex3.api.watcher.DirectoryWatcher;
import com.github.pnavais.common.file.FileCommons;
import com.github.pnavais.ex3.event.FileEvent;
import com.github.pnavais.ex3.event.SimpleEventBus;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

/**
 * The directory watcher allows to obtain notifications on
 * directory changes.
 */
@Component
@Slf4j
public class SimpleDirectoryWatcher implements DirectoryWatcher {

    /**
     * The Watch service.
     */
    private final WatchService watchService;

    /**
     * The registered key map
     */
    private final Map<WatchKey, Path> keyMap;

    /**
     * The thread for file events reception
     */
    private Thread watchThread;

    /**
     * Creates a watcher using the default file system
     */
    public SimpleDirectoryWatcher() throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        this.keyMap = new LinkedHashMap<>();
    }

    /**
     * Register the following string path with the watch service
     *
     * @param path the path to register
     */
    @Override
    public void registerPath(@NonNull String path) {
        registerPath(Paths.get(path));
    }

    /**
     * Register a full path with the watch service.
     *
     * @param path the path to register
     */
    @Override
    public void registerPath(@NonNull Path path) {
        if (FileCommons.isDirectory(path) && Files.exists(path)) {
            try {
                WatchKey watchKey = path.register(watchService, ENTRY_CREATE);
                this.keyMap.put(watchKey, path);
                log.debug("Registered path \"{}\" for event notifications", path.toString());
            } catch (IOException e) {
                log.error("Error registering path for event notifications");
            }
        } else {
            log.warn("The given path \"{}\" cannot be registered (check that path exists and points to a valid " +
                    "directory)", path);
        }
    }

    /**
     * Starts the thread to watch for file events
     */
    @Override
    public void start() {
        this.watchThread = new Thread(this::processEvents);
        this.watchThread.setDaemon(true);
        this.watchThread.start();
    }

    /**
     * Stops the thread to watch for file events
     */
    @Override
    public void stop() {
        if (this.watchThread.isAlive() && !(this.watchThread.isInterrupted())) {
            this.watchThread.interrupt();
        }
    }

    /**
     * Process file events.
     */
    private void processEvents() {
        WatchKey key;
        try {
            while ((key = watchService.take()) != null) {
                if (keyMap.containsKey(key)) {
                    checkNewFiles(key).ifPresent(files -> {
                        log.debug("Received {} new file{}", files.size(), files.size()>1 ? "s" : "");
                        SimpleEventBus.getDefault().publish(FileEvent.with(files));
                    });

                    // reset key and remove from set if directory no longer accessible
                    boolean valid = key.reset();
                    if (!valid) {
                        keyMap.remove(key);
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Verify poll events to check if the event is valid
     * or not and new files have been created.
     *
     * @param key the key to verify
     * @return the list of new files or empty
     */
    private Optional<List<Path>> checkNewFiles(WatchKey key) {
        List<Path> newFiles = new ArrayList<>();

        for (WatchEvent<?> event : key.pollEvents()) {
            if ((event.kind() == ENTRY_CREATE) && (event.context() instanceof Path)) {
                newFiles.add(keyMap.get(key).resolve((Path)event.context()));
            }
        }

        return newFiles.isEmpty() ? Optional.empty() : Optional.of(newFiles);
    }

}
