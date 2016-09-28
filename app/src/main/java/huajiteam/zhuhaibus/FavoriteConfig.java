package huajiteam.zhuhaibus;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import huajiteam.zhuhaibus.zhdata.data.BusLineInfo;
import huajiteam.zhuhaibus.zhdata.GetBusInfoFromJson;
import huajiteam.zhuhaibus.zhdata.exceptions.BusLineInvalidException;

/**
 * Created by KelaKim on 2016/5/11.
 */
class FavoriteConfig implements Serializable {
    private BusLineInfo[] busLineInfos;
    private Activity activity;

    FavoriteConfig(Activity activity) {
        this.activity = activity;
        reloadData();
    }

    public BusLineInfo[] getBusLineInfos() {
        return this.busLineInfos;
    }

    ArrayList<BusLineInfo> getBusLineInfoArray() {
        ArrayList<BusLineInfo> arrayList = new ArrayList<BusLineInfo>();
        Collections.addAll(arrayList, busLineInfos);
        return arrayList;
    }

    private void setFavoriteJson(String favoriteJson) {
        try {
            this.busLineInfos = new GetBusInfoFromJson().getBusLineInfoFromJson(favoriteJson);
        } catch (BusLineInvalidException e) {
            //
        }
        saveData(favoriteJson);
    }

    public void setFavoriteJson(BusLineInfo[] busLineInfos) {
        this.busLineInfos = busLineInfos;
        saveData(busLineInfos);
    }

    public void setFavoriteJson(ArrayList<BusLineInfo> oldInfo) {
        int count = oldInfo.size();
        BusLineInfo[] busLineInfos = new BusLineInfo[count];
        for (int i = 0; i < count; i++) {
            busLineInfos[i] = oldInfo.get(i);
        }
        this.busLineInfos = busLineInfos;
        saveData(busLineInfos);
    }

    void reloadData() {
        SharedPreferences favorites = activity.getSharedPreferences("favorite_list", 0);
        String favoriteJson = favorites.getString("favorite_json", "[]");
        try {
            this.busLineInfos = new GetBusInfoFromJson().getBusLineInfoFromJson(favoriteJson);
        } catch (BusLineInvalidException e) {
            //
        }
    }

    private void saveData(String json) {
        SharedPreferences favorites = activity.getSharedPreferences("favorite_list", 0);
        SharedPreferences.Editor editor = favorites.edit();
        editor.putString("favorite_json", json);
        editor.apply();
    }

    private void saveData(BusLineInfo[] busLineInfos) {
        String json = new Gson().toJson(busLineInfos, BusLineInfo[].class);
        saveData(json);
    }

    private void saveData(ArrayList<BusLineInfo> oldInfo) {
        int count = oldInfo.size();
        BusLineInfo[] busLineInfos = new BusLineInfo[count];
        for (int i = 0; i < count; i++) {
            busLineInfos[i] = oldInfo.get(i);
        }
        this.busLineInfos = busLineInfos;
        String json = new Gson().toJson(busLineInfos, BusLineInfo[].class);
        saveData(json);
    }

    void addData(BusLineInfo busLineInfo) {
        ArrayList<BusLineInfo> tmpData = this.getBusLineInfoArray();
        tmpData.add(busLineInfo);
        saveData(tmpData);
    }

    void clearAllData() {
        this.setFavoriteJson("[]");
    }
}
