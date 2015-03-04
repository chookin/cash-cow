package chookin.etl.processor;

import chookin.etl.common.ResultItems;

/**
 * Created by zhuyin on 3/2/15.
 */
public interface PageProcessor {

    /**
     * process the page, extract urls to fetch, extract the data and store
     *
     * @param page
     */
    public void process(ResultItems page);
}
