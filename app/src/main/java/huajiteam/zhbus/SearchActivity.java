package huajiteam.zhbus;

import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;

import huajiteam.zhbus.zhdata.BusLineInfo;
import huajiteam.zhbus.zhdata.GetBusInfo;
import huajiteam.zhbus.zhdata.exceptions.BusLineInvalidException;
import huajiteam.zhbus.zhdata.exceptions.HttpCodeInvalidException;

public class SearchActivity extends AppCompatActivity {

    GetConfig config;
    String busLineNumber = "";

    private ProgressDialog progressDialog;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Log.i("qwq","pwp");
            switch (msg.what) {
                case 0:
                    Intent intent = new Intent();
                    intent.setClass(SearchActivity.this, SearchResultActivity.class);
                    ArrayList<BusLineInfo> arrayList = new ArrayList<BusLineInfo>();
                    for (BusLineInfo i : (BusLineInfo[]) msg.obj) {
                        arrayList.add(i);
                    }
                    intent.putExtra("busLineInfos", arrayList);
                    intent.putExtra("config", config);
                    startActivity(intent);
                    progressDialog.cancel();
                    break;
                case -1:
                    progressDialog.cancel();
                    makeAlert(getString(R.string.error), getString(R.string.unknown_error) + msg.obj);
                    break;
                case -1001:
                    progressDialog.cancel();
                    makeAlert(getString(R.string.error), getString(R.string.error_api_invalid));
                    break;
                case -1002:
                    progressDialog.cancel();
                    makeAlert(getString(R.string.error), getString(R.string.main_error_bus_line_invalid));
                    break;
                case -1003:
                    progressDialog.cancel();
                    makeAlert(getString(R.string.error), getString(R.string.network_error));
                    break;
                default:
                    progressDialog.cancel();
                    makeAlert(getString(R.string.error), getString(R.string.yi));
                    break;
            }
        }
    };

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
        setContentView(R.layout.activity_search);
        ActionBar actionBar = getDelegate().getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        this.config = new GetConfig(getApplicationContext());

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.numberOne:
                        busLineNumber = busLineNumber + "1";
                        updateDisplay();
                        break;
                    case R.id.numberTwo:
                        busLineNumber = busLineNumber + "2";
                        updateDisplay();
                        break;
                    case R.id.numberThree:
                        busLineNumber = busLineNumber + "3";
                        updateDisplay();
                        break;
                    case R.id.numberFour:
                        busLineNumber = busLineNumber + "4";
                        updateDisplay();
                        break;
                    case R.id.numberFive:
                        busLineNumber = busLineNumber + "5";
                        updateDisplay();
                        break;
                    case R.id.numberSix:
                        busLineNumber = busLineNumber + "6";
                        updateDisplay();
                        break;
                    case R.id.numberSeven:
                        busLineNumber = busLineNumber + "7";
                        updateDisplay();
                        break;
                    case R.id.numberEight:
                        busLineNumber = busLineNumber + "8";
                        updateDisplay();
                        break;
                    case R.id.numberNine:
                        busLineNumber = busLineNumber + "9";
                        updateDisplay();
                        break;
                    case R.id.numberZero:
                        busLineNumber = busLineNumber + "0";
                        updateDisplay();
                        break;
                    case R.id.numberStar:
                        busLineNumber = busLineNumber + "*";
                        updateDisplay();
                        break;
                    case R.id.numberSharp:
                        busLineNumber = busLineNumber + "#";
                        updateDisplay();
                        if (busLineNumber.equals("*#06#")) {
                            new SearchBus("", config.getSearchBusLineUrl()).start();
                            progressDialog = ProgressDialog.show(SearchActivity.this, getString(R.string.check_title), getString(R.string.loading));
                        }
                        break;
                    case R.id.numberA:
                        busLineNumber = busLineNumber + "A";
                        updateDisplay();
                        break;
                    case R.id.numberC:
                        busLineNumber = busLineNumber + "C";
                        updateDisplay();
                        break;
                    case R.id.numberF:
                        busLineNumber = busLineNumber + "F";
                        updateDisplay();
                        return;
                    case R.id.numberK:
                        busLineNumber = busLineNumber + "K";
                        updateDisplay();
                        break;
                    case R.id.numberN:
                        busLineNumber = busLineNumber + "N";
                        updateDisplay();
                        break;
                    case R.id.numberZ:
                        busLineNumber = busLineNumber + "Z";
                        updateDisplay();
                        break;
                    case R.id.clearAll:
                        busLineNumber = "";
                        updateDisplay();
                        break;
                    case R.id.backSpace:
                        if (!busLineNumber.equals("")) {
                            busLineNumber = busLineNumber.substring(0, busLineNumber.length() - 1);
                            updateDisplay();
                        }
                        break;
                    case R.id.searchButton:
                        if (busLineNumber.equals("")) {
                            makeAlert("WARNING", getString(R.string.main_error_bus_line_null));
                        } else {
                            new SearchBus(busLineNumber, config.getSearchBusLineUrl()).start();
                            progressDialog = ProgressDialog.show(SearchActivity.this, getString(R.string.check_title), getString(R.string.loading));
                        }
                        break;
                }
            }
        };

        findViewById(R.id.numberOne).setOnClickListener(onClickListener);
        findViewById(R.id.numberTwo).setOnClickListener(onClickListener);
        findViewById(R.id.numberThree).setOnClickListener(onClickListener);
        findViewById(R.id.numberFour).setOnClickListener(onClickListener);
        findViewById(R.id.numberFive).setOnClickListener(onClickListener);
        findViewById(R.id.numberSix).setOnClickListener(onClickListener);
        findViewById(R.id.numberSeven).setOnClickListener(onClickListener);
        findViewById(R.id.numberEight).setOnClickListener(onClickListener);
        findViewById(R.id.numberNine).setOnClickListener(onClickListener);
        findViewById(R.id.numberZero).setOnClickListener(onClickListener);
        findViewById(R.id.numberStar).setOnClickListener(onClickListener);
        findViewById(R.id.numberSharp).setOnClickListener(onClickListener);
        findViewById(R.id.numberA).setOnClickListener(onClickListener);
        findViewById(R.id.numberC).setOnClickListener(onClickListener);
        findViewById(R.id.numberF).setOnClickListener(onClickListener);
        findViewById(R.id.numberK).setOnClickListener(onClickListener);
        findViewById(R.id.numberN).setOnClickListener(onClickListener);
        findViewById(R.id.numberZ).setOnClickListener(onClickListener);
        findViewById(R.id.clearAll).setOnClickListener(onClickListener);
        findViewById(R.id.backSpace).setOnClickListener(onClickListener);
        findViewById(R.id.searchButton).setOnClickListener(onClickListener);
    }

    private void updateDisplay() {
        TextView displayBusNumber = (TextView) findViewById(R.id.lineNumber);
        displayBusNumber.setText(this.busLineNumber);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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
}
