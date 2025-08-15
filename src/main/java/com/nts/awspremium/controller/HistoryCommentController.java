package com.nts.awspremium.controller;

import com.nts.awspremium.model.*;
import com.nts.awspremium.repositories.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/historycomment")
public class HistoryCommentController {

    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ProxySettingRepository proxySettingRepository;
    @Autowired
    private VideoCommentRepository videoCommentRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private HistoryViewRepository historyViewRepository;

    @Autowired
    private HistoryCommentRepository historyCommentRepository;
    @Autowired
    private DataCommentRepository dataCommentRepository;

    @Autowired
    private DataReplyCommentRepository dataReplyCommentRepository;
    @Autowired
    private HistoryViewSumRepository historyViewSumRepository;

    @Autowired
    private HistoryCommentSumRepository historyCommentSumRepository;
    @Autowired
    private OrderCommentTrue orderCommentTrue;
    @Autowired
    private ProxyVNTrue proxyVNTrue;
    @Autowired
    private ProxyUSTrue proxyUSTrue;
    @Autowired
    private ProxyVultrTrue proxyVultrTrue;
    @Autowired
    private ProxyKRTrue proxyKRTrue;
    @Autowired
    private ProxyJPTrue proxyJPTrue;
    @Autowired
    private VpsRepository vpsRepository;
    @Autowired
    private ProxyRepository proxyRepository;
    @GetMapping(value = "getoff", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> getoff(@RequestParam(defaultValue = "") String username, @RequestParam(defaultValue = "") String vps) {
        JSONObject resp = new JSONObject();
        JSONObject fail_resp = new JSONObject();
        if (vps.length() == 0) {
            fail_resp.put("status", "fail");
            fail_resp.put("message", "Vps không để trống");
            return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        if(vpsRepository.checkVpsCmtTrue(vps.trim())==0){
            fail_resp.put("status", "fail");
            fail_resp.put("message", "Vps không chạy cmt!");
            return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
        }
        if (username.length() == 0) {
            fail_resp.put("status", "fail");
            fail_resp.put("message", "Username không để trống");
            return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        /*
        if (historyViewRepository.PROCESSLISTVIEW() >= 40) {
            resp.put("status", "fail");
            resp.put("username", "");
            resp.put("fail", "video");
            resp.put("message", "Không còn video để comment!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }
         */
        Random ran = new Random();
        try {
            Thread.sleep(ran.nextInt(1000));
            Long historieId = historyCommentRepository.getId(username);
            List<VideoComment> videos = null;
            if (historieId == null) {
                HistoryComment history = new HistoryComment();
                history.setUsername(username);
                history.setListvideo("");
                history.setRunning(0);
                history.setVps(vps);
                history.setVideoid("");
                history.setOrderid(0L);
                history.setGeo(accountRepository.getGeoByUsername(username.trim()));
                history.setTimeget(System.currentTimeMillis());
                if (history.getGeo().equals("cmt-vn")) {
                    videos = videoCommentRepository.getvideoCommentVN("",orderCommentTrue.getValue());
                } else if (history.getGeo().equals("cmt-us")) {
                    videos = videoCommentRepository.getvideoCommentUS("",orderCommentTrue.getValue());
                }else if (history.getGeo().equals("cmt-kr")) {
                    videos = videoCommentRepository.getvideoCommentKR("",orderCommentTrue.getValue());
                }else{
                    fail_resp.put("status", "fail");
                    fail_resp.put("message", "Username không cmt!");
                    return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                }
                if (videos.size() > 0) {
                    history.setVideoid(videos.get(0).getVideoid());
                    history.setTimeget(System.currentTimeMillis());
                    history.setOrderid(videos.get(0).getOrderid());
                    history.setRunning(1);
                    historyCommentRepository.save(history);

                    dataCommentRepository.updateRunningComment(System.currentTimeMillis(),username.trim(),vps.trim(),videos.get(0).getOrderid());
                    Thread.sleep(ran.nextInt(1000)+500);
                    String comment=dataCommentRepository.getCommentByOrderIdAndUsername(videos.get(0).getOrderid(),username.trim());
                    if(comment!=null){
                        if(historyCommentSumRepository.checkCommentIdTrue(Long.parseLong(comment.split(",")[0]))>0){
                            dataCommentRepository.updateRunningCommentDone(Long.parseLong(comment.split(",")[0]));
                            history.setRunning(0);
                            historyCommentRepository.save(history);
                            fail_resp.put("status", "fail");
                            fail_resp.put("username", history.getUsername());
                            fail_resp.put("fail", "video");
                            fail_resp.put("message", "Không còn video để comment!");
                            return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                        }
                        resp.put("comment_id", comment.split(",")[0]);
                        resp.put("comment", comment.substring(comment.indexOf(",")+1));
                    }else{
                        history.setRunning(0);
                        historyCommentRepository.save(history);
                        fail_resp.put("status", "fail");
                        fail_resp.put("username", history.getUsername());
                        fail_resp.put("fail", "video");
                        fail_resp.put("message", "Không còn video để comment!");
                        return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                    }
                    String[] proxy=accountRepository.getProxyByUsername(username.trim()).split(":");
                    if(proxy[0].trim().length()<4){
                        List<Proxy> proxies=null;
                        if (accountRepository.getGeoByUsername(username.trim()).equals("cmt-vn")) {
                            proxies=proxyRepository.getProxyFixAccountByGeo("vn");
                        } else if (accountRepository.getGeoByUsername(username.trim()).equals("cmt-us")) {
                            proxies=proxyRepository.getProxyFixAccountByGeo("us");
                        } else if (accountRepository.getGeoByUsername(username.trim()).equals("cmt-kr")) {
                            proxies=proxyRepository.getProxyFixAccountByGeo("kr");
                        }
                        if(proxies.size()!=0) {
                            proxy=proxies.get(0).getProxy().split(":");
                            Account account=accountRepository.findAccountByUsername(username.trim());
                            account.setProxy(proxies.get(0).getProxy());
                            accountRepository.save(account);
                            proxyRepository.updateProxyGet(vps,System.currentTimeMillis(),proxies.get(0).getId());
                        }
                    }else{
                        Random rand=new Random();
                        if(proxyRepository.checkProxyLiveByUsername(username.trim())==0){
                            if(history.getGeo().equals("vn")){
                                proxy=proxyVNTrue.getValue().get(rand.nextInt(proxyVNTrue.getValue().size())).split(":");
                            }else if(history.getGeo().equals("us")){
                                proxy=proxyUSTrue.getValue().get(rand.nextInt(proxyUSTrue.getValue().size())).split(":");
                            }else if(history.getGeo().equals("kr")){
                                proxy=proxyKRTrue.getValue().get(rand.nextInt(proxyKRTrue.getValue().size())).split(":");
                            }
                            if(proxy.length==0){
                                history.setRunning(0);
                                history.setTimeget(System.currentTimeMillis());
                                historyCommentRepository.save(history);
                                fail_resp.put("status", "fail");
                                fail_resp.put("message", "Proxy die!");
                                return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                            }
                        }
                    }
                    String[] proxysetting=proxySettingRepository.getUserPassByHost(proxy[0]).split(",");
                    resp.put("channel_id", videos.get(0).getChannelid());
                    resp.put("status", "true");
                    resp.put("video_id", videos.get(0).getVideoid());
                    resp.put("video_title", videos.get(0).getVideotitle());
                    resp.put("username", history.getUsername());
                    resp.put("geo", accountRepository.getGeoByUsername(username.trim()));
                    resp.put("proxy",proxy[0]+":"+proxy[1]+":"+proxysetting[0]+":"+proxysetting[1]);
                    if (ran.nextInt(10000) > 5000) {
                        resp.put("source", "dtn");
                    } else {
                        resp.put("source", "search");
                    }
                    if (videos.get(0).getDuration() > 360) {
                        resp.put("video_duration", 180 + ran.nextInt(180));
                    } else {
                        resp.put("video_duration", videos.get(0).getDuration());
                    }
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                } else {
                    history.setRunning(0);
                    historyCommentRepository.save(history);
                    fail_resp.put("status", "fail");
                    fail_resp.put("username", history.getUsername());
                    fail_resp.put("fail", "video");
                    fail_resp.put("message", "Không còn video để comment!");
                    return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                }
            } else {
                List<HistoryComment> histories = historyCommentRepository.getHistoriesById(historieId);
                //System.out.println(System.currentTimeMillis()-histories.get(0).getTimeget());
                if (System.currentTimeMillis() - histories.get(0).getTimeget() < (30000L + (long) ran.nextInt(60000))) {
                    //histories.get(0).setTimeget(System.currentTimeMillis());
                    //historyViewRepository.save(histories.get(0));
                    fail_resp.put("status", "fail");
                    fail_resp.put("username", histories.get(0).getUsername());
                    fail_resp.put("fail", "video");
                    fail_resp.put("message", "Không còn video để comment!");
                    return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                }
                if (histories.get(0).getGeo().equals("cmt-vn")) {
                    //videos=videoViewRepository.getvideoViewNoCheckMaxThreadVN(histories.get(0).getListvideo());
                    videos = videoCommentRepository.getvideoCommentVN(histories.get(0).getListvideo(),orderCommentTrue.getValue());
                } else if (histories.get(0).getGeo().equals("cmt-us")) {
                    //videos=videoViewRepository.getvideoViewNoCheckMaxThreadUS(histories.get(0).getListvideo());
                    videos = videoCommentRepository.getvideoCommentUS(histories.get(0).getListvideo(),orderCommentTrue.getValue());
                }else if (histories.get(0).getGeo().equals("cmt-kr")) {
                    //videos=videoViewRepository.getvideoViewNoCheckMaxThreadUS(histories.get(0).getListvideo());
                    videos = videoCommentRepository.getvideoCommentKR(histories.get(0).getListvideo(),orderCommentTrue.getValue());
                }else{
                    fail_resp.put("status", "fail");
                    fail_resp.put("message", "Username không cmt!");
                    return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                }
                if (videos.size() > 0) {
                    histories.get(0).setTimeget(System.currentTimeMillis());
                    histories.get(0).setVideoid(videos.get(0).getVideoid());
                    histories.get(0).setOrderid(videos.get(0).getOrderid());
                    histories.get(0).setRunning(1);
                    historyCommentRepository.save(histories.get(0));
                } else {
                    histories.get(0).setTimeget(System.currentTimeMillis());
                    histories.get(0).setRunning(0);
                    historyCommentRepository.save(histories.get(0));
                    fail_resp.put("status", "fail");
                    fail_resp.put("username", histories.get(0).getUsername());
                    fail_resp.put("fail", "video");
                    fail_resp.put("message", "Không còn video để comment!");
                    return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                }
                dataCommentRepository.updateRunningComment(System.currentTimeMillis(),username.trim(),vps.trim(),videos.get(0).getOrderid());
                Thread.sleep(ran.nextInt(1000)+500);
                String comment=dataCommentRepository.getCommentByOrderIdAndUsername(videos.get(0).getOrderid(),username.trim());
                if(comment!=null){
                    if(historyCommentSumRepository.checkCommentIdTrue(Long.parseLong(comment.split(",")[0]))>0){
                        dataCommentRepository.updateRunningCommentDone(Long.parseLong(comment.split(",")[0]));
                        histories.get(0).setRunning(0);
                        historyCommentRepository.save(histories.get(0));
                        fail_resp.put("status", "fail");
                        fail_resp.put("username", histories.get(0).getUsername());
                        fail_resp.put("fail", "video");
                        fail_resp.put("message", "Không còn video để comment!");
                        return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                    }
                    resp.put("comment_id", comment.split(",")[0]);
                    resp.put("comment", comment.substring(comment.indexOf(",")+1));
                }else{
                    histories.get(0).setTimeget(System.currentTimeMillis());
                    histories.get(0).setRunning(0);
                    historyCommentRepository.save(histories.get(0));
                    fail_resp.put("status", "fail");
                    fail_resp.put("username", histories.get(0).getUsername());
                    fail_resp.put("fail", "video");
                    fail_resp.put("message", "Không còn video để comment!");
                    return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                }
                String[] proxy=accountRepository.getProxyByUsername(username.trim()).split(":");
                if(proxy[0].trim().length()<4){
                    List<Proxy> proxies=null;
                    if (accountRepository.getGeoByUsername(username.trim()).equals("cmt-vn")) {
                        proxies=proxyRepository.getProxyFixAccountByGeo("vn");
                    } else if (accountRepository.getGeoByUsername(username.trim()).equals("cmt-us")) {
                        proxies=proxyRepository.getProxyFixAccountByGeo("us");
                    } else if (accountRepository.getGeoByUsername(username.trim()).equals("cmt-kr")) {
                        proxies=proxyRepository.getProxyFixAccountByGeo("kr");
                    }
                    if(proxies.size()!=0) {
                        proxy=proxies.get(0).getProxy().split(":");
                        Account account=accountRepository.findAccountByUsername(username.trim());
                        account.setProxy(proxies.get(0).getProxy());
                        accountRepository.save(account);
                        proxyRepository.updateProxyGet(vps,System.currentTimeMillis(),proxies.get(0).getId());
                    }
                }else{
                    Random rand=new Random();
                    if(proxyRepository.checkProxyLiveByUsername(username.trim())==0){
                        if(histories.get(0).getGeo().equals("vn")){
                            proxy=proxyVNTrue.getValue().get(rand.nextInt(proxyVNTrue.getValue().size())).split(":");
                        }else if(histories.get(0).getGeo().equals("us")){
                            proxy=proxyUSTrue.getValue().get(rand.nextInt(proxyUSTrue.getValue().size())).split(":");
                        }else if(histories.get(0).getGeo().equals("kr")){
                            proxy=proxyKRTrue.getValue().get(rand.nextInt(proxyKRTrue.getValue().size())).split(":");
                        }
                        if(proxy.length==0){
                            histories.get(0).setRunning(0);
                            histories.get(0).setTimeget(System.currentTimeMillis());
                            historyCommentRepository.save(histories.get(0));
                            fail_resp.put("status", "fail");
                            fail_resp.put("message", "Proxy die!");
                            return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                        }
                    }
                }
                String[] proxysetting=proxySettingRepository.getUserPassByHost(proxy[0]).split(",");
                histories.get(0).setTimeget(System.currentTimeMillis());
                histories.get(0).setVps(vps);
                histories.get(0).setRunning(1);
                historyCommentRepository.save(histories.get(0));
                resp.put("channel_id", videos.get(0).getChannelid());
                resp.put("status", "true");
                resp.put("video_id", videos.get(0).getVideoid());
                resp.put("video_title", videos.get(0).getVideotitle());
                resp.put("username", histories.get(0).getUsername());
                resp.put("geo", accountRepository.getGeoByUsername(username.trim()));
                resp.put("proxy", proxy[0]+":"+proxy[1]+":"+proxysetting[0]+":"+proxysetting[1]);
                if (ran.nextInt(10000) > 5000) {
                    resp.put("source", "dtn");
                } else {
                    resp.put("source", "search");
                }
                if (videos.get(0).getDuration() > 360) {
                    resp.put("video_duration", 180 + ran.nextInt(180));
                } else {
                    resp.put("video_duration", videos.get(0).getDuration());
                }

                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }

        } catch (Exception e) {
            fail_resp.put("status", "fail");
            fail_resp.put("fail", "sum");
            fail_resp.put("message", e.getMessage());
            return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "get26-9", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> get26(@RequestParam(defaultValue = "") String username, @RequestParam(defaultValue = "") String vps) {
        JSONObject resp = new JSONObject();
        JSONObject fail_resp = new JSONObject();
        if (vps.length() == 0) {
            fail_resp.put("status", "fail");
            fail_resp.put("message", "Vps không để trống");
            return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        if(vpsRepository.checkVpsCmtTrue(vps.trim())==0){
            fail_resp.put("status", "fail");
            fail_resp.put("message", "Vps không chạy cmt!");
            return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
        }
        if (username.length() == 0) {
            fail_resp.put("status", "fail");
            fail_resp.put("message", "Username không để trống");
            return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        /*
        if (historyViewRepository.PROCESSLISTVIEW() >= 40) {
            resp.put("status", "fail");
            resp.put("username", "");
            resp.put("fail", "video");
            resp.put("message", "Không còn video để comment!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }
         */
        Random ran = new Random();
        try {
            Thread.sleep(ran.nextInt(1000));
            Long historieId = historyCommentRepository.getId(username);
            List<VideoComment> videos = null;
            if (historieId == null) {
                HistoryComment history = new HistoryComment();
                history.setUsername(username);
                history.setListvideo("");
                history.setRunning(0);
                history.setVps(vps);
                history.setVideoid("");
                history.setOrderid(0L);
                history.setGeo(accountRepository.getGeoByUsername(username.trim()));
                history.setTimeget(System.currentTimeMillis());
                if (history.getGeo().equals("cmt-vn")) {
                    videos = videoCommentRepository.getvideoCommentVNTest("",orderCommentTrue.getValue());
                } else if (history.getGeo().equals("cmt-us")) {
                    videos = videoCommentRepository.getvideoCommentUSTest("",orderCommentTrue.getValue());
                }else if (history.getGeo().equals("cmt-kr")) {
                    videos = videoCommentRepository.getvideoCommentKRTest("",orderCommentTrue.getValue());
                }else{
                    fail_resp.put("status", "fail");
                    fail_resp.put("message", "Username không cmt!");
                    return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                }
                if (videos.size() > 0) {
                    Service service = serviceRepository.getInfoService(videos.get(0).getService());
                    history.setVideoid(videos.get(0).getVideoid());
                    history.setTimeget(System.currentTimeMillis());
                    history.setOrderid(videos.get(0).getOrderid());
                    history.setRunning(1);
                    historyCommentRepository.save(history);

                    dataCommentRepository.updateRunningComment(System.currentTimeMillis(),username.trim(),vps.trim(),videos.get(0).getOrderid());
                    Thread.sleep(ran.nextInt(1000)+500);
                    String comment=dataCommentRepository.getCommentByOrderIdAndUsername(videos.get(0).getOrderid(),username.trim());
                    if(comment!=null){
                        if(historyCommentSumRepository.checkCommentIdTrue(Long.parseLong(comment.split(",")[0]))>0){
                            dataCommentRepository.updateRunningCommentDone(Long.parseLong(comment.split(",")[0]));
                            history.setRunning(0);
                            historyCommentRepository.save(history);
                            fail_resp.put("status", "fail");
                            fail_resp.put("username", history.getUsername());
                            fail_resp.put("fail", "video");
                            fail_resp.put("message", "Không còn video để comment!");
                            return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                        }
                        resp.put("reply","");
                        if(dataReplyCommentRepository.checkReplyByCommentId(Long.parseLong(comment.split(",")[0]))>0){
                            resp.put("device_type","pc");
                        }else{
                            resp.put("device_type","mobile");
                        }
                        resp.put("comment_id", comment.split(",")[0]);
                        resp.put("comment", comment.substring(comment.indexOf(",")+1));
                    }else if(service.getReply()>0){
                        dataReplyCommentRepository.updateRunningComment(System.currentTimeMillis(),username.trim(),vps.trim(),videos.get(0).getOrderid());
                        Thread.sleep(ran.nextInt(1000)+500);
                        Long reply_id=dataReplyCommentRepository.getCommentByOrderIdAndUsername(videos.get(0).getOrderid(),username.trim());
                        if(reply_id!=null){
                            String info_reply=dataReplyCommentRepository.getInfoReplyBYId(reply_id);
                            resp.put("device_type","pc");
                            resp.put("reply",info_reply.split(",")[0]);
                            resp.put("comment_id", reply_id);
                            resp.put("comment", info_reply.substring(info_reply.indexOf(",")+1));
                        }else{
                            history.setRunning(0);
                            historyCommentRepository.save(history);
                            fail_resp.put("status", "fail");
                            fail_resp.put("username", history.getUsername());
                            fail_resp.put("fail", "video");
                            fail_resp.put("message", "Không còn video để comment!");
                            return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                        }
                    }else{
                        history.setRunning(0);
                        historyCommentRepository.save(history);
                        fail_resp.put("status", "fail");
                        fail_resp.put("username", history.getUsername());
                        fail_resp.put("fail", "video");
                        fail_resp.put("message", "Không còn video để comment!");
                        return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                    }
                    String[] proxy=accountRepository.getProxyByUsername(username.trim()).split(":");
                    if(proxy[0].trim().length()<4){
                        List<Proxy> proxies=null;
                        if (accountRepository.getGeoByUsername(username.trim()).equals("cmt-vn")) {
                            proxies=proxyRepository.getProxyFixAccountByGeo("vn");
                        } else if (accountRepository.getGeoByUsername(username.trim()).equals("cmt-us")) {
                            proxies=proxyRepository.getProxyFixAccountByGeo("us");
                        } else if (accountRepository.getGeoByUsername(username.trim()).equals("cmt-kr")) {
                            proxies=proxyRepository.getProxyFixAccountByGeo("kr");
                        }
                        if(proxies.size()!=0) {
                            proxy=proxies.get(0).getProxy().split(":");
                            Account account=accountRepository.findAccountByUsername(username.trim());
                            account.setProxy(proxies.get(0).getProxy());
                            accountRepository.save(account);
                            proxyRepository.updateProxyGet(vps,System.currentTimeMillis(),proxies.get(0).getId());
                        }
                    }else{
                        Random rand=new Random();
                        if(proxyRepository.checkProxyLiveByUsername(username.trim())==0){
                            if(history.getGeo().equals("vn")){
                                proxy=proxyVNTrue.getValue().get(rand.nextInt(proxyVNTrue.getValue().size())).split(":");
                            }else if(history.getGeo().equals("us")){
                                proxy=proxyUSTrue.getValue().get(rand.nextInt(proxyUSTrue.getValue().size())).split(":");
                            }else if(history.getGeo().equals("kr")){
                                proxy=proxyKRTrue.getValue().get(rand.nextInt(proxyKRTrue.getValue().size())).split(":");
                            }
                            if(proxy.length==0){
                                history.setRunning(0);
                                history.setTimeget(System.currentTimeMillis());
                                historyCommentRepository.save(history);
                                fail_resp.put("status", "fail");
                                fail_resp.put("message", "Proxy die!");
                                return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                            }
                        }
                    }
                    String[] proxysetting=proxySettingRepository.getUserPassByHost(proxy[0]).split(",");
                    resp.put("channel_id", videos.get(0).getChannelid());
                    resp.put("status", "true");
                    resp.put("video_id", videos.get(0).getVideoid());
                    resp.put("video_title", videos.get(0).getVideotitle());
                    resp.put("username", history.getUsername());
                    resp.put("geo", accountRepository.getGeoByUsername(username.trim()));
                    resp.put("proxy",proxy[0]+":"+proxy[1]+":"+proxysetting[0]+":"+proxysetting[1]);
                    if(ran.nextInt(1000)<300){
                        resp.put("like", 1);
                    }else{
                        resp.put("like", 0);
                    }
                    if (ran.nextInt(10000) > 5000) {
                        resp.put("source", "dtn");
                    } else {
                        resp.put("source", "search");
                    }
                    if (videos.get(0).getDuration() > 360) {
                        resp.put("video_duration", 180 + ran.nextInt(180));
                    } else {
                        resp.put("video_duration", videos.get(0).getDuration());
                    }
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                } else {
                    history.setRunning(0);
                    historyCommentRepository.save(history);
                    fail_resp.put("status", "fail");
                    fail_resp.put("username", history.getUsername());
                    fail_resp.put("fail", "video");
                    fail_resp.put("message", "Không còn video để comment!");
                    return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                }
            } else {
                List<HistoryComment> histories = historyCommentRepository.getHistoriesById(historieId);
                //System.out.println(System.currentTimeMillis()-histories.get(0).getTimeget());
                if (System.currentTimeMillis() - histories.get(0).getTimeget() < (30000L + (long) ran.nextInt(60000))) {
                    //histories.get(0).setTimeget(System.currentTimeMillis());
                    //historyViewRepository.save(histories.get(0));
                    fail_resp.put("status", "fail");
                    fail_resp.put("username", histories.get(0).getUsername());
                    fail_resp.put("fail", "video");
                    fail_resp.put("message", "Không còn video để comment!");
                    return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                }
                if (histories.get(0).getGeo().equals("cmt-vn")) {
                    //videos=videoViewRepository.getvideoViewNoCheckMaxThreadVN(histories.get(0).getListvideo());
                    videos = videoCommentRepository.getvideoCommentVNTest(histories.get(0).getListvideo(),orderCommentTrue.getValue());
                } else if (histories.get(0).getGeo().equals("cmt-us")) {
                    //videos=videoViewRepository.getvideoViewNoCheckMaxThreadUS(histories.get(0).getListvideo());
                    videos = videoCommentRepository.getvideoCommentUSTest(histories.get(0).getListvideo(),orderCommentTrue.getValue());
                }else if (histories.get(0).getGeo().equals("cmt-kr")) {
                    //videos=videoViewRepository.getvideoViewNoCheckMaxThreadUS(histories.get(0).getListvideo());
                    videos = videoCommentRepository.getvideoCommentKRTest(histories.get(0).getListvideo(),orderCommentTrue.getValue());
                }else{
                    fail_resp.put("status", "fail");
                    fail_resp.put("message", "Username không cmt!");
                    return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                }
                if (videos.size() > 0) {
                    histories.get(0).setTimeget(System.currentTimeMillis());
                    histories.get(0).setVideoid(videos.get(0).getVideoid());
                    histories.get(0).setOrderid(videos.get(0).getOrderid());
                    histories.get(0).setRunning(1);
                    historyCommentRepository.save(histories.get(0));
                } else {
                    histories.get(0).setTimeget(System.currentTimeMillis());
                    histories.get(0).setRunning(0);
                    historyCommentRepository.save(histories.get(0));
                    fail_resp.put("status", "fail");
                    fail_resp.put("username", histories.get(0).getUsername());
                    fail_resp.put("fail", "video");
                    fail_resp.put("message", "Không còn video để comment!");
                    return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                }
                Service service = serviceRepository.getInfoService(videos.get(0).getService());
                dataCommentRepository.updateRunningComment(System.currentTimeMillis(),username.trim(),vps.trim(),videos.get(0).getOrderid());
                Thread.sleep(ran.nextInt(1000)+500);
                String comment=dataCommentRepository.getCommentByOrderIdAndUsername(videos.get(0).getOrderid(),username.trim());
                if(comment!=null){
                    if(historyCommentSumRepository.checkCommentIdTrue(Long.parseLong(comment.split(",")[0]))>0){
                        dataCommentRepository.updateRunningCommentDone(Long.parseLong(comment.split(",")[0]));
                        histories.get(0).setRunning(0);
                        historyCommentRepository.save(histories.get(0));
                        fail_resp.put("status", "fail");
                        fail_resp.put("username", histories.get(0).getUsername());
                        fail_resp.put("fail", "video");
                        fail_resp.put("message", "Không còn video để comment!");
                        return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                    }
                    resp.put("reply","");
                    if(dataReplyCommentRepository.checkReplyByCommentId(Long.parseLong(comment.split(",")[0]))>0){
                        resp.put("device_type","pc");
                    }else{
                        resp.put("device_type","mobile");
                    }
                    resp.put("comment_id", comment.split(",")[0]);
                    resp.put("comment", comment.substring(comment.indexOf(",")+1));
                }else if(service.getReply()>0) {
                    dataReplyCommentRepository.updateRunningComment(System.currentTimeMillis(),username.trim(),vps.trim(),videos.get(0).getOrderid());
                    Thread.sleep(ran.nextInt(1000)+500);
                    Long reply_id=dataReplyCommentRepository.getCommentByOrderIdAndUsername(videos.get(0).getOrderid(),username.trim());
                    if(reply_id!=null){
                        String info_reply=dataReplyCommentRepository.getInfoReplyBYId(reply_id);
                        resp.put("device_type","pc");
                        resp.put("reply",info_reply.split(",")[0]);
                        resp.put("comment_id", reply_id);
                        resp.put("comment", info_reply.substring(info_reply.indexOf(",")+1));
                    }else{
                        histories.get(0).setRunning(0);
                        historyCommentRepository.save(histories.get(0));
                        fail_resp.put("status", "fail");
                        fail_resp.put("username", histories.get(0).getUsername());
                        fail_resp.put("fail", "video");
                        fail_resp.put("message", "Không còn video để comment!");
                        return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                    }
                }else{
                    histories.get(0).setRunning(0);
                    historyCommentRepository.save(histories.get(0));
                    fail_resp.put("status", "fail");
                    fail_resp.put("username", histories.get(0).getUsername());
                    fail_resp.put("fail", "video");
                    fail_resp.put("message", "Không còn video để comment!");
                    return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                }
                String[] proxy=accountRepository.getProxyByUsername(username.trim()).split(":");
                if(proxy[0].trim().length()<4){
                    List<Proxy> proxies=null;
                    if (accountRepository.getGeoByUsername(username.trim()).equals("cmt-vn")) {
                        proxies=proxyRepository.getProxyFixAccountByGeo("vn");
                    } else if (accountRepository.getGeoByUsername(username.trim()).equals("cmt-us")) {
                        proxies=proxyRepository.getProxyFixAccountByGeo("us");
                    } else if (accountRepository.getGeoByUsername(username.trim()).equals("cmt-kr")) {
                        proxies=proxyRepository.getProxyFixAccountByGeo("kr");
                    }
                    if(proxies.size()!=0) {
                        proxy=proxies.get(0).getProxy().split(":");
                        Account account=accountRepository.findAccountByUsername(username.trim());
                        account.setProxy(proxies.get(0).getProxy());
                        accountRepository.save(account);
                        proxyRepository.updateProxyGet(vps,System.currentTimeMillis(),proxies.get(0).getId());
                    }
                }else{
                    Random rand=new Random();
                    if(proxyRepository.checkProxyLiveByUsername(username.trim())==0){
                        if(histories.get(0).getGeo().equals("vn")){
                            proxy=proxyVNTrue.getValue().get(rand.nextInt(proxyVNTrue.getValue().size())).split(":");
                        }else if(histories.get(0).getGeo().equals("us")){
                            proxy=proxyUSTrue.getValue().get(rand.nextInt(proxyUSTrue.getValue().size())).split(":");
                        }else if(histories.get(0).getGeo().equals("kr")){
                            proxy=proxyKRTrue.getValue().get(rand.nextInt(proxyKRTrue.getValue().size())).split(":");
                        }
                        if(proxy.length==0){
                            histories.get(0).setRunning(0);
                            histories.get(0).setTimeget(System.currentTimeMillis());
                            historyCommentRepository.save(histories.get(0));
                            fail_resp.put("status", "fail");
                            fail_resp.put("message", "Proxy die!");
                            return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                        }
                    }
                }
                String[] proxysetting=proxySettingRepository.getUserPassByHost(proxy[0]).split(",");
                histories.get(0).setTimeget(System.currentTimeMillis());
                histories.get(0).setVps(vps);
                histories.get(0).setRunning(1);
                historyCommentRepository.save(histories.get(0));
                resp.put("channel_id", videos.get(0).getChannelid());
                resp.put("status", "true");
                resp.put("video_id", videos.get(0).getVideoid());
                resp.put("video_title", videos.get(0).getVideotitle());
                resp.put("username", histories.get(0).getUsername());
                resp.put("geo", accountRepository.getGeoByUsername(username.trim()));
                resp.put("proxy", proxy[0]+":"+proxy[1]+":"+proxysetting[0]+":"+proxysetting[1]);
                if(ran.nextInt(1000)<300){
                    resp.put("like", 1);
                }else{
                    resp.put("like", 0);
                }
                if (ran.nextInt(10000) > 5000) {
                    resp.put("source", "dtn");
                } else {
                    resp.put("source", "search");
                }
                if (videos.get(0).getDuration() > 360) {
                    resp.put("video_duration", 180 + ran.nextInt(180));
                } else {
                    resp.put("video_duration", videos.get(0).getDuration());
                }
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }

        } catch (Exception e) {
            fail_resp.put("status", "fail");
            fail_resp.put("fail", "sum");
            fail_resp.put("message", e.getMessage());
            return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "get", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> get(@RequestParam(defaultValue = "") String username, @RequestParam(defaultValue = "") String vps) {
        JSONObject resp = new JSONObject();
        JSONObject fail_resp = new JSONObject();
        if (vps.length() == 0) {
            fail_resp.put("status", "fail");
            fail_resp.put("message", "Vps không để trống");
            return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        if(vpsRepository.checkVpsCmtTrue(vps.trim())==0){
            fail_resp.put("status", "fail");
            fail_resp.put("message", "Vps không chạy cmt!");
            return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
        }
        if (username.length() == 0) {
            fail_resp.put("status", "fail");
            fail_resp.put("message", "Username không để trống");
            return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        /*
        if (historyViewRepository.PROCESSLISTVIEW() >= 40) {
            resp.put("status", "fail");
            resp.put("username", "");
            resp.put("fail", "video");
            resp.put("message", "Không còn video để comment!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }
         */
        Random ran = new Random();
        try {
            Thread.sleep(ran.nextInt(1000));
            Long historieId = historyCommentRepository.getId(username);
            List<VideoComment> videos = null;
            if (historieId == null) {
                resp.put("status", "fail");
                resp.put("message", "Username không tồn tại!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
            } else {
                List<HistoryComment> histories = historyCommentRepository.getHistoriesById(historieId);

                if(histories.get(0).getTask_index()>=histories.get(0).getMax_task()){
                    resp.put("status", "fail");
                    resp.put("message", "off_profile");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
                if (System.currentTimeMillis() - histories.get(0).getTimeget() < (30000L + (long) ran.nextInt(60000))) {
                    //histories.get(0).setTimeget(System.currentTimeMillis());
                    //historyViewRepository.save(histories.get(0));
                    fail_resp.put("status", "fail");
                    fail_resp.put("username", histories.get(0).getUsername());
                    fail_resp.put("fail", "video");
                    fail_resp.put("message", "Không còn video để comment!");
                    return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                }

                histories.get(0).setTask_index(histories.get(0).getTask_index()+1);

                if (histories.get(0).getGeo().equals("cmt-vn")) {
                    //videos=videoViewRepository.getvideoViewNoCheckMaxThreadVN(histories.get(0).getListvideo());
                    videos = videoCommentRepository.getvideoCommentVNTest(histories.get(0).getListvideo(),orderCommentTrue.getValue());
                } else if (histories.get(0).getGeo().equals("cmt-us")) {
                    //videos=videoViewRepository.getvideoViewNoCheckMaxThreadUS(histories.get(0).getListvideo());
                    videos = videoCommentRepository.getvideoCommentUSTest(histories.get(0).getListvideo(),orderCommentTrue.getValue());
                }else if (histories.get(0).getGeo().equals("cmt-kr")) {
                    //videos=videoViewRepository.getvideoViewNoCheckMaxThreadUS(histories.get(0).getListvideo());
                    videos = videoCommentRepository.getvideoCommentKRTest(histories.get(0).getListvideo(),orderCommentTrue.getValue());
                }else if (histories.get(0).getGeo().equals("cmt-jp")) {
                    //videos=videoViewRepository.getvideoViewNoCheckMaxThreadUS(histories.get(0).getListvideo());
                    videos = videoCommentRepository.getvideoCommentJPTest(histories.get(0).getListvideo(),orderCommentTrue.getValue());
                }else if (histories.get(0).getGeo().contains("live")) {
                    //videos=videoViewRepository.getvideoViewNoCheckMaxThreadUS(histories.get(0).getListvideo());
                    videos = videoCommentRepository.getvideoChatLiveByGeo(histories.get(0).getGeo(),histories.get(0).getListvideo(),orderCommentTrue.getValue());
                }else if (histories.get(0).getGeo().equals("cmt-test1")) {
                    //videos=videoViewRepository.getvideoViewNoCheckMaxThreadUS(histories.get(0).getListvideo());
                    videos = videoCommentRepository.getvideoCommentTESTTest(histories.get(0).getListvideo(),orderCommentTrue.getValue());
                }else{
                    fail_resp.put("status", "fail");
                    fail_resp.put("message", "Username không cmt!");
                    return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                }
                if (videos.size() > 0) {
                    Thread.sleep(150+ran.nextInt(200));
                    if(!orderCommentTrue.getValue().contains(videos.get(0).getOrderid().toString())){
                        histories.get(0).setTimeget(System.currentTimeMillis());
                        histories.get(0).setRunning(0);
                        historyCommentRepository.save(histories.get(0));
                        fail_resp.put("status", "fail");
                        fail_resp.put("username", histories.get(0).getUsername());
                        fail_resp.put("fail", "video");
                        fail_resp.put("message", "Không còn video để comment!");
                        return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                    }
                    histories.get(0).setTimeget(System.currentTimeMillis());
                    histories.get(0).setVideoid(videos.get(0).getVideoid());
                    histories.get(0).setOrderid(videos.get(0).getOrderid());
                    histories.get(0).setRunning(1);
                    historyCommentRepository.save(histories.get(0));
                } else {
                    histories.get(0).setTimeget(System.currentTimeMillis());
                    histories.get(0).setRunning(0);
                    historyCommentRepository.save(histories.get(0));
                    fail_resp.put("status", "fail");
                    fail_resp.put("username", histories.get(0).getUsername());
                    fail_resp.put("fail", "video");
                    fail_resp.put("message", "Không còn video để comment!");
                    return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                }
                Service service = serviceRepository.getInfoService(videos.get(0).getService());
                dataCommentRepository.updateRunningComment(System.currentTimeMillis(),username.trim(),vps.trim(),videos.get(0).getOrderid());
                Thread.sleep(ran.nextInt(1000)+500);
                String comment=dataCommentRepository.getCommentByOrderIdAndUsername(videos.get(0).getOrderid(),username.trim());
                if(comment!=null){
                    if(historyCommentSumRepository.checkCommentIdTrue(Long.parseLong(comment.split(",")[0]))>0){
                        dataCommentRepository.updateRunningCommentDone(Long.parseLong(comment.split(",")[0]));
                        histories.get(0).setRunning(0);
                        historyCommentRepository.save(histories.get(0));
                        fail_resp.put("status", "fail");
                        fail_resp.put("username", histories.get(0).getUsername());
                        fail_resp.put("fail", "video");
                        fail_resp.put("message", "Không còn video để comment!");
                        return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                    }
                    resp.put("reply","");
                    if(dataReplyCommentRepository.checkReplyByCommentId(Long.parseLong(comment.split(",")[0]))>0){
                        resp.put("device_type","pc");
                    }else{
                        resp.put("device_type",service.getDevice_type().trim());
                    }
                    resp.put("comment_id", comment.split(",")[0]);
                    resp.put("comment", comment.substring(comment.indexOf(",")+1));
                }else if(service.getReply()>0) {
                    dataReplyCommentRepository.updateRunningComment(System.currentTimeMillis(),username.trim(),vps.trim(),videos.get(0).getOrderid());
                    Thread.sleep(ran.nextInt(1000)+500);
                    Long reply_id=dataReplyCommentRepository.getCommentByOrderIdAndUsername(videos.get(0).getOrderid(),username.trim());
                    if(reply_id!=null){
                        String info_reply=dataReplyCommentRepository.getInfoReplyBYId(reply_id);
                        resp.put("device_type","pc");
                        resp.put("reply",info_reply.split(",")[0]);
                        resp.put("comment_id", reply_id);
                        resp.put("comment", info_reply.substring(info_reply.indexOf(",")+1));
                    }else{
                        histories.get(0).setRunning(0);
                        historyCommentRepository.save(histories.get(0));
                        fail_resp.put("status", "fail");
                        fail_resp.put("username", histories.get(0).getUsername());
                        fail_resp.put("fail", "video");
                        fail_resp.put("message", "Không còn video để comment!");
                        return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                    }
                }else{
                    histories.get(0).setRunning(0);
                    historyCommentRepository.save(histories.get(0));
                    fail_resp.put("status", "fail");
                    fail_resp.put("username", histories.get(0).getUsername());
                    fail_resp.put("fail", "video");
                    fail_resp.put("message", "Không còn video để comment!");
                    return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                }
                String[] proxy =new String[0];
                if(histories.get(0).getGeo().contains("vn")){
                    if(service.getGeo().equals("go")){
                        if(proxyUSTrue.getValue().size()!=0){
                            proxy=proxyUSTrue.getValue().get(ran.nextInt(proxyUSTrue.getValue().size())).split(":");
                        }else{
                            proxy= new String[]{};
                        }
                    }else{
                        if(proxyVNTrue.getValue().size()!=0){
                            proxy=proxyVNTrue.getValue().get(ran.nextInt(proxyVNTrue.getValue().size())).split(":");
                        }else{
                            proxy= new String[]{};
                        }
                    }
                }else if(histories.get(0).getGeo().contains("us")){
                    if(proxyUSTrue.getValue().size()!=0){
                        proxy=proxyUSTrue.getValue().get(ran.nextInt(proxyUSTrue.getValue().size())).split(":");
                    }else{
                        proxy= new String[]{};
                    }
                }else if(histories.get(0).getGeo().contains("kr")){
                    if(proxyUSTrue.getValue().size()!=0){
                        proxy=proxyUSTrue.getValue().get(ran.nextInt(proxyUSTrue.getValue().size())).split(":");
                    }else{
                        proxy= new String[]{};
                    }
                }else if(histories.get(0).getGeo().contains("jp")){
                    if(proxyUSTrue.getValue().size()!=0){
                        proxy=proxyUSTrue.getValue().get(ran.nextInt(proxyUSTrue.getValue().size())).split(":");
                    }else{
                        proxy= new String[]{};
                    }
                }else if(histories.get(0).getGeo().contains("test")){
                    if(proxyVNTrue.getValue().size()!=0){
                        proxy=proxyVNTrue.getValue().get(ran.nextInt(proxyVNTrue.getValue().size())).split(":");
                    }else{
                        proxy= new String[]{};
                    }
                }
                if(proxy.length==0){
                    histories.get(0).setRunning(0);
                    histories.get(0).setTimeget(System.currentTimeMillis());
                    historyCommentRepository.save(histories.get(0));
                    fail_resp.put("status", "fail");
                    fail_resp.put("message", "Hết proxy khả dụng");
                    return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                }
                String[] proxysetting=proxySettingRepository.getUserPassByHost(proxy[0]).split(",");
                histories.get(0).setTimeget(System.currentTimeMillis());
                histories.get(0).setVps(vps);
                histories.get(0).setRunning(1);
                historyCommentRepository.save(histories.get(0));
                resp.put("channel_id", videos.get(0).getChannelid());
                resp.put("status", "true");
                resp.put("video_id", videos.get(0).getVideoid());
                resp.put("live", service.getLive() == 1 ? "true" : "fail");
                resp.put("video_title", videos.get(0).getVideotitle());
                resp.put("username", histories.get(0).getUsername());
                resp.put("geo", accountRepository.getGeoByUsername(username.trim()));

                String proxy_ha= proxyRepository.get_Proxy_HA(String.join(":", proxy));
                if(proxy_ha!=null){
                    proxy=proxy_ha.trim().split(":");
                }
                resp.put("proxy", proxy[0]+":"+proxy[1]+":"+proxysetting[0]+":"+proxysetting[1]);
                if(ran.nextInt(1000)<300){
                    resp.put("like", 1);
                }else{
                    resp.put("like", 0);
                }
                if (ran.nextInt(10000) > 5000) {
                    resp.put("source", "dtn");
                } else {
                    resp.put("source", "search");
                }
                if (videos.get(0).getDuration() > 360) {
                    resp.put("video_duration", 180 + ran.nextInt(180));
                } else {
                    resp.put("video_duration", videos.get(0).getDuration());
                }
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }

        } catch (Exception e) {
            fail_resp.put("status", "fail");
            fail_resp.put("fail", "sum");
            fail_resp.put("message", e.getMessage());
            return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "getCmt", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> getCmt(@RequestParam(defaultValue = "") String vps) {
        JSONObject resp = new JSONObject();
        JSONObject fail_resp = new JSONObject();
        if (vps.length() == 0) {
            fail_resp.put("status", "fail");
            fail_resp.put("message", "Vps không để trống");
            return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        Vps vps_check=vpsRepository.getVpsByName(vps.trim());
        if(vps_check==null){
            resp.put("status", "fail");
            resp.put("message", "Vps không tồn tại");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }
        if(vps_check.getCmt()==0){
            resp.put("status", "fail");
            resp.put("message", "Vps không chạy cmt!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }


        Random ran = new Random();
        try {
            Thread.sleep(ran.nextInt(1000));
            Long historieId=null;
            if(vps_check.getVpsoption().contains("vn")){
                if(ran.nextInt(100)<10){
                    historieId = historyCommentRepository.getAccToCmtNoCheckProxy_By_Geo(vps.trim(),"cmt-vn");
                }else{
                    historieId = historyCommentRepository.getAccToCmtNoCheckProxy_By_Geo(vps.trim(),"cmt-us");
                }
            }else{
                historieId = historyCommentRepository.getAccToCmtNoCheckProxy(vps.trim());
            }
            if (historieId == null) {
                vps_check.setTask_time(System.currentTimeMillis());
                vpsRepository.save(vps_check);
                resp.put("status", "fail");
                resp.put("fail", "user");
                resp.put("message", "Không còn user để cmt!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }

            List<VideoComment> videos = null;
            List<HistoryComment> histories = historyCommentRepository.getHistoriesById(historieId);

            if (System.currentTimeMillis() - histories.get(0).getTimeget() < (30000L + (long) ran.nextInt(60000))) {
                //histories.get(0).setTimeget(System.currentTimeMillis());
                //historyViewRepository.save(histories.get(0));
                fail_resp.put("status", "fail");
                fail_resp.put("username", histories.get(0).getUsername());
                fail_resp.put("fail", "video");
                fail_resp.put("message", "Không còn video để comment!");
                return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
            }

            histories.get(0).setMax_task(3+ran.nextInt(4));
            histories.get(0).setTask_index(0);

            if (histories.get(0).getGeo().equals("cmt-vn")) {
                //videos=videoViewRepository.getvideoViewNoCheckMaxThreadVN(histories.get(0).getListvideo());
                videos = videoCommentRepository.getvideoCommentVNTest(histories.get(0).getListvideo(),orderCommentTrue.getValue());
            } else if (histories.get(0).getGeo().equals("cmt-us")) {
                //videos=videoViewRepository.getvideoViewNoCheckMaxThreadUS(histories.get(0).getListvideo());
                videos = videoCommentRepository.getvideoCommentUSTest(histories.get(0).getListvideo(),orderCommentTrue.getValue());
            }else if (histories.get(0).getGeo().equals("cmt-kr")) {
                //videos=videoViewRepository.getvideoViewNoCheckMaxThreadUS(histories.get(0).getListvideo());
                videos = videoCommentRepository.getvideoCommentKRTest(histories.get(0).getListvideo(),orderCommentTrue.getValue());
            }else if (histories.get(0).getGeo().equals("cmt-jp")) {
                //videos=videoViewRepository.getvideoViewNoCheckMaxThreadUS(histories.get(0).getListvideo());
                videos = videoCommentRepository.getvideoCommentJPTest(histories.get(0).getListvideo(),orderCommentTrue.getValue());
            }else if (histories.get(0).getGeo().contains("live")) {
                //videos=videoViewRepository.getvideoViewNoCheckMaxThreadUS(histories.get(0).getListvideo());
                videos = videoCommentRepository.getvideoChatLiveByGeo(histories.get(0).getGeo(),histories.get(0).getListvideo(),orderCommentTrue.getValue());
            }else if (histories.get(0).getGeo().equals("cmt-test1")) {
                //videos=videoViewRepository.getvideoViewNoCheckMaxThreadUS(histories.get(0).getListvideo());
                videos = videoCommentRepository.getvideoCommentTESTTest(histories.get(0).getListvideo(),orderCommentTrue.getValue());
            }else{
                fail_resp.put("status", "fail");
                fail_resp.put("message", "Username không cmt!");
                return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
            }
            if (videos.size() > 0) {
                Thread.sleep(150+ran.nextInt(200));
                if(!orderCommentTrue.getValue().contains(videos.get(0).getOrderid().toString())){
                    histories.get(0).setTimeget(System.currentTimeMillis());
                    histories.get(0).setRunning(0);
                    historyCommentRepository.save(histories.get(0));
                    fail_resp.put("status", "fail");
                    fail_resp.put("username", histories.get(0).getUsername());
                    fail_resp.put("fail", "video");
                    fail_resp.put("message", "Không còn video để comment!");
                    return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                }
                histories.get(0).setTimeget(System.currentTimeMillis());
                histories.get(0).setVideoid(videos.get(0).getVideoid());
                histories.get(0).setOrderid(videos.get(0).getOrderid());
                histories.get(0).setRunning(1);
                historyCommentRepository.save(histories.get(0));
            } else {
                histories.get(0).setTimeget(System.currentTimeMillis());
                histories.get(0).setRunning(0);
                historyCommentRepository.save(histories.get(0));
                fail_resp.put("status", "fail");
                fail_resp.put("username", histories.get(0).getUsername());
                fail_resp.put("fail", "video");
                fail_resp.put("message", "Không còn video để comment!");
                return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
            }
            Service service = serviceRepository.getInfoService(videos.get(0).getService());
            dataCommentRepository.updateRunningComment(System.currentTimeMillis(),histories.get(0).getUsername().trim(),vps.trim(),videos.get(0).getOrderid());
            Thread.sleep(ran.nextInt(1000)+500);
            String comment=dataCommentRepository.getCommentByOrderIdAndUsername(videos.get(0).getOrderid(),histories.get(0).getUsername().trim());
            if(comment!=null){
                if(historyCommentSumRepository.checkCommentIdTrue(Long.parseLong(comment.split(",")[0]))>0){
                    dataCommentRepository.updateRunningCommentDone(Long.parseLong(comment.split(",")[0]));
                    histories.get(0).setRunning(0);
                    historyCommentRepository.save(histories.get(0));
                    fail_resp.put("status", "fail");
                    fail_resp.put("username", histories.get(0).getUsername());
                    fail_resp.put("fail", "video");
                    fail_resp.put("message", "Không còn video để comment!");
                    return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                }
                resp.put("reply","");
                if(dataReplyCommentRepository.checkReplyByCommentId(Long.parseLong(comment.split(",")[0]))>0){
                    resp.put("device_type","pc");
                }else{
                    resp.put("device_type",service.getDevice_type().trim());
                }
                resp.put("comment_id", comment.split(",")[0]);
                resp.put("comment", comment.substring(comment.indexOf(",")+1));
            }else if(service.getReply()>0) {
                dataReplyCommentRepository.updateRunningComment(System.currentTimeMillis(),histories.get(0).getUsername().trim(),vps.trim(),videos.get(0).getOrderid());
                Thread.sleep(ran.nextInt(1000)+500);
                Long reply_id=dataReplyCommentRepository.getCommentByOrderIdAndUsername(videos.get(0).getOrderid(),histories.get(0).getUsername().trim());
                if(reply_id!=null){
                    String info_reply=dataReplyCommentRepository.getInfoReplyBYId(reply_id);
                    resp.put("device_type","pc");
                    resp.put("reply",info_reply.split(",")[0]);
                    resp.put("comment_id", reply_id);
                    resp.put("comment", info_reply.substring(info_reply.indexOf(",")+1));
                }else{
                    histories.get(0).setRunning(0);
                    historyCommentRepository.save(histories.get(0));
                    fail_resp.put("status", "fail");
                    fail_resp.put("username", histories.get(0).getUsername());
                    fail_resp.put("fail", "video");
                    fail_resp.put("message", "Không còn video để comment!");
                    return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                }
            }else{
                histories.get(0).setRunning(0);
                historyCommentRepository.save(histories.get(0));
                fail_resp.put("status", "fail");
                fail_resp.put("username", histories.get(0).getUsername());
                fail_resp.put("fail", "video");
                fail_resp.put("message", "Không còn video để comment!");
                return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
            }
            String[] proxy =new String[0];
            if(histories.get(0).getGeo().contains("vn")){
                if(service.getGeo().equals("go")){
                    if(proxyVultrTrue.getValue().size()!=0){
                        proxy=proxyVultrTrue.getValue().get(ran.nextInt(proxyVultrTrue.getValue().size())).split(":");
                    }else{
                        proxy= new String[]{};
                    }
                }else{
                    if(proxyVNTrue.getValue().size()!=0){
                        proxy=proxyVNTrue.getValue().get(ran.nextInt(proxyVNTrue.getValue().size())).split(":");
                    }else{
                        proxy= new String[]{};
                    }
                }
            }else if(histories.get(0).getGeo().contains("us")){
                if(proxyUSTrue.getValue().size()!=0){
                    proxy=proxyUSTrue.getValue().get(ran.nextInt(proxyUSTrue.getValue().size())).split(":");
                }else{
                    proxy= new String[]{};
                }
            }else if(histories.get(0).getGeo().contains("kr")){
                if(proxyUSTrue.getValue().size()!=0){
                    proxy=proxyUSTrue.getValue().get(ran.nextInt(proxyUSTrue.getValue().size())).split(":");
                }else{
                    proxy= new String[]{};
                }
            }else if(histories.get(0).getGeo().contains("jp")){
                if(proxyUSTrue.getValue().size()!=0){
                    proxy=proxyUSTrue.getValue().get(ran.nextInt(proxyUSTrue.getValue().size())).split(":");
                }else{
                    proxy= new String[]{};
                }
            }else if(histories.get(0).getGeo().contains("test")){
                if(proxyVNTrue.getValue().size()!=0){
                    proxy=proxyVNTrue.getValue().get(ran.nextInt(proxyVNTrue.getValue().size())).split(":");
                }else{
                    proxy= new String[]{};
                }
            }
            if(proxy.length==0){
                histories.get(0).setRunning(0);
                histories.get(0).setTimeget(System.currentTimeMillis());
                historyCommentRepository.save(histories.get(0));
                fail_resp.put("status", "fail");
                fail_resp.put("message", "Hết proxy khả dụng");
                return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
            }
            String[] proxysetting=proxySettingRepository.getUserPassByHost(proxy[0]).split(",");
            histories.get(0).setTimeget(System.currentTimeMillis());
            histories.get(0).setVps(vps);
            histories.get(0).setRunning(1);
            histories.get(0).setTask_index(1);
            historyCommentRepository.save(histories.get(0));
            resp.put("channel_id", videos.get(0).getChannelid());
            resp.put("status", "true");
            resp.put("video_id", videos.get(0).getVideoid());
            resp.put("live", service.getLive() == 1 ? "true" : "fail");
            resp.put("video_title", videos.get(0).getVideotitle());
            resp.put("username", histories.get(0).getUsername());
            resp.put("geo", histories.get(0).getGeo().trim());

            String proxy_ha= proxyRepository.get_Proxy_HA(proxy[0].trim());
            if(proxy_ha!=null){
                proxy=proxy_ha.trim().split(":");
            }

            resp.put("proxy", proxy[0]+":"+proxy[1]+":"+proxysetting[0]+":"+proxysetting[1]);
            if(ran.nextInt(1000)<300){
                resp.put("like", 1);
            }else{
                resp.put("like", 0);
            }
            if (ran.nextInt(10000) > 5000) {
                resp.put("source", "dtn");
            } else {
                resp.put("source", "search");
            }
            if (videos.get(0).getDuration() > 360) {
                resp.put("video_duration", 180 + ran.nextInt(180));
            } else {
                resp.put("video_duration", videos.get(0).getDuration());
            }
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            fail_resp.put("status", "fail");
            fail_resp.put("fail", "sum");
            fail_resp.put("message", e.getMessage());
            return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "gettest", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> gettest(@RequestParam(defaultValue = "") String username, @RequestParam(defaultValue = "") String vps) {
        JSONObject resp = new JSONObject();
        JSONObject fail_resp = new JSONObject();
        if (vps.length() == 0) {
            fail_resp.put("status", "fail");
            fail_resp.put("message", "Vps không để trống");
            return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        if(vpsRepository.checkVpsCmtTrue(vps.trim())==0){
            fail_resp.put("status", "fail");
            fail_resp.put("message", "Vps không chạy cmt!");
            return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
        }
        if (username.length() == 0) {
            fail_resp.put("status", "fail");
            fail_resp.put("message", "Username không để trống");
            return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        /*
        if (historyViewRepository.PROCESSLISTVIEW() >= 40) {
            resp.put("status", "fail");
            resp.put("username", "");
            resp.put("fail", "video");
            resp.put("message", "Không còn video để comment!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }
         */
        Random ran = new Random();
        try {
            Thread.sleep(ran.nextInt(1000));
            Long historieId = historyCommentRepository.getId(username);
            List<VideoComment> videos = null;
            if (historieId == null) {
                HistoryComment history = new HistoryComment();
                history.setUsername(username);
                history.setListvideo("");
                history.setRunning(0);
                history.setVps(vps);
                history.setVideoid("");
                history.setOrderid(0L);
                history.setGeo(accountRepository.getGeoByUsername(username.trim()));
                history.setTimeget(System.currentTimeMillis());
                if (history.getGeo().equals("cmt-vn")) {
                    videos = videoCommentRepository.getvideoCommentVNTest("",orderCommentTrue.getValue());
                } else if (history.getGeo().equals("cmt-us")) {
                    videos = videoCommentRepository.getvideoCommentUSTest("",orderCommentTrue.getValue());
                }else if (history.getGeo().equals("cmt-kr")) {
                    videos = videoCommentRepository.getvideoCommentKRTest("",orderCommentTrue.getValue());
                }else{
                    fail_resp.put("status", "fail");
                    fail_resp.put("message", "Username không cmt!");
                    return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                }
                if (videos.size() > 0) {
                    Service service = serviceRepository.getInfoService(videos.get(0).getService());
                    history.setVideoid(videos.get(0).getVideoid());
                    history.setTimeget(System.currentTimeMillis());
                    history.setOrderid(videos.get(0).getOrderid());
                    history.setRunning(1);
                    historyCommentRepository.save(history);

                    dataCommentRepository.updateRunningComment(System.currentTimeMillis(),username.trim(),vps.trim(),videos.get(0).getOrderid());
                    Thread.sleep(ran.nextInt(1000)+500);
                    String comment=dataCommentRepository.getCommentByOrderIdAndUsername(videos.get(0).getOrderid(),username.trim());
                    if(comment!=null){
                        if(historyCommentSumRepository.checkCommentIdTrue(Long.parseLong(comment.split(",")[0]))>0){
                            dataCommentRepository.updateRunningCommentDone(Long.parseLong(comment.split(",")[0]));
                            history.setRunning(0);
                            historyCommentRepository.save(history);
                            fail_resp.put("status", "fail");
                            fail_resp.put("username", history.getUsername());
                            fail_resp.put("fail", "video");
                            fail_resp.put("message", "Không còn video để comment!");
                            return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                        }
                        resp.put("reply","");
                        if(dataReplyCommentRepository.checkReplyByCommentId(Long.parseLong(comment.split(",")[0]))>0){
                            resp.put("device_type","pc");
                        }else{
                            resp.put("device_type","mobile");
                        }
                        resp.put("comment_id", comment.split(",")[0]);
                        resp.put("comment", comment.substring(comment.indexOf(",")+1));
                    }else if(service.getReply()>0){
                        dataReplyCommentRepository.updateRunningComment(System.currentTimeMillis(),username.trim(),vps.trim(),videos.get(0).getOrderid());
                        Thread.sleep(ran.nextInt(1000)+500);
                        Long reply_id=dataReplyCommentRepository.getCommentByOrderIdAndUsername(videos.get(0).getOrderid(),username.trim());
                        if(reply_id!=null){
                            String info_reply=dataReplyCommentRepository.getInfoReplyBYId(reply_id);
                            resp.put("device_type","pc");
                            resp.put("reply",info_reply.split(",")[0]);
                            resp.put("comment_id", reply_id);
                            resp.put("comment", info_reply.substring(info_reply.indexOf(",")+1));
                        }else{
                            history.setRunning(0);
                            historyCommentRepository.save(history);
                            fail_resp.put("status", "fail");
                            fail_resp.put("username", history.getUsername());
                            fail_resp.put("fail", "video");
                            fail_resp.put("message", "Không còn video để comment!");
                            return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                        }
                    }else{
                        history.setRunning(0);
                        historyCommentRepository.save(history);
                        fail_resp.put("status", "fail");
                        fail_resp.put("username", history.getUsername());
                        fail_resp.put("fail", "video");
                        fail_resp.put("message", "Không còn video để comment!");
                        return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                    }
                    String[] proxy=accountRepository.getProxyByUsername(username.trim()).split(":");
                    if(proxy[0].trim().length()<4){
                        List<Proxy> proxies=null;
                        if (accountRepository.getGeoByUsername(username.trim()).equals("cmt-vn")) {
                            proxies=proxyRepository.getProxyFixAccountByGeo("vn");
                        } else if (accountRepository.getGeoByUsername(username.trim()).equals("cmt-us")) {
                            proxies=proxyRepository.getProxyFixAccountByGeo("us");
                        } else if (accountRepository.getGeoByUsername(username.trim()).equals("cmt-kr")) {
                            proxies=proxyRepository.getProxyFixAccountByGeo("kr");
                        }
                        if(proxies.size()!=0) {
                            proxy=proxies.get(0).getProxy().split(":");
                            Account account=accountRepository.findAccountByUsername(username.trim());
                            account.setProxy(proxies.get(0).getProxy());
                            accountRepository.save(account);
                            proxyRepository.updateProxyGet(vps,System.currentTimeMillis(),proxies.get(0).getId());
                        }
                    }else{
                        Random rand=new Random();
                        if(proxyRepository.checkProxyLiveByUsername(username.trim())==0){
                            if(history.getGeo().equals("vn")){
                                proxy=proxyVNTrue.getValue().get(rand.nextInt(proxyVNTrue.getValue().size())).split(":");
                            }else if(history.getGeo().equals("us")){
                                proxy=proxyUSTrue.getValue().get(rand.nextInt(proxyUSTrue.getValue().size())).split(":");
                            }else if(history.getGeo().equals("kr")){
                                proxy=proxyKRTrue.getValue().get(rand.nextInt(proxyKRTrue.getValue().size())).split(":");
                            }
                            if(proxy.length==0){
                                history.setRunning(0);
                                history.setTimeget(System.currentTimeMillis());
                                historyCommentRepository.save(history);
                                fail_resp.put("status", "fail");
                                fail_resp.put("message", "Proxy die!");
                                return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                            }
                        }
                    }
                    String[] proxysetting=proxySettingRepository.getUserPassByHost(proxy[0]).split(",");
                    resp.put("channel_id", videos.get(0).getChannelid());
                    resp.put("status", "true");
                    resp.put("video_id", videos.get(0).getVideoid());
                    resp.put("video_title", videos.get(0).getVideotitle());
                    resp.put("username", history.getUsername());
                    resp.put("geo", accountRepository.getGeoByUsername(username.trim()));
                    resp.put("proxy",proxy[0]+":"+proxy[1]+":"+proxysetting[0]+":"+proxysetting[1]);
                    if(ran.nextInt(1000)<300){
                        resp.put("like", 1);
                    }else{
                        resp.put("like", 0);
                    }
                    if (ran.nextInt(10000) > 5000) {
                        resp.put("source", "dtn");
                    } else {
                        resp.put("source", "search");
                    }
                    if (videos.get(0).getDuration() > 360) {
                        resp.put("video_duration", 180 + ran.nextInt(180));
                    } else {
                        resp.put("video_duration", videos.get(0).getDuration());
                    }
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                } else {
                    history.setRunning(0);
                    historyCommentRepository.save(history);
                    fail_resp.put("status", "fail");
                    fail_resp.put("username", history.getUsername());
                    fail_resp.put("fail", "video");
                    fail_resp.put("message", "Không còn video để comment!");
                    return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                }
            } else {
                List<HistoryComment> histories = historyCommentRepository.getHistoriesById(historieId);
                //System.out.println(System.currentTimeMillis()-histories.get(0).getTimeget());
                if (System.currentTimeMillis() - histories.get(0).getTimeget() < (30000L + (long) ran.nextInt(60000))) {
                    //histories.get(0).setTimeget(System.currentTimeMillis());
                    //historyViewRepository.save(histories.get(0));
                    fail_resp.put("status", "fail");
                    fail_resp.put("username", histories.get(0).getUsername());
                    fail_resp.put("fail", "video");
                    fail_resp.put("message", "Không còn video để comment!");
                    return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                }
                if (histories.get(0).getGeo().equals("cmt-vn")) {
                    //videos=videoViewRepository.getvideoViewNoCheckMaxThreadVN(histories.get(0).getListvideo());
                    videos = videoCommentRepository.getvideoCommentVNTest(histories.get(0).getListvideo(),orderCommentTrue.getValue());
                } else if (histories.get(0).getGeo().equals("cmt-us")) {
                    //videos=videoViewRepository.getvideoViewNoCheckMaxThreadUS(histories.get(0).getListvideo());
                    videos = videoCommentRepository.getvideoCommentUSTest(histories.get(0).getListvideo(),orderCommentTrue.getValue());
                }else if (histories.get(0).getGeo().equals("cmt-kr")) {
                    //videos=videoViewRepository.getvideoViewNoCheckMaxThreadUS(histories.get(0).getListvideo());
                    videos = videoCommentRepository.getvideoCommentKRTest(histories.get(0).getListvideo(),orderCommentTrue.getValue());
                }else{
                    fail_resp.put("status", "fail");
                    fail_resp.put("message", "Username không cmt!");
                    return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                }
                if (videos.size() > 0) {
                    histories.get(0).setTimeget(System.currentTimeMillis());
                    histories.get(0).setVideoid(videos.get(0).getVideoid());
                    histories.get(0).setOrderid(videos.get(0).getOrderid());
                    histories.get(0).setRunning(1);
                    historyCommentRepository.save(histories.get(0));
                } else {
                    histories.get(0).setTimeget(System.currentTimeMillis());
                    histories.get(0).setRunning(0);
                    historyCommentRepository.save(histories.get(0));
                    fail_resp.put("status", "fail");
                    fail_resp.put("username", histories.get(0).getUsername());
                    fail_resp.put("fail", "video");
                    fail_resp.put("message", "Không còn video để comment!");
                    return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                }
                Service service = serviceRepository.getInfoService(videos.get(0).getService());
                dataCommentRepository.updateRunningComment(System.currentTimeMillis(),username.trim(),vps.trim(),videos.get(0).getOrderid());
                Thread.sleep(ran.nextInt(1000)+500);
                String comment=dataCommentRepository.getCommentByOrderIdAndUsername(videos.get(0).getOrderid(),username.trim());
                if(comment!=null){
                    if(historyCommentSumRepository.checkCommentIdTrue(Long.parseLong(comment.split(",")[0]))>0){
                        dataCommentRepository.updateRunningCommentDone(Long.parseLong(comment.split(",")[0]));
                        histories.get(0).setRunning(0);
                        historyCommentRepository.save(histories.get(0));
                        fail_resp.put("status", "fail");
                        fail_resp.put("username", histories.get(0).getUsername());
                        fail_resp.put("fail", "video");
                        fail_resp.put("message", "Không còn video để comment!");
                        return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                    }
                    resp.put("reply","");
                    if(dataReplyCommentRepository.checkReplyByCommentId(Long.parseLong(comment.split(",")[0]))>0){
                        resp.put("device_type","pc");
                    }else{
                        resp.put("device_type","mobile");
                    }
                    resp.put("comment_id", comment.split(",")[0]);
                    resp.put("comment", comment.substring(comment.indexOf(",")+1));
                }else if(service.getReply()>0) {
                    dataReplyCommentRepository.updateRunningComment(System.currentTimeMillis(),username.trim(),vps.trim(),videos.get(0).getOrderid());
                    Thread.sleep(ran.nextInt(1000)+500);
                    Long reply_id=dataReplyCommentRepository.getCommentByOrderIdAndUsername(videos.get(0).getOrderid(),username.trim());
                    if(reply_id!=null){
                        String info_reply=dataReplyCommentRepository.getInfoReplyBYId(reply_id);
                        resp.put("device_type","pc");
                        resp.put("reply",info_reply.split(",")[0]);
                        resp.put("comment_id", reply_id);
                        resp.put("comment", info_reply.substring(info_reply.indexOf(",")+1));
                    }else{
                        histories.get(0).setRunning(0);
                        historyCommentRepository.save(histories.get(0));
                        fail_resp.put("status", "fail");
                        fail_resp.put("username", histories.get(0).getUsername());
                        fail_resp.put("fail", "video");
                        fail_resp.put("message", "Không còn video để comment!");
                        return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                    }
                }else{
                    histories.get(0).setRunning(0);
                    historyCommentRepository.save(histories.get(0));
                    fail_resp.put("status", "fail");
                    fail_resp.put("username", histories.get(0).getUsername());
                    fail_resp.put("fail", "video");
                    fail_resp.put("message", "Không còn video để comment!");
                    return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                }
                String[] proxy=accountRepository.getProxyByUsername(username.trim()).split(":");
                if(proxy[0].trim().length()<4){
                    List<Proxy> proxies=null;
                    if (accountRepository.getGeoByUsername(username.trim()).equals("cmt-vn")) {
                        proxies=proxyRepository.getProxyFixAccountByGeo("vn");
                    } else if (accountRepository.getGeoByUsername(username.trim()).equals("cmt-us")) {
                        proxies=proxyRepository.getProxyFixAccountByGeo("us");
                    } else if (accountRepository.getGeoByUsername(username.trim()).equals("cmt-kr")) {
                        proxies=proxyRepository.getProxyFixAccountByGeo("kr");
                    }
                    if(proxies.size()!=0) {
                        proxy=proxies.get(0).getProxy().split(":");
                        Account account=accountRepository.findAccountByUsername(username.trim());
                        account.setProxy(proxies.get(0).getProxy());
                        accountRepository.save(account);
                        proxyRepository.updateProxyGet(vps,System.currentTimeMillis(),proxies.get(0).getId());
                    }
                }else{
                    Random rand=new Random();
                    if(proxyRepository.checkProxyLiveByUsername(username.trim())==0){
                        if(histories.get(0).getGeo().equals("vn")){
                            proxy=proxyVNTrue.getValue().get(rand.nextInt(proxyVNTrue.getValue().size())).split(":");
                        }else if(histories.get(0).getGeo().equals("us")){
                            proxy=proxyUSTrue.getValue().get(rand.nextInt(proxyUSTrue.getValue().size())).split(":");
                        }else if(histories.get(0).getGeo().equals("kr")){
                            proxy=proxyKRTrue.getValue().get(rand.nextInt(proxyKRTrue.getValue().size())).split(":");
                        }
                        if(proxy.length==0){
                            histories.get(0).setRunning(0);
                            histories.get(0).setTimeget(System.currentTimeMillis());
                            historyCommentRepository.save(histories.get(0));
                            fail_resp.put("status", "fail");
                            fail_resp.put("message", "Proxy die!");
                            return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.OK);
                        }
                    }
                }
                String[] proxysetting=proxySettingRepository.getUserPassByHost(proxy[0]).split(",");
                histories.get(0).setTimeget(System.currentTimeMillis());
                histories.get(0).setVps(vps);
                histories.get(0).setRunning(1);
                historyCommentRepository.save(histories.get(0));
                resp.put("channel_id", videos.get(0).getChannelid());
                resp.put("status", "true");
                resp.put("video_id", videos.get(0).getVideoid());
                resp.put("video_title", videos.get(0).getVideotitle());
                resp.put("username", histories.get(0).getUsername());
                resp.put("geo", accountRepository.getGeoByUsername(username.trim()));
                resp.put("proxy", proxy[0]+":"+proxy[1]+":"+proxysetting[0]+":"+proxysetting[1]);
                if(ran.nextInt(1000)<300){
                    resp.put("like", 1);
                }else{
                    resp.put("like", 0);
                }
                if (ran.nextInt(10000) > 5000) {
                    resp.put("source", "dtn");
                } else {
                    resp.put("source", "search");
                }
                if (videos.get(0).getDuration() > 360) {
                    resp.put("video_duration", 180 + ran.nextInt(180));
                } else {
                    resp.put("video_duration", videos.get(0).getDuration());
                }
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }

        } catch (Exception e) {
            StackTraceElement stackTraceElement = Arrays.stream(e.getStackTrace()).filter(ste -> ste.getClassName().equals(this.getClass().getName())).collect(Collectors.toList()).get(0);
            System.out.println(stackTraceElement.getMethodName());
            System.out.println(stackTraceElement.getLineNumber());
            System.out.println(stackTraceElement.getClassName());
            System.out.println(stackTraceElement.getFileName());
            fail_resp.put("status", "fail");
            fail_resp.put("fail", "sum");
            fail_resp.put("message", e.getMessage());
            return new ResponseEntity<String>(fail_resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(value = "/updatevideoidoff", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> updatevideoidoff(@RequestParam(defaultValue = "") String username,
                                         @RequestParam(defaultValue = "") String videoid, @RequestParam(defaultValue = "0") Long comment_id ) {
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
            Long historieId = historyCommentRepository.getId(username);
            if (historieId == null) {
                resp.put("status", "fail");
                resp.put("message", "Không tìm thấy username!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            } else {
                if(historyCommentSumRepository.checkCommentIdTrue(comment_id)==0){
                    dataCommentRepository.updateRunningCommentDone(comment_id);
                    HistoryCommentSum historySum = new HistoryCommentSum();
                    historySum.setUsername(username);
                    historySum.setTime(System.currentTimeMillis());
                    historySum.setCommentid(comment_id);
                    historySum.setCommnent(dataCommentRepository.getCommentByCommentId(comment_id));
                    historySum.setOrderid(videoCommentRepository.getOrderIdByVideoId(videoid.trim()));
                    try {
                        historyCommentSumRepository.save(historySum);
                    } catch (Exception e) {
                        try {
                            historyCommentSumRepository.save(historySum);
                        } catch (Exception f) {
                        }
                    }
                    if (historyCommentRepository.getListVideoById(historieId).length() > 44) {
                        historyCommentRepository.updateListVideoNew(videoid, historieId);
                    } else {
                        historyCommentRepository.updateListVideo(videoid, historieId);
                    }
                }
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

    @GetMapping(value = "/updatevideoid", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> updatevideoid(@RequestParam(defaultValue = "") String username,
                                         @RequestParam(defaultValue = "") String videoid,
                                         @RequestParam(defaultValue = "0") Long comment_id,
                                         @RequestParam(defaultValue = "") String lc
                                         ) {
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
            HistoryComment historyCmt = historyCommentRepository.getHistoryCmtByUsername(username);
            if (historyCmt==null) {
                resp.put("status", "fail");
                resp.put("message", "Không tìm thấy username!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            } else {
                if(dataCommentRepository.checkByCommentId(comment_id)>0){
                    if(historyCommentSumRepository.checkCommentIdTrue(comment_id)==0){
                        dataCommentRepository.updateRunningCommentDone(comment_id);
                        HistoryCommentSum historySum = new HistoryCommentSum();
                        historySum.setUsername(username);
                        historySum.setTime(System.currentTimeMillis());
                        historySum.setCommentid(comment_id);
                        historySum.setCommnent(dataCommentRepository.getCommentByCommentId(comment_id));
                        historySum.setOrderid(videoCommentRepository.getOrderIdByVideoId(videoid.trim()));
                        try {
                            historyCommentSumRepository.save(historySum);
                        } catch (Exception e) {
                            try {
                                historyCommentSumRepository.save(historySum);
                            } catch (Exception f) {
                            }
                        }

                        char target = ',';
                        long count = historyCmt.getListvideo().trim().chars().filter(ch -> ch == target).count();

                        if(count>=6){
                            //int occurrence = (int)count-2;  // Lần xuất hiện thứ n cần tìm
                            OptionalInt position = IntStream.range(0, historyCmt.getListvideo().trim().length())
                                    .filter(i -> historyCmt.getListvideo().trim().charAt(i) == target)
                                    .skip(count-6)//occurrence-1
                                    .findFirst();
                            historyCmt.setListvideo(historyCmt.getListvideo().trim().substring(position.getAsInt()+1)+videoid.trim()+",");
                        }else{
                            historyCmt.setListvideo(historyCmt.getListvideo()+videoid.trim()+",");
                        }
                        historyCmt.setTask_count(historyCmt.getTask_count()+1);
                        historyCommentRepository.save(historyCmt);

                        dataReplyCommentRepository.resetRunningReply(lc,comment_id);
                    }
                }else{
                    HistoryCommentSum historySum = new HistoryCommentSum();
                    historySum.setUsername(username);
                    historySum.setTime(System.currentTimeMillis());
                    historySum.setCommentid(comment_id);
                    historySum.setCommnent("Reply: "+dataReplyCommentRepository.getCommentByCommentId(comment_id));
                    historySum.setOrderid(videoCommentRepository.getOrderIdByVideoId(videoid.trim()));
                    try {
                        historyCommentSumRepository.save(historySum);
                    } catch (Exception e) {
                        try {
                            historyCommentSumRepository.save(historySum);
                        } catch (Exception f) {
                        }
                    }

                    char target = ',';
                    long count = historyCmt.getListvideo().trim().chars().filter(ch -> ch == target).count();

                    if(count>=6){
                        //int occurrence = (int)count-2;  // Lần xuất hiện thứ n cần tìm
                        OptionalInt position = IntStream.range(0, historyCmt.getListvideo().trim().length())
                                .filter(i -> historyCmt.getListvideo().trim().charAt(i) == target)
                                .skip(count-6)//occurrence-1
                                .findFirst();
                        historyCmt.setListvideo(historyCmt.getListvideo().trim().substring(position.getAsInt()+1)+videoid.trim()+",");
                    }else{
                        historyCmt.setListvideo(historyCmt.getListvideo()+videoid.trim()+",");
                    }
                    historyCmt.setTask_count(historyCmt.getTask_count()+1);
                    historyCommentRepository.save(historyCmt);

                    if(dataReplyCommentRepository.checkCheckDoneByCommentId(comment_id)>0){
                        DataComment dataComment = new DataComment();
                        dataComment.setOrderid(videoCommentRepository.getOrderIdByVideoId(videoid.trim()));
                        dataComment.setComment("reply done");
                        dataComment.setUsername(username);
                        dataComment.setRunning(2);
                        dataComment.setTimeget(System.currentTimeMillis());
                        dataComment.setVps("");
                        dataCommentRepository.save(dataComment);
                    }
                    dataReplyCommentRepository.deleteCommentDone(comment_id);
                }
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

    @GetMapping(value = "/checkcmttrue", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> checkcmttrue(@RequestParam(defaultValue = "") String username,@RequestParam(defaultValue = "0") Long comment_id ) {
        JSONObject resp = new JSONObject();
        if (username.length() == 0) {
            resp.put("status", "fail");
            resp.put("message", "Username không để trống");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        if (comment_id == 0) {
            resp.put("status", "fail");
            resp.put("message", "comment_id không để trống");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        try {
            if(dataCommentRepository.checkByCommentId(comment_id)>0){
                if(historyCommentSumRepository.checkCommentIdTrue(comment_id)>0||dataCommentRepository.getCommentByCommentIdAndUsername(comment_id,username.trim())==0){
                    resp.put("status", "fail");
                    resp.put("message", "Không cmt!");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }else{
                    resp.put("status", "true");
                    resp.put("message", "Sẵn sàng cmt!");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
            }else{
                if(dataReplyCommentRepository.getCommentByCommentIdAndUsername(comment_id,username.trim())==0){
                    resp.put("status", "fail");
                    resp.put("message", "Không cmt!");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }else{
                    resp.put("status", "true");
                    resp.put("message", "Sẵn sàng cmt!");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
            }

        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/update", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> update(@RequestParam(defaultValue = "") String username,
                                  @RequestParam(defaultValue = "") String videoid, @RequestParam(defaultValue = "") String channelid, @RequestParam(defaultValue = "0") Integer duration) {
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

            Long historieId = historyViewRepository.getId(username);
            if (historieId == null) {
                resp.put("status", "fail");
                resp.put("message", "Không tìm thấy username!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            } else {
                //histories.get(0).setProxy(proxy);
                //histories.get(0).setRunning(0);
                //histories.get(0).setVideoid("");
                //histories.get(0).setVps("");
                //historyRepository.save(histories.get(0));
                Integer check_duration = historyViewRepository.checkDurationViewByTimecheck(historieId, (long) (duration));
                if (check_duration > 0) {
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
                    resp.put("status", "true");
                    resp.put("message", "Update view thành công!");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
                //historyViewRepository.updateduration(duration,username,videoid);
                resp.put("status", "fail");
                resp.put("message", "Không update duration !");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }
        } catch (Exception e) {
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
            Long historieId = historyCommentRepository.getId(username);
            historyCommentRepository.resetThreadBuffhById(historieId);
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
            dataCommentRepository.resetRunningCommentByRunningHisCron();
            dataReplyCommentRepository.resetRunningCommentByRunningHisCron();
            historyCommentRepository.resetThreadThan15mcron();
            dataCommentRepository.resetRunningCommentByCron();
            resp.put("status", "true");
            resp.put("message", "Reset thread error thành công!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(value = "delcommentdone", produces = "application/hal+json;charset=utf8")
    public ResponseEntity<String> delcommentdone() {
        JSONObject resp = new JSONObject();
        try {
            dataCommentRepository.deleteCommentDoneByCron();
            dataReplyCommentRepository.deleteCommentDoneByCron();
            resp.put("status", "true");
            resp.put("message", "Delete comment thành công!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(value = "delnamebyvps", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> delnamebyvps(@RequestParam(defaultValue = "") String vps) {
        JSONObject resp = new JSONObject();
        if (vps.length() == 0) {
            resp.put("status", "fail");
            resp.put("message", "vps không để trống");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        try {
            historyViewRepository.resetThreadViewByVps(vps.trim());
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
    @GetMapping(value = "delhistorysumcron", produces = "application/hal+json;charset=utf8")
    public ResponseEntity<String> delhistorysumcron() {
        JSONObject resp = new JSONObject();
        try {
            historyCommentSumRepository.DelHistorySum();
            resp.put("status", "true");
            resp.put("message", "Delete history thành công!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }
}
