package comxsobolx.github.rssfeedreader.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by aleksandr on 26.07.16.
 */
public class WebSite {
    private Integer id;
    private String title;
    private String link;

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
