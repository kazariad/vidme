package dev.dkaz.vidme.video;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Component
public class VideoFileStorage {
    private final Path ROOT;
    private final Map<String, Path> LOOKUP_CACHE;

    public VideoFileStorage(@Value("${vidme.video.root-dir}") String rootDir) {
        ROOT = Paths.get(rootDir);
        LOOKUP_CACHE = new ConcurrentHashMap<>();
    }

    public Optional<Path> findByFileId(String fileId) {
        if (LOOKUP_CACHE.containsKey(fileId)) {
            return Optional.of(LOOKUP_CACHE.get(fileId));
        }

        try (Stream<Path> paths = Files.find(ROOT, 1, (path, basicFileAttributes) ->
                path.getFileName().toString().equals(fileId + ".webm")
        )) {
            Optional<Path> pathOpt = paths.findFirst();
            pathOpt.ifPresent(path -> LOOKUP_CACHE.put(fileId, path));
            return pathOpt;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
