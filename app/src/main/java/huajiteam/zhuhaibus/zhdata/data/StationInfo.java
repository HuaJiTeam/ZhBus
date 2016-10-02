package huajiteam.zhuhaibus.zhdata.data;

import java.io.Serializable;

/**
 * Created by KelaKim on 2016/5/4.
 */
public class StationInfo extends BusData {
    private String Id;
    private String Name;
    private String Lng;
    private String Lat;
    private String Description;

    public String getId() {
        return this.Id;
    }

    public String getName() {
        return this.Name;
    }

    public String getLongitude() {
        return this.Lng;
    }

    public String getLatitude() {
        return this.Lat;
    }

    public String getDescription() {
        return this.Description;
    }
}
