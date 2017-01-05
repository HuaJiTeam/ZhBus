package huajiteam.zhuhaibus.service;

import java.util.ArrayList;

public class ListenData {

    String busLine;
    String fromStation;
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
}
