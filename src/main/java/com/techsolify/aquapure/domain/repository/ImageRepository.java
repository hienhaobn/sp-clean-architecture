package com.techsolify.aquapure.domain.repository;

import com.techsolify.aquapure.domain.entity.ImageDocument;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends ElasticsearchRepository<ImageDocument, String> {

  List<ImageDocument> findByTagsContaining(String tag);

  List<ImageDocument> findByDescriptionContaining(String keyword);

  List<ImageDocument> findByFileNameContaining(String keyword);
}
