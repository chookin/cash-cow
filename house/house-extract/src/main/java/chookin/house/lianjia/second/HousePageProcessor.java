package chookin.house.lianjia.second;

import chookin.house.House;
import chookin.house.SiteName;
import cmri.etl.common.Request;
import cmri.etl.common.ResultItems;
import cmri.etl.downloader.CasperJsDownloader;
import cmri.etl.processor.PageProcessor;
import cmri.utils.lang.StringHelper;
import cmri.utils.lang.TimeHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhuyin on 3/28/15.
 */
public class HousePageProcessor implements PageProcessor {
    private static final Log LOG = LogFactory.getLog(HousePageProcessor.class);
    static PageProcessor processor = new HousePageProcessor();

    public static Set<Request> getSeedRequests() {
        Set<Request> requests = new HashSet<>();
        requests.add(new Request("http://bj.lianjia.com/ershoufang/", processor)
                .setDownloader(CasperJsDownloader.getInstance())
                .setValidPeriod(TimeHelper.DAY_MILLISECONDS)
                .putExtra("category", "2hand")
        );
        return requests;
    }

    @Override
    public void process(ResultItems page) {
        Document doc = (Document) page.getResource();
        String category = page.getRequest().getExtra("category", String.class);
        Elements elements = doc.select("#house-lst > li");
        for (Element element : elements) {
            House house = new House();
            Element item = element.select("div.info-panel > h2 > a").first();
            String name = item.text();
            String url = item.absUrl("href");
            String code = StringHelper.parseRegex(url, "ershoufang/([\\w]+).shtml", 1);
            house.setName(name)
                    .setSite(SiteName.Lianjia)
                    .setUrl(url)
                    .setCode(code)
                    .set("category", category);

            item = element.select(".zone").first();
            house.set("type", item.text());//户型

            item = element.select("div.where > span:nth-child(4)").first();
            house.set("orientation", item.text()); //朝向

            String year = StringHelper.parseRegex(element.text(), "([\\d]{4})年", 1);
            if (year != null)
                house.set("year", Integer.valueOf(year));

            item = element.select(".meters").first();
            house.set("area", parseInt(item.text()));//面积

            item = element.select(".price > .num").first();
            house.set("price", Integer.valueOf(item.text()));//售价

            item = element.select(".price-pre").first();
            house.set("avgPrice", parseInt(item.text()));//单价

            item = element.select(".square").first();
            house.set("viewNum", parseInt(item.text()));//客户看房数

            item = element.select(".fang05-ex").first();
            if (item != null) {
                house.set("school", item.text());
            }
            item = element.select(".fang-subway-ex").first();
            if (item != null) {
                house.set("subway", item.text());
            }
            item = element.select(".taxfree-ex").first();
            if (item != null) {
                house.set("tax", item.text());
            }
            item = element.select(".unique-ex").first();
            if (item != null) {
                house.set("unique", item.text());
            }
            item = element.select(".haskey-ex").first();
            if (item != null) {
                house.set("haskey", item.text());
            }
            LOG.trace(house);
            page.addTargetRequest(new Request(url, HouseDetailPageProcessor.getInstance())
                    .setPriority(8)
                    .putExtra("house", house)
            );
        }
        if (!page.getRequest().getUrl().contains("/pg")) {// only for the first page
            int pageNum = getPageNum(doc);
            for (int i = 2; i < pageNum; ++i) {
                String url = String.format("http://bj.lianjia.com/ershoufang/pg%d", i);
                page.addTargetRequest(new Request(url, processor)
                        .setValidPeriod(TimeHelper.DAY_MILLISECONDS)
                        .setPriority(7)
                );
            }
        }
    }

    private int getPageNum(Document doc) {
        Elements elements = doc.select(".house-lst-page-box");
        if (elements != null) {
            String num = StringHelper.parseRegex(elements.outerHtml(), "totalPage[\\D]+([\\d]+)", 1);
            if (num != null)
                return Integer.parseInt(num);
        }
        throw new RuntimeException("Failed to parse page num");
    }

    private static Integer parseInt(String str) {
        str = StringHelper.parseRegex(str, "([\\d]+)", 1);
        return Integer.valueOf(str);
    }
}
