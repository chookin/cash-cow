package tax.chaoyang;

import cmri.etl.common.Request;
import cmri.etl.common.ResultItems;
import cmri.etl.processor.PageProcessor;
import cmri.utils.lang.DateHelper;
import com.google.gson.*;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.util.*;

/**
 * Created by zhuyin on 6/10/15.
 北京市朝阳区地方税务局
 http://123.57.88.180:8008/apt/choose_business.jsp

 {
 "now" : [["1433933995915"]],
 "afternoon" : [[null, null, null, "2015-06-10:0", "2015-06-11:0", "2015-06-12:0", "2015-06-13:-1"], ["2015-06-14:-1", "2015-06-15:0", "2015-06-16:0", "2015-06-17:0", "2015-06-18:0", "2015-06-19:0", "2015-06-20:-1"], ["2015-06-21:-1", "2015-06-22:-1", "2015-06-23:0", "2015-06-24:0", "2015-06-25:-1", "2015-06-26:-1", "2015-06-27:-1"], ["2015-06-28:-1", "2015-06-29:-1", "2015-06-30:-1", "2015-07-01:-1", "2015-07-02:-1", "2015-07-03:-1", "2015-07-04:-1"]],
 "moning" : [[null, null, null, "2015-06-10:0", "2015-06-11:0", "2015-06-12:0", "2015-06-13:-1"], ["2015-06-14:-1", "2015-06-15:0", "2015-06-16:0", "2015-06-17:0", "2015-06-18:0", "2015-06-19:0", "2015-06-20:-1"], ["2015-06-21:-1", "2015-06-22:-1", "2015-06-23:0", "2015-06-24:0", "2015-06-25:-1", "2015-06-26:-1", "2015-06-27:-1"], ["2015-06-28:-1", "2015-06-29:-1", "2015-06-30:-1", "2015-07-01:-1", "2015-07-02:-1", "2015-07-03:-1", "2015-07-04:-1"]]
 }

 */
public class DatePagePrcoessor implements PageProcessor {
    static final Logger LOG = Logger.getLogger(DatePagePrcoessor.class);
    static DatePagePrcoessor prcoessor = new DatePagePrcoessor();

    static Collection<Request> getSeedRequests(){
        return Collections.singletonList(new Request("http://123.57.88.180:8008/apt/order.html?method=loadOrderDate", prcoessor)
                        .ignoreCache(true)
                        .setTarget(Request.TargetResource.Json)
        );
    }
    @Override
    public void process(ResultItems page) {
        String str = (String) page.getResource();
        JsonObject oJson = new JsonParser().parse(str).getAsJsonObject();

        String strNow = oJson.get("now").getAsString();
        Date now = new Date(Long.valueOf(strNow));

        Map<Date, Integer> avail = new TreeMap<>();
        JsonArray moning = oJson.get("moning").getAsJsonArray();
        avail.putAll(parse(moning));
        JsonArray afternoon = oJson.get("afternoon").getAsJsonArray();
        avail.putAll(parse(afternoon));

        LOG.info("Now: " + DateHelper.toString(now, "yyyy-MM-dd H:m:s"));
        LOG.info(toString(avail));

        alert(avail);
    }

    /**
     * parse string like "[[null, null, null, "2015-06-10:0", "2015-06-11:0", "2015-06-12:0", "2015-06-13:-1"], ["2015-06-14:-1", "2015-06-15:0", "2015-06-16:0", "2015-06-17:0", "2015-06-18:0", "2015-06-19:0", "2015-06-20:-1"], ["2015-06-21:-1", "2015-06-22:-1", "2015-06-23:0", "2015-06-24:0", "2015-06-25:-1", "2015-06-26:-1", "2015-06-27:-1"], ["2015-06-28:-1", "2015-06-29:-1", "2015-06-30:-1", "2015-07-01:-1", "2015-07-02:-1", "2015-07-03:-1", "2015-07-04:-1"]]"
     */
    Map<Date, Integer> parse(JsonArray arr){
        Map<Date, Integer> rst = new HashMap<>();
        for(JsonElement element: arr){
            JsonArray subArr = element.getAsJsonArray();
            for(JsonElement subElement: subArr){
                if(JsonNull.INSTANCE.equals(subElement)){
                    continue;
                }
                String str = subElement.getAsString();
                String[] val = str.split(":");
                if(val.length != 2){
                    LOG.error(arr);
                    continue;
                }
                Date date = DateHelper.parseDate(val[0]);
                int myAvail = Integer.valueOf(val[1]);
                rst.put(date, myAvail);
            }
        }
        return rst;
    }

    String toString(Map<Date, Integer> avail){
        StringBuilder str = new StringBuilder();
        for(Map.Entry<Date, Integer> entry: avail.entrySet()){
            str.append(DateHelper.toString(entry.getKey())).append(" ").append(getState(entry.getValue())).append("\n");
        }
        return str.toString();
    }

    String getState(int state){
        switch (state){
            case 0:
                return "没号了";
            case -1:
                return "不可预约";
            default:
                return "可预约";
        }
    }

    boolean avaible(int state){
        return state != 0 && state != -1;
    }

    boolean alert(Map<Date, Integer> avail){
        for(Map.Entry<Date, Integer> entry: avail.entrySet()){
            boolean ok = alert(entry.getKey(), entry.getValue());
            if(ok){
                return true;
            }
        }
        return false;
    }

    boolean alert(Date date, int state){
        if(!avaible(state)){
            return false;
        }
        JOptionPane.showMessageDialog(null, DateHelper.toString(date) + "可预约啦", "恭喜", JOptionPane.INFORMATION_MESSAGE);
        JOptionPane optionPane = new JOptionPane();
        JDialog dialog = optionPane.createDialog("恭喜");
        dialog.setSize(696, 158);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
        return true;
    }
}
