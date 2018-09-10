package Ex2;

import java.io.IOException;
import java.util.Comparator;
import java.util.Stack;

public class BandsDataControllerImpl implements  BandsDataController {

    private static BandsDataControllerImpl instance;
    // BandsListRevert ; BandsMapRevert for revert function.
    // in case user already saved new list to file we need and extra list to revert.
    // extra map just so we won't have to remap.
    private BandsArrayList<Band> BandsList, BandsListRevert;
    private BandsHashMap<String, Band> BandsMap, BandsMapRevert;
    private BandsArrayList<Band>.BandsListIterator BListIterator;
    private static final BandDataAccessObject BandDAO = BandDataAccessObject.getInstance();

    private Stack<BandsDataCommand> commands;


    private BandsDataControllerImpl()
            throws IOException, ClassNotFoundException {
        setBandsList();
        setBandsMap();
        setBandsIterator();
    }

    // "Lazy Initialization" - single thread, there's no need for Thread Safe Singleton.
    public static BandsDataControllerImpl getInstance() throws IOException, ClassNotFoundException {
        if (instance == null) {
            instance = new BandsDataControllerImpl();
        }
        return instance;
    }

    private void setBandsList() throws IOException, ClassNotFoundException {
        BandsList = BandsListRevert = BandDAO.readAllBands();
        quickSort(BandsList, 0, BandsList.size(), serialNumComparator);
    }

    private void setBandsMap() throws IOException, ClassNotFoundException {
        BandsMap = BandsMapRevert = BandDAO.getBandsMappedByName();
    }

    private void setBandsIterator() {
        BListIterator = (BandsArrayList<Band>.BandsListIterator) BandsList.listIterator();
    }

    // previous/next logic - on regular call returns desired Band and calls execute which pushes this command into the commands stack.
    // if called from undo program will pop last command from stack so there's no need to push this one in. just jumps to return.

    @Override
    public Band previous(boolean calledFromUndo) {
        Previous previousOp = new Previous();
        if (!calledFromUndo)
            previousOp.execute();
        return BListIterator.previous();
    }

    @Override
    public Band next(boolean calledFromUndo) {
        Next nextOp = new Next();
        if (!calledFromUndo)
            nextOp.execute();
        return BListIterator.next();
    }

    @Override
    public void sort(Comparator<Band> comparator) {
        Sort sortOp = new Sort(comparator);
        sortOp.execute();
        commands.push(sortOp);
    }

    @Override
    public void add(Band band) {
        Add addOp = new Add(band);
        addOp.execute();
        commands.push(addOp);
    }

    @Override
    public void remove() {
        Remove removeOp = new Remove();
        removeOp.execute();
        commands.push(removeOp);
    }

    @Override
    public void undo() {
        commands.pop().undo();
    }

    @Override
    public void revert() {
        BandsList = BandsListRevert;
        BandsMap = BandsMapRevert;
        setBandsIterator();
    }

    @Override
    public void save() throws IOException {
        BandDAO.saveBands(BandsList.toArray());
    }

    @Override
    public Band getBandByName(String name) {
        return BandsMap.get(name);
    }


    ///////////////////////////*************  Commands  **************///////////////////////////

    class Sort implements BandsDataCommand {
        private Comparator<Band> comparator;

        private Sort(Comparator<Band> comparator) {
            this.comparator = comparator;
        }

        @Override
        public void execute() {
            quickSort(BandsList, 0, BandsList.size(), comparator);
        }

        @Override
        public void undo() {
            quickSort(BandsList, 0, BandsList.size(), serialNumComparator);
        }
    }

    class Next implements BandsDataCommand {

        @Override
        public void execute() {
            commands.push(Next.this);
        }

        @Override
        public void undo() {
            previous(true);
        }
    }

    class Previous implements BandsDataCommand {

        @Override
        public void execute() {
            commands.push(Previous.this);
        }

        @Override
        public void undo() {
            next(true);
        }
    }

    class Add implements BandsDataCommand {
        private Band band;

        private Add(Band band) {
            this.band = band;
        }

        @Override
        public void execute() {
            BandsList.add(BListIterator.currentIndex(), band);
            BandsMap.put(band.getName(), band);
        }

        @Override
        public void undo() {
            while (BListIterator.hasNext()) {
                Band someBand = BListIterator.next();
                if (someBand.equals(band)) {
                    BListIterator.remove();
                }
            }
            BandsMap.remove(band.getName());
        }
    }

    class Remove implements BandsDataCommand {
        private Band band;
        private int indexBandRemoved = BListIterator.currentIndex();

        @Override
        public void execute() {
            BListIterator.remove();
            band = BandsMap.remove(BListIterator.getCurrent().getName());
        }

        @Override
        public void undo() {
            BandsList.add(indexBandRemoved, band);
            BandsMap.put(band.getName(), band);
        }
    }


    ///////////////////////////*************  Comparators  **************///////////////////////////

    /**
     * Comparator to sort bands list in order of name
     */

    public static Comparator<Band> nameComparator = new Comparator<Band>() {

        @Override
        public int compare(Band b1, Band b2) {
            return (b1.getName().compareTo(b2.getName()));
        }
    };

    /**
     * Comparator to sort bands list in order of origin
     */
    public static Comparator<Band> originComparator = new Comparator<Band>() {

        @Override
        public int compare(Band b1, Band b2) {
            return (b1.getOrigin().compareTo(b2.getOrigin()));
        }
    };

    /**
     * Comparator to sort bands list in order of number of fans
     */
    public static Comparator<Band> fansComparator = new Comparator<Band>() {

        @Override
        public int compare(Band b1, Band b2) {
            return b1.getNumOfFans() - b2.getNumOfFans();
        }
    };

    /**
     * Comparator to sort bands list in order of serial number
     */
    public static Comparator<Band> serialNumComparator = new Comparator<Band>() {

        @Override
        public int compare(Band b1, Band b2) {
            return b1.getSerialNumber() - b2.getSerialNumber();
        }
    };

    ///////////////////////////*************  Helper functions  **************///////////////////////////

    int partition(BandsArrayList<Band> arr, int low, int high, Comparator<Band> comparator) {
        Band pivot = arr.get(high);
        int i = (low - 1); // index of smaller element
        for (int j = low; j < high; j++) {

            if (comparator.compare(arr.get(j), pivot) <= 0) {
                i++;

                // swap arr[i] and arr[j]
                Band temp_i = arr.get(i);
                Band temp_j = arr.get(j);

                arr.remove(i);
                arr.add(i , temp_j);
                arr.remove(j);
                arr.add(j , temp_i);
            }
        }
        // swap arr[i+1] and arr[high] (or pivot)
        Band temp_i = arr.get(i + 1);
        Band temp_high = arr.get(high);

        arr.remove(i + 1);
        arr.add(i + 1 , temp_high);
        arr.remove(temp_high);
        arr.add(high , temp_i);

        return i + 1;
    }

    //    low  --> Starting index,
    //    high  --> Ending index
    void quickSort(BandsArrayList<Band> arr, int low, int high, Comparator<Band> comparator) {
        if (low < high) {
            int pi = partition(arr, low, high, comparator);

            quickSort(arr, low, pi - 1, comparator);
            quickSort(arr, pi + 1, high, comparator);
        }
    }
}