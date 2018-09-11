package Ex2;

import java.io.*;
import java.util.Collections;

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
        Band[] temp = new Band[0];
        BandsArrayList<Band> bandsList = new BandsArrayList<>(temp);
        try {
            Band[] bands = readBandsArrayFromFile();
            Collections.addAll(bandsList, bands);

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
            Band[] bands = readBandsArrayFromFile();
            for (int i = 0; i < bands.length; i++)
                BandsMap.put(bands[i].getName(), bands[i]);

        } catch (IOException e) {
            System.out.println("Error reading saving data.");
            e.printStackTrace(System.out);

        } finally {
            input.close();
        }
        return BandsMap;
    }

    private Band[] readBandsArrayFromFile() throws IOException, ClassNotFoundException {
        input = new ObjectInputStream(new FileInputStream(fileName));
        FileInputStream inputFile = new FileInputStream(fileName);
        input = new ObjectInputStream(inputFile);
        Object[] bandscheck = (Object[]) input.readObject();
        return (Band[]) bandscheck;
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