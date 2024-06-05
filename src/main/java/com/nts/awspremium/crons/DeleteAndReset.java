package com.nts.awspremium.crons;

import com.nts.awspremium.controller.*;
import com.nts.awspremium.model.OrderSpeedTrue;
import com.nts.awspremium.model.OrderTrue;
import com.nts.awspremium.repositories.HistoryTrafficRepository;
import com.nts.awspremium.repositories.VideoViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DeleteAndReset {
    @Autowired
    private HistoryTrafficController historyTrafficController;
    @Autowired
    private HistoryCommentController historyCommentController;
    @Autowired
    private HistoryViewController historyViewController;
    @Autowired
    private VideoCommentController videoCommentController;
    @Autowired
    private VideoViewController videoViewController;
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
                                Thread.sleep(60000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            historyCommentController.delcommentdone();
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            historyTrafficController.delthreadcron();
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            historyViewController.delthreadcron();
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            videoCommentController.DeleteOrderNotValidCron();
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            videoViewController.DeleteOrderNotValidCron();
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




