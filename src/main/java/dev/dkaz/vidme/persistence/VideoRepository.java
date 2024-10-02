package dev.dkaz.vidme.persistence;

import dev.dkaz.vidme.model.Video;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface VideoRepository extends ListCrudRepository<Video, Long>, PagingAndSortingRepository<Video, Long> {
}
