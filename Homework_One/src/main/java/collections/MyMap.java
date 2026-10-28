package collections;

public interface MyMap<K,V> {
    int size();
    void put(K key, V value);
    V get(K key);
    V remove(K key);
}
