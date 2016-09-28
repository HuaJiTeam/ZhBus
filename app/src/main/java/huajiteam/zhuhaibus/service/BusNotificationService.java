package huajiteam.zhuhaibus.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BusNotificationService extends Service {
    public BusNotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: 增加到站提醒
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
