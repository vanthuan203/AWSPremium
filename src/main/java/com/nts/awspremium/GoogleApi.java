package com.nts.awspremium;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import okhttp3.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.StringReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleApi {

    public static String getYoutubeId(String url) {
        String pattern = "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";

        Pattern compiledPattern = Pattern.compile(pattern,
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }/*from w  w  w.  j a  va  2 s .c om*/
        return null;
    }

    public static Integer getCountLike(String order_key,String key){
        try {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
            Random ran = new Random();
            Request request = null;
            Iterator k = null;
            request = new Request.Builder().url("https://www.googleapis.com/youtube/v3/videos?key="+key.trim()+"&fields=items(statistics(likeCount))&part=statistics&id=" + order_key).get().build();
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                String resultJson1 = response.body().string();
                Object obj1 = new JSONParser().parse(resultJson1);
                JSONObject jsonObject1 = (JSONObject) obj1;
                JSONArray items = (JSONArray) jsonObject1.get("items");
                if (items == null) {
                    return -2;
                }
                k = items.iterator();
                if (k.hasNext() == false) {
                    return -1;
                }
                JSONObject video = (JSONObject) k.next();
                JSONObject statistics = (JSONObject) video.get("statistics");
                return Integer.parseInt(statistics.get("likeCount").toString());
            }else{
                return -2;
            }
        } catch (IOException | ParseException e) {
            return -2;
        }

    }

    public static String[] get_Content_Comment(String lc,String key){
        try {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
            Random ran = new Random();
            Request request = null;
            Iterator k = null;
            request = new Request.Builder().url("https://www.googleapis.com/youtube/v3/comments?fields=items(snippet(textDisplay,authorDisplayName))&part=snippet&key="+key.trim()+"&textFormat=plainText&id=" + lc).get().build();
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                String resultJson1 = response.body().string();
                Object obj1 = new JSONParser().parse(resultJson1);
                JSONObject jsonObject1 = (JSONObject) obj1;
                JSONArray items = (JSONArray) jsonObject1.get("items");
                if (items == null) {
                    return null;
                }
                k = items.iterator();
                if (k.hasNext() == false) {
                    return null;
                }
                JSONObject video = (JSONObject) k.next();
                JSONObject snippet = (JSONObject) video.get("snippet");
                return new String[] {
                        snippet.get("authorDisplayName").toString(),
                        snippet.get("textDisplay").toString()
                };
            }else{
                return null;
            }
        } catch (IOException | ParseException e) {
            return null;
        }

    }

    public static Integer getCountSubcriber(String order_key,String key){
        try {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
            Random ran = new Random();
            Request request = null;
            Iterator k = null;
            request = new Request.Builder().url("https://www.googleapis.com/youtube/v3/channels?key="+key.trim()+"&fields=items(statistics(subscriberCount))&part=statistics&id=" + order_key).get().build();
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                String resultJson1 = response.body().string();
                Object obj1 = new JSONParser().parse(resultJson1);
                JSONObject jsonObject1 = (JSONObject) obj1;
                JSONArray items = (JSONArray) jsonObject1.get("items");
                if (items == null) {
                    return -2;
                }
                k = items.iterator();
                if (k.hasNext() == false) {
                    return -1;
                }
                JSONObject video = (JSONObject) k.next();
                JSONObject statistics = (JSONObject) video.get("statistics");
                return Integer.parseInt(statistics.get("subscriberCount").toString());
            }else{
                return -2;
            }
        } catch (IOException | ParseException e) {
            return -2;
        }

    }

    public static Integer getCountComment(String order_key,String key){
        try {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
            Random ran = new Random();
            Request request = null;
            Iterator k = null;
            request = new Request.Builder().url("https://www.googleapis.com/youtube/v3/videos?key="+key.trim()+"&fields=items(statistics(commentCount))&part=statistics&id=" + order_key).get().build();
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                String resultJson1 = response.body().string();
                response.body().close();
                Object obj1 = new JSONParser().parse(resultJson1);
                JSONObject jsonObject1 = (JSONObject) obj1;
                JSONArray items = (JSONArray) jsonObject1.get("items");
                if (items == null) {
                    return -2;
                }
                k = items.iterator();
                if (k.hasNext() == false) {
                    return -1;
                }
                JSONObject video = (JSONObject) k.next();
                JSONObject statistics = (JSONObject) video.get("statistics");
                return Integer.parseInt(statistics.get("commentCount").toString());
            }else{
                response.body().close();
                return -2;
            }
        } catch (IOException | ParseException e) {
            return -2;
        }

    }

    public static Integer getCountView(String order_key,String key){
        try {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
            Random ran = new Random();
            Request request = null;
            Iterator k = null;
            request = new Request.Builder().url("https://www.googleapis.com/youtube/v3/videos?key="+key.trim()+"&fields=items(statistics(viewCount))&part=statistics&id=" + order_key).get().build();
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                String resultJson1 = response.body().string();
                response.body().close();
                Object obj1 = new JSONParser().parse(resultJson1);
                JSONObject jsonObject1 = (JSONObject) obj1;
                JSONArray items = (JSONArray) jsonObject1.get("items");
                if (items == null) {
                    return -2;
                }
                k = items.iterator();
                if (k.hasNext() == false) {
                    return -1;
                }
                JSONObject video = (JSONObject) k.next();
                JSONObject statistics = (JSONObject) video.get("statistics");
                return Integer.parseInt(statistics.get("viewCount").toString());
            }else{
                response.body().close();
                return -2;
            }
        } catch (IOException | ParseException e) {
            return -2;
        }

    }
    public static Integer getCountSubcriberCurrent(String order_key){
        try {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
            Request request = null;
            request = new Request.Builder().url("https://api.socialcounts.org/youtube-live-subscriber-count/" + order_key).get().build();
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                String resultJson1 = response.body().string();
                response.body().close();
                Object obj1 = new JSONParser().parse(resultJson1);
                JSONObject jsonObject1 = (JSONObject) obj1;
                return Integer.parseInt(jsonObject1.get("est_sub").toString());
            }else{
                response.body().close();
                return -2;
            }
        } catch (IOException | ParseException e) {
            return -2;
        }

    }


    public static String checkComment(String video_id) {

        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            Request request = new Request.Builder()
                    .url("https://yt-api.p.rapidapi.com/comments?id="+video_id)
                    .addHeader("x-rapidapi-host", "yt-api.p.rapidapi.com")
                    .addHeader("x-rapidapi-key","4010c38bfamsh398346af7e9f654p1492c2jsn20af8f084b5a")
                    .addHeader("X-CACHEBYPASS", "1")
                    .get().build();
            Response response = client.newCall(request).execute();
            String resultJson = response.body().string();
            response.body().close();
            JsonObject jsonObject = JsonParser.parseString(resultJson).getAsJsonObject();

            // Kiểm tra nếu msg là "success"
            if (!jsonObject.get("commentsCount").isJsonNull()) {
                // Lấy followerCount từ data.stats
                JsonArray jsonData = jsonObject.getAsJsonArray("data");
                if(jsonData.isJsonNull()){
                    return null;
                }else{
                    StringBuilder sb = new StringBuilder();
                    int limit = Math.min(30, jsonData.size()); // lấy tối đa 30
                    for (int i = 0; i < limit; i++) {
                        JsonObject item = jsonData.get(i).getAsJsonObject();
                        if (sb.length() > 0) sb.append(",");
                        sb.append(item.get("authorText").getAsString());
                    }
                    //System.out.println("Authors: " + sb.toString());
                    return sb.toString();

                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static String getListVideo(String channel_id) {

        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            Request request = new Request.Builder()
                    .url("https://yt-api.p.rapidapi.com/channel/videos?id="+channel_id+"&sort_by=oldest")
                    .addHeader("x-rapidapi-host", "yt-api.p.rapidapi.com")
                    .addHeader("x-rapidapi-key","4010c38bfamsh398346af7e9f654p1492c2jsn20af8f084b5a")
                    .addHeader("X-CACHEBYPASS", "1")
                    .get().build();
            Response response = client.newCall(request).execute();
            String resultJson = response.body().string();
            response.body().close();
            JsonObject jsonObject = JsonParser.parseString(resultJson).getAsJsonObject();

            // Kiểm tra nếu msg là "success"
            if (!jsonObject.get("data").isJsonNull()) {
                // Lấy followerCount từ data.stats
                JsonArray jsonData = jsonObject.getAsJsonArray("data");
                if(jsonObject.get("msg").toString().length()!=0 &&jsonData.size()==0){
                    return "del";
                }
                if(jsonData.isJsonNull()){
                    return null;
                }else{
                    StringBuilder sb = new StringBuilder();
                    int limit = Math.min(30, jsonData.size()); // lấy tối đa 30
                    for (int i = 0; i < limit; i++) {
                        JsonObject item = jsonData.get(i).getAsJsonObject();
                        if (System.currentTimeMillis() - Instant.parse(item.get("publishedAt").getAsString()).toEpochMilli() < 7L * 24 * 60 * 60 * 1000) {
                            continue;
                        }
                        if (sb.length() > 0) sb.append("#end");
                        String video_info=item.get("videoId").getAsString()+"#video"+item.get("title").getAsString();
                        sb.append(video_info);
                    }
                    //System.out.println("Authors: " + sb.toString());
                    return sb.toString();

                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }


    public static Integer getCountLikeCurrent(String order_key){
        try {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
            Request request = null;
            request = new Request.Builder().url("https://api.socialcounts.org/youtube-video-live-view-count/" + order_key).get().build();
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                String resultJson = response.body().string();
                response.body().close();
                Object obj = new JsonParser().parse(resultJson);
                JsonObject jsonObject = (JsonObject) obj;

                // Get the table array
                JsonArray tableArray = jsonObject.getAsJsonArray("table");

                // Iterate through the table array to find the Like Count
                for (JsonElement element : tableArray) {
                    JsonObject tableItem = element.getAsJsonObject();
                    if (tableItem.get("name").getAsString().equals("Like Count")) {
                        return  tableItem.get("count").getAsInt();
                    }
                }
                return -2;
            }else{
                response.body().close();
                return -2;
            }
        } catch (Exception e) {
            return -2;
        }
    }
    public static String getChannelId(String channelUrl) {
        try {
            // Kết nối tới trang YouTube và lấy nội dung trang
            Document doc = Jsoup.connect(channelUrl).get();
            // Tìm tất cả các thẻ <script> chứa đoạn JSON
            Elements scriptElements = doc.select("script");
            for (Element scriptElement : scriptElements) {
                String scriptContent = scriptElement.html();
                if (scriptContent.contains("responseContext")) {
                    // Lấy phần JSON trong nội dung của thẻ script
                    int startIndex = scriptContent.indexOf("{");
                    int endIndex = scriptContent.lastIndexOf("}") + 1;
                    String jsonString = scriptContent.substring(startIndex, endIndex);
                    //System.out.println(jsonString);
                    JsonReader reader = new JsonReader(new StringReader(jsonString));
                    reader.setLenient(true);
                    JsonElement jsonElement = JsonParser.parseReader(reader);
                    JsonObject jsonObject =  jsonElement.getAsJsonObject();
                    JsonObject jsonElement11 =  jsonObject.getAsJsonObject("metadata");
                    String id = jsonObject.getAsJsonObject("metadata")
                            .getAsJsonObject("channelMetadataRenderer")
                            .get("title").toString().replace("\"","");
                    String chann = jsonObject.getAsJsonObject("metadata")
                            .getAsJsonObject("channelMetadataRenderer")
                            .get("externalId").toString().replace("\"","");
                    return id+","+chann;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getFacebook(String link) {
        try {
            // Kết nối tới trang YouTube và lấy nội dung trang
            Document doc = Jsoup.connect(link)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000)
                    .get();
            Elements posts = doc.select(".post");
            // Tìm tất cả các thẻ <script> chứa đoạn JSON
            Elements scriptElements = doc.selectXpath("script");
            for (Element scriptElement : scriptElements) {
                String scriptContent = scriptElement.html();
                if (scriptContent.contains("responseContext")) {
                    // Lấy phần JSON trong nội dung của thẻ script
                    int startIndex = scriptContent.indexOf("{");
                    int endIndex = scriptContent.lastIndexOf("}") + 1;
                    String jsonString = scriptContent.substring(startIndex, endIndex);
                    JsonReader reader = new JsonReader(new StringReader(jsonString));
                    reader.setLenient(true);
                    JsonElement jsonElement = JsonParser.parseReader(reader);
                    JsonObject jsonObject =  jsonElement.getAsJsonObject();
                    JsonObject jsonElement11 =  jsonObject.getAsJsonObject("metadata");
                    String id = jsonObject.getAsJsonObject("metadata")
                            .getAsJsonObject("channelMetadataRenderer")
                            .get("title").toString().replace("\"","");
                    String chann = jsonObject.getAsJsonObject("metadata")
                            .getAsJsonObject("channelMetadataRenderer")
                            .get("externalId").toString().replace("\"","");
                    return id+","+chann;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static List<String> getVideoLinks(String channelUrl) {
        List<String> videoList = new ArrayList<>();

        try {
            // Kết nối tới trang YouTube và lấy nội dung trang
            Document doc = Jsoup.connect(channelUrl).get();

            // Tìm tất cả các thẻ <script> chứa đoạn JSON
            Elements scriptElements = doc.select("script");
            for (Element scriptElement : scriptElements) {
                String scriptContent = scriptElement.html();
                if (scriptContent.contains("videoRenderer")) {
                    // Lấy phần JSON trong nội dung của thẻ script
                    int startIndex = scriptContent.indexOf("{");
                    int endIndex = scriptContent.lastIndexOf("}") + 1;
                    String jsonString = scriptContent.substring(startIndex, endIndex);
                    //System.out.println(jsonString);
                    // Phân tích cú pháp JSON và trích xuất videoId
                    JsonReader reader = new JsonReader(new StringReader(jsonString));
                    reader.setLenient(true);
                    JsonElement jsonElement = JsonParser.parseReader(reader);
                    JsonObject jsonObject =  jsonElement.getAsJsonObject();
                    //System.out.println(jsonObject);
                    JsonArray items = jsonObject.getAsJsonObject("contents")
                            .getAsJsonObject("twoColumnBrowseResultsRenderer")
                            .getAsJsonArray("tabs");
                    int index=0;
                    JsonArray item_video=null;
                    while(index<2){
                        try{
                            index=index+1;
                            item_video=items.get(index-1).getAsJsonObject().
                                    getAsJsonObject("tabRenderer")
                                    .getAsJsonObject("content")
                                    .getAsJsonObject("richGridRenderer")
                                    .getAsJsonArray("contents");
                            break;
                        }catch (Exception e){
                            continue;
                        }
                    }

                    if(item_video.size()>0){
                        for (int i=0;i<item_video.size();i++){
                            try{
                                String video_id=item_video.get(i).getAsJsonObject().
                                        getAsJsonObject("richItemRenderer")
                                        .getAsJsonObject("content")
                                        .getAsJsonObject("videoRenderer").get("videoId").toString().replace("\"","");
                                String video_title=item_video.get(i).getAsJsonObject().
                                        getAsJsonObject("richItemRenderer")
                                        .getAsJsonObject("content")
                                        .getAsJsonObject("videoRenderer")
                                        .getAsJsonObject("title")
                                        .getAsJsonArray("runs").get(0).getAsJsonObject().get("text").toString().replace("\"","");
                                String video_duration=item_video.get(i).getAsJsonObject().
                                        getAsJsonObject("richItemRenderer")
                                        .getAsJsonObject("content")
                                        .getAsJsonObject("videoRenderer")
                                        .getAsJsonObject("lengthText").get("simpleText").toString().replace("\"","");
                                String[] duration_Text=video_duration.toString().split(":");
                                Long duration=0L;
                                if(duration_Text.length==4){
                                    duration=Long.parseLong(duration_Text[0].trim())*3600*24+Long.parseLong(duration_Text[0].trim())*3600+Long.parseLong(duration_Text[1].trim())*60+Long.parseLong(duration_Text[2].trim());
                                }else if(duration_Text.length==3){
                                    duration=Long.parseLong(duration_Text[0].trim())*3600+Long.parseLong(duration_Text[1].trim())*60+Long.parseLong(duration_Text[2].trim());
                                }else if(duration_Text.length==2){
                                    duration=Long.parseLong(duration_Text[0].trim())*60+Long.parseLong(duration_Text[1].trim());
                                    if(duration<90){
                                        continue;
                                    }
                                }else{
                                    continue;
                                }
                                videoList.add(video_id+"~#"+video_title+"~#"+duration) ;
                                if(videoList.size()>=5){
                                    break;
                                }
                            }catch (Exception e){

                            }

                        }
                    }
                }
            }
            return videoList;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return videoList;
    }
}
