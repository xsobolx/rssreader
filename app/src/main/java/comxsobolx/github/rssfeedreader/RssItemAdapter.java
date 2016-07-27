package comxsobolx.github.rssfeedreader;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;

import comxsobolx.github.rssfeedreader.model.RssFeedItem;

/**
 * Created by aleksandr on 26.07.16.
 */
class RssItemAdapter extends RecyclerView.Adapter<RssItemAdapter.RssItemHolder> {
    private static final String TAG="RssItemAdapter";
    private List<RssFeedItem> rssFeedItems;
    private Context context;

    RssItemAdapter(Context context, List<RssFeedItem> feed) {
        this.context = context;
        this.rssFeedItems = feed;
    }

    @Override
    public RssItemHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        final LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        final View v = layoutInflater.inflate(R.layout.rss_feed_item, viewGroup, false);
        RssItemHolder holder = new RssItemHolder(v, new RssItemHolder.RssItemViewHolderClicks() {
            @Override
            public void onItem(View caller) {
                Intent intent = new Intent(context, DisplayWebPageActivity.class);
                intent.putExtra("itemUrl", rssFeedItems.get(i).getMlink());
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public int getItemCount() {
        return rssFeedItems.size();
    }

    public void clearData() {
        rssFeedItems.clear();
    }

    public void setData(List<RssFeedItem> feed) {
        this.rssFeedItems = feed;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(final RssItemHolder holder, int pos) {

        RssFeedItem item = rssFeedItems.get(pos);

        holder.titleTextField.setText(item.getMtitle());
        Document doc = Jsoup.parse(item.getMdescription());
        Element imageElement = doc.select("img").first();
        if ( imageElement != null ) {
            String absoluteUrl = imageElement.absUrl("src");
            if (absoluteUrl != null) {
                UrlImageViewHelper.setUrlDrawable(holder.imageView, absoluteUrl,
                        android.R.drawable.gallery_thumb);
                holder.descriptionTextField.setText(doc.body().text());
            }
        }
        holder.publicationDateTextField.setText(item.getMpubDate());
    }

    public static class RssItemHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        private TextView titleTextField;
        private TextView descriptionTextField;
        private CardView container;
        private final RssItemViewHolderClicks listener;

        private ImageView imageView;
        private TextView publicationDateTextField;

        public RssItemHolder(View itemView, RssItemViewHolderClicks listener) {
            super(itemView);
            container = (CardView) itemView.findViewById(R.id.container);
            titleTextField = (TextView) itemView.findViewById(R.id.title);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            descriptionTextField = (TextView) itemView.findViewById(R.id.description);
            publicationDateTextField = (TextView) itemView.findViewById(R.id.pubdate);
            this.listener = listener;
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItem(view);
        }

        public static interface RssItemViewHolderClicks {
            public void onItem(View caller);
        }
    }
}
