package chookin.stock.controller;

import com.jfinal.core.Controller;

/**
 * Created by chookin on 10/5/14.
 */
public class AboutController extends Controller {

    public void index(){
        renderText("Hello world. chookin");
    }
}