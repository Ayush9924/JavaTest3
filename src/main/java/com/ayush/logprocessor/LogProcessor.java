package com.ayush.logprocessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Reads a single log file and updates the shared ActivityAggregator.
 * One instance is intended to run in one thread.
 */
public class LogProcessor implements Runnable {

    private final Path filePath;
    private final ActivityAggregator aggregator;

    public LogProcessor(Path filePath, ActivityAggregator aggregator) {
        this.filePath = filePath;
        this.aggregator = aggregator;
    }

    @Override
    public void run() {
        if (filePath == null || aggregator == null) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            int lineNo = 0;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                LogEntry entry = LogEntry.fromLine(line);
                if (entry == null) {
                    System.err.printf("[WARN] Skipping malformed line in %s at line %d: %s%n",
                            filePath.getFileName(), lineNo, line);
                    continue;
                }

                aggregator.record(entry);
            }
        } catch (IOException ex) {
            System.err.printf("[ERROR] Failed to read file %s: %s%n", filePath, ex.getMessage());
        }
    }
}
