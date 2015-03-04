package chookin.utils.web;

import org.apache.log4j.Logger;

import java.util.Date;

/**
 * Created by zhuyin on 2/12/15.
 */
public class Proxy {
    private static final Logger LOG = Logger.getLogger(Proxy.class);

    /**
     * Use command is:
     * java -Dhttp.proxyHost=proxy.cmcc  -Dhttp.proxyPort=8080 -jar my.jar
     */
    public Proxy set(){
        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", Integer.toString(port));
        System.setProperty("http.proxyUser", user);
        System.setProperty("http.proxyPassword", passwd);
        LOG.info("set proxy: "+toString());
        return this;
    }
    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPasswd() {
        return passwd;
    }
    public String getDesc(){
        return desc;
    }

    public Proxy setHost(String host) {
        this.host = host;
        return this;
    }

    public Proxy setPort(int port) {
        this.port = port;
        return this;
    }

    public Proxy setUser(String user) {
        this.user = user;
        return this;
    }

    public Proxy setPasswd(String passwd) {
        this.passwd = passwd;
        return this;
    }

    public Proxy setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public Proxy setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public Proxy setLocation(String location) {
        this.location = location;
        return this;
    }

    public boolean isHighAnonymity() {
        return isHighAnonymity;
    }

    public Proxy setHighAnonymity(boolean isHighAnonymity) {
        this.isHighAnonymity = isHighAnonymity;
        return this;
    }

    public String getType() {
        return type;
    }

    public Proxy setType(String type) {
        this.type = type;
        return this;
    }

    public Float getSpeed() {
        return speed;
    }

    public Proxy setSpeed(Float speed) {
        this.speed = speed;
        return this;
    }

    public Float getConnectTime() {
        return connectTime;
    }

    public Proxy setConnectTime(Float connectTime) {
        this.connectTime = connectTime;
        return this;
    }

    public Date getValidateTime() {
        return validateTime;
    }

    public Proxy setValidateTime(Date validateTime) {
        this.validateTime = validateTime;
        return this;
    }

    String host = "";
    int port;
    String user = "";
    String passwd = "";
    String desc = "";

    String country;
    String location;
    boolean isHighAnonymity;
    String type;
    Float speed;
    Float connectTime;
    Date validateTime;

    @Override
    public String toString() {
        if(host == null){
            return "None";
        }
        return "{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", user='" + user + '\'' +
                ", passwd='" + passwd + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
