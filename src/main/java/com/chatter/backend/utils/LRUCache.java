package com.chatter.backend.utils;

import java.util.HashMap;

public class LRUCache<K, V> {
    HashMap<K, Node<K, V>> map;
    int capacity;
    Node<K, V> head;
    Node<K, V> tail;

    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.head = new Node<K, V>(null, null);
        this.tail = new Node<K, V>(null, null);
        head.next = tail;
        tail.prev = head;
    }

    private void addToHead(Node<K, V> node) {
        node.next = this.head.next;
        node.prev = this.head;
        this.head.next.prev = node;
        this.head.next = node;
    }

    private void remove(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    public synchronized V get(K key) {
        Node<K, V> node = this.map.get(key);
        if (node != null) {
            this.remove(node);
            this.addToHead(node);
            return node.value;
        }
        return null;
    }

    public synchronized boolean containsKey(K key) {
        return this.map.containsKey(key);
    }

    public synchronized void put(K key, V value) {
        Node<K, V> node = this.map.get(key);

        if (node != null) {
            node.value = value;
            this.remove(node);
            this.addToHead(node);
        } else {
            Node<K, V> new_node = new Node<K, V>(key, value);
            this.map.put(key, new_node);
            this.addToHead(new_node);

            if (map.size() > capacity) {
                this.map.remove(tail.prev.key);
                this.remove(tail.prev);
            }
        }
    }

    public synchronized void delete(K key) {
        if (this.map.containsKey(key)) {
            this.remove(this.map.get(key));
            this.map.remove(key);
        }
    }

    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

}
