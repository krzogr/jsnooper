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
import java.net.Socket;

import static org.krzogr.jsnooper.tracking.ObjectTracker.startTracking;
import static org.krzogr.jsnooper.tracking.ObjectTracker.stopTracking;
import static org.krzogr.jsnooper.tracking.TrackingUtils.closeQuietly;

public class TrackingSocketSession {
    private final static String START_COMMAND = "start";
    private final static String STOP_COMMAND = "stop";
    private final static String QUIT_COMMAND = "quit";

    public void runSession(Socket socket) {
        try {
            runSessionLoop(socket);
        } catch (IOException e) {
            if (!socket.isClosed()) {
                System.err.println("Fatal error while processing socket commands: " + e.getMessage());
                closeQuietly(socket);
            }
        }
    }

    private void runSessionLoop(Socket socket) throws IOException {
        try (InputStream input = socket.getInputStream();
             OutputStream output = socket.getOutputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));

            printUsage(writer);
            printPrompt(writer);

            String command = reader.readLine();

            while (command != null && socket.isConnected()) {
                runCommand(command, socket, writer);
                printPrompt(writer);
                command = reader.readLine();
            }
        }
    }

    private void printUsage(BufferedWriter writer) throws IOException {
        writer.write("jsnooper control panel");
        writer.newLine();

        writer.append("Commands: ")
                .append(START_COMMAND)
                .append(" | ").append(STOP_COMMAND)
                .append(" | ").append(QUIT_COMMAND);

        writer.newLine();
        writer.flush();
    }

    private void printPrompt(BufferedWriter writer) throws IOException {
        writer.write("> ");
        writer.flush();
    }

    private void runCommand(String command, Socket socket, BufferedWriter writer) throws IOException {
        if (START_COMMAND.equalsIgnoreCase(command)) {
            startTracking();
        } else if(STOP_COMMAND.equalsIgnoreCase(command)) {
            stopTracking();
        } else if(QUIT_COMMAND.equalsIgnoreCase(command)) {
            socket.close();
        } else {
            writer.append("Unknown command: ").append(command);
            writer.newLine();
        }
    }
}
