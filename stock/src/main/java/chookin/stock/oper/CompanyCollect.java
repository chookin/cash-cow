package chookin.stock.oper;

import chookin.stock.extractor.pipeline.CompanyInfoPileline;
import chookin.stock.handler.StockMapHandler;
import chookin.stock.orm.domain.CompanyInfoEntity;
import chookin.stock.orm.domain.StockEntity;
import chookin.stock.orm.repository.CompanyInfoRepository;
import chookin.stock.utils.SpringHelper;
import cmri.etl.downloader.JsoupDownloader;
import cmri.etl.pipeline.FilePipeline;
import cmri.etl.spider.Spider;
import cmri.utils.configuration.ConfigManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by zhuyin on 3/21/15.
 */
@Service
public class CompanyCollect extends BaseOper{
    @Autowired
    private CompanyInfoPileline pipeline;

    @Autowired
    private CompanyInfoRepository repository;

    @Override
    boolean action() {
        if (!processOption(OperName.CollectCompany)) {
            return false;
        }
        doWork();
        return true;
    }
    private void doWork(){
        Map<String, StockEntity> stocks = StockMapHandler.getStocksMap();

        Spider spider = new Spider(OperName.CollectCompany)
                .setDownloader( JsoupDownloader.getInstance())
                .addPipeline(pipeline)
                .addPipeline(new FilePipeline())
                .setSleepMillisecond(ConfigManager.getPropertyAsInteger("download.sleepMilliseconds"))
                .thread(ConfigManager.getPropertyAsInteger("download.concurrent.num"))
                .setTimeOut(ConfigManager.getPropertyAsInteger("download.timeout"))
                .setValidateSeconds(ConfigManager.getPropertyAsLong("page.validPeriod"));

        for(Map.Entry<String, StockEntity> entry : stocks.entrySet()){
            StockEntity stock = entry.getValue();
            CompanyInfoEntity company = repository.findOne(stock.getCode());
            if(company == null){
                company = new CompanyInfoEntity();
                company.setStockCode(stock.getCode());
            }
            spider.addRequest(chookin.stock.extractor.qq.CompanyPageProcessor.getRequest(stock)
            ).addRequest(chookin.stock.extractor.sina.CompanyPageProcessor.getRequest(stock)
            ).addRequest(chookin.stock.extractor.eastmoney.CompanyPageProcessor.getRequest(stock)
            );
        }
        spider.run();
    }

    public static void main(String[] args){
        CompanyCollect oper = (CompanyCollect) SpringHelper.getAppContext().getBean("companyCollect");
        oper.doWork();
    }
}
