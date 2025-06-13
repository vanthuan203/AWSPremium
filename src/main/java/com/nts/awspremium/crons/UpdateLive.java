package com.nts.awspremium.crons;

import com.nts.awspremium.controller.VideoCommentController;
import com.nts.awspremium.controller.VideoViewController;
import com.nts.awspremium.model.OrderSpeedTrue;
import com.nts.awspremium.model.OrderTrue;
import com.nts.awspremium.repositories.VideoViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class UpdateLive {
    @Autowired
    private VideoCommentController videoCommentController;
    @Autowired
    private VideoViewController videoViewController;
    @Autowired
    private OrderTrue orderTrue;
    @Autowired
    private OrderSpeedTrue orderSpeedTrue;
    @Autowired
    private Environment env;
    //@PostConstruct
    public void init() throws InterruptedException {
        try{
            if(Integer.parseInt(env.getProperty("server.port"))==8000){
                new Thread(() -> {
                    //Random rand =new Random();
                    while (true) {
                        try {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            videoViewController.updateRunningLiveOrder();
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            videoCommentController.updateRunningLiveOrder();
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            videoCommentController.updateStateChatLilveAI();
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




