package chookin.stock.oper;

import cmri.utils.lang.BaseOper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by zhuyin on 1/25/15.
 */
@Service
class CollectOper extends BaseOper {
    @Autowired
    private StockCollect stockCollect;

    @Autowired
    private CompanyCollect companyCollect;

    @Autowired
    private HistoryCollect historyCollect;

    @Autowired
    private TradeCollect tradeCollect;

    @Override
    public boolean action() {
        Map<String, String> args = getOptions().options();
        return stockCollect.setArgs(args).action()
                || companyCollect.setArgs(args).action()
                || historyCollect.setArgs(args).action()
                || tradeCollect.setArgs(args).action()
                ;
    }
}