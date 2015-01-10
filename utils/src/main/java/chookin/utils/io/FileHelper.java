package chookin.utils.io;

import chookin.utils.web.UrlHelper;
import com.sun.corba.se.spi.orbutil.fsm.Input;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chookin on 7/6/14.
 */
public class FileHelper {
    private final static Logger LOG = Logger.getLogger(FileHelper.class);

    public static String getUrlFileName(String url){
        String filename = UrlHelper.eraseProtocolAndStart3W(url);
        filename = FileHelper.formatFileName(filename);
        if(FileHelper.getExtension(filename).isEmpty()){
            filename = filename + ".html";
        }
        return filename;
    }
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
        String absPath = new File(fileName).getAbsolutePath();
        LOG.trace(String.format("save file %s", absPath));
        String directory = absPath.substring(0, absPath.lastIndexOf('/'));
        mkdirs(directory);

        Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName), "UTF-8"));
        try {
            out.write(str);
        } finally {
            out.close();
        }
    }
    public static void save(InputStream in, String fileName) throws IOException {
        if(in == null){
            return;
        }
        if(fileName == null || fileName.trim().isEmpty()){
            throw new IllegalArgumentException("fileName");
        }
        OutputStream out = new FileOutputStream(fileName);
        IOUtils.copy(in, out);
        in.close();
        out.close();
    }
    public static List<String> readLines(String fileName) throws IOException {
        List<String> lines = new ArrayList<>();
        // In Java 7 you should use auto close features.
        try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine();
            while (line != null) {
                lines.add(line);
                line = br.readLine();
            }
        }
        return lines;
    }
    public static String readString(String fileName) throws IOException {
        StringBuilder sb = new StringBuilder();
        // In Java 7 you should use auto close features.
        try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
        }
        return sb.toString();
    }
    public static void save(byte[] bytes, String fileName)
            throws IOException {
        LOG.trace(String.format("save file %s", fileName));
        int index = fileName.lastIndexOf('/');
        if(index != -1){
            String directory = fileName.substring(0, index);
            mkdirs(directory);
        }

        FileOutputStream output = new FileOutputStream(fileName);
        output.write(bytes);
        output.close();
    }
    /**
     * Note:
     * <li>Can't create a file and a folder with the same name and in the same folder. The OS would not allow you to do that since the name is the id for that file/folder object. So we have delete the older file</li>
     * <li>While File#mkdirs only return false if a file already exists with the same name.</li>
     * @param dirpath
     * @throws IOException
     *             if exists a file with the same name
     */
    public static void mkdirs(String dirpath) throws IOException {
        if (new java.io.File(dirpath).isDirectory()) {
            return;
        }
        dirpath = dirpath.replace('\\', '/').replace("//", "/");

        String basePath = "";
        if(dirpath.startsWith("/")){
            basePath = "/";
        }

        String[] paths = dirpath.split("/");
        for (String path : paths) {
            if (path.isEmpty()) {
                continue;
            }
            if(basePath.isEmpty()){
                basePath = path;
            }else{
                basePath = basePath + "/" + path;
            }
            java.io.File file = new java.io.File(basePath);
            if (file.isFile()) {
                throw new IOException(String.format("failed to make dir '%s', because a file already exists with that name",
                        file.getPath()));
            }
            if(file.exists()){
                continue;
            }
            boolean isCreated = file.mkdir();
            if(isCreated){
                LOG.trace(String.format("make dir '%s'", file.getAbsolutePath()));
            }
        }
    }
}
