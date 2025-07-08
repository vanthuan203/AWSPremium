package com.nts.awspremium.crons;

import com.nts.awspremium.controller.VpsController;
import com.nts.awspremium.controller.WebTrafficController;
import com.nts.awspremium.model.OrderSpeedTrue;
import com.nts.awspremium.model.OrderTrue;
import com.nts.awspremium.model.Setting;
import com.nts.awspremium.repositories.SettingRepository;
import com.nts.awspremium.repositories.VideoViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class ResetBAS {
    @Autowired
    private VpsController vpsController;
    @Autowired
    private SettingRepository settingRepository;
    @Autowired
    private OrderTrue orderTrue;
    @Autowired
    private OrderSpeedTrue orderSpeedTrue;
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
                                Thread.sleep(5*60000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            Setting setting=settingRepository.getSettingId1();
                            vpsController.resetBasNoCheckByCron(setting.getLimit_vps_reset(),3);
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            vpsController.resetBasDailyByCron(50);
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




