package Ex2;

import java.io.*;

public class BandDataAccessObject implements BandDataAccess {

    private static BandDataAccessObject instance;
    private ObjectInputStream input = null;
    private ObjectOutputStream output = null;
    private String fileName = "bands.bin";

    private BandDataAccessObject(){}
    // "Lazy Initialization" - single thread, there's no need for Thread Safe Singleton.
    public static BandDataAccessObject getInstance(){
        if(instance == null){
            instance = new BandDataAccessObject();
        }
        return instance;
    }

    @Override
    public BandsArrayList readAllBands() throws IOException, ClassNotFoundException {
        Band[] BandsArr = new Band[20];
        BandsArrayList<Band> bandsList = new BandsArrayList<>(BandsArr);
        try {
            input = new ObjectInputStream(new FileInputStream(fileName));
            while(input.available() > 0) // check if the file stream is at the end
                bandsList.add((Band) input.readObject());

        } catch (IOException e) {
            System.out.println("Error reading saving data.");
            e.printStackTrace(System.out);

        } finally {
                input.close();
        }
        return bandsList;
    }


    @Override
    public BandsHashMap getBandsMappedByName() throws IOException, ClassNotFoundException {
        BandsHashMap<String,Band> BandsMap = new BandsHashMap<>();
        try {
            input = new ObjectInputStream(new FileInputStream(fileName));
            BandsMap = ReadBandsToMap(BandsMap);

        } catch (IOException e) {
            System.out.println("Error reading saving data.");
            e.printStackTrace(System.out);

        } finally {
            input.close();
        }
        return BandsMap;
    }

    private BandsHashMap ReadBandsToMap(BandsHashMap BandsMap) throws IOException, ClassNotFoundException {
        while(input.available() > 0) // check if the file stream is at the end
        {
            Band band = (Band) input.readObject();
            BandsMap.put(band.getName(), band);
        }
        return BandsMap;
    }

    @Override
    public void saveBands(Band[] bands) throws IOException {
        try {
            output = new ObjectOutputStream(new FileOutputStream(fileName));
            for(int i = 0; i < bands.length; i++)
                output.writeObject(bands[i]);

        } catch (IOException e) {
            System.out.println("Error reading saving data.");
            e.printStackTrace(System.out);

        } finally {
                output.close();
        }
    }

}