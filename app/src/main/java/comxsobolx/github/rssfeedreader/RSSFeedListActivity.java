package comxsobolx.github.rssfeedreader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import comxsobolx.github.rssfeedreader.model.WebSite;

public class RSSFeedListActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private String[] sqliteIds;

    public static String TAG_ID = "id";
    public static String TAG_TITLE = "title";
    public static String TAG_LINK = "link";

    private ListView lv;
    private List<String> rssTitlesList = new ArrayList<>();
    ArrayList<HashMap<String, String>> rssFeedList;

    private RssDatabaseHelper rssDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssfeed_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddNewSiteActivity.class);
                startActivityForResult(intent, 100);
            }
        });

        rssFeedList = new ArrayList<HashMap<String, String>>();
        rssDb = new RssDatabaseHelper(
                getApplicationContext());


        rssDb.addSites();
        new loadStoreSites().execute();

        lv = (ListView) findViewById(R.id.website_listview);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String rssLink =
                        ((TextView) view.findViewById(R.id.link)).getText().toString();
                Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                intent.putExtra(TAG_LINK, rssLink);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.website_listview) {
            menu.setHeaderTitle("Delete");
            menu.add(Menu.NONE, 0, 0, "Delete Feed");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        if (menuItemIndex == 0) {
            rssDb = new RssDatabaseHelper(getApplicationContext());
            WebSite site = new WebSite();
            site.setId(Integer.parseInt(sqliteIds[info.position]));
            rssDb.deleteSite(site);
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rssfeed_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class loadStoreSites extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(
                    RSSFeedListActivity.this);
            pDialog.setMessage("Loading websites ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            runOnUiThread(new Runnable() {
                public void run() {

                    ArrayList<WebSite> siteList = rssDb.getAllSites();

                    sqliteIds = new String[siteList.size()];

                    for (int i = 0; i < siteList.size(); i++) {

                        WebSite s = siteList.get(i);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_ID, s.getId().toString());
                        map.put(TAG_TITLE, s.getTitle());
                        map.put(TAG_LINK, s.getLink());

                        rssFeedList.add(map);

                        sqliteIds[i] = s.getId().toString();
                    }

                    Log.d("Loading: ", siteList.size() + " sites");

                    ListAdapter adapter = new SimpleAdapter(
                            RSSFeedListActivity.this,
                            rssFeedList, R.layout.websites_row,
                            new String[] { TAG_ID, TAG_TITLE, TAG_LINK },
                            new int[] { R.id.sqlite_id, R.id.title, R.id.link });

                    lv.setAdapter(adapter);
                    registerForContextMenu(lv);

                }
            });
            return null;
        }

        protected void onPostExecute(String args) {
            pDialog.dismiss();
        }

    }


}
