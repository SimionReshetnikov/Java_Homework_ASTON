public class MyHashMap <K, V> implements MyMap <K, V> {
    private Node<K, V> [] table;
    private int size;
    private final float loadFactor;
    private int threshold;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_INITIAL_LOAD_FACTOR = 0.75f;

    public MyHashMap() {
        this.table = (Node<K, V>[])new Node[DEFAULT_INITIAL_CAPACITY];
        this.size = 0;
        this.loadFactor = DEFAULT_INITIAL_LOAD_FACTOR;
        this.threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_INITIAL_LOAD_FACTOR);
    }

    private int hash(K key) {
        if (key == null) {
            return 0;
        }
        int hashCodeKey = key.hashCode();
        return hashCodeKey ^ hashCodeKey >>> 16;
    }

    private void putVal(int hash, K key, V value, int index) {
        Node<K, V> currentNode = table[index];

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

    @Override
    public void put(K key, V value) {
        int hashKey = hash(key);
        int indexTable = (table.length - 1) & hashKey;
        putVal(hashKey, key, value, indexTable);
    }

    @Override
    public V get(K key) {
        int hashKey = hash(key);
        int indexTable = (table.length - 1) & hashKey;
        Node<K, V> currentNode = table[indexTable];

        while (currentNode != null) {
            if (currentNode.getHash() == hashKey) {
                if (currentNode.getKey() == key || key != null && key.equals(currentNode.getKey())) {
                    return currentNode.getValue();
                }
            }

            currentNode = currentNode.getNext();
        }

        return null;
    }

    @Override
    public V remove(K key) {
        return null;
    }
}
