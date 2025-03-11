package com.techsolify.aquapure.domain.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "images")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDocument {

  @Id private String id;

  @Field(type = FieldType.Text, name = "file_name")
  private String fileName;

  @Field(type = FieldType.Text, name = "file_url")
  private String fileUrl;

  @Field(type = FieldType.Text, name = "content_type")
  private String contentType;

  @Field(type = FieldType.Long, name = "file_size")
  private Long fileSize;

  @Field(type = FieldType.Text, name = "description")
  private String description;

  @Field(type = FieldType.Text, name = "tags")
  private String[] tags;

  @Field(type = FieldType.Date, name = "upload_date")
  private LocalDateTime uploadDate;
}
