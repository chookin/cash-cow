package chookin.stock.Data;

/**
 * Created by chookin on 7/6/14.
 */
public enum Exchange {
    ShangHai("sh"),
    ShenZhen("sz"),
    Unknown("unkown");
    private Exchange(String desc){
        this.description = desc;
    }

    public String toString(){
        return description;
    }

    private String description;
}
