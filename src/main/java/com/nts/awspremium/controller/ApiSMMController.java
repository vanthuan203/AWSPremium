package com.nts.awspremium.controller;

import com.nts.awspremium.GoogleApi;
import com.nts.awspremium.TikTokApi;
import com.nts.awspremium.model.*;
import com.nts.awspremium.model_system.OrderThreadCheck;
import com.nts.awspremium.platform.youtube.YoutubeOrder;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/api")
public class ApiSMMController {
    @Autowired
    private OrderRunningRepository orderRunningRepository;
    @Autowired
    private OrderHistoryRepository orderHistoryRepository;
    @Autowired
    private ServiceSMMRepository serviceRepository;
    @Autowired
    private LogErrorRepository logErrorRepository;
    @Autowired
    private AdminRepository userRepository;
    @Autowired
    private YoutubeOrder youtubeOrder;



    @PostMapping(value = "/smm", consumes = MediaType.APPLICATION_JSON_VALUE,  produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> raw(@RequestBody DataRequest data) throws IOException, ParseException {
        JSONObject resp = new JSONObject();
        try{
            Admin user = userRepository.find_User_By_Token(data.getKey().trim());
            if (data.getKey().length() == 0 || user==null) {
                resp.put("error", "Key not found");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }
            if (data.getAction().equals("services")) {
                List<ServiceSMM> services = serviceRepository.get_All_Service_Enabled();
                JSONArray arr_Service = new JSONArray();
                float rate;
                for (int i = 0; i < services.size(); i++) {
                    rate = services.get(i).getService_rate() * ((float) (user.getRate()) / 100) * ((float) (100 - user.getDiscount()) / 100);
                    JSONObject serviceJson = new JSONObject();
                    serviceJson.put("service", services.get(i).getService_id());
                    serviceJson.put("name", services.get(i).getService_name());
                    serviceJson.put("type", services.get(i).getService_type());
                    serviceJson.put("category", services.get(i).getService_category());
                    serviceJson.put("platform", services.get(i).getPlatform());
                    serviceJson.put("rate", rate);
                    serviceJson.put("min", services.get(i).getMin_quantity());
                    serviceJson.put("max", services.get(i).getMax_quantity());
                    arr_Service.add(serviceJson);
                }
                return new ResponseEntity<String>(arr_Service.toJSONString(), HttpStatus.OK);
            }
            if (data.getAction().equals("balance")) {
                JSONObject serviceJson = new JSONObject();
                serviceJson.put("balance", user.getBalance());
                serviceJson.put("currency", "USD");
                return new ResponseEntity<String>(serviceJson.toJSONString(), HttpStatus.OK);
            }

            if (data.getAction().equals("status")) {
                if (data.getOrders().length() == 0) {
                    OrderRunning orderRunning = orderRunningRepository.get_Order_By_Id(data.getOrder());
                    OrderHistory orderHistory = orderHistoryRepository.get_Order_By_Id(data.getOrder());
                    if (orderRunning != null) {
                        resp.put("start_count", orderRunning.getStart_count());
                        resp.put("current_count", orderRunning.getTotal() + orderRunning.getStart_count());
                        resp.put("charge", orderRunning.getCharge());
                        if (orderRunning.getThread() <= 0) {
                            resp.put("status", "Pending");
                        } else {
                            resp.put("status", "In progress");
                        }
                        resp.put("remains", orderRunning.getQuantity() - orderRunning.getTotal());
                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                    } else {
                        if (orderHistory == null) {
                            resp.put("error", "Incorrect order ID");
                            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                        } else {
                            resp.put("start_count", orderHistory.getStart_count());
                            resp.put("current_count", orderHistory.getStart_count() + orderHistory.getTotal());
                            resp.put("charge", orderHistory.getCharge());
                            if (orderHistory.getCancel() == 1) {
                                resp.put("status", "Canceled");
                            } else if (orderHistory.getCancel() == 2) {
                                resp.put("status", "Partial");
                            } else {
                                resp.put("status", "Completed");
                            }
                            resp.put("remains", orderHistory.getQuantity() - orderHistory.getTotal());
                            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                        }
                    }

                } else {
                    List<String> ordersArrInput = new ArrayList<>();
                    ordersArrInput.addAll(Arrays.asList(data.getOrders().split(",")));
                    String listId = String.join(",", ordersArrInput);
                    List<OrderRunning> orderRunnings = orderRunningRepository.get_Order_By_ListId(ordersArrInput);
                    JSONObject orderList = new JSONObject();
                    for (OrderRunning order : orderRunnings) {
                        JSONObject orderJson = new JSONObject();
                        orderJson.put("start_count", order.getStart_count());
                        orderJson.put("current_count", order.getStart_count() + order.getTotal());
                        orderJson.put("charge", order.getCharge());
                        if (order.getThread() <=0) {
                            orderJson.put("status", "Pending");
                        } else {
                            orderJson.put("status", "In progress");
                        }
                        //videoview.put("status", "In progress");
                        orderJson.put("remains", order.getQuantity() - order.getTotal());
                        orderList.put("" + order.getOrder_id(), orderJson);
                        ordersArrInput.remove("" + order.getOrder_id());
                    }
                    String listIdHis = String.join(",", ordersArrInput);
                    List<OrderHistory> orderHistories = orderHistoryRepository.get_Order_By_ListId(ordersArrInput);
                    for (OrderHistory orderH : orderHistories) {
                        JSONObject orderJson = new JSONObject();
                        if (orderHistories != null) {
                            orderJson.put("start_count", orderH.getStart_count());
                            orderJson.put("current_count", orderH.getTotal() + orderH.getStart_count());
                            orderJson.put("charge", orderH.getCharge());
                            if (orderH.getCancel() == 1) {
                                orderJson.put("status", "Canceled");
                            } else if (orderH.getCancel() == 2) {
                                orderJson.put("status", "Partial");
                            } else {
                                orderJson.put("status", "Completed");
                            }
                            orderJson.put("remains", orderH.getQuantity() - orderH.getTotal());
                            orderList.put("" + orderH.getOrder_id(), orderJson);
                            ordersArrInput.remove("" + orderH.getOrder_id());
                        }
                    }
                    for (String orderId : ordersArrInput) {
                        JSONObject orderIdError = new JSONObject();
                        orderIdError.put("error", "Incorrect order ID");
                        orderList.put(orderId, orderIdError);
                    }
                    return new ResponseEntity<String>(orderList.toJSONString(), HttpStatus.OK);
                }
            }
            if (data.getAction().equals("add")) {
                JSONObject get_task = null;
                ServiceSMM service = serviceRepository.get_Service(data.getService());
                if (service == null) {
                    resp.put("error", "Invalid service");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
                if(data.getQuantity() > service.getMax_quantity() || data.getQuantity() < service.getMin_quantity()){
                    resp.put("error", "Min/Max order is: " + service.getMin_quantity() + "/" + service.getMax_quantity());
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
                if(orderRunningRepository.get_Count_OrderRunning_By_Service(service.getService_id())>=service.getMax_order()){
                    resp.put("error", "System busy try again");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
                if(service.getPlatform().trim().equals("youtube")){
                    if(service.getTask().trim().equals("view")){
                        get_task=youtubeOrder.youtube_view(data,service,user);
                    }else if(service.getTask().trim().equals("like")){
                        get_task=youtubeOrder.youtube_like(data,service,user);
                    }else if(service.getTask().trim().equals("subscriber")){
                        get_task=youtubeOrder.youtube_subscriber(data,service,user);
                    }
                }
                if(get_task==null){
                    resp.put("error","Can't insert link");
                    get_task=resp;
                }
                return new ResponseEntity<String>(get_task.toJSONString(), HttpStatus.OK);
            }
            resp.put("error", "api system error");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }catch (Exception e) {
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

            resp.put("error", "api system error");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }
    }

    @PostMapping(value = "/smm", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,  produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> xxx(DataRequest data) throws IOException, ParseException {
        JSONObject resp = new JSONObject();
        try{
            Admin user = userRepository.find_User_By_Token(data.getKey().trim());
            if (data.getKey().length() == 0 || user==null) {
                resp.put("error", "Key not found");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }
            if (data.getAction().equals("services")) {
                List<ServiceSMM> services = serviceRepository.get_All_Service_Enabled();
                JSONArray arr_Service = new JSONArray();
                float rate;
                for (int i = 0; i < services.size(); i++) {
                    rate = services.get(i).getService_rate() * ((float) (user.getRate()) / 100) * ((float) (100 - user.getDiscount()) / 100);
                    JSONObject serviceJson = new JSONObject();
                    serviceJson.put("service", services.get(i).getService_id());
                    serviceJson.put("name", services.get(i).getService_name());
                    serviceJson.put("type", services.get(i).getService_type());
                    serviceJson.put("category", services.get(i).getService_category());
                    serviceJson.put("platform", services.get(i).getPlatform());
                    serviceJson.put("rate", rate);
                    serviceJson.put("min", services.get(i).getMin_quantity());
                    serviceJson.put("max", services.get(i).getMax_quantity());
                    arr_Service.add(serviceJson);
                }
                return new ResponseEntity<String>(arr_Service.toJSONString(), HttpStatus.OK);
            }
            if (data.getAction().equals("balance")) {
                JSONObject serviceJson = new JSONObject();
                serviceJson.put("balance", user.getBalance());
                serviceJson.put("currency", "USD");
                return new ResponseEntity<String>(serviceJson.toJSONString(), HttpStatus.OK);
            }

            if (data.getAction().equals("status")) {
                if (data.getOrders().length() == 0) {
                    OrderRunning orderRunning = orderRunningRepository.get_Order_By_Id(data.getOrder());
                    OrderHistory orderHistory = orderHistoryRepository.get_Order_By_Id(data.getOrder());
                    if (orderRunning != null) {
                        resp.put("start_count", orderRunning.getStart_count());
                        resp.put("current_count", orderRunning.getTotal() + orderRunning.getStart_count());
                        resp.put("charge", orderRunning.getCharge());
                        if (orderRunning.getThread() <= 0) {
                            resp.put("status", "Pending");
                        } else {
                            resp.put("status", "In progress");
                        }
                        resp.put("remains", orderRunning.getQuantity() - orderRunning.getTotal());
                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                    } else {
                        if (orderHistory == null) {
                            resp.put("error", "Incorrect order ID");
                            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                        } else {
                            resp.put("start_count", orderHistory.getStart_count());
                            resp.put("current_count", orderHistory.getStart_count() + orderHistory.getTotal());
                            resp.put("charge", orderHistory.getCharge());
                            if (orderHistory.getCancel() == 1) {
                                resp.put("status", "Canceled");
                            } else if (orderHistory.getCancel() == 2) {
                                resp.put("status", "Partial");
                            } else {
                                resp.put("status", "Completed");
                            }
                            resp.put("remains", orderHistory.getQuantity() - orderHistory.getTotal());
                            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                        }
                    }

                } else {
                    List<String> ordersArrInput = new ArrayList<>();
                    ordersArrInput.addAll(Arrays.asList(data.getOrders().split(",")));
                    String listId = String.join(",", ordersArrInput);
                    List<OrderRunning> orderRunnings = orderRunningRepository.get_Order_By_ListId(ordersArrInput);
                    JSONObject orderList = new JSONObject();
                    for (OrderRunning order : orderRunnings) {
                        JSONObject orderJson = new JSONObject();
                        orderJson.put("start_count", order.getStart_count());
                        orderJson.put("current_count", order.getStart_count() + order.getTotal());
                        orderJson.put("charge", order.getCharge());
                        if (order.getThread() <=0) {
                            orderJson.put("status", "Pending");
                        } else {
                            orderJson.put("status", "In progress");
                        }
                        //videoview.put("status", "In progress");
                        orderJson.put("remains", order.getQuantity() - order.getTotal());
                        orderList.put("" + order.getOrder_id(), orderJson);
                        ordersArrInput.remove("" + order.getOrder_id());
                    }
                    String listIdHis = String.join(",", ordersArrInput);
                    List<OrderHistory> orderHistories = orderHistoryRepository.get_Order_By_ListId(ordersArrInput);
                    for (OrderHistory orderH : orderHistories) {
                        JSONObject orderJson = new JSONObject();
                        if (orderHistories != null) {
                            orderJson.put("start_count", orderH.getStart_count());
                            orderJson.put("current_count", orderH.getTotal() + orderH.getStart_count());
                            orderJson.put("charge", orderH.getCharge());
                            if (orderH.getCancel() == 1) {
                                orderJson.put("status", "Canceled");
                            } else if (orderH.getCancel() == 2) {
                                orderJson.put("status", "Partial");
                            } else {
                                orderJson.put("status", "Completed");
                            }
                            orderJson.put("remains", orderH.getQuantity() - orderH.getTotal());
                            orderList.put("" + orderH.getOrder_id(), orderJson);
                            ordersArrInput.remove("" + orderH.getOrder_id());
                        }
                    }
                    for (String orderId : ordersArrInput) {
                        JSONObject orderIdError = new JSONObject();
                        orderIdError.put("error", "Incorrect order ID");
                        orderList.put(orderId, orderIdError);
                    }
                    return new ResponseEntity<String>(orderList.toJSONString(), HttpStatus.OK);
                }
            }
            if (data.getAction().equals("add")) {
                JSONObject get_task = null;
                ServiceSMM service = serviceRepository.get_Service(data.getService());
                if (service == null) {
                    resp.put("error", "Invalid service");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
                if(data.getQuantity() > service.getMax_quantity() || data.getQuantity() < service.getMin_quantity()){
                    resp.put("error", "Min/Max order is: " + service.getMin_quantity() + "/" + service.getMax_quantity());
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
                if(orderRunningRepository.get_Count_OrderRunning_By_Service(service.getService_id())>=service.getMax_order()){
                    resp.put("error", "System busy try again");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
                if(service.getPlatform().trim().equals("youtube")){
                    if(service.getTask().trim().equals("view")){
                        get_task=youtubeOrder.youtube_view(data,service,user);
                    }else if(service.getTask().trim().equals("like")){
                        get_task=youtubeOrder.youtube_like(data,service,user);
                    }else if(service.getTask().trim().equals("subscriber")){
                        get_task=youtubeOrder.youtube_subscriber(data,service,user);
                    }else if(service.getTask().trim().equals("live")){
                        get_task=youtubeOrder.youtube_live(data,service,user);
                    }
                }
                if(get_task==null){
                    resp.put("error","Can't insert link");
                    get_task=resp;
                }
                return new ResponseEntity<String>(get_task.toJSONString(), HttpStatus.OK);
            }
            resp.put("error", "api system error");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }catch (Exception e) {
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

            resp.put("error", "api system error");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }
    }

    @PostMapping(value = "/web", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> web(@RequestBody DataRequest data, @RequestHeader(defaultValue = "") String Authorization) throws IOException, ParseException {
        JSONObject resp = new JSONObject();
        try{
            Admin user = userRepository.find_User_By_Token(Authorization.trim());
            if (user==null) {
                resp.put("error", "Key not found");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }
            JSONObject get_task = null;
            ServiceSMM service = serviceRepository.get_Service_Web(data.getService());
            if(service.getPlatform().trim().equals("youtube")){
                if(service.getTask().trim().equals("view")){
                    get_task=youtubeOrder.youtube_view(data,service,user);
                }else if(service.getTask().trim().equals("like")){
                    get_task=youtubeOrder.youtube_like(data,service,user);
                }else if(service.getTask().trim().equals("subscriber")){
                    get_task=youtubeOrder.youtube_subscriber(data,service,user);
                }
            }
            if(get_task.get("error")==null){
                resp.put("order_running", true);
                resp.put("order_id",get_task.get("order"));
            }else{
                resp.put("error", get_task.get("error"));
            }
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }catch (Exception e) {
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

            resp.put("error", "api system error");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }
    }


    public JSONObject refill(DataRequest data,String username) throws IOException, ParseException {
        JSONObject resp = new JSONObject();
        try{
            Admin user = userRepository.find_User_By_Username(username.trim());
            if (user==null) {
                resp.put("error", "Username not found");
                return resp;
            }
            JSONObject get_task = null;
            ServiceSMM service = serviceRepository.get_Service_Web(data.getService());
            if(service.getPlatform().trim().equals("youtube")){
                if(service.getTask().trim().equals("view")){
                    get_task=youtubeOrder.youtube_view(data,service,user);
                }else if(service.getTask().trim().equals("like")){
                    get_task=youtubeOrder.youtube_like(data,service,user);
                }else if(service.getTask().trim().equals("subscriber")){
                    get_task=youtubeOrder.youtube_subscriber_refill(data,service,user);
                }
            }
            if(get_task.get("error")==null){
                resp.put("order_running", true);
                resp.put("order_id",get_task.get("order"));
            }else{
                resp.put("error", get_task.get("error"));
            }
            return resp;
        }catch (Exception e) {
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

            resp.put("error", "api system error");
            return resp;
        }
    }
}