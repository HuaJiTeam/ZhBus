package huajiteam.zhuhaibus;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by KelaKim on 2016/5/2.
 */
public class GetConfig implements java.io.Serializable {
    //private SharedPreferences sp;

    private String busApiUrl;
    private String hintLogo;

    private boolean autoFlushNotice;
    private boolean titleIsBus;

    private boolean firstRun;
    //private String lastVersion;

    private boolean doNotDisplayAds;

    private int waitTime;

    public GetConfig(Context context) {
        SharedPreferences sp;
        sp =  PreferenceManager.getDefaultSharedPreferences(context);
        this.busApiUrl = sp.getString("bus_api", "http://www.zhbuswx.com/Handlers/BusQuery.ashx");
        this.hintLogo = sp.getString("hint_logo", "apple_moon_emoji");
        //this.lastVersion = sp.getString("last_version", "1.0");

        this.autoFlushNotice = sp.getBoolean("auto_flush_notice", false);
        this.titleIsBus = sp.getBoolean("title_is_bus", false);
        this.firstRun = sp.getBoolean("first_run", true);
        this.doNotDisplayAds = sp.getBoolean("do_not_display_ad", false);

        if (this.busApiUrl.equals("")) {
            this.busApiUrl = "http://www.zhbuswx.com/Handlers/BusQuery.ashx";
            sp.edit().putString("search_line_api_url", this.busApiUrl).apply();
        }

        try {
            this.waitTime = Integer.parseInt(sp.getString("auto_flush_wait_time", "10"));
        } catch (NumberFormatException e) {
            this.waitTime = 10;
            sp.edit().putString("auto_flush_wait_time", "10").apply();
        }
    }

    public String getBusApiUrl() {
        return this.busApiUrl;
    }

    public String getSearchBusLineUrl() {
        return this.busApiUrl;
    }

    public String getSearchStationUrl() {
        return this.busApiUrl;
    }

    public String getSearchOnlineBusUrl() {
        return this.busApiUrl;
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

    public boolean getIsFirstRun() {
        return this.firstRun;
    }

    public boolean getDoNotDisplayAds() {
        return this.doNotDisplayAds;
    }
}
