package huajiteam.zhbus.zhdata;

import java.io.Serializable;

/**
 * Created by KelaKim on 2016/5/4.
 */
public class OnlineBusInfo extends BusData {
    private String BusNumber;
    private String CurrentStation;

    public String getBusNumber() {
        return this.BusNumber;
    }

    public String getCurrentStation() {
        return this.CurrentStation;
    }
}
