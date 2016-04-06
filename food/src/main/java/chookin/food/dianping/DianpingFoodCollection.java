package chookin.food.dianping;

import cmri.etl.common.MapEntity;
import cmri.etl.common.Request;
import cmri.etl.common.ResultItems;
import cmri.etl.job.JobAdapter;
import cmri.etl.pipeline.FilePipeline;
import cmri.etl.pipeline.MongoPipeline;
import cmri.etl.processor.PageProcessor;
import cmri.etl.spider.SpiderAdapter;
import cmri.utils.lang.BaseOper;
import cmri.utils.lang.StringHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Created by chookin on 16/3/6.
 */
public class DianpingFoodCollection extends JobAdapter{
    static final Log LOG = LogFactory.getLog(DianpingFoodCollection.class);
    public static final String siteName = "dianping";

    @Override
    public void run() {
        try {
            onStart();
            collectFood(optionsPack.options());
            onSuccess();
        } catch (Throwable t) {
            onFail();
            LOG.error(null, t);
        }
    }

    public Collection<Request> getSeedRequests() {
        return CategoriesPageProcessor.getSeedRequests();
    }

    void collectFood(Map<String, String> paras) {
        new SpiderAdapter(siteName + " food collection", paras)
                .addRequest(getSeedRequests())
                .addPipeline(new FilePipeline(), new MongoPipeline(siteName))
                .addListener(this.spidersListener()) // 让Job能自动知道该Spider的状态,即Spider的启动,停止,成功,失败等事件通过监听器通知到Job
                .run();
    }

    public static void main(String[] args) {
        new BaseOper() {
            @Override
            public boolean action() {
                new DianpingFoodCollection().init(getOptions().options())
                        .start();
                return true;
            }
        }.setArgs(args).action();
    }


    static class CategoriesPageProcessor implements PageProcessor {
        private static final CategoriesPageProcessor processor = new CategoriesPageProcessor();

        public static Collection<Request> getSeedRequests() {
            return Collections.singletonList(new Request("http://www.dianping.com/beijing/food", processor)
            );
        }

        @Override
        public void process(ResultItems page) {
            Document doc = (Document) page.getResource();
            Elements elements = doc.getElementsByTag("script");
            for (Element element : elements) {
                Elements eStyles = Jsoup.parse(element.html(), element.baseUri()).select(".fpp_cooking li a");
                if (eStyles.isEmpty()) {
                    continue;
                }
                for (Element e : eStyles) {
                    String style = e.text();
                    String url = e.absUrl("href");
                    LOG.trace("find food style: " + style + " " + url);
                    String code = StringHelper.parseRegex(url, "(\\d+)$", 1);
                    MapEntity item = new MapEntity();
                    item.put("_id", MapEntity.genId(siteName, code, style))
                            .put("collection", "category")
                            .put("site", siteName)
                            .put("name", style)
                            .put("code", code)
                            .put("style", style)
                            .put("url", url);
                    page.addItem(item);
                    page.addTargetRequest(FoodsPageProcessor.getRequest(url, style));
                }
            }
        }
    }

    static class FoodsPageProcessor implements PageProcessor {
        private static final FoodsPageProcessor processor = new FoodsPageProcessor();

        /**
         * @param url
         * @param style 菜系
         * @return
         */
        public static Request getRequest(String url, String style) {
            return new Request(url, processor)
                    .putExtra("style", style)
                    ;
        }

        @Override
        public void process(ResultItems page) {
            Document doc = (Document) page.getResource();
            String style = (String) page.getRequest().getExtra("style");
            Elements elements = doc.select("#shop-all-list > ul > li");
            for (Element element : elements) {
                Element eTitle = element.select("div.tit a").first();
                if (eTitle == null) {
                    continue;
                }
                String name = eTitle.attr("title");
                String url = eTitle.absUrl("href");
                String code = StringHelper.parseRegex(url, "/([\\d]+)", 1);
                MapEntity item = new MapEntity();
                item.put("_id", MapEntity.genId(siteName, code, name))
                        .put("collection", "food")
                        .put("site", siteName)
                        .put("name", name)
                        .put("code", code)
                        .put("style", style)
                        .put("url", url);
                Element eCommentList = element.select("span.comment-list").first();
                if (eCommentList != null) {
                    String commentList = eCommentList.text();
                    // 口味8.4 环境8.6 服务8.3
                    item.put("tasteScore", Double.valueOf(StringHelper.parseRegex(commentList, "口味(\\d+\\.\\d+)", 1)));
                    item.put("envScore", Double.valueOf(StringHelper.parseRegex(commentList, "环境(\\d+\\.\\d+)", 1)));
                    item.put("srvScore", Double.valueOf(StringHelper.parseRegex(commentList, "服务(\\d+\\.\\d+)", 1)));
                }
                Element eReviewNum = element.select("div.comment > a.review-num > b").first();
                if (eReviewNum != null) {
                    item.put("reviewNum", Integer.valueOf(eReviewNum.text()));
                }
                Element eAddr = element.select("div.tag-addr .addr").first();
                if (eAddr != null) {
                    item.put("address", eAddr.text());
                }
                LOG.trace(item);
                page.addItem(item);
                page.addTargetRequest(ReviewsPageProcessor.getRequest(item));
            }
            String next = getNextPageUrl(doc);
            if (next != null) {
                Request request = getRequest(next, style)
                        .setPriority(5);
                page.addTargetRequest(request);
            }
        }

        String getNextPageUrl(Document doc) {
            Element element = doc.select("div.content-wrap > div.shop-wrap > div.page > a.next").first();
            if (element != null) {
                return element.absUrl("href");
            }
            return null;
        }
    }

    static class ReviewsPageProcessor implements PageProcessor {
        private static final ReviewsPageProcessor processor = new ReviewsPageProcessor();

        public static Request getRequest(MapEntity item) {
            String url = String.format("http://www.dianping.com/shop/%s/review_more", item.get("code"));
            return new Request(url, processor)
                    .putExtra("item", item);
        }

        @Override
        public void process(ResultItems page) {
            Document doc = (Document) page.getResource();
            MapEntity item = (MapEntity) page.getRequest().getExtra("item");
            if (item == null) {
                throw new RuntimeException("item is null for " + page.getRequest());
            }

            Elements reviewElements = doc.select("div.comment-list > ul > li");
            for (Element element : reviewElements) {

                String reviewerName = element.select("p.name").text();
                String reviewerId = element.select("a[user-id]").attr("user-id");

                MapEntity review = new MapEntity();
                review.put("name", item.get("name"))
                        .put("code", item.get("code"))
                        .put("reviewerName", reviewerName)
                        .put("reviewId", reviewerId);
                LOG.trace(review);
                page.addItem(review);
            }
        }

        String getNextPageUrl(Document doc) {
            Element element = doc.select("div.Pages > div > a.NextPage").first();
            if (element != null) {
                return element.absUrl("href");
            }
            return null;
        }

    }
}
