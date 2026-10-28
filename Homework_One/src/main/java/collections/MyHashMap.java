package collections;

public class MyHashMap<K,V> implements MyMap<K,V> {
    private static final float LOAD_FACTOR = 0.75f;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private Node<K,V>[] table;
    private int size;
    private int threshold;

    @SuppressWarnings("unchecked")
    public MyHashMap() {
        this.table = new Node[DEFAULT_INITIAL_CAPACITY];
        this.size = 0;
        this.threshold = (int) (DEFAULT_INITIAL_CAPACITY * LOAD_FACTOR);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        int hashKey = hash(key);
        int indexTable = (table.length - 1) & hashKey;
        putVal(hashKey, key, value, indexTable);

        if (size > threshold) {
            resize();
        }
    }

    @Override
    public V get(K key) {
        int hashKey = hash(key);
        int indexTable = (table.length - 1) & hashKey;
        Node<K,V> currentNode = table[indexTable];

        while (currentNode != null) {
            if (currentNode.getHash() == hashKey) {
                if (currentNode.getKey() == key || (key != null && key.equals(currentNode.getKey()))) {
                    return currentNode.getValue();
                }
            }

            currentNode = currentNode.getNext();
        }
        return null;
    }

    @Override
    public V remove(K key) {
        int hashKey = hash(key);
        int indexTable = (table.length - 1) & hashKey;
        Node<K,V> currentNode = table[indexTable];
        Node<K,V> prev = null;

        while (currentNode != null) {
            if (currentNode.getHash() == hashKey) {
                if (currentNode.getKey() == key || (key != null && key.equals(currentNode.getKey()))) {
                   V value = currentNode.getValue();
                   if (prev == null) {
                       table[indexTable] = currentNode.getNext();
                   } else {
                       prev.setNext(currentNode.getNext());
                   }
                    size--;
                    return value;
                }
            }
            prev = currentNode;
            currentNode = currentNode.getNext();
        }
        return null;
    }

    private int hash(K key) {
        if (key == null) {
            return 0;
        }
        int hashCodeKey = key.hashCode();
        return hashCodeKey ^ (hashCodeKey >>> 16);
    }

    private void resize() {
        int newCapacity = table.length * 2;
        Node<K,V>[] newTable = new Node[newCapacity];

        for (Node<K,V> node : table) {
            Node<K,V> currentNode = node;
            while (currentNode != null) {
                Node<K,V> next = currentNode.getNext();
                currentNode.setNext(null);

                int newIndex = (newCapacity - 1) & currentNode.getHash();

                if (newTable[newIndex] == null) {
                    newTable[newIndex] = currentNode;
                } else {
                    Node<K,V> lastNode = newTable[newIndex];
                    while (lastNode.getNext() != null) {
                        lastNode = lastNode.getNext();
                    }
                    lastNode.setNext(currentNode);
                }

                currentNode = next;
            }
        }

        table = newTable;
        threshold = (int) (newCapacity * LOAD_FACTOR);
    }

    private void putVal(int hash, K key, V value, int index) {
        Node<K,V> currentNode = table[index];

        if (currentNode == null) {
            table[index] = new Node<>(hash, key, value);
            size++;
            return;
        }

        while (currentNode != null) {
            if (currentNode.getHash() == hash) {
                if (currentNode.getKey() == key || (key != null && key.equals(currentNode.getKey()))) {
                    currentNode.setValue(value);
                    return;
                }
            }

            if (currentNode.getNext() == null) {
                currentNode.setNext(new Node<>(hash, key, value));
                size++;
                return;
            }
            currentNode = currentNode.getNext();
        }
    }
}
