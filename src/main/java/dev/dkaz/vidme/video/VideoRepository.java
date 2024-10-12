package dev.dkaz.vidme.video;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VideoRepository extends ListCrudRepository<Video, Long>, PagingAndSortingRepository<Video, Long> {
    @Query("SELECT * FROM video ORDER BY RAND() LIMIT :limit")
    List<Video> findRandom(@Param("limit") int limit);

    @Query("SELECT * FROM video WHERE MATCH(title, description) AGAINST(:query) LIMIT :limit OFFSET :offset")
    List<Video> findByTitleDescriptionSearch(@Param("query") String query, @Param("offset") int offset, @Param("limit") int limit);

    @Modifying
    @Query("UPDATE video SET views = views + 1 WHERE id = :id")
    void incrementViews(@Param("id") Long id);
}
