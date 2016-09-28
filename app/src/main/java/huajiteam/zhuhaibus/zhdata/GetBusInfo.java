package huajiteam.zhuhaibus.zhdata;

import huajiteam.zhuhaibus.zhdata.data.BusLineInfo;
import huajiteam.zhuhaibus.zhdata.data.OnlineBusInfo;
import huajiteam.zhuhaibus.zhdata.data.StationInfo;
import huajiteam.zhuhaibus.zhdata.exceptions.HttpCodeInvalidException;
import okhttp3.Response;

import java.io.IOException;

/**
 * Created by KelaKim on 2016/5/4.
 */
public class GetBusInfo extends GetBusInfoFromJson {

    public BusLineInfo[] getBusLineInfo(String busApiUrl, String busLine) throws IOException {
        Response resData = new GetWebContent().getData(busApiUrl + "?handlerName=GetLineListByLineName&key=" + busLine);
        if (resData.code() != 200) {
            throw new HttpCodeInvalidException(resData);
        }
        String resStr = resData.body().string();
        return getBusLineInfoFromJson(resStr);
    }

    public StationInfo[] getStationInfo(String busApiUrl, String busID) throws IOException {
        Response resData = new GetWebContent().getData(busApiUrl +
                "?handlerName=GetStationList&lineId=" + busID
        );
        if (resData.code() != 200) {
            throw new HttpCodeInvalidException(resData);
        }
        String resStr = resData.body().string();
        return getStationInfoFromJson(resStr);
    }

    public OnlineBusInfo[] getOnlineBusInfo(String busApiUrl, String busName, String busWay) throws IOException {
        Response resData = new GetWebContent().getData(busApiUrl +
                "?handlerName=GetBusListOnRoad&lineName=" + busName +
                "&fromStation=" + busWay
        );
        if (resData.code() != 200) {
            throw new HttpCodeInvalidException(resData);
        }
        String resStr = resData.body().string();
        return getOnlineBusInfoFromJson(resStr);
    }

    public BusLineInfo[] getBusLineInfoByStationName(String busApiUrl, String stationName) throws IOException {
        Response resData = new GetWebContent().getData(busApiUrl + "?handlerName=GetLineListByStationName&key=" + stationName);
        if (resData.code() != 200) {
            throw new HttpCodeInvalidException(resData);
        }
        String resStr = resData.body().string();
        return getBusLineInfoFromJson(resStr);
    }

    public BusLineInfo[] getBusLineInfo(String busApiUrl, String busLine, String staticIP) throws IOException {
        Response resData = new GetWebContent().getData(busApiUrl + "?handlerName=GetLineListByLineName&key=" + busLine, staticIP);
        if (resData.code() != 200) {
            throw new HttpCodeInvalidException(resData);
        }
        String resStr = resData.body().string();
        return getBusLineInfoFromJson(resStr);
    }

    public StationInfo[] getStationInfo(String busApiUrl, String busID, String staticIP) throws IOException {
        Response resData = new GetWebContent().getData(busApiUrl +
                "?handlerName=GetStationList&lineId=" + busID
                , staticIP
        );
        if (resData.code() != 200) {
            throw new HttpCodeInvalidException(resData);
        }
        String resStr = resData.body().string();
        return getStationInfoFromJson(resStr);
    }

    public OnlineBusInfo[] getOnlineBusInfo(String busApiUrl, String busName, String busWay, String staticIP) throws IOException {
        Response resData = new GetWebContent().getData(busApiUrl +
                "?handlerName=GetBusListOnRoad&lineName=" + busName +
                "&fromStation=" + busWay
                , staticIP
        );
        if (resData.code() != 200) {
            throw new HttpCodeInvalidException(resData);
        }
        String resStr = resData.body().string();
        return getOnlineBusInfoFromJson(resStr);
    }

    public BusLineInfo[] getBusLineInfoByStationName(String busApiUrl, String stationName, String staticIP) throws IOException {
        Response resData = new GetWebContent().getData(busApiUrl + "?handlerName=GetLineListByStationName&key=" + stationName, staticIP);
        if (resData.code() != 200) {
            throw new HttpCodeInvalidException(resData);
        }
        String resStr = resData.body().string();
        return getBusLineInfoFromJson(resStr);
    }
}
