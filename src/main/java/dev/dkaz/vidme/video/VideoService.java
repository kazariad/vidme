package dev.dkaz.vidme.video;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
public class VideoService {
    private final VideoRepository videoRepository;
    private final VideoFileStorage videoFileStorage;
    private final Integer pageSize;

    public VideoService(
            VideoRepository videoRepository,
            VideoFileStorage videoFileStorage,
            @Value("${vidme.videos.page-size}") Integer pageSize) {
        this.videoRepository = videoRepository;
        this.videoFileStorage = videoFileStorage;
        this.pageSize = pageSize;
    }

    public List<Video> findFeaturesVideos() {
        return videoRepository.findRandom(pageSize);
    }

    public List<Video> searchVideos(String query, int page) {
        if (query.isBlank() || query.equals("*")) {
            return videoRepository.findAll(Pageable.ofSize(pageSize).withPage(page)).toList();
        } else {
            return videoRepository.findByTitleDescriptionSearch(query, page * pageSize, pageSize);
        }
    }

    public Optional<Video> findVideoById(Long id) {
        videoRepository.incrementViews(id);
        return videoRepository.findById(id);
    }

    public Optional<Path> findVideoFileById(String fileId) {
        return videoFileStorage.findById(fileId);
    }
}
