package chookin.stock.orm.service;

import chookin.stock.extractor.HistoryDataDetailExtr;
import chookin.stock.extractor.HistoryDataExtr;
import chookin.stock.extractor.RealDataExtr;
import chookin.stock.extractor.StockListExtr;
import chookin.stock.extractor.eastmoney.ECompanyInfoExtr;
import chookin.stock.extractor.eastmoney.EmStockListExtr;
import chookin.stock.extractor.gtimg.TRealDataExtr;
import chookin.stock.extractor.qq.QCompanyInfoExtr;
import chookin.stock.extractor.sina.SCompanyInfoExtr;
import chookin.stock.extractor.sina.SHistoryDataDetailExtr;
import chookin.stock.extractor.sina.SHistoryDataExtr;
import chookin.stock.orm.domain.CompanyInfoEntity;
import chookin.stock.orm.domain.HistoryDataEntity;
import chookin.stock.orm.domain.RealDataEntity;
import chookin.stock.orm.domain.StockEntity;
import chookin.stock.orm.repository.CompanyInfoRepository;
import chookin.stock.orm.repository.HistoryDataRepository;
import chookin.stock.orm.repository.RealDataRepository;
import chookin.stock.orm.repository.StockRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by chookin on 7/6/14.
 */
@Service
public class ZStock {
    private final static Logger LOG = Logger.getLogger(ZStock.class);
    private Map<String, StockEntity> stocksMap = new ConcurrentSkipListMap<String, StockEntity>();

    public Map<String, StockEntity> getStocksMap(){
        if(this.stocksMap.isEmpty()){
            LOG.info("start to get stocks map");
            Iterable<StockEntity> stocks = this.stockRepository.findAll();
            Map<String, StockEntity> map = new TreeMap<String, StockEntity>();
            for(StockEntity item : stocks){
                if(!item.getDiscarded()){
                    map.put(item.getStockCode(), item);
                }
            }
            this.stocksMap.putAll(map);
            LOG.info(String.format("get %d stocks", this.stocksMap.size()));
        }
        return this.stocksMap;
    }

    @Autowired
    private StockRepository stockRepository;
    @Transactional
    public void saveStocks() throws IOException {
        Map<String, StockEntity> stocks = extractStockList();
        this.stockRepository.save(stocks.values());
    }

    @Autowired
    private RealDataRepository realDataRepository;

    @Transactional
    public void saveRealData() throws IOException {
        Map<String, RealDataEntity> realdatas = extractRealData();
        this.realDataRepository.save(realdatas.values());
    }
    @Autowired
    private CompanyInfoRepository companyInfoRepository;

    @Transactional
    public void saveCompanyInfo() throws IOException {
        Map<String, CompanyInfoEntity> companies = extractCompanyInfos();
        this.companyInfoRepository.save(companies.values());
    }

    @Autowired
    private HistoryDataRepository historyDataRepository;

    public void saveCurrentQuarterHistoryData() throws IOException{
        Calendar calendar =Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1; // 在格里高利历和罗马儒略历中一年中的第一个月是 JANUARY，它为 0
        int quarter = (month + 2) / 3;
        int year = calendar.get(Calendar.YEAR);
        this.saveHistoryData(year, quarter);
    }
    public void saveHistoryData(int startYear, int startQuarter, int endYear, int endQuater) throws IOException {
        LOG.info(String.format("start to save history data for %d-%d to %d-%d", startYear, startQuarter, endYear, endQuater));
        if(startYear > endYear){
            return;
        }
        if(startYear == endYear){
            for(int quarter = startQuarter; quarter <= endQuater; ++quarter){
                this.saveHistoryData(startYear, quarter);
            }
            return;
        }
        for(int quarter = startQuarter; quarter <= 4; ++quarter){
            this.saveHistoryData(startYear, quarter);
        }
        for(int year = startYear + 1; year < endYear; ++year){
            for(int quarter = 1; quarter <= 4; ++quarter){
                this.saveHistoryData(year, quarter);
            }
        }
        for(int quarter = 1; quarter <= endQuater;++quarter ){
            this.saveHistoryData(endYear, quarter);
        }
    }
    @Transactional
    public void saveHistoryData(int year, int quarter) throws IOException {
        LOG.info(String.format("start to save history data of %d-%d", year, quarter));
        Map<String, StockEntity> stockMap = this.getStocksMap();
        Collection<StockEntity> stocks = new ArrayList<StockEntity>();
        int count = 0;
        for(Map.Entry<String, StockEntity> entry: stockMap.entrySet()){
            stocks.add(entry.getValue());
            if(stocks.size() < 100){
                continue;
            }
            Collection<HistoryDataEntity> histDatas = this.extractHistoryData(stocks, year, quarter);
            this.historyDataRepository.save(histDatas);
            count += histDatas.size();
            stocks.clear();
        }
        LOG.info(String.format("%d history data of %d-%d were saved", count, year, quarter));
    }

    public void saveHistoryDetail() throws IOException{
        Map<String, StockEntity> stocksMap = this.getStocksMap();
        for(Map.Entry<String, StockEntity> entry: stocksMap.entrySet()){
            Calendar startDay  = Calendar.getInstance();
            startDay.add(Calendar.DAY_OF_YEAR, -3);
            Calendar endDay = Calendar.getInstance();
            endDay.add(Calendar.DAY_OF_YEAR, -3);

            for ( Calendar curDay = startDay; curDay.compareTo(endDay) <= 0; curDay.add(Calendar.DAY_OF_YEAR, 1) ){
                int weekDay = curDay.get(Calendar.DAY_OF_WEEK);
                if(weekDay == 1 || weekDay == 7){//SUNDAY, SATURDAY
                    continue;
                }
                StockEntity entity = entry.getValue();
                HistoryDataDetailExtr extr = new SHistoryDataDetailExtr(entity);
                extr.extract(curDay.getTime());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public Map<String, StockEntity> extractStockList() throws IOException{
        StockListExtr extractor = new EmStockListExtr();
        return extractor.getNewStocks(this.getStocksMap());
    }

    private Map<String, RealDataEntity> extractRealData() throws IOException {
        Map<String, RealDataEntity> rst = new TreeMap<String, RealDataEntity>();
        Map<String, StockEntity> stocks = this.getStocksMap();
        for(Map.Entry<String, StockEntity> entry : stocks.entrySet()){
            StockEntity entity = entry.getValue();
            RealDataExtr extractor = new TRealDataExtr(entity);
            RealDataEntity real = extractor.extract();
            rst.put(real.getStockCode(), real);
        }
        return  rst;
    }

    @Transactional
    private void removeStock(StockEntity item){
        item.setDiscarded(true);
        this.stockRepository.save(item);
        this.stocksMap.remove(item.getStockCode());
    }
    private Map<String, CompanyInfoEntity> extractCompanyInfos() throws IOException {
        Map<String, CompanyInfoEntity> rst = new TreeMap<String, CompanyInfoEntity>();
        Map<String, StockEntity> stocks = this.getStocksMap();
        for(Map.Entry<String, StockEntity> entry : stocks.entrySet()){
            StockEntity stock = entry.getValue();

            CompanyInfoEntity company = this.companyInfoRepository.findOne(stock.getStockCode());
            if(company == null){
                company = new CompanyInfoEntity();
                company.setStockCode(stock.getStockCode());
            }
            rst.put(company.getStockCode(), company);

            String msg = String.format("extract company info of %s", company.getStockCode());
            try {
                new SCompanyInfoExtr(stock).extract(company);
            } catch (Throwable t) {
                LOG.error(msg, t);
            }
            try{
                new QCompanyInfoExtr(stock).extract(company);
            }catch (Throwable t){
                LOG.error(msg, t);
            }
            try{
                new ECompanyInfoExtr(stock).extract(company);
            }catch (Throwable t){
                LOG.error(msg, t);
            }
        }
        return  rst;
    }
    private Collection<HistoryDataEntity> extractHistoryData(StockEntity stock, int year, int quarter) throws IOException{
        List<HistoryDataEntity> rst = new ArrayList<HistoryDataEntity>();
        HistoryDataExtr extractor = new SHistoryDataExtr(stock);
        try {
            Collection<HistoryDataEntity> entities = extractor.extract(year, quarter);
            if (entities == null){
                this.removeStock(stock);
            }else{
                rst.addAll(entities);
            }
        } catch (Throwable t) {
            LOG.error(null, t);
        }
        return rst;
    }
    private Collection<HistoryDataEntity> extractHistoryData(Collection<StockEntity> stocks, int year, int quarter) throws IOException{
        List<HistoryDataEntity> rst = new ArrayList<HistoryDataEntity>();
        for(StockEntity stock: stocks){
            rst.addAll(extractHistoryData(stock, year, quarter));
        }
        return rst;
    }
}