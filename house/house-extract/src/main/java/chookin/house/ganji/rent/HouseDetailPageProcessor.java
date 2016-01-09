package chookin.house.ganji.rent;

import chookin.house.House;
import cmri.etl.common.ResultItems;
import cmri.etl.processor.PageProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by zhuyin on 4/1/15.
 */
public class HouseDetailPageProcessor implements PageProcessor {
    private static final Log LOG = LogFactory.getLog(HouseDetailPageProcessor.class);

    private static HouseDetailPageProcessor processor = new HouseDetailPageProcessor();

    public static HouseDetailPageProcessor getInstance(){
        return processor;
    }

    @Override
    public void process(ResultItems page) {
        House house = page.getRequest().getExtra("house", House.class);
        Document doc = (Document) page.getResource();

        Element element = doc.select(".with-area").first();
        if(element != null) {
            String region = element.text();
            element = doc.select(".addr-area").first();
            region = region + "-"+element.text();
            house.set("region", region); // 位置
        }

        element = doc.select("li.peizhi").first();
        house.set("fitment", element.text());
        LOG.trace(house);
        page.setField("house", house);
    }
}
