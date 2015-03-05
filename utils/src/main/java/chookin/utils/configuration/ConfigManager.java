package chookin.utils.configuration;

import chookin.utils.concurrent.ThreadHelper;
import chookin.utils.io.FileHelper;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.SortedMap;

/**
 * Created by zhuyin on 7/6/14.
 */
public class ConfigManager {
	private final static Logger LOG = Logger.getLogger(ConfigManager.class);
	/**
	 * The main configuration file's name
	 */
	private static String mainConfigFileName;
	private static long lastReConfigTime = Long.MIN_VALUE;
	private static SortedMap<String, String> paras;

	public static synchronized void setFile(String fileName){
		mainConfigFileName = fileName;
		if(new File(mainConfigFileName).exists()){
			LOG.info("Main configuration file is '" + new File(mainConfigFileName).getAbsolutePath() + "'.");
		}else{
			LOG.info("Main configuration file  is class resource '" + mainConfigFileName + "'.");
		}
	}
	public static void dumpConfigFile() throws IOException {
		dumpConfigFile(mainConfigFileName);
		dumpConfigFile("log4j.properties");
	}
	public static void dumpConfigFile(String fileName) throws IOException {
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
		FileHelper.save(in, fileName);
		LOG.info("save configuration file to " + fileName);
	}
	public static InputStream getResourceFile(String fileName) throws IOException {
		File file = new File(fileName);
		if(file.exists()){
			return new FileInputStream(fileName);
		} else {
			return Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
		}
	}

	public static synchronized String getProperty(String item){
		if(ConfigManager.mainConfigFileName == null){
			LOG.warn("without configuration mainConfigFile");
			return null;
		}
		if(paras == null){
			try {
				startWatchDaemon();
				reConfig();
			} catch (Exception e) {
				LOG.fatal(null, e);
				System.exit(-1);
			}
		}
		return paras.get(item.toLowerCase());
	}

	public static synchronized String getProperty(String item, String defaultValue){
		String value = getProperty(item);
		return value == null? defaultValue: value;
	}
	/**
	 * The {@code boolean} returned represents the value {@code true} if the string argument
	 * is not {@code null} and is equal, ignoring case, to the string
	 * {@code "true"}. <p>
	 * Example: {@code Boolean.parseBoolean("True")} returns {@code true}.<br>
	 * Example: {@code Boolean.parseBoolean("yes")} returns {@code false}.
	 */
	public static synchronized boolean getPropertyAsBool(String item){
		String val = checkConfigured(item);
		return Boolean.parseBoolean(val);
	}

	public static synchronized int getPropertyAsInteger(String item){
		String val = checkConfigured(item);
		return Integer.parseInt(val);
	}

	public static synchronized long getPropertyAsLong(String item){
		String val = checkConfigured(item);
		return Long.parseLong(val);
	}
	private static Thread watchDaemon;
	private static synchronized void startWatchDaemon(){
		if(watchDaemon != null) {
			return;
		}
		watchDaemon = new Thread(){
			@Override
			public void run() {
				while (true) {
					ThreadHelper.sleep(1000);
					try {
						reConfig();
					} catch (ParserConfigurationException | SAXException | IOException e) {
						LOG.error("Failed to reload main configuration file "+mainConfigFileName, e);
					}
				}
			}
		};
		watchDaemon.setDaemon(true);
		watchDaemon.start();
	}

	private static String checkConfigured(String property){
		String val = getProperty(property);
		if(val == null){
			throw new IllegalArgumentException("property "+ property+ " is not configured");
		}
		return val;
	}

	/**
	 * reload configuration mainConfigFile, and update configuration parameters
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	static synchronized void reConfig() throws ParserConfigurationException, SAXException, IOException {
		long now = System.currentTimeMillis();
		// time interval between two configurations, ms
		long configMinInter = 10 * 1000;
		if (now < lastReConfigTime + configMinInter) {
			return;
		}
		lastReConfigTime = now;
		Object mainConfigFile;
		if(new File(mainConfigFileName).exists()){
			mainConfigFile = mainConfigFileName;
		}else{
			mainConfigFile = Thread.currentThread().getContextClassLoader().getResourceAsStream(mainConfigFileName);
		}
		paras = new ConfigFile(mainConfigFile).getProperties();
	}
}


