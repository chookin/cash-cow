package chookin.house;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by zhuyin on 3/28/15.
 */
public class House implements Serializable {
    private String code;
    private String name;
    private String site;
    private String url;
    private Map<String, Object> properties = new TreeMap<>();
    /**
     * acquisition time
     */
    private Date time;
    public House() {
        this.time = new Date();
    }

    public String getCode() {
        return code;
    }

    public House setCode(String code){
        this.code = code;
        return this;
    }

    public String getName() {
        return name;
    }

    public House setName(String name) {
        this.name = name;
        return this;
    }

    public String getSite() {
        return site;
    }

    public House setSite(String site) {
        this.site = site;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public House setUrl(String url) {
        this.url = url;
        return this;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public House emptyProperties() {
        this.properties.clear();
        return this;
    }

    public House set(Map<String, Object> properties) {
        if(properties !=null) {
            this.properties.putAll(properties);
        }
        return this;
    }

    public House set(String name, Object value) {
        if(value == null){
            return this;
        }
        if(value instanceof String && ((String) value).isEmpty()){
            return this;
        }
        this.properties.put(name, value);
        return this;
    }

    public Object get(String propertyName) {
        return properties.get(propertyName);
    }

    public House setTime(Date time) {
        this.time = time;
        return this;
    }

    public Date getTime() {
        return this.time;
    }

    @Override
    public String toString() {
        return "House{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", site='" + site + '\'' +
                ", url='" + url + '\'' +
                ", properties=" + properties +
                ", time=" + time +
                '}';
    }
}
