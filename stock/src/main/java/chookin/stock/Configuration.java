package chookin.stock;

/**
 * Created by chookin on 7/6/14.
 */
public class Configuration{
    public static class LocalResourceConfiguration {
        private String localArchivePath = "/home/chookin/stock";

        public String getLocalArchivePath() {
            return localArchivePath;
        }

        public void setLocalArchivePath(String localArchivePath) {
            this.localArchivePath = localArchivePath;
        }

    }

    public static LocalResourceConfiguration getLocalResource() {
        return LocalResource;
    }

    private static LocalResourceConfiguration LocalResource;
    static {
        Configuration.LocalResource = new LocalResourceConfiguration();
    }
}
