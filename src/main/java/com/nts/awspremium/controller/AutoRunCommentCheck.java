package com.nts.awspremium.controller;

import com.nts.awspremium.model.OrderCommentTrue;
import com.nts.awspremium.model.OrderFollowerTrue;
import com.nts.awspremium.model.OrderSpeedTrue;
import com.nts.awspremium.model.OrderTrue;
import com.nts.awspremium.repositories.ChannelTikTokRepository;
import com.nts.awspremium.repositories.VideoCommentRepository;
import com.nts.awspremium.repositories.VideoViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Random;

@Component
public class AutoRunCommentCheck {
    @Autowired
    private VideoCommentRepository videoCommentRepository;

    @Autowired
    private ChannelTikTokRepository channelTikTokRepository;
    @Autowired
    private OrderCommentTrue orderCommentTrue;
    @Autowired
    private OrderFollowerTrue orderFollowerTrue;
    @Autowired
    private Environment env;
    @PostConstruct
    public void init() throws InterruptedException {
        try{
            Random rand =new Random();
            Thread.sleep(rand.nextInt(1000));
                new Thread(() -> {
                    while (true) {
                        try {
                            orderCommentTrue.setValue(videoCommentRepository.getListOrderTrueThreadON());
                            //orderFollowerTrue.setValue(channelTikTokRepository.getListOrderTrueThreadON());
                        } catch (Exception e) {
                            continue;
                        }
                    }
                }).start();
        }catch (Exception e){

        }



    }
}




