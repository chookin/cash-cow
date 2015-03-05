package chookin.etl.pipeline;

import chookin.etl.common.ResultItems;

import java.io.IOException;
import java.util.Map;

/**
 * Created by zhuyin on 3/3/15.
 */
public class ConsolePipeline implements Pipeline {
    @Override
    public void process(ResultItems resultItems) throws IOException {
        if (resultItems.isSkip() || resultItems.getResource() == null){
            return;
        }
        System.out.println("get page: " + resultItems.getRequest().getUrl());
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            System.out.println(entry.getKey() + ":\t" + entry.getValue());
        }
    }

    @Override
    public void close() {

    }
}
