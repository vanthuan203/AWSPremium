package com.nts.awspremium.crons;

import com.nts.awspremium.controller.OrderRunningController;
import com.nts.awspremium.repositories.OrderRunningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class RunOrderPending {
    @Autowired
    private OrderRunningRepository orderRunningRepository;
    @Autowired
    private OrderRunningController orderRunningController;
    @Autowired
    private Environment env;
    //@PostConstruct
    public void init() throws InterruptedException {
        try{
            if(Integer.parseInt(env.getProperty("server.port"))==80099){
                new Thread(() -> {
                    //Random rand =new Random();
                    while (true) {
                        try {
                            try {
                                Thread.sleep(60000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            orderRunningController.update_Running_Order_Pending();
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




