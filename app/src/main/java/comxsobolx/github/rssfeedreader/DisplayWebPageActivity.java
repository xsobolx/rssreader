package comxsobolx.github.rssfeedreader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class DisplayWebPageActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_web_page);

        Intent intent = getIntent();
        String pageUrl = intent.getStringExtra("itemUrl");

        webView = (WebView) findViewById(R.id.webpage);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(pageUrl);

        webView.setWebChromeClient(new DisplayWebPageActivityClient());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);
        DisplayWebPageActivity.this.setProgress(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class DisplayWebPageActivityClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            DisplayWebPageActivity.this.setValue(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }

    public void setValue(int progress) {
        this.progressBar.setProgress(progress);
    }
}
