package huajiteam.zhbus;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Streams;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import huajiteam.zhbus.zhdata.BusLineInfo;
import huajiteam.zhbus.zhdata.GetBusInfo;
import huajiteam.zhbus.zhdata.OnlineBusInfo;
import huajiteam.zhbus.zhdata.StationInfo;
import huajiteam.zhbus.zhdata.exceptions.BusLineInvalidException;
import huajiteam.zhbus.zhdata.exceptions.HttpCodeInvalidException;

public class OnlineBusActivity extends AppCompatActivity {

    BusLineInfo busLineInfo;
    GetConfig config;
    StationInfo[] stationInfos = null;
    OnlineBusInfo[] onlineBusInfos;
    Timer timer = new Timer();
    MAdapter mAdapter;

    boolean firstRun = true;
    boolean timerRunning = false;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (config.getWaitTime() == 0) {
                        new Thread(new UpdateOnlineBuses(config, busLineInfo)).start();
                        timerRunning = false;
                    } else {
                        timer.schedule(new UpdateOnlineBuses(config, busLineInfo), 200, config.getWaitTime() * 1000);
                        timerRunning = true;
                    }
                    break;
                case 1:
                    makeSnackbar("少女祈祷成功");
                    onlineBusInfos = (OnlineBusInfo[]) msg.obj;
                    mAdapter = new MAdapter(OnlineBusActivity.this);
                    ListView listView = (ListView) findViewById(R.id.onlineBusListView);
                    listView.setAdapter(mAdapter);
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
                    break;
                case -1001:
                    makeSnackbar(getString(R.string.error_api_invalid));
                    break;
                case -1003:
                    makeSnackbar(getString(R.string.network_error));
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

        Bundle bundle = getIntent().getExtras();
        this.busLineInfo = (BusLineInfo) bundle.get("busLineInfo");
        this.config = (GetConfig) bundle.get("config");

        makeSnackbar("少女祈祷中...");
        new Thread(new GetStation(config, busLineInfo)).start();

        FloatingActionButton reflushButton = (FloatingActionButton) findViewById(R.id.flushButton);
        reflushButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stationInfos == null) {
                    new Thread(new GetStation(config, busLineInfo)).start();
                    timer = new Timer();
                } else {
                    if (!config.getAutoFlushNotice()) {
                        makeSnackbar("少女祈祷中...");
                    }
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

    protected void onStop() {
        super.onStop();
        if (timerRunning) {
            timer.cancel();
        }
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
        public ImageView statusImg;
        public TextView stationName;
        public TextView onlineBuses;
        public Button moreInformation;
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
                stationInfos = new GetBusInfo().getStationInfo(
                        this.config.getSearchStationUrl() ,
                        this.busLineInfo.getID()
                );
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
                if (e.toString().indexOf("okhttp3.Address@") != -1) {
                    mHandler.obtainMessage(-1003).sendToTarget();
                } else {
                    mHandler.obtainMessage(-1, e.toString()).sendToTarget();
                }
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
                onlineBusInfos = new GetBusInfo().getOnlineBusInfo(
                        this.config.getSearchOnlineBusUrl() ,
                        this.busLineInfo.getName() ,
                        this.busLineInfo.getFromStation()
                );
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
                if (e.toString().indexOf("okhttp3.Address@") != -1) {
                    mHandler.obtainMessage(-2003).sendToTarget();
                } else {
                    mHandler.obtainMessage(-2, e.toString()).sendToTarget();
                }
                return;
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

        public MAdapter(Context context) {
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
                viewHolder.moreInformation = (Button) convertView.findViewById(R.id.moreInformationButton);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (config.getTitleIsBus()) {
                viewHolder.onlineBuses.setText(stationInfo.getName());
            } else {
                viewHolder.stationName.setText(stationInfo.getName());
            }

            GetDisplayImg getDisplayImg = new GetDisplayImg();

            if (onlineBusInfos.length == 0) {
                if (config.getTitleIsBus()) {
                    viewHolder.stationName.setText("");
                } else  {
                    viewHolder.onlineBuses.setText("");
                }
                viewHolder.statusImg.setImageDrawable(getDisplayImg.getNoBusDrawable());
            } else {
                for (OnlineBusInfo data : onlineBusInfos) {
                    if (data.getCurrentStation().equals(stationInfo.getName())) {
                        if (config.getTitleIsBus()) {
                            viewHolder.stationName.setText(data.getBusNumber());
                        } else  {
                            viewHolder.onlineBuses.setText(data.getBusNumber());
                        }
                        viewHolder.statusImg.setImageDrawable(getDisplayImg.getHaveBusDrawable());
                        break;
                    } else {
                        if (config.getTitleIsBus()) {
                            viewHolder.stationName.setText("");
                        } else  {
                            viewHolder.onlineBuses.setText("");
                        }
                        viewHolder.statusImg.setImageDrawable(getDisplayImg.getNoBusDrawable());
                    }
                }
            }

            final int moreId = viewHolder.moreInformation.getId();

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == moreId) {
                        makeAlert("详细信息",
                                "ID: " + stationInfo.getId() + "\n" +
                                "站名: " + stationInfo.getName()+ "\n" +
                                "经度: " + stationInfo.getLongitude() + "\n" +
                                "纬度: " + stationInfo.getLatitude() + "\n" +
                                "描述: " + stationInfo.getDescription()
                        );
                    } else {
                        makeSnackbar("Excuse me?");
                    }
                }
            };

            viewHolder.moreInformation.setOnClickListener(onClickListener);
            return convertView;
        }
    }

    class GetDisplayImg {
        private Drawable haveBusDrawable = null;
        private Drawable noBusDrawable = null;

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
                    return;
                default:
                    this.noBusDrawable = getImg(R.drawable.hint_icon_apple_black_moon_emoji);
                    this.haveBusDrawable = getImg(R.drawable.hint_icon_apple_yellow_moon_emoji);
                    return;
            }
        }

        private Drawable getImg(int imgId) {
            return getResources().getDrawable(imgId);
        }

        Drawable getHaveBusDrawable() {
            return this.haveBusDrawable;
        }

        Drawable getNoBusDrawable() {
            return this.noBusDrawable;
        }
    }
}
