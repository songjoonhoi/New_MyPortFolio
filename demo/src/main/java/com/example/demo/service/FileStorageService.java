// src/main/java/com/example/demo/service/FileStorageService.java
package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

@Slf4j
@Service
public class FileStorageService {

    // 허용된 MIME 타입
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
        "image/jpeg",
        "image/png", 
        "image/webp"
    );
    
    // 허용된 파일 확장자
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
        "jpg", "jpeg", "png", "webp"
    );
    
    // 최대 파일 크기 (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    // 썸네일 크기 정의
    public enum ThumbnailSize {
        SMALL(200, 200),
        MEDIUM(800, 600),
        LARGE(1600, 1200);
        
        public final int width;
        public final int height;
        
        ThumbnailSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
    
    // 파일 저장 기본 경로
    private final Path fileStorageLocation;
    private final Path thumbnailStorageLocation;
    
    public FileStorageService(@Value("${app.file.upload-dir:src/main/resources/static/images}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.thumbnailStorageLocation = Paths.get(uploadDir, "thumbnails").toAbsolutePath().normalize();
        
        try {
            Files.createDirectories(this.fileStorageLocation);
            Files.createDirectories(this.thumbnailStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("파일 저장 디렉토리를 생성할 수 없습니다.", ex);
        }
    }
    
    /**
     * 파일 업로드 (보안 검증 포함)
     */
    public String storeFile(MultipartFile file) {
        validateFile(file);
        
        try {
            // 안전한 파일명 생성
            String safeFileName = generateSafeFileName(file.getOriginalFilename());
            
            // 파일 저장
            Path targetLocation = this.fileStorageLocation.resolve(safeFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            // 이미지인 경우 썸네일 생성
            if (isImageFile(file)) {
                generateThumbnails(targetLocation, safeFileName);
            }
            
            log.info("파일 업로드 완료: {}", safeFileName);
            return "/images/" + safeFileName;
            
        } catch (IOException ex) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", ex);
        }
    }
    
    /**
     * 파일 유효성 검증
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }
        
        // 파일 크기 검증
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기가 10MB를 초과할 수 없습니다.");
        }
        
        // MIME 타입 검증
        String mimeType = file.getContentType();
        if (mimeType == null || !ALLOWED_MIME_TYPES.contains(mimeType.toLowerCase())) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. (JPEG, PNG, WebP만 허용)");
        }
        
        // 파일 확장자 검증
        String fileName = file.getOriginalFilename();
        if (fileName == null || !hasValidExtension(fileName)) {
            throw new IllegalArgumentException("유효하지 않은 파일 확장자입니다.");
        }
        
        // 파일 시그니처 검증 (매직 넘버)
        try {
            if (!hasValidFileSignature(file)) {
                throw new IllegalArgumentException("파일이 손상되었거나 유효하지 않습니다.");
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 검증 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * 안전한 파일명 생성
     */
    private String generateSafeFileName(String originalFileName) {
        if (originalFileName == null) {
            originalFileName = "file";
        }
        
        // 파일 확장자 추출
        String extension = getFileExtension(originalFileName);
        
        // 현재 시간 + UUID + 해시를 조합한 고유 파일명 생성
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        
        try {
            // 원본 파일명의 해시값 생성
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(originalFileName.getBytes());
            String hash = Base64.getEncoder().encodeToString(hashBytes).replace("/", "_").replace("+", "-").substring(0, 8);
            
            return String.format("%s_%s_%s.%s", timestamp, uuid, hash, extension);
        } catch (NoSuchAlgorithmException e) {
            // MD5 알고리즘을 사용할 수 없는 경우 UUID만 사용
            return String.format("%s_%s.%s", timestamp, uuid, extension);
        }
    }
    
    /**
     * 파일 확장자 검증
     */
    private boolean hasValidExtension(String fileName) {
        String extension = getFileExtension(fileName);
        return ALLOWED_EXTENSIONS.contains(extension.toLowerCase());
    }
    
    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }
    
    /**
     * 파일 시그니처(매직 넘버) 검증
     */
    private boolean hasValidFileSignature(MultipartFile file) throws IOException {
        byte[] fileHeader = new byte[8];
        int bytesRead = file.getInputStream().read(fileHeader);
        
        if (bytesRead < 2) {
            return false;
        }
        
        // JPEG 파일 시그니처: FF D8
        if (fileHeader[0] == (byte) 0xFF && fileHeader[1] == (byte) 0xD8) {
            return true;
        }
        
        // PNG 파일 시그니처: 89 50 4E 47
        if (bytesRead >= 4 && 
            fileHeader[0] == (byte) 0x89 && 
            fileHeader[1] == (byte) 0x50 && 
            fileHeader[2] == (byte) 0x4E && 
            fileHeader[3] == (byte) 0x47) {
            return true;
        }
        
        // WebP 파일 시그니처: RIFF....WEBP
        if (bytesRead >= 8 &&
            fileHeader[0] == 'R' && fileHeader[1] == 'I' && 
            fileHeader[2] == 'F' && fileHeader[3] == 'F' &&
            fileHeader[8-4] == 'W' && fileHeader[8-3] == 'E' && 
            fileHeader[8-2] == 'B' && fileHeader[8-1] == 'P') {
            return true;
        }
        
        return false;
    }
    
    /**
     * 이미지 파일 여부 확인
     */
    private boolean isImageFile(MultipartFile file) {
        String mimeType = file.getContentType();
        return mimeType != null && mimeType.startsWith("image/");
    }
    
    /**
     * 썸네일 생성
     */
    private void generateThumbnails(Path originalFilePath, String fileName) {
        try {
            BufferedImage originalImage = ImageIO.read(originalFilePath.toFile());
            if (originalImage == null) {
                log.warn("이미지를 읽을 수 없습니다: {}", fileName);
                return;
            }
            
            String nameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
            String extension = getFileExtension(fileName);
            
            // 각 썸네일 크기별로 생성
            for (ThumbnailSize size : ThumbnailSize.values()) {
                BufferedImage thumbnail = createThumbnail(originalImage, size.width, size.height);
                String thumbnailFileName = String.format("%s_%s.%s", 
                    nameWithoutExt, size.name().toLowerCase(), extension);
                
                Path thumbnailPath = thumbnailStorageLocation.resolve(thumbnailFileName);
                
                // WebP는 JPEG로 변환하여 저장 (Java 기본 ImageIO가 WebP 쓰기를 지원하지 않을 수 있음)
                String outputFormat = "webp".equalsIgnoreCase(extension) ? "jpeg" : extension;
                ImageIO.write(thumbnail, outputFormat, thumbnailPath.toFile());
            }
            
            log.info("썸네일 생성 완료: {}", fileName);
            
        } catch (IOException e) {
            log.error("썸네일 생성 실패: {}", fileName, e);
        }
    }
    
    /**
     * 썸네일 이미지 생성 (비율 유지)
     */
    private BufferedImage createThumbnail(BufferedImage originalImage, int targetWidth, int targetHeight) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        
        // 비율 계산
        double widthRatio = (double) targetWidth / originalWidth;
        double heightRatio = (double) targetHeight / originalHeight;
        double ratio = Math.min(widthRatio, heightRatio);
        
        int newWidth = (int) (originalWidth * ratio);
        int newHeight = (int) (originalHeight * ratio);
        
        // 고품질 리샘플링을 위한 설정
        BufferedImage thumbnail = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = thumbnail.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
        
        return thumbnail;
    }
    
    /**
     * 썸네일 URL 생성
     */
    public String getThumbnailUrl(String originalImageUrl, ThumbnailSize size) {
        if (originalImageUrl == null || !originalImageUrl.startsWith("/images/")) {
            return originalImageUrl;
        }
        
        String fileName = originalImageUrl.substring("/images/".length());
        String nameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
        String extension = getFileExtension(fileName);
        
        String thumbnailFileName = String.format("%s_%s.%s", 
            nameWithoutExt, size.name().toLowerCase(), extension);
        
        return "/images/thumbnails/" + thumbnailFileName;
    }
    
    /**
     * 파일 삭제
     */
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith("/images/")) {
            return false;
        }
        
        try {
            String fileName = fileUrl.substring("/images/".length());
            Path filePath = fileStorageLocation.resolve(fileName);
            
            // 원본 파일 삭제
            boolean deleted = Files.deleteIfExists(filePath);
            
            // 썸네일 파일들도 삭제
            deleteThumbnails(fileName);
            
            log.info("파일 삭제 완료: {}", fileName);
            return deleted;
            
        } catch (IOException e) {
            log.error("파일 삭제 실패: {}", fileUrl, e);
            return false;
        }
    }
    
    /**
     * 썸네일 파일들 삭제
     */
    private void deleteThumbnails(String originalFileName) {
        try {
            String nameWithoutExt = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
            String extension = getFileExtension(originalFileName);
            
            for (ThumbnailSize size : ThumbnailSize.values()) {
                String thumbnailFileName = String.format("%s_%s.%s", 
                    nameWithoutExt, size.name().toLowerCase(), extension);
                Path thumbnailPath = thumbnailStorageLocation.resolve(thumbnailFileName);
                Files.deleteIfExists(thumbnailPath);
            }
            
        } catch (IOException e) {
            log.warn("썸네일 삭제 중 오류 발생: {}", originalFileName, e);
        }
    }
    
    /**
     * 파일 존재 여부 확인
     */
    public boolean fileExists(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith("/images/")) {
            return false;
        }
        
        String fileName = fileUrl.substring("/images/".length());
        Path filePath = fileStorageLocation.resolve(fileName);
        return Files.exists(filePath);
    }
    
    /**
     * 파일 정보 조회
     */
    public FileInfo getFileInfo(String fileUrl) {
        if (!fileExists(fileUrl)) {
            return null;
        }
        
        try {
            String fileName = fileUrl.substring("/images/".length());
            Path filePath = fileStorageLocation.resolve(fileName);
            
            long size = Files.size(filePath);
            String mimeType = Files.probeContentType(filePath);
            
            return new FileInfo(fileName, size, mimeType, fileUrl);
            
        } catch (IOException e) {
            log.error("파일 정보 조회 실패: {}", fileUrl, e);
            return null;
        }
    }
    
    /**
     * 파일 정보 클래스
     */
    public static class FileInfo {
        private final String fileName;
        private final long size;
        private final String mimeType;
        private final String url;
        
        public FileInfo(String fileName, long size, String mimeType, String url) {
            this.fileName = fileName;
            this.size = size;
            this.mimeType = mimeType;
            this.url = url;
        }
        
        public String getFileName() { return fileName; }
        public long getSize() { return size; }
        public String getMimeType() { return mimeType; }
        public String getUrl() { return url; }
        
        public String getFormattedSize() {
            if (size < 1024) return size + " B";
            if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        }
    }
    
    /**
     * 허용된 파일 타입 목록 반환
     */
    public static Set<String> getAllowedMimeTypes() {
        return Collections.unmodifiableSet(ALLOWED_MIME_TYPES);
    }
    
    /**
     * 허용된 파일 확장자 목록 반환
     */
    public static Set<String> getAllowedExtensions() {
        return Collections.unmodifiableSet(ALLOWED_EXTENSIONS);
    }
    
    /**
     * 최대 파일 크기 반환 (바이트)
     */
    public static long getMaxFileSize() {
        return MAX_FILE_SIZE;
    }
    
    /**
     * 최대 파일 크기 반환 (포맷된 문자열)
     */
    public static String getMaxFileSizeFormatted() {
        return String.format("%.0f MB", MAX_FILE_SIZE / (1024.0 * 1024.0));
    }
}