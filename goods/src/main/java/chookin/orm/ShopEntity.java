package chookin.orm;

import java.util.Date;

/**
 * Created by zhuyin on 1/6/15.
 */
public class ShopEntity {
    /**
     * 店铺名
     */
    private String name;
    /**
     * 店铺Id
     */
    private String code;
    /**
     * 粉丝数目
     */
    private int fansNum;
    /**
     * 全部宝贝数目
     */
    private int goodsNum;
    /**
     * 好评率
     */
    private float good;
    /**
     * 开店时间
     */
    private Date openTime;
    /**
     * 掌柜名
     */
    private String shopkeeper;
    /**
     * 掌柜Id
     */
    private String shopKeeperId;
    /**
     * 服务电话
     */
    private String phone;
    /**
     * 店铺URL
     */
    private String url;

}
