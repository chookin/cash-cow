package chookin.utils.configuration;

import java.io.File;
import java.io.IOException;
import java.util.SortedMap;

import javax.xml.parsers.ParserConfigurationException;

import chookin.utils.web.NetworkHelper;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

public class ConfigManager {
	private final static Logger LOG = Logger.getLogger(ConfigManager.class);
	private static Object file;
	private static long lastReConfigTime = Long.MIN_VALUE;
	/**
	 * time interval between two configurations, ms
	 */
	private static long configMinInter = 7 * 1000;
	private static SortedMap<String, String> paras;

	public static synchronized void setFile(String fileName){
		if(new File(fileName).exists()){
			ConfigManager.file = fileName;
		}else{
			ConfigManager.file = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
		}
		NetworkHelper.setProxy(ConfigManager.getPropertyAsBool("proxy.enable"));
	}
	public static synchronized String getProperty(String item){
		if(ConfigManager.file == null){
			LOG.warn("without configuration file");
			return null;
		}
		if(paras == null){
			try {
				reConfig();
			} catch (Exception e) {
				LOG.fatal(null, e);
				System.exit(-1);
			}
		}
		return paras.get(item.toLowerCase());
	}
	/**
	 * The {@code boolean} returned represents the value {@code true} if the string argument
	 * is not {@code null} and is equal, ignoring case, to the string
	 * {@code "true"}. <p>
	 * Example: {@code Boolean.parseBoolean("True")} returns {@code true}.<br>
	 * Example: {@code Boolean.parseBoolean("yes")} returns {@code false}.
	 */
	public static synchronized boolean getPropertyAsBool(String item){
		return Boolean.parseBoolean(getProperty(item));
	}

	public static synchronized int getPropertyAsInteger(String item){
		return Integer.parseInt(getProperty(item));
	}
	public static synchronized long getPropertyAsLong(String item){
		return Long.parseLong(getProperty(item));
	}
	/**
	 * reload configuration file, and update configuration parameters
	 * @throws IOException
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public static synchronized void reConfig() throws ParserConfigurationException, SAXException, IOException {
		long now = System.currentTimeMillis();
		if (now < lastReConfigTime + configMinInter) {
			return;
		}
		LOG.info("configuration file is '" + file + "'.");
		lastReConfigTime = now;
		paras = new ConfigFile(file).getProperties();
	}
}


