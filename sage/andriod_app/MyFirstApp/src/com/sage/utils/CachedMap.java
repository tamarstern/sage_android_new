package com.sage.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CachedMap<K, V> implements Map<K, V> {

    private final Map<K, V> delegate = new ConcurrentHashMap<K, V>();
    private Queue<K> keyInsertionOrder = new ConcurrentLinkedQueue<K>();
    private final int maxCapacity;

    public CachedMap(int maxCapacity) {
        if (maxCapacity < 1) {
            throw new IllegalArgumentException(
                    "Capacity must be greater than 0");
        }
        this.maxCapacity = maxCapacity;
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
	       return delegate.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    @Override
    public V get(Object key) {
        return delegate.get(key);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

       @Override
    public V put(K key, V value) {
        V previous = delegate.put(key, value);
        keyInsertionOrder.remove(key);
        keyInsertionOrder.add(key);

        if (delegate.size() > maxCapacity) {
            K oldest = keyInsertionOrder.poll();
            delegate.remove(oldest);
        }
        return previous;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (K key : m.keySet()) {
            put(key, m.get(key));
        }
    }

    @Override
    public V remove(Object key) {
        keyInsertionOrder.remove(key);
        return delegate.remove(key);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public Collection<V> values() {
        return delegate.values();
    }
}