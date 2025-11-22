package com.nts.awspremium.controller;

import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/finger")
public class FingerController {


    @GetMapping(value = "random")
    public ResponseEntity<org.springframework.core.io.Resource> random(@RequestParam(defaultValue = "28") String ver) {
        try {
            Path imageDir = Paths.get("/root/data/finger"+ver).toAbsolutePath().normalize();
            // Lấy danh sách file trong thư mục
            List<Path> files = Files.list(imageDir)
                    .filter(Files::isRegularFile)
                    .filter(p -> {
                        String name = p.getFileName().toString().toLowerCase();
                        return name.endsWith(".txt");
                    })
                    .collect(Collectors.toList());

            if (files.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Chọn ngẫu nhiên
            Path randomFile = files.get(new Random().nextInt(files.size()));
            org.springframework.core.io.Resource resource = new UrlResource(randomFile.toUri());

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (IOException e) {
            throw new RuntimeException("Không thể đọc file.", e);
        }
    }


    @PostMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadTxt(
            @RequestParam("file") MultipartFile file) {

        try {
            // Kiểm tra định dạng txt
            if (file.isEmpty() || !file.getOriginalFilename().toLowerCase().endsWith(".txt")) {
                return ResponseEntity.badRequest().body("Chỉ được upload file .txt");
            }

            // Thư mục lưu
            Path uploadDir = Paths.get("/root/data").toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);

            // Đường dẫn file lưu
            Path targetFile = uploadDir.resolve(file.getOriginalFilename());

            // Lưu file
            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("Upload thành công: " + file.getOriginalFilename());

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Không thể upload file.");
        }
    }

}
