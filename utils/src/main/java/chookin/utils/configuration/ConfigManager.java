package chookin.utils.configuration;

import java.io.IOException;
import java.util.SortedMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class ConfigManager {
	private static Object fileName = "config.xml";
	private static long lastReConfigTime = Long.MIN_VALUE;
	/**
	 * time interval between two configurations, ms
	 */
	private static long configMinInter = 7 * 1000;
	private static SortedMap<String, String> paras;
	public static synchronized void setFile(String fileName){
		ConfigManager.fileName = fileName;
	}
	public static synchronized String getProperty(String item){
		return paras.get(item.toLowerCase());
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
		lastReConfigTime = now;
		paras = new ConfigFile(fileName).getProperties();
	}
}


