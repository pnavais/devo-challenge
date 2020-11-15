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

package com.github.pnavais.common.system;

import java.util.Locale;

/**
 * Platform specific utility methods
 */
public final class SystemCommons {

    /**
     * types of Operating Systems
     */
    public enum OSName {
        WINDOWS, MAC_OS, LINUX, OTHER
    }

    /* Cached result of OS detection */
    protected static OSName osName;

    /**
     * Detect the operating system from the os.name System property and cache
     * the result.
     *
     * @return - the operating system detected
     */
    public static OSName getOperatingSystemType() {
        if (osName == null) {
            String osAux = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            if ((osAux.contains("mac")) || (osAux.contains("darwin"))) {
                osName = OSName.MAC_OS;
            } else if (osAux.contains("win")) {
                osName = OSName.WINDOWS;
            } else if (osAux.contains("nix") || osAux.contains("nux") || osAux.contains("aix")) {
                osName = OSName.LINUX;
            } else {
                osName = OSName.OTHER;
            }
        }
        return osName;
    }

    /**
     * Checks whether the operating system is Windows or not.
     *
     * @return true if the operating system is Windows, false otherwise
     */
    public static boolean isWindows() {
        return getOperatingSystemType() == OSName.WINDOWS;
    }

}
