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

package com.github.pnavais.ex3.server;
import com.github.pnavais.ex3.api.event.BusEvent;
import com.github.pnavais.ex3.api.event.BusEventListener;
import com.github.pnavais.ex3.api.index.IndexManager;
import com.github.pnavais.ex3.api.ranking.RankingManager;
import com.github.pnavais.ex3.api.watcher.DirectoryWatcher;
import com.github.pnavais.ex3.config.TfIdfServerConfig;
import com.github.pnavais.ex3.event.FileEvent;
import com.github.pnavais.ex3.event.SimpleEventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Timer;
import java.util.TimerTask;

/**
 * The TF/IDF Server starts as a daemon threads to wait for file events
 * and recompute the TF/IDF statistics and the ranking thread to display
 * at a fixed frequency the statistic ranking for the documents in the set.
 */
@Component
@Slf4j
public class TfIdfServer implements Runnable, BusEventListener {

    /**
     * The server thread
     */
    private final Thread serverThread;

    /**
     * The directory watcher
     */
    private final DirectoryWatcher watcher;

    /**
     * The index manager
     */
    private final IndexManager indexManager;

    /**
     * The ranking manager
     */
    private final RankingManager rankingManager;

    @Autowired
    public TfIdfServer(DirectoryWatcher watcher, IndexManager manager, RankingManager rankingManager) {
        this.watcher = watcher;
        this.indexManager = manager;
        this.rankingManager = rankingManager;

        this.serverThread = new Thread(this);
        this.serverThread.setDaemon(true);

        SimpleEventBus.getDefault().register(FileEvent.class, this);
    }

    /**
     * Starts the file directory watcher and the server
     * thread.
     */
    public void start(TfIdfServerConfig config) throws InterruptedException {
        // Initialize the index
        this.indexManager.setTerms(config.getTerms());
        this.indexManager.loadFrom(config.getInput());

        // Initialize the directory watcher
        watcher.registerPath(config.getInput());
        watcher.start();

        // Starts a ranking timer with the configured fixed rate (period) in milliseconds
        long period = config.getPeriod() * 1000L;
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                rankingManager.displayTopResults(config.getMaxResults(), indexManager);
            }
        }, period, period);

        log.info("Looking for terms {} in directory [{}]. Ranking displayed every {}s", config.getTerms(),
                config.getInput(), config.getPeriod());

        // Starts the server thread
        serverThread.start();
        serverThread.join();
    }

    /**
     * Stops the file directory watcher and the server thread
     */
    public void stop() {
        log.info("Stopping server");
        watcher.stop();
        serverThread.interrupt();
    }

    /**
     * Performs the loop to trigger the processing of incoming
     * file notification events.
     */
    @Override
    public void run() {
        do {
            synchronized (this) {
                try {
                    wait();
                    indexManager.refresh();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        } while (true);
    }

    /**
     * Reacts upon file notifications by adding the files to the
     * index and notifying the server thread of the incoming bus notification.
     *
     * @param e the file event.
     */
    @Override
    public void onEvent(BusEvent e) {
        if (e instanceof FileEvent) {
            synchronized (this) {
                ((FileEvent) e).getFileList().forEach(indexManager::addFile);
                notifyAll();
            }
        }
    }

}
