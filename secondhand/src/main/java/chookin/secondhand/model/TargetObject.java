package chookin.secondhand.model;

import java.io.Serializable;

/**
 * Created by chookin on 16/10/12.
 */
public class TargetObject implements Serializable{
    private String site;
    private String category;
    private String url;

    public String getSite() {
        return site;
    }

    public TargetObject setSite(String site) {
        this.site = site;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public TargetObject setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public TargetObject setUrl(String url) {
        this.url = url;
        return this;
    }
}
