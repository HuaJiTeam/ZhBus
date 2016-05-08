package huajiteam.zhbus;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.Serializable;
import java.io.StreamCorruptedException;

/**
 * Created by KelaKim on 2016/5/2.
 */
public class GetConfig implements Serializable {
    private String searchBusLineUrl;
    private String searchStationUrl;
    private String searchOnlineBusUrl;
    private String hintLogo;

    private boolean autoFlushNotice;
    private boolean titleIsBus;

    private int waitTime;

    public GetConfig(Context context) {
        SharedPreferences sp =  PreferenceManager.getDefaultSharedPreferences(context);
        this.searchBusLineUrl = sp.getString("search_line_api_url", "https://lab.yhtng.com/ZhuhaiBus/Sample.php?method=SearchLine");
        this.searchStationUrl = sp.getString("search_station_api_url", "https://lab.yhtng.com/ZhuhaiBus/Sample.php?method=StationInfo");
        this.searchOnlineBusUrl = sp.getString("search_online_bus_api_url", "https://lab.yhtng.com/ZhuhaiBus/Sample.php?method=OnlineBus");
        this.hintLogo = sp.getString("hint_logo", "apple_moon_emoji");

        this.autoFlushNotice = sp.getBoolean("auto_flush_notice", false);
        this.titleIsBus = sp.getBoolean("title_is_bus", false);

        if (this.searchBusLineUrl.equals("")) {
            this.searchBusLineUrl = "https://lab.yhtng.com/ZhuhaiBus/Sample.php?method=SearchLine";
            sp.edit().putString("search_line_api_url", this.searchBusLineUrl).apply();
        }

        if (this.searchStationUrl.equals("")) {
            this.searchStationUrl = "https://lab.yhtng.com/ZhuhaiBus/Sample.php?method=StationInfo";
            sp.edit().putString("search_station_api_url", this.searchStationUrl).apply();
        }

        if (this.searchOnlineBusUrl.equals("")) {
            this.searchOnlineBusUrl = "https://lab.yhtng.com/ZhuhaiBus/Sample.php?method=OnlineBus";
            sp.edit().putString("search_online_bus_api_url", this.searchOnlineBusUrl).apply();
        }

        try {
            this.waitTime = Integer.parseInt(sp.getString("auto_flush_wait_time", "10"));
        } catch (NumberFormatException e) {
            this.waitTime = 10;
            sp.edit().putString("auto_flush_wait_time", "10").apply();
        }
    }

    public String getSearchBusLineUrl() {
        return this.searchBusLineUrl;
    }

    public String getSearchStationUrl() {
        return this.searchStationUrl;
    }

    public String getSearchOnlineBusUrl() {
        return this.searchOnlineBusUrl;
    }

    public String getHintLogo() {
        return this.hintLogo;
    }

    public int getWaitTime() {
        return this.waitTime;
    }

    public boolean getAutoFlushNotice() {
        return this.autoFlushNotice;
    }

    public boolean getTitleIsBus() {
        return this.titleIsBus;
    }
}
