package chookin.utils.configuration;

import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parameter expression is like "${user.home}/stock", its key can include English letters, number, dot, underline.
 * Created by zhuyin on 3/6/15.
 */
public class ParameterParser {
    /**
     * For example, for "${user.home}/stock", its key is "user.home", and region is "${user.home}".
     */
    private static final String keyRegex = "\\$\\{([a-zA-Z\\d\\._]+)\\}";
    private static final String regionRegex = "\\$\\{[a-zA-Z\\d\\._]+\\}";
    public ParameterParser(){
    }

    /**
     *
     * @return sorted regions.
     */
    public Set<String> parseRegions(String parameter){
        Set<String> regions = new TreeSet<>();
        Pattern p = Pattern.compile(regionRegex);
        Matcher m = p.matcher(parameter);
        while (m.find()){
            regions.add(m.group());
        }
        return regions;
    }

    public String parseKey(String region){
        Pattern p = Pattern.compile(keyRegex);
        Matcher m = p.matcher(region);
        if (m.find()){
            return m.group(1);
        }
        return null;
    }

    public String getReal(String parameter){
        String real = parameter;
        for(String region : parseRegions(parameter)){
            String key = parseKey(region);
            String property = ConfigManager.getProperty(key);
            if(property == null){
                throw new RuntimeException("cannot find property "+region);
            }
            real = real.replace(region, property);
        }
        return real;
    }

}
