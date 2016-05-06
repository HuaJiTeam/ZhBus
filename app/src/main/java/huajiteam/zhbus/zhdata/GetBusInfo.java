package huajiteam.zhbus.zhdata;

import huajiteam.zhbus.zhdata.exceptions.BusLineInvalidException;
import huajiteam.zhbus.zhdata.exceptions.HttpCodeInvalidException;
import okhttp3.Response;

import java.io.IOException;

/**
 * Created by KelaKim on 2016/5/4.
 */
public class GetBusInfo extends GetBusInfoFromJson {

    public BusLineInfo[] getBusLineInfo(String busApiUrl, String busLine) throws IOException {
        //http://www.zhbuswx.com/BusLine/WS.asmx/SearchLine
        Response resData = new GetWebContent().postJsonData(
                busApiUrl,
                "{\"key\": \"" + busLine + "\"}"
        );
        if (resData.code() != 200) {
            throw new HttpCodeInvalidException(resData);
        }
        String resStr = resData.body().string();
        if (resStr.equals("{\"d\":[]}")) {
            throw new BusLineInvalidException(resData);
        }
        return getBusLineInfoFromJson(resStr);
    }

    public StationInfo[] getStationInfo(String busApiUrl, String busID) throws IOException {
        //http://www.zhbuswx.com/BusLine/WS.asmx/LoadStationByLineId
        Response resData = new GetWebContent().postJsonData(
                busApiUrl,
                "{\"lineId\": \"" + busID + "\"}"
        );
        if (resData.code() != 200) {
            throw new HttpCodeInvalidException(resData);
        }
        String resStr = resData.body().string();
        if (resStr.replace("{\"d\":[]}", "").equals("")) {
            throw new BusLineInvalidException(resData);
        }
        return getStationInfoFromJson(resStr);
    }

    public OnlineBusInfo[] getOnlineBusInfo(String busApiUrl, String busName, String busWay) throws IOException {
        //http://www.zhbuswx.com/BusLine/WS.asmx/GetBusListOnRoad
        Response resData = new GetWebContent().postJsonData(
                busApiUrl,
                "{\"lineName\":\"" + busName + "\",\"fromStation\":\"" + busWay + "\"}"
        );
        if (resData.code() != 200) {
            throw new HttpCodeInvalidException(resData);
        }
        String resStr = resData.body().string();
        if (resStr.replace("{\"d\":[]}", "").equals("")) {
            throw new BusLineInvalidException(resData);
        }
        return getOnlineBusInfoFromJson(resStr);
    }
}
