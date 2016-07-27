package comxsobolx.github.rssfeedreader;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;

import comxsobolx.github.rssfeedreader.model.RssFeed;
import comxsobolx.github.rssfeedreader.model.RssFeedItem;

/**
 * Created by aleksandr on 26.07.16.
 */
class RssItemAdapter extends RecyclerView.Adapter<RssItemAdapter.RssItemHolder> {
    private static final String TAG="RssItemAdapter";
    private List<RssFeedItem> rssFeedItems;
    private Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(RssFeedItem item);
    }

    RssItemAdapter(List<RssFeedItem> feed, OnItemClickListener listener) {
        this.rssFeedItems = feed;
        this.listener = listener;
    }

    @Override
    public RssItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        final View v = layoutInflater.inflate(R.layout.rss_feed_item, viewGroup, false);
        return new RssItemHolder(v);
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
        holder.bind(rssFeedItems.get(pos), listener);
    }

    public static class RssItemHolder extends RecyclerView.ViewHolder{
        private TextView titleTextField;
        private TextView descriptionTextField;
        private RelativeLayout container;

        private ImageView imageView;
        private TextView publicationDateTextField;

        public RssItemHolder(View itemView) {
            super(itemView);
            container = (RelativeLayout) itemView.findViewById(R.id.container);
            titleTextField = (TextView) itemView.findViewById(R.id.title);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            descriptionTextField = (TextView) itemView.findViewById(R.id.description);
            publicationDateTextField = (TextView) itemView.findViewById(R.id.pubdate);
        }

        public void bind(final RssFeedItem item, final OnItemClickListener listener) {
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
