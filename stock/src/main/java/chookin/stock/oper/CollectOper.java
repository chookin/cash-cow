package chookin.stock.oper;

import chookin.stock.handler.CollectHandler;
import cmri.utils.lang.OptionParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by zhuyin on 1/25/15.
 */
@Service
class CollectOper extends BaseAction{
    public CollectOper setArgs(String[] args) {
        super.setArgs(args);
        return this;
    }
    @Autowired
    private CollectHandler collectHandler;

    /**
     * <ul>
     *     <li>collect --stock</li>
     *     <li>collect --company</li>
     *     <li>collect --hist=2014:3:2014:4</li>
     *     <li>collect --histdetail=2014-11-5:2014-12-28</li>
     * </ul>
     * @return
     * @throws IOException
     */
    public boolean action() throws IOException {
        String option = "collect";
        if(!processOption(option)){
            return false;
        }
        return collectStocks()
                || collectCompany()
                || collectHist()
                || collectHistDetail()
                ;
    }
    boolean collectStocks() throws IOException {
        String option = "--stock";
        if(!processOption(option)){
            return false;
        }
        collectHandler.collectStocks();
        return true;
    }
    public boolean collectCompany() throws IOException {
        String option = "--company";
        if(!processOption(option)){
            return false;
        }
        collectHandler.collectCompanyInfo();
        return true;
    }
    public boolean collectHist() throws IOException {
        String option = "--hist";
        if(!processOption(option)){
            return false;
        }
        String para = getOption(option);
        String[] items = para.split(OptionParser.item_separator);
        collectHandler.collectHistoryData(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]), Integer.parseInt(items[3]));
        return true;
    }
    public boolean collectHistDetail() throws IOException {
        String option = "--histdetail";
        if(!processOption(option)){
            return false;
        }
        String para = getOption(option);
        String[] items = para.split(OptionParser.item_separator);
        collectHandler.collectHistoryDetail(items[0], items[1]);
        return true;
    }
}