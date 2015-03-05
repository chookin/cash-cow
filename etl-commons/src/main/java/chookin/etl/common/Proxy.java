package chookin.etl.common;

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

    public Double getAccessTime() {
        return accessTime;
    }

    public Proxy setAccessTime(Double time) {
        this.accessTime = time;
        return this;
    }

    public Double getConnectTime() {
        return connectTime;
    }

    public Proxy setConnectTime(Double connectTime) {
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
    /**
     * HTTP or HTTPS.
     */
    String type;
    /**
     * Time usage to access, in seconds.
     */
    Double accessTime;

    /**
     * Time usage to establish connection, in seconds
     */
    Double connectTime;

    /**
     * Time of validate this proxy usability.
     */
    Date validateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Proxy proxy = (Proxy) o;

        if (port != proxy.port) return false;
        if (host != null ? !host.equals(proxy.host) : proxy.host != null) return false;
        if (passwd != null ? !passwd.equals(proxy.passwd) : proxy.passwd != null) return false;
        if (user != null ? !user.equals(proxy.user) : proxy.user != null) return false;
        if (validateTime != null ? !validateTime.equals(proxy.validateTime) : proxy.validateTime != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + port;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (passwd != null ? passwd.hashCode() : 0);
        return result;
    }

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
