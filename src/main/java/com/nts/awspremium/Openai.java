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
            jsonRequest.addProperty("model", "gpt-4.1");

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

    public static String checkVideo4(String message,String key) {

        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(300, TimeUnit.SECONDS) // Time to establish the connection
                    .readTimeout(600, TimeUnit.SECONDS)    // Time to read the response
                    .writeTimeout(600, TimeUnit.SECONDS)   // Time to write data to the server
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            JsonObject jsonRequest = new JsonObject();
            jsonRequest.addProperty("model", "gpt-4.1");

            // Create the messages array
            JsonArray messagesArray = new JsonArray();

            // First message (developer role)
            JsonObject developerMessage = new JsonObject();
            developerMessage.addProperty("role", "system");
            developerMessage.addProperty("content", "TASK:\n" +
                    "You are a video topic analyst.\n" +
                    "Based on the provided \"Title\" and \"Description\", analyze and determine the topic of the video according to the following rules:\n" +
                    "SCOPE OF APPLICATION:\n" +
                    "• Return True (violation) if the video has a topic that encourages, promotes or advertises ONLY the following activities:\n" +
                    "– Lottery, gambling or betting. Do not filter content such as: “Xổ số kien thiet”, “KQXS”, “XS miền Bắc/Nam/Trung”, or lottery results broadcasts, as they are legal.\n" +
                    "– Drugs (use, trade or production).\n" +
                    "– Politics (defamation of politicians and celebrities).\n" +
                    "• Return False (not a violation) for the remaining topics or for the above topics but with good purposes (entertainment, education, drug prevention propaganda...).\n" +
                    "OUTPUT FORMAT:\n" +
                    "Returns only one word: True or False for each case. Does not return any other information.");
            messagesArray.add(developerMessage);

            // Second message (user role)
            JsonObject userMessage = new JsonObject();
            userMessage.addProperty("role", "user");
            String antiCache = "\n\nRequest-ID: " + System.currentTimeMillis();
            userMessage.addProperty("content", message + antiCache);
            messagesArray.add(userMessage);

            // Add the messages array to the main JSON object
            jsonRequest.add("messages", messagesArray);

            RequestBody body = RequestBody.create(mediaType, jsonRequest.toString());
            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer "+key)
                    .addHeader("Cache-Control", "no-cache, no-store, must-revalidate")
                    .addHeader("Pragma", "no-cache")
                    .addHeader("Expires", "0")
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

    public static String checkVideo(String message,String key) {

        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(300, TimeUnit.SECONDS) // Time to establish the connection
                    .readTimeout(600, TimeUnit.SECONDS)    // Time to read the response
                    .writeTimeout(600, TimeUnit.SECONDS)   // Time to write data to the server
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            JsonObject jsonRequest = new JsonObject();
            jsonRequest.addProperty("model", "gpt-5");
            JsonObject reasoning = new JsonObject();
            reasoning.addProperty("effort", "minimal");
            jsonRequest.add("reasoning", reasoning);

            // Create the messages array
            JsonArray messagesArray = new JsonArray();

            // First message (developer role)
            JsonObject developerMessage = new JsonObject();
            developerMessage.addProperty("role", "developer");
            developerMessage.addProperty("content", "TASK:\\n\" +\n" +
                    "                    \"You are a video topic analyst.\\n\" +\n" +
                    "                    \"Based on the provided \\\"Title\\\" and \\\"Description\\\", analyze and determine the topic of the video according to the following rules:\\n\" +\n" +
                    "                    \"SCOPE OF APPLICATION:\\n\" +\n" +
                    "                    \"• Return True (violation) if the video has a topic that encourages, promotes or advertises ONLY the following activities:\\n\" +\n" +
                    "                    \"– Lottery, gambling or betting. Do not filter content such as: “Xổ số kien thiet”, “KQXS”, “XS miền Bắc/Nam/Trung”, or lottery results broadcasts, as they are legal.\\n\" +\n" +
                    "                    \"– Drugs (use, trade or production).\\n\" +\n" +
                    "                    \"– Politics (defamation of politicians and celebrities).\\n\" +\n" +
                    "                    \"• Return False (not a violation) for the remaining topics or for the above topics but with good purposes (entertainment, education, drug prevention propaganda...).\\n\" +\n" +
                    "                    \"OUTPUT FORMAT:\\n\" +\n" +
                    "                    \"Returns only one word: True or False for each case. Does not return any other information.");
            messagesArray.add(developerMessage);

            // Second message (user role)
            JsonObject userMessage = new JsonObject();
            userMessage.addProperty("role", "user");
            userMessage.addProperty("content", message);
            messagesArray.add(userMessage);

            // Add the messages array to the main JSON object
            jsonRequest.add("input", messagesArray);

            RequestBody body = RequestBody.create(mediaType, jsonRequest.toString());
            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/responses")
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
                String result = jsonObject.getAsJsonArray("output")
                        .get(1).getAsJsonObject()
                        .getAsJsonArray("content")
                        .get(0).getAsJsonObject()
                        .get("text").getAsString();
                return result;
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
            jsonRequest.addProperty("model", "gpt-4.1-mini");

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


    public static String createChatTask(String link) {

        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(300, TimeUnit.SECONDS) // Time to establish the connection
                    .readTimeout(600, TimeUnit.SECONDS)    // Time to read the response
                    .writeTimeout(600, TimeUnit.SECONDS)   // Time to write data to the server
                    .build();

            // Tạo JSON body bằng JSONObject
            JSONObject json = new JSONObject();
            json.put("youtube_url",link);

            // Request body
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());
            // Build request
            Request request = new Request.Builder()
                    .url("https://aicomment.yofatik.ai/api/v1/tasks")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer yV)}s%q^m$%$8eFZ}L,sspEa=reI~N7O")
                    .build();
            // Gửi request và lấy response
            Response response = client.newCall(request).execute();
            String resultJson = response.body().string();
            response.body().close();
            JsonObject jsonObject = JsonParser.parseString(resultJson).getAsJsonObject();
            return jsonObject.get("_id").getAsString();

        } catch (Exception e) {
            return null;
        }
    }

    public static String createChat(String task_id,Integer comment_count) {

        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(300, TimeUnit.SECONDS) // Time to establish the connection
                    .readTimeout(600, TimeUnit.SECONDS)    // Time to read the response
                    .writeTimeout(600, TimeUnit.SECONDS)   // Time to write data to the server
                    .build();

            // Tạo JSON body bằng JSONObject
            JSONObject json = new JSONObject();
            json.put("task_id",task_id);
            json.put("comment_count",comment_count);

            // Request body
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());
            // Build request
            Request request = new Request.Builder()
                    .url("https://aicomment.yofatik.ai/api/v1/chat")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer yV)}s%q^m$%$8eFZ}L,sspEa=reI~N7O")
                    .build();
            // Gửi request và lấy response
            Response response = client.newCall(request).execute();
            String resultJson = response.body().string();
            response.body().close();
            JsonObject jsonObject = JsonParser.parseString(resultJson).getAsJsonObject();
            return jsonObject.get("chat_id").getAsString();

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

    public static String stopTask(String uuid) {

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
                    .url("https://ai-comment.yofatik.ai/api/v1/tasks/stop?uuid="+uuid)
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

    public static String statusTaskLive(String uuid) {

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

    public static String[] getChatTask(String uuid) {

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
                    .url("https://aicomment.yofatik.ai/api/v1/chat/"+uuid+"/status")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer yV)}s%q^m$%$8eFZ}L,sspEa=reI~N7O")
                    .get().build();
            // Gửi request và lấy response
            Response response = client.newCall(request).execute();
            String resultJson = response.body().string();
            response.body().close();
            JsonObject jsonObject = JsonParser.parseString(resultJson).getAsJsonObject();
            return new String[] { jsonObject.get("status").getAsString(),jsonObject.get("comments")!=null?jsonObject.get("comments").getAsString():"" };
        } catch (Exception e) {
            return null;
        }
    }

}
