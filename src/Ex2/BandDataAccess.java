package Ex2;

import java.io.IOException;

public interface BandDataAccess {
    BandsArrayList readAllBands() throws IOException, ClassNotFoundException;

    BandsHashMap getBandsMappedByName() throws IOException, ClassNotFoundException;

    void saveBands(Band[] bands) throws IOException;
}
