package com.nts.awspremium.controller;

import com.nts.awspremium.StringUtils;
import com.nts.awspremium.model.*;
import com.nts.awspremium.repositories.*;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/accview")

public class AccountViewController {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountReg24hRepository accountReg24hRepository;
    @Autowired
    private GoogleSuiteRepository googleSuiteRepository;
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private HistoryViewRepository historyViewRepository;

    @Autowired
    private HistoryTrafficRepository historyTrafficRepository;

    @Autowired
    private HistoryCommentRepository historyCommentRepository;

    @Autowired
    private ProxyRepository proxyRepository;

    @Autowired
    private VpsRepository vpsRepository;
    @Autowired
    private RecoverRepository recoverRepository;
    @Autowired
    private ProxyVNTrue proxyVNTrue;
    @Autowired
    private ProxySettingRepository proxySettingRepository;
    @Autowired
    private ProxyUSTrue proxyUSTrue;
    @Autowired
    private SettingRepository settingRepository;
    @Autowired
    private CheckProsetListTrue checkProsetListTrue;

    @PostMapping(value = "/create", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> createaccount(@RequestBody Account newaccount, @RequestHeader(defaultValue = "") String Authorization,
                                         @RequestParam(defaultValue = "1") Integer update) {
        JSONObject resp = new JSONObject();
        Integer checktoken = adminRepository.FindAdminByToken(Authorization);
        if (checktoken == 0) {
            resp.put("status", "fail");
            resp.put("message", "Token expired");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        try {
            Long idUsername = accountRepository.findIdUsername(newaccount.getUsername().trim());
            if (idUsername != null) {
                if (update == 1) {
                    accountRepository.updateAccountView(newaccount.getPassword(), newaccount.getRecover(), newaccount.getLive(), idUsername);
                    resp.put("status", "true");
                    resp.put("message", "Update " + newaccount.getUsername() + " thành công!");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                } else {
                    resp.put("status", "fail");
                    resp.put("message", "Account " + newaccount.getUsername() + " đã tồn tại");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
            } else {
                Account account = new Account();
                account.setUsername(newaccount.getUsername().toLowerCase());
                account.setPassword(newaccount.getPassword());
                account.setRecover(newaccount.getRecover().toLowerCase());
                account.setLive(1);
                account.setVps("");
                account.setProxy("");
                account.setRunning(0);
                account.setGet_time(0L);
                account.setEnd_time(0L);
                account.setDate(newaccount.getDate());
                account.setGeo(newaccount.getGeo());
                account.setGoogle_suite(newaccount.getGoogle_suite());
                account.setStatus(false);
                account.setReg(false);
                account.setGroup_mail(newaccount.getGroup_mail()==null?"0":newaccount.getGroup_mail());
                accountRepository.save(account);
                resp.put("status", "true");
                resp.put("message", "Insert " + newaccount.getUsername() + " thành công!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/get", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> getAccount(@RequestParam(defaultValue = "") String vps, @RequestParam(defaultValue = "vn") String geo,@RequestParam(defaultValue = "0") Integer cmt,@RequestHeader(defaultValue = "") String Authorization) throws InterruptedException {
        JSONObject resp = new JSONObject();
        Random ran = new Random();
        Integer checktoken = adminRepository.FindAdminByToken(Authorization);
        if (checktoken == 0) {
            resp.put("status", "fail");
            resp.put("message", "Token expired");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        if (vps.length() == 0) {
            resp.put("status", "fail");
            resp.put("message", "Tên vps không để trống");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        if(vpsRepository.checkVpsGetAccountTrue(vps.trim())==0){
            resp.put("status", "fail");
            resp.put("message", "Đã đủ acc cho Vps!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }
        if(checkProsetListTrue.getValue()>=50){
            resp.put("status", "fail");
            resp.put("message", "Get account không thành công, thử lại sau ítp phút!");
            Thread.sleep(ran.nextInt(1000));
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }
        try {
            Integer check_geo=0;
            if (cmt==0) {
                if(geo.contains("smm")){
                    Integer check_get = vpsRepository.checkGetAccountSMMByThreadVps(vps.trim(),geo.trim());
                    if (check_get == 0) {
                        resp.put("status", "fail");
                        resp.put("message", "Đã đủ acc cho Vps!");
                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                    }
                }else{
                    Integer check_get = vpsRepository.checkGetAccount5ByThreadVps(vps.trim(),geo.trim());
                    if (check_get == 0) {
                        resp.put("status", "fail");
                        resp.put("message", "Đã đủ acc cho Vps!");
                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                    }
                }

            } else {
                if(vpsRepository.checkVpsCmtTrue(vps.trim())==0){
                    resp.put("status", "fail");
                    resp.put("message", "Đã đủ acc cmt cho Vps!");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
                Integer check_get=0;
                if(geo.trim().equals("vn")){
                    check_get= vpsRepository.checkGetAccountCmtByVps(vps.trim(),"cmt-vn");
                    if(check_get!=0){
                        check_geo=1;
                    }else{
                        check_get= vpsRepository.checkGetAccountCmtByVpsUS(vps.trim(),"cmt-us");
                        if(check_get!=0){
                            check_geo=2;
                        }
                    }
                }else if(geo.trim().equals("us")){
                    check_get= vpsRepository.checkGetAccountCmtByVpsUS(vps.trim(),"cmt-"+geo.trim());
                }else if(geo.trim().equals("kr")){
                    check_get= vpsRepository.checkGetAccountCmtByVpsKR(vps.trim(),"cmt-"+geo.trim());
                }else if(geo.trim().equals("jp")){
                    check_get= vpsRepository.checkGetAccountCmtByVpsJP(vps.trim(),"cmt-"+geo.trim());
                }else if(geo.trim().equals("vn")){
                    check_get= vpsRepository.checkGetAccountCmtByVps(vps.trim(),"cmt-"+geo.trim());
                }else if(geo.trim().contains("live")){
                    check_get= vpsRepository.checkGetAccount5ByThreadVps(vps.trim(),"cmt-"+geo.trim());
                }
                if (check_get == 0) {
                    resp.put("status", "fail");
                    resp.put("message", "Đã đủ acc cmt cho Vps!");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
            }
            Thread.sleep(ran.nextInt(500));
            Long idbyVps=null;
            if (cmt==0){
                idbyVps = accountRepository.getaccountByVps(vps.trim(),geo.trim());
            }else{
                idbyVps = accountRepository.getaccountByVps(vps.trim(),"cmt-"+geo.trim());
                if(idbyVps==null&&geo.trim().equals("vn")){
                    idbyVps = accountRepository.getaccountByVps(vps.trim(),"cmt-us");
                }
            }
            if (idbyVps == null) {
                Thread.sleep(ran.nextInt(500));
                Long id=null;
                if(cmt==0){
                    id = accountRepository.getAccountView("view");
                }else{
                    if(geo.equals("vn")){
                        if(check_geo==1){
                            id = accountRepository.getAccountView("cmt-vn");
                        }else if(check_geo==2){
                            id = accountRepository.getAccountView("cmt-us");
                        }
                    }else{
                        id = accountRepository.getAccountView("cmt-"+geo.trim());
                    }
                }
                if (id == null) {
                    resp.put("status", "fail");
                    resp.put("message", "Đã đủ acc cho Vps! Hết tài khoản thỏa mãn");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                } else {
                    try {

                        List<Account> account = accountRepository.findAccountById(id);
                        Thread.sleep(100 + ran.nextInt(200));
                        Integer accountcheck = accountRepository.checkAccountById(id);
                        if (accountcheck == 0) {
                            resp.put("status", "fail");
                            resp.put("message", "Get account không thành công, thử lại sau ít phút!");
                            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                        }
                        if(cmt==0){
                            account.get(0).setProxy("");
                            account.get(0).setGeo(geo.trim());
                            /*
                            if(account.get(0).getProxy()== null|| account.get(0).getProxy().length()==0){
                                List<Proxy> proxies=proxyRepository.getProxyFixAccountByGeo(geo.trim());
                                if(proxies.size()!=0){
                                    account.get(0).setProxy(proxies.get(0).getProxy());
                                    proxyRepository.updateProxyGet(vps,System.currentTimeMillis(),proxies.get(0).getId());
                                }else{
                                    account.get(0).setProxy("");
                                }
                            }

                             */
                        }else{
                            account.get(0).setProxy("");
                        }
                        account.get(0).setVps(vps.trim());
                        account.get(0).setRunning(1);
                        account.get(0).setGet_time(System.currentTimeMillis());
                        accountRepository.save(account.get(0));
                        if(cmt==0){
                            Long historieId = historyViewRepository.getId(account.get(0).getUsername());
                            if (historieId == null) {
                                try{
                                    HistoryView history = new HistoryView();
                                    history.setUsername(account.get(0).getUsername());
                                    history.setListvideo("");
                                    history.setProxy(account.get(0).getProxy());
                                    history.setTypeproxy((account.get(0).getProxy().split(":"))[0]);
                                    history.setRunning(0);
                                    history.setTask_done(0);
                                    history.setVps(vps);
                                    history.setVideoid("");
                                    history.setOrderid(0L);
                                    history.setChannelid("");
                                    history.setGeo(account.get(0).getGeo());
                                    history.setFinger_id(0L);
                                    history.setTask_time(0L);
                                    history.setMax_time(0);
                                    history.setTimeget(System.currentTimeMillis());
                                    historyViewRepository.save(history);
                                }catch (Exception e){
                                    Thread.sleep(10+ran.nextInt(1000));
                                    HistoryView history = new HistoryView();
                                    history.setUsername(account.get(0).getUsername());
                                    history.setListvideo("");
                                    history.setProxy(account.get(0).getProxy());
                                    history.setTypeproxy((account.get(0).getProxy().split(":"))[0]);
                                    history.setRunning(0);
                                    history.setTask_done(0);
                                    history.setVps(vps);
                                    history.setVideoid("");
                                    history.setOrderid(0L);
                                    history.setChannelid("");
                                    history.setGeo(account.get(0).getGeo());
                                    history.setFinger_id(0L);
                                    history.setTask_time(0L);
                                    history.setMax_time(0);
                                    history.setTimeget(System.currentTimeMillis());
                                    historyViewRepository.save(history);
                                }

                            }else {
                                List<HistoryView> histories = historyViewRepository.getHistoriesById(historieId);
                                histories.get(0).setListvideo("");
                                histories.get(0).setProxy(account.get(0).getProxy());
                                histories.get(0).setTypeproxy((account.get(0).getProxy().split(":"))[0]);
                                histories.get(0).setRunning(0);
                                histories.get(0).setTask_done(0);
                                histories.get(0).setVps(vps);
                                histories.get(0).setVideoid("");
                                histories.get(0).setOrderid(0L);
                                histories.get(0).setChannelid("");
                                histories.get(0).setGeo(account.get(0).getGeo());
                                histories.get(0).setFinger_id(0L);
                                histories.get(0).setTask_time(0L);
                                histories.get(0).setMax_time(0);
                                histories.get(0).setTimeget(System.currentTimeMillis());
                                historyViewRepository.save(histories.get(0));
                            }
                        }

                        resp.put("status", "true");

                        if(cmt==1){
                            float ran_SL=ran.nextFloat()*100F;
                            if(ran_SL<80){
                                resp.put("avatar", "true");
                            }else{
                                resp.put("avatar", "fail");
                            }
                        }

                        resp.put("username", account.get(0).getUsername());
                        resp.put("geo", account.get(0).getGeo().replace("cmt-","").trim());
                        resp.put("password", account.get(0).getPassword());
                        resp.put("recover", account.get(0).getRecover());
                        resp.put("cookie", "");
                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                    } catch (Exception e) {
                        resp.put("status", "fail");
                        resp.put("message", e.getMessage());
                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
                    }
                }
            } else {
                try {
                    List<Account> accountbyVps = accountRepository.findAccountById(idbyVps);
                    if(cmt==0){
                        /*
                        if(accountbyVps.get(0).getProxy()==null || accountbyVps.get(0).getProxy().length()==0){
                            List<Proxy> proxies=proxyRepository.getProxyFixAccountByGeo(geo.trim());
                            if(proxies.size()!=0){
                                accountbyVps.get(0).setProxy(proxies.get(0).getProxy());
                                proxyRepository.updateProxyGet(vps,System.currentTimeMillis(),proxies.get(0).getId());
                            }else{
                                accountbyVps.get(0).setProxy("");
                            }
                        }
                         */
                         accountbyVps.get(0).setProxy("");

                    }else{
                        accountbyVps.get(0).setProxy("");
                    }
                    accountbyVps.get(0).setVps(vps.trim());
                    accountbyVps.get(0).setRunning(1);
                    accountbyVps.get(0).setGet_time(System.currentTimeMillis());
                    accountRepository.save(accountbyVps.get(0));
                    if(cmt==0){
                        Long historieId = historyViewRepository.getId(accountbyVps.get(0).getUsername());
                        if (historieId == null) {
                            try{
                                HistoryView history = new HistoryView();
                                history.setUsername(accountbyVps.get(0).getUsername());
                                history.setListvideo("");
                                history.setProxy(accountbyVps.get(0).getProxy());
                                history.setTypeproxy((accountbyVps.get(0).getProxy().split(":"))[0]);
                                history.setRunning(0);
                                history.setTask_done(0);
                                history.setVps(vps);
                                history.setVideoid("");
                                history.setOrderid(0L);
                                history.setChannelid("");
                                history.setGeo(accountbyVps.get(0).getGeo());
                                history.setFinger_id(0L);
                                history.setTask_time(0L);
                                history.setTimeget(System.currentTimeMillis());
                                historyViewRepository.save(history);
                            }catch (Exception e){
                                Thread.sleep(10+ran.nextInt(1000));
                                HistoryView history = new HistoryView();
                                history.setUsername(accountbyVps.get(0).getUsername());
                                history.setListvideo("");
                                history.setProxy(accountbyVps.get(0).getProxy());
                                history.setTypeproxy((accountbyVps.get(0).getProxy().split(":"))[0]);
                                history.setRunning(0);
                                history.setTask_done(0);
                                history.setVps(vps);
                                history.setVideoid("");
                                history.setOrderid(0L);
                                history.setChannelid("");
                                history.setGeo(accountbyVps.get(0).getGeo());
                                history.setFinger_id(0L);
                                history.setTask_time(0L);
                                history.setTimeget(System.currentTimeMillis());
                                historyViewRepository.save(history);
                            }
                        }else {
                            List<HistoryView> histories = historyViewRepository.getHistoriesById(historieId);
                            histories.get(0).setListvideo("");
                            histories.get(0).setProxy(accountbyVps.get(0).getProxy());
                            histories.get(0).setTypeproxy((accountbyVps.get(0).getProxy().split(":"))[0]);
                            histories.get(0).setRunning(0);
                            histories.get(0).setTask_done(0);
                            histories.get(0).setVps(vps);
                            histories.get(0).setVideoid("");
                            histories.get(0).setOrderid(0L);
                            histories.get(0).setChannelid("");
                            histories.get(0).setGeo(accountbyVps.get(0).getGeo());
                            histories.get(0).setFinger_id(0L);
                            histories.get(0).setTask_time(0L);
                            histories.get(0).setTimeget(System.currentTimeMillis());
                            historyViewRepository.save(histories.get(0));
                        }
                    }

                    resp.put("status", "true");

                    if(cmt==1){
                        float ran_SL=ran.nextFloat()*100F;
                        if(ran_SL<80){
                            resp.put("avatar", "true");
                        }else{
                            resp.put("avatar", "fail");
                        }
                    }

                    resp.put("username", accountbyVps.get(0).getUsername());
                    resp.put("geo", accountbyVps.get(0).getGeo().replace("cmt-","").trim());
                    resp.put("recover", accountbyVps.get(0).getRecover());
                    resp.put("cookie", "");
                    resp.put("password", accountbyVps.get(0).getPassword());
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                } catch (Exception e) {
                    resp.put("status", "fail");
                    resp.put("message", e.getMessage());
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
                }
            }
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/gettraffic", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> gettraffic(@RequestParam(defaultValue = "") String vps, @RequestParam(defaultValue = "") String geo,@RequestHeader(defaultValue = "") String Authorization) throws InterruptedException {
        JSONObject resp = new JSONObject();
        Random ran = new Random();
        Integer checktoken = adminRepository.FindAdminByToken(Authorization);
        if (checktoken == 0) {
            resp.put("status", "fail");
            resp.put("message", "Token expired");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        if (vps.length() == 0) {
            resp.put("status", "fail");
            resp.put("message", "Tên vps không để trống");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        if(vpsRepository.checkVpsValid(vps.trim())==0){
            resp.put("status", "fail");
            resp.put("message", "Vps không tồn tại!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }
        if(vpsRepository.checkVpsGetAccountTrue(vps.trim())==0){
            resp.put("status", "fail");
            resp.put("message", "Đã đủ acc cho Vps!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }
        if(checkProsetListTrue.getValue()>=50){
            resp.put("status", "fail");
            resp.put("message", "Get account không thành công, thử lại sau ít phút!");
            Thread.sleep(ran.nextInt(1000));
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        }
        try {
            Integer check_get = vpsRepository.checkGetAccount5ByThreadVps(vps.trim(),geo.trim());
            if (check_get == 0) {
                resp.put("status", "fail");
                resp.put("message", "Đã đủ acc cho Vps!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }
            Thread.sleep(ran.nextInt(500));
            Long idbyVps=null;
            idbyVps = accountRepository.getaccountByVps(vps.trim(),geo.trim());

            if (idbyVps == null) {
                Thread.sleep(ran.nextInt(500));
                Long id=null;
                id = accountRepository.getAccountView(geo.trim());
                if (id == null) {
                    resp.put("status", "fail");
                    resp.put("message", "Đã đủ acc cho Vps! Hết tài khoản thỏa mãn");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                } else {
                    try {
                        List<Account> account = accountRepository.findAccountById(id);
                        Thread.sleep(100 + ran.nextInt(200));
                        Integer accountcheck = accountRepository.checkAccountById(id);
                        if (accountcheck == 0) {
                            resp.put("status", "fail");
                            resp.put("message", "Get account không thành công, thử lại sau ítp phút!");
                            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                        }else{
                            if(account.get(0).getProxy().length()==0 || account.get(0).getProxy().equals("0:0") ){
                                List<Proxy> proxies=proxyRepository.getProxyFixAccountTraffic();
                                if(proxies.size()!=0) {
                                    account.get(0).setProxy(proxies.get(0).getProxy());
                                    accountRepository.save(account.get(0));
                                    proxyRepository.updateProxyGetTraffic(vps, System.currentTimeMillis(), proxies.get(0).getId());
                                }
                            }
                        }
                        account.get(0).setVps(vps.trim());
                        account.get(0).setRunning(1);
                        account.get(0).setGet_time(System.currentTimeMillis());
                        accountRepository.save(account.get(0));
                        Long historieId = historyTrafficRepository.getId(account.get(0).getUsername());
                        if (historieId == null) {
                            try{
                                HistoryTraffic history = new HistoryTraffic();
                                history.setUsername(account.get(0).getUsername());
                                history.setListorderid("");
                                history.setRunning(0);
                                history.setVps(vps);
                                history.setOrderid(0L);
                                if(ran.nextInt(100)>50){
                                    history.setDevice("mobile");
                                }else{
                                    history.setDevice("pc");
                                }
                                history.setGeo(account.get(0).getGeo());
                                history.setTimeget(System.currentTimeMillis());
                                historyTrafficRepository.save(history);
                            }catch (Exception e){
                                Thread.sleep(10+ran.nextInt(1000));
                                HistoryTraffic history = new HistoryTraffic();
                                history.setUsername(account.get(0).getUsername());
                                history.setListorderid("");
                                history.setRunning(0);
                                history.setVps(vps);
                                history.setOrderid(0L);
                                if(ran.nextInt(1000)<750){
                                    history.setDevice("mobile");
                                }else{
                                    history.setDevice("pc");
                                }
                                history.setGeo(account.get(0).getGeo());
                                history.setTimeget(System.currentTimeMillis());
                                historyTrafficRepository.save(history);
                            }

                        }else {
                            List<HistoryTraffic> histories = historyTrafficRepository.getHistoriesById(historieId);
                            histories.get(0).setUsername(account.get(0).getUsername());
                            histories.get(0).setListorderid("");
                            histories.get(0).setRunning(0);
                            histories.get(0).setVps(vps);
                            histories.get(0).setOrderid(0L);
                            if(ran.nextInt(1000)<750){
                                histories.get(0).setDevice("mobile");
                            }else{
                                histories.get(0).setDevice("pc");
                            }
                            histories.get(0).setGeo(account.get(0).getGeo());
                            histories.get(0).setTimeget(System.currentTimeMillis());
                            historyTrafficRepository.save(histories.get(0));
                        }

                        resp.put("status", "true");
                        resp.put("username", account.get(0).getUsername());
                        resp.put("password", account.get(0).getPassword());
                        resp.put("recover", account.get(0).getRecover());
                        resp.put("cookie", "");
                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                    } catch (Exception e) {
                        resp.put("status", "fail");
                        resp.put("message", e.getMessage());
                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
                    }
                }
            } else {
                try {
                    List<Account> accountbyVps = accountRepository.findAccountById(idbyVps);
                    accountbyVps.get(0).setVps(vps.trim());
                    accountbyVps.get(0).setRunning(1);
                    accountbyVps.get(0).setGet_time(System.currentTimeMillis());
                    accountRepository.save(accountbyVps.get(0));
                    Long historieId = historyTrafficRepository.getId(accountbyVps.get(0).getUsername());
                    if (historieId == null) {
                        try{
                            HistoryTraffic history = new HistoryTraffic();
                            history.setUsername(accountbyVps.get(0).getUsername());
                            history.setListorderid("");
                            history.setRunning(0);
                            history.setVps(vps);
                            history.setOrderid(0L);
                            if(ran.nextInt(100)>50){
                                history.setDevice("mobile");
                            }else{
                                history.setDevice("pc");
                            }
                            history.setGeo(accountbyVps.get(0).getGeo());
                            history.setTimeget(System.currentTimeMillis());
                            historyTrafficRepository.save(history);
                        }catch (Exception e){
                            Thread.sleep(10+ran.nextInt(1000));
                            HistoryTraffic history = new HistoryTraffic();
                            history.setUsername(accountbyVps.get(0).getUsername());
                            history.setListorderid("");
                            history.setRunning(0);
                            history.setVps(vps);
                            history.setOrderid(0L);
                            if(ran.nextInt(100)>50){
                                history.setDevice("mobile");
                            }else{
                                history.setDevice("pc");
                            }
                            history.setGeo(accountbyVps.get(0).getGeo());
                            history.setTimeget(System.currentTimeMillis());
                            historyTrafficRepository.save(history);
                        }
                    }else {
                        List<HistoryTraffic> histories = historyTrafficRepository.getHistoriesById(historieId);
                        histories.get(0).setUsername(accountbyVps.get(0).getUsername());
                        histories.get(0).setListorderid("");
                        histories.get(0).setRunning(0);
                        histories.get(0).setVps(vps);
                        histories.get(0).setOrderid(0L);
                        if(ran.nextInt(100)>50){
                            histories.get(0).setDevice("mobile");
                        }else{
                            histories.get(0).setDevice("pc");
                        }
                        histories.get(0).setGeo(accountbyVps.get(0).getGeo());
                        histories.get(0).setTimeget(System.currentTimeMillis());
                        historyTrafficRepository.save(histories.get(0));
                    }

                    resp.put("status", "true");
                    resp.put("username", accountbyVps.get(0).getUsername());
                    resp.put("recover", accountbyVps.get(0).getRecover());
                    resp.put("cookie", "");
                    resp.put("password", accountbyVps.get(0).getPassword());
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                } catch (Exception e) {
                    resp.put("status", "fail");
                    resp.put("message", e.getMessage());
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
                }
            }
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(value = "/getlogin", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> getlogin(@RequestHeader(defaultValue = "") String Authorization) {
        JSONObject resp = new JSONObject();
        Integer checktoken = adminRepository.FindAdminByToken(Authorization);
        if (checktoken == 0) {

            resp.put("status", "fail");
            resp.put("message", "Token expired");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        try {
            Long id = accountRepository.getAccountNeedLogin();
            if (id == null) {
                resp.put("status", "fail");
                resp.put("message", "Hết tài khoản thỏa mãn!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            } else {
                try {
                    List<Account> account = accountRepository.findAccountById(id);
                    if (account.get(0).getRunning() == 1) {
                        resp.put("status", "fail");
                        resp.put("message", "Get account không thành công, thử lại sau ítp phút!");
                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                    }
                    //account.get(0).setVps("");
                    account.get(0).setRunning(1);
                    accountRepository.save(account.get(0));

                    resp.put("status", "true");
                    resp.put("username", account.get(0).getUsername());
                    resp.put("password", account.get(0).getPassword());
                    resp.put("recover", account.get(0).getRecover());
                    //resp.put("cookie",account.get(0).getCookie());
                    //resp.put("encodefinger",encodefingerRepository.findEncodefingerSub(account.get(0).getUsername().trim()));
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                } catch (Exception e) {
                    resp.put("status", "fail");
                    resp.put("message", e.getMessage());
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
                }
            }
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping(value = "/get_Account_Reg", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> get_Account_Reg(@RequestHeader(defaultValue = "") String Authorization,
                                           @RequestParam(defaultValue = "") String google_suite) {
        JSONObject resp = new JSONObject();
        Integer checktoken = adminRepository.FindAdminByToken(Authorization);
        if (checktoken == 0) {
            resp.put("status", "fail");
            resp.put("message", "Token expired");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        if (google_suite.length() == 0) {
            resp.put("status", "fail");
            resp.put("message", "google_suite không để trống!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        try {
            GoogleSuite googleSuite =googleSuiteRepository.get_Google_Suite(google_suite.trim());
            if(googleSuite==null){
                resp.put("status", "fail");
                resp.put("message", "google_suite không tồn tại!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
            }else{
                if(googleSuite.getState()==false){
                    resp.put("status", "fail");
                    resp.put("time", googleSuite.getUpdate_time());
                    resp.put("message", "Đợi 24h nhé!");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                }
            }
            Long id = accountRepository.getAccountREG(google_suite.trim());
            if (id == null) {
                resp.put("status", "fail");
                resp.put("message", "Hết tài khoản thỏa mãn!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            } else {
                try {
                    List<Account> account = accountRepository.findAccountById(id);
                    if (account.size()==0 || account.get(0).getReg() == true) {
                        resp.put("status", "fail");
                        resp.put("message", "Get account không thành công, thử lại sau ít phút!");
                        return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                    }
                    account.get(0).setReg(true);
                    account.get(0).setGet_time(System.currentTimeMillis());
                    accountRepository.save(account.get(0));

                    AccountReg24h accountReg24h =new AccountReg24h();
                    accountReg24h.setId(account.get(0).getGoogle_suite()+"|"+account.get(0).getUsername().trim());
                    accountReg24h.setStatus(false);
                    accountReg24h.setUpdate_time(System.currentTimeMillis());
                    accountReg24hRepository.save(accountReg24h);


                    resp.put("status", "true");
                    resp.put("username", account.get(0).getUsername());
                    resp.put("password", account.get(0).getPassword());
                    resp.put("recover", account.get(0).getRecover());
                    resp.put("recover", account.get(0).getRecover());
                    resp.put("cmt", 0);
                    resp.put("avatar", 0);
                    resp.put("geo", "vn");
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
                } catch (Exception e) {
                    resp.put("status", "fail");
                    resp.put("message", e.getMessage());
                    return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
                }
            }
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }

    }


    @GetMapping(value = "/cron_Google_suite", produces = "application/hal+json;charset=utf8")
    public ResponseEntity<String> cron_Google_suite() {
        JSONObject resp = new JSONObject();
        try {
            accountReg24hRepository.deleteAllByThan10m();
            accountReg24hRepository.deleteAllByThan24h();
            accountRepository.resetAccountRegByThan10m();
            googleSuiteRepository.update_State_Google_Suite();
            resp.put("status", "true");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }



    @GetMapping(value = "/update_Account_Reg", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> update_Account_Reg(@RequestHeader(defaultValue = "") String Authorization,
                                              @RequestParam(defaultValue = "") String username,
                                              @RequestParam(defaultValue = "0") Integer status) {
        JSONObject resp = new JSONObject();
        Integer checktoken = adminRepository.FindAdminByToken(Authorization);
        if (checktoken == 0) {
            resp.put("status", "fail");
            resp.put("message", "Token expired");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        if (username.length() == 0) {
            resp.put("status", "fail");
            resp.put("message", "username không để trống!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }

        try {

            Account account = accountRepository.findAccountByUsername(username.trim().toLowerCase());
            if(account==null){
                resp.put("status", "fail");
                resp.put("message", "Tài khoản không tồn tại!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
            }
            AccountReg24h accountReg24h = accountReg24hRepository.get_Reg_24h_By_Username(account.getGoogle_suite().trim()+"|"+account.getUsername().trim());

            GoogleSuite googleSuite =googleSuiteRepository.get_Google_Suite(account.getGoogle_suite().trim());

            if(status==1){
                if(accountReg24h!=null){
                    accountReg24h.setUpdate_time(System.currentTimeMillis());
                    accountReg24h.setStatus(true);
                    accountReg24hRepository.save(accountReg24h);
                }

                account.setReg(true);
                account.setStatus(true);
                accountRepository.save(account);
                Setting setting =settingRepository.getSettingId1();
                if(googleSuite.getState()==true&&accountReg24hRepository.count_Reg_24h_By_GoogleSuite(account.getGoogle_suite().trim()+"%")>=setting.getPricerate()){
                    googleSuite.setState(false);
                    googleSuite.setUpdate_time(System.currentTimeMillis());
                    googleSuiteRepository.save(googleSuite);
                }

            }else if(status==0){
                account.setReg(false);
                account.setStatus(false);
                accountRepository.save(account);
                if(accountReg24h!=null){
                    accountReg24hRepository.delete(accountReg24h);
                }
            }else if(status==-1){

                account.setReg(false);
                account.setStatus(false);
                accountRepository.save(account);
                if(accountReg24h!=null){
                    accountReg24hRepository.delete(accountReg24h);
                }

                if(googleSuite.getState()==true){
                    googleSuite.setState(false);
                    googleSuite.setUpdate_time(System.currentTimeMillis());
                    googleSuiteRepository.save(googleSuite);
                }
            }else if(status>1){
                account.setReg(false);
                account.setStatus(false);
                account.setLive(status);
                accountRepository.save(account);
                if(accountReg24h!=null){
                    accountReg24hRepository.delete(accountReg24h);
                }
            }
            resp.put("status", "true");
            resp.put("message", "update thành công!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }


    }

    @GetMapping(value = "/list_Google_Suite", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> list_Google_Suite(@RequestHeader(defaultValue = "") String Authorization) {
        JSONObject resp = new JSONObject();
        try {
            Integer checktoken = adminRepository.FindAdminByToken(Authorization);
            if (checktoken == 0) {
                resp.put("status", "fail");
                resp.put("message", "Token expired");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
            }

            resp.put("status", "true");
            resp.put("list", googleSuiteRepository.get_List_Google_Suite());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(value = "/countgmails", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> countgmails(@RequestHeader(defaultValue = "") String Authorization) {
        JSONObject resp = new JSONObject();
        try {
            Integer checktoken = adminRepository.FindAdminByToken(Authorization);
            if (checktoken == 0) {
                resp.put("status", "fail");
                resp.put("message", "Token expired");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
            }

            Integer allgmail = accountRepository.getCountGmailLiveView();
            resp.put("counts", allgmail);
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/sumgmails", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> subgmails(@RequestHeader(defaultValue = "") String Authorization) {
        JSONObject resp = new JSONObject();
        try {
            Integer checktoken = adminRepository.FindAdminByToken(Authorization);
            if (checktoken == 0) {
                resp.put("status", "fail");
                resp.put("message", "Token expired");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
            }

            Integer allgmail = accountRepository.getCountGmailBuffh();
            resp.put("counts", allgmail);
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/resetaccountbyusername", produces = "application/hal_json;charset=utf8")
    ResponseEntity<String> resetaccountbyusername(@RequestParam(defaultValue = "") String username, @RequestParam(defaultValue = "0") Integer live,@RequestHeader(defaultValue = "") String Authorization) {
        JSONObject resp = new JSONObject();
        Integer checktoken = adminRepository.FindAdminByToken(Authorization);
        if (checktoken == 0) {

            resp.put("status", "fail");
            resp.put("message", "Token expired");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        try {
            if (username.length() == 0) {
                resp.put("status", "fail");
                resp.put("message", "username không được để trống!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
            }
            Long idUsername = accountRepository.findIdUsername(username.trim());
            Long idHistory=historyViewRepository.getId(username.trim());
            if(accountRepository.getProxyByUsername(username.trim().trim()).length()>4){
                proxyRepository.updaterunningProxy(accountRepository.getProxyByUsername(username.trim()));
            }
            historyViewRepository.deleteHistoryById(idHistory);
            accountRepository.resetAccountByUsername(live, idUsername);
            resp.put("status", "true");
            resp.put("message", "Reset Account thành công!");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);

        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/checkaccount", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> checkaccount(@RequestParam(defaultValue = "") String username, @RequestParam(defaultValue = "") String vps,@RequestHeader(defaultValue = "") String Authorization) {
        JSONObject resp = new JSONObject();
        Integer checktoken = adminRepository.FindAdminByToken(Authorization);
        if (checktoken == 0) {

            resp.put("status", "fail");
            resp.put("message", "Token expired");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        try {
            //Thread.sleep((long)(Math.random() * 10000));
            if (username.length() == 0) {
                resp.put("status", "fail");
                resp.put("message", "Username không được để trống!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
            }
            if (vps.length() == 0) {
                resp.put("status", "fail");
                resp.put("message", "Vps không được để trống!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
            }

            Long id_username = accountRepository.findIdByUsername(username);
            if (id_username == null) {
                resp.put("status", "fail");
                resp.put("fail", "nouser");
                resp.put("message", "Username không tồn tại!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }

            Integer accountcheck = accountRepository.checkIdByVps(id_username, "%" + vps.trim() + "%");
            if (accountcheck == 0) {
                resp.put("status", "fail");
                resp.put("fail", "nouser");
                resp.put("message", "Yều cầu lấy tài khoản khác");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            } else {
                accountRepository.updateTimecheckById(System.currentTimeMillis(), id_username);
                resp.put("status", "true");
                resp.put("message", "Check time user thành công!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/getinfo", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> getinfo(@RequestParam(defaultValue = "") String username,@RequestHeader(defaultValue = "") String Authorization) {
        JSONObject resp = new JSONObject();
        Integer checktoken = adminRepository.FindAdminByToken(Authorization);
        if (checktoken == 0) {

            resp.put("status", "fail");
            resp.put("message", "Token expired");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        try {
            if (username.length() == 0) {
                resp.put("status", "fail");
                resp.put("message", "Username không được để trống!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
            }
            Long idUsername = accountRepository.findIdUsername(username);
            if (idUsername == null) {
                resp.put("status", "fail");
                resp.put("message", "Username không tồn tại!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            } else {
                String account = accountRepository.getInfo(idUsername);
                String[] accountinfo = account.split(",");
                resp.put("status", "true");
                //resp.put("username",accounts.get(0).getUsername());
                resp.put("password", accountinfo[0]);
                resp.put("recover", accountinfo[1]);
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/getrecover", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> getrecover(@RequestHeader(defaultValue = "") String Authorization) {
        JSONObject resp = new JSONObject();
        Integer checktoken = adminRepository.FindAdminByToken(Authorization);
        if (checktoken == 0) {
            resp.put("status", "fail");
            resp.put("message", "Token expired");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        try {
            Recover recover = recoverRepository.getRecover();
            recover.setTimeget(System.currentTimeMillis());
            recover.setCount(recover.getCount()+1);
            recoverRepository.save(recover);
            resp.put("status", "true");
            resp.put("username", recover.getUsername());
            resp.put("password", recover.getPassword());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/checkrecover", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> checkrecover(@RequestParam(defaultValue = "") String recover,@RequestHeader(defaultValue = "") String Authorization) {
        JSONObject resp = new JSONObject();
        Integer checktoken = adminRepository.FindAdminByToken(Authorization);
        if (checktoken == 0) {
            resp.put("status", "fail");
            resp.put("message", "Token expired");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        try {
           if(recoverRepository.checkRecover(recover)==0){
               resp.put("status", "fail");
               resp.put("message","Recover không tồn tại!");
           }else{
               resp.put("status", "true");
               resp.put("message","Recover hợp lệ!");
           }
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/getinforecover", produces = "application/hal+json;charset=utf8")
    ResponseEntity<String> getinforecover(@RequestParam(defaultValue = "") String username,@RequestHeader(defaultValue = "") String Authorization) {
        JSONObject resp = new JSONObject();
        Integer checktoken = adminRepository.FindAdminByToken(Authorization);
        if (checktoken == 0) {
            resp.put("status", "fail");
            resp.put("message", "Token expired");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        try {
            if (username.length() == 0) {
                resp.put("status", "fail");
                resp.put("message", "Username không được để trống!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
            }
            Recover recover = recoverRepository.getInfoRecover(username);
            if (recover == null) {
                resp.put("status", "fail");
                resp.put("message", "Recover không tồn tại!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            } else {
                resp.put("status", "true");
                //resp.put("username",accounts.get(0).getUsername());
                resp.put("password", recover.getPassword());
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/update", produces = "application/hal_json;charset=utf8")
    ResponseEntity<String> update(@RequestParam(defaultValue = "") String username, @RequestParam(defaultValue = "") String password, @RequestParam(defaultValue = "") String recover,@RequestHeader(defaultValue = "") String Authorization) {
        JSONObject resp = new JSONObject();
        Integer checktoken = adminRepository.FindAdminByToken(Authorization);
        if (checktoken == 0) {

            resp.put("status", "fail");
            resp.put("message", "Token expired");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        try {
            if (username.length() == 0) {
                resp.put("status", "fail");
                resp.put("message", "Username không được để trống!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
            }
            Long idUsername = accountRepository.findIdUsername(username);
            if (idUsername == null) {
                resp.put("status", "fail");
                resp.put("message", "Username không tồn tại!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            } else {
                List<Account> accounts = accountRepository.findAccountById(idUsername);
                if (password.length() > 0) {
                    accounts.get(0).setPassword(password);
                }
                if (recover.length() > 0) {
                    accounts.get(0).setRecover(recover);
                }
                accountRepository.save(accounts.get(0));
                resp.put("status", "true");
                resp.put("message", "Update " + username + " thành công!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);
            }
        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }


    }

    @PostMapping(value = "/resetaccnotinvps", produces = "application/hal_json;charset=utf8")
    ResponseEntity<String> resetaccnotinvps(@RequestBody String listacc, @RequestHeader(defaultValue = "") String Authorization, @RequestParam(defaultValue = "") String vps) {
        JSONObject resp = new JSONObject();
        try {
            Integer checktoken = adminRepository.FindAdminByToken(Authorization);
            if (checktoken == 0) {
                resp.put("status", "fail");
                resp.put("message", "Token expired");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
            }
            if (vps.length() == 0) {
                resp.put("status", "fail");
                resp.put("message", "vps không đươc để trống!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
            }
            if (listacc.length() == 0 || listacc.isEmpty()) {
                resp.put("status", "fail");
                resp.put("message", "vps không đươc để trống!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
            }
            accountRepository.updateListAccount(vps.trim(), listacc);
            resp.put("status", "true");
            resp.put("message", listacc);
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);

        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/dellAccViewByVPS", produces = "application/hal_json;charset=utf8")
    ResponseEntity<String> dellAccViewByVPS(@RequestHeader(defaultValue = "") String Authorization, @RequestParam(defaultValue = "") String vps) {
        JSONObject resp = new JSONObject();
        try {
            Integer checktoken = adminRepository.FindAdminByToken(Authorization);
            if (checktoken == 0) {
                resp.put("status", "fail");
                resp.put("message", "Token expired");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
            }
            if (vps.length() == 0) {
                resp.put("status", "fail");
                resp.put("message", "vps không đươc để trống!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
            }
            proxyRepository.resetProxyViewByVPS(vps.trim());
            accountRepository.resetAccountViewByVps(vps.trim());
            historyViewRepository.resetHistoryViewByVps(vps.trim());
            resp.put("status", "true");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);

        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/dellAccCmtByVPS", produces = "application/hal_json;charset=utf8")
    ResponseEntity<String> dellAccCmtByVPS(@RequestHeader(defaultValue = "") String Authorization, @RequestParam(defaultValue = "") String vps) {
        JSONObject resp = new JSONObject();
        try {
            Integer checktoken = adminRepository.FindAdminByToken(Authorization);
            if (checktoken == 0) {
                resp.put("status", "fail");
                resp.put("message", "Token expired");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
            }
            if (vps.length() == 0) {
                resp.put("status", "fail");
                resp.put("message", "vps không đươc để trống!");
                return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
            }
            proxyRepository.resetProxyCmtByVPS(vps.trim());
            accountRepository.resetAccountCmtByVps(vps.trim());
            historyCommentRepository.resetThreadViewByVps(vps.trim());
            resp.put("status", "true");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);

        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/resetAccountByTimecheck", produces = "application/hal_json;charset=utf8")
    ResponseEntity<String> resetAccountByTimecheck() {
        JSONObject resp = new JSONObject();
        try {
            accountRepository.resetAccountByTimecheck();
            resp.put("status", "true");
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.OK);

        } catch (Exception e) {
            resp.put("status", "fail");
            resp.put("message", e.getMessage());
            return new ResponseEntity<String>(resp.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

}
