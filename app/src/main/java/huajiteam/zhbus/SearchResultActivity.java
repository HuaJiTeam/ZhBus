package huajiteam.zhbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class SearchResultActivity extends AppCompatActivity {

    String displayContent = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Intent intent = getIntent();
        GetConfig config = new GetConfig(getApplicationContext());

        TextView textView = (TextView) findViewById(R.id.testTextView);

        textView.setText("Search bus line URL: " + config.getSearchBusLineUrl() + "\n" +
                         "Search stations URL: " + config.getSearchStationUrl() + "\n" +
                         "Search online buses URL: " + config.getSearchOnlineBusUrl() + "\n" +
                         "Wait time: " + config.getWaitTime());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void displayInfo() {
        Snackbar.make(findViewById(R.id.toolbar),  displayContent, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
}
