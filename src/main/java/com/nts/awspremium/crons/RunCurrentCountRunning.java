package com.nts.awspremium.crons;

import com.nts.awspremium.controller.OrderHistoryController;
import com.nts.awspremium.controller.OrderRunningController;
import com.nts.awspremium.model.OrderRunning;
import com.nts.awspremium.repositories.OrderRunningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class RunCurrentCountRunning {
    @Autowired
    private OrderRunningRepository orderRunningRepository;
    @Autowired
    private OrderRunningController orderRunningController;
    @Autowired
    private Environment env;
    @PostConstruct
    public void init() throws InterruptedException {
        try{
            if(Integer.parseInt(env.getProperty("server.port"))==8000){

                    for(int i=1;i<=30;i++){
                        int finalI = i;
                        new Thread(() -> {
                            //Random rand =new Random();
                            while (true) {
                                try {
                                    orderRunningController.update_Current_Total(finalI, 50);
                                    try {
                                        Thread.sleep(60000);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }).start();
                    }
                }
        }catch (Exception e){
        }



    }
}




