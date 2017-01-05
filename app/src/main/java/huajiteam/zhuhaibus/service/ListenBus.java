package huajiteam.zhuhaibus.service;

import java.util.ArrayList;

/**
 * Created by nicon on 2016/11/17.
 */

public class ListenBus {

    private String stationName;
    private ArrayList<String> inStationBus = new ArrayList<>();

    public ListenBus(String stationName) {
        this.stationName = stationName;
    }

    public String getStationName() {
        return this.stationName;
    }


    boolean getBusIsInStation(String busName) {
        for (String i : this.inStationBus) {
            if (i.equals(busName)) {
                return true;
            }
        }
        return false;
    }

    void addBusInStation(String busName) {
        this.inStationBus.add(busName);
    }

    ArrayList<String> getInStationBus() {
        return this.inStationBus;
    }

    void setBusLeave(String busName) {
        int length = inStationBus.size();
        for (int i=0; i<length; i++) {
            if (inStationBus.get(i).equals(busName)) {
                inStationBus.remove(i);
            }
        }
    }

}
