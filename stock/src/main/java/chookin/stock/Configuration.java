package chookin.stock;

/**
 * Created by chookin on 7/6/14.
 */
public class Configuration{
    public static class LocalResourceConfiguration {

    }

    public static LocalResourceConfiguration getLocalResource() {
        return LocalResource;
    }

    private static LocalResourceConfiguration LocalResource;
    static {
        Configuration.LocalResource = new LocalResourceConfiguration();
    }
}
