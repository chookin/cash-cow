package chookin.etl.pipeline;

import chookin.etl.common.ResultItems;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by zhuyin on 3/2/15.
 */
public interface Pipeline extends Closeable{
    void process(ResultItems resultItems) throws IOException;
}
