package huajiteam.zhuhaibus;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

import huajiteam.zhuhaibus.service.ListenLinesManager;

public class LineListenerActivity extends AppCompatActivity {

    ArrayList<String[]> busdata;
    MAdapter mAdapter;
    ListenLinesManager listenLinesManager = new ListenLinesManager();
    ToggleButton toggleButton;
    TextView noneText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_listener);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getDelegate().getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        this.busdata = listenLinesManager.getArrayListeningList();
        this.toggleButton = (ToggleButton) findViewById(R.id.listenServiceButton);
        this.noneText = (TextView) findViewById(R.id.noneText);
        ListView listView = (ListView) findViewById(R.id.listeningLines);
        mAdapter = new MAdapter(this);
        listView.setAdapter(mAdapter);

        if (!busdata.isEmpty()) {
            noneText.setText("");
        }

        toggleButton.setChecked(listenLinesManager.isServiceRunning(getApplicationContext()));
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Log.i("BusListenerService", "True");
                    if (listenLinesManager.getListeningBusIsEmpty()) {
                        Snackbar.make(
                                toggleButton,
                                "无法开启服务，因为监听列表为空。请尝试添加要监听的车辆后再开启服务。",
                                Snackbar.LENGTH_LONG
                        ).setAction("Action", null).show();
                        toggleButton.setChecked(false);
                    } else {
                        if (!listenLinesManager.isServiceRunning(getApplicationContext())) {
                            listenLinesManager.startService(LineListenerActivity.this);
                        }
                    }
                } else {
                    Log.i("BusListenerService", "False");
                    try {
                        stopListener();
                    } catch (NullPointerException e) {
                        Snackbar.make(
                                toggleButton,
                                "服务未运行",
                                Snackbar.LENGTH_LONG
                        ).setAction("Action", null).show();
                        toggleButton.setChecked(false);
                    }
                }
            }
        });

        final Button clearButton = (Button) findViewById(R.id.clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleButton.setChecked(false);
                try {
                    clearAndStop();
                    Snackbar.make(clearButton, "成功清理并停止了服务。", Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .show();
                } catch (NullPointerException e) {
                    Snackbar.make(
                            clearButton,
                            "服务未运行",
                            Snackbar.LENGTH_LONG
                    ).setAction("Action", null).show();
                    toggleButton.setChecked(false);
                }
                busdata = listenLinesManager.getArrayListeningList();
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void clearAndStop() {
        listenLinesManager.clearListeningBus();
        this.stopListener();
    }

    private void stopListener() {
        listenLinesManager.stopListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    class ViewHolder {
        TextView title;
        TextView to;
        Button deleteButton;
    }

    public class MAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        MAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return busdata.size();
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
        public void notifyDataSetChanged() {
            if (busdata.isEmpty()) {
                toggleButton.setChecked(false);
                noneText.setText("您还没有硬点监听的线路");
            }
            super.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;

            final String[] now = busdata.get(position);

            if (convertView == null) {
                viewHolder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.listening_stations, null);
                viewHolder.title = (TextView) convertView.findViewById(R.id.title);
                viewHolder.to = (TextView) convertView.findViewById(R.id.to);
                viewHolder.deleteButton = (Button) convertView.findViewById(R.id.deleteButton);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.title.setText(now[0] + " " + now[2]);
            viewHolder.to.setText("从 " + now[1] + " 出发");
            viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listenLinesManager.removeListenStation(now[0], now[1], now[2]);
                    busdata = listenLinesManager.getArrayListeningList();
                    mAdapter.notifyDataSetChanged();
                    Snackbar.make(findViewById(R.id.toolbar), "成功地取消了监听。", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            });
            return convertView;
        }
    }
}
