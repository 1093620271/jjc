package com.jjc.api.commons.convert;


import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author huoquan
 * @date 2018/8/23.
 */
public class CustomDateEdtor implements WebBindingInitializer {

    @Override
    public void initBinder(WebDataBinder webDataBinder) {
        // TODO Auto-generated method stub
        //转换日期格式
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
}
