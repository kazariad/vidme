package dev.dkaz.vidme.video;

import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
public class VideoService {
    private final VideoRepository videoRepository;
    private final VideoFileStorage videoFileStorage;

    public VideoService(VideoRepository videoRepository, VideoFileStorage videoFileStorage) {
        this.videoRepository = videoRepository;
        this.videoFileStorage = videoFileStorage;
    }

    public List<Video> findFeaturedVideos(int page, int pageSize) {
        return videoRepository.findFeatured(page, pageSize);
    }

    public List<Video> searchVideos(String query, VideoSort sort, int page, int pageSize) {
        return videoRepository.findByTitleDescriptionSearch(query, sort, page, pageSize);
    }

    public Optional<Video> findVideoById(Long id) {
        videoRepository.incrementViews(id);
        return videoRepository.findById(id);
    }

    public Optional<Path> findVideoFileById(String fileId) {
        return videoFileStorage.findById(fileId);
    }
}
