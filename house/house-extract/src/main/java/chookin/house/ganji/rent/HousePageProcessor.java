package chookin.house.ganji.rent;

import chookin.house.House;
import chookin.house.SiteName;
import cmri.etl.common.Request;
import cmri.etl.common.ResultItems;
import cmri.etl.processor.PageProcessor;
import cmri.utils.lang.StringHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhuyin on 4/1/15.
 */
public class HousePageProcessor implements PageProcessor {
    private static final Log LOG = LogFactory.getLog(HousePageProcessor.class);
    static PageProcessor processor = new HousePageProcessor();

    public static Set<Request> getSeedRequests(){
        Set<Request> requests = new HashSet<>();
        requests.add(new Request("http://bj.ganji.com/fang1/m1/")
                        .setPageProcessor(processor)
                        .putExtra("category", "rent")
                        .putExtra("rentType", "whole")
        );
        return requests;
    }
    @Override
    public void process(ResultItems page) {

        Document doc = (Document) page.getResource();
        String category = page.getRequest().getExtra("category", String.class);
        String rentType = page.getRequest().getExtra("rentType", String.class);

        Elements elements = doc.select("div.list-mod4");
        for (Element element : elements) {
            House house = new House();
            Element item = element.select("a.list-info-title").first();
            String name = item.text();
            String url = item.absUrl("href");
            String code = StringHelper.parseRegex(url, "([\\w]+).htm", 1);
            house.setName(name)
                    .setSite(SiteName.Ganji)
                    .setUrl(url)
                    .setCode(code)
                    .set("rentType", rentType)
                    .set("category", category);

            item = element.select(" div.list-mod2 > p").first();
            if(item != null) {
                // 2室1厅1卫/74㎡/5/28层/简单装修/南北向/今天
                String[] arrs = item.text().split("/");
                house.set("type", arrs[0]); // 户型
                String area = arrs[1];
                area = StringHelper.parseRegex(area, "([\\d]+)", 1);
                house.set("area", Integer.valueOf(area));
                String floor = arrs[2]+"/"+arrs[3];
                house.set("floor", floor);
                house.set("decoration", arrs[4]);
                house.set("orientation", arrs[5]);
            }

            item = element.select(".sale-price").first();
            if(item != null)
                house.set("price", Integer.valueOf(item.text())); // 租金

            LOG.trace(house);
            page.addTargetRequest(new Request(url)
                            .setPageProcessor(HouseDetailPageProcessor.getInstance())
                            .setPriority(8)
                            .putExtra("house", house)
            );
        }

        String next = getNextPage(doc);
        if(next != null){
            page.addTargetRequest(new Request(next)
                            .setPageProcessor(processor)
                            .setPriority(7)
                            .putExtra("category", category)
                            .putExtra("rentType", "whole")
            );
        }
    }

    private String getNextPage(Document doc){
        Element element = doc.select(".pageBox a.next").first();
        if(element != null){
            return element.absUrl("href");
        }
        return null;
    }
}
