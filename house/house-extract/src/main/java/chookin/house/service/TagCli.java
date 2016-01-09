package chookin.house.service;


/**
 * Created by zhuyin on 3/28/15.
 */
public class TagCli extends cmri.utils.lang.BaseOper {
    @Override
    public boolean action() {
        if(getOptions().exists("dump-config")){
            return new ConfigProcess().setArgs(getOptions().options()).action();
        }
        return false;
    }

    public static void main(String[] args){
        new TagCli().setArgs(args).action();
    }
}
