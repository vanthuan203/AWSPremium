package com.nts.awspremium.controller;

import com.nts.awspremium.model.*;
import com.nts.awspremium.model_system.OrderThreadCheck;
import com.nts.awspremium.platform.youtube.YoutubeTask;
import com.nts.awspremium.platform.youtube.YoutubeUpdate;
import com.nts.awspremium.repositories.*;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/task")
public class HistoryTaskController {
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
    private ProxyJPTrue proxyJPTrue;
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
    private ModeOptionRepository modeOptionRepository;

    @Autowired
    private AccountTaskRepository accountTaskRepository;

    @Autowired
    private OrderRunningRepository orderRunningRepository;

    @Autowired
    private HistorySumRepository historySumRepository;
    @Autowired
    private LogErrorRepository logErrorRepository;


    @GetMapping(value = "getTaskAndUser", produces = "application/hal+json;charset=utf8")
    public ResponseEntity<Map<String, Object>> getTaskAndUser(@RequestParam(defaultValue = "") String vps) {
        Map<String, Object> resp = new LinkedHashMap<>();
        Map<String, Object> data = new LinkedHashMap<>();
        if (vps.length() == 0) {
            resp.put("status", false);
            data.put("message", "vps không để trống");
            resp.put("data", data);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }
        Vps vps_check=vpsRepository.getVpsByName(vps.trim());
        if(vps_check==null){
            resp.put("status", false);
            data.put("message", "Vps không tồn tại");
            resp.put("data", data);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }

        Long historieId = historyViewRepository.getAccToViewNoCheckProxy(vps.trim());
        if (historieId == null) {
            vps_check.setTask_time(System.currentTimeMillis());
            vpsRepository.save(vps_check);
            resp.put("status",false);
            data.put("message", "Không còn user để view!");
            resp.put("data", data);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }


        List<VideoView> videos = null;
        List<HistoryView> histories = historyViewRepository.getHistoriesById(historieId);

        Map<String, Object> get_task =null;
        Random ran = new Random();
        try {
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
                    get_task=youtubeTask.youtube_view(histories.get(0).getUsername(),vps_check.getVpsoption());
                }else if(task.equals("youtube_like")){
                    get_task=youtubeTask.youtube_like(histories.get(0).getUsername(),vps_check.getVpsoption());
                }else if(task.equals("youtube_subscriber")){
                    get_task=youtubeTask.youtube_subscriber(histories.get(0).getUsername(),vps_check.getVpsoption());
                }else if(task.equals("youtube_live")){
                    get_task=youtubeTask.youtube_live(histories.get(0).getUsername(),vps_check.getVpsoption());
                }
                if(get_task!=null?get_task.get("status").equals(true):false){
                    break;
                }
            }
            if(get_task==null){
                histories.get(0).setTimeget(System.currentTimeMillis());
                histories.get(0).setTask_done(histories.get(0).getTask_done()+1);
                historyViewRepository.save(histories.get(0));

                vps_check.setTask_time(System.currentTimeMillis());
                vpsRepository.save(vps_check);

                resp.put("status",false);
                resp.put("fail", "video");
                data.put("message", "Không còn video để view!");
                resp.put("data", data);
                return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
            }else if(get_task.get("status").equals(false)){
                histories.get(0).setTimeget(System.currentTimeMillis());
                histories.get(0).setTask_done(histories.get(0).getTask_done()+1);
                historyViewRepository.save(histories.get(0));

                vps_check.setTask_time(System.currentTimeMillis());
                vpsRepository.save(vps_check);

                resp.put("status", false);
                data.put("message", "Không còn video để view!");
                resp.put("data", data);
                return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
            }
            Map<String, Object> respJson=new LinkedHashMap<>();
            Map<String, Object>  dataJson= (Map<String, Object>) get_task.get("data");

            Thread.sleep(ran.nextInt(150));
            if(!orderThreadCheck.getValue().contains(dataJson.get("order_id").toString())){
                histories.get(0).setTimeget(System.currentTimeMillis());
                histories.get(0).setTask_time(System.currentTimeMillis());
                historyViewRepository.save(histories.get(0));
                resp.put("status", "fail");
                data.put("message", "Không còn video để view!");
                resp.put("data", data);
                return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
            }

            histories.get(0).setTimeget(System.currentTimeMillis());
            histories.get(0).setVideoid( dataJson.get("video_id").toString());
            histories.get(0).setOrderid(Long.parseLong(dataJson.get("order_id").toString()));
            histories.get(0).setChannelid(dataJson.get("channel_id").toString());
            histories.get(0).setTimeget(System.currentTimeMillis());
            histories.get(0).setRunning(1);
            historyViewRepository.save(histories.get(0));

            respJson.put("status",true);
            respJson.put("data",dataJson);
            return new ResponseEntity<>(resp, HttpStatus.OK);

        } catch (Exception e) {
            resp.put("status", "fail");
            StackTraceElement stackTraceElement = Arrays.stream(e.getStackTrace()).filter(ste -> ste.getClassName().equals(this.getClass().getName())).collect(Collectors.toList()).get(0);
            System.out.println(stackTraceElement.getMethodName());
            System.out.println(stackTraceElement.getLineNumber());
            System.out.println(stackTraceElement.getClassName());
            System.out.println(stackTraceElement.getFileName());
            System.out.println("Error : " + e.getMessage());
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }
    }


}
