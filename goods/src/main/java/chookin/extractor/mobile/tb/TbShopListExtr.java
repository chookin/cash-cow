package chookin.extractor.mobile.tb;

import chookin.extractor.mobile.ShopListExtr;
import chookin.orm.ShopEntity;

import java.io.IOException;
import java.util.List;

/**
 * Created by zhuyin on 1/6/15.
 */
public class TbShopListExtr extends ShopListExtr {
    public TbShopListExtr(String startUrl) {
        super(startUrl);
    }

    @Override
    public List<ShopEntity> extract() throws IOException {
        return null;
    }

    List<ShopEntity> extractPage(String url){
        return null;
    }
}
