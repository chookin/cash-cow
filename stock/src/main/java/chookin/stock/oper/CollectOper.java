package chookin.stock.oper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

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
    private HistDataCollect histDataCollect;

    @Autowired
    private TradeCollect tradeCollect;

    @Autowired
    private RealDataCollect realDataCollect;

    public CollectOper setArgs(String[] args) {
        super.setArgs(args);
        return this;
    }

    public boolean action() throws IOException {
        String[] args = getOptionParser().getArgs();
        return stockCollect.setArgs(args).action()
                || companyCollect.setArgs(args).action()
                || histDataCollect.setArgs(args).action()
                || tradeCollect.setArgs(args).action()
                || realDataCollect.setArgs(args).action()
                ;
    }
}