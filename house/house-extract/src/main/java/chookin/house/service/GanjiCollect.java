package chookin.house.service;

import chookin.house.SiteName;
import chookin.house.ganji.rent.HousePageProcessor;
import chookin.house.service.base.BaseOper;
import chookin.house.service.base.HouseCollect;
import cmri.etl.common.Request;

import java.util.Set;

/**
 * Created by zhuyin on 4/2/15.
 */
public class GanjiCollect extends BaseOper implements HouseCollect {
    public GanjiCollect(String[] args) {
        super(args);
    }

    @Override
    public boolean action() {
        return collectHouses();
    }

    @Override
    public String getSiteName() {
        return SiteName.Ganji;
    }

    @Override
    public Set<Request> getSeedRequests() {
        return HousePageProcessor.getSeedRequests();
    }

    public static void main(String[] args){
        new GanjiCollect(args).action();
    }
}
