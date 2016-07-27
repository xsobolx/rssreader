package comxsobolx.github.rssfeedreader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import comxsobolx.github.rssfeedreader.model.RssChannel;
import comxsobolx.github.rssfeedreader.model.RssFeed;
import comxsobolx.github.rssfeedreader.model.RssFeedItem;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;


public class FeedActivity extends AppCompatActivity {
    private RecyclerView rss_feed_list;
    private ArrayList<RssFeedItem> rssFeedItemList = new ArrayList<>();
    private static String TAG = "FeedActivity";
    private RssItemAdapter adapter = null;
    private RssDatabaseHelper rssDB;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        rss_feed_list = (RecyclerView)findViewById(R.id.rss_feed_list);
        rss_feed_list.setHasFixedSize(true);
        rss_feed_list.addItemDecoration(new MarginDecoration(this));
        LinearLayoutManager layoutMgr = new LinearLayoutManager(this);
        rss_feed_list.setLayoutManager(layoutMgr);

        adapter = new RssItemAdapter(this, rssFeedItemList);

        rssDB = new RssDatabaseHelper(this);

        Intent intent = getIntent();

        String rssLink = intent.getStringExtra(RSSFeedListActivity.TAG_LINK);

        if (isOnline()) {
            updateRssItemsFromInternet(rssLink);
        } else {
            new loadRssFeedItems().execute();
        }

    }

    void updateRssItemsFromInternet(String rssLink ) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Request request = new Request.Builder()
                .url(rssLink)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.e(TAG, "Request failure " + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Reader reader = new StringReader(response.body().string());
                Persister serializer = new Persister();
                try {
                    RssFeed feed = serializer.read(RssFeed.class, reader, false);
                    RssChannel channel = feed.getmChannel();
                    rssFeedItemList = channel.getFeedItems();
                    for (RssFeedItem item :
                            rssFeedItemList) {
                        rssDB.addFeedItem(item);
                    }

                    updateDisplay(rssFeedItemList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateDisplay(final ArrayList<RssFeedItem> items) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setData(items);
                adapter.notifyDataSetChanged();
                rss_feed_list.setAdapter(adapter);
            }
        });
    }

    private class MarginDecoration extends RecyclerView.ItemDecoration {
        private int margin;

        public MarginDecoration(Context context) {
            margin = context.getResources().getDimensionPixelSize(R.dimen.item_margin);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(margin, margin, margin, margin);
        }
    }

    private class loadRssFeedItems extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(
                    FeedActivity.this);
            pDialog.setMessage("Loading recent articles...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            rssFeedItemList = rssDB.getAllItems();
            updateDisplay(rssFeedItemList);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }
}
