package comxsobolx.github.rssfeedreader.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by aleksandr on 26.07.16.
 */
@Root(name = "channel", strict = false)
public class WebSite {
    Integer id;
    @Element(name="title", required = false)
    String title;
    @Element(name="link", required = false)
    String link;



    public WebSite(){

    }

    public WebSite(String title, String link){
        this.title = title;
        this.link = link;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setLink(String link){
        this.link = link;
    }


    public Integer getId(){
        return this.id;
    }

    public String getTitle(){
        return this.title;
    }

    public String getLink(){
        return this.link;
    }
}
