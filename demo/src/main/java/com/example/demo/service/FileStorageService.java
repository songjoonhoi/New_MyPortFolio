package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    // 이미지가 저장될 기본 경로 (프로젝트 루트 아래 static/images)
    private final Path fileStorageLocation = Paths.get("src/main/resources/static/images").toAbsolutePath().normalize();

    public FileStorageService() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("이미지를 저장할 디렉토리를 만들 수 없습니다.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // 파일 이름 중복을 피하기 위해 UUID를 사용
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("파일 이름에 부적절한 문자가 포함되어 있습니다. " + fileName);
            }

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);

            // 웹에서 접근 가능한 경로를 반환 (/images/파일이름)
            return "/images/" + fileName;

        } catch (IOException ex) {
            throw new RuntimeException("파일 " + fileName + "을 저장할 수 없습니다. 다시 시도해 주세요.", ex);
        }
    }
}
