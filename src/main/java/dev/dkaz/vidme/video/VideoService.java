package dev.dkaz.vidme.video;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
public class VideoService {
    private final VideoRepository videoRepository;
    private final VideoFileStorage videoFileStorage;
    private final Integer featuredVideosLimit;

    public VideoService(
            VideoRepository videoRepository,
            VideoFileStorage videoFileStorage,
            @Value("${vidme.featured-vids.limit}") Integer featuredVideosLimit) {
        this.videoRepository = videoRepository;
        this.videoFileStorage = videoFileStorage;
        this.featuredVideosLimit = featuredVideosLimit;
    }

    public List<Video> findAllVideos() {
        return videoRepository.findAll();
    }

    public List<Video> findFeaturesVideos() {
        return videoRepository.findAllOrderByRandom(featuredVideosLimit);
    }

    public Optional<Video> findVideoById(Long id) {
        videoRepository.incrementViews(id);
        return videoRepository.findById(id);
    }

    public Optional<Path> findVideoFileById(String fileId) {
        return videoFileStorage.findById(fileId);
    }
}
