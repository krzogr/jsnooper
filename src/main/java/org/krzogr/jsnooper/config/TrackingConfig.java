/*
 * Copyright (C) 2024 krzogr (krzogr@gmail.com)
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

import java.util.*;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public class TrackingConfig {
    private static final String MatchAll = "*";

    private List<String> excludedThreads;
    private List<String> includedThreads;
    private List<String> leafClasses;

    private String outputDirectory;
    private String outputFilePrefix;
    private Integer port;

    public TrackingConfig() {
        excludedThreads = emptyList();
        includedThreads = emptyList();
        leafClasses = emptyList();
        outputDirectory = null;
        outputFilePrefix = null;
    }

    public List<String> getExcludedThreads() {
        return excludedThreads;
    }

    public void setExcludedThreads(List<String> value) {
        excludedThreads = requireNonNull(value);
    }

    public List<String> getIncludedThreads() {
        return includedThreads;
    }

    public void setIncludedThreads(List<String> value) {
        includedThreads = requireNonNull(value);
    }

    public List<String> getLeafClasses() {
        return leafClasses;
    }

    public void setLeafClasses(List<String> value) {
        leafClasses = requireNonNull(value);
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String value) {
        outputDirectory = value;
    }

    public String getOutputFilePrefix() {
        return outputFilePrefix;
    }

    public void setOutputFilePrefix(String value) {
        outputFilePrefix = value;
    }

    public Integer getSocketPort() {
        return port;
    }

    public void setSocketPort(Integer port) {
        this.port = port;
    }

    public void loadFrom(Properties props) {
        leafClasses = getStringList(props, "leaf-classes");
        excludedThreads = getStringList(props, "excluded-threads");
        includedThreads = getStringList(props, "included-threads");
        outputDirectory = getString(props, "output-directory");
        outputFilePrefix = getString(props, "output-file-prefix");
        port = getInteger(props, "port");

        if (!props.containsKey("leaf-classes")) {
            configureDefaultLeafClasses();
        }
    }

    public boolean canTrackThread(String threadName) {
        for (int i = 0; i < includedThreads.size(); i++) {
            String pattern = includedThreads.get(i);
            if (pattern.equals(MatchAll) || threadName.startsWith(pattern)) {
                return true;
            }
        }

        for (int i = 0; i < excludedThreads.size(); i++) {
            String pattern = excludedThreads.get(i);
            if (pattern.equals(MatchAll) || threadName.startsWith(pattern)) {
                return false;
            }
        }

        return true;
    }

    public boolean isLeafClass(String className) {
        for (int i = 0; i < leafClasses.size(); i++) {
            String pattern = leafClasses.get(i);
            if (className.startsWith(pattern)) {
                return true;
            }
        }

        return false;
    }

    public void configureDefaults() {
        configureDefaultLeafClasses();
    }

    private void configureDefaultLeafClasses() {
        leafClasses = singletonList("java.");
    }

    private List<String> getStringList(Properties props, String key) {
        String result = props.getProperty(key);
        if (result != null && !result.trim().isEmpty()) {
            return stream(result.trim().split(",")).collect(toList());
        }

        return emptyList();
    }

    private String getString(Properties props, String key) {
        String result = props.getProperty(key);
        return (result != null && !result.trim().isEmpty()) ? result.trim() : null;
    }

    private Integer getInteger(Properties props, String key) {
        String result = props.getProperty(key);
        return (result != null && !result.trim().isEmpty()) ? Integer.valueOf(result.trim()) : null;
    }
}
