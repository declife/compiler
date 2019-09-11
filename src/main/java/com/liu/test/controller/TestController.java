package com.liu.test.controller;


import com.liu.test.compile.service.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test")
public class TestController {

    @Autowired
    private service service;

    @RequestMapping(value = "/liu", method = RequestMethod.GET)
    public String hello() {
        if(SpringUtil.getTomcatClassLoader() == null) {
            SpringUtil.setTomcatClassLoader(Thread.currentThread().getContextClassLoader());
//            System.out.println(SpringUtil.getTomcatClassLoader());
        }
        return service.hello();
    }

}
