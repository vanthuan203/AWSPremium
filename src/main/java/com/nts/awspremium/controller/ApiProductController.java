package com.nts.awspremium.controller;

import com.nts.awspremium.TikTokApi;
import com.nts.awspremium.model.*;
import com.nts.awspremium.repositories.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/api")
public class ApiProductController {
    @Autowired
    private ChannelTikTokRepository channelTikTokRepository;
    @Autowired
    private ChannelTikTokHistoryRepository channelTikTokHistoryRepository;
    @Autowired
    private ProductHistoryRepository productHistoryRepository;
    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private ProxyRepository proxyRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private SettingRepository settingRepository;
    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private DataOrderRepository dataOrderRepository;

    @Autowired
    private LimitServiceRepository limitServiceRepository;

    @Autowired
    private AccountRepository accountRepository;


    @PostMapping(value = "/product", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> product(DataRequest data) throws IOException, ParseException {
        JSONObject resp = new JSONObject();
        try {
            List<Admin> admins = adminRepository.FindByToken(data.getKey().trim());
            if (data.getKey().length() == 0 || admins.size() == 0) {
                resp.put("error", "Key not found");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }
            //Danh sách dịch vụ view cmc
            if (data.getAction().equals("products")) {
                List<Service> services = serviceRepository.getAllServiceProduct();
                JSONArray arr = new JSONArray();
                float rate;
                for (int i = 0; i < services.size(); i++) {
                    rate = services.get(i).getRate() * ((float) (admins.get(0).getRate()) / 100) * ((float) (100 - admins.get(0).getDiscount()) / 100);
                    JSONObject serviceBuffH = new JSONObject();
                    serviceBuffH.put("product", services.get(i).getService());
                    serviceBuffH.put("name", services.get(i).getName());
                    serviceBuffH.put("type", services.get(i).getType());
                    serviceBuffH.put("category", services.get(i).getCategory());
                    serviceBuffH.put("rate", rate);
                    serviceBuffH.put("min", services.get(i).getMin());
                    serviceBuffH.put("max", services.get(i).getMax());
                    serviceBuffH.put("inventory", services.get(i).getMax());
                    serviceBuffH.put("status","In stock");
                    if(services.get(i).getTask().equals("proxy")){
                        serviceBuffH.put("require","Input username/password");
                    }
                    arr.add(serviceBuffH);
                }
                return new ResponseEntity<String>(arr.toJSONString(), HttpStatus.OK);
            }
            //truy vấn số dư tài khoản
            if (data.getAction().equals("balance")) {
                JSONObject serviceBuffH = new JSONObject();
                serviceBuffH.put("balance", admins.get(0).getBalance());
                serviceBuffH.put("currency", "USD");
                return new ResponseEntity<String>(serviceBuffH.toJSONString(), HttpStatus.OK);
            }
            //Get trạng thái đơns
            if (data.getAction().equals("status")) {
                if (data.getOrders().length() == 0) {

                    ProductHistory productHistory = productHistoryRepository.getProductHistoriesById(data.getOrder());


                    if (productHistory == null) {
                        resp.put("error", "Incorrect order ID");
                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                    } else {
                        Service service = serviceRepository.getService(productHistory.getService());
                        List<String> idArrInput = new ArrayList<>();
                        idArrInput.addAll(Arrays.asList(productHistory.getList_id().split(",")));

                        String list_data="";
                        if(service.getTask().equals("gmail")){
                            List<Account> accounts=accountRepository.findAccountByListId(idArrInput);
                            for(int i=0;i<accounts.size();i++){
                                if(i>0){
                                    list_data=list_data+",";
                                }
                                list_data+=accounts.get(i).getUsername()+"|"+accounts.get(i).getPassword()+"|"+accounts.get(i).getRecover();
                            }
                        }else{
                            List<Proxy> proxies=proxyRepository.findProxyByListId(idArrInput);
                            for(int i=0;i<proxies.size();i++){
                                if(i>0){
                                    list_data=list_data+",";
                                }
                                list_data+=proxies.get(i).getProxy();
                            }
                        }

                        resp.put("charge", productHistory.getCharge());
                        if (productHistory.getCancel() == 1) {
                            resp.put("status", "Canceled");
                        } else if (productHistory.getCancel() == 2) {
                            resp.put("status", "Partial");
                        } else {
                            resp.put("status", "Completed");
                        }
                        resp.put("result", list_data);
                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                    }

                } else {
                    List<String> ordersArrInput = new ArrayList<>();
                    ordersArrInput.addAll(Arrays.asList(data.getOrders().split(",")));
                    JSONObject productObject = new JSONObject();
                    List<ProductHistory> productHistoryList = productHistoryRepository.getChannelTikTokHisByListId(ordersArrInput);
                    for (ProductHistory vh : productHistoryList) {
                        JSONObject product_list = new JSONObject();
                        if (productHistoryList != null) {

                            Service service = serviceRepository.getService(vh.getService());
                            List<String> idArrInput = new ArrayList<>();
                            idArrInput.addAll(Arrays.asList(vh.getList_id().split(",")));

                            String list_data="";
                            if(service.getTask().equals("gmail")){
                                List<Account> accounts=accountRepository.findAccountByListId(idArrInput);
                                for(int i=0;i<accounts.size();i++){
                                    if(i>0){
                                        list_data=list_data+",";
                                    }
                                    list_data+=accounts.get(i).getUsername()+"|"+accounts.get(i).getPassword()+"|"+accounts.get(i).getRecover();
                                }
                            }else{
                                List<Proxy> proxies=proxyRepository.findProxyByListId(idArrInput);
                                for(int i=0;i<proxies.size();i++){
                                    if(i>0){
                                        list_data=list_data+",";
                                    }
                                    list_data+=proxies.get(i).getProxy();
                                }
                            }


                            product_list.put("charge", vh.getCharge());
                            if (vh.getCancel() == 1) {
                                product_list.put("status", "Canceled");
                            } else if (vh.getCancel() == 2) {
                                product_list.put("status", "Partial");
                            } else {
                                product_list.put("status", "Completed");
                            }
                            product_list.put("result", list_data);
                            productObject.put("" + vh.getOrder_id(), product_list);
                            ordersArrInput.remove("" + vh.getOrder_id());
                        }
                    }
                    for (String orderId : ordersArrInput) {
                        JSONObject orderIdError = new JSONObject();
                        orderIdError.put("error", "Incorrect order ID");
                        productObject.put(orderId, orderIdError);
                    }
                    return new ResponseEntity<String>(productObject.toJSONString(), HttpStatus.OK);
                }
            }
            if (data.getAction().equals("add_product_order")) {

                Service service = serviceRepository.getService(data.getProduct());
                if (service == null) {
                    resp.put("error", "Invalid product");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
                if(data.getRequire().trim().length()==0&&service.getTask().equals("proxy")){
                    resp.put("error", "Require is null");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
                if (data.getQuantity() > service.getMax() || data.getQuantity() < service.getMin()) {
                    resp.put("error", "Min/Max order is: " + service.getMin() + "/" + service.getMax());
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
                float priceorder = 0;
                priceorder = data.getQuantity() * service.getRate() * ((float) (admins.get(0).getRate()) / 100) * ((float) (100 - admins.get(0).getDiscount()) / 100);
                if (priceorder > (float) admins.get(0).getBalance()) {
                    resp.put("error", "Your balance not enough");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
                String list_data="";
                String list_id_product="";
                if(service.getTask().equals("gmail")){
                    List<Long> list_id=accountRepository.getAccountRandByLimit(data.getQuantity());
                    for(int i=0;i<list_id.size();i++){
                        Account account=accountRepository.getAccountById(list_id.get(i));
                        if(i>0){
                            list_data=list_data+",";
                            list_id_product=list_id_product+",";
                        }
                        list_id_product+=account.getId().toString();
                        list_data+=account.getUsername()+"|"+account.getPassword()+"|"+account.getRecover();
                    }
                }else{
                    List<Long> list_id=proxyRepository.getProxyRandByLimit(data.getQuantity());
                    for(int i=0;i<list_id.size();i++){
                        Proxy proxy=proxyRepository.getProxyById(list_id.get(i));
                        if(i>0){
                            list_data=list_data+",";
                            list_id_product=list_id_product+",";
                        }
                        list_id_product+=proxy.getId().toString();
                        list_data+=proxy.getProxy();
                    }
                }

                ProductHistory productHistory = new ProductHistory();
                productHistory.setInsert_time(System.currentTimeMillis());
                productHistory.setCharge(priceorder);
                productHistory.setQuantity(data.getQuantity());
                productHistory.setList_id(list_id_product);
                productHistory.setCancel(0);
                productHistory.setService(service.getService());
                productHistory.setNote("");
                productHistory.setUser(admins.get(0).getUsername());
                productHistory.setRefund(0);
                productHistory.setRefund_time(0L);
                productHistoryRepository.save(productHistory);

                Float balance_update=adminRepository.updateBalanceFine(-priceorder,admins.get(0).getUsername().trim());
                Balance balance = new Balance();
                balance.setUser(admins.get(0).getUsername().trim());
                balance.setTime(System.currentTimeMillis());
                balance.setTotalblance(balance_update);
                balance.setBalance(-priceorder);
                balance.setService(data.getService());
                balance.setNote("Order " + data.getQuantity());
                balanceRepository.save(balance);
                resp.put("order", productHistory.getOrder_id());
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }
        } catch (Exception e) {
            //dong loi
            StackTraceElement stackTraceElement = Arrays.stream(e.getStackTrace()).filter(ste -> ste.getClassName().equals(this.getClass().getName())).collect(Collectors.toList()).get(0);
            System.out.println(stackTraceElement.getMethodName());
            System.out.println(stackTraceElement.getLineNumber());
            System.out.println(stackTraceElement.getClassName());
            System.out.println(stackTraceElement.getFileName());
            System.out.println("Error : " + e.getMessage());
            resp.put("error", "api system error");
            resp.put("error",e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }
        resp.put("error", "api system error");
        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
    }
}
