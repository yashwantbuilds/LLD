package com.yashwant.learning.lld.lru;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class LRUCache {
    private final int capacity; // Maximum capacity of the cache
    private final Map<Integer, Integer> map; // Stores key-value pairs
    private final Deque<Integer> deque; // Maintains access order of keys

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.deque = new LinkedList<>();
    }

    public int get(int key) {
        if (!map.containsKey(key)) {
            return -1; // Key not present
        }

        // Move accessed key to the front of the deque
        deque.remove(key);
        deque.addFirst(key);
        return map.get(key);
    }

    public void put(int key, int value) {
        if (map.containsKey(key)) {
            // Update existing key
            deque.remove(key);
        } else if (map.size() == capacity) {
            // Cache is full, remove the least recently used item
            int lruKey = deque.removeLast();
            map.remove(lruKey);
        }

        // Add the key to the front of the deque
        deque.addFirst(key);
        map.put(key, value);
    }
}

/**
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache obj = new LRUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */