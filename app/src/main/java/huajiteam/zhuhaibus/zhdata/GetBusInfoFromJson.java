package huajiteam.zhuhaibus.zhdata;

import com.google.gson.Gson;

import huajiteam.zhuhaibus.zhdata.exceptions.BusLineInvalidException;

/**
 * Created by KelaKim on 2016/5/4.
 */
public class GetBusInfoFromJson {
    public BusLineInfo[] getBusLineInfoFromJson(String jsonData) throws BusLineInvalidException {
        if (jsonData.indexOf("{\"flag\":") != -1) {
            BusLineInfoFlag busObject = new Gson().fromJson(jsonData, BusLineInfoFlag.class);
            if (busObject.getFlag() != 1002) {
                throw new BusLineInvalidException();
            }
            return busObject.getData();
        } else {
            return new Gson().fromJson(jsonData, BusLineInfo[].class);
        }
    }

    StationInfo[] getStationInfoFromJson(String jsonData) {
        StationInfoFlag busObject = new Gson().fromJson(jsonData, StationInfoFlag.class);
        return busObject.getData();
    }

    OnlineBusInfo[] getOnlineBusInfoFromJson(String jsonData) {
        OnlineBusInfoFlag busObject = new Gson().fromJson(jsonData, OnlineBusInfoFlag.class);
        return busObject.getData();
    }
}
