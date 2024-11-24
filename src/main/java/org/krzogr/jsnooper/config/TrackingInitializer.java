/*
 * Copyright (C) 2025 krzogr (krzogr@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.krzogr.jsnooper.config;

import java.io.FileInputStream;
import java.util.Properties;

import static org.krzogr.jsnooper.socket.TrackingSocketThread.startTrackingSocketThread;
import static org.krzogr.jsnooper.tracking.ObjectTracker.getTrackingConfig;

public class TrackingInitializer {
    public static void initTracking(String argsString) {
        preloadInternalClasses();
        TrackingConfig config = getTrackingConfig();

        if (argsString == null || argsString.isEmpty()) {
            config.configureDefaults();
            return;
        }

        String[] args = argsString.split(",");
        String configFile = getConfigFile(args);

        if (configFile != null) {
            loadConfig(configFile, config);
        } else {
            config.configureDefaults();
        }

        Integer argsPort = getSocketPort(args);
        Integer port = argsPort != null ? argsPort : config.getSocketPort();
        if (port != null) {
            startTrackingSocketThread(port);
        }
    }

    /**
     * ObjectTracker class must be loaded before instrumentation starts because it will be referenced by every new object after instrumentation.
     */
    private static void preloadInternalClasses() {
        try {
            Class.forName("org.krzogr.jsnooper.tracking.ObjectTracker");
        } catch (Exception e) {
            throw new RuntimeException("Fatal error while preloading jsnooper classes: " + e.getMessage(), e);
        }
    }

    private static Integer getSocketPort(String[] args) {
        String port = getArg(args, "port=");
        return port != null ? Integer.valueOf(port) : null;
    }

    private static String getConfigFile(String[] args) {
        return getArg(args, "config=");
    }

    private static String getArg(String[] args, String argPrefix) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith(argPrefix)) {
                return args[i].substring(argPrefix.length());
            }
        }

        return null;
    }

    private static void loadConfig(String configPath, TrackingConfig config) {
        try (FileInputStream input = new FileInputStream(configPath)) {
            Properties props = new Properties();
            props.load(input);
            config.loadFrom(props);
        } catch (Exception e) {
            throw new RuntimeException("Fatal error wile loading config from " + configPath + ": " + e.getMessage(), e);
        }
    }
}
