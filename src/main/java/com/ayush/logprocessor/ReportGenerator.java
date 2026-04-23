package com.ayush.logprocessor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

/**
 * Generates final report file from aggregated data.
 */
public class ReportGenerator {

    public void generate(Path outputPath, ActivityAggregator aggregator) throws IOException {
        if (outputPath.getParent() != null) {
            Files.createDirectories(outputPath.getParent());
        }

        Map<String, Integer> sortedUsers = aggregator.getUserCountsSortedDesc();
        Map<String, Integer> actionTotals = new TreeMap<>(aggregator.getActionTypeCountsSnapshot());
        Map.Entry<String, Integer> mostActive = aggregator.getMostActiveUser();

        try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
            writer.write("===== USER ACTIVITY REPORT =====");
            writer.newLine();

            for (Map.Entry<String, Integer> entry : sortedUsers.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue() + " actions");
                writer.newLine();
            }

            writer.newLine();
            writer.write("----- ACTION TYPE TOTALS -----");
            writer.newLine();
            for (Map.Entry<String, Integer> actionEntry : actionTotals.entrySet()) {
                writer.write(actionEntry.getKey() + ": " + actionEntry.getValue());
                writer.newLine();
            }

            writer.newLine();
            if (mostActive != null) {
                writer.write("Most active user: " + mostActive.getKey() + " (" + mostActive.getValue() + " actions)");
            } else {
                writer.write("Most active user: N/A");
            }
            writer.newLine();
        }
    }
}
