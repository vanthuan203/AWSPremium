package com.nts.awspremium;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StringUtils {
    public static long getLongTimeFromString(String dateTimeString,String format){
        SimpleDateFormat f = new SimpleDateFormat(format);
        try {
            Date d = f.parse(dateTimeString);
            long milliseconds = d.getTime();
            return milliseconds;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String cleanTitle(String title) {
        if (title == null) return null;

        // 1️⃣ Xóa toàn bộ ký tự không phải chữ, số hoặc khoảng trắng
        // Bao gồm emoji, icon, ký tự đặc biệt, ký hiệu tiền tệ, dấu câu, vv.
        title = title.replaceAll("[^\\p{L}\\p{N}\\s]", "");

        // 2️⃣ Xóa khoảng trắng dư thừa
        title = title.replaceAll("\\s{2,}", " ").trim();

        return title;
    }

    public static List<String> splitByWords(String input, int chunkSize) {
        String[] parts = input.split(" ");

        return IntStream.range(0, (parts.length + chunkSize - 1) / chunkSize)
                .mapToObj(i -> String.join(" ", Arrays.copyOfRange(
                        parts, i * chunkSize, Math.min(parts.length, (i + 1) * chunkSize)
                )))
                .collect(Collectors.toList());
    }

    public static String convertMMMtoMM(String mmm){
        try {
            if(mmm.contains("Jul")){
                return "07";
            }else if (mmm.contains("Feb")) {
                return "02";
            }else if(mmm.contains("Mar")){
                return "03";
            }else if(mmm.contains("Apr")){
                return "04";
            }else if(mmm.contains("May")){
                return "05";
            }else if(mmm.contains("Jun")){
                return "06";
            }else if(mmm.contains("Aug")){
                return "08";
            }else if(mmm.contains("Sep")){
                return "09";
            }else if(mmm.contains("Oct")){
                return "10";
            }else if(mmm.contains("Nov")){
                return "11";
            }else if(mmm.contains("Dec")){
                return "12";
            }

        } catch (Exception e) {
        }
        return "01";
    }
    public static String getProxyhost(String proxy){
        int indexport=proxy.indexOf(":");
        return proxy.substring(0,indexport)+"%";
    }
}
