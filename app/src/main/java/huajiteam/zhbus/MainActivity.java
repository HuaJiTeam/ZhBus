package huajiteam.zhbus;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import huajiteam.zhbus.zhdata.BusLineInfo;
import huajiteam.zhbus.zhdata.GetBusInfo;
import huajiteam.zhbus.zhdata.exceptions.BusLineInvalidException;
import huajiteam.zhbus.zhdata.exceptions.HttpCodeInvalidException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    GetConfig config;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, SearchResultActivity.class);
                    intent.putExtra("busLineInfos", (BusLineInfo[])msg.obj);
                    intent.putExtra("config", config);
                    startActivity(intent);
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
                        new SearchBus(busLineText.replace("fatfatsb", ""), config.searchBusLineUrl).start();
                    }
                }
            });
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
            makeSnackbar("Oops...功能暂时没有");
        } else if (id == R.id.nav_history) {
            makeSnackbar("Oops...功能暂时没有");
        } else if (id == R.id.nav_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        } else if (id == R.id.nav_ckeck_updates) {
            makeAlert("检查更新", "没有更新。");
        } else if (id == R.id.nav_feedback) {
            makeAlert("发送反馈", "#(滑稽)\n\n开发中");
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
                mHandler.obtainMessage(-1, e.toString()).sendToTarget();
                return;
            }
            mHandler.obtainMessage(0, busLineInfos).sendToTarget();
        }
    }
}
