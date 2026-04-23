package com.ayush.logprocessor;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;


public final class LogEntry {
    private final LocalDateTime timestamp;
    private final String userId;
    private final String action;

    public LogEntry(LocalDateTime timestamp, String userId, String action) {
        this.timestamp = Objects.requireNonNull(timestamp, "timestamp cannot be null");
        this.userId = Objects.requireNonNull(userId, "userId cannot be null");
        this.action = Objects.requireNonNull(action, "action cannot be null");
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getAction() {
        return action;
    }

    public static LogEntry fromLine(String line) {
        if (line == null) {
            return null;
        }

        String trimmed = line.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        String[] parts = trimmed.split("\\s+");
        if (parts.length != 3) {
            return null;
        }

        try {
            LocalDateTime ts = LocalDateTime.parse(parts[0]);
            String userId = parts[1];
            String action = parts[2].toUpperCase();
            return new LogEntry(ts, userId, action);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }
}
