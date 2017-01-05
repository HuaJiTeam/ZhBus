package huajiteam.zhuhaibus.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import huajiteam.zhuhaibus.service.exceptions.BusLineNotFoundException;
import huajiteam.zhuhaibus.service.exceptions.FromStationNotFoundException;

public class ListenLinesManager {

    private static Intent service;

    public void startService(Activity activity) {
        Log.i("Service", "Starting");
        service = new Intent();
        service.setClass(activity, BusNotificationService.class);
        activity.startService(service);
    }

    public void stopListener(Context context) {
        if (service == null) {
            throw new NullPointerException("Service not running.");
        }
        context.stopService(service);
    }

    void setServiceNull() {
        service = null;
    }

    public void clearListeningBus() {
        ListenLines.busdata = null;
    }

    public boolean isServiceRunning(Context context) {
        return this.isServiceRunning(context, "huajiteam.zhuhaibus.service.BusNotificationService");
    }

    private boolean isServiceRunning(Context context, String serviceName){
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : services) {
            String name = info.service.getClassName();
            if (serviceName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<ListenData> getListeningBus() {
        return ListenLines.busdata;
    }

    public boolean getListeningBusIsEmpty() {
        return ListenLines.busdata == null || ListenLines.busdata.isEmpty();
    }

    public void addBus(String busLine, String fromStation, String stationName) {
        if (ListenLines.busdata == null) {
            ListenLines.busdata = new ArrayList<>();
        }
        boolean busLineExisted = false;
        boolean stationNameExisted = false;
        for (ListenData listenData : ListenLines.busdata) {
            if (listenData.busLine.equals(busLine)) {
                busLineExisted = true;
                if (listenData.fromStation.equals(fromStation)) {
                    for (ListenBus s : listenData.listeningStations) {
                        if (s.getStationName().equals(stationName)) {
                            stationNameExisted = true;
                            break;
                        }
                    }
                    if (!stationNameExisted) {
                        listenData.addListeningStation(new ListenBus(stationName));
                        return;
                    }
                    break;
                } else {
                    busLineExisted = false;
                }
            }
        }

        if (!busLineExisted) {
            ListenLines.busdata.add(
                new ListenData()
                .setBusLine(busLine)
                .setFromStation(fromStation)
                .addListeningStation(new ListenBus(stationName))
            );
        }
    }

    public ArrayList<String[]> getArrayListeningList() {
        ArrayList<String[]> tmpArray = new ArrayList<>();
        if (ListenLines.busdata == null) {
            return tmpArray;
        }
        for (ListenData listenData : ListenLines.busdata) {
            for (ListenBus s : listenData.listeningStations) {
                // str[0] 是公交线路， str[1] 是从哪个车站出发的， str[2] 是车站名字
                tmpArray.add(new String[]{listenData.busLine, listenData.fromStation, s.getStationName()});
            }
        }
        return tmpArray;
    }

    public boolean getLineIsListening(String busLine, String fromStation, String stationName) {
        if (ListenLines.busdata == null) {
            return false;
        }
        for (ListenData listenData : ListenLines.busdata) {
            if (listenData.busLine.equals(busLine)) {
                if (listenData.fromStation.equals(fromStation)) {
                    for (ListenBus s : listenData.listeningStations) {
                        if (s.getStationName().equals(stationName)) {
                            return true;
                        }
                    }
                    break;
                }
            }
        }
        return false;
    }

    public void removeListenStation(String busLine, String fromStation, String stationName) {
        if (ListenLines.busdata == null) {
            return;
        }
        int line = 0;
        for (ListenData listenData : ListenLines.busdata) {
            if (listenData.busLine.equals(busLine)) {
                if (listenData.fromStation.equals(fromStation)) {
                    int station = 0;
                    for (ListenBus s : listenData.listeningStations) {
                        if (s.getStationName().equals(stationName)) {
                            listenData.listeningStations.remove(station);
                            if (listenData.listeningStations.isEmpty()) {
                                ListenLines.busdata.remove(line);
                            }
                            return;
                        }
                        station += 1;
                    }
                    return;
                }
            }
            line += 1;
        }
    }

    public void removeListenLine(String busLine, String fromStation) {
        if (ListenLines.busdata == null) {
            return;
        }
        int line = 0;
        for (ListenData listenData : ListenLines.busdata) {
            if (listenData.busLine.equals(busLine)) {
                ListenLines.busdata.remove(line);
            }
            line += 1;
        }
    }

    public boolean isStationExisted(String busLine, String fromStation, String stationName) throws Exception {
        if (ListenLines.busdata == null) {
            return false;
        }
        boolean busLineFound = false;
        for (ListenData listenData : ListenLines.busdata) {
            if (listenData.busLine.equals(busLine)) {
                busLineFound = true;
                if (listenData.fromStation.equals(fromStation)) {
                    for (ListenBus s : listenData.listeningStations) {
                        if (s.getStationName().equals(stationName)){
                            return true;
                        }
                    }
                }
            }
        }
        if (busLineFound) {
            throw new FromStationNotFoundException();
        } else {
            throw new BusLineNotFoundException();
        }
    }
}
