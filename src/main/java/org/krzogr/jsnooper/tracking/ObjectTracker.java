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
import org.krzogr.jsnooper.socket.TrackingSocketThread;

import static org.krzogr.jsnooper.output.TrackingOutputs.*;
import static org.krzogr.jsnooper.tracking.TrackingUtils.closeQuietly;

public class ObjectTracker {
    private static final TrackingControl control = new TrackingControl();
    private static final TrackingConfig config = new TrackingConfig();
    private static final TrackingTask task = new TrackingTask(TrackingSocketThread.ThreadName);
    private static TrackingOutput output = createNullTrackingOutput();

    public static TrackingConfig getTrackingConfig() {
        return config;
    }

    public static boolean isTrackingInProgress() {
        return control.isTrackingEnabled();
    }

    public static synchronized void startTracking() {
        if (!control.isTrackingEnabled()) {
            output = createTrackingOutput(config.getOutputDirectory(), config.getOutputFilePrefix());
            control.enableTracking();
        }
    }

    public static synchronized void stopTracking() {
        control.disableTracking();

        TrackingOutput prevOutput = output;
        output = createNullTrackingOutput();
        closeQuietly(prevOutput);
    }

    public static void trackObject(Object obj) {
        TrackingScope scope = control.getTrackingScope();

        if (scope.isTrackingEnabled()) {
            try {
                scope.disableTracking();
                task.trackObject(obj, scope, config, output);
            } catch (Exception e) {
                System.err.println("Error while tracking object: " + e.getMessage());
                e.printStackTrace();
            } finally {
                scope.enableTracking();
            }
        }
    }
}
