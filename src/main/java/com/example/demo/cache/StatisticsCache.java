package com.example.demo.cache;

import java.util.function.Predicate;

public interface StatisticsCache<T, V> {
    long getTimestamp(T transaction);
    void update(T transaction);
    void invalidate(T transaction);
    void add(T transaction);
    void deleteAll();
    V mapReduce(Predicate<Long> predicate);
}
