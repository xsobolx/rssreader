package comxsobolx.github.rssfeedreader.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by aleksandr on 26.07.16.
 */
@Root(name = "rss", strict = false)
public class RssFeed implements Serializable {
    @Element(name = "channel")
    private RssChannel mChannel;

    public RssChannel getmChannel() {
        return mChannel;
    }

    public RssFeed() {
    }

    public RssFeed(RssChannel mChannel) {
        this.mChannel = mChannel;
    }


}

