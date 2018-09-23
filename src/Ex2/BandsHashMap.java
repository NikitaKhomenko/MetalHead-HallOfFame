package Ex2;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class BandsHashMap implements Map<String,Band> {
    private static int DEFAULT_INITIAL_CAPACITY = 4;
    private static int MAXIMUM_CAPACITY = 1<<30;
    private static float DEFAULT_MAXIMUM_LOAD_FACTOR = 0.75f;
    private int size;
    private int capacity;
    private float thresholdLoadFactor;
    private BandEntry[] table;


    /**constructors*/
    public BandsHashMap() {
        this(DEFAULT_INITIAL_CAPACITY,DEFAULT_MAXIMUM_LOAD_FACTOR);
    }

    public BandsHashMap(int capacity) {
        this(capacity,DEFAULT_MAXIMUM_LOAD_FACTOR);
    }

    public BandsHashMap(int capacity, float loadFactor) {
        if (capacity > MAXIMUM_CAPACITY) {
            this.capacity = MAXIMUM_CAPACITY;
        }
        else {
            this.capacity = trimToPowerOf2(capacity);
        }
        this.thresholdLoadFactor = loadFactor;
        this.size =0;
        table = new BandEntry[this.capacity];
    }

    /* remove all entries from map*/
    @Override
    public void clear() {
        size =0;
        removeEntries();
    }
    /* return true if the key exists*/
    @Override
    public boolean containsKey(Object key) {
        int index = hash(key.hashCode());
        if (table[index] != null && table[index].getKey().equals(key)) {
            return true;
        }
        return false;
    }
    /* return true if the value exists*/
    @Override
    public boolean containsValue(Object value) {
        for (int i =0; i < capacity; i ++) {
            if (table[i] != null) {
                if (table[i].getValue().equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Set<Entry<String, Band>> entrySet() { /**return set of the entries(BandEntries) in the map*/
        Set<Entry<String,Band>> set = new HashSet<>();
        for (int i =0; i < capacity; i ++) {
            if (table[i] != null) {
                set.add(table[i]);
            }
        }
        return set;
    }

    @Override
    public Band get(Object key) { /**returning an element by specified key*/
        if (key != null) {
            int index = hash(((String)key).hashCode());
            if (table[index] != null) {
                return table[index].getValue();
            }
        }
        return null;
    }

    @Override
    public boolean isEmpty() { /**returning if the map contains values or not*/
        return size ==0;
    }

    @Override
    public Set<String> keySet() { /** returning a set of the keys in this map*/
        Set<String> set = new HashSet<>();
        for (Entry<String, Band> entry : entrySet()) {
            set.add(entry.getKey());
        }
        return set;
    }

    @Override
    public Band put(String key, Band value) { /** adding an element to the map by specified key*/
        int index = hash(key.hashCode());
        if (get(key) != null && table[index].getKey().equals(key)) { // if the key already exists
            Band oldValue = table[index].getValue();
            table[index].setValue(value);
            size ++;
            return oldValue;
        }

        if (size +1 >= capacity*thresholdLoadFactor || get(key) != null) { //if need rehash
            if (capacity == MAXIMUM_CAPACITY) {
                throw new RuntimeException("Exceeding maximum capacity");
            }
            rehash();

        }
        int newIndex = hash(key.hashCode());
        table[newIndex] = new BandEntry(key, value);
        size ++;
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Band> m) { /** adding a full map to this map*/
        Set<?> set = m.entrySet();

        for(Object entry : set) {
            put(((BandEntry)entry).getKey() , ((BandEntry)entry).getValue());
        }

    }

    @Override
    public Band remove(Object key) {/** removing element by specified key*/
        if (get(key) == null) {
            return null;
        }
        int index = hash(key.hashCode());
        Band oldValue = table[index].getValue();
        table[index] = null;
        size --;
        return oldValue;
    }

    @Override
    public int size() {/** retutn size of the map*/
        return size;
    }

    @Override
    public Collection<Band> values() { /** return a set consisting of the values in the map*/

        List<Band> list= new LinkedList<>();

        for (Entry<String, Band> entry : entrySet()) {
            list.add(entry.getValue());
        }

        return list;
    }

    private int trimToPowerOf2(int initialCapacity) {/**trims the capacity to power of 2*/
        int capacity =1;
        while(capacity < initialCapacity) {
            capacity <<= 1;
        }
        return capacity;
    }

    private void rehash() {/** the rehash function*/
        Set<Entry<String, Band>> set = entrySet();
        capacity <<= 1;
        table = new BandEntry[capacity];
        size =0;
        for (Entry<String, Band> entry : set) {
            put(entry.getKey(), entry.getValue());
        }

    }

    private int hash(int hashCode) {
        return supplyHashCode(hashCode) & (capacity -1);
    }

    private int supplyHashCode(int h) {
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    private void removeEntries() {
        for (int i =0; i < capacity; i ++) {
            if (table[i] != null ) {
                table[i] = null;
            }
        }
    }


    /** the entries of the map <String, Band> */
    public static class BandEntry implements Entry<String,Band>{

        private final String key;
        private Band value;

        public BandEntry(String key, Band value) {
            this.key = key;
            setValue(value);
        }
        @Override
        public String getKey() {
            return key;
        }
        @Override
        public Band getValue() {
            return value;
        }
        @Override
        public Band setValue(Band value) {
            Band oldValue = getValue();
            this.value = value;
            return oldValue;
        }

    }



}