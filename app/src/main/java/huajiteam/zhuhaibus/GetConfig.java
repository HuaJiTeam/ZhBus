package huajiteam.zhuhaibus;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.Serializable;

/**
 * Created by KelaKim on 2016/5/2.
 */
public class GetConfig implements Serializable {
    //private SharedPreferences sp;

    private Activity activity;

    private String busApiUrl;
    private String stataicIP;
    private String hintLogo;

    private boolean enableStaticIP;
    private boolean autoFlushNotice;
    private boolean titleIsBus;

    private boolean firstRun;
    //private String lastVersion;

    private boolean doNotDisplayAds;

    private int waitTime;

    GetConfig(Activity activity) {
        this.activity = activity;
        this.reloadData();
    }

    void reloadData() {
        SharedPreferences sp =  PreferenceManager.getDefaultSharedPreferences(activity);
        this.busApiUrl = sp.getString("bus_api", "http://www.zhbuswx.com/Handlers/BusQuery.ashx");
        this.stataicIP = sp.getString("static_ip", "120.25.149.162");
        this.hintLogo = sp.getString("hint_logo", "apple_moon_emoji");
        //this.lastVersion = sp.getString("last_version", "1.0");

        this.enableStaticIP = sp.getBoolean("enable_static_ip", true);
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

    String getBusApiUrl() {
        return this.busApiUrl;
    }

    String getHintLogo() {
        return this.hintLogo;
    }

    String getStataicIP() {
        return this.stataicIP;
    }

    int getWaitTime() {
        return this.waitTime;
    }

    boolean getAutoFlushNotice() {
        return this.autoFlushNotice;
    }

    boolean getTitleIsBus() {
        return this.titleIsBus;
    }

    boolean getIsFirstRun() {
        return this.firstRun;
    }

    boolean getDoNotDisplayAds() {
        return this.doNotDisplayAds;
    }

    boolean getEnableStaticIP() {
        return this.enableStaticIP;
    }
}
