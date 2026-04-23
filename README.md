# Concurrent Log Processing System

A professional Java implementation of a concurrent log processing pipeline using multithreading, file handling, and OOP design.

## Features

- Processes multiple log files in parallel (one file per worker thread)
- Thread-safe aggregation of user activity counts
- Robust malformed-line handling (ignored with warning)
- Handles empty files, missing/invalid directories, and IO exceptions
- Generates `report.txt` summary
- Bonus:
  - Sort by highest activity
  - Most active user in report footer
  - Per-action totals (LOGIN, LOGOUT, UPLOAD, DOWNLOAD, etc.)

## Project Structure

- `src/main/java/com/ayush/logprocessor/LogEntry.java`
- `src/main/java/com/ayush/logprocessor/ActivityAggregator.java`
- `src/main/java/com/ayush/logprocessor/LogProcessor.java`
- `src/main/java/com/ayush/logprocessor/ReportGenerator.java`
- `src/main/java/com/ayush/logprocessor/Main.java`

## Input Log Format

Each line must be:

```text
<timestamp> <userId> <action>
```

Example:

```text
2026-04-20T10:15:30 user123 LOGIN
```

Malformed lines are skipped.

## How to Run

### Compile

```bash
javac -d out src/main/java/com/ayush/logprocessor/*.java
```

### Execute

```bash
java -cp out com.ayush.logprocessor.Main ./logs ./report.txt
```

Arguments:
- arg0: logs directory (default: `./logs`)
- arg1: output report path (default: `./report.txt`)

## Sample Output

```text
===== USER ACTIVITY REPORT =====
user123: 4 actions
user456: 3 actions
user789: 2 actions

----- ACTION TYPE TOTALS -----
DOWNLOAD: 2
LOGIN: 3
LOGOUT: 3
UPLOAD: 1

Most active user: user123 (4 actions)
```

## Notes

- Uses `ConcurrentHashMap` + `AtomicInteger` for safe concurrent updates.
- Uses an `ExecutorService` with bounded thread pool sized by file count/CPU.
- Uses `Files.newBufferedReader` and NIO `Path` APIs.
