package chookin.utils.io;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuyin on 7/6/14.
 */
public class FileHelper {
    private final static Logger LOG = Logger.getLogger(FileHelper.class);

    public static String formatFileName(String fileName){
        String myName = fileName.replaceAll("&|<|>|\n", "-");
        return FilenameUtils.normalize(myName);
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
        makeParentDirs(fileName);

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
        makeParentDirs(fileName);
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
    public static List<String> readLines(String fileName, String code) throws IOException {
        List<String> lines = new ArrayList<>();
        FileInputStream fInputStream = new FileInputStream(fileName);
        InputStreamReader inputStreamReader = new InputStreamReader(fInputStream, code);
        // In Java 7 you should use auto close features.
        try(BufferedReader br = new BufferedReader(inputStreamReader)) {
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
        makeParentDirs(fileName);
        FileOutputStream output = new FileOutputStream(fileName);
        output.write(bytes);
        output.close();
    }

    public static void makeParentDirs(String fileName) throws IOException {
        if(fileName == null || fileName.trim().isEmpty()){
            throw new IllegalArgumentException("fileName");
        }
        FileHelper.mkdir(new File(fileName).getAbsoluteFile().getParentFile().getAbsolutePath());
    }

    /**
     * Note:
     * <li>Can't create a file and a folder with the same name and in the same folder. The OS would not allow you to do that since the name is the id for that file/folder object. So we have delete the older file</li>
     * <li>While File#mkdir only return false if a file already exists with the same name.</li>
     * @param dirpath
     * @throws IOException
     *    if exists a file with the same name, or if failed to make dir because of security error.
     */
    public static void mkdir(String dirpath) throws IOException {
        if (new java.io.File(dirpath).isDirectory()) {
            return;
        }
        LOG.info("make dir: "+ dirpath);
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
        if (!new File(dirpath).isDirectory()) {
            throw new IOException("Failed to create dir "+ dirpath);
        }
    }
    public static void main(String[] args){
        try {
            FileHelper.save("abc", "/home/zhuyin/stock/tmp/a.m.taobao.com/i40924136103.htm?sid=88ad6ad\n" +
                    "7be926117-abtest=13-rn=9c93719bf63a792f388116731db40f33");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
