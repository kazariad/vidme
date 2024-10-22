package dev.dkaz.vidme.video;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface VideoRepository extends ListCrudRepository<Video, Long>, PagingAndSortingRepository<Video, Long>, VideoRepositoryExt {
    @Modifying
    @Query("UPDATE video SET views = views + 1 WHERE id = :id")
    void incrementViews(@Param("id") Long id);
}
