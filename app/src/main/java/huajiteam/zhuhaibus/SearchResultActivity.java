package huajiteam.zhuhaibus;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;

import huajiteam.zhuhaibus.zhdata.data.BusLineInfo;

public class SearchResultActivity extends AppCompatActivity {

    private ArrayList<BusLineInfo> busLineInfos;
    private GetConfig config;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Bundle bundle = getIntent().getExtras();
        this.busLineInfos = (ArrayList<BusLineInfo>) bundle.get("busLineInfos");
        this.config = new GetConfig(this);

        ListView listView = (ListView) findViewById(R.id.searchResultList);
        MAdapter mAdapter = new MAdapter(this, new FavoriteConfig(this));
        listView.setAdapter(mAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getDelegate().getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Add advertisement
        AdView mAdView = (AdView) findViewById(R.id.adView);
        if (!this.config.getDoNotDisplayAds()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        } else {
            mAdView.getLayoutParams().height = 0;
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
        Snackbar.make(findViewById(R.id.searchResultList), content, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public final class ViewHolder {
        TextView busName;
        TextView busSummary;
        ImageButton addToFav;
        ImageButton searchButton;
    }

    public class MAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private FavoriteConfig favoriteConfig;

        MAdapter(Context context, FavoriteConfig favoriteConfig) {
            this.mInflater = LayoutInflater.from(context);
            this.favoriteConfig = favoriteConfig;
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
            final ViewHolder viewHolder;

            final BusLineInfo busLineInfo = busLineInfos.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.search_bus_results, null);
                viewHolder.busName = (TextView) convertView.findViewById(R.id.lineName);
                viewHolder.busSummary = (TextView) convertView.findViewById(R.id.summaryMessage);
                viewHolder.addToFav = (ImageButton) convertView.findViewById(R.id.addFavButton);
                viewHolder.searchButton = (ImageButton) convertView.findViewById(R.id.searchOLButton);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (favoriteConfig.getBusIsInList(busLineInfo.getName(), busLineInfo.getToStation())) {
                viewHolder.addToFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24px));
            } else {
                viewHolder.addToFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_24sp));
            }

            viewHolder.busName.setText(busLineInfo.getName() + "，往" + busLineInfo.getToStation());
            viewHolder.busSummary.setText(getString(R.string.search_result_price) + busLineInfo.getPrice());

            final int addToFavId = viewHolder.addToFav.getId();
            final int searchButtonId = viewHolder.searchButton.getId();

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == addToFavId) {
                        favoriteConfig.reloadData();
                        if (favoriteConfig.getBusIsInList(busLineInfo.getName(), busLineInfo.getToStation())) {
                            favoriteConfig.removeBusInList(busLineInfo.getName(), busLineInfo.getToStation());
                            viewHolder.addToFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_24sp));
                        } else {
                            favoriteConfig.addData(busLineInfo);
                            viewHolder.addToFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24px));
                        }
                        makeSnackbar(getString(R.string.success));
                    } else if (v.getId() == searchButtonId) {
                        Intent intent = new Intent();
                        intent.setClass(SearchResultActivity.this, OnlineBusActivity.class);
                        intent.putExtra("busLineInfo", busLineInfo);
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
