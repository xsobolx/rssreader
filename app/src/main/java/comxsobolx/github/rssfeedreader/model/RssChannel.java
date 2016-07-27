package comxsobolx.github.rssfeedreader.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aleksandr on 26.07.16.
 */
@Root(strict = false)
public class RssChannel implements Serializable {
    @ElementList(inline = true, name="item")
    private ArrayList<RssFeedItem> mFeedItems;

    public ArrayList<RssFeedItem> getFeedItems() {
        return mFeedItems;
    }

    public RssChannel() {
    }

    public RssChannel(ArrayList<RssFeedItem> mFeedItems) {
        this.mFeedItems = mFeedItems;
    }
}
