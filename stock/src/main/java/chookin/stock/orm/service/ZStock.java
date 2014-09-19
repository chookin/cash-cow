package chookin.stock.orm.service;

import chookin.stock.extractor.CompanyInfoExtr;
import chookin.stock.extractor.HistoryDataExtr;
import chookin.stock.extractor.RealDataExtr;
import chookin.stock.extractor.StockListExtr;
import chookin.stock.extractor.eastmoney.EmStockListExtr;
import chookin.stock.extractor.gtimg.TRealDataExtr;
import chookin.stock.extractor.qq.QCompanyInfoExtr;
import chookin.stock.extractor.sina.SCompanyInfoExtr;
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
            Iterable<StockEntity> stocks = this.stockRepository.findAll();
            Map<String, StockEntity> map = new TreeMap<String, StockEntity>();
            for(StockEntity item : stocks){
                if(!item.getDiscarded()){
                    map.put(item.getStockCode(), item);
                }
            }
            this.stocksMap.putAll(map);
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

    public void saveHistoryData(int startYear, int startQuarter, int endYear, int endQuater) throws IOException {
        if(startYear > endYear){
            return;
        }
        if(startYear == endYear){
            for(int quarter = startQuarter; quarter < endQuater; ++quarter){
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
        Collection<HistoryDataEntity> histDatas = extractHistoryData(this.getStocksMap(), year, quarter);
        LOG.info(String.format("start to save history data of %d-%d", year, quarter));
        this.historyDataRepository.save(histDatas);
        LOG.info(String.format("%d history data of %d-%d were saved", histDatas.size(), year, quarter));
    }
    public Map<String, StockEntity> extractStockList() throws IOException{
        StockListExtr extractor = new EmStockListExtr();
        return extractor.getStocks();
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
            CompanyInfoExtr extractor = new QCompanyInfoExtr(stock);
            try {
                CompanyInfoEntity entity = extractor.extract();
                if (entity == null){
                    this.removeStock(stock);
                }else{
                    rst.put(stock.getStockCode(), entity);
                }
            } catch (Throwable t) {
                LOG.error(null, t);
            }
        }
        return  rst;
    }

    private Collection<HistoryDataEntity> extractHistoryData(Map<String, StockEntity> stocks, int year, int quarter) throws IOException{
        List<HistoryDataEntity> rst = new ArrayList<HistoryDataEntity>();
        for(Map.Entry<String, StockEntity> entry : stocks.entrySet()){
            StockEntity stock = entry.getValue();
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
        }
        return rst;
    }
}