package Ex2;

import java.io.Serializable;
import java.util.Objects;

public class Band implements Serializable{
    private int serialNumber;
    private String name;
    private int numOfFans;
    private Integer formedYear;
    private String origin;
    private boolean hasSplit;
    private String style;

    public Band(int serialNumber, String name, int numOfFans, Integer formedYear,
                String origin, boolean hasSplit, String style) {
        setSerialNumber(serialNumber);
        setName(name);
        setNumOfFans(numOfFans);
        setFormedYear(formedYear);
        setOrigin(origin);
        setHasSplit(hasSplit);
        setStyle(style);
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Band band = (Band) o;
        return hasSplit() == band.hasSplit() &&
                Objects.equals(getName(), band.getName()) &&
                Objects.equals(getOrigin(), band.getOrigin());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getOrigin(), hasSplit());
    }

    public int getNumOfFans() {
        return numOfFans;
    }

    public void setNumOfFans(int numOfFans) {
        this.numOfFans = numOfFans;
    }

    public Integer getFormedYear() {
        return formedYear;
    }

    public void setFormedYear(Integer formedYear) {
        this.formedYear = formedYear;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public boolean hasSplit() {
        return hasSplit;
    }

    public void setHasSplit(boolean hasSplit) {
        this.hasSplit = hasSplit;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
