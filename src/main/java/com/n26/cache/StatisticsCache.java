package com.n26.cache;

import java.time.LocalDateTime;
import java.util.function.Predicate;

public interface StatisticsCache<T, V> {
    LocalDateTime getTimestamp(T transaction);
    void update(T transaction);
    void invalidate(T transaction);
    void add(T transaction);
    void deleteAll();
    V mapReduce(Predicate<LocalDateTime> predicate);
}
