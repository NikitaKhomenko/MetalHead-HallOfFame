package Ex2;

import java.io.IOException;
import java.util.Comparator;

public interface BandsDataController {
    Band previous();

    Band next();

    void sort(Comparator<Band> comparator);

    void add(Band band);

    void remove();

    void undo();

    void revert();

    void save() throws IOException;

    Band getBandByName(String name);
}
