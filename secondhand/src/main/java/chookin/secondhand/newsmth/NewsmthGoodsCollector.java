package chookin.secondhand.newsmth;

import chookin.secondhand.base.GoodsCollection;
import chookin.secondhand.model.TargetObject;
import chookin.secondhand.utils.EntityHelper;
import cmri.etl.common.MapEntity;
import cmri.etl.common.MapItem;
import cmri.etl.common.Request;
import cmri.etl.common.ResultItems;
import cmri.etl.processor.PageProcessor;
import cmri.utils.lang.BaseOper;
import cmri.utils.lang.StringHelper;
import cmri.utils.lang.TimeHelper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 抓取水木二手市场
 * <p>
 * Created by chookin on 16/10/11.
 */
public class NewsmthGoodsCollector extends GoodsCollection {
    private static final String SITE = "newsmth";

    @Override
    public String site() {
        return SITE;
    }

    /**
     * 获取种子请求
     */
    @Override
    public Collection<Request> getSeedRequests() {
        List<Request> requests = new ArrayList<>();
        TargetObject target = new TargetObject()
                .setSite(site())
                .setCategory("二手电脑市场")
                .setUrl("http://newsmth.net/nForum/board/SecondComputer?ajax");
        requests.add(ListPageProcessor.getRequest(target));

        target = new TargetObject()
                .setSite(site())
                .setCategory("二手数码产品")
                .setUrl("http://www.newsmth.net/nForum/board/SecondDigi?ajax");
        requests.add(ListPageProcessor.getRequest(target));

        target = new TargetObject()
                .setSite(site())
                .setCategory("二手市场主版")
                .setUrl("http://www.newsmth.net/nForum/board/SecondMarket?ajax");
        requests.add(ListPageProcessor.getRequest(target));
        return requests;
    }

    /**
     * 文章列表页面的处理器,解析页面得到一个个帖子的主题名、发帖时间、作者、回复数、最新回复时间
     */
    static class ListPageProcessor implements PageProcessor {
        private final static PageProcessor PROCESSOR = new ListPageProcessor();

        static Request getRequest(TargetObject object) {
            return getRequest(object, 1);
        }

        /**
         * 生成Request
         *
         * @param pageNum 页面位置,从1开始
         */
        static Request getRequest(TargetObject object, int pageNum) {
            String url = String.format("%s?ajax&p=%d", object.getUrl(), pageNum);
            return new Request(url, PROCESSOR)
                    .putExtra("targetObject", object)
                    .putExtra("pageNum", pageNum)
                    ;
        }

        @Override
        public void process(ResultItems page) {
            TargetObject targetObject = (TargetObject) page.getRequest().getExtra("targetObject");
            int pageNum = (int) page.getRequest().getExtra("pageNum");
            Document document = (Document) page.getResource();

            List<MapItem> entities = new ArrayList<>();
            Elements elements = document.select("#body > div.b-content > table > tbody > tr");
            for (Element element : elements) {
                if (!element.select("tr.top > td.title_9 a").isEmpty()) {
                    continue;
                }
                Element topic = element.select("td.title_9 a").first();
                if (topic == null) {
                    continue;
                }
                String title = topic.text();
                String url = topic.absUrl("href");
                String topicId = StringHelper.parseRegex(url, "/(\\d+)$", 1);

                String createTimeStr = element.select("td.title_10").first().text();
                Date createTime = TimeHelper.parseDate(createTimeStr);

                String author = element.select("td.title_12 a").first().text();

                String commentCountStr = element.select("td.title_11.middle").last().text();
                int commentCount = Integer.valueOf(commentCountStr);

                String lastCommentTimeStr = element.select("td.title_10").last().text();
                Date lastCommentTime = TimeHelper.parseDate(lastCommentTimeStr);

                MapEntity entity = new MapEntity();
                entity.put("_id", EntityHelper.getRecordId(targetObject, topicId))
                        .put("collection", "topic")
                        .put("id", topicId)
                        .put("title", title)
                        .put("url", url)
                        .put("createTime", createTime)
                        .put("author", author)
                        .put("commentCount", commentCount)
                        .put("lastCommentTime", lastCommentTime)
                ;

                entities.add(entity);
            }
            // 保存数据到Response中
            page.addItem(entities);
            // 添加新的请求
            page.addTargetRequest(getNextPageRequest(targetObject, document, pageNum, entities));
        }

        /**
         * 获取下一个页面的请求
         *
         * @param entities 选择一批entity测试是否存在,用以判定之后的页面数据已被采集
         */
        Request getNextPageRequest(TargetObject object, Document doc, int curPageNum, Collection<MapItem> entities) {
            int totalPageNum = getTotalPageNum(doc);
            if (curPageNum >= totalPageNum) {
                return null;
            }
            if (!EntityHelper.continueCrawl(object, entities)) {
                return null;
            }

            return getRequest(object, curPageNum + 1);
        }

        /**
         * 获取文章列表页有多少页
         */
        int getTotalPageNum(Document doc) {
            // 主题数
            String topicCountStr = doc.select("div.t-pre-bottom div.page ul.pagination li.page-pre i").text();
            int topicCount = Integer.valueOf(topicCountStr);
            int pageSize = 30;// 每页包含的主题数
            return (topicCount + pageSize - 1) / pageSize;
        }
    }

    public static void main(String[] args) {
        new BaseOper() {
            @Override
            public boolean action() {
                new NewsmthGoodsCollector()
                        .init(getOptions().options())
                        .start();
                return true;
            }
        }.setArgs(args).action();
    }
}
