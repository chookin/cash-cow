package chookin.stock.oper;

/**
 * Created by zhuyin on 5/20/15.
 */
public enum OperName {
    CollectStock("collect-stock"),
    CollectCompany("collect-company"),
    CollectHist("collect-hist"),
    CollectReal("collect-real");
    private String desc;

    OperName(String desc){
        this.desc = desc;
    }

    public String getDesc(){
        return this.desc;
    }
}
