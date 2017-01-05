package huajiteam.zhuhaibus.zhdata;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;

import huajiteam.zhuhaibus.zhdata.data.BusLineInfo;
import huajiteam.zhuhaibus.zhdata.data.BusLineInfoFlag;
import huajiteam.zhuhaibus.zhdata.data.OnlineBusInfo;
import huajiteam.zhuhaibus.zhdata.data.OnlineBusInfoFlag;
import huajiteam.zhuhaibus.zhdata.data.StationInfo;
import huajiteam.zhuhaibus.zhdata.data.StationInfoFlag;
import huajiteam.zhuhaibus.zhdata.exceptions.BusLineInvalidException;

/**
 * Created by KelaKim on 2016/5/4.
 */
public class GetBusInfoFromJson {
    public BusLineInfo[] getBusLineInfoFromJson(String jsonData) throws BusLineInvalidException {
        if (jsonData.substring(0,1).equals("<")) {
            throw new NullPointerException(jsonData);
        }
        Log.i("test", "ok");
        if (jsonData.contains("{\"flag\":")) {
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
        if (jsonData.substring(0,1).equals("<")) {
            throw new NullPointerException(jsonData);
        }
        StationInfoFlag busObject = new Gson().fromJson(jsonData, StationInfoFlag.class);
        return busObject.getData();
    }

    OnlineBusInfo[] getOnlineBusInfoFromJson(String jsonData) {
        if (jsonData.substring(0,1).equals("<")) {
            throw new NullPointerException(jsonData);
        }
        OnlineBusInfoFlag busObject = new Gson().fromJson(jsonData, OnlineBusInfoFlag.class);
        return busObject.getData();
    }
}
