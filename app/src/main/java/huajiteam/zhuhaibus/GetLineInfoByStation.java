package huajiteam.zhuhaibus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;

import huajiteam.zhuhaibus.zhdata.GetBusInfo;
import huajiteam.zhuhaibus.zhdata.data.BusLineInfo;
import huajiteam.zhuhaibus.zhdata.data.OnlineBusInfo;
import huajiteam.zhuhaibus.zhdata.exceptions.BusLineInvalidException;
import huajiteam.zhuhaibus.zhdata.exceptions.HttpCodeInvalidException;

public class GetLineInfoByStation extends AppCompatActivity {

    private ArrayList<BusLineInfo> busLineInfos;
    private GetConfig config;
    private ProgressDialog progressDialog;
    ListView listView;
    MAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_line_info_by_station);
        config = new GetConfig(this);
        listView = (ListView) findViewById(R.id.getLineInfoListView);

        Bundle bundle = getIntent().getExtras();
        String stationName = bundle.getString("stationName", "");
        if (stationName.equals("")) {
            this.busLineInfos = (ArrayList<BusLineInfo>) bundle.get("busLineInfos");
        } else {
            progressDialog = ProgressDialog.show(GetLineInfoByStation.this, getString(R.string.connect_server_message), getString(R.string.waiting));
            new Thread(new SearchBus(stationName, config.getBusApiUrl())).start();
        }

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

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    busLineInfos =  new ArrayList<BusLineInfo>();
                    Collections.addAll(busLineInfos, (BusLineInfo[]) msg.obj);
                    mAdapter = new MAdapter(GetLineInfoByStation.this);
                    listView.setAdapter(mAdapter);
                    progressDialog.dismiss();
                    break;
                case -1:
                    makeAlert(getString(R.string.error), getString(R.string.unknown_error) + msg.obj);
                    progressDialog.dismiss();
                    break;
                case -1001:
                    makeSnackbar(getString(R.string.error_api_invalid));
                    progressDialog.dismiss();
                    break;
                case -1002:
                    makeSnackbar(getString(R.string.main_error_bus_line_invalid));
                    progressDialog.dismiss();
                    break;
                case -1003:
                    makeSnackbar(getString(R.string.network_error));
                    progressDialog.dismiss();
                    break;
                default:
                    makeSnackbar(getString(R.string.yi));
                    progressDialog.dismiss();
                    break;
            }
        }
    };

    public final class ViewHolder {
        TextView busName;
        TextView busSummary;
        ImageButton addToFav;
        ImageButton searchButton;
    }

    private void makeSnackbar(String content) {
        Snackbar.make(findViewById(R.id.activity_get_line_info_by_station), content, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void makeAlert(String title, String content) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    class SearchBus implements Runnable {

        String stationName;
        String apiUrl;

        SearchBus(String stationName, String apiUrl) {
            this.stationName = stationName;
            this.apiUrl = apiUrl;
        }

        @Override
        public void run() {
            BusLineInfo[] busLineInfos;
            try {
                if (config.getEnableStaticIP()) {
                    busLineInfos = new GetBusInfo().getBusLineInfoByStationName(this.apiUrl, this.stationName, config.getStataicIP());
                } else {
                    busLineInfos = new GetBusInfo().getBusLineInfoByStationName(this.apiUrl, this.stationName);
                }
            } catch (HttpCodeInvalidException | StringIndexOutOfBoundsException | JsonSyntaxException | IllegalArgumentException e) {
                mHandler.obtainMessage(-1001).sendToTarget();
                return;
            } catch (BusLineInvalidException e) {
                mHandler.obtainMessage(-1002).sendToTarget();
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
            if (busLineInfos.length == 0) {
                mHandler.obtainMessage(-1002).sendToTarget();
                return;
            }
            mHandler.obtainMessage(0, busLineInfos).sendToTarget();
        }
    }

    public class MAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        MAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return busLineInfos.size();
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

            final BusLineInfo busLineInfo = busLineInfos.get(position);
            if (convertView == null) {
                viewHolder = new GetLineInfoByStation.ViewHolder();

                convertView = mInflater.inflate(R.layout.search_bus_results, null);
                viewHolder.busName = (TextView) convertView.findViewById(R.id.lineName);
                viewHolder.busSummary = (TextView) convertView.findViewById(R.id.summaryMessage);
                viewHolder.addToFav = (ImageButton) convertView.findViewById(R.id.addFavButton);
                viewHolder.searchButton = (ImageButton) convertView.findViewById(R.id.searchOLButton);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.busName.setText(busLineInfo.getName() + "，往" + busLineInfo.getToStation());
            viewHolder.busSummary.setText(getString(R.string.search_result_price) + busLineInfo.getPrice());

            final int addToFavId = viewHolder.addToFav.getId();
            final int searchButtonId = viewHolder.searchButton.getId();

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == addToFavId) {
                        FavoriteConfig favoriteConfig = new FavoriteConfig(GetLineInfoByStation.this);
                        favoriteConfig.addData(busLineInfo);
                        makeSnackbar(getString(R.string.success));
                    } else if (v.getId() == searchButtonId) {
                        Intent intent = new Intent();
                        intent.setClass(GetLineInfoByStation.this, OnlineBusActivity.class);
                        intent.putExtra("busLineInfo", busLineInfo);
                        intent.putExtra("config", config);
                        startActivity(intent);
                    } else {
                        makeSnackbar(getString(R.string.yi));
                    }
                }
            };

            viewHolder.addToFav.setOnClickListener(onClickListener);
            viewHolder.searchButton.setOnClickListener(onClickListener);
            return convertView;
        }
    }
}
