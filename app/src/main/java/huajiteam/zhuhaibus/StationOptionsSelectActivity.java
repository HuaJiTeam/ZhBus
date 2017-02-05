package huajiteam.zhuhaibus;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import huajiteam.zhuhaibus.service.BusNotificationService;
import huajiteam.zhuhaibus.service.ListenLinesManager;
import huajiteam.zhuhaibus.zhdata.data.BusLineInfo;

public class StationOptionsSelectActivity extends AppCompatActivity {

    private BusLineInfo busLineInfo;
    private String stationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_options_select);

        GetConfig config = new GetConfig(this);
        Button searchStationButton = (Button) findViewById(R.id.searchStation);
        final Button listenStationButton = (Button) findViewById(R.id.notification);
        TextView displayStation = (TextView) findViewById(R.id.displayStation);
        TextView displayLine = (TextView) findViewById(R.id.displayLine);

        Bundle bundle = getIntent().getExtras();
        this.busLineInfo = (BusLineInfo) bundle.get("busLineInfo");
        this.stationName = (String) bundle.get("stationName");

        final ListenLinesManager listenLinesManager = new ListenLinesManager();

        if (new ListenLinesManager().getLineIsListening(
                busLineInfo.getName(),
                busLineInfo.getFromStation(),
                stationName)
                ) {
            listenStationButton.setText("停止监听此站点");
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.searchStation:
                        Intent intent = new Intent();
                        intent.setClass(StationOptionsSelectActivity.this, GetLineInfoByStation.class);
                        intent.putExtra("stationName", stationName);
                        startActivity(intent);
                        break;
                    case R.id.notification:
                        if (listenLinesManager.getLineIsListening(
                                busLineInfo.getName(),
                                busLineInfo.getFromStation(),
                                stationName)
                                ) {
                            listenLinesManager.removeListenStation(
                                    busLineInfo.getName(),
                                    busLineInfo.getFromStation(),
                                    stationName
                            );
                            Snackbar.make(listenStationButton, "成功移除。", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            listenStationButton.setText("当车辆到站的时候通知我");
                        } else {
                            listenLinesManager.addBus(
                                    busLineInfo.getName(),
                                    busLineInfo.getFromStation(),
                                    busLineInfo.getToStation(),
                                    stationName
                            );
                            if (listenLinesManager.isServiceRunning(getApplicationContext())) {
                                Log.i("Service", "Running");
                                Snackbar.make(listenStationButton, "成功加入了监听列表，应用将在有车到站时向您发送通知。", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            } else {
                                Log.i("Service", "Not Running");
                                Snackbar.make(listenStationButton, "成功加入了监听列表并开启了服务，应用将在有车到站时向您发送通知。", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                listenLinesManager.startService(StationOptionsSelectActivity.this);
                            }
                            listenStationButton.setText("停止监听此站点");
                        }
                }
            }
        };
        displayStation.setText(stationName);
        displayLine.setText(busLineInfo.getName());
        searchStationButton.setOnClickListener(onClickListener);
        listenStationButton.setOnClickListener(onClickListener);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getDelegate().getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Add advertisement
        AdView mAdView = (AdView) findViewById(R.id.adView);
        if (!config.getDoNotDisplayAds()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        } else if (mAdView != null) {
            mAdView.getLayoutParams().height = 0;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
