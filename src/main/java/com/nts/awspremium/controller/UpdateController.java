package com.nts.awspremium.controller;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/app")
public class UpdateController {

    private final String appDirectory = "/root/autoView"; // thư mục chứa các file .exe

    @GetMapping("/check-update")
    public ResponseEntity<?> checkForUpdate(@RequestParam String currentVersion) {
        File folder = new File(appDirectory);
        File[] files = folder.listFiles((dir, name) -> name.matches(".*_v\\d+\\.\\d+\\.\\d+\\.exe$"));

        if (files == null || files.length == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No update files found.");
        }

        // tìm file mới nhất
        File latestFile = Arrays.stream(files)
                .max(Comparator.comparing(UpdateController::extractVersion))
                .orElse(null);

        if (latestFile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No valid update file.");
        }

        String latestVersion = extractVersion(latestFile);

        if (compareVersion(latestVersion, currentVersion) > 0) {
            // nếu version mới hơn => trả file
            Resource resource = new FileSystemResource(latestFile);
            if (!((FileSystemResource) resource).exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Update file not found.");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment().filename(latestFile.getName()).build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } else {
            // không cần update => trả JSON
            return ResponseEntity.ok(Collections.singletonMap("update", false));
        }
    }

    private static String extractVersion(File file) {
        String name = file.getName();
        Matcher matcher = Pattern.compile("_v(\\d+\\.\\d+\\.\\d+)").matcher(name);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "0.0.0";
    }

    private static int compareVersion(String v1, String v2) {
        int[] p1 = Arrays.stream(v1.split("\\.")).mapToInt(Integer::parseInt).toArray();
        int[] p2 = Arrays.stream(v2.split("\\.")).mapToInt(Integer::parseInt).toArray();
        for (int i = 0; i < Math.min(p1.length, p2.length); i++) {
            if (p1[i] != p2[i]) return p1[i] - p2[i];
        }
        return p1.length - p2.length;
    }
}