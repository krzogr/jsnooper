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

package org.krzogr.jsnooper.tracking;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TrackingUtils {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    public static void closeQuietly(AutoCloseable output) {
        try {
            if (output != null) {
                output.close();
            }
        } catch (Exception e) {
            System.err.println("Error while closing " + output.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    public static File getTrackingOutputFile(String outputDirectory, String outputFilePrefix) {
        String dir = outputDirectory != null ? outputDirectory : ".";
        String prefix = outputFilePrefix != null ? outputFilePrefix : "snapshot-";
        String timestamp = LocalDateTime.now().format(formatter);
        return new File(dir, prefix + timestamp + ".csv");
    }
}
