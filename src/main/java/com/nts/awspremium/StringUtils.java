package com.nts.awspremium;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
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

    public static String splitRandomClean(String input, int chunkSize) {
        // 1. Bỏ ký tự đặc biệt (chỉ giữ chữ, số, khoảng trắng)
        String cleaned = input.replaceAll("[^\\p{L}\\p{N}\\s]+", "").trim();

        // 2. Tách từ
        String[] parts = cleaned.split("\\s+");
        List<String> chunks = new ArrayList<>();

        // 3. Chia theo chunkSize
        for (int i = 0; i < parts.length; i += chunkSize) {
            int end = Math.min(i + chunkSize, parts.length);
            int wordCount = end - i;

            // Chỉ lấy nhóm từ >= 3 từ
            if (wordCount >= 3) {
                chunks.add(String.join(" ", Arrays.copyOfRange(parts, i, end)));
            }
        }

        // 4. Nếu không có nhóm nào hợp lệ
        if (chunks.isEmpty()) return cleaned;

        // 5. Random trả về 1 nhóm
        return chunks.get(new Random().nextInt(chunks.size()));
    }

    public static int calcMobilePercent(long lastUpdateMillis) {

        long now = System.currentTimeMillis();
        long hours = (now - lastUpdateMillis) / (1000 * 60 * 60);

        long h = hours % 10; // chu kỳ 10 giờ

        // 0–1h → 100%
        if (h < 2) return 100;

        // 2h → 95%
        if (h == 2) return 95;

        // 3–5h → giảm từ 90 → 85 → 80
        if (h >= 3 && h <= 5) {
            return 95 - (int)((h - 2) * 5); // 90, 85, 80
        }

        // 6–9h → tăng từ 85 → 90 → 95 → 100
        if (h >= 6 && h <= 9) {
            return 80 + (int)((h - 5) * 5); // 85, 90, 95, 100
        }

        return 100;
    }


    public static int calcMobilePercent1(long lastUpdateMillis) {
        long now = System.currentTimeMillis();
        long minutes = (now - lastUpdateMillis) / (1000 * 60); // tổng phút trôi qua
        long m = minutes % 480; // chu kỳ 480 phút = 8 giờ

        if (m < 120) return 100;       // 0–119 phút → 100%
        if (m < 150) return 95;        // 120–149 → 95%
        if (m < 210) return 100;       // 150–209 → 100%
        if (m < 240) return 90;        // 210–239 → 90%
        if (m < 300) return 100;       // 240–299 → 100%
        if (m < 330) return 85;        // 300–329 → 85%
        if (m < 390) return 100;       // 330–389 → 100%
        if (m < 420) return 80;        // 390–419 → 80%
        if (m < 480) return 100;       // 420–479 → 100%

        return 100; // fallback
    }

    public static int calcMobilePercent2(long lastUpdateMillis) {
        long now = System.currentTimeMillis();
        long minutes = (now - lastUpdateMillis) / (1000 * 60); // tổng phút trôi qua
        long m = minutes % 420; // chu kỳ 420 phút = 7 giờ

        if (m < 60) return 100;       // 0–59 → 100%
        if (m < 90) return 95;        // 60–89 → 95%
        if (m < 150) return 100;      // 90–149 → 100%
        if (m < 180) return 90;       // 150–179 → 90%
        if (m < 240) return 100;      // 180–239 → 100%
        if (m < 270) return 85;       // 240–269 → 85%
        if (m < 330) return 100;      // 270–329 → 100%
        if (m < 360) return 80;       // 330–359 → 80%
        if (m < 420) return 100;      // 360–419 → 100%

        return 100; // fallback
    }

    public static int calcMobilePercent3(long lastUpdateMillis, int currentPercent) {
        long now = System.currentTimeMillis();
        long minutesElapsed = (now - lastUpdateMillis) / (1000 * 60);

        // Thời gian giữ từng mốc
        int holdMinutes;
        if (currentPercent == 100) {
            holdMinutes = 60; // giữ 100% 60 phút
        } else {
            holdMinutes = 15; // các mốc khác giữ 15 phút
        }

        if (minutesElapsed < holdMinutes) {
            // Chưa đủ thời gian giữ -> trả về % hiện tại
            return currentPercent;
        }

        // Chuyển sang mốc tiếp theo theo chu kỳ
        int nextPercent;
        switch (currentPercent) {
            case 100: nextPercent = 95; break;
            case 95:  nextPercent = 100; break;
            case 90:  nextPercent = 100; break;
            case 85:  nextPercent = 100; break;
            case 80:  nextPercent = 100; break;
            default:  nextPercent = 100; break;
        }

        return nextPercent;
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
