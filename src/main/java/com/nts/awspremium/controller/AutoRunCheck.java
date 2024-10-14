package com.nts.awspremium.controller;

import com.nts.awspremium.model.OrderSpeedTimeTrue;
import com.nts.awspremium.model.OrderSpeedTrue;
import com.nts.awspremium.model.OrderTrue;
import com.nts.awspremium.repositories.VideoViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Random;

@Component
public class AutoRunCheck {
    @Autowired
    private VideoViewRepository videoViewRepository;
    @Autowired
    private OrderTrue orderTrue;
    @Autowired
    private OrderSpeedTrue orderSpeedTrue;
    @Autowired
    private OrderSpeedTimeTrue orderSpeedTimeTrue;
    @Autowired
    private Environment env;
    @PostConstruct
    public void init() throws InterruptedException {
        try{
            Random rand =new Random();
            Thread.sleep(rand.nextInt(1000));
                new Thread(() -> {
                    //Random rand =new Random();
                    while (true) {
                        try {
                            try {
                                Thread.sleep(rand.nextInt(150));
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            orderTrue.setValue(videoViewRepository.getListOrderTrueThreadON());
                            orderSpeedTrue.setValue(videoViewRepository.getListOrderSpeedTrueThreadONTEST());
                            orderSpeedTimeTrue.setValue(videoViewRepository.getListOrderSpeedTimeTrueThread());
                        } catch (Exception e) {
                            continue;
                        }
                    }
                }).start();
        }catch (Exception e){

        }



    }
}




