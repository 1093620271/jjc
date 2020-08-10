package com.jjc.api.commons.convert;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

import com.github.pagehelper.PageInfo;
import com.jjc.comm.common.sys.PageResult;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

public class MessageConverter extends FastJsonHttpMessageConverter {
    public MessageConverter() {
    }
    @Override
    protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if(obj instanceof PageInfo) {
            PageResult r = new PageResult();
            r.setBizData(((PageInfo) obj).getList());
            r.setTotal(((PageInfo) obj).getTotal());
            super.writeInternal(r, outputMessage);
        }else {
            super.writeInternal(obj, outputMessage);
        }
    }
}
