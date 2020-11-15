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

package com.github.pnavais.ex3;

import com.github.pnavais.common.cli.ShortErrorMessageHandler;
import com.github.pnavais.ex3.config.TfIdfServerConfig;
import com.github.pnavais.ex3.server.TfIdfServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import picocli.CommandLine;

/**
 * This is the entry point application allowing to launch
 * the TF/IDF server with the given parameters.
 */
@SpringBootApplication
@Slf4j
public class TfIdfServerApp implements CommandLineRunner {

    /** Server configuration */
    private static TfIdfServerConfig config;

    /** Server instance */
    TfIdfServer server;

    @Autowired
    public TfIdfServerApp(TfIdfServer server ) {
        this.server = server;
    }

    @Override
    public void run(String[] args) throws Exception {
        server.start(config);
    }

    @EventListener
    public void onShutdown(ContextClosedEvent event) {
        server.stop();
    }

    /**
     * Main entry point of the applications, process
     * the arguments and store the configuration statically.
     * If the configuration is valid, launch the Spring
     * application which eventually will start the server.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int exitCode = new CommandLine(new TfIdfServerConfig(cfg -> {
            TfIdfServerApp.config = cfg;
            SpringApplication.run(TfIdfServerApp.class, args);
        })).setParameterExceptionHandler(new ShortErrorMessageHandler())
                .execute(args);
        System.exit(exitCode);
    }

}
