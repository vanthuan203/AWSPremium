package com.nts.awspremium.crons;

import com.nts.awspremium.controller.OrderHistoryController;
import com.nts.awspremium.controller.OrderRunningController;
import com.nts.awspremium.repositories.OrderHistoryRepository;
import com.nts.awspremium.repositories.OrderRunningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class RunCurrentCount {
    @Autowired
    private OrderRunningRepository orderRunningRepository;
    @Autowired
    private OrderHistoryController orderHistoryController;
    @Autowired
    private Environment env;
    @PostConstruct
    public void init() throws InterruptedException {
        try{
            if(Integer.parseInt(env.getProperty("server.port"))==8000){
                new Thread(() -> {
                    //Random rand =new Random();
                    while (true) {
                        try {
                            try {
                                Thread.sleep(15000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            orderHistoryController.update_Current_Count();
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




