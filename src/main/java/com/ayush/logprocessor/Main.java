package com.ayush.logprocessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;


public class Main {

    public static void main(String[] args) {
        Path logsDir = args.length > 0 ? Paths.get(args[0]) : Paths.get("logs");
        Path reportPath = args.length > 1 ? Paths.get(args[1]) : Paths.get("report.txt");

        if (!Files.exists(logsDir) || !Files.isDirectory(logsDir)) {
            System.err.println("[ERROR] Logs directory does not exist or is not a directory: " + logsDir.toAbsolutePath());
            return;
        }

        List<Path> logFiles = new ArrayList<>();
        try (Stream<Path> stream = Files.list(logsDir)) {
            stream.filter(Files::isRegularFile)
                  .filter(path -> path.getFileName().toString().toLowerCase().endsWith(".txt"))
                  .forEach(logFiles::add);
        } catch (IOException ex) {
            System.err.println("[ERROR] Failed to list files in directory: " + ex.getMessage());
            return;
        }

        if (logFiles.isEmpty()) {
            System.out.println("[INFO] No log files found in directory: " + logsDir.toAbsolutePath());
            // still generate an empty report for consistency
            ActivityAggregator emptyAggregator = new ActivityAggregator();
            try {
                new ReportGenerator().generate(reportPath, emptyAggregator);
                System.out.println("[INFO] Empty report generated at: " + reportPath.toAbsolutePath());
            } catch (IOException ex) {
                System.err.println("[ERROR] Failed to write report: " + ex.getMessage());
            }
            return;
        }

        ActivityAggregator aggregator = new ActivityAggregator();

        int poolSize = Math.min(logFiles.size(), Math.max(2, Runtime.getRuntime().availableProcessors()));
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        for (Path logFile : logFiles) {
            executor.submit(new LogProcessor(logFile, aggregator));
        }

        executor.shutdown();

        try {
            boolean finished = executor.awaitTermination(5, TimeUnit.MINUTES);
            if (!finished) {
                System.err.println("[WARN] Processing timeout reached. Forcing shutdown...");
                executor.shutdownNow();
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.err.println("[ERROR] Interrupted while waiting for worker threads.");
            executor.shutdownNow();
        }

        try {
            new ReportGenerator().generate(reportPath, aggregator);
            System.out.println("[INFO] Report generated successfully at: " + reportPath.toAbsolutePath());
        } catch (IOException ex) {
            System.err.println("[ERROR] Failed to generate report: " + ex.getMessage());
        }
    }
}
