package chookin.house.lianjia.second;

import chookin.house.House;
import cmri.etl.common.ResultItems;
import cmri.etl.processor.PageProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuyin on 3/28/15.
 */
public class HouseDetailPageProcessor implements PageProcessor {
    private static final Log LOG = LogFactory.getLog(HouseDetailPageProcessor.class);

    private static HouseDetailPageProcessor processor = new HouseDetailPageProcessor();

    public static HouseDetailPageProcessor getInstance(){
        return processor;
    }

    private HouseDetailPageProcessor(){}
    @Override
    public void process(ResultItems page) {
        House house = page.getRequest().getExtra("house", House.class);
        Document doc = (Document) page.getResource();

        Element element = doc.select(".zone-name").first();
        house.set("street", element.text());//小区

        element = doc.select(".region").first();//西城区...
        if(element != null){
            house.set("region", element.text());
        }

        house.set("floor", getFloor(doc));

        Elements elements = doc.select(".items.clear .content");
        List<String> comments = new ArrayList<>();
        for(Element item : elements){
            String comment = item.text();
            comments.add(comment);
        }
        if(!comments.isEmpty()){
            house.set("comments", comments);
        }
        LOG.trace(house);
        page.setField("house", house);
    }

    private String getFloor(Document doc){
        Element element = doc.select("div.info-box.left > div.desc-text.clear > dl:nth-child(7) > dd").first();
        if(element != null) {
            String floor = element.text();
            if (floor.contains("楼")) {
                return floor;
            }
        }
        LOG.warn("Fail to parse floor of " + doc.baseUri());
        return null;
    }
}
