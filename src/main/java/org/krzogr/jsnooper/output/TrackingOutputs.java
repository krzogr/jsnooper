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

package org.krzogr.jsnooper.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.krzogr.jsnooper.tracking.TrackingUtils.closeQuietly;
import static org.krzogr.jsnooper.tracking.TrackingUtils.getTrackingOutputFile;

public class TrackingOutputs {


    public static TrackingOutput createNullTrackingOutput() {
        return NullTrackingOutput.instance;
    }

    public static TrackingOutput createTrackingOutput(String outputDirectory, String outputFilePrefix) {
        if (outputDirectory == null && outputFilePrefix == null) {
            return new StdOutTrackingOutput();
        }

        File outFile = getTrackingOutputFile(outputDirectory, outputFilePrefix);
        try {
            return new FileTrackingOutput(outFile);
        } catch (Exception e) {
            System.err.println("Error while creating output file " + outFile.getAbsolutePath() + ": " + e.getMessage());
            return NullTrackingOutput.instance;
        }
    }

    private static class NullTrackingOutput implements TrackingOutput {
        private static final NullTrackingOutput instance = new NullTrackingOutput();

        @Override
        public void writeTrackingInfo(String trackingInfo) {
        }

        @Override
        public void close() {
        }
    }

    private static class StdOutTrackingOutput implements TrackingOutput {
        @Override
        public void writeTrackingInfo(String trackingInfo) {
            System.out.println(trackingInfo);
        }

        @Override
        public void close() {
        }
    }

    private static class FileTrackingOutput implements TrackingOutput {
        private final File file;
        private final BufferedWriter writer;
        private boolean closed;

        private FileTrackingOutput(File file) throws IOException {
            this.file = file;
            this.writer = new BufferedWriter(new FileWriter(file));
            printHeader();
        }

        @Override
        public synchronized void writeTrackingInfo(String trackingInfo) {
            if (!closed) {
                try {
                    writer.write(trackingInfo);
                    writer.newLine();
                    writer.flush();
                } catch (Exception e) {
                    System.err.println("Error while writing to file " + file.getAbsolutePath() + ": " + e.getMessage());
                    close();
                }
            }
        }

        @Override
        public synchronized void close() {
            if (!closed) {
                closed = true;
                closeQuietly(writer);
            }
        }

        private void printHeader() throws IOException {
            writer.append("TIMESTAMP,THREAD,OBJECT,PATH");
            writer.newLine();
        }
    }
}
