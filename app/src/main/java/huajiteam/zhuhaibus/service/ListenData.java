package huajiteam.zhuhaibus.service;

import java.util.ArrayList;

public class ListenData {

    String busLine;
    String fromStation;
    String toStation;
    ArrayList<ListenBus> listeningStations = new ArrayList<>();

    public ListenData addListeningStation(ListenBus stationName) {
        this.listeningStations.add(stationName);
        return this;
    }

    public ListenData setBusLine(String line) {
        this.busLine = line;
        return this;
    }

    public ListenData setFromStation(String fromStation) {
        this.fromStation = fromStation;
        return this;
    }

    public ListenData setToStation(String toStation) {
        this.toStation = toStation;
        return this;
    }
}
