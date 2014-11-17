package chookin.utils;

import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Created by chookin on 11/17/14.
 */
public class OptionParser {
    public static final String item_separator = ":";
    public static final String group_separator = ",";
    private String[] args;
    private NavigableMap<String, String> options = new TreeMap<String, String>();
    public OptionParser(String[] args){
        this.args = args;
        this.parse();
    }
    private void parse(){
        for(String arg: this.args){
            String myArg = "";
            String val = "";
            int indexEqualSign = arg.indexOf("=");
            if(indexEqualSign == -1){
                indexEqualSign = arg.length();
            }else{
                val = arg.substring(indexEqualSign + 1, arg.length());
            }
            if(arg.startsWith("--")){
                myArg = arg.substring(2, indexEqualSign);
            }else if(arg.startsWith("-")){
                myArg = arg.substring(1, indexEqualSign);
            }
            if (!myArg.isEmpty()) {
                this.options.put(myArg, val);
            }

        }
    }
    public String getOption(char option){
        String myOption = "" + option;
        return getOption(myOption);
    }

    /**
     * Get the value of a option.
     * @param option name of this option.
     * @return null if not have this option.
     */
    public String getOption(String option){
        if( this.options.containsKey(option)){
            return this.options.get(option);
        }else{
            return null;
        }
    }
}
