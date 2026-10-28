package collections;

public class Node<K,V> {
    private int hash;
    private K key;
    private V value;
    private Node<K,V> next = null;

    public Node(int hash, K key, V value) {
        this.hash = hash;
        this.key = key;
        this.value = value;
    }

    public int getHash() {
        return hash;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V newValue) {
        this.value = newValue;
    }

    public Node<K,V> getNext() {
        return next;
    }

    public void setNext(Node<K,V> nextNode) {
        this.next = nextNode;
    }
}
