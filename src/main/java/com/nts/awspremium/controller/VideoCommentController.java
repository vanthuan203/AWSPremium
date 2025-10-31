package com.nts.awspremium.controller;

import com.nts.awspremium.GoogleApi;
import com.nts.awspremium.Openai;
import com.nts.awspremium.model.*;
import com.nts.awspremium.repositories.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/videocomment")
public class VideoCommentController {

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private DataCommentRepository dataCommentRepository;
    @Autowired
    private OpenAiKeyRepository openAiKeyRepository;
    @Autowired
    private DataReplyCommentRepository dataReplyCommentRepository;
    @Autowired
    private SettingRepository settingRepository;

    @Autowired
    private VideoViewHistoryRepository videoViewHistoryRepository;
    @Autowired
    private VideoViewRepository videoViewRepository;

    @Autowired
    private VideoCommentRepository videoCommentRepository;

    @Autowired
    private VideoCommentHistoryRepository videoCommentHistoryRepository;
    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private DataOrderRepository dataOrderRepository;

    @Autowired
    private GoogleAPIKeyRepository googleAPIKeyRepository;

    @Autowired
    private ProxyUSTrue proxyVNTrue;
    @Autowired
    private ProxySettingRepository proxySettingRepository;

    @GetMapping(path = "getorderview", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> getorderview(@RequestHeader(defaultValue = "") String Authorization, @RequestParam(defaultValue = "") String user) {
        JSONObject resp = new JSONObject();
        //Integer checktoken= adminRepository.FindAdminByToken(Authorization.split(",")[0]);
        List<Admin> admins = adminRepository.FindByToken(Authorization.trim());
        if (Authorization.length() == 0 || admins.size() == 0) {
            resp.put("status", "fail");
            resp.put("message", "Token expired");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        try {
            List<OrderCommentRunning> orderRunnings;
            if (user.length() == 0) {
                orderRunnings = videoCommentRepository.getOrder();

            } else {
                orderRunnings = videoCommentRepository.getOrder(user.trim());
            }

            JSONArray jsonArray = new JSONArray();

            for (int i = 0; i < orderRunnings.size(); i++) {
                JSONObject obj = new JSONObject();
                obj.put("orderid", orderRunnings.get(i).getOrderId());
                obj.put("videoid", orderRunnings.get(i).getVideoId());
                obj.put("videotitle", orderRunnings.get(i).getVideoTitle());
                obj.put("commentstart", orderRunnings.get(i).getCommentStart());
                obj.put("maxthreads", orderRunnings.get(i).getMaxthreads());
                obj.put("insertdate", orderRunnings.get(i).getInsertDate());
                obj.put("total", orderRunnings.get(i).getTotal());
                obj.put("commentorder", orderRunnings.get(i).getCommentOrder());
                obj.put("note", orderRunnings.get(i).getNote());
                obj.put("duration", orderRunnings.get(i).getDuration());
                obj.put("service", orderRunnings.get(i).getService());
                obj.put("user", orderRunnings.get(i).getUser());
                obj.put("commenttotal", orderRunnings.get(i).getCommentTotal());
                obj.put("comment24h", orderRunnings.get(i).getComment24h());
                obj.put("price", orderRunnings.get(i).getPrice());
                obj.put("geo",  orderRunnings.get(i).getGeo());
                obj.put("ai",  orderRunnings.get(i).getAi());
                obj.put("live",  orderRunnings.get(i).getLive());
                obj.put("lc_code",  orderRunnings.get(i).getLc_code());
                jsonArray.add(obj);
            }

            resp.put("total", orderRunnings.size());
            resp.put("videocomment", jsonArray);
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "getorderviewcheckcannel", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> getorderviewcheckcannel(@RequestHeader(defaultValue = "") String Authorization, @RequestParam(defaultValue = "") String user) {
        JSONObject resp = new JSONObject();
        //Integer checktoken= adminRepository.FindAdminByToken(Authorization.split(",")[0]);
        List<Admin> admins = adminRepository.FindByToken(Authorization.trim());
        if (Authorization.length() == 0 || admins.size() == 0) {
            resp.put("status", "fail");
            resp.put("message", "Token expired");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        try {
            List<OrderCommentRunning> orderRunnings;
            if (user.length() == 0) {
                orderRunnings = videoCommentRepository.getOrderCheckCancel();

            } else {
                orderRunnings = videoCommentRepository.getOrderCheckCancel(user.trim());
            }

            JSONArray jsonArray = new JSONArray();

            for (int i = 0; i < orderRunnings.size(); i++) {
                JSONObject obj = new JSONObject();
                obj.put("orderid", orderRunnings.get(i).getOrderId());
                obj.put("videoid", orderRunnings.get(i).getVideoId());
                obj.put("videotitle", orderRunnings.get(i).getVideoTitle());
                obj.put("commentstart", orderRunnings.get(i).getCommentStart());
                obj.put("maxthreads", orderRunnings.get(i).getMaxthreads());
                obj.put("insertdate", orderRunnings.get(i).getInsertDate());
                obj.put("total", orderRunnings.get(i).getTotal());
                obj.put("commentorder", orderRunnings.get(i).getCommentOrder());
                obj.put("note", orderRunnings.get(i).getNote());
                obj.put("duration", orderRunnings.get(i).getDuration());
                obj.put("service", orderRunnings.get(i).getService());
                obj.put("user", orderRunnings.get(i).getUser());
                obj.put("commenttotal", orderRunnings.get(i).getCommentTotal());
                obj.put("price", orderRunnings.get(i).getPrice());
                jsonArray.add(obj);
            }

            resp.put("total", orderRunnings.size());
            resp.put("videocomment", jsonArray);
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/updateCurrentTotalCheck", produces = "application/hal+json;charset=utf8")
    public ResponseEntity<String> updateCurrentTotalCheck() throws IOException, ParseException {
        JSONObject resp = new JSONObject();
        List<String> listvideo = videoCommentRepository.getVideoByTotalCheck(50);
        if (listvideo.size() == 0) {
            resp.put("status", "true");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }
        String s_videoid = "";
        List<String> videofale =  new ArrayList<String>();
        for (int i = 0; i < listvideo.size(); i++) {
            if (i == 0) {
                s_videoid = listvideo.get(i);
            } else {
                s_videoid = s_videoid + "," + listvideo.get(i);
            }
            videofale.add(listvideo.get(i).toString());
        }
        //VIDEOOOOOOOOOOOOOOO
        OkHttpClient client1 = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();

        Request request1 = null;
        List<GoogleAPIKey> keys = googleAPIKeyRepository.getAllByState();
        request1 = new Request.Builder().url("https://www.googleapis.com/youtube/v3/videos?key=" + keys.get(0).getKey().trim() + "&fields=items(id,statistics(commentCount))&part=statistics&id=" + s_videoid).get().build();
        keys.get(0).setCount(keys.get(0).getCount() + 1L);
        googleAPIKeyRepository.save(keys.get(0));
        Response response1 = client1.newCall(request1).execute();

        String resultJson1 = response1.body().string();

        Object obj1 = new JSONParser().parse(resultJson1);

        JSONObject jsonObject1 = (JSONObject) obj1;
        JSONArray items = (JSONArray) jsonObject1.get("items");
        if(items==null){
            resp.put("status", "false");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }
        JSONArray jsonArray = new JSONArray();
        Iterator k = items.iterator();

        while (k.hasNext()) {
            try {
                JSONObject video = (JSONObject) k.next();
                JSONObject obj = new JSONObject();
                JSONObject statistics = (JSONObject) video.get("statistics");
                Integer current_count =Integer.parseInt(statistics.get("commentCount").toString());
                /*
                VideoComment videoComment =videoCommentRepository.getVideoCmtByVideoid(video.get("id").toString());
                if(videoComment!=null && current_count<=videoComment.getCommentstart()){
                    videoCommentRepository.updateCommentTotal(videoComment.getVideoid());
                    delete("1",videoComment.getVideoid(),1);
                }else{
                    videoCommentRepository.updateNote(current_count, video.get("id").toString());
                }

                 */
                videoCommentRepository.updateNote(current_count, video.get("id").toString());
                videofale.remove(video.get("id").toString());
            } catch (Exception e) {
                continue;
            }
        }
        videoCommentRepository.updateValid(videofale);
        resp.put("status", "true");
        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
    }

    @GetMapping(path = "updateorderviewcron", produces = "application/hal+json;charset=utf8")
    public ResponseEntity<String> updateorderviewcron() {
        JSONObject resp = new JSONObject();
        //Integer checktoken= adminRepository.FindAdminByToken(Authorization.split(",")[0]);
        try {
            List<String> viewBuff;
            List<VideoComment> videoViewList = videoCommentRepository.getAllOrder();
            viewBuff = videoCommentRepository.getTotalCommentBuffByDataComment();
            Boolean check_current=false;
            TimeZone timeZone = TimeZone.getTimeZone("GMT+7");
            Calendar calendar = Calendar.getInstance(timeZone);
            int min = calendar.get(Calendar.MINUTE);
            if(min%2==0){
                check_current=true;
            }
            for (int i = 0; i < videoViewList.size(); i++) {
                int viewtotal = 0;
                int view24h =0;
                if(check_current){
                    Random random=new Random();
                    String[] proxy=proxyVNTrue.getValue().get(random.nextInt(proxyVNTrue.getValue().size())).split(":");
                    String[] proxysetting=proxySettingRepository.getUserPassByHost(proxy[0]).split(",");
                    view24h=GoogleApi.getCountCommentCurrent(videoViewList.get(i).getVideoid(), new String[]{proxy[0], proxy[1], proxysetting[0],proxysetting[1]});
                    if(view24h==0){
                        view24h=videoViewList.get(i).getComment24h();
                    }
                }else{
                    view24h=videoViewList.get(i).getComment24h();
                }
                for (int j = 0; j < viewBuff.size(); j++) {
                    if (videoViewList.get(i).getVideoid().equals(viewBuff.get(j).split(",")[0])) {
                        viewtotal = Integer.parseInt(viewBuff.get(j).split(",")[1]);
                    }
                }
                try {
                    if(viewtotal>videoViewList.get(i).getCommenttotal()){
                        videoCommentRepository.updateViewOrderByVideoId(viewtotal,view24h, System.currentTimeMillis(), videoViewList.get(i).getVideoid());
                    }
                } catch (Exception e) {

                }
            }
            //JSONArray lineItems = jsonObject.getJSONArray("lineItems");

            resp.put("total", videoViewList.size());
            resp.put("videocomment", true);
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "updateOrderCheckCancelCron", produces = "application/hal+json;charset=utf8")
    public ResponseEntity<String> updateOrderCheckCancelCron() {
        JSONObject resp = new JSONObject();
        try {
            videoCommentRepository.updateOrderCheckCancel();
            resp.put("status", true);
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(value = "/updateRunningLiveOrder", produces = "application/hal+json;charset=utf8")
    public ResponseEntity<String> updateRunningLiveOrder() throws IOException, ParseException {
        JSONObject resp = new JSONObject();
        List<VideoComment> listvideo = videoCommentRepository.getAllOrderLiveChatRunning();
        if (listvideo.size() == 0) {
            resp.put("status", "true");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }
        String s_videoid = "";
        for (int i = 0; i < listvideo.size(); i++) {
            if (i == 0) {
                s_videoid = listvideo.get(i).getVideoid();
            } else {
                s_videoid = s_videoid + "," + listvideo.get(i).getVideoid();
            }
            //System.out.println(s_videoid);
        }
        //VIDEOOOOOOOOOOOOOOO
        OkHttpClient client1 = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();

        Request request1 = null;
        List<GoogleAPIKey> keys = googleAPIKeyRepository.getAllByState();
        request1 = new Request.Builder().url("https://www.googleapis.com/youtube/v3/videos?key=" + keys.get(0).getKey().trim() + "&fields=items(id,snippet(liveBroadcastContent))&part=snippet&id=" + s_videoid).get().build();
        keys.get(0).setCount(keys.get(0).getCount() + 1L);
        googleAPIKeyRepository.save(keys.get(0));
        Response response1 = client1.newCall(request1).execute();

        String resultJson1 = response1.body().string();

        Object obj1 = new JSONParser().parse(resultJson1);

        JSONObject jsonObject1 = (JSONObject) obj1;
        JSONArray items = (JSONArray) jsonObject1.get("items");
        if(items==null){
            resp.put("status", "fail");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }
        JSONArray jsonArray = new JSONArray();
        Iterator k = items.iterator();
        Setting setting = settingRepository.getSettingId1();
        while (k.hasNext()) {
            try {
                JSONObject video = (JSONObject) k.next();
                JSONObject snippet = (JSONObject) video.get("snippet");
                VideoComment videoView = videoCommentRepository.getVideoCmtByVideoid(video.get("id").toString());
                if (snippet.get("liveBroadcastContent").toString().equals("live")&&videoView.getMaxthreads()==-2) {
                    videoCommentRepository.updateRunningLiveOrderByVideoId(videoView.getVideoid());
                }else if(!snippet.get("liveBroadcastContent").toString().equals("live")&&videoView.getMaxthreads()>=0){
                    delete("1",videoView.getVideoid(),1);
                }else if(snippet.get("liveBroadcastContent").toString().equals("none")&&videoView.getMaxthreads()==-2){
                    delete("1",videoView.getVideoid(),1);
                }
            } catch (Exception e) {
                resp.put("status", e);
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }

        }
        resp.put("status", "true");
        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
    }


    @GetMapping(path = "getorderviewhhistory", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> getorderviewhhistory(@RequestHeader(defaultValue = "") String Authorization, @RequestParam(defaultValue = "") String user) {
        JSONObject resp = new JSONObject();
        //Integer checktoken= adminRepository.FindAdminByToken(Authorization.split(",")[0]);
        List<Admin> admins = adminRepository.FindByToken(Authorization.trim());
        if (Authorization.length() == 0 || admins.size() == 0) {
            resp.put("status", "fail");
            resp.put("message", "Token expired");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        try {
            List<OrderCommentHistory> orderRunnings;
            if (user.length() == 0) {
                orderRunnings = videoCommentHistoryRepository.getVideoViewHistories();
            } else {
                orderRunnings = videoCommentHistoryRepository.getVideoViewHistories(user.trim());
            }
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < orderRunnings.size(); i++) {
                JSONObject obj = new JSONObject();
                obj.put("orderid", orderRunnings.get(i).getOrderId());
                obj.put("videoid", orderRunnings.get(i).getVideoid());
                obj.put("commentstart", orderRunnings.get(i).getCommentstart());
                obj.put("insertdate", orderRunnings.get(i).getInsertdate());
                obj.put("user", orderRunnings.get(i).getUser());
                obj.put("note", orderRunnings.get(i).getNote());
                obj.put("enddate", orderRunnings.get(i).getEnddate());
                obj.put("cancel", orderRunnings.get(i).getCancel());
                //obj.put("home_rate", orderRunnings.get(i).get());
                obj.put("commentend", orderRunnings.get(i).getCommentend());
                obj.put("commenttotal", orderRunnings.get(i).getCommenttotal());
                obj.put("commentorder", orderRunnings.get(i).getCommentorder());
                obj.put("price", orderRunnings.get(i).getPrice());
                obj.put("service", orderRunnings.get(i).getService());
                obj.put("geo", orderRunnings.get(i).getGeo());
                obj.put("ai", orderRunnings.get(i).getAi());
                obj.put("live", orderRunnings.get(i).getLive());
                obj.put("lc_code",  orderRunnings.get(i).getLc_code());
                jsonArray.add(obj);
            }
            //JSONArray lineItems = jsonObject.getJSONArray("lineItems");

            resp.put("total", orderRunnings.size());
            resp.put("videocomment", jsonArray);
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "findorder", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> findorder(@RequestHeader(defaultValue = "") String Authorization, @RequestParam(defaultValue = "") String videoid) {
        JSONObject resp = new JSONObject();
        List<Admin> admins = adminRepository.FindByToken(Authorization.trim());
        if (Authorization.length() == 0 || admins.size() == 0) {
            resp.put("status", "fail");
            resp.put("message", "Token expired");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        try {
            List<String> ordersArrInput = new ArrayList<>();
            ordersArrInput.addAll(Arrays.asList(videoid.split(",")));
            List<VideoCommentHistory> orderRunnings = videoCommentHistoryRepository.getVideoViewHistoriesByListVideoId(ordersArrInput);
            if (orderRunnings.size() == 0) {
                resp.put("status", "fail");
                resp.put("total", orderRunnings.size());
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < orderRunnings.size(); i++) {
                JSONObject obj = new JSONObject();
                obj.put("orderid", orderRunnings.get(i).getOrderid());
                obj.put("videoid", orderRunnings.get(i).getVideoid());
                obj.put("videotitle", orderRunnings.get(i).getVideotitle());
                obj.put("commentstart", orderRunnings.get(i).getCommentstart());
                obj.put("maxthreads", orderRunnings.get(i).getMaxthreads());
                obj.put("insertdate", orderRunnings.get(i).getInsertdate());
                obj.put("user", orderRunnings.get(i).getUser());
                obj.put("note", orderRunnings.get(i).getNote());
                obj.put("duration", orderRunnings.get(i).getDuration());
                obj.put("enddate", orderRunnings.get(i).getEnddate());
                obj.put("cancel", orderRunnings.get(i).getCancel());
                //obj.put("home_rate", orderRunnings.get(i).get());
                obj.put("commentend", orderRunnings.get(i).getCommentend());
                obj.put("commenttotal", orderRunnings.get(i).getCommenttotal());
                obj.put("commentorder", orderRunnings.get(i).getCommentorder());
                obj.put("price", orderRunnings.get(i).getPrice());
                obj.put("service", orderRunnings.get(i).getService());
                jsonArray.add(obj);
            }
            //JSONArray lineItems = jsonObject.getJSONArray("lineItems");
            resp.put("total", orderRunnings.size());
            resp.put("videocomment", jsonArray);
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(path = "getcountviewbufforder", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> getcountviewbufforder(@RequestParam(defaultValue = "") String user) {
        JSONObject resp = new JSONObject();
        //Integer checktoken= adminRepository.FindAdminByToken(Authorization.split(",")[0]);
        try {
            Integer countvieworder = 0;
            if (user.length() == 0) {
                countvieworder = videoViewRepository.getCountViewBuffOrder();
            } else {
                countvieworder = videoViewRepository.getCountViewBuffOrder(user.trim());
            }
            resp.put("totalvieworder", countvieworder);
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "getinfo", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> getinfo(@RequestParam(defaultValue = "") Long orderid) {
        JSONObject resp = new JSONObject();
        //Integer checktoken= adminRepository.FindAdminByToken(Authorization.split(",")[0]);
        try {
            VideoView videoView = videoViewRepository.getInfoByOrderId(orderid);
            JSONArray jsonArray = new JSONArray();


            JSONObject obj = new JSONObject();
            obj.put("orderid", videoView.getOrderid());
            obj.put("videoid", videoView.getVideoid());
            obj.put("videotitle", videoView.getVideotitle());
            obj.put("viewstart", videoView.getViewstart());
            obj.put("maxthreads", videoView.getMaxthreads());
            obj.put("insertdate", videoView.getInsertdate());
            obj.put("vieworder", videoView.getVieworder());
            obj.put("note", videoView.getNote());
            obj.put("duration", videoView.getDuration());
            obj.put("service", videoView.getService());

            obj.put("view24h", videoView.getView24h());
            obj.put("viewtotal", videoView.getViewtotal());
            obj.put("price", videoView.getPrice());
            if (videoView.getService() == 669 || videoView.getService() == 688 || videoView.getService() == 689) {
                obj.put("keyword", dataOrderRepository.getListKeyByOrderid(orderid));
            } else {
                obj.put("keyword", "");
            }
            jsonArray.add(obj);

            resp.put("info", jsonArray);
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "getcountviewbuffedorder", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> getcountviewbuffedorder(@RequestParam(defaultValue = "") String user) {
        JSONObject resp = new JSONObject();
        //Integer checktoken= adminRepository.FindAdminByToken(Authorization.split(",")[0]);
        try {
            Integer countvieworder = 0;
            if (user.length() == 0) {
                countvieworder = videoViewRepository.getCountViewBuffedOrder();
            } else {
                countvieworder = videoViewRepository.getCountViewBuffedOrder(user.trim());
            }
            resp.put("totalviewbuffedorder", countvieworder);
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }


    @DeleteMapping(path = "delete", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> delete(@RequestHeader(defaultValue = "") String Authorization, @RequestParam(defaultValue = "") String videoid, @RequestParam(defaultValue = "1") Integer cancel) {
        JSONObject resp = new JSONObject();
        //Integer checktoken= adminRepository.FindAdminByToken(Authorization.split(",")[0]);
        List<Admin> admins = adminRepository.FindByToken(Authorization.trim());
        if (Authorization.length() == 0 || admins.size() == 0) {
            resp.put("status", "fail");
            resp.put("message", "Token expired");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        if (videoid.length() == 0) {
            resp.put("status", "fail");
            resp.put("message", "videoid không được để trống");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        try {
            String[] videoidArr = videoid.split(",");
            for (int i = 0; i < videoidArr.length; i++) {

                Long enddate = System.currentTimeMillis();
                List<VideoComment> videoBuffh = videoCommentRepository.getVideoBuffhById(videoidArr[i].trim());
                VideoCommentHistory videoBuffhnew = new VideoCommentHistory();
                videoBuffhnew.setOrderid(videoBuffh.get(0).getOrderid());
                videoBuffhnew.setDuration(videoBuffh.get(0).getDuration());
                videoBuffhnew.setInsertdate(videoBuffh.get(0).getInsertdate());
                videoBuffhnew.setService(videoBuffh.get(0).getService());
                videoBuffhnew.setChannelid(videoBuffh.get(0).getChannelid());
                videoBuffhnew.setVideotitle(videoBuffh.get(0).getVideotitle());
                videoBuffhnew.setVideoid(videoBuffh.get(0).getVideoid());
                videoBuffhnew.setCommentstart(videoBuffh.get(0).getCommentstart());
                videoBuffhnew.setCommentorder(videoBuffh.get(0).getCommentorder());
                videoBuffhnew.setMaxthreads(videoBuffh.get(0).getMaxthreads());
                videoBuffhnew.setNote(videoBuffh.get(0).getNote());
                videoBuffhnew.setLc_code(videoBuffh.get(0).getLc_code());
                videoBuffhnew.setNumbh(0);
                videoBuffhnew.setTimecheck(0L);
                //videoBuffhnew.setPrice(videoBuffh.get(0).getPrice());
                Service service = serviceRepository.getService(videoBuffh.get(0).getService());
                if (cancel == 1) {
                    List<Admin> user = adminRepository.getAdminByUser(videoBuffh.get(0).getUser());
                    //Hoàn tiền những view chưa buff
                    int viewbuff = videoBuffh.get(0).getCommenttotal();
                    float price_refund = ((videoBuffh.get(0).getCommentorder() - videoBuffh.get(0).getCommenttotal()) / (float) videoBuffh.get(0).getCommentorder()) * videoBuffh.get(0).getPrice();
                    //float pricebuffed=(videoBuffh.get(0).getViewtotal()/1000F)*service.getRate()*((float)(100-admins.get(0).getDiscount())/100);
                    float pricebuffed = (videoBuffh.get(0).getPrice() - price_refund);
                    videoBuffhnew.setPrice(pricebuffed);
                    if (viewbuff == 0) {
                        videoBuffhnew.setCancel(1);
                    } else {
                        videoBuffhnew.setCancel(2);
                    }
                    //hoàn tiền & add thong báo số dư
                    int viewthan = (int) (videoBuffh.get(0).getCommentorder() - viewbuff);
                    //
                    Float balance_update=adminRepository.updateBalanceFine(price_refund,user.get(0).getUsername().trim());
                    Balance balance = new Balance();
                    balance.setUser(user.get(0).getUsername().trim());
                    balance.setTime(System.currentTimeMillis());
                    balance.setTotalblance(balance_update);
                    balance.setBalance(price_refund);
                    balance.setService(videoBuffh.get(0).getService());
                    balance.setNote("Refund " + (viewthan) + " cmt cho video " + videoBuffh.get(0).getVideoid());
                    balanceRepository.save(balance);
                } else {
                    videoBuffhnew.setPrice(videoBuffh.get(0).getPrice());
                    videoBuffhnew.setCancel(0);
                }
                videoBuffhnew.setUser(videoBuffh.get(0).getUser());
                videoBuffhnew.setEnddate(enddate);
                videoBuffhnew.setCommenttotal(videoBuffh.get(0).getCommenttotal());
                videoCommentHistoryRepository.save(videoBuffhnew);
                videoCommentRepository.deletevideoByVideoId(videoidArr[i].trim());
            }
            resp.put("videocomment", "");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(path = "updatechanneldonecron", produces = "application/hal+json;charset=utf8")
    public ResponseEntity<String> updatechanneldonecron() {
        JSONObject resp = new JSONObject();
        //Integer checktoken= adminRepository.FindAdminByToken(Authorization.split(",")[0]);
        try {
            //historyRepository.updateHistoryByAccount();
            List<VideoComment> videoBuffh = videoCommentRepository.getOrderFullCmt();
            for (int i = 0; i < videoBuffh.size(); i++) {
                Long enddate = System.currentTimeMillis();

                Service service = serviceRepository.getService(videoBuffh.get(0).getService());

                VideoCommentHistory videoBuffhnew = new VideoCommentHistory();
                videoBuffhnew.setOrderid(videoBuffh.get(i).getOrderid());
                videoBuffhnew.setDuration(videoBuffh.get(i).getDuration());
                videoBuffhnew.setInsertdate(videoBuffh.get(i).getInsertdate());
                videoBuffhnew.setChannelid(videoBuffh.get(i).getChannelid());
                videoBuffhnew.setVideotitle(videoBuffh.get(i).getVideotitle());
                videoBuffhnew.setVideoid(videoBuffh.get(i).getVideoid());
                videoBuffhnew.setCommentstart(videoBuffh.get(i).getCommentstart());
                videoBuffhnew.setMaxthreads(videoBuffh.get(i).getMaxthreads());
                videoBuffhnew.setNote(videoBuffh.get(i).getNote());
                videoBuffhnew.setCancel(0);
                videoBuffhnew.setNumbh(0);
                videoBuffhnew.setTimecheck(0L);
                videoBuffhnew.setUser(videoBuffh.get(i).getUser());
                videoBuffhnew.setLc_code(videoBuffh.get(i).getLc_code());
                videoBuffhnew.setEnddate(enddate);
                videoBuffhnew.setService(videoBuffh.get(i).getService());
                videoBuffhnew.setCommenttotal(videoBuffh.get(i).getCommenttotal());
                videoBuffhnew.setCommentorder(videoBuffh.get(i).getCommentorder());
                videoBuffhnew.setPrice(videoBuffh.get(i).getPrice());
                try {
                    videoCommentHistoryRepository.save(videoBuffhnew);
                    videoCommentRepository.deletevideoByVideoId(videoBuffh.get(i).getVideoid().trim());
                } catch (Exception e) {

                }
            }
            resp.put("status", "true");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "updateVideoReplyDoneCron", produces = "application/hal+json;charset=utf8")
    public ResponseEntity<String> updateVideoReplyDoneCron() {
        JSONObject resp = new JSONObject();
        //Integer checktoken= adminRepository.FindAdminByToken(Authorization.split(",")[0]);
        try {
            //historyRepository.updateHistoryByAccount();
            List<VideoComment> videoBuffh = videoCommentRepository.getOrderFullReply();
            for (int i = 0; i < videoBuffh.size(); i++) {
                Long enddate = System.currentTimeMillis();

                VideoCommentHistory videoBuffhnew = new VideoCommentHistory();
                videoBuffhnew.setOrderid(videoBuffh.get(i).getOrderid());
                videoBuffhnew.setDuration(videoBuffh.get(i).getDuration());
                videoBuffhnew.setInsertdate(videoBuffh.get(i).getInsertdate());
                videoBuffhnew.setChannelid(videoBuffh.get(i).getChannelid());
                videoBuffhnew.setVideotitle(videoBuffh.get(i).getVideotitle());
                videoBuffhnew.setVideoid(videoBuffh.get(i).getVideoid());
                videoBuffhnew.setCommentstart(videoBuffh.get(i).getCommentstart());
                videoBuffhnew.setMaxthreads(videoBuffh.get(i).getMaxthreads());
                videoBuffhnew.setNote(videoBuffh.get(i).getNote());
                videoBuffhnew.setCancel(0);
                videoBuffhnew.setNumbh(0);
                videoBuffhnew.setTimecheck(0L);
                videoBuffhnew.setUser(videoBuffh.get(i).getUser());
                videoBuffhnew.setEnddate(enddate);
                videoBuffhnew.setService(videoBuffh.get(i).getService());
                videoBuffhnew.setCommenttotal(videoBuffh.get(i).getCommenttotal());
                videoBuffhnew.setCommentorder(videoBuffh.get(i).getCommentorder());
                videoBuffhnew.setPrice(videoBuffh.get(i).getPrice());
                videoBuffhnew.setLc_code(videoBuffh.get(i).getLc_code());
                try {
                    videoCommentHistoryRepository.save(videoBuffhnew);
                    videoCommentRepository.deletevideoByVideoId(videoBuffh.get(i).getVideoid().trim());
                } catch (Exception e) {

                }
            }
            resp.put("status", "true");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(path = "updateStateComment", produces = "application/hal+json;charset=utf8")
    public ResponseEntity<String> updateStateComment() {
        JSONObject resp = new JSONObject();
        //Integer checktoken= adminRepository.FindAdminByToken(Authorization.split(",")[0]);
        try {
            //historyRepository.updateHistoryByAccount();
            List<VideoComment> videoComments = videoCommentRepository.getOrderThreadNull();
            Setting setting = settingRepository.getSettingId1();
            for (int i = 0; i < videoComments.size(); i++) {
                String[] comments;
                Service service = serviceRepository.getServiceNoCheckEnabled(videoComments.get(i).getService());

                if(service.getAi()==1){
                    Integer count_render=videoComments.get(i).getCommentorder()-videoComments.get(i).getComment_render();
                    count_render=count_render>=100?100:count_render;
                    String prompt=videoComments.get(i).getListcomment().replace("#cmcmedia@$123",count_render.toString());
                    String list_Comment=null;
                    if(service.getExpired()==0){
                        list_Comment= Openai.chatGPT(prompt,openAiKeyRepository.get_OpenAI_Key());
                    }else{
                        list_Comment= Openai.chatGPT4oMini(prompt,openAiKeyRepository.get_OpenAI_Key());
                    }
                    if(list_Comment==null){
                        continue;
                    }else {
                        comments = list_Comment.split("\n");
                    }
                }else if(service.getAi()==2){
                    if(videoComments.get(i).getListcomment().length()==0){
                        Integer count_render=videoComments.get(i).getCommentorder()-videoComments.get(i).getComment_render();
                        count_render=count_render>=100?100:count_render;
                        String uuid=Openai.createTask("https://www.youtube.com/watch?v="+ videoComments.get(i).getVideoid(),count_render,"youtube","comment",0,videoComments.get(i).getVideotitle(),videoComments.get(i).getChanneltitle(),videoComments.get(i).getDescription());
                        if(uuid!=null){
                            videoComments.get(i).setListcomment(uuid);
                            videoCommentRepository.save( videoComments.get(i));
                        }
                        continue;
                    }
                    String status=Openai.statusTask(videoComments.get(i).getListcomment());
                    if(status!=null&&status.equals("completed")) {

                        String list_Comment = Openai.getTask(videoComments.get(i).getListcomment());
                        if (list_Comment == null) {
                            continue;
                        } else {
                            comments = list_Comment.split("\\R");
                        }
                        videoComments.get(i).setListcomment("");
                    }else  if(status!=null&&status.equals("failed"))  {
                        Integer count_render=videoComments.get(i).getCommentorder()-videoComments.get(i).getComment_render();
                        count_render=count_render>=100?100:count_render;
                        String uuid=Openai.createTask("https://www.youtube.com/watch?v="+ videoComments.get(i).getVideoid(),count_render,"youtube","comment",0,videoComments.get(i).getVideotitle(),videoComments.get(i).getChanneltitle(),videoComments.get(i).getDescription());
                        if(uuid!=null){
                            videoComments.get(i).setListcomment(uuid);
                            videoCommentRepository.save( videoComments.get(i));
                        }
                        continue;
                    }else{
                        continue;
                    }
                }else{
                    comments = videoComments.get(i).getListcomment().split("\n");
                }
                for (int j = 0; j < comments.length; j++) {
                    if (comments[j].length() == 0) {
                        continue;
                    }
                    DataComment dataComment = new DataComment();
                    dataComment.setOrderid(videoComments.get(i).getOrderid());
                    dataComment.setComment(comments[j]);
                    dataComment.setUsername("");
                    dataComment.setRunning(0);
                    dataComment.setTimeget(0L);
                    dataComment.setVps("");
                    dataCommentRepository.save(dataComment);
                }
                videoComments.get(i).setComment_render(dataCommentRepository.count_All_By_OrderId(videoComments.get(i).getOrderid()));
                if(service.getAi()==2&&videoComments.get(i).getComment_render()< videoComments.get(i).getCommentorder()){
                    Integer count_render=videoComments.get(i).getCommentorder()-videoComments.get(i).getComment_render();
                    count_render=count_render>=100?100:count_render;
                    String uuid=Openai.createTask("https://www.youtube.com/watch?v="+ videoComments.get(i).getVideoid(),count_render,"youtube","comment",0,videoComments.get(i).getVideotitle(),videoComments.get(i).getChanneltitle(),videoComments.get(i).getDescription());
                    if(uuid!=null){
                        videoComments.get(i).setListcomment(uuid);
                    }
                }
                if(service.getExpired()==0){
                    int max_thread = service.getThread() + ((int)(videoComments.get(i).getCommentorder() / 100)<1?0:(int)(videoComments.get(i).getCommentorder() / 100));
                    if (max_thread <= 50) {
                        videoComments.get(i).setMaxthreads(max_thread);
                    } else {
                        videoComments.get(i).setMaxthreads(50);
                    }
                }else{
                    videoComments.get(i).setMaxthreads(service.getThread());
                }
                videoCommentRepository.save(videoComments.get(i));
            }
            resp.put("status", "true");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "updateStateCommentAI", produces = "application/hal+json;charset=utf8")
    public ResponseEntity<String> updateStateCommentAI() {
        JSONObject resp = new JSONObject();
        //Integer checktoken= adminRepository.FindAdminByToken(Authorization.split(",")[0]);
        try {
            //historyRepository.updateHistoryByAccount();
            List<VideoComment> videoComments = videoCommentRepository.getOrderAIThreadNull();
            for (int i = 0; i < videoComments.size(); i++) {
                Service service = serviceRepository.getServiceNoCheckEnabled(videoComments.get(i).getService());
                if(service.getAi()==1){
                    String[] comments;
                    Integer count_render=videoComments.get(i).getCommentorder()-videoComments.get(i).getComment_render();
                    count_render=count_render>=100?100:count_render;
                    String prompt=videoComments.get(i).getListcomment().replace("#cmcmedia@$123",count_render.toString());
                    String list_Comment=null;
                    if(service.getExpired()==0){
                        list_Comment= Openai.chatGPT(prompt,openAiKeyRepository.get_OpenAI_Key());
                    }else{
                        list_Comment= Openai.chatGPT4oMini(prompt,openAiKeyRepository.get_OpenAI_Key());
                    }
                    if(list_Comment==null){
                        continue;
                    }else {
                        comments = list_Comment.split("\n");
                    }
                    for (int j = 0; j < comments.length; j++) {
                        if (comments[j].length() == 0) {
                            continue;
                        }
                        DataComment dataComment = new DataComment();
                        dataComment.setOrderid(videoComments.get(i).getOrderid());
                        dataComment.setComment(comments[j]);
                        dataComment.setUsername("");
                        dataComment.setRunning(0);
                        dataComment.setTimeget(0L);
                        dataComment.setVps("");
                        dataCommentRepository.save(dataComment);
                    }
                    videoComments.get(i).setComment_render(dataCommentRepository.count_All_By_OrderId(videoComments.get(i).getOrderid()));
                    if(videoComments.get(i).getMaxthreads()<=0){
                        if(service.getExpired()==0){
                            int max_thread = service.getThread() + ((int)(videoComments.get(i).getCommentorder() / 100)<1?0:(int)(videoComments.get(i).getCommentorder() / 100));
                            if (max_thread <= 50) {
                                videoComments.get(i).setMaxthreads(max_thread);
                            } else {
                                videoComments.get(i).setMaxthreads(50);
                            }
                        }else{
                            videoComments.get(i).setMaxthreads(service.getThread());
                        }
                    }
                    videoCommentRepository.save(videoComments.get(i));
                }else if(service.getAi()==2){
                    String[] comments;
                    if(videoComments.get(i).getListcomment().length()==0){
                        Integer count_render=videoComments.get(i).getCommentorder()-videoComments.get(i).getComment_render();
                        count_render=count_render>=100?100:count_render;
                        String uuid=Openai.createTask("https://www.youtube.com/watch?v="+ videoComments.get(i).getVideoid(),count_render,"youtube","comment",0,videoComments.get(i).getVideotitle(),videoComments.get(i).getChanneltitle(),videoComments.get(i).getDescription());
                        if(uuid!=null){
                            videoComments.get(i).setListcomment(uuid);
                            videoCommentRepository.save( videoComments.get(i));
                        }
                        continue;
                    }
                    String status=Openai.statusTask(videoComments.get(i).getListcomment());
                    if(status!=null&&status.equals("completed")) {

                        String list_Comment = Openai.getTask(videoComments.get(i).getListcomment());
                        if (list_Comment == null) {
                            continue;
                        } else {
                            comments = list_Comment.split("\\R");
                        }
                        videoComments.get(i).setListcomment("");
                    }else  if(status!=null&&status.equals("failed"))  {
                        Integer count_render=videoComments.get(i).getCommentorder()-videoComments.get(i).getComment_render();
                        count_render=count_render>=100?100:count_render;
                        String uuid=Openai.createTask("https://www.youtube.com/watch?v="+ videoComments.get(i).getVideoid(),count_render,"youtube","comment",0,videoComments.get(i).getVideotitle(),videoComments.get(i).getChanneltitle(),videoComments.get(i).getDescription());
                        if(uuid!=null){
                            videoComments.get(i).setListcomment(uuid);
                            videoCommentRepository.save( videoComments.get(i));
                        }
                        continue;
                    }else {
                        continue;
                    }
                    for (int j = 0; j < comments.length; j++) {
                        if (comments[j].trim().length() == 0) {
                            continue;
                        }
                        DataComment dataComment = new DataComment();
                        dataComment.setOrderid(videoComments.get(i).getOrderid());
                        dataComment.setComment(comments[j]);
                        dataComment.setUsername("");
                        dataComment.setRunning(0);
                        dataComment.setTimeget(0L);
                        dataComment.setVps("");
                        dataCommentRepository.save(dataComment);
                    }
                    videoComments.get(i).setComment_render(dataCommentRepository.count_All_By_OrderId(videoComments.get(i).getOrderid()));
                    if(videoComments.get(i).getComment_render()< videoComments.get(i).getCommentorder()){
                        Integer count_render=videoComments.get(i).getCommentorder()-videoComments.get(i).getComment_render();
                        count_render=count_render>=100?100:count_render;
                        String uuid=Openai.createTask("https://www.youtube.com/watch?v="+ videoComments.get(i).getVideoid(),count_render,"youtube","comment",0,videoComments.get(i).getVideotitle(),videoComments.get(i).getChanneltitle(),videoComments.get(i).getDescription());
                        if(uuid!=null){
                            videoComments.get(i).setListcomment(uuid);
                        }
                    }
                    if(videoComments.get(i).getMaxthreads()<=0){
                        if(service.getExpired()==0){
                            int max_thread = service.getThread() + ((int)(videoComments.get(i).getCommentorder() / 100)<1?0:(int)(videoComments.get(i).getCommentorder() / 100));
                            if (max_thread <= 50) {
                                videoComments.get(i).setMaxthreads(max_thread);
                            } else {
                                videoComments.get(i).setMaxthreads(50);
                            }
                        }else{
                            videoComments.get(i).setMaxthreads(service.getThread());
                        }
                    }
                    videoCommentRepository.save(videoComments.get(i));
                }

            }
            resp.put("status", "true");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(path = "updateStateChatLilveAI", produces = "application/hal+json;charset=utf8")
    public ResponseEntity<String> updateStateChatLilveAI() {
        JSONObject resp = new JSONObject();
        //Integer checktoken= adminRepository.FindAdminByToken(Authorization.split(",")[0]);
        try {
            //historyRepository.updateHistoryByAccount();
            List<VideoComment> videoComments = videoCommentRepository.getOrderLiveAIThreadNull();
            for (int i = 0; i < videoComments.size(); i++) {
                Service service = serviceRepository.getServiceNoCheckEnabled(videoComments.get(i).getService());
                String[] comments;
                if(videoComments.get(i).getListcomment().length()==0){
                    String uuid=Openai.createChatTask("https://www.youtube.com/watch?v="+ videoComments.get(i).getVideoid());
                    if(uuid!=null){
                        videoComments.get(i).setListcomment(uuid);
                        videoCommentRepository.save( videoComments.get(i));
                    }
                    continue;
                }
                if((System.currentTimeMillis()-videoComments.get(i).getChat_time())/1000/60>service.getMintime()&&videoComments.get(i).getChat_id().length()==0){
                    String uuid=Openai.createChat(videoComments.get(i).getListcomment(),service.getMax_render());
                    if(uuid!=null){
                        videoComments.get(i).setChat_id(uuid);
                        videoComments.get(i).setChat_time(System.currentTimeMillis());
                        videoCommentRepository.save( videoComments.get(i));
                    }
                    continue;
                }

                String[] data=Openai.getChatTask(videoComments.get(i).getChat_id());
                if(data!=null&&data[0].equals("completed")) {

                    String list_Comment =data[1];
                    if (list_Comment == null) {
                        continue;
                    } else {
                        comments = list_Comment.split("\\R");
                    }
                    videoComments.get(i).setChat_id("");
                    videoCommentRepository.save( videoComments.get(i));

                }else if(data!=null&&data[0].equals("failed")) {
                    videoComments.get(i).setChat_id("");
                    videoComments.get(i).setChat_time(0L);
                    videoCommentRepository.save( videoComments.get(i));
                    continue;
                }else {
                    continue;
                }
                dataCommentRepository.updateCommentByOrderId(videoComments.get(i).getOrderid());
                for (int j = 0; j < comments.length; j++) {
                    if (comments[j].trim().length() == 0) {
                        continue;
                    }
                    DataComment dataComment = new DataComment();
                    dataComment.setOrderid(videoComments.get(i).getOrderid());
                    dataComment.setComment(comments[j]);
                    dataComment.setUsername("");
                    dataComment.setRunning(0);
                    dataComment.setTimeget(0L);
                    dataComment.setVps("");
                    dataCommentRepository.save(dataComment);
                }
                if(videoComments.get(i).getMaxthreads()<=0){
                    if(service.getExpired()==0&&service.getLive()==0){
                        int max_thread = service.getThread() + ((int)(videoComments.get(i).getCommentorder() / 100)<1?0:(int)(videoComments.get(i).getCommentorder() / 100));
                        if (max_thread <= 50) {
                            videoComments.get(i).setMaxthreads(max_thread);
                        } else {
                            videoComments.get(i).setMaxthreads(50);
                        }
                    }else{
                        videoComments.get(i).setMaxthreads(service.getThread());
                    }
                }
                videoCommentRepository.save(videoComments.get(i));
            }
            resp.put("status", "true");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "updateStateReply", produces = "application/hal+json;charset=utf8")
    public ResponseEntity<String> updateStateReply() {
        JSONObject resp = new JSONObject();
        //Integer checktoken= adminRepository.FindAdminByToken(Authorization.split(",")[0]);
        try {
            //historyRepository.updateHistoryByAccount();
            List<VideoComment> videoComments = videoCommentRepository.getOrderReplyThreadNull();
            Setting setting = settingRepository.getSettingId1();
            for (int i = 0; i < videoComments.size(); i++) {
                Service service = serviceRepository.getServiceNoCheckEnabled(videoComments.get(i).getService());
                String[] comments;
                if(service.getAi()==1){
                    Integer count_render=videoComments.get(i).getCommentorder()-videoComments.get(i).getComment_render();
                    count_render=count_render>=100?100:count_render;
                    String prompt=videoComments.get(i).getListcomment().replace("#cmcmedia@$123",count_render.toString());
                    String list_Comment=null;
                    if(service.getExpired()==0){
                        list_Comment= Openai.chatGPT(prompt,openAiKeyRepository.get_OpenAI_Key());
                    }else{
                        list_Comment= Openai.chatGPT4oMini(prompt,openAiKeyRepository.get_OpenAI_Key());
                    }
                    if(list_Comment==null){
                        continue;
                    }else {
                        comments = list_Comment.split("\n");
                    }
                }else{
                    comments = videoComments.get(i).getListcomment().split("\n");
                }
                List<String> arrCmt = new ArrayList<>();
                if(service.getReply()==1){
                    for (int j = 0; j < comments.length; j++) {
                        if (comments[j].length() == 0) {
                            continue;
                        }
                        int check_done=0;
                        if(comments[j].indexOf("|")>0){
                            String[] cmt_reply=comments[j].split("\\|");
                            if(!arrCmt.contains(cmt_reply[0].trim())&&cmt_reply[0].trim().length()>0){
                                arrCmt.add(cmt_reply[0].trim());
                                DataComment dataComment = new DataComment();
                                dataComment.setOrderid(videoComments.get(i).getOrderid());
                                dataComment.setComment(cmt_reply[0].trim());
                                dataComment.setUsername("");
                                dataComment.setRunning(0);
                                dataComment.setTimeget(0L);
                                dataComment.setVps("");
                                dataCommentRepository.save(dataComment);
                            }else{
                                check_done=1;
                            }
                            if(cmt_reply.length>1){
                                DataReplyComment dataReplyComment=new DataReplyComment();
                                dataReplyComment.setComment_id(dataCommentRepository.getByCommentId(videoComments.get(i).getOrderid(),cmt_reply[0].trim()));
                                dataReplyComment.setOrderid(videoComments.get(i).getOrderid());
                                dataReplyComment.setReply(cmt_reply[1].trim());
                                dataReplyComment.setRunning(-1);
                                dataReplyComment.setCheck_done(check_done);
                                dataReplyComment.setTimeget(0L);
                                dataReplyComment.setUsername("");
                                dataReplyComment.setVps("");
                                dataReplyCommentRepository.save(dataReplyComment);
                            }
                        }else{
                            if(!arrCmt.equals(comments[j].trim())) {

                                arrCmt.add(comments[j].trim());

                                DataComment dataComment = new DataComment();
                                dataComment.setOrderid(videoComments.get(i).getOrderid());
                                dataComment.setComment(comments[j].trim());
                                dataComment.setUsername("");
                                dataComment.setRunning(0);
                                dataComment.setTimeget(0L);
                                dataComment.setVps("");
                                dataCommentRepository.save(dataComment);
                            }
                        }
                    }
                }else{
                    for (int j = 0; j < comments.length; j++) {
                        if (comments[j].length() == 0) {
                            continue;
                        }
                        DataReplyComment dataReplyComment=new DataReplyComment();
                        dataReplyComment.setComment_id(-1L);
                        dataReplyComment.setOrderid(videoComments.get(i).getOrderid());
                        dataReplyComment.setReply(comments[j].trim());
                        dataReplyComment.setRunning(0);
                        dataReplyComment.setCheck_done(1);
                        dataReplyComment.setTimeget(0L);
                        dataReplyComment.setUsername("");
                        dataReplyComment.setLink(videoComments.get(i).getLc_code());
                        dataReplyComment.setVps("");
                        dataReplyCommentRepository.save(dataReplyComment);
                    }
                }
                if(service.getReply()==2){
                    videoComments.get(i).setComment_render(dataReplyCommentRepository.count_All_By_OrderId(videoComments.get(i).getOrderid()));
                }else{
                    videoComments.get(i).setComment_render(dataCommentRepository.count_All_By_OrderId(videoComments.get(i).getOrderid()));
                }
                if(service.getExpired()==0){
                    int max_thread = service.getThread() + ((int)(videoComments.get(i).getCommentorder() / 100)<1?0:(int)(videoComments.get(i).getCommentorder() / 100));
                    if (max_thread <= 50) {
                        videoComments.get(i).setMaxthreads(max_thread);
                    } else {
                        videoComments.get(i).setMaxthreads(50);
                    }
                }else{
                    videoComments.get(i).setMaxthreads(service.getThread());
                }
                videoCommentRepository.save(videoComments.get(i));
            }
            resp.put("status", "true");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            StackTraceElement stackTraceElement = Arrays.stream(e.getStackTrace()).filter(ste -> ste.getClassName().equals(this.getClass().getName())).collect(Collectors.toList()).get(0);
            System.out.println(stackTraceElement.getMethodName());
            System.out.println(stackTraceElement.getLineNumber());
            System.out.println(stackTraceElement.getClassName());
            System.out.println(stackTraceElement.getFileName());
            System.out.println("Error : " + e.getMessage());
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(path = "updateStateReplyAI", produces = "application/hal+json;charset=utf8")
    public ResponseEntity<String> updateStateReplyAI() {
        JSONObject resp = new JSONObject();
        //Integer checktoken= adminRepository.FindAdminByToken(Authorization.split(",")[0]);
        try {
            //historyRepository.updateHistoryByAccount();
            List<VideoComment> videoComments = videoCommentRepository.getOrderReplyAIThreadNull();
            for (int i = 0; i < videoComments.size(); i++) {
                Service service = serviceRepository.getServiceNoCheckEnabled(videoComments.get(i).getService());
                String[] comments;
                if(service.getAi()==1){
                    Integer count_render=videoComments.get(i).getCommentorder()-videoComments.get(i).getComment_render();
                    count_render=count_render>=100?100:count_render;
                    String prompt=videoComments.get(i).getListcomment().replace("#cmcmedia@$123",count_render.toString());
                    String list_Comment=null;
                    if(service.getExpired()==0){
                        list_Comment= Openai.chatGPT(prompt,openAiKeyRepository.get_OpenAI_Key());
                    }else{
                        list_Comment= Openai.chatGPT4oMini(prompt,openAiKeyRepository.get_OpenAI_Key());
                    }
                    if(list_Comment==null){
                        continue;
                    }else {
                        comments = list_Comment.split("\n");
                    }
                }else{
                    comments = videoComments.get(i).getListcomment().split("\n");
                }
                List<String> arrCmt = new ArrayList<>();
                if(service.getReply()==1){
                    for (int j = 0; j < comments.length; j++) {
                        if (comments[j].length() == 0) {
                            continue;
                        }
                        int check_done=0;
                        if(comments[j].indexOf("|")>0){
                            String[] cmt_reply=comments[j].split("\\|");
                            if(!arrCmt.contains(cmt_reply[0].trim())&&cmt_reply[0].trim().length()>0){
                                arrCmt.add(cmt_reply[0].trim());
                                DataComment dataComment = new DataComment();
                                dataComment.setOrderid(videoComments.get(i).getOrderid());
                                dataComment.setComment(cmt_reply[0].trim());
                                dataComment.setUsername("");
                                dataComment.setRunning(0);
                                dataComment.setTimeget(0L);
                                dataComment.setVps("");
                                dataCommentRepository.save(dataComment);
                            }else{
                                check_done=1;
                            }
                            if(cmt_reply.length>1){
                                DataReplyComment dataReplyComment=new DataReplyComment();
                                dataReplyComment.setComment_id(dataCommentRepository.getByCommentId(videoComments.get(i).getOrderid(),cmt_reply[0].trim()));
                                dataReplyComment.setOrderid(videoComments.get(i).getOrderid());
                                dataReplyComment.setReply(cmt_reply[1].trim());
                                dataReplyComment.setRunning(-1);
                                dataReplyComment.setCheck_done(check_done);
                                dataReplyComment.setTimeget(0L);
                                dataReplyComment.setUsername("");
                                dataReplyComment.setVps("");
                                dataReplyCommentRepository.save(dataReplyComment);
                            }
                        }else{
                            if(!arrCmt.equals(comments[j].trim())) {

                                arrCmt.add(comments[j].trim());

                                DataComment dataComment = new DataComment();
                                dataComment.setOrderid(videoComments.get(i).getOrderid());
                                dataComment.setComment(comments[j].trim());
                                dataComment.setUsername("");
                                dataComment.setRunning(0);
                                dataComment.setTimeget(0L);
                                dataComment.setVps("");
                                dataCommentRepository.save(dataComment);
                            }
                        }
                    }
                }else{
                    for (int j = 0; j < comments.length; j++) {
                        if (comments[j].length() == 0) {
                            continue;
                        }
                        DataReplyComment dataReplyComment=new DataReplyComment();
                        dataReplyComment.setComment_id(-1L);
                        dataReplyComment.setOrderid(videoComments.get(i).getOrderid());
                        dataReplyComment.setReply(comments[j].trim());
                        dataReplyComment.setRunning(0);
                        dataReplyComment.setCheck_done(1);
                        dataReplyComment.setTimeget(0L);
                        dataReplyComment.setUsername("");
                        dataReplyComment.setLink(videoComments.get(i).getLc_code());
                        dataReplyComment.setVps("");
                        dataReplyCommentRepository.save(dataReplyComment);
                    }
                }
                if(service.getReply()==2){
                    videoComments.get(i).setComment_render(dataReplyCommentRepository.count_All_By_OrderId(videoComments.get(i).getOrderid()));
                }else{
                    videoComments.get(i).setComment_render(dataCommentRepository.count_All_By_OrderId(videoComments.get(i).getOrderid()));
                }
                if(videoComments.get(i).getMaxthreads()<=0){
                    if(service.getExpired()==0){
                        int max_thread = service.getThread() + ((int)(videoComments.get(i).getCommentorder() / 100)<1?0:(int)(videoComments.get(i).getCommentorder() / 100));
                        if (max_thread <= 50) {
                            videoComments.get(i).setMaxthreads(max_thread);
                        } else {
                            videoComments.get(i).setMaxthreads(50);
                        }
                    }else{
                        videoComments.get(i).setMaxthreads(service.getThread());
                    }
                }
                videoCommentRepository.save(videoComments.get(i));
            }
            resp.put("status", "true");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            StackTraceElement stackTraceElement = Arrays.stream(e.getStackTrace()).filter(ste -> ste.getClassName().equals(this.getClass().getName())).collect(Collectors.toList()).get(0);
            System.out.println(stackTraceElement.getMethodName());
            System.out.println(stackTraceElement.getLineNumber());
            System.out.println(stackTraceElement.getClassName());
            System.out.println(stackTraceElement.getFileName());
            System.out.println("Error : " + e.getMessage());
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(path = "updateordercheck", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> updateordercheck(@RequestParam(defaultValue = "") String videoid) {
        JSONObject resp = new JSONObject();
        try {
            String[] videoidArr = videoid.split(",");
            for (int i = 0; i < videoidArr.length; i++) {
                videoViewRepository.updateOrderCheck(videoidArr[i]);
            }
            resp.put("videoview", "");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "updatecheckcancel", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> updatecheckcancel(@RequestParam(defaultValue = "") String videoid) {
        JSONObject resp = new JSONObject();
        try {
            videoCommentRepository.updateCheckCancel(videoid.trim());
            resp.put("status", "true"+videoid);
            resp.put("message", "update trạng thái đơn thành công!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "DeleteOrderNotValidCron", produces = "application/hal+json;charset=utf8")
    public ResponseEntity<String> DeleteOrderNotValidCron() {
        JSONObject resp = new JSONObject();
        try {
            List<OrderCommentRunning> orderRunnings = videoCommentRepository.getOrderCancelThan2h();
            for (int i=0;i<orderRunnings.size();i++){
                delete("1",orderRunnings.get(i).getVideoId(),1);
            }
            List<VideoComment> videoComments=videoCommentRepository.getAllOrderCheckCancel();
            for(int i=0;i<videoComments.size();i++){

                OkHttpClient client1 = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();

                Request request1 = null;
                List<GoogleAPIKey> keys = googleAPIKeyRepository.getAllByState();
                request1 = new Request.Builder().url("https://www.googleapis.com/youtube/v3/videos?key=" + keys.get(0).getKey().trim() + "&fields=items(id,contentDetails(regionRestriction(blocked)))&part=id,contentDetails&id=" + videoComments.get(i).getVideoid().trim()).get().build();
                keys.get(0).setCount(keys.get(0).getCount() + 1L);
                googleAPIKeyRepository.save(keys.get(0));

                Response response1 = client1.newCall(request1).execute();

                String resultJson1 = response1.body().string();

                Object obj1 = new JSONParser().parse(resultJson1);

                JSONObject jsonObject1 = (JSONObject) obj1;
                JSONArray items = (JSONArray) jsonObject1.get("items");
                if (items == null) {
                    continue;
                }
                //System.out.println(items);
                Iterator k = items.iterator();
                if (k.hasNext() == false) {
                    delete("1",videoComments.get(i).getVideoid().trim(),1);
                    continue;
                }else {
                    while (k.hasNext()) {
                        try {
                            JSONObject video = (JSONObject) k.next();
                            JSONObject contentDetails = (JSONObject) video.get("contentDetails");
                            JSONObject regionRestriction = (JSONObject) contentDetails.get("regionRestriction");
                            if (regionRestriction != null) {
                                if (regionRestriction.get("blocked").toString().indexOf("VN") > 0 && videoCommentRepository.getServiceByVideoId(videoComments.get(i).getVideoid().trim(), "vn") > 0) {
                                    delete("1", videoComments.get(i).getVideoid().trim(), 1);
                                } else if (regionRestriction.get("blocked").toString().indexOf("US") > 0 && videoCommentRepository.getServiceByVideoId(videoComments.get(i).getVideoid().trim(), "us") > 0) {
                                    delete("1", videoComments.get(i).getVideoid().trim(), 1);
                                }else if (regionRestriction.get("blocked").toString().indexOf("KR") > 0 && videoCommentRepository.getServiceByVideoId(videoComments.get(i).getVideoid().trim(), "kr") > 0) {
                                    delete("1", videoComments.get(i).getVideoid().trim(), 1);
                                }else if (regionRestriction.get("blocked").toString().indexOf("JP") > 0 && videoCommentRepository.getServiceByVideoId(videoComments.get(i).getVideoid().trim(), "jp") > 0) {
                                    delete("1", videoComments.get(i).getVideoid().trim(), 1);
                                } else {
                                    videoCommentRepository.updateOrderCheck(videoComments.get(i).getVideoid().trim());
                                }
                            } else {
                                videoCommentRepository.updateOrderCheck(videoComments.get(i).getVideoid().trim());
                            }
                        } catch (Exception e) {
                            break;
                        }
                    }
                }
            }
            resp.put("status", true);
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(path = "refund", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> refund(@RequestParam(defaultValue = "") String orderid) {
        JSONObject resp = new JSONObject();
        if(orderid.length()==0){
            resp.put("status", "fail");
            resp.put("message", "OrderId không được trống!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }
        List<String> ordersArrInput = new ArrayList<>();
        ordersArrInput.addAll(Arrays.asList(orderid.split(",")));
        try {
            videoCommentHistoryRepository.updateRefund(ordersArrInput);
            resp.put("status", "true");
            resp.put("message", "Refund đơn thành công!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping(path = "update", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> update(@RequestHeader(defaultValue = "") String Authorization, @RequestBody VideoComment videoBuffh) {
        JSONObject resp = new JSONObject();
        //Integer checktoken= adminRepository.FindAdminByToken(Authorization.split(",")[0]);
        List<Admin> admins = adminRepository.FindByToken(Authorization.trim());
        if (Authorization.length() == 0 || admins.size() == 0) {
            resp.put("status", "fail");
            resp.put("message", "Token expired");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        try {
            String[] videoidIdArr = videoBuffh.getVideoid().split("\n");
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < videoidIdArr.length; i++) {
                List<VideoComment> video = videoCommentRepository.getVideoBuffhById(videoidIdArr[i].trim());
                Service service = serviceRepository.getService(video.get(0).getService());
                float priceorder = 0;
                if (videoBuffh.getCommentorder() != video.get(0).getCommentorder()) {
                    List<Admin> user = adminRepository.getAdminByUser(videoBuffh.getUser());
                    priceorder = ((videoBuffh.getCommentorder() - video.get(0).getCommentorder())) * (video.get(0).getPrice() / video.get(0).getCommentorder());

                    if (priceorder > (float) user.get(0).getBalance()) {
                        resp.put("message", "Số tiền không đủ!!");
                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                    }
                    int timethan = videoBuffh.getCommentorder() - video.get(0).getCommentorder();

                    //
                    if (timethan != 0) {
                        Float balance_update=adminRepository.updateBalanceFine(-priceorder,videoBuffh.getUser());
                        Balance balance = new Balance();
                        balance.setUser(videoBuffh.getUser());
                        balance.setTime(System.currentTimeMillis());
                        balance.setTotalblance(balance_update);
                        balance.setBalance(-priceorder);
                        balance.setService(videoBuffh.getService());
                        if (priceorder < 0) {
                            balance.setNote("Refund " + (-timethan) + " view cho " + videoBuffh.getVideoid());
                        } else if (timethan != 0) {
                            balance.setNote("Order thêm " + timethan + " view cho " + videoBuffh.getVideoid());
                        }

                        balanceRepository.save(balance);
                    }
                }
                video.get(0).setMaxthreads(videoBuffh.getMaxthreads());
                video.get(0).setCommentorder(videoBuffh.getCommentorder());
                video.get(0).setNote(videoBuffh.getNote());
                video.get(0).setPrice(videoBuffh.getPrice() + priceorder);
                videoCommentRepository.save(video.get(0));

                List<OrderCommentRunning> orderRunnings = videoCommentRepository.getVideoViewById(videoidIdArr[i].trim());
                JSONObject obj = new JSONObject();
                obj.put("orderid", orderRunnings.get(0).getOrderId());
                obj.put("videoid", orderRunnings.get(0).getVideoId());
                obj.put("videotitle", orderRunnings.get(0).getVideoTitle());
                obj.put("viewstart", orderRunnings.get(0).getCommentStart());
                obj.put("maxthreads", orderRunnings.get(0).getMaxthreads());
                obj.put("insertdate", orderRunnings.get(0).getInsertDate());
                obj.put("total", orderRunnings.get(0).getTotal());
                obj.put("note", orderRunnings.get(0).getNote());
                obj.put("duration", orderRunnings.get(0).getDuration());
                obj.put("commentorder", orderRunnings.get(0).getCommentOrder());
                obj.put("service", orderRunnings.get(0).getService());
                obj.put("user", orderRunnings.get(0).getUser());
                obj.put("geo", service.getGeo());
                obj.put("commenttotal", orderRunnings.get(0).getCommentTotal());
                obj.put("price", videoBuffh.getPrice() + priceorder);

                jsonArray.add(obj);
            }
            resp.put("videocomment", jsonArray);
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }
    String refundCMTByVideoComment(@RequestBody() VideoCommentHistory videoCommentHistory) {

        try {
            Service service = serviceRepository.getInfoService(videoCommentHistory.getService());
            JSONObject obj = new JSONObject();

            OkHttpClient client1 = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
            List<GoogleAPIKey> keys = googleAPIKeyRepository.getAllByState();
            Request request1 = null;
            request1 = new Request.Builder().url("https://www.googleapis.com/youtube/v3/videos?key=" + keys.get(0).getKey().trim() + "&fields=items(statistics(commentCount))&part=statistics&id=" + videoCommentHistory.getVideoid().trim()).get().build();
            keys.get(0).setCount(keys.get(0).getCount() + 1L);
            googleAPIKeyRepository.save(keys.get(0));
            Response response1 = client1.newCall(request1).execute();

            String resultJson1 = response1.body().string();

            Object obj1 = new JSONParser().parse(resultJson1);

            JSONObject jsonObject1 = (JSONObject) obj1;
            JSONArray items = (JSONArray) jsonObject1.get("items");
            if (items == null) {
                videoCommentHistory.setTimecheck(System.currentTimeMillis());
                videoCommentHistoryRepository.save(videoCommentHistory);
                return "Không check được cmt";
            }
            Iterator k = items.iterator();
            if (k.hasNext() == false) {
                videoCommentHistory.setTimecheck(System.currentTimeMillis());
                videoCommentHistoryRepository.save(videoCommentHistory);
                return "Không check được cmt";
            }
            while (k.hasNext()) {
                try {
                    JSONObject video = (JSONObject) k.next();
                    JSONObject statistics = (JSONObject) video.get("statistics");
                    List<Admin> user = adminRepository.getAdminByUser(videoCommentHistory.getUser());
                    //Hoàn tiền những view chưa buff
                    int cmtCount = Integer.parseInt(statistics.get("commentCount").toString());
                    int cmtFix = videoCommentHistory.getCommentorder() > videoCommentHistory.getCommenttotal() ? videoCommentHistory.getCommenttotal() : videoCommentHistory.getCommentorder();
                    int cmtThan = cmtFix + videoCommentHistory.getCommentstart() - cmtCount;
                    if(cmtThan<=0){
                        if(service.getChecktime()==0){
                            videoCommentHistory.setCommentend(cmtCount);
                            videoCommentHistory.setTimecheck(System.currentTimeMillis());
                        }
                        videoCommentHistoryRepository.save(videoCommentHistory);
                        return "Đủ cmt | " +cmtCount+"/"+(cmtFix+videoCommentHistory.getCommentstart());
                    }
                    if(cmtThan>cmtFix){
                        cmtThan=cmtFix;
                    }

                    float price_refund = ((cmtThan) / (float) cmtFix) * videoCommentHistory.getPrice();
                    //float pricebuffed=(videoBuffh.get(0).getViewtotal()/1000F)*service.getRate()*((float)(100-admins.get(0).getDiscount())/100);
                    if (videoCommentHistory.getPrice() < price_refund) {
                        price_refund = videoCommentHistory.getPrice();
                    }
                    float pricebuffed = (videoCommentHistory.getPrice() - price_refund);
                    videoCommentHistory.setPrice(pricebuffed);
                    videoCommentHistory.setCommentend(cmtCount);
                    videoCommentHistory.setTimecheck(System.currentTimeMillis());
                    videoCommentHistory.setCommenttotal(cmtFix - cmtThan);
                    videoCommentHistory.setNumbh(1);
                    if (videoCommentHistory.getCommenttotal()==0) {
                        videoCommentHistory.setCancel(1);
                    } else {
                        videoCommentHistory.setCancel(2);
                    }
                    videoCommentHistoryRepository.save(videoCommentHistory);
                    //hoàn tiền & add thong báo số dư
                    Float balance_update=adminRepository.updateBalanceFine(price_refund,videoCommentHistory.getUser().trim());
                    Balance balance = new Balance();
                    balance.setUser(user.get(0).getUsername().trim());
                    balance.setTime(System.currentTimeMillis());
                    balance.setTotalblance(balance_update);
                    balance.setBalance(price_refund);
                    balance.setService(videoCommentHistory.getService());
                    balance.setNote("Refund " + (cmtThan) + " cmt cho " + videoCommentHistory.getVideoid());
                    balanceRepository.save(balance);

                    if(videoCommentHistory.getPrice()==0){
                        return "Đã hoàn 100%";
                    }else{
                        return "Đã hoàn phần thiếu";
                    }
                } catch (Exception e) {
                    return "Fail";
                }
            }
            return "Fail";
        } catch (Exception e) {
            return "Fail";
        }
    }


    String refundCMTByVideoComment100(@RequestBody() VideoCommentHistory videoCommentHistory) {

        try {
            Service service = serviceRepository.getInfoService(videoCommentHistory.getService());
            List<Admin> user = adminRepository.getAdminByUser(videoCommentHistory.getUser());
            float price_refund = videoCommentHistory.getPrice();
            Integer cmtThan=videoCommentHistory.getCommenttotal();
            videoCommentHistory.setPrice(0F);
            videoCommentHistory.setTimecheck(System.currentTimeMillis());
            videoCommentHistory.setCommenttotal(0);
            videoCommentHistory.setCancel(1);
            videoCommentHistoryRepository.save(videoCommentHistory);
            //hoàn tiền & add thong báo số dư
            Float balance_update=adminRepository.updateBalanceFine(price_refund,videoCommentHistory.getUser().trim());
            Balance balance = new Balance();
            balance.setUser(user.get(0).getUsername().trim());
            balance.setTime(System.currentTimeMillis());
            balance.setTotalblance(balance_update);
            balance.setBalance(price_refund);
            balance.setService(videoCommentHistory.getService());
            balance.setNote("Refund " + (cmtThan) + " cmt cho " + videoCommentHistory.getVideoid());
            balanceRepository.save(balance);
            return "Đã hoàn 100%";

        } catch (Exception e) {
            return "Fail";
        }
    }

    @GetMapping(path = "updateRefundHis", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> updateRefundHis(@RequestHeader(defaultValue = "") String Authorization,@RequestParam(defaultValue = "") String orderid) {
        JSONObject resp = new JSONObject();
        //Integer checktoken= adminRepository.FindAdminByToken(Authorization.split(",")[0]);
        List<Admin> admins = adminRepository.FindByToken(Authorization.trim());
        if (Authorization.length() == 0 || admins.size() == 0) {
            resp.put("status", "fail");
            resp.put("message", "Token expired");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        try {
            String[] videoidIdArr = orderid.split(",");
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < videoidIdArr.length; i++) {
                String status="No refunds";
                VideoCommentHistory video = videoCommentHistoryRepository.getVideoViewHisById(Long.parseLong(videoidIdArr[i].trim()));
                Float price_old=video.getPrice();
                Service service = serviceRepository.getInfoService(video.getService());
                VideoCommentHistory video_refil=video;
                if(service.getRefill()==0){
                    status="DV không bảo hành";
                }else if(video.getUser().equals("baohanh01@gmail.com")){
                    status="Đơn bảo hành";
                } else if(videoCommentRepository.getCountVideoIdNotPending(video.getVideoid())>0){
                    status="Đơn mới đang chạy";
                }else if(video.getCancel()==1){
                    status="Được hủy trước đó";
                }else if(serviceRepository.checkGuarantee(video.getEnddate(),service.getMaxtimerefill())==0){
                    status="Quá hạn "+service.getMaxtimerefill()+" ngày";
                }else{
                    status=refundCMTByVideoComment(video);
                    video_refil= videoCommentHistoryRepository.getVideoViewHisById(Long.parseLong(videoidIdArr[i].trim()));
                }

                JSONObject obj = new JSONObject();
                obj.put("orderid", video_refil.getOrderid());
                obj.put("videoid", video_refil.getVideoid());
                obj.put("videotitle", video_refil.getVideotitle());
                obj.put("commentstart",video_refil.getCommentstart());
                obj.put("maxthreads", video_refil.getMaxthreads());
                obj.put("insertdate", video_refil.getInsertdate());
                obj.put("user", video_refil.getUser());
                obj.put("note", video_refil.getNote());
                obj.put("duration", video_refil.getDuration());
                obj.put("enddate", video_refil.getEnddate());
                obj.put("cancel", video_refil.getCancel());
                //obj.put("home_rate", orderRunnings.get(i).get());
                obj.put("commentend", video_refil.getCommentend());
                obj.put("commenttotal", video_refil.getCommenttotal());
                obj.put("commentorder", video_refil.getCommentorder());
                obj.put("price", video_refil.getPrice());
                obj.put("service", video_refil.getService());
                obj.put("status", status);

                jsonArray.add(obj);
            }
            resp.put("videocomment", jsonArray);
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "updateRefund100His", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> updateRefund100His(@RequestHeader(defaultValue = "") String Authorization,@RequestParam(defaultValue = "") String orderid) {
        JSONObject resp = new JSONObject();
        //Integer checktoken= adminRepository.FindAdminByToken(Authorization.split(",")[0]);
        List<Admin> admins = adminRepository.FindByToken(Authorization.trim());
        if (Authorization.length() == 0 || admins.size() == 0) {
            resp.put("status", "fail");
            resp.put("message", "Token expired");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        try {
            String[] videoidIdArr = orderid.split(",");
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < videoidIdArr.length; i++) {
                String status="No refunds";
                VideoCommentHistory video = videoCommentHistoryRepository.getVideoViewHisById(Long.parseLong(videoidIdArr[i].trim()));
                Float price_old=video.getPrice();
                Service service = serviceRepository.getInfoService(video.getService());
                VideoCommentHistory video_refil=video;
                if(service.getRefill()==0){
                    status="DV không bảo hành";
                }else if(video.getUser().equals("baohanh01@gmail.com")){
                    status="Đơn bảo hành";
                } else if(videoCommentRepository.getCountVideoIdNotPending(video.getVideoid())>0){
                    status="Đơn mới đang chạy";
                }else if(video.getCancel()==1){
                    status="Được hủy trước đó";
                }else if(serviceRepository.checkGuarantee(video.getEnddate(),service.getMaxtimerefill())==0){
                    status="Quá hạn "+service.getMaxtimerefill()+" ngày";
                }else{
                    status=refundCMTByVideoComment100(video);
                    video_refil= videoCommentHistoryRepository.getVideoViewHisById(Long.parseLong(videoidIdArr[i].trim()));
                }

                JSONObject obj = new JSONObject();
                obj.put("orderid", video_refil.getOrderid());
                obj.put("videoid", video_refil.getVideoid());
                obj.put("videotitle", video_refil.getVideotitle());
                obj.put("commentstart",video_refil.getCommentstart());
                obj.put("maxthreads", video_refil.getMaxthreads());
                obj.put("insertdate", video_refil.getInsertdate());
                obj.put("user", video_refil.getUser());
                obj.put("note", video_refil.getNote());
                obj.put("duration", video_refil.getDuration());
                obj.put("enddate", video_refil.getEnddate());
                obj.put("cancel", video_refil.getCancel());
                //obj.put("home_rate", orderRunnings.get(i).get());
                obj.put("commentend", video_refil.getCommentend());
                obj.put("commenttotal", video_refil.getCommenttotal());
                obj.put("commentorder", video_refil.getCommentorder());
                obj.put("price", video_refil.getPrice());
                obj.put("service", video_refil.getService());
                obj.put("status", status);

                jsonArray.add(obj);
            }
            resp.put("videocomment", jsonArray);
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "updatethread", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> updatethread(@RequestHeader(defaultValue = "") String Authorization, @RequestBody VideoComment videoBuffh) {
        JSONObject resp = new JSONObject();
        //Integer checktoken= adminRepository.FindAdminByToken(Authorization.split(",")[0]);
        List<Admin> admins = adminRepository.FindByToken(Authorization.trim());
        if (Authorization.length() == 0 || admins.size() == 0) {
            resp.put("status", "fail");
            resp.put("message", "Token expired");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        try {
            String[] videoidIdArr = videoBuffh.getVideoid().split("\n");
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < videoidIdArr.length; i++) {
                List<VideoComment> video = videoCommentRepository.getVideoBuffhById(videoidIdArr[i].trim());
                video.get(0).setMaxthreads(videoBuffh.getMaxthreads());
                videoCommentRepository.save(video.get(0));

                List<OrderCommentRunning> orderRunnings = videoCommentRepository.getVideoViewById(videoidIdArr[i].trim());
                JSONObject obj = new JSONObject();
                obj.put("orderid", orderRunnings.get(0).getOrderId());
                obj.put("videoid", orderRunnings.get(0).getVideoId());
                obj.put("videotitle", orderRunnings.get(0).getVideoTitle());
                obj.put("commentstart", orderRunnings.get(0).getCommentStart());
                obj.put("maxthreads", videoBuffh.getMaxthreads());
                obj.put("insertdate", orderRunnings.get(0).getInsertDate());
                obj.put("total", orderRunnings.get(0).getTotal());
                obj.put("note", orderRunnings.get(0).getNote());
                obj.put("duration", orderRunnings.get(0).getDuration());
                obj.put("service", orderRunnings.get(0).getService());
                obj.put("user", orderRunnings.get(0).getUser());
                obj.put("commenttotal", orderRunnings.get(0).getCommentTotal());
                obj.put("price", orderRunnings.get(0).getPrice());
                obj.put("commentorder", orderRunnings.get(0).getCommentOrder());


                jsonArray.add(obj);
            }
            resp.put("videocomment", jsonArray);
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

}
