package huajiteam.zhuhaibus;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import huajiteam.zhuhaibus.zhdata.BusLineInfo;

public class FavoriteActivity extends AppCompatActivity {

    FavoriteConfig favoriteConfig;
    ListView listView;
    ArrayList<BusLineInfo> favBuses;
    MAdapter mAdapter;
    private GetConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getDelegate().getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        this.favoriteConfig = new FavoriteConfig(this);
        this.favBuses = favoriteConfig.getBusLineInfoArray();
        this.config = new GetConfig(getApplicationContext());

        listView = (ListView) findViewById(R.id.favoriteListView);
        mAdapter = new MAdapter(this);
        listView.setAdapter(mAdapter);

        // Add advertisement
        if (!this.config.getDoNotDisplayAds()) {
            AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favorite_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.action_manage_item:
                makeAlert("Oops...", "功能正在开发……");
                break;
            case R.id.clear_all:
                AlertDialog.Builder builder = new AlertDialog.Builder(FavoriteActivity.this);
                builder.setTitle("确定吗？");
                builder.setMessage("您的所有收藏将被删除，并且此操作不可恢复。");
                builder.setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FavoriteActivity.this.favoriteConfig.clearAllData();
                        FavoriteActivity.this.makeSnackbar("清除成功");
                        favBuses.clear();
                        favBuses = favoriteConfig.getBusLineInfoArray();
                        mAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
                break;
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
                            intent.setClass(FavoriteActivity.this, OnlineBusActivity.class);
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
