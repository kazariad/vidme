package dev.dkaz.vidme.service;

import dev.dkaz.vidme.model.Video;
import dev.dkaz.vidme.persistence.VideoFileStorage;
import dev.dkaz.vidme.persistence.VideoRepository;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Optional;

@Service
public class VideoService {
    private final VideoRepository videoRepository;
    private final VideoFileStorage videoFileStorage;

    public VideoService(VideoRepository videoRepository, VideoFileStorage videoFileStorage) {
        this.videoRepository = videoRepository;
        this.videoFileStorage = videoFileStorage;
    }

    public void incrementVideoViews(Long id) {
        videoRepository.incrementViews(id);
    }

    public Optional<Video> findVideoById(Long id) {
        return videoRepository.findById(id);
    }

    public Optional<Path> findVideoFileByFileId(String fileId) {
        return videoFileStorage.findByFileId(fileId);
    }
}
