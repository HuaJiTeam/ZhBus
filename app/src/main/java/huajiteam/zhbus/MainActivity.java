package huajiteam.zhbus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Streams;

import java.io.IOException;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import huajiteam.zhbus.zhdata.BusLineInfo;
import huajiteam.zhbus.zhdata.GetBusInfo;
import huajiteam.zhbus.zhdata.exceptions.BusLineInvalidException;
import huajiteam.zhbus.zhdata.exceptions.HttpCodeInvalidException;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    GetConfig config;
    MAdapter mAdapter;
    ArrayList<BusLineInfo> favBuses;
    FavoriteConfig favoriteConfig;
    private ProgressDialog progressDialog;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, SearchResultActivity.class);
                    ArrayList<BusLineInfo> arrayList = new ArrayList<BusLineInfo>();
                    for (BusLineInfo i : (BusLineInfo[]) msg.obj) {
                        arrayList.add(i);
                    }
                    intent.putExtra("busLineInfos", arrayList);
                    intent.putExtra("config", config);
                    startActivity(intent);
                    break;
                case 2000:
                    makeAlert("没有更新", "没有更新");
                    progressDialog.dismiss();
                    break;
                case 2:
                    final Map<String, String> map = (Map<String, String>) msg.obj;
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("发现了新的更新");
                    builder.setMessage("当前版本：" + map.get("now") + "\n" +
                            "最新版本：" + map.get("new") + "\n\n" +
                            "更新说明：\n" + map.get("note") + "\n\n" +
                            "是否立即更新？");
                    builder.setPositiveButton("使用浏览器下载", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(map.get("uri"))));
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();
                    progressDialog.dismiss();
                    break;
                case -2:
                    makeAlert("出现了一个错误", "未知错误: " + msg.obj);
                    progressDialog.dismiss();
                    break;
                case -2001:
                    makeSnackbar("无法获取本地版本号");
                    progressDialog.dismiss();
                    break;
                case -2002:
                    makeSnackbar("更新服务器异常");
                    progressDialog.dismiss();
                    break;
                case -2003:
                    makeSnackbar(getString(R.string.network_error));
                    progressDialog.dismiss();
                    break;
                case -1:
                    makeAlert("出现了一个错误", "未知错误: " + msg.obj);
                    break;
                case -1001:
                    makeSnackbar(getString(R.string.error_api_invalid));
                    break;
                case -1002:
                    makeSnackbar(getString(R.string.main_error_bus_line_invalid));
                    break;
                case -1003:
                    makeSnackbar(getString(R.string.network_error));
                    break;
                default:
                    makeSnackbar("噫");
                    break;
            }
        }
    };

    private void makeSnackbar(String content) {
        Snackbar.make(findViewById(R.id.toolbar), content, Snackbar.LENGTH_LONG).setAction("Action", null).show();
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

    protected void onRestart() {
        super.onRestart();
        favBuses.clear();
        favBuses = favoriteConfig.getBusLineInfoArray();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button bt = (Button) findViewById(R.id.searchButton);
        if (bt != null) {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText editText = (EditText) findViewById(R.id.busLineInputBox);
                    String busLineText = editText.getText().toString();
                    if (busLineText.equals("")) {
                        Snackbar.make(editText, getString(R.string.main_error_bus_line_null), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else {
                        makeSnackbar(getString(R.string.connect_server_message));
                        config = new GetConfig(getApplicationContext());
                        new SearchBus(busLineText.replace("fatfatsb", ""), config.getSearchBusLineUrl()).start();
                    }
                }
            });

            favoriteConfig = new FavoriteConfig(this);
            favBuses = favoriteConfig.getBusLineInfoArray();
            ListView listView = (ListView) findViewById(R.id.favOnMain);
            mAdapter = new MAdapter(this);
            listView.setAdapter(mAdapter);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            makeSnackbar("Oops...功能暂时没有");
        } else if (id == R.id.nav_favorite) {
            Intent favoriteIntent = new Intent(this, FavoriteActivity.class);
            startActivity(favoriteIntent);
        } else if (id == R.id.nav_history) {
            makeSnackbar("Oops...功能暂时没有");
        } else if (id == R.id.nav_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        } else if (id == R.id.nav_ckeck_updates) {
            this.progressDialog = ProgressDialog.show(this, "请稍等", "正在为您检查更新...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String nowVer;
                    try {
                        nowVer = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                    } catch (PackageManager.NameNotFoundException e) {
                        mHandler.obtainMessage(-2001).sendToTarget();
                        return;
                    }
                    Response response;
                    String latestVer;
                    try {
                        response = new GetWebContent().httpGet("https://lab.yhtng.com/ZhuhaiBus/update.json");
                        latestVer = response.body().string();
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
                    if (response.code() != 200) {
                        mHandler.obtainMessage(-2002).sendToTarget();
                        return;
                    }
                    UpdatesData updatesData;
                    try {
                        updatesData = new Gson().fromJson(latestVer, UpdatesData.class);
                    } catch (StringIndexOutOfBoundsException | JsonSyntaxException | IllegalArgumentException e) {
                        mHandler.obtainMessage(-2002).sendToTarget();
                        return;
                    }
                    if (updatesData.stableVersion.equals(nowVer)) {
                        mHandler.obtainMessage(2000).sendToTarget();
                    } else {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("uri", updatesData.downloadURL);
                        map.put("now", nowVer);
                        map.put("new", updatesData.stableVersion);
                        map.put("note", updatesData.note);
                        mHandler.obtainMessage(2, map).sendToTarget();
                    }
                }

                class UpdatesData {
                    String stableVersion;
                    String downloadURL;
                    String note;
                }
            }).start();
        } else if (id == R.id.nav_feedback) {
            makeAlert("发送反馈", "如需反馈，请将您需要反馈的内容发送至 yhjserv@gmail.com");
        } else if (id == R.id.nav_about) {
            makeAlert("关于",
                    "UI/翻译: https://github.com/bitkwan\n" +
                    "主要开发者: https://github.com/kelakim");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class SearchBus extends Thread {

        String busLine;
        String apiUrl;

        public SearchBus(String busLine, String apiUrl) {
            this.busLine = busLine;
            this.apiUrl = apiUrl;
        }

        @Override
        public void run() {
            BusLineInfo[] busLineInfos;
            try {
                busLineInfos = new GetBusInfo().getBusLineInfo(apiUrl, this.busLine);
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
                if (e.toString().indexOf("okhttp3.Address@") != -1) {
                    mHandler.obtainMessage(-1003).sendToTarget();
                } else {
                    mHandler.obtainMessage(-1, e.toString()).sendToTarget();
                }
                return;
            }
            mHandler.obtainMessage(0, busLineInfos).sendToTarget();
        }
    }

    public final class ViewHolder {
        public TextView busName;
        public TextView busSummary;
        public Button searchButton;
    }

    class MAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public MAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if (favBuses.size() == 0){
                return 1;
            } else {
                return favBuses.size();
            }
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

            if (convertView == null) {
                viewHolder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.favorite_bus_results, null);
                viewHolder.busName = (TextView) convertView.findViewById(R.id.lineName);
                viewHolder.busSummary = (TextView) convertView.findViewById(R.id.summaryMessage);
                viewHolder.searchButton = (Button) convertView.findViewById(R.id.searchOLButton);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (favBuses.size() == 0) {
                viewHolder.busName.setText(getString(R.string.favorite_bus_null));
                viewHolder.busSummary.setText(getString(R.string.favorite_bus_null));
                viewHolder.searchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        makeSnackbar(getString(R.string.favorite_bus_null));
                    }
                });
            } else {
                final BusLineInfo busLineInfo = favBuses.get(position);
                viewHolder.busName.setText(busLineInfo.getName() + "，往" + busLineInfo.getToStation());
                viewHolder.busSummary.setText(getString(R.string.search_result_price) + busLineInfo.getPrice());

                final int searchButtonId = viewHolder.searchButton.getId();

                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == searchButtonId) {
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this, OnlineBusActivity.class);
                            intent.putExtra("busLineInfo", busLineInfo);
                            intent.putExtra("config", new GetConfig(getApplicationContext()));
                            startActivity(intent);
                        } else {
                            makeSnackbar("WTF!!??");
                        }
                    }
                };

                viewHolder.searchButton.setOnClickListener(onClickListener);
            }
            return convertView;
        }
    }
}
