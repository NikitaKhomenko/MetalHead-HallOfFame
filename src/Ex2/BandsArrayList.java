package Ex2;

import java.util.*;

public class BandsArrayList<Band> implements List<Band>{
    private Band arr[];
    private int size;
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    public BandsArrayList(Band data[]) {
        arr = data;
        size = data.length;
    }

    private boolean isFull(){
        return size == arr.length;
    }

    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+this.size;
    }

    private void rangeCheckForAdd(int index) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > arr.length || isFull()) {
            grow(minCapacity);
        }
    }

    private void grow(int minCapacity) {

        // overflow-conscious code

        int oldCapacity = arr.length+1; // if capacity is 0 >>> 1
        int newCapacity = oldCapacity + (oldCapacity >> 1);

        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;

        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);

        // minCapacity is usually close to size, so this is a win:

        arr = Arrays.copyOf(arr, newCapacity);

    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();

        return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }

    private void rangeCheck(int index) {
        if (index >= size)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    Band arr(int index) {
        return (Band) arr[index];
    }

        @Override
    public boolean add(Band element) {
        if(element == null)
            return false;

        ensureCapacity(size);
        arr[size++] = element;
        return true;
    }

    @Override
    public void add(int index, Band element) {
        rangeCheckForAdd(index);
        ensureCapacity(size);
        System.arraycopy(arr, index, arr, index + 1, size - index);
        arr[index] = element;
        size++;
    }

    @Override
    public boolean addAll(Collection<? extends Band> collection) {
        for(Band element : collection)
            add(element);
        return collection.isEmpty();
    }

    @Override
    public boolean addAll(int index, Collection<? extends Band> collection) {
        rangeCheckForAdd(index);

        int numMoved = size - index;
        if (numMoved > 0){
            System.arraycopy(arr, index, arr, index + collection.size(), size - index);
        }
        int i = 0;
        for(Band element : collection){
            add(index + i, element);
            ++i;
        }
        return collection.isEmpty();
    }

    @Override
    public void clear() {
        size = 0;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        Iterator<?> iter = c.iterator();

        while(iter.hasNext()){
            Object elem = iter.next();
            if(!contains(elem)){
                return false;
            }
        }
        return true;
    }

    @Override
    public Band get(int index) {
        rangeCheckForAdd(index);
        if(index >= size)
            throw new IndexOutOfBoundsException();

        return arr[index];
    }

    @Override
    public int indexOf(Object o) {
        for(int i = 0 ; i < size ; i++){
            if(arr[i].equals(o)){
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        int found = -1;
        for(int i = 0 ; i < size ; i++){
            if(arr[i].equals(o)){
                found = i;
            }
        }
        return found;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<Band> iterator() {
        return new BandsIterator();
    }

    class BandsIterator implements Iterator<Band> { // *Iterator*

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public Band next() {
            if (hasNext())
                return (Band) arr[++index];

            else
                throw new NoSuchElementException("This is the end of the array");
        }

    }

    class BandsListIterator implements ListIterator<Band> { // **List Iterator

        private int index;

        public BandsListIterator(int index) {
            this.index = index;
        }

        public BandsListIterator() {
            this.index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < size-1;
        }

        @Override
        public Band next() {
            if (hasNext())
                return (Band) arr[++index];
            return arr[index = 0];
        }

        @Override
        public boolean hasPrevious() {
            return index > 0;
        }

        @Override
        public Band previous() {
            if (hasPrevious())
                return (Band) arr[--index];
            return arr[index = 0];
        }

        @Override
        public int nextIndex() {
            if (hasNext()) {
                int a = index + 1;
                return a;
            }
            return -1;

        }

        @Override
        public int previousIndex() {
            if (hasPrevious()) {
                int b = index - 1;
                return b;
            }
            return -1;
        }

        @Override
        public void remove() {
            System.arraycopy(arr, index, arr, index - 1, size - index);
            size--;
        }

        @Override
        public void set(Band e) {
            arr[index] = e;
        }

        @Override
        public void add(Band e) {
            ensureCapacity(size);
        }

        public Band getCurrent(){
            return (Band) arr[index];
        }

        public int currentIndex(){
            return index;
        }

    }

    @Override
    public ListIterator<Band> listIterator() {
        return new BandsListIterator();
    }

    @Override
    public ListIterator<Band> listIterator(int index) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index: "+index);

        return new BandsListIterator(index);
    }

    @Override
    public boolean remove(Object o) {
        if (o != null)
            for(int i = 0; i < arr.length; i++){
                if( o.equals(get(i))){
                    System.arraycopy(arr, i + 1, arr, i, size - i);
                    return true;
                }
            }
        return false;
    }

    @Override
    public Band remove(int index) {
        rangeCheckForAdd(index);
        Band valueRemoved = arr[index];
        System.arraycopy(arr, index+1, arr, index, size - index);
        --size;
        return valueRemoved;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean changed = false;
        if (collection != null)
            for(int i = 0; i < arr.length; i++){
                for(Object element : collection)
                    if (element.equals(get(i))) {
                        System.arraycopy(arr, i, arr, i - 1, size - i);
                        changed = true;
                    }
            }
            size = size - collection.size();
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean changed = false;
        if (collection != null)
            for(int i = 0; i < arr.length; i++){
                for(Object element : collection)
                    if (!element.equals(get(i))) {
                        System.arraycopy(arr, i, arr, i - 1, size - i);
                        changed = true;
                    }
            }
        return changed;
    }

    @Override
    public Band set(int index, Band element) {
        remove(index);
        add(index, element);
        return null;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public List<Band> subList(int fromIndex, int toIndex) {
        BandsArrayList<Band> subList = new BandsArrayList<>(arr);

        subListRangeCheck(fromIndex, toIndex, size);
        if (fromIndex == toIndex)
            return null;

        for (int i = fromIndex; i < toIndex; i++) {
            subList.add(arr[i]);
        }

        return subList;
    }

    static void subListRangeCheck(int fromIndex, int toIndex, int size) {
        if (fromIndex < 0)
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);

        if (toIndex > size)
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);

        if (fromIndex > toIndex)
            throw new IllegalArgumentException("fromIndex(" + fromIndex +
                    ") > toIndex(" + toIndex + ")");
    }

    @Override
    public Band[] toArray() {
        return Arrays.copyOf(arr, size);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        // TODO Auto-generated method stub
        return null;
    }


}