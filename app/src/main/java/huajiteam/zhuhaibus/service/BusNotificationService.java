package huajiteam.zhuhaibus.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Streams;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import huajiteam.zhuhaibus.GetConfig;
import huajiteam.zhuhaibus.LineListenerActivity;
import huajiteam.zhuhaibus.MainActivity;
import huajiteam.zhuhaibus.OnlineBusActivity;
import huajiteam.zhuhaibus.R;
import huajiteam.zhuhaibus.zhdata.GetBusInfo;
import huajiteam.zhuhaibus.zhdata.data.BusLineInfo;
import huajiteam.zhuhaibus.zhdata.data.OnlineBusInfo;
import huajiteam.zhuhaibus.zhdata.exceptions.HttpCodeInvalidException;

import static android.support.v4.app.NotificationCompat.*;

public class BusNotificationService extends Service {

    GetConfig config;
    Timer timer;

    public BusNotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i("BusService", "Create");
        config = new GetConfig(getApplicationContext());
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("BusService", "Running");
        timer = new Timer();
        if (config.getWaitTime() <= 0) {
            timer.schedule(new Listener(), 0, 600000);
        } else {
            timer.schedule(new Listener(), 0, config.getWaitTime() * 1000);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i("BusService", "Stop");
        timer.cancel();
        new ListenLinesManager().setServiceNull();
        super.onDestroy();
    }

    private OnlineBusInfo[] getBusLineInfo(String busLine, String fromStation) {
        OnlineBusInfo[] onlineBusInfos = null;
        try {
            if (config.getEnableStaticIP()) {
                onlineBusInfos = new GetBusInfo().getOnlineBusInfo(
                        this.config.getBusApiUrl(),
                        busLine,
                        fromStation,
                        config.getStataicIP()
                );
            } else {
                onlineBusInfos = new GetBusInfo().getOnlineBusInfo(
                        this.config.getBusApiUrl(),
                        busLine,
                        fromStation
                );

            }
        } catch (Exception ignore) {
        }
        if (onlineBusInfos == null) {
            onlineBusInfos = new OnlineBusInfo[0];
        }
        return onlineBusInfos;
    }



    class Listener extends TimerTask {

        @Override
        public void run() {
            if (ListenLines.busdata == null || ListenLines.busdata.isEmpty()) {
                stopSelf();
                return;
            }
            for (ListenData listenData : ListenLines.busdata) {
                OnlineBusInfo[] obi;
                try {
                    obi = getBusLineInfo(listenData.busLine, listenData.fromStation);
                } catch (Exception e) {
                    return;
                }
                for (OnlineBusInfo station : obi) {
                    for (ListenBus listenBus : listenData.listeningStations) {
                        if (station.getCurrentStation().equals(listenBus.getStationName())) {
                            if (!listenBus.getBusIsInStation(station.getBusNumber())) {
                                Log.i("Bus", "Changed");
                                listenBus.addBusInStation(station.getBusNumber());
                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                Builder notification = new Builder(BusNotificationService.this);
                                notification.setSmallIcon(R.drawable.ic_md_bus_white);
                                notification.setContentTitle(listenData.busLine + " 已到达 " + listenBus.getStationName());
                                notification.setContentText("往 " + listenData.fromStation + " 方向，" + station.getBusNumber());
                                notification.setAutoCancel(true);
                                notification.setTicker(listenData.busLine + " 已到达 " + listenBus.getStationName());
                                notification.setDefaults(Notification.DEFAULT_SOUND);
                                notification.setPriority(NotificationCompat.PRIORITY_HIGH);
                                Intent serviceManager = new Intent(BusNotificationService.this, LineListenerActivity.class);
                                PendingIntent resultPendingIntent = PendingIntent.getActivity(
                                        BusNotificationService.this,
                                        0,
                                        serviceManager,
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                );
                                notification.setContentIntent(resultPendingIntent);
                                notificationManager.notify((int)(Math.random()*10000), notification.build());
                                Log.i("hh",""+listenBus.getBusIsInStation(station.getBusNumber()));
                            }
                        }
                    }
                }
            }
        }
    }
}
