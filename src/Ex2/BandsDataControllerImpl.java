package Ex2;

import java.io.IOException;
import java.util.Comparator;
import java.util.Stack;

public class BandsDataControllerImpl implements  BandsDataController {

    private static BandsDataControllerImpl instance;
    // BandsListRevert for revert function.
    // in case user already saved new list to file we need an extra list to revert.
    private BandsArrayList<Band> BandsList, BandsListRevert;
    private BandsHashMap BandsMap;
    private BandsArrayList<Band>.BandsListIterator BListIterator;
    private static final BandDataAccessObject BandDAO = BandDataAccessObject.getInstance();

    private Stack<BandsDataCommand> commands = new Stack<>();


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
        BandsList = BandDAO.readAllBands();
        BandsListRevert = BandDAO.readAllBands();
    }

    private void setBandsMap() throws IOException, ClassNotFoundException {
        BandsMap = BandDAO.getBandsMappedByName();
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
    public Band previous() {
        Previous previousOp = new Previous();
        previousOp.execute();
        commands.push(previousOp);
        return previousOp.getBand();
    }

    @Override
    public Band next() {
        Next nextOp = new Next();
        nextOp.execute();
        commands.push(nextOp);
        return nextOp.getBand();
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
        if (!commands.empty())
            commands.pop().undo();
    }

    @Override
    public void revert() {
        BandsList = new BandsArrayList<Band>(BandsListRevert.toArray());
        BandsMap = new BandsHashMap();
        for (int i = 0; i < BandsList.size(); i++)
            BandsMap.put(BandsList.get(i).getName(), BandsList.get(i));
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

        public Sort(Comparator<Band> comparator) {
            this.comparator = comparator;
        }

        @Override
        public void execute() {
            BandsList.sort(comparator);
        }

        @Override
        public void undo() {
            BandsList.sort(getSerialNumComparator());
        }
    }

    class Next implements BandsDataCommand {
        private Band band;

        @Override
        public void execute() {
            band = BListIterator.next();
        }

        public Band getBand() {
            return band;
        }

        @Override
        public void undo() {
            band = BListIterator.previous();
        }
    }

    class Previous implements BandsDataCommand {
        private Band band;

        @Override
        public void execute() {
            band = BListIterator.previous();
        }

        public Band getBand() {
            return band;
        }

        @Override
        public void undo() {
            band = BListIterator.next();
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
    class nameComparator implements Comparator<Band> {

        @Override
        public int compare(Band b1, Band b2) {
            return b1.getName().compareToIgnoreCase(b2.getName());
        }
    }

    /**
     * Comparator to sort bands list in order of origin
     */
    class fansComparator implements Comparator<Band> {

        @Override
        public int compare(Band b1, Band b2) {
            return b2.getNumOfFans() - b1.getNumOfFans();
        }
    }

    /**
     * Comparator to sort bands list in order of number of fans
     */
    class originComparator implements Comparator<Band> {

        @Override
        public int compare(Band b1, Band b2) {
            return b1.getOrigin().compareToIgnoreCase(b2.getOrigin());
        }
    }

    /**
     * Comparator to sort bands list in order of serial number
     */
    class serialNumComparator implements Comparator<Band> {

        @Override
        public int compare(Band b1, Band b2) {
            return b1.getSerialNumber() - b2.getSerialNumber();
        }
    }

    public Comparator<Band> getNameComparator() {
        return new nameComparator();
    }

    public Comparator<Band> getOriginComparator() {
        return new originComparator();
    }

    public Comparator<Band> getFansComparator() {
        return new fansComparator();
    }

    public Comparator<Band> getSerialNumComparator() {
        return new serialNumComparator();
    }
}

