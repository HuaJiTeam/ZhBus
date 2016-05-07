package huajiteam.zhbus;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.Serializable;

/**
 * Created by KelaKim on 2016/5/2.
 */
public class GetConfig implements Serializable {
    public String searchBusLineUrl;
    public String searchStationUrl;
    public String searchOnlineBusUrl;

    public boolean autoFlushNotice;

    public int waitTime;

    public GetConfig(Context context) {
        SharedPreferences sp =  PreferenceManager.getDefaultSharedPreferences(context);
        searchBusLineUrl = sp.getString("search_line_api_url", "https://lab.yhtng.com/ZhuhaiBus/Sample.php?method=SearchLine");
        searchStationUrl = sp.getString("search_station_api_url", "https://lab.yhtng.com/ZhuhaiBus/Sample.php?method=StationInfo");
        searchOnlineBusUrl = sp.getString("search_online_bus_api_url", "https://lab.yhtng.com/ZhuhaiBus/Sample.php?method=OnlineBus");
        autoFlushNotice = sp.getBoolean("auto_flush_notice", false);

        if (searchBusLineUrl.equals("")) {
            searchBusLineUrl = "https://lab.yhtng.com/ZhuhaiBus/Sample.php?method=SearchLine";
            sp.edit().putString("search_line_api_url", searchBusLineUrl).apply();
        }

        if (searchStationUrl.equals("")) {
            searchStationUrl = "https://lab.yhtng.com/ZhuhaiBus/Sample.php?method=StationInfo";
            sp.edit().putString("search_station_api_url", searchStationUrl).apply();
        }

        if (searchOnlineBusUrl.equals("")) {
            searchOnlineBusUrl = "https://lab.yhtng.com/ZhuhaiBus/Sample.php?method=OnlineBus";
            sp.edit().putString("search_online_bus_api_url", searchOnlineBusUrl).apply();
        }

        try {
            waitTime = Integer.parseInt(sp.getString("auto_flush_wait_time", "10"));
        } catch (NumberFormatException e) {
            waitTime = 10;
            sp.edit().putString("auto_flush_wait_time", "10").apply();
        }
    }

    public String getSearchBusLineUrl() {
        return searchBusLineUrl;
    }

    public String getSearchStationUrl() {
        return searchStationUrl;
    }

    public String getSearchOnlineBusUrl() {
        return searchOnlineBusUrl;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public boolean getAutoFlushNotice() {
        return this.autoFlushNotice;
    }
}
