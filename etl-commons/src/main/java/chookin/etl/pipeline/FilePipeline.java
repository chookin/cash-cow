package chookin.etl.pipeline;
import chookin.etl.common.ResultItems;
import chookin.etl.common.UrlHelper;
import chookin.utils.io.FileHelper;

import java.io.IOException;

/**
 * Created by zhuyin on 3/2/15.
 */
public class FilePipeline implements Pipeline {

    String getFileName(ResultItems resultItems){
        return UrlHelper.getFilePath(resultItems.getRequest().getUrl());
    }
    Pipeline saveDocument(ResultItems resultItems) throws IOException {
        if(resultItems.getSpider().getValidateMilliSeconds() <= 0L){
            return this;
        }
        if (resultItems.isCacheUsed()) {
            return this;
        }
        if(resultItems.getResource() == null){
            return this;
        }
        FileHelper.save(resultItems.getResource().toString(), getFileName(resultItems));
        return this;
    }

    boolean saveByteArray(ResultItems resultItems) throws IOException {
        if(resultItems.isCacheUsed()){
            return false;
        }
        byte[] bytes = (byte[]) resultItems.getResource();
        if(bytes.length < byteMinSize){
            return false;
        }
        FileHelper.save(bytes, getFileName(resultItems));
        return true;
    }

    /**
     * Ignore the resource if its' byte length less than minsize
     */
    private long byteMinSize;
    public FilePipeline setByteMinSize(long byteMinSize){
        this.byteMinSize = byteMinSize;
        return this;
    }
    @Override
    public void process(ResultItems resultItems) throws IOException {
        switch (resultItems.getRequest().getTarget()){
            case ByteArray:
                saveByteArray(resultItems);
                break;
            default:
                saveDocument(resultItems);
        }
    }

    @Override
    public void close() {

    }
}
