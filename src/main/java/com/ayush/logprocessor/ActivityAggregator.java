package com.ayush.logprocessor;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class ActivityAggregator {

    private final ConcurrentHashMap<String, AtomicInteger> userActionCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> actionTypeCounts = new ConcurrentHashMap<>();

    public void record(LogEntry entry) {
        userActionCounts
                .computeIfAbsent(entry.getUserId(), ignored -> new AtomicInteger(0))
                .incrementAndGet();

        actionTypeCounts
                .computeIfAbsent(entry.getAction(), ignored -> new AtomicInteger(0))
                .incrementAndGet();
    }

    public Map<String, Integer> getUserCountsSnapshot() {
        Map<String, Integer> plain = userActionCounts.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));

        return Collections.unmodifiableMap(plain);
    }

    public Map<String, Integer> getActionTypeCountsSnapshot() {
        Map<String, Integer> plain = actionTypeCounts.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));

        return Collections.unmodifiableMap(plain);
    }

    public Map<String, Integer> getUserCountsSortedDesc() {
        return userActionCounts.entrySet()
                .stream()
                .sorted(Comparator
                        .<Map.Entry<String, AtomicInteger>>comparingInt(e -> e.getValue().get())
                        .reversed()
                        .thenComparing(Map.Entry::getKey))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().get(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    public Map.Entry<String, Integer> getMostActiveUser() {
        return userActionCounts.entrySet()
                .stream()
                .map(e -> Map.entry(e.getKey(), e.getValue().get()))
                .max(Comparator
                        .<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue)
                        .thenComparing(Map.Entry::getKey))
                .orElse(null);
    }
}
