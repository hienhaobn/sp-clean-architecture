package com.techsolify.aquapure.service;

import com.techsolify.aquapure.domain.entity.ImageDocument;
import com.techsolify.aquapure.domain.repository.ImageRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

  private final FileStorageService fileStorageService;
  private final ImageRepository imageRepository;

  /**
   * Upload an image to MinIO and index it in Elasticsearch
   *
   * @param file the image file
   * @param description the image description
   * @param tags the image tags
   * @return the uploaded image document
   */
  public ImageDocument uploadImage(MultipartFile file, String description, String[] tags)
      throws IOException {
    // Upload the file to MinIO
    String fileUrl = fileStorageService.uploadFile(file);

    // Create an Elasticsearch document
    ImageDocument imageDocument =
        ImageDocument.builder()
            .id(UUID.randomUUID().toString())
            .fileName(file.getOriginalFilename())
            .fileUrl(fileUrl)
            .contentType(file.getContentType())
            .fileSize(file.getSize())
            .description(description)
            .tags(tags)
            .uploadDate(LocalDateTime.now())
            .build();

    // Save the document to Elasticsearch
    return imageRepository.save(imageDocument);
  }

  /**
   * Search for images by tag
   *
   * @param tag the tag to search for
   * @return a list of matching images
   */
  @Cacheable(value = "imagesByTag", key = "#tag")
  public List<ImageDocument> searchByTag(String tag) {
    log.debug("Searching for images with tag: {}", tag);
    return imageRepository.findByTagsContaining(tag);
  }

  /**
   * Search for images by description
   *
   * @param keyword the keyword to search for
   * @return a list of matching images
   */
  @Cacheable(value = "imagesByDescription", key = "#keyword")
  public List<ImageDocument> searchByDescription(String keyword) {
    log.debug("Searching for images with description containing: {}", keyword);
    return imageRepository.findByDescriptionContaining(keyword);
  }

  /**
   * Search for images by filename
   *
   * @param keyword the keyword to search for
   * @return a list of matching images
   */
  @Cacheable(value = "imagesByFileName", key = "#keyword")
  public List<ImageDocument> searchByFileName(String keyword) {
    log.debug("Searching for images with filename containing: {}", keyword);
    return imageRepository.findByFileNameContaining(keyword);
  }

  /**
   * Delete an image
   *
   * @param id the image ID
   */
  @CacheEvict(
      value = {"imagesByTag", "imagesByDescription", "imagesByFileName"},
      allEntries = true)
  public void deleteImage(String id) {
    // Get the image document
    imageRepository
        .findById(id)
        .ifPresent(
            imageDocument -> {
              // Extract the object name from the URL
              String objectName =
                  imageDocument
                      .getFileUrl()
                      .substring(
                          imageDocument.getFileUrl().lastIndexOf("/") + 1,
                          imageDocument.getFileUrl().indexOf("?"));

              // Delete the file from MinIO
              fileStorageService.deleteFile(objectName);

              // Delete the document from Elasticsearch
              imageRepository.delete(imageDocument);

              log.info("Image {} deleted successfully", id);
            });
  }
}
