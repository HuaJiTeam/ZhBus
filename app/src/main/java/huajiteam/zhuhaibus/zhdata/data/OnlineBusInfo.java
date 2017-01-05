package huajiteam.zhuhaibus.zhdata.data;

import java.io.Serializable;

/**
 * Created by KelaKim on 2016/5/4.
 */
public class OnlineBusInfo extends BusData {
    private String BusNumber;
    private String CurrentStation;

    public OnlineBusInfo() {}

    public OnlineBusInfo(String busNumber, String currentStation) {
        this.BusNumber = busNumber;
        this.CurrentStation = currentStation;
    }

    public String getBusNumber() {
        return this.BusNumber;
    }

    public String getCurrentStation() {
        return this.CurrentStation;
    }
}
