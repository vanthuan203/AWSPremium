package com.nts.awspremium.controller;

import com.nts.awspremium.model.*;
import com.nts.awspremium.repositories.ProxyRepository;
import com.nts.awspremium.repositories.VideoViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Random;

@Component
public class AutoRunProxyTrue {
    @Autowired
    private VideoViewRepository videoViewRepository;
    @Autowired
    private ProxyRepository proxyRepository;
    @Autowired
    private ProxyVNTrue proxyVNTrue;
    @Autowired
    private ProxyUSTrue proxyUSTrue;
    @Autowired
    private ProxyJPTrue proxyJPTrue;
    @Autowired
    private ProxyKRTrue proxyKRTrue;
    @Autowired
    private ProxyTestTrue proxyTestTrue;
    @Autowired
    private ProxyVultrTrue proxyVultrTrue;
    @Autowired
    private Environment env;
    @PostConstruct
    public void init() throws InterruptedException {
        try{
            if(Integer.parseInt(env.getProperty("server.port"))!=8010) {
                new Thread(() -> {
                    Random rand = new Random();
                    while (true) {
                        try {
                            proxyVNTrue.setValue(proxyRepository.getProxyTrue("vn"));
                            proxyTestTrue.setValue(proxyRepository.getProxyTrue("test1"));
                            proxyUSTrue.setValue(proxyRepository.getProxyTrue("us"));
                            proxyKRTrue.setValue(proxyRepository.getProxyKRTrue());
                            proxyJPTrue.setValue(proxyRepository.getProxyTrue("jp"));
                            proxyVultrTrue.setValue(proxyRepository.getProxyALLRTrue());
                            try {
                                Thread.sleep(60000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        } catch (Exception e) {
                            continue;
                        }
                    }
                }).start();
            }
        }catch (Exception e){

        }



    }
}




