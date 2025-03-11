package com.techsolify.aquapure.service;

import com.techsolify.aquapure.config.MinioConfig;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

  private final MinioClient minioClient;
  private final MinioConfig minioConfig;

  /**
   * Upload a file to MinIO
   *
   * @param file the file to upload
   * @return the file URL
   */
  public String uploadFile(MultipartFile file) throws IOException {
    try {
      String bucketName = minioConfig.getBucketName();

      // Create bucket if it doesn't exist
      boolean found =
          minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
      if (!found) {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        log.info("Created bucket: {}", bucketName);
      }

      // Generate a unique file name with the original extension
      String extension = FilenameUtils.getExtension(file.getOriginalFilename());
      String objectName = UUID.randomUUID().toString() + "." + extension;

      // Upload the file
      minioClient.putObject(
          PutObjectArgs.builder()
              .bucket(bucketName)
              .object(objectName)
              .contentType(file.getContentType())
              .stream(file.getInputStream(), file.getSize(), -1)
              .build());

      log.info("File {} uploaded successfully", objectName);

      // Generate a presigned URL for the file
      return minioClient.getPresignedObjectUrl(
          GetPresignedObjectUrlArgs.builder()
              .bucket(bucketName)
              .object(objectName)
              .method(Method.GET)
              .expiry(7, TimeUnit.DAYS)
              .build());
    } catch (Exception e) {
      log.error("Error uploading file", e);
      throw new IOException("Failed to upload file", e);
    }
  }

  /**
   * Delete a file from MinIO
   *
   * @param objectName the name of the file to delete
   */
  public void deleteFile(String objectName) {
    try {
      minioClient.removeObject(
          RemoveObjectArgs.builder()
              .bucket(minioConfig.getBucketName())
              .object(objectName)
              .build());
      log.info("File {} deleted successfully", objectName);
    } catch (Exception e) {
      log.error("Error deleting file", e);
    }
  }

  /**
   * Get a file from MinIO
   *
   * @param objectName the name of the file to get
   * @return the input stream of the file
   */
  public InputStream getFile(String objectName) throws IOException {
    try {
      return minioClient.getObject(
          GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(objectName).build());
    } catch (Exception e) {
      log.error("Error getting file", e);
      throw new IOException("Failed to get file", e);
    }
  }
}
