package com.nts.awspremium.controller;

import com.nts.awspremium.StringUtils;
import com.nts.awspremium.model.*;
import com.nts.awspremium.model_system.OrderThreadCheck;
import com.nts.awspremium.platform.youtube.YoutubeTask;
import com.nts.awspremium.platform.youtube.YoutubeUpdate;
import com.nts.awspremium.repositories.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/historyview")
public class HistoryViewController {

    @Autowired
    private YoutubeUpdate youtubeUpdate;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private FingerprintsPCRepository fingerprintsPCRepository;
    @Autowired
    private ProxyRepository proxyRepository;
    @Autowired
    private ProxySettingRepository proxySettingRepository;
    @Autowired
    private VideoViewRepository videoViewRepository;
    @Autowired
    private DataCommentRepository dataCommentRepository;
    @Autowired
    private DataReplyCommentRepository dataReplyCommentRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private HistoryViewRepository historyViewRepository;

    @Autowired
    private HistoryCommentRepository historyCommentRepository;
    @Autowired
    private DataOrderRepository dataOrderRepository;
    @Autowired
    private HistoryViewSumRepository historyViewSumRepository;
    @Autowired
    private OrderTrue orderTrue;
    @Autowired
    private OrderSpeedTrue orderSpeedTrue;
    @Autowired
    private OrderSpeedTimeTrue orderSpeedTimeTrue;
    @Autowired
    private ProxyVNTrue proxyVNTrue;
    @Autowired
    private ProxyUSTrue proxyUSTrue;
    @Autowired
    private ProxyKRTrue proxyKRTrue;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private ServiceSMMRepository serviceSMMRepository;

    @Autowired
    private IpV4Repository ipV4Repository;
    @Autowired
    private SettingRepository settingRepository;
    @Autowired
    private OrderThreadCheck orderThreadCheck;
    @Autowired
    private VpsRepository vpsRepository;
    @Autowired
    private YoutubeTask youtubeTask;
    @Autowired
    private TaskPriorityRepository taskPriorityRepository;
    @Autowired
    private OrderRunningRepository orderRunningRepository;

    @Autowired
    private HistorySumRepository historySumRepository;
    @Autowired
    private LogErrorRepository logErrorRepository;
    /*
         @GetMapping(value = "get", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> get(@RequestParam(defaultValue = "") String username, @RequestParam(defaultValue = "") String vps, @RequestParam(defaultValue = "0") Integer buffh) {
        JSONObject resp = new JSONObject();
        if (username.length() == 0) {
            resp.put("status", "fail");
            resp.put("message", "Username không để trống");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        if(vpsRepository.checkVpsCmtTrue(vps.trim())==0){
            resp.put("status", "fail");
            resp.put("message", "Vps không chạy view!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }
        Random ran = new Random();
        try {
            Thread.sleep(ran.nextInt(1000));
            Long historieId = historyViewRepository.getId(username);
            List<VideoView> videos = null;
            if (historieId == null) {
                resp.put("status", "fail");
                resp.put("message", "Username không tồn tại!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
            } else {
                List<HistoryView> histories = historyViewRepository.getHistoriesById(historieId);
                int checkRedirect = 0;
                if (buffh == 1) {
                    videos = videoViewRepository.getvideoBuffHByGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderTrue.getValue());
                } else {
                    if (histories.get(0).getGeo().trim().equals("vn")?(ran.nextInt(1000) < settingRepository.getRedirectVN()):(ran.nextInt(1000) < settingRepository.getRedirectUS())) {
                        checkRedirect = 1;
                        videos = videoViewRepository.getvideoBuffHByGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderTrue.getValue());
                        if (videos.size() == 0) {
                            videos = videoViewRepository.getvideoViewByGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderTrue.getValue());
                        }
                    } else {
                        videos = videoViewRepository.getvideoViewByGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderTrue.getValue());
                    }
                }
                if (videos.size() > 0) {
                    histories.get(0).setTimeget(System.currentTimeMillis());
                    histories.get(0).setVideoid(videos.get(0).getVideoid());
                    histories.get(0).setOrderid(videos.get(0).getOrderid());
                    histories.get(0).setChannelid(videos.get(0).getChannelid());
                } else if (buffh == 0 && (histories.get(0).getGeo().trim().equals("vn")?(ran.nextInt(1000) < settingRepository.getRedirectVN()*2):(ran.nextInt(1000) < settingRepository.getRedirectUS()*2)) && checkRedirect == 0) {
                    videos = videoViewRepository.getvideoBuffHByGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderTrue.getValue());
                    if (videos.size() > 0) {
                        histories.get(0).setTimeget(System.currentTimeMillis());
                        histories.get(0).setVideoid(videos.get(0).getVideoid());
                        histories.get(0).setOrderid(videos.get(0).getOrderid());
                        histories.get(0).setChannelid(videos.get(0).getChannelid());
                    } else {
                        videos = videoViewRepository.getvideoViewByGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderSpeedTrue.getValue());
                        if (videos.size() > 0) {
                            histories.get(0).setTimeget(System.currentTimeMillis());
                            histories.get(0).setVideoid(videos.get(0).getVideoid());
                            histories.get(0).setOrderid(videos.get(0).getOrderid());
                            histories.get(0).setChannelid(videos.get(0).getChannelid());
                        } else if(settingRepository.getRandView()>=ran.nextInt(1000)) {
                            try{
                                videos = videoViewRepository.getvideoViewRandByGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo());
                                if (videos.size() > 0) {
                                    histories.get(0).setTimeget(System.currentTimeMillis());
                                    histories.get(0).setVideoid(videos.get(0).getVideoid());
                                    histories.get(0).setOrderid(videos.get(0).getOrderid());
                                    histories.get(0).setChannelid(videos.get(0).getChannelid());
                                } else {
                                    histories.get(0).setTimeget(System.currentTimeMillis());
                                    historyViewRepository.save(histories.get(0));
                                    resp.put("status", "fail");
                                    resp.put("username", histories.get(0).getUsername());
                                    resp.put("fail", "video");
                                    resp.put("message", "Không còn video để view!");
                                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                                }
                            }catch (Exception e){
                                histories.get(0).setTimeget(System.currentTimeMillis());
                                historyViewRepository.save(histories.get(0));
                                resp.put("status", "fail");
                                resp.put("username", histories.get(0).getUsername());
                                resp.put("fail", "video");
                                resp.put("message", "Không còn video để view!");
                                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                            }

                        }else{
                            histories.get(0).setTimeget(System.currentTimeMillis());
                            historyViewRepository.save(histories.get(0));
                            resp.put("status", "fail");
                            resp.put("username", histories.get(0).getUsername());
                            resp.put("fail", "video");
                            resp.put("message", "Không còn video để view!");
                            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                        }
                    }

                } else{
                    videos = videoViewRepository.getvideoViewByGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderSpeedTrue.getValue());
                    if (videos.size() > 0) {
                        histories.get(0).setTimeget(System.currentTimeMillis());
                        histories.get(0).setVideoid(videos.get(0).getVideoid());
                        histories.get(0).setOrderid(videos.get(0).getOrderid());
                        histories.get(0).setChannelid(videos.get(0).getChannelid());
                    } else if(settingRepository.getRandView()>=ran.nextInt(1000)) {
                        try{
                            videos = videoViewRepository.getvideoViewRandByGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo());
                            if (videos.size() > 0) {
                                histories.get(0).setTimeget(System.currentTimeMillis());
                                histories.get(0).setVideoid(videos.get(0).getVideoid());
                                histories.get(0).setOrderid(videos.get(0).getOrderid());
                                histories.get(0).setChannelid(videos.get(0).getChannelid());
                            } else {
                                histories.get(0).setTimeget(System.currentTimeMillis());
                                historyViewRepository.save(histories.get(0));
                                resp.put("status", "fail");
                                resp.put("username", histories.get(0).getUsername());
                                resp.put("fail", "video");
                                resp.put("message", "Không còn video để view!");
                                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                            }
                        }catch (Exception e){
                            histories.get(0).setTimeget(System.currentTimeMillis());
                            historyViewRepository.save(histories.get(0));
                            resp.put("status", "fail");
                            resp.put("username", histories.get(0).getUsername());
                            resp.put("fail", "video");
                            resp.put("message", "Không còn video để view!");
                            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                        }

                    }else{
                        histories.get(0).setTimeget(System.currentTimeMillis());
                        historyViewRepository.save(histories.get(0));
                        resp.put("status", "fail");
                        resp.put("username", histories.get(0).getUsername());
                        resp.put("fail", "video");
                        resp.put("message", "Không còn video để view!");
                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                    }
                }
                Service service = serviceRepository.getInfoService(videos.get(0).getService());

                histories.get(0).setTimeget(System.currentTimeMillis());
                histories.get(0).setRunning(1);
                historyViewRepository.save(histories.get(0));
                resp.put("live", service.getLive() == 1 ? "true" : "fail");
                resp.put("channel_id", videos.get(0).getChannelid());
                resp.put("status", "true");
                resp.put("video_id", videos.get(0).getVideoid());
                resp.put("video_title", videos.get(0).getVideotitle());
                resp.put("username", histories.get(0).getUsername());
                resp.put("geo", accountRepository.getGeoByUsername(username.trim()));
                resp.put("like", "fail");
                resp.put("sub", "fail");
                if(service.getNiche()==1){
                    String[] nicheArr = service.getKeyniche().split(",");
                    resp.put("niche_key", nicheArr[ran.nextInt(nicheArr.length)]);
                }else{
                    resp.put("niche_key","");
                }
                String list_key = dataOrderRepository.getListKeyByOrderid(videos.get(0).getOrderid());
                String key = "";
                if (list_key != null && list_key.length() != 0) {
                    String[] keyArr = list_key.split(",");
                    key = keyArr[ran.nextInt(keyArr.length)];
                }
                resp.put("suggest_type", "fail");
                resp.put("suggest_key", key.length() == 0 ? videos.get(0).getVideotitle() : key);
                resp.put("suggest_video", "");
                List<String> arrSource = new ArrayList<>();
                for (int i = 0; i < service.getSuggest(); i++) {
                    arrSource.add("suggest");
                }
                for (int i = 0; i < service.getSearch(); i++) {
                    arrSource.add("search");
                }
                for (int i = 0; i < service.getDtn(); i++) {
                    arrSource.add("dtn");
                }
                for (int i = 0; i < service.getEmbed(); i++) {
                    arrSource.add("embed");
                }
                for (int i = 0; i < service.getDirect(); i++) {
                    arrSource.add("direct");
                }
                for (int i = 0; i < service.getExternal(); i++) {
                    arrSource.add("external");
                }
                for (int i = 0; i < service.getPlaylists(); i++) {
                    arrSource.add("playlists");
                }
                String source_view = arrSource.get(ran.nextInt(arrSource.size())).trim();
                if (source_view.equals("suggest") && service.getType().equals("Special")) {
                    resp.put("suggest_type", "true");
                } else if (source_view.equals("search") && service.getType().equals("Special")) {
                    resp.put("video_title", key.length() == 0 ? videos.get(0).getVideotitle() : key);
                }
                resp.put("source", source_view);
                if(source_view.equals("embed")){
                    resp.put("suggest_video", videos.get(0).getLink());
                }

                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                if (service.getMintime() != service.getMaxtime() && service.getLive() == 0) {
                    if (videos.get(0).getDuration() > service.getMaxtime() * 60) {
                        resp.put("video_duration", service.getMintime() * 60 + (service.getMintime() < service.getMaxtime() ? (ran.nextInt((service.getMaxtime() - service.getMintime()) * 45) + (service.getMaxtime() >= 10 ? 30 : 0)) : 0));
                    } else {
                        resp.put("video_duration", service.getMintime() * 60 < videos.get(0).getDuration() ? (service.getMintime() * 60 + ran.nextInt((int)(videos.get(0).getDuration() - service.getMintime() * 60))) : videos.get(0).getDuration());
                    }
                } else if (service.getLive() == 1) {
                    int min_check = (int) ((service.getMintime() * 0.15) > 30 ? 30 : (service.getMintime() * 0.15));
                    if ((System.currentTimeMillis() - videos.get(0).getTimestart()) / 1000 / 60 < min_check) {
                        resp.put("video_duration", service.getMintime() * 60 + (service.getMintime() >= 15 ? 120 : 0));
                    } else {
                        int time_live = videos.get(0).getMinstart() - (int) ((System.currentTimeMillis() - videos.get(0).getTimestart()) / 1000 / 60);
                        resp.put("video_duration", (time_live > 0 ? time_live : 0) * 60 + (service.getMintime() >= 15 ? 120 : 0));
                    }
                } else {
                    if (videos.get(0).getDuration() > service.getMaxtime() * 60) {
                        resp.put("video_duration", service.getMintime() * 60 + (service.getMintime() < service.getMaxtime() ? (ran.nextInt((service.getMaxtime() - service.getMintime()) * 60 + service.getMaxtime() >= 10 ? 60 : 0)) : 0));
                    } else {
                        resp.put("video_duration", videos.get(0).getDuration());
                    }
                }
                if(((Integer.parseInt(resp.get("video_duration").toString())<6||Integer.parseInt(resp.get("video_duration").toString())>20))&&service.getMaxtime()==1){
                    resp.put("video_duration",ran.nextInt(14)+6);
                }
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }

        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("fail", "sum");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }
     */
     @GetMapping(value = "get", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> get(@RequestParam(defaultValue = "") String username, @RequestParam(defaultValue = "") String vps, @RequestParam(defaultValue = "0") Integer buffh) {
        JSONObject resp = new JSONObject();
        if (username.length() == 0) {
            resp.put("status", "fail");
            resp.put("message", "Username không để trống");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
         Vps vps_check=vpsRepository.getVpsByName(vps.trim());
         if(vps_check==null){
             resp.put("status", "fail");
             resp.put("message", "Vps không tồn tại");
             return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
         }

        Random ran = new Random();
        try {
            //Thread.sleep(ran.nextInt(1000));
/*
            if(ran.nextInt(100)<60){
                resp.put("status", "fail");
                resp.put("message", "Bỏ qua nhiệm vụ");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }

 */
            Long historieId = historyViewRepository.getId(username);
            List<VideoView> videos = null;
            if (historieId == null) {
                resp.put("status", "fail");
                resp.put("message", "Username không tồn tại!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
            } else {
                List<HistoryView> histories = historyViewRepository.getHistoriesById(historieId);

                if(!vps_check.getVpsoption().equals("smm")&&((System.currentTimeMillis()-histories.get(0).getTask_time())/1000<= (10+ran.nextInt(5)))){
                    Thread.sleep(ran.nextInt(1000));
                    resp.put("status", "fail");
                    resp.put("username", histories.get(0).getUsername());
                    resp.put("fail", "video");
                    resp.put("message", "Không còn video để view!");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }else  if(vps_check.getVpsoption().equals("smm")&&((System.currentTimeMillis()-histories.get(0).getTask_time())/1000<= (60+ran.nextInt(5)))){
                    Thread.sleep(ran.nextInt(500));
                    resp.put("status", "fail");
                    resp.put("username", histories.get(0).getUsername());
                    resp.put("fail", "video");
                    resp.put("message", "Không còn video để view!");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
                Map<String, Object> get_task =null;
                if(vps_check.getVpsoption().equals("smm")){
                    List<TaskPriority> priorityTasks =taskPriorityRepository.get_Priority_Task_By_Platform("youtube");
                    List<String> arrTask = new ArrayList<>();

                    for(int i=0;i<priorityTasks.size();i++){
                        for (int j = 0; j < priorityTasks.get(i).getPriority(); j++) {
                            arrTask.add(priorityTasks.get(i).getTask());
                        }
                    }
                    while (arrTask.size()>0){
                        String task = arrTask.get(ran.nextInt(arrTask.size())).trim();

                        while(arrTask.remove(task)) {}
                        if(task.equals("youtube_view")){
                            get_task=youtubeTask.youtube_view(histories.get(0).getUsername(),"auto");
                        }else if(task.equals("youtube_like")){
                            get_task=youtubeTask.youtube_like(histories.get(0).getUsername(),"auto");
                        }else if(task.equals("youtube_subscriber")){
                            get_task=youtubeTask.youtube_subscriber(histories.get(0).getUsername(),"auto");
                        }
                        if(get_task!=null?get_task.get("status").equals(true):false){
                            break;
                        }
                    }
                    if(get_task==null){
                        resp.put("status", "fail");
                        resp.put("fail", "video");
                        resp.put("message", "Không còn video để view!");
                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                    }else if(get_task.get("status").equals(false)){
                        resp.put("status", "fail");
                        resp.put("fail", "video");
                        resp.put("message", "Không còn video để view!");
                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                    }
                    Map<String, Object>  dataJson= (Map<String, Object>) get_task.get("data");

                    Thread.sleep(ran.nextInt(150));
                    if(!orderThreadCheck.getValue().contains(dataJson.get("order_id").toString())){
                        resp.put("status", "fail");
                        resp.put("fail", "video");
                        resp.put("message", "Không còn video để view!");
                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                    }

                    histories.get(0).setTimeget(System.currentTimeMillis());
                    histories.get(0).setVideoid( dataJson.get("video_id").toString());
                    histories.get(0).setOrderid(Long.parseLong(dataJson.get("order_id").toString()));
                    histories.get(0).setChannelid(dataJson.get("channel_id").toString());
                    histories.get(0).setTimeget(System.currentTimeMillis());
                    histories.get(0).setRunning(1);
                    historyViewRepository.save(histories.get(0));

                    resp.put("status","true");
                    resp.put("live",dataJson.get("live").toString());
                    resp.put("channel_id", dataJson.get("channel_id").toString());
                    resp.put("video_id", dataJson.get("video_id").toString());
                    resp.put("video_title", dataJson.get("video_title").toString());
                    resp.put("username", histories.get(0).getUsername());
                    resp.put("service_id", Integer.parseInt(dataJson.get("service_id").toString()));
                    resp.put("geo",dataJson.get("geo").toString());
                    resp.put("like", dataJson.get("like").toString());
                    resp.put("sub", dataJson.get("sub").toString());
                    resp.put("niche_key",dataJson.get("niche_key").toString());

                    resp.put("suggest_type", dataJson.get("suggest_type").toString());
                    resp.put("suggest_key", dataJson.get("suggest_key").toString());
                    resp.put("suggest_video", dataJson.get("suggest_video").toString());
                    resp.put("suggest_type", dataJson.get("suggest_type").toString());

                    resp.put("source", dataJson.get("source").toString());
                    resp.put("suggest_video", dataJson.get("suggest_video").toString());
                    resp.put("video_duration",Integer.parseInt(dataJson.get("video_duration").toString()));
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);


                }else{
                    String geo_rand=histories.get(0).getGeo().trim();
                    if(histories.get(0).getGeo_rand()!=null){
                        if(histories.get(0).getGeo_rand().length()!=0){
                            geo_rand=histories.get(0).getGeo_rand().trim();
                        }
                    }
                    if (buffh == 1) {
                        videos = videoViewRepository.getvideoBuffHByGeo(geo_rand, histories.get(0).getListvideo(), orderTrue.getValue());
                    } else {
                        if (geo_rand.equals("vn")?(ran.nextInt(1000) < settingRepository.getRedirectVN()):(ran.nextInt(1000) < settingRepository.getRedirectUS())) {
                            videos = videoViewRepository.getvideoBuffHByGeo(geo_rand, histories.get(0).getListvideo(), orderTrue.getValue());
                            if (videos.size() == 0) {
                                videos = videoViewRepository.getvideoViewByGeo(geo_rand, histories.get(0).getListvideo(), orderTrue.getValue());
                            }
                        } else {
                            videos = videoViewRepository.getvideoViewByGeo(geo_rand, histories.get(0).getListvideo(), orderTrue.getValue());
                        }
                    }
                    if (videos.size() > 0) {
                        histories.get(0).setTimeget(System.currentTimeMillis());
                        histories.get(0).setVideoid(videos.get(0).getVideoid());
                        histories.get(0).setOrderid(videos.get(0).getOrderid());
                        histories.get(0).setChannelid(videos.get(0).getChannelid());
                    } else if (buffh == 0) {
                        videos = videoViewRepository.getvideoBuffHByGeo(geo_rand, histories.get(0).getListvideo(), orderTrue.getValue());
                        if (videos.size() > 0) {
                            histories.get(0).setTimeget(System.currentTimeMillis());
                            histories.get(0).setVideoid(videos.get(0).getVideoid());
                            histories.get(0).setOrderid(videos.get(0).getOrderid());
                            histories.get(0).setChannelid(videos.get(0).getChannelid());
                        } else {
                            videos = videoViewRepository.getvideoViewByGeo(geo_rand, histories.get(0).getListvideo(), orderSpeedTimeTrue.getValue());
                            if (videos.size() > 0) {
                                histories.get(0).setTimeget(System.currentTimeMillis());
                                histories.get(0).setVideoid(videos.get(0).getVideoid());
                                histories.get(0).setOrderid(videos.get(0).getOrderid());
                                histories.get(0).setChannelid(videos.get(0).getChannelid());
                            }else{
                                videos = videoViewRepository.getvideoViewByGeo(geo_rand, histories.get(0).getListvideo(), orderSpeedTrue.getValue());
                                if (videos.size() > 0) {
                                    histories.get(0).setTimeget(System.currentTimeMillis());
                                    histories.get(0).setVideoid(videos.get(0).getVideoid());
                                    histories.get(0).setOrderid(videos.get(0).getOrderid());
                                    histories.get(0).setChannelid(videos.get(0).getChannelid());
                                }else{
                                    histories.get(0).setTimeget(System.currentTimeMillis());
                                    histories.get(0).setTask_time(System.currentTimeMillis());
                                    historyViewRepository.save(histories.get(0));
                                    resp.put("status", "fail");
                                    resp.put("username", histories.get(0).getUsername());
                                    resp.put("fail", "video");
                                    resp.put("message", "Không còn video để view!");
                                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                                }
                            }
                        }
                    } else{
                        videos = videoViewRepository.getvideoViewByGeo(geo_rand, histories.get(0).getListvideo(), orderSpeedTimeTrue.getValue());
                        if (videos.size() > 0) {
                            histories.get(0).setTimeget(System.currentTimeMillis());
                            histories.get(0).setVideoid(videos.get(0).getVideoid());
                            histories.get(0).setOrderid(videos.get(0).getOrderid());
                            histories.get(0).setChannelid(videos.get(0).getChannelid());
                        }else{
                            videos = videoViewRepository.getvideoViewByGeo(geo_rand, histories.get(0).getListvideo(), orderSpeedTrue.getValue());
                            if (videos.size() > 0) {
                                histories.get(0).setTimeget(System.currentTimeMillis());
                                histories.get(0).setVideoid(videos.get(0).getVideoid());
                                histories.get(0).setOrderid(videos.get(0).getOrderid());
                                histories.get(0).setChannelid(videos.get(0).getChannelid());
                            }else{
                                histories.get(0).setTimeget(System.currentTimeMillis());
                                histories.get(0).setTask_time(System.currentTimeMillis());
                                historyViewRepository.save(histories.get(0));
                                resp.put("status", "fail");
                                resp.put("username", histories.get(0).getUsername());
                                resp.put("fail", "video");
                                resp.put("message", "Không còn video để view!");
                                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                            }
                        }
                    }
                    Thread.sleep(150+ran.nextInt(200));
                    if(!orderSpeedTimeTrue.getValue().contains(videos.get(0).getOrderid().toString()) && !orderTrue.getValue().contains(videos.get(0).getOrderid().toString()) && !orderSpeedTrue.getValue().contains(videos.get(0).getOrderid().toString())){
                        histories.get(0).setTimeget(System.currentTimeMillis());
                        histories.get(0).setTask_time(System.currentTimeMillis());
                        histories.get(0).setVideoid("");
                        histories.get(0).setOrderid(0L);
                        histories.get(0).setChannelid("");
                        histories.get(0).setRunning(0);
                        historyViewRepository.save(histories.get(0));
                        resp.put("status", "fail");
                        resp.put("username", histories.get(0).getUsername());
                        resp.put("fail", "video");
                        resp.put("message", "Không còn video để view!");
                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                    }
                    Service service = serviceRepository.getInfoService(videos.get(0).getService());

                    histories.get(0).setTimeget(System.currentTimeMillis());
                    histories.get(0).setRunning(1);
                    historyViewRepository.save(histories.get(0));
                    resp.put("live", service.getLive() == 1 ? "true" : "fail");
                    resp.put("channel_id", videos.get(0).getChannelid());
                    resp.put("status", "true");
                    resp.put("video_id", videos.get(0).getVideoid());
                    resp.put("video_title", videos.get(0).getVideotitle());
                    resp.put("username", histories.get(0).getUsername());
                    resp.put("service_id", service.getService());
                    resp.put("geo", accountRepository.getGeoByUsername(username.trim()));
                    if(videos.get(0).getService()==801){
                        resp.put("like", "true");
                    }else{
                        resp.put("like", "fail");
                    }
                    if(videos.get(0).getService()==802){
                        resp.put("sub", "true");
                    }else{
                        resp.put("sub", "fail");
                    }
                    if(service.getNiche()==1){
                        String[] nicheArr = service.getKeyniche().split(",");
                        resp.put("niche_key", nicheArr[ran.nextInt(nicheArr.length)]);
                    }else{
                        resp.put("niche_key","");
                    }
                    String list_key = dataOrderRepository.getListKeyByOrderid(videos.get(0).getOrderid());
                    String key = "";
                    if (list_key != null && list_key.length() != 0) {
                        String[] keyArr = list_key.split(",");
                        key = keyArr[ran.nextInt(keyArr.length)];
                    }
                    resp.put("suggest_type", "fail");
                    resp.put("suggest_key", key.length() == 0 ? videos.get(0).getVideotitle() : key);
                    resp.put("suggest_video", "");
                    List<String> arrSource = new ArrayList<>();
                    for (int i = 0; i < service.getSuggest(); i++) {
                        arrSource.add("suggest");
                    }
                    for (int i = 0; i < service.getSearch(); i++) {
                        arrSource.add("search");
                    }
                    for (int i = 0; i < service.getDtn(); i++) {
                        arrSource.add("dtn");
                    }
                    for (int i = 0; i < service.getEmbed(); i++) {
                        arrSource.add("embed");
                    }
                    for (int i = 0; i < service.getDirect(); i++) {
                        arrSource.add("direct");
                    }
                    for (int i = 0; i < service.getExternal(); i++) {
                        arrSource.add("external");
                    }
                    for (int i = 0; i < service.getPlaylists(); i++) {
                        arrSource.add("playlists");
                    }
                    String source_view = arrSource.get(ran.nextInt(arrSource.size())).trim();
                    if (source_view.equals("suggest") && service.getType().equals("Special")) {
                        resp.put("suggest_type", "true");
                    } else if (source_view.equals("search") && service.getType().equals("Special")) {
                        resp.put("video_title", key.length() == 0 ? videos.get(0).getVideotitle() : key);
                    }
                    resp.put("source", source_view);
                    if(source_view.equals("embed")){
                        resp.put("suggest_video", videos.get(0).getLink());
                    }

                    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                    if (service.getMintime() != service.getMaxtime()) {
                        if (videos.get(0).getDuration() > service.getMaxtime() * 60) {
                            //resp.put("video_duration", service.getMintime() * 60 + (service.getMintime() < service.getMaxtime() ? (ran.nextInt((service.getMaxtime() - service.getMintime()) * 45) + (service.getMaxtime() >= 10 ? 30 : 0)) : 0));
                            resp.put("video_duration", service.getMintime() * 60 +ran.nextInt((service.getMaxtime()-service.getMintime()==1?30:60)));
                        } else {
                            //resp.put("video_duration", service.getMintime() * 60 < videos.get(0).getDuration() ? (service.getMintime() * 60 + ran.nextInt((int)(videos.get(0).getDuration() - service.getMintime() * 60))) : videos.get(0).getDuration());
                            resp.put("video_duration", service.getMintime() * 60 < videos.get(0).getDuration() ? (service.getMintime() * 60 + ran.nextInt((service.getMaxtime()-service.getMintime()==1?30:60))) : videos.get(0).getDuration());
                        }
                    }else {
                        if (videos.get(0).getDuration() > service.getMaxtime() * 60) {
                            //resp.put("video_duration", service.getMintime() * 60 + (service.getMintime() < service.getMaxtime() ? (ran.nextInt((service.getMaxtime() - service.getMintime()) * 60 + service.getMaxtime() >= 10 ? 60 : 0)) : 0));
                            resp.put("video_duration", service.getMintime() * 60);
                        } else {
                            resp.put("video_duration", videos.get(0).getDuration());
                        }
                    }
                    if(((Integer.parseInt(resp.get("video_duration").toString())<6||Integer.parseInt(resp.get("video_duration").toString())>20))&&service.getMintime()==0){
                        resp.put("video_duration",ran.nextInt(14)+6);
                    }
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }


            }

        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("fail", "sum");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }




    @GetMapping(value = "getNoCheckAcc", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> getNoCheckAcc(@RequestParam(defaultValue = "") String username) {
        JSONObject resp = new JSONObject();
        if (username.length() == 0) {
            resp.put("status", "fail");
            resp.put("message", "Username không để trống");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        Random ran = new Random();
        try {
            List<VideoView> videos = videoViewRepository.getvideoByGeoTraffic();
            if(videos.size()==0) {
                resp.put("status", "fail");
                resp.put("username", username.trim());
                resp.put("fail", "video");
                resp.put("message", "Không còn video để view!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }
            Service service = serviceRepository.getInfoService(videos.get(0).getService());
            resp.put("live", service.getLive() == 1 ? "true" : "fail");
            resp.put("channel_id", videos.get(0).getChannelid());
            resp.put("status", "true");
            resp.put("video_id", videos.get(0).getVideoid());
            resp.put("video_title", videos.get(0).getVideotitle());
            resp.put("username", username.trim());
            resp.put("like", "fail");
            resp.put("sub", "fail");
                if(service.getNiche()==1){
                    String[] nicheArr = service.getKeyniche().split(",");
                    resp.put("niche_key", nicheArr[ran.nextInt(nicheArr.length)]);
                }else{
                    resp.put("niche_key","");
                }
                String list_key = dataOrderRepository.getListKeyByOrderid(videos.get(0).getOrderid());
                String key = "";
                if (list_key != null && list_key.length() != 0) {
                    String[] keyArr = list_key.split(",");
                    key = keyArr[ran.nextInt(keyArr.length)];
                }
                resp.put("suggest_type", "fail");
                resp.put("suggest_key", key.length() == 0 ? videos.get(0).getVideotitle() : key);
                resp.put("suggest_video", "");
                List<String> arrSource = new ArrayList<>();
                for (int i = 0; i < service.getSuggest(); i++) {
                    arrSource.add("suggest");
                }
                for (int i = 0; i < service.getSearch(); i++) {
                    arrSource.add("search");
                }
                for (int i = 0; i < service.getDtn(); i++) {
                    arrSource.add("dtn");
                }
                for (int i = 0; i < service.getEmbed(); i++) {
                    arrSource.add("embed");
                }
                for (int i = 0; i < service.getDirect(); i++) {
                    arrSource.add("direct");
                }
                for (int i = 0; i < service.getExternal(); i++) {
                    arrSource.add("external");
                }
                for (int i = 0; i < service.getPlaylists(); i++) {
                    arrSource.add("playlists");
                }
                String source_view = arrSource.get(ran.nextInt(arrSource.size())).trim();
                if (source_view.equals("suggest") && service.getType().equals("Special")) {
                    resp.put("suggest_type", "true");
                } else if (source_view.equals("search") && service.getType().equals("Special")) {
                    resp.put("video_title", key.length() == 0 ? videos.get(0).getVideotitle() : key);
                }
                resp.put("source", source_view);
                if(source_view.equals("embed")){
                    resp.put("suggest_video", videos.get(0).getLink());
                }else if(source_view.equals("direct")){
                    resp.put("suggest_video","https://www.youtube.com/channel/"+ videos.get(0).getChannelid());
                }

                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                if (service.getMintime() != service.getMaxtime() && service.getLive() == 0) {
                    if (videos.get(0).getDuration() > service.getMaxtime() * 60) {
                        resp.put("video_duration", service.getMintime() * 60 + (service.getMintime() < service.getMaxtime() ? (ran.nextInt((service.getMaxtime() - service.getMintime()) * 45) + (service.getMaxtime() >= 10 ? 30 : 0)) : 0));
                    } else {
                        resp.put("video_duration", service.getMintime() * 60 < videos.get(0).getDuration() ? (service.getMintime() * 60 + ran.nextInt((int)(videos.get(0).getDuration() - service.getMintime() * 60))) : videos.get(0).getDuration());
                    }
                } else if (service.getLive() == 1) {
                    int min_check = (int) ((service.getMintime() * 0.15) > 30 ? 30 : (service.getMintime() * 0.15));
                    if ((System.currentTimeMillis() - videos.get(0).getTimestart()) / 1000 / 60 < min_check) {
                        resp.put("video_duration", service.getMintime() * 60 + (service.getMintime() >= 15 ? 120 : 0));
                    } else {
                        int time_live = videos.get(0).getMinstart() - (int) ((System.currentTimeMillis() - videos.get(0).getTimestart()) / 1000 / 60);
                        resp.put("video_duration", (time_live > 0 ? time_live : 0) * 60 + (service.getMintime() >= 15 ? 120 : 0));
                    }
                } else {
                    if (videos.get(0).getDuration() > service.getMaxtime() * 60) {
                        resp.put("video_duration", service.getMintime() * 60 + (service.getMintime() < service.getMaxtime() ? (ran.nextInt((service.getMaxtime() - service.getMintime()) * 60 + service.getMaxtime() >= 10 ? 60 : 0)) : 0));
                    } else {
                        resp.put("video_duration", videos.get(0).getDuration());
                    }
                }
                if(((Integer.parseInt(resp.get("video_duration").toString())<10||Integer.parseInt(resp.get("video_duration").toString())>45))&&service.getMaxtime()==1){
                    resp.put("video_duration",ran.nextInt(30)+10);
                }
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);

        } catch (Exception e) {
            //show line error
            /*
            StackTraceElement stackTraceElement = Arrays.stream(e.getStackTrace()).filter(ste -> ste.getClassName().equals(this.getClass().getName())).collect(Collectors.toList()).get(0);

            System.out.println(stackTraceElement.getMethodName());
            System.out.println(stackTraceElement.getLineNumber());
            System.out.println(stackTraceElement.getClassName());
            System.out.println(stackTraceElement.getFileName());

            System.out.println("Error : " + e.getMessage());
             */
            resp.put("status", "fail");
            resp.put("fail", "sum");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(value = "getview", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> getview(@RequestParam(defaultValue = "") String vps, @RequestParam(defaultValue = "0") Integer buffh) {
        JSONObject resp = new JSONObject();
        if (vps.length() == 0) {
            resp.put("status", "fail");
            resp.put("message", "Vps không để trống");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        Vps vps_check=vpsRepository.getVpsByName(vps.trim());
        if(vps_check==null){
            resp.put("status", "fail");
            resp.put("message", "Vps không tồn tại");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }
        if(vps_check.getCmt()==0){
            resp.put("status", "fail");
            resp.put("message", "Vps không chạy view!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }

        Random ran = new Random();
        try {
            if((System.currentTimeMillis()-vps_check.getTask_time())/1000< (15+ran.nextInt(5))){
                Thread.sleep(ran.nextInt(1000));
                resp.put("status", "fail");
                resp.put("fail", "user");
                resp.put("message", "Không còn user để view!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }
            //Thread.sleep(ran.nextInt(1000));
            //Long historieId = historyViewRepository.getAccToView(vps.trim());
            Long historieId = historyViewRepository.getAccToViewNoCheckProxy(vps.trim());
            if (historieId == null) {
                vps_check.setTask_time(System.currentTimeMillis());
                vpsRepository.save(vps_check);
                resp.put("status", "fail");
                resp.put("fail", "user");
                resp.put("message", "Không còn user để view!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }


            List<VideoView> videos = null;
            List<HistoryView> histories = historyViewRepository.getHistoriesById(historieId);

            if(histories.get(0).getFinger_id()>0){
                fingerprintsPCRepository.update_Running_Finger_PC(histories.get(0).getFinger_id(),histories.get(0).getUsername().trim()+"%");
                histories.get(0).setFinger_id(0L);
            }

            histories.get(0).setGeo_rand("");
            historyViewRepository.save(histories.get(0));
            String geo_rand=histories.get(0).getGeo().trim();

            Map<String, Object> get_task =null;
            if(vps_check.getVpsoption().equals("smm")){
                geo_rand="smm";
                List<TaskPriority> priorityTasks =taskPriorityRepository.get_Priority_Task_By_Platform("youtube");
                List<String> arrTask = new ArrayList<>();

                for(int i=0;i<priorityTasks.size();i++){
                    for (int j = 0; j < priorityTasks.get(i).getPriority(); j++) {
                        arrTask.add(priorityTasks.get(i).getTask());
                    }
                }
                while (arrTask.size()>0){
                    String task = arrTask.get(ran.nextInt(arrTask.size())).trim();

                    while(arrTask.remove(task)) {}
                    if(task.equals("youtube_view")){
                        get_task=youtubeTask.youtube_view(histories.get(0).getUsername(),"auto");
                    }else if(task.equals("youtube_like")){
                        get_task=youtubeTask.youtube_like(histories.get(0).getUsername(),"auto");
                    }else if(task.equals("youtube_subscriber")){
                        get_task=youtubeTask.youtube_subscriber(histories.get(0).getUsername(),"auto");
                    }
                    if(get_task!=null?get_task.get("status").equals(true):false){
                        break;
                    }
                }
                if(get_task==null){
                    resp.put("status", "fail");
                    resp.put("fail", "video");
                    resp.put("message", "Không còn video để view!");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }else if(get_task.get("status").equals(false)){
                    resp.put("status", "fail");
                    resp.put("fail", "video");
                    resp.put("message", "Không còn video để view!");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
            }else{
                if (buffh == 1) {
                    videos = videoViewRepository.getvideoBuffHByGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderTrue.getValue());
                } else {
                    videos = videoViewRepository.getvideoViewByGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderTrue.getValue());
                }
                if (videos.size() > 0) {
                    histories.get(0).setTimeget(System.currentTimeMillis());
                    histories.get(0).setVideoid(videos.get(0).getVideoid());
                    histories.get(0).setOrderid(videos.get(0).getOrderid());
                    histories.get(0).setChannelid(videos.get(0).getChannelid());
                } else if (buffh == 0) {
                    videos = videoViewRepository.getvideoBuffHByGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderTrue.getValue());
                    if (videos.size() > 0) {
                        histories.get(0).setTimeget(System.currentTimeMillis());
                        histories.get(0).setVideoid(videos.get(0).getVideoid());
                        histories.get(0).setOrderid(videos.get(0).getOrderid());
                        histories.get(0).setChannelid(videos.get(0).getChannelid());
                    } else {
                        videos = videoViewRepository.getvideoViewRandNotGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderTrue.getValue());
                        if (videos.size() > 0&&!geo_rand.equals("test1")) {
                            geo_rand=serviceRepository.getGeoByService(videos.get(0).getService());
                            histories.get(0).setGeo_rand(geo_rand);
                            histories.get(0).setTimeget(System.currentTimeMillis());
                            histories.get(0).setVideoid(videos.get(0).getVideoid());
                            histories.get(0).setOrderid(videos.get(0).getOrderid());
                            histories.get(0).setChannelid(videos.get(0).getChannelid());
                        }else{
                            videos = videoViewRepository.getvideoViewByGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderSpeedTrue.getValue());
                            if (videos.size() > 0) {
                                histories.get(0).setTimeget(System.currentTimeMillis());
                                histories.get(0).setVideoid(videos.get(0).getVideoid());
                                histories.get(0).setOrderid(videos.get(0).getOrderid());
                                histories.get(0).setChannelid(videos.get(0).getChannelid());
                            }else{
                                videos = videoViewRepository.getvideoViewRandNotGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderSpeedTimeTrue.getValue());
                                if(videos.size()>0&&!geo_rand.equals("test1")){
                                    geo_rand=serviceRepository.getGeoByService(videos.get(0).getService());
                                    histories.get(0).setGeo_rand(geo_rand);
                                    histories.get(0).setTimeget(System.currentTimeMillis());
                                    histories.get(0).setVideoid(videos.get(0).getVideoid());
                                    histories.get(0).setOrderid(videos.get(0).getOrderid());
                                    histories.get(0).setChannelid(videos.get(0).getChannelid());
                                }else{
                                    videos = videoViewRepository.getvideoViewRandNotGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderSpeedTrue.getValue());
                                    if(videos.size()>0&&!geo_rand.equals("test1")){
                                        geo_rand=serviceRepository.getGeoByService(videos.get(0).getService());
                                        histories.get(0).setGeo_rand(geo_rand);
                                        histories.get(0).setTimeget(System.currentTimeMillis());
                                        histories.get(0).setVideoid(videos.get(0).getVideoid());
                                        histories.get(0).setOrderid(videos.get(0).getOrderid());
                                        histories.get(0).setChannelid(videos.get(0).getChannelid());
                                    }else{
                                        histories.get(0).setTimeget(System.currentTimeMillis());
                                        histories.get(0).setTask_done(histories.get(0).getTask_done()+1);
                                        historyViewRepository.save(histories.get(0));

                                        vps_check.setTask_time(System.currentTimeMillis());
                                        vpsRepository.save(vps_check);

                                        resp.put("status", "fail");
                                        resp.put("fail", "video");
                                        resp.put("message", "Không còn video để view!");
                                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                                    }
                                }

                            }
                        }

                    }
                } else {
                    videos = videoViewRepository.getvideoViewRandNotGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderTrue.getValue());
                    if (videos.size() > 0&&!geo_rand.equals("test1")) {
                        geo_rand=serviceRepository.getGeoByService(videos.get(0).getService());
                        histories.get(0).setGeo_rand(geo_rand);
                        histories.get(0).setTimeget(System.currentTimeMillis());
                        histories.get(0).setVideoid(videos.get(0).getVideoid());
                        histories.get(0).setOrderid(videos.get(0).getOrderid());
                        histories.get(0).setChannelid(videos.get(0).getChannelid());
                    }else{
                        videos = videoViewRepository.getvideoViewByGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderSpeedTrue.getValue());
                        if (videos.size() > 0) {
                            histories.get(0).setTimeget(System.currentTimeMillis());
                            histories.get(0).setVideoid(videos.get(0).getVideoid());
                            histories.get(0).setOrderid(videos.get(0).getOrderid());
                            histories.get(0).setChannelid(videos.get(0).getChannelid());
                        } else {
                            videos = videoViewRepository.getvideoViewRandNotGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderSpeedTimeTrue.getValue());
                            if(videos.size()>0&&!geo_rand.equals("test1")){
                                geo_rand=serviceRepository.getGeoByService(videos.get(0).getService());
                                histories.get(0).setGeo_rand(geo_rand);
                                histories.get(0).setTimeget(System.currentTimeMillis());
                                histories.get(0).setVideoid(videos.get(0).getVideoid());
                                histories.get(0).setOrderid(videos.get(0).getOrderid());
                                histories.get(0).setChannelid(videos.get(0).getChannelid());
                            }else{
                                videos = videoViewRepository.getvideoViewRandNotGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderSpeedTrue.getValue());
                                if(videos.size()>0&&!geo_rand.equals("test1")){
                                    geo_rand=serviceRepository.getGeoByService(videos.get(0).getService());
                                    histories.get(0).setGeo_rand(geo_rand);
                                    histories.get(0).setTimeget(System.currentTimeMillis());
                                    histories.get(0).setVideoid(videos.get(0).getVideoid());
                                    histories.get(0).setOrderid(videos.get(0).getOrderid());
                                    histories.get(0).setChannelid(videos.get(0).getChannelid());
                                }else{
                                    histories.get(0).setTimeget(System.currentTimeMillis());
                                    histories.get(0).setTask_done(histories.get(0).getTask_done()+1);
                                    historyViewRepository.save(histories.get(0));

                                    vps_check.setTask_time(System.currentTimeMillis());
                                    vpsRepository.save(vps_check);

                                    resp.put("status", "fail");
                                    resp.put("fail", "video");
                                    resp.put("message", "Không còn video để view!");
                                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                                }
                            }
                        }
                    }
                }
            }

            Long finger_id=0L;
            try{
                String stringrand="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefhijkprstuvwx0123456789";
                String code="";
                for(int i=0;i<5;i++){
                    Integer ranver=ran.nextInt(stringrand.length());
                    code=code+stringrand.charAt(ranver);
                }
                code=histories.get(0).getUsername()+code;
                finger_id=fingerprintsPCRepository.get_Finger_PC(System.currentTimeMillis(),code);
                if(finger_id!=null){
                    resp.put("finger_id", finger_id);
                }else {
                    histories.get(0).setTimeget(System.currentTimeMillis());
                    histories.get(0).setTask_done(histories.get(0).getTask_done()+1);
                    historyViewRepository.save(histories.get(0));

                    vps_check.setTask_time(System.currentTimeMillis());
                    vpsRepository.save(vps_check);

                    resp.put("status", "fail");
                    resp.put("fail", "video");
                    resp.put("message", "Không còn video để view!");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
            }catch (Exception e){
                histories.get(0).setTimeget(System.currentTimeMillis());
                histories.get(0).setTask_done(histories.get(0).getTask_done()+1);
                historyViewRepository.save(histories.get(0));

                vps_check.setTask_time(System.currentTimeMillis());
                vpsRepository.save(vps_check);

                resp.put("status", "fail");
                resp.put("fail", "video");
                resp.put("message", "Không còn video để view!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }


            if(vps_check.getVpsoption().equals("smm")){
                String[] proxy = new String[0];
                Random rand=new Random();
                if(ipV4Repository.checkIPv4Live(histories.get(0).getTypeproxy())==0){
                    proxy=proxyVNTrue.getValue().get(rand.nextInt(proxyVNTrue.getValue().size())).split(":");
                    if(proxy.length==0){
                        histories.get(0).setTimeget(System.currentTimeMillis());
                        historyViewRepository.save(histories.get(0));

                        vps_check.setTask_time(System.currentTimeMillis());
                        vpsRepository.save(vps_check);

                        resp.put("status", "fail");
                        resp.put("fail", "video");
                        resp.put("message", "Không còn video để view!");
                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                    }
                }else{
                    proxy=histories.get(0).getProxy().split(":");
                }
                String[] proxysetting=proxySettingRepository.getUserPassByHost(proxy[0]).split(",");

                Map<String, Object>  dataJson= (Map<String, Object>) get_task.get("data");

                Thread.sleep(ran.nextInt(150));
                if(!orderThreadCheck.getValue().contains(dataJson.get("order_id").toString())){
                    resp.put("status", "fail");
                    resp.put("fail", "video");
                    resp.put("message", "Không còn video để view!");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }

                histories.get(0).setTimeget(System.currentTimeMillis());
                histories.get(0).setVideoid( dataJson.get("video_id").toString());
                histories.get(0).setOrderid(Long.parseLong(dataJson.get("order_id").toString()));
                histories.get(0).setChannelid(dataJson.get("channel_id").toString());
                histories.get(0).setTimeget(System.currentTimeMillis());
                histories.get(0).setRunning(1);
                histories.get(0).setFinger_id(finger_id);
                historyViewRepository.save(histories.get(0));

                resp.put("status","true");
                resp.put("live",dataJson.get("live").toString());
                resp.put("channel_id", dataJson.get("channel_id").toString());
                resp.put("video_id", dataJson.get("video_id").toString());
                resp.put("video_title", dataJson.get("video_title").toString());
                resp.put("username", histories.get(0).getUsername());
                resp.put("service_id", Integer.parseInt(dataJson.get("service_id").toString()));
                resp.put("geo",dataJson.get("geo").toString());
                resp.put("like", dataJson.get("like").toString());
                resp.put("sub", dataJson.get("sub").toString());
                resp.put("proxy",proxy[0]+":"+proxy[1]+":"+proxysetting[0]+":"+proxysetting[1]);
                resp.put("niche_key",dataJson.get("niche_key").toString());

                resp.put("suggest_type", dataJson.get("suggest_type").toString());
                resp.put("suggest_key", dataJson.get("suggest_key").toString());
                resp.put("suggest_video", dataJson.get("suggest_video").toString());
                resp.put("suggest_type", dataJson.get("suggest_type").toString());

                resp.put("source", dataJson.get("source").toString());
                resp.put("suggest_video", dataJson.get("suggest_video").toString());
                resp.put("video_duration",Integer.parseInt(dataJson.get("video_duration").toString()));
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);

            }else{
                String[] proxy = new String[0];
                Random rand=new Random();
                if(geo_rand.equals("kr") || ipV4Repository.checkIPv4Live(histories.get(0).getTypeproxy())==0 || !geo_rand.equals(histories.get(0).getGeo())){

                    if(geo_rand.equals("vn")){
                        proxy=proxyVNTrue.getValue().get(rand.nextInt(proxyVNTrue.getValue().size())).split(":");
                    }else if(geo_rand.equals("us")){
                        proxy=proxyUSTrue.getValue().get(rand.nextInt(proxyUSTrue.getValue().size())).split(":");
                    }else if(geo_rand.equals("kr")){
                        proxy=proxyVNTrue.getValue().get(rand.nextInt(proxyVNTrue.getValue().size())).split(":");
                    }else if(geo_rand.equals("test1")){
                        proxy=proxyVNTrue.getValue().get(rand.nextInt(proxyVNTrue.getValue().size())).split(":");
                    }
                    if(proxy.length==0){
                        histories.get(0).setTimeget(System.currentTimeMillis());
                        historyViewRepository.save(histories.get(0));

                        vps_check.setTask_time(System.currentTimeMillis());
                        vpsRepository.save(vps_check);

                        resp.put("status", "fail");
                        resp.put("fail", "video");
                        resp.put("message", "Không còn video để view!");
                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                    }
                }else{
                    proxy=histories.get(0).getProxy().split(":");
                }
                String[] proxysetting=proxySettingRepository.getUserPassByHost(proxy[0]).split(",");
                Service service = serviceRepository.getInfoService(videos.get(0).getService());
                histories.get(0).setTimeget(System.currentTimeMillis());
                histories.get(0).setRunning(1);
                histories.get(0).setFinger_id(finger_id);

                historyViewRepository.save(histories.get(0));
                resp.put("live", service.getLive() == 1 ? "true" : "fail");
                resp.put("channel_id", videos.get(0).getChannelid());
                resp.put("status", "true");
                resp.put("video_id", videos.get(0).getVideoid());
                resp.put("video_title", videos.get(0).getVideotitle());
                resp.put("username", histories.get(0).getUsername());
                resp.put("service_id", service.getService());
                resp.put("geo", histories.get(0).getGeo());
                if(videos.get(0).getService()==801){
                    resp.put("like", "true");
                }else{
                    resp.put("like", "fail");
                }
                if(videos.get(0).getService()==802){
                    resp.put("sub", "true");
                }else{
                    resp.put("sub", "fail");
                }
                resp.put("proxy",proxy[0]+":"+proxy[1]+":"+proxysetting[0]+":"+proxysetting[1]);
                if(service.getNiche()==1){
                    String[] nicheArr = service.getKeyniche().split(",");
                    resp.put("niche_key", nicheArr[ran.nextInt(nicheArr.length)]);
                }else{
                    resp.put("niche_key","");
                }
                String list_key = dataOrderRepository.getListKeyByOrderid(videos.get(0).getOrderid());
                String key = "";
                if (list_key != null && list_key.length() != 0) {
                    String[] keyArr = list_key.split(",");
                    key = keyArr[ran.nextInt(keyArr.length)];
                }
                resp.put("suggest_type", "fail");
                resp.put("suggest_key", key.length() == 0 ? videos.get(0).getVideotitle() : key);
                resp.put("suggest_video", "");
                List<String> arrSource = new ArrayList<>();
                for (int i = 0; i < service.getSuggest(); i++) {
                    arrSource.add("suggest");
                }
                for (int i = 0; i < service.getSearch(); i++) {
                    arrSource.add("search");
                }
                for (int i = 0; i < service.getDtn(); i++) {
                    arrSource.add("dtn");
                }
                for (int i = 0; i < service.getEmbed(); i++) {
                    arrSource.add("embed");
                }
                for (int i = 0; i < service.getDirect(); i++) {
                    arrSource.add("direct");
                }
                for (int i = 0; i < service.getExternal(); i++) {
                    arrSource.add("external");
                }
                for (int i = 0; i < service.getPlaylists(); i++) {
                    arrSource.add("playlists");
                }
                String source_view = arrSource.get(ran.nextInt(arrSource.size())).trim();
                if (source_view.equals("suggest") && service.getType().equals("Special")) {
                    resp.put("suggest_type", "true");
                } else if (source_view.equals("search") && service.getType().equals("Special")) {
                    resp.put("video_title", key.length() == 0 ? videos.get(0).getVideotitle() : key);
                }
                resp.put("source", source_view);
                if(source_view.equals("embed")){
                    resp.put("suggest_video", videos.get(0).getLink());
                }

                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                if (service.getMintime() != service.getMaxtime()) {
                    if (videos.get(0).getDuration() > service.getMaxtime() * 60) {
                        //resp.put("video_duration", service.getMintime() * 60 + (service.getMintime() < service.getMaxtime() ? (ran.nextInt((service.getMaxtime() - service.getMintime()) * 45) + (service.getMaxtime() >= 10 ? 30 : 0)) : 0));
                        resp.put("video_duration", service.getMintime() * 60 +ran.nextInt((service.getMaxtime()-service.getMintime()==1?30:60)));
                    } else {
                        //resp.put("video_duration", service.getMintime() * 60 < videos.get(0).getDuration() ? (service.getMintime() * 60 + ran.nextInt((int)(videos.get(0).getDuration() - service.getMintime() * 60))) : videos.get(0).getDuration());
                        resp.put("video_duration", service.getMintime() * 60 < videos.get(0).getDuration() ? (service.getMintime() * 60 + ran.nextInt((service.getMaxtime()-service.getMintime()==1?30:60))) : videos.get(0).getDuration());
                    }
                }else {
                    if (videos.get(0).getDuration() > service.getMaxtime() * 60) {
                        //resp.put("video_duration", service.getMintime() * 60 + (service.getMintime() < service.getMaxtime() ? (ran.nextInt((service.getMaxtime() - service.getMintime()) * 60 + service.getMaxtime() >= 10 ? 60 : 0)) : 0));
                        resp.put("video_duration", service.getMintime() * 60);
                    } else {
                        resp.put("video_duration", videos.get(0).getDuration());
                    }
                }
                if(((Integer.parseInt(resp.get("video_duration").toString())<6||Integer.parseInt(resp.get("video_duration").toString())>20))&&service.getMaxtime()==1){
                    resp.put("video_duration",ran.nextInt(14)+6);
                }
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }

        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);

        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("fail", "sum");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "getviewtest", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> getviewtest(@RequestParam(defaultValue = "") String vps, @RequestParam(defaultValue = "0") Integer buffh) {
        JSONObject resp = new JSONObject();
        if (vps.length() == 0) {
            resp.put("status", "fail");
            resp.put("message", "Vps không để trống");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        if(vpsRepository.checkVpsCmtTrue(vps.trim())==0){
            resp.put("status", "fail");
            resp.put("message", "Vps không chạy view!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }
        Random ran = new Random();
        try {
            Thread.sleep(ran.nextInt(1000));
            //Long historieId = historyViewRepository.getAccToView(vps.trim());
            Long historieId = historyViewRepository.getAccToViewNoCheckProxy(vps.trim());
            if (historieId == null) {
                resp.put("status", "fail");
                resp.put("fail", "user");
                resp.put("message", "Không còn user để view!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }
            List<VideoView> videos = null;
            List<HistoryView> histories = historyViewRepository.getHistoriesById(historieId);
            histories.get(0).setGeo_rand("");
            historyViewRepository.save(histories.get(0));
            String geo_rand=histories.get(0).getGeo().trim();
            if (buffh == 1) {
                videos = videoViewRepository.getvideoBuffHByGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderTrue.getValue());
            } else {
                videos = videoViewRepository.getvideoViewByGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderTrue.getValue());
            }
            if (videos.size() > 0) {
                histories.get(0).setTimeget(System.currentTimeMillis());
                histories.get(0).setVideoid(videos.get(0).getVideoid());
                histories.get(0).setOrderid(videos.get(0).getOrderid());
                histories.get(0).setChannelid(videos.get(0).getChannelid());
            } else if (buffh == 0) {
                videos = videoViewRepository.getvideoBuffHByGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderTrue.getValue());
                if (videos.size() > 0) {
                    histories.get(0).setTimeget(System.currentTimeMillis());
                    histories.get(0).setVideoid(videos.get(0).getVideoid());
                    histories.get(0).setOrderid(videos.get(0).getOrderid());
                    histories.get(0).setChannelid(videos.get(0).getChannelid());
                } else {
                    videos = videoViewRepository.getvideoViewRandNotGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderTrue.getValue());
                    if (videos.size() > 0&&!geo_rand.equals("test1")) {
                        geo_rand=serviceRepository.getGeoByService(videos.get(0).getService());
                        histories.get(0).setGeo_rand(geo_rand);
                        histories.get(0).setTimeget(System.currentTimeMillis());
                        histories.get(0).setVideoid(videos.get(0).getVideoid());
                        histories.get(0).setOrderid(videos.get(0).getOrderid());
                        histories.get(0).setChannelid(videos.get(0).getChannelid());
                    }else{
                        videos = videoViewRepository.getvideoViewByGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderSpeedTrue.getValue());
                        if (videos.size() > 0) {
                            histories.get(0).setTimeget(System.currentTimeMillis());
                            histories.get(0).setVideoid(videos.get(0).getVideoid());
                            histories.get(0).setOrderid(videos.get(0).getOrderid());
                            histories.get(0).setChannelid(videos.get(0).getChannelid());
                        }else{
                            videos = videoViewRepository.getvideoViewRandNotGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderSpeedTimeTrue.getValue());
                            if(videos.size()>0&&!geo_rand.equals("test1")){
                                geo_rand=serviceRepository.getGeoByService(videos.get(0).getService());
                                histories.get(0).setGeo_rand(geo_rand);
                                histories.get(0).setTimeget(System.currentTimeMillis());
                                histories.get(0).setVideoid(videos.get(0).getVideoid());
                                histories.get(0).setOrderid(videos.get(0).getOrderid());
                                histories.get(0).setChannelid(videos.get(0).getChannelid());
                            }else{
                                videos = videoViewRepository.getvideoViewRandNotGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderSpeedTrue.getValue());
                                if(videos.size()>0&&!geo_rand.equals("test1")){
                                    geo_rand=serviceRepository.getGeoByService(videos.get(0).getService());
                                    histories.get(0).setGeo_rand(geo_rand);
                                    histories.get(0).setTimeget(System.currentTimeMillis());
                                    histories.get(0).setVideoid(videos.get(0).getVideoid());
                                    histories.get(0).setOrderid(videos.get(0).getOrderid());
                                    histories.get(0).setChannelid(videos.get(0).getChannelid());
                                }else{
                                    histories.get(0).setTimeget(System.currentTimeMillis());
                                    histories.get(0).setTask_done(histories.get(0).getTask_done()+1);
                                    historyViewRepository.save(histories.get(0));
                                    resp.put("status", "fail");
                                    resp.put("fail", "video");
                                    resp.put("message", "Không còn video để view!");
                                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                                }
                            }

                        }
                    }

                }
            } else {
                videos = videoViewRepository.getvideoViewRandNotGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderTrue.getValue());
                if (videos.size() > 0&&!geo_rand.equals("test1")) {
                    geo_rand=serviceRepository.getGeoByService(videos.get(0).getService());
                    histories.get(0).setGeo_rand(geo_rand);
                    histories.get(0).setTimeget(System.currentTimeMillis());
                    histories.get(0).setVideoid(videos.get(0).getVideoid());
                    histories.get(0).setOrderid(videos.get(0).getOrderid());
                    histories.get(0).setChannelid(videos.get(0).getChannelid());
                }else{
                    videos = videoViewRepository.getvideoViewByGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderSpeedTrue.getValue());
                    if (videos.size() > 0) {
                        histories.get(0).setTimeget(System.currentTimeMillis());
                        histories.get(0).setVideoid(videos.get(0).getVideoid());
                        histories.get(0).setOrderid(videos.get(0).getOrderid());
                        histories.get(0).setChannelid(videos.get(0).getChannelid());
                    } else {
                        videos = videoViewRepository.getvideoViewRandNotGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderSpeedTimeTrue.getValue());
                        if(videos.size()>0&&!geo_rand.equals("test1")){
                            geo_rand=serviceRepository.getGeoByService(videos.get(0).getService());
                            histories.get(0).setGeo_rand(geo_rand);
                            histories.get(0).setTimeget(System.currentTimeMillis());
                            histories.get(0).setVideoid(videos.get(0).getVideoid());
                            histories.get(0).setOrderid(videos.get(0).getOrderid());
                            histories.get(0).setChannelid(videos.get(0).getChannelid());
                        }else{
                            videos = videoViewRepository.getvideoViewRandNotGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderSpeedTrue.getValue());
                            if(videos.size()>0&&!geo_rand.equals("test1")){
                                geo_rand=serviceRepository.getGeoByService(videos.get(0).getService());
                                histories.get(0).setGeo_rand(geo_rand);
                                histories.get(0).setTimeget(System.currentTimeMillis());
                                histories.get(0).setVideoid(videos.get(0).getVideoid());
                                histories.get(0).setOrderid(videos.get(0).getOrderid());
                                histories.get(0).setChannelid(videos.get(0).getChannelid());
                            }else{
                                histories.get(0).setTimeget(System.currentTimeMillis());
                                histories.get(0).setTask_done(histories.get(0).getTask_done()+1);
                                historyViewRepository.save(histories.get(0));
                                resp.put("status", "fail");
                                resp.put("fail", "video");
                                resp.put("message", "Không còn video để view!");
                                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                            }
                        }
                    }
                }
            }

            String stringrand="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefhijkprstuvwx0123456789";
            String code="";
            for(int i=0;i<50;i++){
                Integer ranver=ran.nextInt(stringrand.length());
                code=code+stringrand.charAt(ranver);
            }
            Long finger_id=fingerprintsPCRepository.get_Finger_PC(System.currentTimeMillis(),code);
            if(finger_id!=null){
                resp.put("finger_id", finger_id);
            }else {
                histories.get(0).setTimeget(System.currentTimeMillis());
                histories.get(0).setTask_done(histories.get(0).getTask_done()+1);
                historyViewRepository.save(histories.get(0));
                resp.put("status", "fail");
                resp.put("fail", "video");
                resp.put("message", "Không còn video để view!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }
            String[] proxy = new String[0];
            Random rand=new Random();
            if(ipV4Repository.checkIPv4Live(histories.get(0).getTypeproxy())==0 || !geo_rand.equals(histories.get(0).getGeo())){

                if(geo_rand.equals("vn")){
                    proxy=proxyVNTrue.getValue().get(rand.nextInt(proxyVNTrue.getValue().size())).split(":");
                }else if(geo_rand.equals("us")){
                    proxy=proxyUSTrue.getValue().get(rand.nextInt(proxyUSTrue.getValue().size())).split(":");
                }else if(geo_rand.equals("kr")){
                    proxy=proxyKRTrue.getValue().get(rand.nextInt(proxyKRTrue.getValue().size())).split(":");
                }else if(geo_rand.equals("test1")){
                    proxy=proxyVNTrue.getValue().get(rand.nextInt(proxyVNTrue.getValue().size())).split(":");
                }
                if(proxy.length==0){
                    histories.get(0).setTimeget(System.currentTimeMillis());
                    historyViewRepository.save(histories.get(0));
                    resp.put("status", "fail");
                    resp.put("fail", "video");
                    resp.put("message", "Không còn video để view!");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
            }else{
                proxy=histories.get(0).getProxy().split(":");
            }
            String[] proxysetting=proxySettingRepository.getUserPassByHost(proxy[0]).split(",");
            Service service = serviceRepository.getInfoService(videos.get(0).getService());

            histories.get(0).setTimeget(System.currentTimeMillis());
            histories.get(0).setRunning(1);

            historyViewRepository.save(histories.get(0));
            resp.put("live", service.getLive() == 1 ? "true" : "fail");
            resp.put("channel_id", videos.get(0).getChannelid());
            resp.put("status", "true");
            resp.put("video_id", videos.get(0).getVideoid());
            resp.put("video_title", videos.get(0).getVideotitle());
            resp.put("username", histories.get(0).getUsername());
            resp.put("geo", histories.get(0).getGeo());
            resp.put("like", "fail");
            resp.put("sub", "fail");
            resp.put("proxy",proxy[0]+":"+proxy[1]+":"+proxysetting[0]+":"+proxysetting[1]);

            if(service.getNiche()==1){
                String[] nicheArr = service.getKeyniche().split(",");
                resp.put("niche_key", nicheArr[ran.nextInt(nicheArr.length)]);
            }else{
                resp.put("niche_key","");
            }
            String list_key = dataOrderRepository.getListKeyByOrderid(videos.get(0).getOrderid());
            String key = "";
            if (list_key != null && list_key.length() != 0) {
                String[] keyArr = list_key.split(",");
                key = keyArr[ran.nextInt(keyArr.length)];
            }
            resp.put("suggest_type", "fail");
            resp.put("suggest_key", key.length() == 0 ? videos.get(0).getVideotitle() : key);
            resp.put("suggest_video", "");
            List<String> arrSource = new ArrayList<>();
            for (int i = 0; i < service.getSuggest(); i++) {
                arrSource.add("suggest");
            }
            for (int i = 0; i < service.getSearch(); i++) {
                arrSource.add("search");
            }
            for (int i = 0; i < service.getDtn(); i++) {
                arrSource.add("dtn");
            }
            for (int i = 0; i < service.getEmbed(); i++) {
                arrSource.add("embed");
            }
            for (int i = 0; i < service.getDirect(); i++) {
                arrSource.add("direct");
            }
            for (int i = 0; i < service.getExternal(); i++) {
                arrSource.add("external");
            }
            for (int i = 0; i < service.getPlaylists(); i++) {
                arrSource.add("playlists");
            }
            String source_view = arrSource.get(ran.nextInt(arrSource.size())).trim();
            if (source_view.equals("suggest") && service.getType().equals("Special")) {
                resp.put("suggest_type", "true");
            } else if (source_view.equals("search") && service.getType().equals("Special")) {
                resp.put("video_title", key.length() == 0 ? videos.get(0).getVideotitle() : key);
            }
            resp.put("source", source_view);
            if(source_view.equals("embed")){
                resp.put("suggest_video", videos.get(0).getLink());
            }

            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////


            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////


            if (service.getMintime() != service.getMaxtime()) {
                if (videos.get(0).getDuration() > service.getMaxtime() * 60) {
                    //resp.put("video_duration", service.getMintime() * 60 + (service.getMintime() < service.getMaxtime() ? (ran.nextInt((service.getMaxtime() - service.getMintime()) * 45) + (service.getMaxtime() >= 10 ? 30 : 0)) : 0));
                    resp.put("video_duration", service.getMintime() * 60 +ran.nextInt((service.getMaxtime()-service.getMintime()==1?30:60)));
                } else {
                    //resp.put("video_duration", service.getMintime() * 60 < videos.get(0).getDuration() ? (service.getMintime() * 60 + ran.nextInt((int)(videos.get(0).getDuration() - service.getMintime() * 60))) : videos.get(0).getDuration());
                    resp.put("video_duration", service.getMintime() * 60 < videos.get(0).getDuration() ? (service.getMintime() * 60 + ran.nextInt((service.getMaxtime()-service.getMintime()==1?30:60))) : videos.get(0).getDuration());
                }
            }else {
                if (videos.get(0).getDuration() > service.getMaxtime() * 60) {
                    //resp.put("video_duration", service.getMintime() * 60 + (service.getMintime() < service.getMaxtime() ? (ran.nextInt((service.getMaxtime() - service.getMintime()) * 60 + service.getMaxtime() >= 10 ? 60 : 0)) : 0));
                    resp.put("video_duration", service.getMintime() * 60);
                } else {
                    resp.put("video_duration", videos.get(0).getDuration());
                }
            }
            if(((Integer.parseInt(resp.get("video_duration").toString())<6||Integer.parseInt(resp.get("video_duration").toString())>20))&&service.getMaxtime()==1){
                resp.put("video_duration",ran.nextInt(14)+6);
            }
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);

        } catch (Exception e) {
            StackTraceElement stackTraceElement = Arrays.stream(e.getStackTrace()).filter(ste -> ste.getClassName().equals(this.getClass().getName())).collect(Collectors.toList()).get(0);
            System.out.println(stackTraceElement.getMethodName());
            System.out.println(stackTraceElement.getLineNumber());
            System.out.println(stackTraceElement.getClassName());
            System.out.println(stackTraceElement.getFileName());
            System.out.println("Error : " + e.getMessage());
            resp.put("status", "fail");
            resp.put("fail", "sum");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    /*
    @GetMapping(value = "getview", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> getview(@RequestParam(defaultValue = "") String vps, @RequestParam(defaultValue = "0") Integer buffh) {
        JSONObject resp = new JSONObject();
        if (vps.length() == 0) {
            resp.put("status", "fail");
            resp.put("message", "Vps không để trống");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        if(vpsRepository.checkVpsCmtTrue(vps.trim())==0){
            resp.put("status", "fail");
            resp.put("message", "Vps không chạy view!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }
        Random ran = new Random();
        try {
            Thread.sleep(ran.nextInt(1000));
            //Long historieId = historyViewRepository.getAccToView(vps.trim());
            Long historieId = historyViewRepository.getAccToViewNoCheckProxy(vps.trim());
            if (historieId == null) {
                resp.put("status", "fail");
                resp.put("fail", "user");
                resp.put("message", "Không còn user để view!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }
            List<VideoView> videos = null;
            List<HistoryView> histories = historyViewRepository.getHistoriesById(historieId);
            if (buffh == 1) {
                videos = videoViewRepository.getvideoBuffHByGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderTrue.getValue());
            } else {
                videos = videoViewRepository.getvideoViewByGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderTrue.getValue());
            }
            if (videos.size() > 0) {
                histories.get(0).setTimeget(System.currentTimeMillis());
                histories.get(0).setVideoid(videos.get(0).getVideoid());
                histories.get(0).setOrderid(videos.get(0).getOrderid());
                histories.get(0).setChannelid(videos.get(0).getChannelid());
            } else if (buffh == 0) {
                videos = videoViewRepository.getvideoBuffHByGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderTrue.getValue());
                if (videos.size() > 0) {
                    histories.get(0).setTimeget(System.currentTimeMillis());
                    histories.get(0).setVideoid(videos.get(0).getVideoid());
                    histories.get(0).setOrderid(videos.get(0).getOrderid());
                    histories.get(0).setChannelid(videos.get(0).getChannelid());
                } else {
                    videos = videoViewRepository.getvideoViewByGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderSpeedTrue.getValue());
                    if (videos.size() > 0) {
                        histories.get(0).setTimeget(System.currentTimeMillis());
                        histories.get(0).setVideoid(videos.get(0).getVideoid());
                        histories.get(0).setOrderid(videos.get(0).getOrderid());
                        histories.get(0).setChannelid(videos.get(0).getChannelid());
                    } else if(settingRepository.getRandView()>=ran.nextInt(1000)){
                        try {
                            videos = videoViewRepository.getvideoViewRandByGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo());
                            if (videos.size() > 0) {
                                histories.get(0).setTimeget(System.currentTimeMillis());
                                histories.get(0).setVideoid(videos.get(0).getVideoid());
                                histories.get(0).setOrderid(videos.get(0).getOrderid());
                                histories.get(0).setChannelid(videos.get(0).getChannelid());
                            } else {
                                histories.get(0).setTimeget(System.currentTimeMillis());
                                historyViewRepository.save(histories.get(0));
                                resp.put("status", "fail");
                                resp.put("fail", "video");
                                resp.put("message", "Không còn video để view!");
                                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                            }
                        }catch (Exception e){
                            histories.get(0).setTimeget(System.currentTimeMillis());
                            historyViewRepository.save(histories.get(0));
                            resp.put("status", "fail");
                            resp.put("fail", "video");
                            resp.put("message", "Không còn video để view!");
                            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                        }

                    }else{
                        histories.get(0).setTimeget(System.currentTimeMillis());
                        historyViewRepository.save(histories.get(0));
                        resp.put("status", "fail");
                        resp.put("fail", "video");
                        resp.put("message", "Không còn video để view!");
                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                    }
                }
            } else if(settingRepository.getRandView()>=ran.nextInt(1000)) {
                videos = videoViewRepository.getvideoViewByGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo(), orderSpeedTrue.getValue());
                if (videos.size() > 0) {
                    histories.get(0).setTimeget(System.currentTimeMillis());
                    histories.get(0).setVideoid(videos.get(0).getVideoid());
                    histories.get(0).setOrderid(videos.get(0).getOrderid());
                    histories.get(0).setChannelid(videos.get(0).getChannelid());
                } else if (settingRepository.getRandView() >= ran.nextInt(1000)) {
                    try {
                        videos = videoViewRepository.getvideoViewRandByGeo(histories.get(0).getGeo().trim(), histories.get(0).getListvideo());
                        if (videos.size() > 0) {
                            histories.get(0).setTimeget(System.currentTimeMillis());
                            histories.get(0).setVideoid(videos.get(0).getVideoid());
                            histories.get(0).setOrderid(videos.get(0).getOrderid());
                            histories.get(0).setChannelid(videos.get(0).getChannelid());
                        } else {
                            histories.get(0).setTimeget(System.currentTimeMillis());
                            historyViewRepository.save(histories.get(0));
                            resp.put("status", "fail");
                            resp.put("fail", "video");
                            resp.put("message", "Không còn video để view!");
                            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                        }
                    } catch (Exception e) {
                        histories.get(0).setTimeget(System.currentTimeMillis());
                        historyViewRepository.save(histories.get(0));
                        resp.put("status", "fail");
                        resp.put("fail", "video");
                        resp.put("message", "Không còn video để view!");
                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                    }

                } else {
                    histories.get(0).setTimeget(System.currentTimeMillis());
                    historyViewRepository.save(histories.get(0));
                    resp.put("status", "fail");
                    resp.put("fail", "video");
                    resp.put("message", "Không còn video để view!");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
            }
            String[] proxy;
            List<Proxy> proxies;
            if(ipV4Repository.checkIPv4Live(histories.get(0).getTypeproxy())==0){
                proxies = proxyRepository.getProxyNotRunningAndLive(histories.get(0).getGeo());
                if(proxies.size()>0){
                    proxy=proxies.get(0).getProxy().split(":");
                }else{
                    histories.get(0).setTimeget(System.currentTimeMillis());
                    historyViewRepository.save(histories.get(0));
                    resp.put("status", "fail");
                    resp.put("fail", "video");
                    resp.put("message", "Không còn video để view!");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
            }else{
                proxy=histories.get(0).getProxy().split(":");
            }
            String[] proxysetting=proxySettingRepository.getUserPassByHost(proxy[0]).split(",");
            Service service = serviceRepository.getInfoService(videos.get(0).getService());

            histories.get(0).setTimeget(System.currentTimeMillis());
            histories.get(0).setRunning(1);

            historyViewRepository.save(histories.get(0));
            resp.put("live", service.getLive() == 1 ? "true" : "fail");
            resp.put("channel_id", videos.get(0).getChannelid());
            resp.put("status", "true");
            resp.put("video_id", videos.get(0).getVideoid());
            resp.put("video_title", videos.get(0).getVideotitle());
            resp.put("username", histories.get(0).getUsername());
            resp.put("geo", histories.get(0).getGeo());
            resp.put("like", "fail");
            resp.put("sub", "fail");
            resp.put("proxy",proxy[0]+":"+proxy[1]+":"+proxysetting[0]+":"+proxysetting[1]);
            if(service.getNiche()==1){
                String[] nicheArr = service.getKeyniche().split(",");
                resp.put("niche_key", nicheArr[ran.nextInt(nicheArr.length)]);
            }else{
                resp.put("niche_key","");
            }
            String list_key = dataOrderRepository.getListKeyByOrderid(videos.get(0).getOrderid());
            String key = "";
            if (list_key != null && list_key.length() != 0) {
                String[] keyArr = list_key.split(",");
                key = keyArr[ran.nextInt(keyArr.length)];
            }
            resp.put("suggest_type", "fail");
            resp.put("suggest_key", key.length() == 0 ? videos.get(0).getVideotitle() : key);
            resp.put("suggest_video", "");
            List<String> arrSource = new ArrayList<>();
            for (int i = 0; i < service.getSuggest(); i++) {
                arrSource.add("suggest");
            }
            for (int i = 0; i < service.getSearch(); i++) {
                arrSource.add("search");
            }
            for (int i = 0; i < service.getDtn(); i++) {
                arrSource.add("dtn");
            }
            for (int i = 0; i < service.getEmbed(); i++) {
                arrSource.add("embed");
            }
            for (int i = 0; i < service.getDirect(); i++) {
                arrSource.add("direct");
            }
            for (int i = 0; i < service.getExternal(); i++) {
                arrSource.add("external");
            }
            for (int i = 0; i < service.getPlaylists(); i++) {
                arrSource.add("playlists");
            }
            String source_view = arrSource.get(ran.nextInt(arrSource.size())).trim();
            if (source_view.equals("suggest") && service.getType().equals("Special")) {
                resp.put("suggest_type", "true");
            } else if (source_view.equals("search") && service.getType().equals("Special")) {
                resp.put("video_title", key.length() == 0 ? videos.get(0).getVideotitle() : key);
            }
            resp.put("source", source_view);
            if(source_view.equals("embed")){
                resp.put("suggest_video", videos.get(0).getLink());
            }

            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////


            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////


            if (service.getMintime() != service.getMaxtime() && service.getLive() == 0) {
                if (videos.get(0).getDuration() > service.getMaxtime() * 60) {
                    resp.put("video_duration", service.getMintime() * 60 + (service.getMintime() < service.getMaxtime() ? (ran.nextInt((service.getMaxtime() - service.getMintime()) * 45) + (service.getMaxtime() >= 10 ? 30 : 0)) : 0));
                } else {
                    resp.put("video_duration", service.getMintime() * 60 < videos.get(0).getDuration() ? (service.getMintime() * 60 + ran.nextInt((int) (videos.get(0).getDuration() - service.getMintime() * 60))) : videos.get(0).getDuration());
                }
            } else if (service.getLive() == 1) {
                int min_check = (int) ((service.getMintime() * 0.15) > 30 ? 30 : (service.getMintime() * 0.15));
                if ((System.currentTimeMillis() - videos.get(0).getTimestart()) / 1000 / 60 < min_check) {
                    resp.put("video_duration", service.getMintime() * 60 + (service.getMintime() >= 15 ? 120 : 0));
                } else {
                    int time_live = videos.get(0).getMinstart() - (int) ((System.currentTimeMillis() - videos.get(0).getTimestart()) / 1000 / 60);
                    resp.put("video_duration", (time_live > 0 ? time_live : 0) * 60 + (service.getMintime() >= 15 ? 120 : 0));
                }
            } else {
                if (videos.get(0).getDuration() > service.getMaxtime() * 60) {
                    resp.put("video_duration", service.getMintime() * 60 + (service.getMintime() < service.getMaxtime() ? (ran.nextInt((service.getMaxtime() - service.getMintime()) * 60 + service.getMaxtime() >= 10 ? 60 : 0)) : 0));
                } else {
                    resp.put("video_duration", videos.get(0).getDuration());
                }
            }
            if(((Integer.parseInt(resp.get("video_duration").toString())<6||Integer.parseInt(resp.get("video_duration").toString())>20))&&service.getMaxtime()==1){
                resp.put("video_duration",ran.nextInt(14)+6);
            }
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);

        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("fail", "sum");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }
     */

    @GetMapping(value = "getlivebyusername", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> getlivebyusername(@RequestParam(defaultValue = "") String username) {
        JSONObject resp = new JSONObject();
        if (username.length() == 0) {
            resp.put("status", "fail");
            resp.put("message", "Username không để trống");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }

        Random ran = new Random();
        try {
            Thread.sleep(ran.nextInt(1000));
            Long historieId = historyViewRepository.getId(username.trim());
            if(historieId==null){
                resp.put("status", "fail");
                resp.put("fail", "user");
                resp.put("message", "Username không tồn tại!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }
            List<VideoView> videos = null;
            List<HistoryView> histories = historyViewRepository.getHistoriesById(historieId);
            if (histories.get(0).getGeo().trim().equals("live-vn")) {
                videos = videoViewRepository.getvideoPreVer2VNTEST(orderTrue.getValue());
            } else if(histories.get(0).getGeo().trim().equals("live-us")) {
                videos = videoViewRepository.getvideoPreVer2USTEST(orderTrue.getValue());
            }else{
                historyViewRepository.save(histories.get(0));
                resp.put("status", "fail");
                resp.put("username", histories.get(0).getUsername());
                resp.put("fail", "video");
                resp.put("message", "Không còn video để view!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }
            if (videos.size() > 0) {
                histories.get(0).setTimeget(System.currentTimeMillis());
                histories.get(0).setVideoid(videos.get(0).getVideoid());
                histories.get(0).setOrderid(videos.get(0).getOrderid());
                histories.get(0).setChannelid(videos.get(0).getChannelid());
            } else {
                historyViewRepository.save(histories.get(0));
                resp.put("status", "fail");
                resp.put("username", histories.get(0).getUsername());
                resp.put("fail", "video");
                resp.put("message", "Không còn video để view!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }

            Service service = serviceRepository.getServiceNoCheckEnabled(videos.get(0).getService());

            histories.get(0).setTimeget(System.currentTimeMillis());
            histories.get(0).setRunning(1);
            historyViewRepository.save(histories.get(0));
            if(videos.get(0).getTimestart()>=System.currentTimeMillis()){
                List<Long> arrTime = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    arrTime.add(System.currentTimeMillis());
                }
                for (int i = 0; i < 15; i++) {
                    arrTime.add(TimeUnit.MINUTES.toMillis((ran.nextInt((int)(service.getMaxtime()*0.1))) +videos.get(0).getTimestart()));
                }
                for (int i = 0; i < 25; i++) {
                    arrTime.add(videos.get(0).getTimestart()+ TimeUnit.MINUTES.toMillis((long)(service.getMaxtime()*0.1))+TimeUnit.MINUTES.toMillis(ran.nextInt((int)(service.getMaxtime()*0.4))));
                }
                for (int i = 0; i < 40; i++) {
                    arrTime.add(videos.get(0).getTimestart()+ TimeUnit.MINUTES.toMillis((long)(service.getMaxtime()*0.4))+TimeUnit.MINUTES.toMillis(ran.nextInt((int)(service.getMaxtime()*0.6))));
                }

                resp.put("time_start", arrTime.get(ran.nextInt(arrTime.size())));
            }else{
                resp.put("time_start", System.currentTimeMillis());
            }
            resp.put("channel_id", videos.get(0).getChannelid());
            resp.put("status", "true");
            resp.put("time_end", (videos.get(0).getTimestart()+ TimeUnit.MINUTES.toMillis((long)(service.getMaxtime()*1.5))+TimeUnit.MINUTES.toMillis((ran.nextInt((5))))));
            resp.put("video_id", videos.get(0).getVideoid());
            resp.put("video_title", videos.get(0).getVideotitle());
            resp.put("geo", "live");
            resp.put("username", histories.get(0).getUsername());
            resp.put("like", "fail");
            resp.put("sub", "fail");

            String list_key = dataOrderRepository.getListKeyByOrderid(videos.get(0).getOrderid());
            String key = "";
            if (list_key != null && list_key.length() != 0) {
                String[] keyArr = list_key.split(",");
                key = keyArr[ran.nextInt(keyArr.length)];
            }
            resp.put("suggest_type", "fail");
            resp.put("suggest_key", key.length() == 0 ? videos.get(0).getVideotitle() : key);
            resp.put("suggest_video", "");
            List<String> arrSource = new ArrayList<>();
            for (int i = 0; i < service.getSuggest(); i++) {
                arrSource.add("suggest");
            }
            for (int i = 0; i < service.getSearch(); i++) {
                arrSource.add("search");
            }
            for (int i = 0; i < service.getDtn(); i++) {
                arrSource.add("dtn");
            }
            for (int i = 0; i < service.getEmbed(); i++) {
                arrSource.add("embed");
            }
            for (int i = 0; i < service.getDirect(); i++) {
                arrSource.add("direct");
            }
            for (int i = 0; i < service.getExternal(); i++) {
                arrSource.add("external");
            }
            for (int i = 0; i < service.getPlaylists(); i++) {
                arrSource.add("playlists");
            }
            String source_view=arrSource.get(ran.nextInt(arrSource.size())).trim();
            if(source_view.equals("suggest")&&service.getType().equals("Special")){
                resp.put("suggest_type", "true");
            }else if(source_view.equals("search")&&service.getType().equals("Special")){
                resp.put("video_title", key.length() == 0 ? videos.get(0).getVideotitle() : key);
            }
            resp.put("source",source_view);
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);



        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("fail", "sum");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "getlive", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> getlive(@RequestParam(defaultValue = "") String vps) {
        JSONObject resp = new JSONObject();
        if (vps.length() == 0) {
            resp.put("status", "fail");
            resp.put("message", "Vps không để trống");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }

        Random ran = new Random();
        try {
            Thread.sleep(ran.nextInt(1000));
            Long historieId = historyViewRepository.getAccToLive(vps.trim());
            if(historieId==null){
                resp.put("status", "fail");
                resp.put("fail", "user");
                resp.put("message", "Không còn user để view!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }
            List<VideoView> videos = null;
            List<HistoryView> histories = historyViewRepository.getHistoriesById(historieId);
            if (histories.get(0).getGeo().trim().equals("live-vn")) {
                videos = videoViewRepository.getvideoPreVer2VNTEST(orderTrue.getValue());
            } else if(histories.get(0).getGeo().trim().equals("live-us")) {
                videos = videoViewRepository.getvideoPreVer2USTEST(orderTrue.getValue());
            }else{
                historyViewRepository.save(histories.get(0));
                resp.put("status", "fail");
                resp.put("username", histories.get(0).getUsername());
                resp.put("fail", "video");
                resp.put("message", "Không còn video để view!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }
            if (videos.size() > 0) {
                histories.get(0).setTimeget(System.currentTimeMillis());
                histories.get(0).setVideoid(videos.get(0).getVideoid());
                histories.get(0).setOrderid(videos.get(0).getOrderid());
                histories.get(0).setChannelid(videos.get(0).getChannelid());
            } else {
                historyViewRepository.save(histories.get(0));
                resp.put("status", "fail");
                resp.put("username", histories.get(0).getUsername());
                resp.put("fail", "video");
                resp.put("message", "Không còn video để view!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }
            String[] proxy;
            List<Proxy> proxies;
            String geo_proxy=histories.get(0).getGeo().trim().split("-")[1];
            if(ipV4Repository.checkIPv4Live(histories.get(0).getTypeproxy())==0){
                proxies = proxyRepository.getProxyNotRunningAndLive(geo_proxy);
                if(proxies.size()>0){
                    proxy=proxies.get(0).getProxy().split(":");
                }else{
                    histories.get(0).setTimeget(System.currentTimeMillis());
                    historyViewRepository.save(histories.get(0));
                    resp.put("status", "fail");
                    resp.put("fail", "proxy");
                    resp.put("message", "Hết proxy khả dụng!");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
            }else{
                proxy = histories.get(0).getProxy().split(":");
            }
            resp.put("proxy",proxy[0] + ":" + proxy[1] + ":1:1");

            Service service = serviceRepository.getServiceNoCheckEnabled(videos.get(0).getService());

            histories.get(0).setTimeget(System.currentTimeMillis());
            histories.get(0).setRunning(1);
            historyViewRepository.save(histories.get(0));
            if(videos.get(0).getTimestart()>=System.currentTimeMillis()){
                List<Long> arrTime = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    arrTime.add(System.currentTimeMillis());
                }
                for (int i = 0; i < 15; i++) {
                    arrTime.add(TimeUnit.MINUTES.toMillis((ran.nextInt((int)(service.getMaxtime()*0.1)))) +videos.get(0).getTimestart());
                }
                for (int i = 0; i < 25; i++) {
                    arrTime.add(videos.get(0).getTimestart()+ TimeUnit.MINUTES.toMillis((long)(service.getMaxtime()*0.1))+TimeUnit.MINUTES.toMillis(ran.nextInt((int)(service.getMaxtime()*0.4))));
                }
                for (int i = 0; i < 40; i++) {
                    arrTime.add(videos.get(0).getTimestart()+ TimeUnit.MINUTES.toMillis((long)(service.getMaxtime()*0.4))+TimeUnit.MINUTES.toMillis(ran.nextInt((int)(service.getMaxtime()*0.6))));
                }

                resp.put("time_start", arrTime.get(ran.nextInt(arrTime.size())));
            }else{
                resp.put("time_start", System.currentTimeMillis());
            }
            resp.put("channel_id", videos.get(0).getChannelid());
            resp.put("status", "true");
            resp.put("time_end", (videos.get(0).getTimestart()+ TimeUnit.MINUTES.toMillis((long)(service.getMaxtime()*1.5))+TimeUnit.MINUTES.toMillis((ran.nextInt((5))))));
            resp.put("video_id", videos.get(0).getVideoid());
            resp.put("video_title", videos.get(0).getVideotitle());
            resp.put("geo", "live");
            resp.put("username", histories.get(0).getUsername());
            resp.put("like", "fail");
            resp.put("sub", "fail");

            String list_key = dataOrderRepository.getListKeyByOrderid(videos.get(0).getOrderid());
            String key = "";
            if (list_key != null && list_key.length() != 0) {
                String[] keyArr = list_key.split(",");
                key = keyArr[ran.nextInt(keyArr.length)];
            }
            resp.put("suggest_type", "fail");
            resp.put("suggest_key", key.length() == 0 ? videos.get(0).getVideotitle() : key);
            resp.put("suggest_video", "");
            List<String> arrSource = new ArrayList<>();
            for (int i = 0; i < service.getSuggest(); i++) {
                arrSource.add("suggest");
            }
            for (int i = 0; i < service.getSearch(); i++) {
                arrSource.add("search");
            }
            for (int i = 0; i < service.getDtn(); i++) {
                arrSource.add("dtn");
            }
            for (int i = 0; i < service.getEmbed(); i++) {
                arrSource.add("embed");
            }
            for (int i = 0; i < service.getDirect(); i++) {
                arrSource.add("direct");
            }
            for (int i = 0; i < service.getExternal(); i++) {
                arrSource.add("external");
            }
            for (int i = 0; i < service.getPlaylists(); i++) {
                arrSource.add("playlists");
            }
            String source_view=arrSource.get(ran.nextInt(arrSource.size())).trim();
            if(source_view.equals("suggest")&&service.getType().equals("Special")){
                resp.put("suggest_type", "true");
            }else if(source_view.equals("search")&&service.getType().equals("Special")){
                resp.put("video_title", key.length() == 0 ? videos.get(0).getVideotitle() : key);
            }
            resp.put("source",source_view);
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);



        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("fail", "sum");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/updatevideoid", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> updatevideoid(@RequestParam(defaultValue = "") String username,
                                         @RequestParam(defaultValue = "") String videoid) {
        JSONObject resp = new JSONObject();
        if (username.length() == 0) {
            resp.put("status", "fail");
            resp.put("message", "Username không để trống");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        if (videoid.length() == 0) {
            resp.put("status", "fail");
            resp.put("message", "videoid không để trống");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        try {
            //Long historieId = historyViewRepository.getId(username);
            HistoryView historyView = historyViewRepository.getHistoryViewByUsername(username.trim());
            if (historyView == null) {
                resp.put("status", "fail");
                resp.put("message", "Không tìm thấy username!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            } else {
                char target = ',';
                long count = historyView.getListvideo().trim().chars().filter(ch -> ch == target).count();

                if(count>=3){
                    //int occurrence = (int)count-2;  // Lần xuất hiện thứ n cần tìm
                    OptionalInt position = IntStream.range(0, historyView.getListvideo().trim().length())
                            .filter(i -> historyView.getListvideo().trim().charAt(i) == target)
                            .skip(count-3)//occurrence-1
                            .findFirst();
                    historyView.setListvideo(historyView.getListvideo().trim().substring(position.getAsInt()+1)+videoid.trim()+",");
                }else{
                    historyView.setListvideo(historyView.getListvideo()+videoid.trim()+",");
                }
                historyViewRepository.save(historyView);

                /*
                if (historyViewRepository.getListVideoById(historieId).length() > 34) {
                    historyViewRepository.updateListVideoNew(videoid, historieId);
                } else {
                    historyViewRepository.updateListVideo(videoid, historieId);
                }

                 */
                resp.put("status", "true");
                resp.put("message", "Update videoid vào history thành công!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/update", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> update(@RequestParam(defaultValue = "") String username,
                                  @RequestParam(defaultValue = "") String videoid, @RequestParam(defaultValue = "") String channelid, @RequestParam(defaultValue = "0") Integer duration,@RequestParam(defaultValue = "0") Integer service_id) {
        JSONObject resp = new JSONObject();

        if (username.length() == 0) {
            resp.put("status", "fail");
            resp.put("message", "Username không để trống");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        if (videoid.length() == 0) {
            resp.put("status", "fail");
            resp.put("message", "Videoid không để trống");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }

        try {
            HistoryView historyView = historyViewRepository.getHistoryViewByUsername(username.trim());
            if (historyView == null) {
                resp.put("status", "fail");
                resp.put("message", "Không tìm thấy username!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            } else {

                Vps vps=vpsRepository.getVpsByName(historyView.getVps());
                System.out.println(vps.getVpsoption());
                if(vps.getVpsoption().equals("smm")){
                    OrderRunning orderRunning=orderRunningRepository.get_Order_By_Order_Key(channelid.trim());
                    if(orderRunning!=null){
                        youtubeUpdate.youtube_subscriber(username.trim(),channelid.trim());
                        HistorySum historySum=new HistorySum();
                        historySum.setOrderRunning(orderRunning);
                        historySum.setAccount_id(username.trim());
                        historySum.setViewing_time(duration);
                        historySum.setAdd_time(System.currentTimeMillis());
                        try {
                            historySumRepository.save(historySum);
                        } catch (Exception e) {
                            try {
                                historySumRepository.save(historySum);
                            } catch (Exception f) {
                            }
                        }
                    }else{
                        orderRunning=orderRunningRepository.get_Order_By_Order_Key(videoid.trim());
                        if(orderRunning!=null){
                            if(orderRunning.getService().getTask().equals("like")){
                                youtubeUpdate.youtube_like(username.trim(),videoid.trim());
                                HistorySum historySum=new HistorySum();
                                historySum.setOrderRunning(orderRunning);
                                historySum.setAccount_id(username.trim());
                                historySum.setViewing_time(duration);
                                historySum.setAdd_time(System.currentTimeMillis());
                                try {
                                    historySumRepository.save(historySum);
                                } catch (Exception e) {
                                    try {
                                        historySumRepository.save(historySum);
                                    } catch (Exception f) {
                                    }
                                }
                            }else if(orderRunning.getService().getTask().equals("view")){
                                youtubeUpdate.youtube_view(username.trim(),videoid.trim());
                                HistorySum historySum=new HistorySum();
                                historySum.setOrderRunning(orderRunning);
                                historySum.setAccount_id(username.trim());
                                historySum.setViewing_time(duration);
                                historySum.setAdd_time(System.currentTimeMillis());
                                try {
                                    historySumRepository.save(historySum);
                                } catch (Exception e) {
                                    try {
                                        historySumRepository.save(historySum);
                                    } catch (Exception f) {
                                    }
                                }
                            }

                        }
                    }

                }else{
                    if (duration > 0) {
                        HistoryViewSum historySum = new HistoryViewSum();
                        historySum.setVideoid(videoid.trim());
                        historySum.setUsername(username);
                        historySum.setTime(System.currentTimeMillis());
                        historySum.setChannelid(channelid);
                        historySum.setDuration(duration);
                        try {
                            historyViewSumRepository.save(historySum);
                        } catch (Exception e) {
                            try {
                                historyViewSumRepository.save(historySum);
                            } catch (Exception f) {
                            }
                        }
                    }else{
                        //historyViewRepository.updateduration(duration,username,videoid);
                        resp.put("status", "fail");
                        resp.put("message", "Không update duration !");
                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                    }
                }
                historyViewRepository.update_Task_Done(username.trim());
                resp.put("status", "true");
                resp.put("message", "Update view thành công!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);

            }
        } catch (Exception e) {
            StackTraceElement stackTraceElement = Arrays.stream(e.getStackTrace()).filter(ste -> ste.getClassName().equals(this.getClass().getName())).collect(Collectors.toList()).get(0);
            LogError logError =new LogError();
            logError.setMethod_name(stackTraceElement.getMethodName());
            logError.setLine_number(stackTraceElement.getLineNumber());
            logError.setClass_name(stackTraceElement.getClassName());
            logError.setFile_name(stackTraceElement.getFileName());
            logError.setMessage(e.getMessage());
            logError.setAdd_time(System.currentTimeMillis());
            Date date_time = new Date(System.currentTimeMillis());
            // Tạo SimpleDateFormat với múi giờ GMT+7
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+7"));
            String formattedDate = sdf.format(date_time);
            logError.setDate_time(formattedDate);
            logErrorRepository.save(logError);
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "delthreadbyusername", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> delthreadbyusername(@RequestParam(defaultValue = "") String username, @RequestParam(defaultValue = "") String videoid) {
        JSONObject resp = new JSONObject();
        if (username.length() == 0) {
            resp.put("status", "fail");
            resp.put("message", "username không để trống");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        try {
            Long historieId = historyViewRepository.getId(username.trim());
            historyViewRepository.resetThreadBuffhById(historieId);
            Long historieIdC = historyCommentRepository.getId(username);
            historyCommentRepository.resetThreadBuffhById(historieIdC);
            //dataCommentRepository.resetRunningComment(username.trim());
            resp.put("status", "true");
            resp.put("message", "Update running thành công!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(value = "getviewbuff7day", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> gettimebuff7day(@RequestParam(defaultValue = "") String user) {
        JSONObject resp = new JSONObject();
        try {
            List<String> time7day;
            if (user.length() == 0) {
                time7day = historyViewSumRepository.Gettimebuff7day();
            } else {
                time7day = historyViewSumRepository.Gettimebuff7day(user.trim());
            }

            JSONArray jsonArray = new JSONArray();
            Integer maxview = 0;

            for (int i = 0; i < time7day.size(); i++) {
                //System.out.println(time7day.get(i).split(",")[1]);
                if (maxview < Integer.parseInt(time7day.get(i).split(",")[1])) {
                    maxview = Integer.parseInt(time7day.get(i).split(",")[1]);
                }
            }
            for (int i = 0; i < time7day.size(); i++) {
                JSONObject obj = new JSONObject();
                obj.put("date", time7day.get(i).split(",")[0]);
                obj.put("view", time7day.get(i).split(",")[1]);
                obj.put("maxview", maxview.toString());

                jsonArray.add(obj);
            }
            resp.put("view7day", jsonArray);

            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "delthreadcron", produces = "application/hal+json;charset=utf8")
    public ResponseEntity<String> delthreadcron() {
        JSONObject resp = new JSONObject();
        try {
            historyViewRepository.resetThreadThan90mcron();
            historyViewRepository.resetThreadcron();
            resp.put("status", "true");
            resp.put("message", "Reset thread error thành công!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "delhistorysumcron", produces = "application/hal+json;charset=utf8")
    public ResponseEntity<String> delhistorysumcron() {
        JSONObject resp = new JSONObject();
        try {
            historyViewSumRepository.DelHistorySum();
            resp.put("status", "true");
            resp.put("message", "Delete history thành công!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(value = "delnamebyvps", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> delnamebyvps(@RequestParam(defaultValue = "") String vps) throws InterruptedException {
        JSONObject resp = new JSONObject();
        if (vps.length() == 0) {
            resp.put("status", "fail");
            resp.put("message", "vps không để trống");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        if (historyViewRepository.PROCESSLISTVIEW() >= 30) {
            Random ran = new Random();
            Thread.sleep(1000 + ran.nextInt(2000));
            resp.put("status", "fail");
            resp.put("message", "Đợi reset threads...");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }
        try {
            historyViewRepository.resetThreadViewByVps(vps.trim());
            historyCommentRepository.resetThreadViewByVps(vps.trim());
            dataCommentRepository.resetRunningCommentByVPS(vps.trim());
            dataReplyCommentRepository.resetRunningCommentByVPS(vps.trim());
            resp.put("status", "true");
            resp.put("message", "Update running thành công!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "vpsrunning", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> vpsrunning(@RequestHeader(defaultValue = "") String Authorization) {
        JSONObject resp = new JSONObject();
        //Integer checktoken= adminRepository.FindAdminByToken(Authorization.split(",")[0]);
        List<Admin> admins = adminRepository.FindByToken(Authorization.trim());
        if (Authorization.length() == 0 || admins.size() == 0) {
            resp.put("status", "fail");
            resp.put("message", "Token expired");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        try {
            List<VpsRunning> vpsRunnings = historyViewRepository.getvpsrunning();

            //String a=orderRunnings.toString();
            JSONArray jsonArray = new JSONArray();

            //JSONObject jsonObject=new JSONObject().put("")
            //JSONObject jsonObject= (JSONObject) new JSONObject().put("Channelid",orderRunnings.get(0).toString());
            //jsonArray.add(orderRunnings);
            Integer sum_total = 0;
            for (int i = 0; i < vpsRunnings.size(); i++) {
                sum_total = sum_total + vpsRunnings.get(i).getTotal();
                JSONObject obj = new JSONObject();
                obj.put("vps", vpsRunnings.get(i).getVps());
                obj.put("total", vpsRunnings.get(i).getTotal());
                obj.put("time", vpsRunnings.get(i).getTime());
                jsonArray.add(obj);
            }
            //JSONArray lineItems = jsonObject.getJSONArray("lineItems");


            resp.put("computers", jsonArray);
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "deleteviewthan24h", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> deleteAllViewThan24h() {
        JSONObject resp = new JSONObject();
        try {
            historyViewRepository.deleteAllViewThan24h();
            resp.put("status", "true");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

}
