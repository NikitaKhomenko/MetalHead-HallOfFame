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
        Band[] bands = BandsList.toArray();
        quickSort(bands, 0, bands.length-1, serialNumComparator);
        BandsList = BandsListRevert = new BandsArrayList<>(bands);
    }

    private void setBandsMap() throws IOException, ClassNotFoundException {
        BandsMap = BandsMapRevert = BandDAO.getBandsMappedByName();
    }

    private void setBandsIterator() {
        BListIterator = (BandsArrayList<Band>.BandsListIterator) BandsList.listIterator();
    }

    // previous/next logic - on regular call returns desired Band and calls execute which pushes this command into the commands stack.
    // if called from undo program will pop last command from stack so there's no need to push this one in. just jumps to return.

    public Band CurrentBand() {
        return BListIterator.getCurrent();
    }

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
            Band[] bands = BandsList.toArray();
            quickSort(bands, 0, bands.length-1, comparator);
            BandsList = new BandsArrayList<>(bands);
        }

        @Override
        public void undo() {
            Band[] bands = BandsList.toArray();
            quickSort(bands, 0, bands.length-1, serialNumComparator);
            BandsList = new BandsArrayList<>(bands);
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
            return b1.getSerialNumber() - b2.getSerialNumber() ;
        }
    };

    public Comparator<Band> getNameComparator(){
        return nameComparator;
    }

    public Comparator<Band> getOriginComparator(){
        return originComparator;
    }

    public Comparator<Band> getFansComparator(){
        return fansComparator;
    }

    public Comparator<Band> getSerialNumComparator(){
        return serialNumComparator;
    }

    ///////////////////////////*************  Helper functions  **************///////////////////////////

    int partition(Band[] arr, int low, int high, Comparator<Band> comparator) {
        Band pivot = arr[high];
        int i = (low-1); // index of smaller element
        for (int j = low; j < high ; j++) {

            if (comparator.compare(arr[j], pivot) <= 0) {
                i++;

                // swap arr[i] and arr[j]

                Band temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        // swap arr[i+1] and arr[high] (or pivot)
        Band temp = arr[i+1];
        arr[i+1] = arr[high];
        arr[high] = temp;

        return i + 1;
    }

    //    low  --> Starting index,
    //    high  --> Ending index
    void quickSort(Band[] arr, int low, int high, Comparator<Band> comparator) {
        if (low < high) {
            int pi = partition(arr, low, high, comparator);

            quickSort(arr, low, pi - 1, comparator);
            quickSort(arr, pi + 1, high, comparator);
        }
    }
}
