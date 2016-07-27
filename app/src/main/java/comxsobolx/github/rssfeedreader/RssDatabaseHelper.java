package comxsobolx.github.rssfeedreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import comxsobolx.github.rssfeedreader.model.RssFeedItem;
import comxsobolx.github.rssfeedreader.model.WebSite;

/**
 * Created by aleksandr on 26.07.16.
 */
public class RssDatabaseHelper extends SQLiteOpenHelper {
    private final static int DATABASE_VERSION = 1;

    private final static String DATABSE_NAME = "rssReader";

    private final static String TABLE_SITES = "websites";

    private final static String TABLE_FEED_ITEMS = "items";

    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_LINK = "link";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_PUBDATE = "pubdate";
    private static final String KEY_RSS_LINK = "rss_link";

    public RssDatabaseHelper(Context context) {
        super(context, DATABSE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_SITES_TABLE = "" +
                "CREATE TABLE "  +
                    TABLE_SITES + "(" +
                        KEY_ID + " INTEGER PRIMARY KEY, " +
                        KEY_TITLE + " TEXT, " +
                        KEY_LINK + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_SITES_TABLE);

        String CREATE_FEED_TABLE = "" +
                "CREATE TABLE " +
                    TABLE_FEED_ITEMS + "(" +
                        KEY_ID + " INTEGER PRIMARY KEY, " +
                        KEY_TITLE + " TEXT, " +
                        KEY_DESCRIPTION + " TEXT, " +
                        KEY_LINK + " TEXT, " +
                        KEY_PUBDATE + " TEXT, " +
                        KEY_RSS_LINK + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_FEED_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FEED_ITEMS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SITES);
        onCreate(sqLiteDatabase);
    }

    public void addFeedItem(RssFeedItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, item.getMtitle());
        values.put(KEY_DESCRIPTION, item.getMdescription());
        values.put(KEY_PUBDATE, item.getMpubDate());
        values.put(KEY_RSS_LINK, item.getRssLink());

        if (!isItemExists(db, TABLE_FEED_ITEMS, item.getMlink())) {
            db.insert(TABLE_FEED_ITEMS, null, values);
            db.close();
        } else {
            updateItem(item);
            db.close();
        }
    }

    private int updateItem(RssFeedItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, item.getMtitle());
        values.put(KEY_DESCRIPTION, item.getMdescription());
        values.put(KEY_PUBDATE, item.getMpubDate());
        values.put(KEY_RSS_LINK, item.getRssLink());

        int update = db.update(TABLE_FEED_ITEMS, values, KEY_LINK + " = ? ",
                new String[] {String.valueOf(item.getMlink())});
        db.close();
        return update;
    }

    public void addSite(WebSite webSite) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, webSite.getTitle());
        values.put(KEY_LINK, webSite.getLink());

        if (!isItemExists(db, TABLE_SITES, webSite.getLink())) {
            db.insert(TABLE_SITES, null, values);
            db.close();
        } else {
            updateSite(webSite);
            db.close();
        }
    }

    public ArrayList<WebSite> getAllSites() {
        ArrayList<WebSite> siteList = new ArrayList<WebSite>();

        String selectQuery = "SELECT * FROM " + TABLE_SITES
                + " ORDER BY id DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                WebSite webSite = new WebSite();
                webSite.setId(Integer.parseInt(cursor.getString(0)));
                webSite.setTitle(cursor.getString(1));
                webSite.setLink(cursor.getString(2));
                siteList.add(webSite);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return siteList;
    }

    public ArrayList<RssFeedItem> getAllItems(String rssLink) {
        ArrayList<RssFeedItem> feedItems = new ArrayList<RssFeedItem>();

        String selectQuery = "SELECT * FROM " + TABLE_FEED_ITEMS
                + " WHERE " + KEY_RSS_LINK + " = ? "
                + " ORDER BY id DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(rssLink)});

        if (cursor.moveToFirst()) {
            do {
                RssFeedItem feedItem = new RssFeedItem();
                feedItem.setMtitle(cursor.getString(1));
                feedItem.setMdescription(cursor.getString(2));
                feedItem.setMlink(cursor.getString(3));
                feedItem.setMpubDate(cursor.getString(4));
                feedItems.add(feedItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return feedItems;
    }

    public int updateSite(WebSite site) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, site.getTitle());
        values.put(KEY_LINK, site.getLink());

        // updating row return
        int update = db.update(TABLE_SITES, values, KEY_LINK + " = ?",
                new String[] { String.valueOf(site.getLink()) });
        db.close();
        return update;

    }

    public WebSite getSite(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SITES, new String[] { KEY_ID, KEY_TITLE,
                        KEY_LINK}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        WebSite site = new WebSite(cursor.getString(1), cursor.getString(2));

        site.setId(Integer.parseInt(cursor.getString(0)));
        site.setTitle(cursor.getString(1));
        site.setLink(cursor.getString(2));
        cursor.close();
        db.close();
        return site;
    }

    public void deleteSite(WebSite site) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SITES, KEY_ID + " = ?",
                new String[] { String.valueOf(site.getId())});
        db.close();
    }

    public boolean isItemExists(SQLiteDatabase db, String table, String link) {
        Cursor cursor = db.rawQuery("SELECT 1 FROM " + table +
                " WHERE link = '" + link + "'", new String[] {});
        boolean exists = (cursor.getCount() > 0);
        return exists;
    }

    public void addSites() {

        WebSite economist =
                new WebSite("Economist",
                        "http://www.economist.com/sections/business-finance/rss.xml/");
        addSite(economist);

        WebSite nasa =
                new WebSite("NASA",
                        "https://www.nasa.gov/rss/dyn/breaking_news.rss/");
        addSite(nasa);

        WebSite feedBurner =
                new WebSite("FeedBurner", "http://feeds.feedburner.com/TechCrunch/social/");
        addSite(feedBurner);
    }

    public void deleteItems(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_FEED_ITEMS);
    }
}



















