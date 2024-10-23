package com.nts.awspremium.controller;

import com.nts.awspremium.model.Balance;
import com.nts.awspremium.model.OrderSpeedTrue;
import com.nts.awspremium.model.OrderTrue;
import com.nts.awspremium.repositories.BalanceRepository;
import com.nts.awspremium.repositories.IpV4Repository;
import com.nts.awspremium.repositories.VideoViewRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
public class AutoRunCheckProxy {
    @Autowired
    private ProxyController proxyController;
    @Autowired
    private IpV4Repository ipV4Repository;
    @Autowired
    private OrderSpeedTrue orderSpeedTrue;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private Environment env;
    @PostConstruct
    public void init() throws InterruptedException {
        try{
            if(Integer.parseInt(env.getProperty("server.port"))==8000) {
                int num_Cron= ipV4Repository.getMaxCron();
                for(int i=1;i<=num_Cron;i++) {
                    int finalI = i;
                    new Thread(() -> {
                        while (true) {
                            try {
                                proxyController.checkproxyMain(finalI);
                            } catch (Exception e) {
                                continue;
                            }
                        }
                    }).start();
                }
            }
        }catch (Exception e){

        }



    }
}




