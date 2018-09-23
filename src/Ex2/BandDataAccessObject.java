package Ex2;

import javafx.scene.control.Alert;

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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            setAlert(alert);

        } finally {
                input.close();
        }
        return bandsList;
    }


    @Override
    public BandsHashMap getBandsMappedByName() throws IOException, ClassNotFoundException {
        BandsHashMap BandsMap = new BandsHashMap();
        try {
            Band[] bands = readBandsArrayFromFile();
            for (int i = 0; i < bands.length; i++)
                BandsMap.put(bands[i].getName(), bands[i]);

        } catch (IOException e) {
            System.out.println("Error reading data.");
            e.printStackTrace(System.out);

        } finally {
            input.close();
        }
        return BandsMap;
    }

    private Band[] readBandsArrayFromFile() throws IOException, ClassNotFoundException {
        input = new ObjectInputStream(new FileInputStream(fileName));
        Object[] bandscheck = (Object[]) input.readObject();
        return (Band[]) bandscheck;
    }

    @Override
    public void saveBands(Band[] bands) throws IOException {
        output = new ObjectOutputStream(new FileOutputStream(fileName));
        output.writeObject(bands);
    }

    ///////////////// ********* Alert Box ********* /////////////////


    private void setAlert (Alert alert) {
        alert.setTitle("Error");
        alert.setHeaderText("I/O Error");
        alert.setContentText("Data file is missing or corrupt.");
        alert.showAndWait();
    }


}