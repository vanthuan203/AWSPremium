package com.nts.awspremium;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Openai {
    public static String chatGPT(String message,String key) {

        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(300, TimeUnit.SECONDS) // Time to establish the connection
                    .readTimeout(600, TimeUnit.SECONDS)    // Time to read the response
                    .writeTimeout(600, TimeUnit.SECONDS)   // Time to write data to the server
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            JsonObject jsonRequest = new JsonObject();
            jsonRequest.addProperty("model", "gpt-4o");

            // Create the messages array
            JsonArray messagesArray = new JsonArray();

            // First message (developer role)
            JsonObject developerMessage = new JsonObject();
            developerMessage.addProperty("role", "developer");
            developerMessage.addProperty("content", "You are a helpful assistant.");
            messagesArray.add(developerMessage);

            // Second message (user role)
            JsonObject userMessage = new JsonObject();
            userMessage.addProperty("role", "user");
            userMessage.addProperty("content", message);
            messagesArray.add(userMessage);

            // Add the messages array to the main JSON object
            jsonRequest.add("messages", messagesArray);

            RequestBody body = RequestBody.create(mediaType, jsonRequest.toString());
            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer "+key)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String resultJson = response.body().string();
                response.body().close();
                Object obj = new JsonParser().parse(resultJson);
                JsonObject jsonObject = JsonParser.parseString(resultJson).getAsJsonObject();
                JsonArray choicesArray = jsonObject.getAsJsonArray("choices");
                if (choicesArray != null && choicesArray.size() > 0) {
                    // Lấy phần tử đầu tiên trong mảng "choices"
                    JsonObject firstChoice = choicesArray.get(0).getAsJsonObject();

                    // Truy cập đối tượng "message"
                    JsonObject messageObject = firstChoice.getAsJsonObject("message");
                    if (messageObject != null) {
                        // Lấy giá trị của trường "content"
                        String content = messageObject.get("content").getAsString();
                        return content;
                    } else {
                        return null;
                    }
                }
                // Iterate through the table array to find the Like Count
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public static String getCaptions(String video_id) {

        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            Request request = new Request.Builder()
                    .url("https://youtube-captions-transcript-subtitles-video-combiner.p.rapidapi.com/download-webvtt/"+video_id+"?language=en")
                    .addHeader("x-rapidapi-host", "youtube-captions-transcript-subtitles-video-combiner.p.rapidapi.com")
                    .addHeader("x-rapidapi-key", "4010c38bfamsh398346af7e9f654p1492c2jsn20af8f084b5a")
                    .get().build();
            Response response = client.newCall(request).execute();
            String resultJson = response.body().string();
            if(response.code()==200){
                StringBuilder result = new StringBuilder();
                String[] lines = resultJson.split("\\R");

                for (String line : lines) {
                    line = line.trim();
                    // Bỏ qua dòng trống, timestamp và "WEBVTT"
                    if (line.isEmpty() || line.equals("WEBVTT") || line.matches("\\d{2}:\\d{2}:\\d{2}\\.\\d{3} --> .*")) {
                        continue;
                    }
                    result.append(line).append("\n");
                }

                return result.toString().trim();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }


    public static String chatGPT4oMini(String message,String key) {

        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(300, TimeUnit.SECONDS) // Time to establish the connection
                    .readTimeout(600, TimeUnit.SECONDS)    // Time to read the response
                    .writeTimeout(600, TimeUnit.SECONDS)   // Time to write data to the server
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            JsonObject jsonRequest = new JsonObject();
            jsonRequest.addProperty("model", "gpt-4o-mini");

            // Create the messages array
            JsonArray messagesArray = new JsonArray();

            // First message (developer role)
            JsonObject developerMessage = new JsonObject();
            developerMessage.addProperty("role", "developer");
            developerMessage.addProperty("content", "You are a helpful assistant.");
            messagesArray.add(developerMessage);

            // Second message (user role)
            JsonObject userMessage = new JsonObject();
            userMessage.addProperty("role", "user");
            userMessage.addProperty("content", message);
            messagesArray.add(userMessage);

            // Add the messages array to the main JSON object
            jsonRequest.add("messages", messagesArray);

            RequestBody body = RequestBody.create(mediaType, jsonRequest.toString());
            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer "+key)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String resultJson = response.body().string();
                response.body().close();
                Object obj = new JsonParser().parse(resultJson);
                JsonObject jsonObject = JsonParser.parseString(resultJson).getAsJsonObject();
                JsonArray choicesArray = jsonObject.getAsJsonArray("choices");
                if (choicesArray != null && choicesArray.size() > 0) {
                    // Lấy phần tử đầu tiên trong mảng "choices"
                    JsonObject firstChoice = choicesArray.get(0).getAsJsonObject();

                    // Truy cập đối tượng "message"
                    JsonObject messageObject = firstChoice.getAsJsonObject("message");
                    if (messageObject != null) {
                        // Lấy giá trị của trường "content"
                        String content = messageObject.get("content").getAsString();
                        return content;
                    } else {
                        return null;
                    }
                }
                // Iterate through the table array to find the Like Count
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public static String chatGPT1(String path,String key) {

        File audioFile = new File(path); // Đường dẫn file cần dịch

        try {
            OkHttpClient client = new OkHttpClient();

            // Tạo body cho file upload
            RequestBody fileBody = RequestBody.create(MediaType.parse("audio/mpeg"), audioFile);

            // Tạo request body (multipart form-data)
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("model", "whisper-1")  // Chọn mô hình Whisper
                    .addFormDataPart("response_format", "text")  // Chọn mô hình Whisper
                    .addFormDataPart("file", audioFile.getName(), fileBody)
                    .build();

            // Tạo request gửi đến OpenAI
            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/audio/translations") //transcriptions
                    .post(requestBody)
                    .addHeader("Authorization", "Bearer " + key)
                    .build();

            // Gửi request và lấy response
            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
               return null;
            }

        } catch (IOException e) {
           return null;
        }
    }


    public static String createTask(String link,Integer quantity,String platform,String task,Integer priority,String video_title,String channel_title,String video_description) {

        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(300, TimeUnit.SECONDS) // Time to establish the connection
                    .readTimeout(600, TimeUnit.SECONDS)    // Time to read the response
                    .writeTimeout(600, TimeUnit.SECONDS)   // Time to write data to the server
                    .build();

            // Tạo JSON body bằng JSONObject
            JSONObject json = new JSONObject();
            json.put("url",link);
            json.put("channel_title", channel_title);
            json.put("video_title", video_title);
            json.put("video_description", video_description);
            json.put("quantity", quantity);
            json.put("platform", platform);
            json.put("task", task);
            json.put("priority", priority);

            // Request body
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());
            // Build request
            Request request = new Request.Builder()
                    .url("https://ai-comment.yofatik.ai/api/v1/tasks/create")
                    .post(body)
                    .build();
            // Gửi request và lấy response
            Response response = client.newCall(request).execute();
            String resultJson = response.body().string();
            response.body().close();
            JsonObject jsonObject = JsonParser.parseString(resultJson).getAsJsonObject();
            return jsonObject.get("uuid").getAsString();

        } catch (Exception e) {
            return null;
        }
    }

    public static String statusTask(String uuid) {

        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(300, TimeUnit.SECONDS) // Time to establish the connection
                    .readTimeout(600, TimeUnit.SECONDS)    // Time to read the response
                    .writeTimeout(600, TimeUnit.SECONDS)   // Time to write data to the server
                    .build();

            // Tạo request body rỗng
            RequestBody emptyBody = RequestBody.create(MediaType.parse("application/json"), "");

            // Gửi POST không có body
            Request request = new Request.Builder()
                    .url("https://ai-comment.yofatik.ai/api/v1/tasks/status?uuid="+uuid)
                    .post(emptyBody)
                    .build();
            // Gửi request và lấy response
            Response response = client.newCall(request).execute();
            String resultJson = response.body().string();
            response.body().close();
            JsonObject jsonObject = JsonParser.parseString(resultJson).getAsJsonObject();
            return jsonObject.get("status").getAsString();

        } catch (Exception e) {
            return null;
        }
    }

    public static String getTask(String uuid) {

        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(300, TimeUnit.SECONDS) // Time to establish the connection
                    .readTimeout(600, TimeUnit.SECONDS)    // Time to read the response
                    .writeTimeout(600, TimeUnit.SECONDS)   // Time to write data to the server
                    .build();

            // Tạo request body rỗng
            RequestBody emptyBody = RequestBody.create(MediaType.parse("application/json"), "");

            // Gửi POST không có body
            Request request = new Request.Builder()
                    .url("https://ai-comment.yofatik.ai/api/v1/tasks/get?uuid="+uuid)
                    .get().build();
            // Gửi request và lấy response
            Response response = client.newCall(request).execute();
            String resultJson = response.body().string();
            response.body().close();
            JsonObject jsonObject = JsonParser.parseString(resultJson).getAsJsonObject();
            return jsonObject.get("cmt").getAsString();

        } catch (Exception e) {
            return null;
        }
    }

}
