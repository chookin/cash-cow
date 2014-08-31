package chookin.utils.web;

/**
 * Created by chookin on 7/6/14.
 */
public class UrlHelper {
    private static String protocolRegex = "[a-zA-Z]+://";

    public static String getProtocolRegex() {
        return protocolRegex;
    }
    /**
     * erase the protocol of a URL and return the erased.
     *
     * @param url
     * @return
     */
    public static String eraseProtocol(String url) {
        return url.replaceAll(String.format("(?i)%s", UrlHelper.getProtocolRegex()), "");
    }
    public static String eraseProtocolAndStart3W(String url) {
        // (?i)让表达式忽略大小写进行匹配;
        // '^'和'$'分别匹配字符串的开始和结束
        return eraseProtocol(url).replaceAll("(?i)(^www.)", "");
    }
}
