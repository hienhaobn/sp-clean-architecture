package com.techsolify.aquapure.adapter.controller;

import com.techsolify.aquapure.domain.entity.ImageDocument;
import com.techsolify.aquapure.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Tag(name = "Image API", description = "API for image upload and search operations")
public class ImageController {

  private final ImageService imageService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(
      summary = "Upload an image",
      description = "Upload an image to MinIO and index it in Elasticsearch")
  public ResponseEntity<ImageDocument> uploadImage(
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "tags", required = false) String[] tags)
      throws IOException {

    return ResponseEntity.ok(imageService.uploadImage(file, description, tags));
  }

  @GetMapping("/search/tag/{tag}")
  @Operation(summary = "Search images by tag", description = "Search for images by tag")
  public ResponseEntity<List<ImageDocument>> searchByTag(@PathVariable String tag) {
    return ResponseEntity.ok(imageService.searchByTag(tag));
  }

  @GetMapping("/search/description/{keyword}")
  @Operation(
      summary = "Search images by description",
      description = "Search for images by description")
  public ResponseEntity<List<ImageDocument>> searchByDescription(@PathVariable String keyword) {
    return ResponseEntity.ok(imageService.searchByDescription(keyword));
  }

  @GetMapping("/search/filename/{keyword}")
  @Operation(summary = "Search images by filename", description = "Search for images by filename")
  public ResponseEntity<List<ImageDocument>> searchByFileName(@PathVariable String keyword) {
    return ResponseEntity.ok(imageService.searchByFileName(keyword));
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Delete an image",
      description = "Delete an image from MinIO and Elasticsearch")
  public ResponseEntity<Void> deleteImage(@PathVariable String id) {
    imageService.deleteImage(id);
    return ResponseEntity.noContent().build();
  }
}
