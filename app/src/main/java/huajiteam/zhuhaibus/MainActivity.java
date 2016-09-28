package huajiteam.zhuhaibus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
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
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import huajiteam.zhuhaibus.zhdata.data.BusLineInfo;
import huajiteam.zhuhaibus.zhdata.GetBusInfo;
import huajiteam.zhuhaibus.zhdata.exceptions.BusLineInvalidException;
import huajiteam.zhuhaibus.zhdata.exceptions.HttpCodeInvalidException;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    GetConfig config;
    MAdapter mAdapter;
    FavoriteConfig favoriteConfig;
    private ProgressDialog progressDialog;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, SearchResultActivity.class);
                    ArrayList<BusLineInfo> arrayList = new ArrayList<BusLineInfo>();
                    Collections.addAll(arrayList, (BusLineInfo[]) msg.obj);
                    intent.putExtra("busLineInfos", arrayList);
                    startActivity(intent);
                    progressDialog.dismiss();
                    break;
                case 2000:
                    makeAlert(getString(R.string.latest_title), getString(R.string.current_latest));
                    progressDialog.dismiss();
                    break;
                case 2:
                    final Map<String, String> map = (Map<String, String>) msg.obj;
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(getString(R.string.new_update));
                    builder.setMessage(getString(R.string.current_ver) + " " + map.get("now") + "\n" +
                            getString(R.string.latest_ver) + " "  + map.get("new") + "\n\n" +
                            getString(R.string.update_change) + "\n" + map.get("note") + "\n\n" +
                            getString(R.string.update_now));
                    builder.setPositiveButton(getString(R.string.update_broswer), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(map.get("uri"))));
                        }
                    });
                    builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();
                    progressDialog.dismiss();
                    break;
                case -2:
                    makeAlert(getString(R.string.error), getString(R.string.unknown_error) + msg.obj);
                    progressDialog.dismiss();
                    break;
                case -2001:
                    makeSnackbar(getString(R.string.cannot_get_current_ver));
                    progressDialog.dismiss();
                    break;
                case -2002:
                    makeSnackbar(getString(R.string.update_server_error));
                    progressDialog.dismiss();
                    break;
                case -2003:
                    makeSnackbar(getString(R.string.network_error));
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
        favoriteConfig.reloadData();
        config.reloadData();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        config = new GetConfig(this);
        if (config.getIsFirstRun()) {
            makeAlert(getString(R.string.open_source_license), new OpenSourceLicense().getLicense());
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("first_run", false).apply();
        }

        try {
            final String recMsg = bundle.getString("msg");
            final String link = bundle.getString("link");
            if (recMsg != null && !recMsg.equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("BROADCAST MESSAGE");
                builder.setMessage(recMsg);
                String cancelButtonString;
                if (link != null && !link.equals("")) {
                    builder.setPositiveButton(getString(R.string.update_broswer), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                        }
                    });
                    cancelButtonString = getString(R.string.cancel);
                } else {
                    cancelButtonString = getString(R.string.okay);
                }
                builder.setNegativeButton(cancelButtonString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create().show();
            }
        } catch (NullPointerException ignored) {}

        final EditText editText = (EditText) findViewById(R.id.busLineInputBox);
        if (editText != null) {
            editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    Log.i("Press", "" + actionId + " " + EditorInfo.IME_ACTION_SEARCH);

                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        String busLineText = editText.getText().toString();
                        if (busLineText.equals("")) {
                            Snackbar.make(editText, getString(R.string.main_error_bus_line_null), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        } else {
                            progressDialog = ProgressDialog.show(MainActivity.this, getString(R.string.connect_server_message), getString(R.string.waiting));
                            new SearchBus(busLineText.replace("fatfatsb", ""), config.getBusApiUrl()).start();
                        }
                        return true;
                    }
                    return false;
                }
            });
        }

        Button bt = (Button) findViewById(R.id.searchButton);

        if (bt != null) {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String busLineText = null;
                    if (editText != null) {
                        busLineText = editText.getText().toString();
                        if (busLineText.equals("")) {
                            Snackbar.make(editText, getString(R.string.main_error_bus_line_null), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        } else {
                            progressDialog = ProgressDialog.show(MainActivity.this, getString(R.string.connect_server_message), getString(R.string.waiting));
                            new SearchBus(busLineText.replace("fatfatsb", ""), config.getBusApiUrl()).start();
                        }
                    }
                }
            });

            favoriteConfig = new FavoriteConfig(this);
            /*if (favoriteConfig.getBusLineInfoArray().size() == 0) {
            }*/
            ListView listView = (ListView) findViewById(R.id.favOnMain);
            mAdapter = new MAdapter(this);
            if (listView != null) {
                listView.setAdapter(mAdapter);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.setDrawerListener(toggle);
        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
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
            Intent favoriteIntent = new Intent(this, SearchActivity.class);
            startActivity(favoriteIntent);
        } else if (id == R.id.nav_favorite) {
            Intent favoriteIntent = new Intent(this, FavoriteActivity.class);
            startActivity(favoriteIntent);
        } else if (id == R.id.nav_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        } else if (id == R.id.nav_ckeck_updates) {
            this.progressDialog = ProgressDialog.show(this, getString(R.string.check_title), getString(R.string.checking));
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
                    String updateUrl;
                    if (nowVer.contains("beta")) {
                        updateUrl = "https://lab.yhtng.com/ZhuhaiBus/updates/beta.json";
                    } else {
                        updateUrl = "https://lab.yhtng.com/ZhuhaiBus/updates/stable.json";
                    }
                    try {
                        response = new GetWebContent().httpGet(updateUrl);
                        latestVer = response.body().string();
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
                    if (updatesData.version.equals(nowVer)) {
                        mHandler.obtainMessage(2000).sendToTarget();
                    } else {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("uri", updatesData.downloadURL);
                        map.put("now", nowVer);
                        map.put("new", updatesData.version);
                        map.put("note", updatesData.note);
                        mHandler.obtainMessage(2, map).sendToTarget();
                    }
                }

                class UpdatesData {
                    String version;
                    String downloadURL;
                    String note;
                }
            }).start();
        } else if (id == R.id.nav_feedback) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getString(R.string.title_activity_feed_back));
            builder.setMessage(getString(R.string.open_the_link)+"\n"
                    +"https://github.com/HuaJiTeam/ZhBus \n"+
                    getString(R.string.to_issue));
            builder.setPositiveButton("Open", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/HuaJiTeam/ZhBus")));
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.create().show();
        } else if (id == R.id.nav_about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("About");
            builder.setMessage("HuaJiTeam: https://github.com/HuaJiTeam");
            builder.setPositiveButton(getString(R.string.open_source_license), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    makeAlert(getString(R.string.open_source_license), new OpenSourceLicense().getLicense());
                }
            });
            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.create().show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    class SearchBus extends Thread {

        String busLine;
        String apiUrl;

        SearchBus(String busLine, String apiUrl) {
            this.busLine = busLine;
            this.apiUrl = apiUrl;
        }

        @Override
        public void run() {
            BusLineInfo[] busLineInfos;
            try {
                if (config.getEnableStaticIP()) {
                    busLineInfos = new GetBusInfo().getBusLineInfo(apiUrl, this.busLine, config.getStataicIP());
                } else {
                    busLineInfos = new GetBusInfo().getBusLineInfo(apiUrl, this.busLine);
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
            mHandler.obtainMessage(0, busLineInfos).sendToTarget();
        }
    }

    public final class ViewHolder {
        TextView busName;
        TextView busSummary;
        ImageButton searchButton;
    }

    class MAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        MAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return favoriteConfig.getBusLineInfoArray().size();
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
                viewHolder.searchButton = (ImageButton) convertView.findViewById(R.id.searchOLButton);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (favoriteConfig.getBusLineInfoArray().size() == 0) {
                viewHolder.busName.setText(getString(R.string.favorite_bus_null));
                viewHolder.busSummary.setText(getString(R.string.favorite_bus_null));
                viewHolder.searchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        makeSnackbar(getString(R.string.favorite_bus_null));
                    }
                });
            } else {
                final BusLineInfo busLineInfo = favoriteConfig.getBusLineInfoArray().get(position);
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
                            startActivity(intent);
                        } else {
                            makeSnackbar(getString(R.string.yi));
                        }
                    }
                };

                viewHolder.searchButton.setOnClickListener(onClickListener);
            }
            return convertView;
        }
    }
}
