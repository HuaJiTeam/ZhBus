package huajiteam.zhuhaibus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import huajiteam.zhuhaibus.zhdata.data.BusLineInfo;
import huajiteam.zhuhaibus.zhdata.GetBusInfo;
import huajiteam.zhuhaibus.zhdata.data.OnlineBusInfo;
import huajiteam.zhuhaibus.zhdata.data.StationInfo;
import huajiteam.zhuhaibus.zhdata.exceptions.HttpCodeInvalidException;

public class OnlineBusActivity extends AppCompatActivity {

    BusLineInfo busLineInfo;
    GetConfig config;
    StationInfo[] stationInfos = null;
    OnlineBusInfo[] onlineBusInfos;
    Timer timer;
    MAdapter mAdapter;
    ListView listView;

    private ProgressDialog progressDialog;

    boolean firstRun = true;
    boolean timerRunning = false;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (config.getWaitTime() <= 0) {
                        new Thread(new UpdateOnlineBuses(config, busLineInfo)).start();
                        timerRunning = false;
                    } else {
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(new UpdateOnlineBuses(config, busLineInfo), 0, config.getWaitTime() * 1000);
                        timerRunning = true;
                    }
                    break;
                case 1:
                    onlineBusInfos = (OnlineBusInfo[]) msg.obj;
                    mAdapter = new MAdapter(OnlineBusActivity.this);
                    if (listView != null) {
                        listView.setAdapter(mAdapter);
                    }
                    progressDialog.dismiss();
                    break;
                case 2:
                    if (config.getAutoFlushNotice()) {
                        makeSnackbar("少女祈祷中...");
                    }
                    onlineBusInfos = (OnlineBusInfo[]) msg.obj;
                    mAdapter.notifyDataSetChanged();
                    break;
                case -1:
                    makeAlert("出现了一个错误", "未知错误: " + msg.obj);
                    if (firstRun) {
                        progressDialog.dismiss();
                    }
                    break;
                case -1001:
                    makeSnackbar(getString(R.string.error_api_invalid));
                    if (firstRun) {
                        progressDialog.dismiss();
                    }
                    break;
                case -1003:
                    makeSnackbar(getString(R.string.network_error));
                    if (firstRun) {
                        progressDialog.dismiss();
                    }
                    break;
                case -2:
                    makeAlert("出现了一个错误", "未知错误: " + msg.obj);
                    timer.cancel();
                    timerRunning = false;
                    break;
                case -2001:
                    makeSnackbar(getString(R.string.error_api_invalid));
                    timer.cancel();
                    timerRunning = false;
                    break;
                case -2003:
                    makeSnackbar(getString(R.string.network_error));
                    timer.cancel();
                    timerRunning = false;
                    break;
                default:
                    makeSnackbar("噫");
                    if (firstRun) {
                        progressDialog.dismiss();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_bus);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getDelegate().getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();
        this.busLineInfo = (BusLineInfo) bundle.get("busLineInfo");
        this.config = new GetConfig(this);

        listView = (ListView) findViewById(R.id.onlineBusListView);
        this.progressDialog = ProgressDialog.show(this, getString(R.string.waiting), getString(R.string.loading));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(OnlineBusActivity.this, GetLineInfoByStation.class);
                intent.putExtra("stationName", stationInfos[position].getName());
                startActivity(intent);
            }
        });

        new Thread(new GetStation(config, busLineInfo)).start();

        FloatingActionButton reflushButton = (FloatingActionButton) findViewById(R.id.flushButton);
        reflushButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!config.getAutoFlushNotice()) {
                    makeSnackbar(getString(R.string.loading));
                }
                if (stationInfos == null) {
                    new Thread(new GetStation(config, busLineInfo)).start();
                    timer = new Timer();
                } else {
                    if (config.getWaitTime() == 0) {
                        new Thread(new UpdateOnlineBuses(config, busLineInfo)).start();
                        timerRunning = false;
                    } else {
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(new UpdateOnlineBuses(config, busLineInfo), 200, config.getWaitTime() * 1000);
                        timerRunning = true;
                    }
                }
            }
        });
    }

    protected void onResume() {
        super.onResume();
        if (config.getWaitTime() != 0) {
            if (!timerRunning) {
                timer = new Timer();
                timer.schedule(new UpdateOnlineBuses(config, busLineInfo), 200, config.getWaitTime() * 1000);
                timerRunning = true;
            }
        }
    }

    protected void onPause() {
        super.onPause();
            if (timerRunning) {
                if (!firstRun) {
                    timer.cancel();
                    timerRunning = false;
                }
            }
    }

    protected void onStop() {
        super.onStop();
        if (timerRunning) {
            timer.cancel();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeSnackbar(String content) {
        Snackbar.make(findViewById(R.id.toolbar), content, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void makeAlert(String title, String content) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(getString(R.string.okay),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
    }

    public final class ViewHolder {
        ImageView statusImg;
        TextView stationName;
        TextView onlineBuses;
    }

    class GetStation implements Runnable {

        GetConfig config;
        BusLineInfo busLineInfo;

        GetStation(GetConfig config, BusLineInfo busLineInfo) {
            this.config = config;
            this.busLineInfo = busLineInfo;
        }

        @Override
        public void run() {
            try {
                if (config.getEnableStaticIP()) {
                    stationInfos = new GetBusInfo().getStationInfo(
                            this.config.getBusApiUrl() ,
                            this.busLineInfo.getID(),
                            config.getStataicIP()
                    );
                } else {
                    stationInfos = new GetBusInfo().getStationInfo(
                            this.config.getBusApiUrl() ,
                            this.busLineInfo.getID()
                    );
                }
            } catch (HttpCodeInvalidException |
                    StringIndexOutOfBoundsException |
                    JsonSyntaxException |
                    IllegalArgumentException e) {
                mHandler.obtainMessage(-1001).sendToTarget();
                return;
            } catch (UnknownHostException | SocketTimeoutException | ConnectException e) {
                mHandler.obtainMessage(-1003).sendToTarget();
                return;
            } catch (IOException e) {
                if (e.toString().contains("okhttp3.Address@")) {
                    mHandler.obtainMessage(-1003).sendToTarget();
                } else {
                    mHandler.obtainMessage(-1, e.toString()).sendToTarget();
                }
                return;
            } catch (Exception e) {
                mHandler.obtainMessage(-1, e.toString()).sendToTarget();
                return;
            }
            if (stationInfos == null) {
                mHandler.obtainMessage(-1003).sendToTarget();
                return;
            }
            mHandler.obtainMessage(0).sendToTarget();
        }
    }

    class UpdateOnlineBuses extends TimerTask {

        BusLineInfo busLineInfo;
        GetConfig config;

        UpdateOnlineBuses(GetConfig config, BusLineInfo busLineInfo) {
            this.config = config;
            this.busLineInfo = busLineInfo;
        }

        @Override
        public void run() {
            OnlineBusInfo[] onlineBusInfos;
            try {
                if (config.getEnableStaticIP()) {
                    onlineBusInfos = new GetBusInfo().getOnlineBusInfo(
                            this.config.getBusApiUrl() ,
                            this.busLineInfo.getName() ,
                            this.busLineInfo.getFromStation(),
                            config.getStataicIP()
                    );
                } else {
                    onlineBusInfos = new GetBusInfo().getOnlineBusInfo(
                            this.config.getBusApiUrl() ,
                            this.busLineInfo.getName() ,
                            this.busLineInfo.getFromStation()
                    );
                }
            } catch (HttpCodeInvalidException |
                    StringIndexOutOfBoundsException |
                    JsonSyntaxException |
                    IllegalArgumentException e) {
                mHandler.obtainMessage(-2001).sendToTarget();
                return;
            } catch (UnknownHostException | SocketTimeoutException | ConnectException e) {
                mHandler.obtainMessage(-2003).sendToTarget();
                return;
            } catch (IOException e) {
                if (e.toString().contains("okhttp3.Address@")) {
                    mHandler.obtainMessage(-2003).sendToTarget();
                } else {
                    mHandler.obtainMessage(-2, e.toString()).sendToTarget();
                }
                return;
            }
            if (onlineBusInfos == null) {
                onlineBusInfos = new OnlineBusInfo[0];
            }
            if (firstRun) {
                firstRun = false;
                mHandler.obtainMessage(1, onlineBusInfos).sendToTarget();
            } else {
                mHandler.obtainMessage(2, onlineBusInfos).sendToTarget();
            }
        }
    }

    public class MAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        MAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return stationInfos.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;

            final StationInfo stationInfo = stationInfos[position];
            if (convertView == null) {
                viewHolder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.display_online_bus, null);
                viewHolder.statusImg = (ImageView) convertView.findViewById(R.id.onlineBusImg);
                viewHolder.stationName = (TextView) convertView.findViewById(R.id.stationName);
                viewHolder.onlineBuses = (TextView) convertView.findViewById(R.id.onlineBuses);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            GetDisplayImg getDisplayImg = new GetDisplayImg();

            String onlineBus = "";
            int olBusCount = 0;

            if (onlineBusInfos.length > 0) {
                for (OnlineBusInfo data : onlineBusInfos) {
                    if (data.getCurrentStation().equals(stationInfo.getName())) {
                        onlineBus = onlineBus + data.getBusNumber() + "; ";
                        olBusCount++;
                    }
                }
            } else {
                if (config.getTitleIsBus()) {
                    viewHolder.stationName.setText("");
                } else  {
                    viewHolder.onlineBuses.setText("");
                }
                if (!getDisplayImg.getIsNone()) {
                    viewHolder.statusImg.setImageDrawable(getDisplayImg.getNoBusDrawable());
                }
            }

            if (config.getTitleIsBus()) {
                if (olBusCount != 0) {
                    viewHolder.stationName.setText(onlineBus.substring(0, onlineBus.length() - 2));
                } else {
                    viewHolder.stationName.setText("");
                }
                viewHolder.onlineBuses.setText(stationInfo.getName() + "，共有 " + olBusCount + " 辆车");
            } else  {
                viewHolder.stationName.setText(stationInfo.getName());
                if (olBusCount != 0) {
                    viewHolder.onlineBuses.setText(onlineBus.substring(0, onlineBus.length() - 2) + "，共有 " + olBusCount + " 辆车");
                } else {
                    viewHolder.onlineBuses.setText("该站无车");
                }
            }
            if (!getDisplayImg.getIsNone()) {
                if (!onlineBus.equals("")) {
                    viewHolder.statusImg.setImageDrawable(getDisplayImg.getHaveBusDrawable());
                } else {
                    viewHolder.statusImg.setImageDrawable(getDisplayImg.getNoBusDrawable());
                }
            } else {
                viewHolder.statusImg.getLayoutParams().width = 0;
            }
            return convertView;
        }
    }

    class GetDisplayImg {
        private Drawable haveBusDrawable = null;
        private Drawable noBusDrawable = null;
        private Boolean isNone = false;

        GetDisplayImg() {
            switch (config.getHintLogo()) {
                case "apple_moon_emoji":
                    this.noBusDrawable = getImg(R.drawable.hint_icon_apple_black_moon_emoji);
                    this.haveBusDrawable = getImg(R.drawable.hint_icon_apple_yellow_moon_emoji);
                    return;
                case "google_moon_emoji":
                    this.noBusDrawable = getImg(R.drawable.hint_icon_google_black_moon_emoji);
                    this.haveBusDrawable = getImg(R.drawable.hint_icon_google_yellow_moon_emoji);
                    return;
                case "md_circle":
                    this.noBusDrawable = getImg(R.drawable.hint_icon_md_circle_2);
                    this.haveBusDrawable = getImg(R.drawable.hint_icon_md_circle_1);
                    return;
                case "md_bus":
                    this.noBusDrawable = null;
                    this.haveBusDrawable = getImg(R.drawable.hint_icon_md_bus);
                    return;
                case "classic_bus":
                    this.noBusDrawable = null;
                    this.haveBusDrawable = getImg(R.drawable.hint_icon_classic_bus);
                    return;
                case "none":
                    this.noBusDrawable = null;
                    this.haveBusDrawable = null;
                    this.isNone = true;
                    return;
                default:
                    this.noBusDrawable = getImg(R.drawable.hint_icon_apple_black_moon_emoji);
                    this.haveBusDrawable = getImg(R.drawable.hint_icon_apple_yellow_moon_emoji);
            }
            this.isNone = haveBusDrawable == null && noBusDrawable == null;
        }

        private Drawable getImg(int imgId) {
            return getResources().getDrawable(imgId);
        }

        Boolean getIsNone() {
            return this.isNone;
        }

        Drawable getHaveBusDrawable() {
            return this.haveBusDrawable;
        }

        Drawable getNoBusDrawable() {
            return this.noBusDrawable;
        }
    }
}
