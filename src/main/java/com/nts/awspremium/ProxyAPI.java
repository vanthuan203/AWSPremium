package com.nts.awspremium;

import okhttp3.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.*;
import java.net.Authenticator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ProxyAPI {
    public static boolean checkProxy(String proxycheck) {
        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
        //System.setProperty("jdk.http.auth.proxying.disabledSchemes", "");
        String[] proxycut = proxycheck.split(":");

        try {
            //System.out.println(proxycut[0]+":"+proxycut[1]+":"+proxycut[2]+":"+ proxycut[3]);
            URL url = new URL("https://www.google.com/");
            java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxycut[0], Integer.parseInt(proxycut[1])));
            if (proxycut.length > 2) {

                Authenticator authenticator = new Authenticator() {
                    public PasswordAuthentication getPasswordAuthentication() {
                        return (new PasswordAuthentication(proxycut[2],
                                proxycut[3].toCharArray()));
                    }
                };
                Authenticator.setDefault(authenticator);
            }
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            conn.connect();
            int code = conn.getResponseCode();
            //System.out.println("Status:"+proxycut+" - "+code);
            //String contents = conn.getResponseMessage();
            //System.out.println("Status:"+contents);
            conn.disconnect();
            if (code == 200 || code == 429 || code ==404) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            //System.out.println(e);
            if(e.toString().indexOf("Authentication")>=0){
                return true;
            }
            return false;
        }
    }

    public static boolean checkProxy(String proxycheck,String link) {
        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
        //System.setProperty("jdk.http.auth.proxying.disabledSchemes", "");
        String[] proxycut = proxycheck.split(":");

        try {
            //System.out.println(proxycut[0]+":"+proxycut[1]+":"+proxycut[2]+":"+ proxycut[3]);
            URL url = new URL(link);
            java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxycut[0], Integer.parseInt(proxycut[1])));
            if (proxycut.length > 2) {

                Authenticator authenticator = new Authenticator() {
                    public PasswordAuthentication getPasswordAuthentication() {
                        return (new PasswordAuthentication(proxycut[2],
                                proxycut[3].toCharArray()));
                    }
                };
                Authenticator.setDefault(authenticator);
            }
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            conn.connect();
            int code = conn.getResponseCode();
            //System.out.println("Status:"+proxycut+" - "+code);
            //String contents = conn.getResponseMessage();
            //System.out.println("Status:"+contents);
            conn.disconnect();
            if (code == 200  || code ==404) { //|| code == 429
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            //System.out.println(e);
            /*
            if(e.toString().indexOf("Authentication")>=0){
                return true;
            }
             */
            return false;
        }
    }


    public static void run_Reboot_VPS(String instance_id){
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("https://api.vultr.com/v2/instances/"+instance_id+"/reboot")
                    .method("POST", body)
                    .addHeader("Authorization", "Bearer BXAVWAY2QRCMR72RFHSZIDD3N72SU3FNG3QA")
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();
        } catch (Exception e) {
        }
    }

    public static List<List<String>> instances_VPS_VULTR(){
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("https://api.vultr.com/v2/instances")
                    .addHeader("Authorization", "Bearer BXAVWAY2QRCMR72RFHSZIDD3N72SU3FNG3QA")
                    .addHeader("Content-Type", "application/json").get()
                    .build();
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                String resultJson1 = response.body().string();
                Object obj1 = new JSONParser().parse(resultJson1);
                JSONObject jsonObject1 = (JSONObject) obj1;
                JSONArray instances = (JSONArray) jsonObject1.get("instances");
                List<List<String>> resultList = new ArrayList<>();
                for (Object obj : instances) {
                    JSONObject instance = (JSONObject) obj;
                    String id = (String) instance.get("id");
                    String mainIp = (String) instance.get("main_ip");
                    List<String> pair = new ArrayList<>();
                    pair.add(id);
                    pair.add(mainIp);
                    resultList.add(pair);
                }
                return  resultList;
            }else{
                return null;
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static boolean checkResponseCode (String link) {
        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
        //System.setProperty("jdk.http.auth.proxying.disabledSchemes", "");

        try {
            URL url = new URL(link.trim());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("User-Agent", "Mozilla");
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(1000);

            conn.connect();
            //System.out.println(proxycut[0]+":"+proxycut[1]+":"+proxycut[2]+":"+ proxycut[3]);
            int code = conn.getResponseCode();
            //String contents = conn.getResponseMessage();
            conn.disconnect();
            if (code == 200 || code == 429) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
