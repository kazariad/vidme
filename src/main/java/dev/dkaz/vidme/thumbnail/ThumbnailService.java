package dev.dkaz.vidme.thumbnail;

import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Optional;

@Service
public class ThumbnailService {
    private final ThumbnailFileStorage thumbnailFileStorage;

    public ThumbnailService(ThumbnailFileStorage thumbnailFileStorage) {
        this.thumbnailFileStorage = thumbnailFileStorage;
    }

    public Optional<Path> findThumbnailFileById(String fileId) {
        return thumbnailFileStorage.findById(fileId);
    }
}
