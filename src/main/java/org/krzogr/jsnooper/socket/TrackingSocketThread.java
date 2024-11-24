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

package org.krzogr.jsnooper.socket;

import java.io.*;
import java.net.ServerSocket;

public class TrackingSocketThread {
    public static final String ThreadName = "TrackingSocketThread";

    public static void startTrackingSocketThread(int port) {
        new TrackingSocketThreadImpl(port).start();
    }

    private static class TrackingSocketThreadImpl extends Thread {
        private final int port;

        private TrackingSocketThreadImpl(int port) {
            this.port = port;

            setName(ThreadName);
            setDaemon(true);
        }

        @Override
        public void run() {
            TrackingSocketSession session = new TrackingSocketSession();

            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("jsnooper listening on port " + port);

                while (!isInterrupted()) {
                    session.runSession(serverSocket.accept());
                }
            } catch (IOException e) {
                System.out.println("Fatal error in " + TrackingSocketThread.class.getSimpleName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
