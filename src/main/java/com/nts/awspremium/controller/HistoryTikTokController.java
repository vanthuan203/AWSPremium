package com.nts.awspremium.controller;

import com.nts.awspremium.GoogleApi;
import com.nts.awspremium.Openai;
import com.nts.awspremium.TikTokApi;
import com.nts.awspremium.model.*;
import com.nts.awspremium.repositories.*;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/tiktok")
public class HistoryTikTokController {
    @Autowired
    private ChannelTikTokRepository channelTikTokRepository;
    @Autowired
    private HistoryTiktokRepository historyTiktokRepository;

    @Autowired
    private HistoryFollowerTiktokRepository historyFollowerTiktokRepository;

    @Autowired
    private HistoryFollowerTiktok24hRepository historyFollowerTiktok24hRepository;

    @Autowired
    private ActivityTikTokRepository activityTikTokRepository;
    @Autowired
    private HistoryFollowerTikTokSumRepository historyFollowerTikTokSumRepository;
    @Autowired
    private OrderFollowerTrue orderFollowerTrue;
    @Autowired
    private SettingTikTokRepository settingTikTokRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private IpV4Repository ipV4Repository;
    @Autowired
    private AccountRegTikTokRepository accountRegTikTokRepository;
    @Autowired
    private AccountTikTokRepository accountTikTokRepository;

    @Autowired
    private Proxy_IPV4_TikTokRepository proxyIpv4TikTokRepository;
    @GetMapping(value = "get", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> get(@RequestParam(defaultValue = "") String username) {
        JSONObject resp = new JSONObject();
        if (username.length() == 0) {
            resp.put("status", "fail");
            resp.put("message", "Username không để trống");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        Random ran = new Random();
        try {
            Thread.sleep(ran.nextInt(500));
            HistoryTikTok historyTikTok = historyTiktokRepository.getHistoryTikTokByUsername(username.trim());
            List<ChannelTiktok> channelTiktoks = null;
            if (historyTikTok == null) {
                resp.put("message", "Username không tồn tại!");
                resp.put("status", "fail");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
            } else {
                if(accountTikTokRepository.checkDeviceAndVPS(historyTikTok.getDevice_id().trim(),historyTikTok.getVps().trim())>0){
                    accountTikTokRepository.updateVPSByDevice(historyTikTok.getVps().trim(),historyTikTok.getDevice_id().trim());
                    accountRegTikTokRepository.updateVPSByDevice(historyTikTok.getVps().trim(),historyTikTok.getDevice_id().trim());
                    historyTiktokRepository.updateVPSByDevice(historyTikTok.getVps().trim(),historyTikTok.getDevice_id().trim());
                }
                if(historyTikTok.getOption_running()==0){
                    if(activityTikTokRepository.checkActivityByUsername(username.trim())==0){
                        String proxy=accountTikTokRepository.getProxyByUsername(username.trim());
                        Random rand=new Random();
                        if(ipV4Repository.checkIPv4Live(accountTikTokRepository.getProxyByUsername(username.trim()))==0){
                            String proxy_rand=proxyIpv4TikTokRepository.getProxyRandTikTok();
                            if(proxy_rand!=null){
                                proxy=proxy_rand;
                            }else{
                                historyTikTok.setTimeget(System.currentTimeMillis());
                                historyTiktokRepository.save(historyTikTok);
                                resp.put("status", "fail");
                                resp.put("username", historyTikTok.getUsername());
                                resp.put("fail", "proxy");
                                resp.put("message", "Hết proxy khả dụng");
                                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                            }
                        }
                        historyTikTok.setTimeget(System.currentTimeMillis());
                        historyTikTok.setRunning(2);
                        historyTiktokRepository.save(historyTikTok);
                        resp.put("status", "true");
                        resp.put("proxy", proxy);
                        resp.put("task","activity");
                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                    }else{
                        historyTikTok.setTimeget(System.currentTimeMillis());
                        historyTiktokRepository.save(historyTikTok);
                        resp.put("status", "fail");
                        resp.put("username", historyTikTok.getUsername());
                        resp.put("fail", "activity");
                        resp.put("message", "Không có nhiêm vụ nuôi tk!");
                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                    }
                }
                SettingTiktok settingTiktok=settingTikTokRepository.getReferenceById(1L);
                if(historyFollowerTiktok24hRepository.countFollower24hByUsername(username.trim()+"%")>=settingTiktok.getMax_follower()){
                    historyTikTok.setTimeget(System.currentTimeMillis());
                    historyTiktokRepository.save(historyTikTok);
                    resp.put("status", "fail");
                    resp.put("username", historyTikTok.getUsername());
                    resp.put("fail", "follower");
                    resp.put("message", "Đủ "+settingTiktok.getMax_follower().toString()+" follower trong 24H!");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
                String list_tiktok_id=historyFollowerTiktokRepository.getListTiktokID(username.trim());
                channelTiktoks = channelTikTokRepository.getChannelTiktokByTask(list_tiktok_id==null?"":list_tiktok_id,orderFollowerTrue.getValue());
                if (channelTiktoks.size() > 0) {
                    String proxy=accountTikTokRepository.getProxyByUsername(username.trim());
                    Random rand=new Random();
                    if(ipV4Repository.checkIPv4Live(accountTikTokRepository.getProxyByUsername(username.trim()))==0){
                        String proxy_rand=proxyIpv4TikTokRepository.getProxyRandTikTok();
                        if(proxy_rand!=null){
                            proxy=proxy_rand;
                        }else{
                            historyTikTok.setTimeget(System.currentTimeMillis());
                            historyTiktokRepository.save(historyTikTok);
                            resp.put("status", "fail");
                            resp.put("username", historyTikTok.getUsername());
                            resp.put("fail", "proxy");
                            resp.put("message", "Hết proxy khả dụng");
                            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                        }
                    }
                    historyTikTok.setTimeget(System.currentTimeMillis());
                    historyTikTok.setOrderid(channelTiktoks.get(0).getOrderid());
                    historyTikTok.setRunning(1);
                    historyTiktokRepository.save(historyTikTok);
                    resp.put("status", "true");
                    resp.put("proxy", proxy);
                    resp.put("task", "follower");
                    resp.put("tiktok_id",channelTiktoks.get(0).getTiktok_id());

                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);

                } else {
                        historyTikTok.setTimeget(System.currentTimeMillis());
                        historyTiktokRepository.save(historyTikTok);
                        resp.put("status", "fail");
                        resp.put("username", historyTikTok.getUsername());
                        resp.put("fail", "follower");
                        resp.put("message", "Không có nhiêm vụ follower!");
                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                    }
            }

        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("fail", "sum");
            resp.put("message", e.getMessage());
            StackTraceElement stackTraceElement = Arrays.stream(e.getStackTrace()).filter(ste -> ste.getClassName().equals(this.getClass().getName())).collect(Collectors.toList()).get(0);
            System.out.println(stackTraceElement.getMethodName());
            System.out.println(stackTraceElement.getLineNumber());
            System.out.println(stackTraceElement.getClassName());
            System.out.println(stackTraceElement.getFileName());
            System.out.println("Error : " + e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping(value = "/updateTask", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> updateTask(@RequestParam(defaultValue = "") String username,@RequestParam  Boolean status,@RequestParam(defaultValue = "") String task,
                                         @RequestParam(defaultValue = "") String tiktok_id) {
        JSONObject resp = new JSONObject();
        if (username.length() == 0) {
            resp.put("status", "fail");
            resp.put("message", "username không để trống");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        if (task.length() == 0) {
            resp.put("status", "fail");
            resp.put("message", "task không để trống");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        } else {
            if (task.equals("activity")) {
                if (status == true) {
                    historyTiktokRepository.resetThreadByUsername(username.trim());
                    ActivityTikTok activityTikTok = new ActivityTikTok();
                    activityTikTok.setUsername(username.trim());
                    activityTikTok.setTime_update(System.currentTimeMillis());
                    activityTikTokRepository.save(activityTikTok);
                    resp.put("status", "true");
                    resp.put("message", "Update activity thành công");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                } else {
                    resp.put("status", "true");
                    resp.put("message", "Update activity thành công");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
            }
        }
        if (tiktok_id.length() == 0) {
            resp.put("status", "fail");
            resp.put("message", "tiktok_id không để trống");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        if (status == null) {
            resp.put("status", "fail");
            resp.put("message", "status không để trống");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        try {
            historyTiktokRepository.resetThreadByUsername(username.trim());
            if (status == true) {
                HistoryFollowerTikTok historyFollowerTikTok=historyFollowerTiktokRepository.getHistoriesByUsername(username.trim());
                if(historyFollowerTikTok==null){
                    HistoryFollowerTikTok historyFollowerTikTokNew=new HistoryFollowerTikTok();
                    historyFollowerTikTokNew.setUsername(username.trim());
                    historyFollowerTikTokNew.setList_tiktok_id(tiktok_id.trim()+"|");
                    historyFollowerTikTokNew.setTime_update(System.currentTimeMillis());
                    historyFollowerTiktokRepository.save(historyFollowerTikTokNew);
                }else{
                    historyFollowerTikTok.setList_tiktok_id(historyFollowerTikTok.getList_tiktok_id()+tiktok_id+"|");
                    historyFollowerTikTok.setTime_update(System.currentTimeMillis());
                    historyFollowerTiktokRepository.save(historyFollowerTikTok);
                }

                HistoryFollower24hTikTok historyFollower24hTikTok =new HistoryFollower24hTikTok();
                historyFollower24hTikTok.setCode(username+tiktok_id.trim());
                historyFollower24hTikTok.setTime(System.currentTimeMillis());
                historyFollowerTiktok24hRepository.save(historyFollower24hTikTok);


                HistoryFollowerTikTokSum historyFollowerTikTokSum = new HistoryFollowerTikTokSum();
                historyFollowerTikTokSum.setTiktok_id(tiktok_id.trim());
                historyFollowerTikTokSum.setUsername(username.trim());
                historyFollowerTikTokSum.setTime(System.currentTimeMillis());
                historyFollowerTikTokSumRepository.save(historyFollowerTikTokSum);
                resp.put("status", "true");
                resp.put("message", "Update orderid vào history thành công!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }
            resp.put("status", "true");
            resp.put("message", "Reset thread thành công!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping(value = "test", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> test(@RequestParam(defaultValue = "") String vps) throws InterruptedException {
        JSONObject resp = new JSONObject();
        String aa="You are a YouTube comment generator. Your task is to create emotionally expressive, human-like comments that reflect realistic user reactions to a video.\n" +
                "\n" +
                "You are given the following:\n" +
                "- %s: the title of the video\n" +
                "- %s: the name of the creator\n" +
                "-  %s: the full transcript of the video\n" +
                "\n" +
                "From these, understand the video’s content and emotional tone. Identify the key themes, subject matter, and emotional moments.\n" +
                "\n" +
                "Then, generate #quantity (At least the value of the number of comments must be sufficient) comments in the language detected from the %s, which is %s.\n" +
                "\n" +
                "Comment generation rules:\n" +
                "\n" +
                "1. **Comment relevance (CRITICAL):**\n" +
                "   - Comments must relate to the actual content of the video or the main theme from the %s\n" +
                "   - Do not insert random emotional reactions that are irrelevant to the topic\n" +
                "   - Use appropriate tone and emotion for the topic:\n" +
                "     - For serious or analytical topics (e.g., economics, history), use thoughtful, shocked, skeptical, or impressed tones\n" +
                "     - For emotional topics (e.g., family, drama), use sadness, empathy, shock, or heartache\n" +
                "     - For funny/light content (e.g., comedy), use laughter, surprise, or silliness\n" +
                "\n" +
                "2. **Comment type distribution:**\n" +
                "   - 80%% of comments must be short (1–5 words)\n" +
                "   - 20%% of comments must be longer (1–3 sentences)\n" +
                "\n" +
                "3. **Short comment style (80%%):**\n" +
                "   - Must express strong, but context-appropriate emotions\n" +
                "   - No periods at the end\n" +
                "   - Use “???” instead of “?”\n" +
                "   - Emojis and expressive faces are allowed: \uD83D\uDE2D \uD83D\uDE02 \uD83E\uDD2F \uD83E\uDD2C \uD83D\uDC80 \uD83D\uDE22 :((( :))) >.< :)) =)))\n" +
                "   - Examples (adapt based on content theme):\n" +
                "     - Chính sách này lạ quá???\n" +
                "     - Khó hiểu thật sự\n" +
                "     - Phân tích sâu ghê\n" +
                "     - Cười không ngậm được mồm \uD83D\uDE02\n" +
                "     - Buồn ghê á\n" +
                "     - Căng vậy luôn???\n" +
                "     - Giải thích đỉnh luôn\n" +
                "\n" +
                "4. **Long comment style (20%%):**\n" +
                "   - Must feel like the commenter watched the full video\n" +
                "   - Language should reflect the tone of the topic\n" +
                "   - Content can be reflective, impressed, or mildly critical, depending on the video\n" +
                "   - Examples:\n" +
                "     - Phân tích rất rõ ràng, mình chưa từng hiểu vấn đề này theo góc nhìn như vậy\n" +
                "     - Cảm ơn video đã mở mang nhiều về chính sách tài khóa, trình bày dễ hiểu và dễ theo dõi\n" +
                "     - Đoạn nói về nợ công cuối video thực sự khiến mình suy nghĩ nhiều\n" +
                "\n" +
                "5. **Output formatting:**\n" +
                "   - Do not number the comments\n" +
                "   - Do not leave blank lines between comments\n" +
                "   - Return a plain list of comments, one per line\n" +
                "\n" +
                "All comments must match the tone and theme of the video. Avoid generic or emotional mismatches. Every comment should feel like a real human reaction from someone who either watched the full video or paid close attention to the title and main message.";
        String prompt = String.format(
                aa,
                "Video Title",     // %s 1
                "Creator Name",    // %s 2
                "Transcript here", // %s 3
                15,                // %d
                "Transcript here", // %s 4
                "Vietnamese",      // %s 5
                "Transcript here"  // %s 6
        );
        System.out.println(prompt);
        resp.put("message", "vps không để trống");
        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
    }

    @GetMapping(value = "/dellHisFollower24HByCron", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> dellHisFollower24HByCron() {
        JSONObject resp = new JSONObject();
        try{
            historyFollowerTiktok24hRepository.deleteAllByThan24h();
            resp.put("status", "true");
            resp.put("message", "Delete follower >24h thành công!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "delThreadErrorByCron", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> delThreadErrorByCron() {
        JSONObject resp = new JSONObject();
        try {
            historyTiktokRepository.resetThreadcron();
            resp.put("status", "true");
            resp.put("message", "Reset thread error thành công!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "delHistorySumByCron", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> delHistorySumByCron() {
        JSONObject resp = new JSONObject();
        try {
            historyFollowerTikTokSumRepository.DelHistorySum();
            resp.put("status", "true");
            resp.put("message", "Delete history thành công!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "updateOptionRunningByCron", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> updateOptionRunningByCron() {
        JSONObject resp = new JSONObject();
        try {
            historyTiktokRepository.updateOptionRunningFollower();
            resp.put("status", "true");
            resp.put("message", "update running follower thành công!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

}
