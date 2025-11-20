package com.homeheaven.service;

import com.homeheaven.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {
    
    private final Path fileStorageLocation;
    
    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        
        try {
            Files.createDirectories(this.fileStorageLocation);
            log.info("Upload directory created at: {}", this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create upload directory", ex);
        }
    }
    
    public String storeFile(MultipartFile file) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        
        try {
            if (file.isEmpty()) {
                throw new FileStorageException("Failed to store empty file " + originalFilename);
            }
            
            if (originalFilename.contains("..")) {
                throw new FileStorageException("Filename contains invalid path sequence " + originalFilename);
            }
            
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;
            
            Path targetLocation = this.fileStorageLocation.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("File stored successfully: {}", filename);
            return filename;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + originalFilename, ex);
        }
    }
    
    public void deleteFile(String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
            log.info("File deleted: {}", filename);
        } catch (IOException ex) {
            throw new FileStorageException("Could not delete file " + filename, ex);
        }
    }
}
