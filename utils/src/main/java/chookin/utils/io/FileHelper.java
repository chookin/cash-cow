package chookin.utils.io;

import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by chookin on 7/6/14.
 */
public class FileHelper {
    private final static Logger LOG = Logger.getLogger(FileHelper.class);
    public static String formatFileName(String fileName){
        return fileName.replaceAll("&|<|>","-");
    }
    public static String getExtension(String filename) {
        filename = filename.replace('\\', '/');
        int indexLastSlash = filename.lastIndexOf('/');
        if (indexLastSlash == -1) {
            indexLastSlash = 0;
        }
        int indexLastDot = filename.substring(indexLastSlash).lastIndexOf('.');
        if (indexLastDot == -1) {
            return "";
        } else {
            return filename.substring(indexLastDot).toLowerCase();
        }
    }
    /**
     * Writes a string to a file. Create parent directories if not exist.
     * @param str
     * @param fileName
     * @throws IOException
     */
    public static void save(String str, String fileName)
            throws IOException {
        LOG.info(String.format("save file %s", fileName));
        String directory = fileName.substring(0, fileName.lastIndexOf('/'));
        mkdirs(directory);

        FileWriter writer;
        writer = new FileWriter(fileName);
        writer.write(str);
        writer.close();
    }
    /**
     * note: can't create a file and a folder with the same name and in the same folder. The OS would not allow you to do that since the name is the id for that file/folder object. So we have delete the older file
     * @param dirpath
     * @throws IOException
     *             if exists a file with the same name
     */
    public static void mkdirs(String dirpath) throws IOException {
        if (new java.io.File(dirpath).isDirectory()) {
            return;
        }
        dirpath = dirpath.replace('\\', '/').replace("//", "/");
        int indexDiskPathEnd = dirpath.indexOf(':') + 1;
        String disk = dirpath.substring(0, indexDiskPathEnd);

        String[] paths = dirpath.substring(indexDiskPathEnd).split("/");
        String basePath = disk;
        for (String path : paths) {
            if (path.isEmpty()) {
                continue;
            }
            basePath = basePath + "/" + path;
            java.io.File file = new java.io.File(basePath);
            if (file.isDirectory()) {
                continue;
            }
            if (file.isFile()) {
                throw new IOException(String.format("already exist file %s",
                        file.getPath()));
            }
            boolean isCreated = file.mkdir();
            if(isCreated){
                LOG.info(String.format("dir %s created", file.getName()));
            }
        }
    }
}
