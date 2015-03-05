package chookin.etl.processor;

import chookin.etl.common.ResultItems;

/**
 * Created by zhuyin on 3/2/15.
 */
public interface PageProcessor {

    /**
     * Process the page, extract urls to fetch, extract the data and store
     *
     * @param page downloaded web resource.
     */
    public void process(ResultItems page);
}
