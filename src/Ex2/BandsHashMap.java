package Ex2;

import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;

public class BandsHashMap<K_String, V_Band>  implements Map<K_String, V_Band>
{
    private BandEntry[] table;
    private int size;
    private static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
    private double threshold;

    // package private
    BandsHashMap() {
        if (DEFAULT_INITIAL_CAPACITY <= 0)
            throw new IllegalArgumentException ("Illegal capacity: " + DEFAULT_INITIAL_CAPACITY);
        table = new BandEntry[DEFAULT_INITIAL_CAPACITY];
        size = 0;
        setThreshold(DEFAULT_INITIAL_CAPACITY);
    }

    private void setThreshold(int capacity) {
        this.threshold = capacity*0.75;
    }

    public BandsHashMap(Map<? extends K_String, ? extends V_Band> m) {
        throw new UnsupportedOperationException();
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (threshold == size) {
            table = resize();
        }
    }

    private BandEntry<K_String,V_Band>[] resize() {
        BandEntry<K_String,V_Band>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int newCap = oldCap*2;
        setThreshold(newCap);

        @SuppressWarnings({"rawtypes","unchecked"})
        BandEntry<K_String,V_Band>[] newTab = (BandEntry<K_String,V_Band>[])new BandEntry[newCap];
        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                BandEntry<K_String,V_Band> e;
                if ((e = oldTab[j]) != null) {
                    oldTab[j] = null;
                    if (e.next == null)
                        newTab[e.hash & (newCap - 1)] = e;
                    else { // preserve order
                        BandEntry<K_String,V_Band> loHead = null, loTail = null;
                        BandEntry<K_String,V_Band> hiHead = null, hiTail = null;
                        BandEntry<K_String,V_Band> next;
                        do {
                            next = e.next;
                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }

    public V_Band get(Object key) {
        int hash = key == null ? 0 : key.hashCode();
        int index = key == null ? 0 : hash % table.length;

        // search for the specified key
        for (BandEntry<K_String, V_Band> e = table[index]; e != null; e = e.next) {
            if (hash == e.hash && (key == e.key ||
                    (key != null && key.equals(e.key)))) {
                return e.value;
            }
        }
        return null;
    }

    /**
     * Returns true if this map contains a mapping for the
     * specified key.
     */
    public boolean containsKey(Object key) {
        int hash = key == null ? 0 : key.hashCode();
        int index = key == null ? 0 : hash % table.length;

        // search for the specified key at the hashed index
        for (BandEntry<K_String, V_Band> e = table[index]; e != null; e = e.next)
            if (hash == e.hash && (key == e.key ||
                    (key != null && key.equals(e.key))))
                return true;
        return false;
    }

    /**
     * Returns true if this map maps one or more keys to the
     * specified value.
     */
    public boolean containsValue(Object value) {
        // search for the specified value in the whole map
        for (BandEntry aTable : table)
            for (BandEntry<K_String, V_Band> e = aTable; e != null; e = e.next)
                if (value == e.value ||
                        (value != null && value.equals(e.value)))
                    return true;
        return false;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     */
    public V_Band put(K_String key, V_Band value) {
        int hash = key == null ? 0 : key.hashCode();
        int index = key == null ? 0 : hash % table.length;

        // check if the key is already contained: update the value
        for (BandEntry<K_String, V_Band> e = table[index]; e != null; e = e.next) {
            if (hash == e.hash && (key == e.key || (key != null && key.equals(e.key)))) {
                V_Band oldValue = e.value;
                e.value = value;
                return oldValue;
            }
        }

        // insert the new mapping at the beginning of the list
        ensureCapacity();
        BandEntry<K_String, V_Band> e = new BandEntry(hash, key, value, table[index]);
        table[index] = e;
        size++;
        return null;
    }

    /**
     * Copies all of the mappings from the specified map to this map.
     */
    public void putAll(Map<? extends K_String, ? extends V_Band> m) {
        if (m.size() == 0)
            return;

        for (Iterator<? extends Map.Entry<? extends K_String, ? extends V_Band>> i =
             m.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry<? extends K_String, ? extends V_Band> e = i.next();
            put(e.getKey(), e.getValue());
        }
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * Returns null if the BandsHashMap contains no mapping for this key.
     */
    public V_Band remove(Object key) {
        int hash = key == null ? 0 : key.hashCode();
        int index = key == null ? 0 : hash % table.length;

        // search for the specified key
        BandEntry<K_String, V_Band> p = table[index];
        for (BandEntry<K_String, V_Band> e = table[index]; e != null; e = e.next) {
            if (hash == e.hash && (key == e.key ||
                    (key != null && key.equals(e.key)))) {
                if (p == e)
                    table[index] = e.next;
                else
                    p.next = e.next;
                size--;
                return e.value;
            }
            p = e;
        }

        return null;
    }

    /**
     * Removes all of the mappings from this map.
     */
    public void clear() {
        for (int index = 0; index < table.length; index++)
            table[index] = null;
        size = 0;
    }

    static class BandEntry<K_String, V_Band> implements Map.Entry<K_String, V_Band> {
        final int hash;
        final K_String key;
        V_Band value;
        BandEntry<K_String, V_Band> next;

        BandEntry(int h, K_String k, V_Band v, BandEntry<K_String, V_Band> n) {
            hash = h;
            key = k;
            value = v;
            next = n;
        }

        public K_String getKey() {
            return key;
        }

        public V_Band getValue() {
            return value;
        }

        public V_Band setValue(V_Band v) {
            V_Band val = value;
            value = v;
            return val;
        }

        public boolean equals(Object o) {
            // same object reference
            if (o == this)
                return true;

            // check instance type (Map.BandEntry)
            if (!(o instanceof Map.Entry))
                return false;

            // check k,v pair
            K_String k1 = getKey();
            V_Band v1 = getValue();
            K_String k2 = ((BandEntry<K_String, V_Band>)o).getKey();
            V_Band v2 = ((BandEntry<K_String, V_Band>)o).getValue();
            if ((k1 == k2 || (k1 != null && k1.equals(k2))) &&
                    (v1 == v2 || (v1 != null && v1.equals(v2))))
                return true;
            return false;
        }

        public int hashCode() {
            return (key == null ? 0 : key.hashCode()) ^
                    (value == null ? 0 : value.hashCode());
        }
    }

    // Views

    /**
     * Returns a Set view of the keys contained in this map.
     */
    public Set<K_String> keySet() {
        return new KeySet();
    }

    private class KeySet extends java.util.AbstractSet<K_String> {
        public int size() {
            return size;
        }

        public Iterator<K_String> iterator() {
            return new KeySetIterator();
        }
    }

    private class KeySetIterator extends HashIterator<K_String> {
        public K_String next() {
            return nextEntry().getKey();
        }
    }

    /**
     * Returns a Collection view of the values contained in this map.
     */

    public Collection<V_Band> values() {
        return new Values();
    }

    private class Values extends java.util.AbstractCollection<V_Band> {
        public int size() {
            return size;
        }

        public Iterator<V_Band> iterator() {
            return new ValuesIterator();
        }
    }

    private class ValuesIterator extends HashIterator<V_Band> {
        public V_Band next() {
            return nextEntry().getValue();
        }
    }

    /**
     * Returns a Set view of the mappings contained in this map.
     */
    public Set<Map.Entry<K_String, V_Band>> entrySet() {
        return new EntrySet();
    }

    private class EntrySet extends java.util.AbstractSet<Map.Entry<K_String, V_Band>> {
        public int size() {
            return size;
        }

        public Iterator<Map.Entry<K_String, V_Band>> iterator() {
            return new EntrySetIterator();
        }
    }

    private class EntrySetIterator extends HashIterator<Map.Entry<K_String, V_Band>> {
        public Map.Entry<K_String, V_Band> next() {
            return nextEntry();
        }
    }

    /**
     * Provides a skeletal implementation of a hash iterator over the
     * elements in this hash map.
     */
    private abstract class HashIterator<T> implements Iterator<T> {
        private int index;
        private BandEntry<K_String, V_Band> currEntry;
        private BandEntry<K_String, V_Band> nextEntry;

        // initialize the iterator to the first entry.
        public HashIterator() {
            index = 0;
            currEntry = null;
            nextEntry = null;
            for ( ; index < table.length; index++)
                if (table[index] != null)
                    nextEntry = table[index];
        }

        public boolean hasNext() {
            return nextEntry != null;
        }

        // the next() method has to be implemeted for the specific type
        // T, by extending the abstract class, and making use of the
        // more generic nextEntry() method here below.
        public abstract T next();

        public BandEntry<K_String, V_Band> nextEntry() {
            currEntry = nextEntry;
            if (nextEntry.next != null) {
                nextEntry = nextEntry.next;
            } else {
                nextEntry = null;
                for ( ; index < table.length; index++)
                    if (table[index] != null)
                        nextEntry = table[index];
            }

            return currEntry;
        }

        // since this hash map uses a sigle linked list to record its
        // mappings, it's not easy to remove the BandEntry without breaking
        // the list. the simpler solution is to call the remove()
        // method for the specified key
        public void remove() {
            BandsHashMap.this.remove(nextEntry.getKey());
        }
    }

    // Comparison and hashing

    /**
     * Compares the specified object with this map for equality.  Returns
     * true if the given object is also a map and the two maps
     * represent the same mappings.
     */
    public boolean equals(Object o) {
        // trivial check
        if (o == this)
            return true;

        // check that it's an instance of Map
        if (!(o instanceof Map))
            return false;
        Map<K_String, V_Band> m = (Map<K_String, V_Band>)o;

        // check that the size is the same */
        if (m.size() != size)
            return false;
        // and that each element is contained in the map */
        Set<Map.Entry<K_String, V_Band>> s = entrySet();
        for (Iterator<Map.Entry<K_String, V_Band>> i = s.iterator(); i.hasNext(); ) {
            Map.Entry<K_String, V_Band> e = i.next();
            K_String key = e.getKey();
            V_Band value = e.getValue();

            if (!m.containsKey(key))
                return false;
            if (!value.equals(m.get(key)))
                return false;
        }
        return true;
    }

    /**
     * Returns the hash code value for this map.  The hash code of a map is
     * defined to be the sum of the hash codes of each entry in the map's
     * entrySet() view.
     */
    public int hashCode() {
        int hash = 0;
        Set<Map.Entry<K_String, V_Band>> s = entrySet();
        for (Iterator<Map.Entry<K_String, V_Band>> i = s.iterator(); i.hasNext(); )
            hash += i.next().hashCode();

        return hash;
    }

}