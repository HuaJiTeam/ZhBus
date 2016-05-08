package huajiteam.zhbus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import huajiteam.zhbus.zhdata.BusLineInfo;
import huajiteam.zhbus.zhdata.exceptions.BusLineInvalidException;

public class SearchResultActivity extends AppCompatActivity {

    private ArrayList<BusLineInfo> busLineInfos;
    private GetConfig config;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Bundle bundle = getIntent().getExtras();
        this.busLineInfos = (ArrayList<BusLineInfo>) bundle.get("busLineInfos");
        this.config = (GetConfig) bundle.get("config");

        ListView listView = (ListView) findViewById(R.id.searchResultList);
        MAdapter mAdapter = new MAdapter(this);
        listView.setAdapter(mAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void makeSnackbar(String content) {
        Snackbar.make(findViewById(R.id.searchResultList), content, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public final class ViewHolder {
        public TextView busName;
        public TextView busSummary;
        public Button addToFav;
        public Button searchButton;
    }

    public class MAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public MAdapter(Context context) {
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
                viewHolder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.search_bus_results, null);
                viewHolder.busName = (TextView) convertView.findViewById(R.id.lineName);
                viewHolder.busSummary = (TextView) convertView.findViewById(R.id.summaryMessage);
                viewHolder.addToFav = (Button) convertView.findViewById(R.id.addFavButton);
                viewHolder.searchButton = (Button) convertView.findViewById(R.id.searchOLButton);
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
                        makeSnackbar("功能开发中...");
                    } else if (v.getId() == searchButtonId) {
                        Intent intent = new Intent();
                        intent.setClass(SearchResultActivity.this, OnlineBusActivity.class);
                        intent.putExtra("busLineInfo", busLineInfo);
                        intent.putExtra("config", config);
                        startActivity(intent);
                    } else {
                        makeSnackbar("WTF!!??");
                    }
                }
            };

            viewHolder.addToFav.setOnClickListener(onClickListener);
            viewHolder.searchButton.setOnClickListener(onClickListener);
            return convertView;
        }
    }
}
