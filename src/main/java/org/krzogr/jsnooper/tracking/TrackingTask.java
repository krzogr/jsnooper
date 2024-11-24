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

import org.krzogr.jsnooper.config.TrackingConfig;
import org.krzogr.jsnooper.output.TrackingOutput;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TrackingTask {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final String Separator = ",";

    private final String trackingSocketThreadName;

    public TrackingTask(String trackingSocketThreadName) {
        this.trackingSocketThreadName = trackingSocketThreadName;
    }

    public void trackObject(Object obj, TrackingScope scope, TrackingConfig config, TrackingOutput output) {
        String threadName = Thread.currentThread().getName();
        if (trackingSocketThreadName.equals(threadName) || !config.canTrackThread(threadName)) {
            return;
        }

        String objectId = getObjectId(obj);
        String objectPath = getObjectPath(obj, scope, config);

        StringBuilder buffer = scope.getBuffer();
        buffer.setLength(0);

        buffer.append(LocalDateTime.now().format(formatter)).append(Separator);
        buffer.append(threadName).append(Separator);
        buffer.append(objectId).append(Separator);
        buffer.append(objectPath);

        output.writeTrackingInfo(buffer.toString());
    }

    private String getObjectId(Object obj) {
        return obj.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(obj));
    }

    private String getObjectPath(Object obj, TrackingScope scope, TrackingConfig config) {
        StringBuilder buffer = scope.getBuffer();
        buffer.setLength(0);
        String sep = "";

        StackTraceElement[] frames = Thread.currentThread().getStackTrace();
        for (int i = frames.length-1; i > 1; i--) {
            StackTraceElement frame = frames[i];
            String className = frame.getClassName();

            if (className.startsWith("org.krzogr.jsnooper") || className.startsWith("java.lang.Object")) {
                break;
            }

            buffer.append(sep);
            appendFrame(buffer, frame);
            sep = " -> ";

            if (config.isLeafClass(frame.getClassName())) {
                break;
            }
        }

        return buffer.toString();
    }

    private void appendFrame(StringBuilder buffer, StackTraceElement frame) {
        buffer.append(frame.getClassName()).append(".").append(frame.getMethodName());

        if (frame.getFileName() != null) {
            buffer.append("(").append(frame.getFileName());
            if (frame.getLineNumber() > 0) {
                buffer.append(":").append(frame.getLineNumber());
            }
            buffer.append(")");
        }
    }
}
