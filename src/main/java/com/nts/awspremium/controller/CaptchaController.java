package com.nts.awspremium.controller;


import com.nts.awspremium.Openai;
import com.nts.awspremium.repositories.OpenAiKeyRepository;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Random;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/captcha")
public class CaptchaController {
    @Autowired
    private Environment env;
    @Autowired
    private OpenAiKeyRepository openAiKeyRepository;

    @PostMapping(value = "/speech_to_text", produces = "application/hal_json;charset=utf8")
    ResponseEntity<String> speech_to_text(@RequestBody JSONObject body) {
        JSONObject resp = new JSONObject();

        try {
            if(1==1){
                resp.put("status", false);
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }
            Random ran= new Random();
            String stringrand="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefhijkprstuvwx0123456789";
            String name="";
            for(int i=0;i<10;i++){
                Integer ranver=ran.nextInt(stringrand.length());
                name=name+stringrand.charAt(ranver);
            }
            name="File/"+name+".mp3";
            File file = new File(name);
            FileUtils.copyURLToFile(new URL(body.get("link").toString()), file);
            String text=Openai.chatGPT1(name,openAiKeyRepository.get_OpenAI_Key());
            if(text!=null){
                resp.put("status", true);
                resp.put("text", text.trim());
            }else{
                resp.put("status", false);
            }

            file.delete();
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", false);
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }

    }

}
