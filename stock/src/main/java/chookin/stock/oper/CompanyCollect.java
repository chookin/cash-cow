package chookin.stock.oper;

import chookin.stock.extractor.pipeline.CompanyPipeline;
import chookin.stock.handler.StockMapHandler;
import chookin.stock.orm.domain.CompanyEntity;
import chookin.stock.orm.domain.StockEntity;
import chookin.stock.orm.repository.CompanyRepository;
import chookin.stock.utils.SpringHelper;
import cmri.etl.pipeline.FilePipeline;
import cmri.etl.spider.Spider;
import cmri.etl.spider.SpiderAdapter;
import cmri.utils.lang.BaseOper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhuyin on 3/21/15.
 */
@Service
public class CompanyCollect extends BaseOper {
    @Autowired
    private CompanyPipeline pipeline;

    @Autowired
    private CompanyRepository repository;

    @Override
    public boolean action() {
        if (!getOptions().process(OperName.CollectCompany)) {
            return false;
        }
        doWork();
        return true;
    }
    private void doWork(){
        Spider spider = new SpiderAdapter(OperName.CollectCompany)
                .addPipeline(pipeline)
                .addPipeline(new FilePipeline())
                ;
        addRequest(spider);
        spider.run();
    }

    private Map<String, CompanyEntity> getSavedCompanies(){
        Iterable<CompanyEntity> companies = repository.findAll();
        Map<String, CompanyEntity> map = new HashMap<>();
        for(CompanyEntity entity: companies) map.put(entity.getStockCode(), entity);
        return map;
    }

    private void addRequest(Spider spider){
        Map<String, StockEntity> stocks = StockMapHandler.getStocksMap();
        Map<String, CompanyEntity> savedCompanies = getSavedCompanies();
        for(Map.Entry<String, StockEntity> entry : stocks.entrySet()){
            StockEntity stock = entry.getValue();
            CompanyEntity company = savedCompanies.get(stock.getCode());
            if(company == null){
                company = new CompanyEntity();
                company.setStockCode(stock.getCode());
            }
            spider.addRequest(chookin.stock.extractor.qq.CompanyPageProcessor.getRequest(company)
            ).addRequest(chookin.stock.extractor.sina.CompanyPageProcessor.getRequest(company)
            ).addRequest(chookin.stock.extractor.eastmoney.CompanyPageProcessor.getRequest(stock, company)
            );
        }
    }
    public static void main(String[] args){
        CompanyCollect oper = (CompanyCollect) SpringHelper.getAppContext().getBean("companyCollect");
        oper.doWork();
    }
}
