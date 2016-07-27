package comxsobolx.github.rssfeedreader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import comxsobolx.github.rssfeedreader.model.RssFeed;
import comxsobolx.github.rssfeedreader.model.WebSite;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class AddNewSiteActivity extends AppCompatActivity {

    public static final String TAG = AddNewSiteActivity.class.getSimpleName();

    private Button btnSubmit;
    private Button btnCancel;
    private EditText txtUrl;
    private TextView lblMessage;


    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_site);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        txtUrl = (EditText) findViewById(R.id.txtUrl);
        lblMessage = (TextView) findViewById(R.id.lblMessage);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = txtUrl.getText().toString();

                if (url.length() > 0) {
                    lblMessage.setText("");

                    String urlPattern =
                            "^http(s{0,1})://[a-zA-Z0-9_/\\-\\.]+\\.([A-Za-z/]{2,5})[a-zA-Z0-9_/\\&\\?\\=\\-\\.\\~\\%]*";
                    if (url.matches(urlPattern)) {
                        new loadRssFeed().execute(url);
                    } else {
                        lblMessage.setText("Please enter a valid url");
                    }
                } else {
                    lblMessage.setText("Please enter a website url");
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private class loadRssFeed extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddNewSiteActivity.this);
            pDialog.setMessage("Fetching RSS Information ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String url = strings[0];
            String rssUrl = null;
            try {
                Document doc = Jsoup.connect(url).get();
                Elements links = doc.select("link[type=application/rss+xml]");

                if (links.size() > 0) {
                    rssUrl = links.get(0).attr("abs:href").toString();
                    Elements titleElements = doc.select("title");
                    String title = titleElements.get(0).text().toString();
                    Log.d(TAG, "url: " + rssUrl + " title: " + title);
                    WebSite webSite = new WebSite(title, rssUrl);
                    RssDatabaseHelper rssDB = new RssDatabaseHelper(getApplicationContext());
                    rssDB.addSite(webSite);
                    Intent intent = getIntent();
                    setResult(100, intent);
                    finish();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lblMessage.setText("Rss url not found");
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
        }
    }
}


//    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//                    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//                    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
//
//                    Request request = new Request.Builder()
//                            .url(rssUrl)
//                            .build();
//
//                    client.newCall(request).enqueue(new Callback() {
//                        @Override
//                        public void onFailure(Call call, IOException e) {
//                            Log.e(TAG, "Request failure " + e.getLocalizedMessage());
//                        }
//
//                        @Override
//                        public void onResponse(Call call, Response response) throws IOException {
//                            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//
//                            Reader reader = new StringReader(response.body().string());
//                            Persister serializer = new Persister();
//                            try {
//                                WebSite site = serializer.read(WebSite.class, reader, false);
//                                RssDatabaseHelper rssDB =
//                                        new RssDatabaseHelper(getApplicationContext());
//                                rssDB.addSite(site);
//                                Intent intent = getIntent();
//                                setResult(100, intent);
//                                finish();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
































