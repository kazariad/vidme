package dev.dkaz.vidme.video;

import dev.dkaz.vidme.TimeUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;

@Controller
public class VideoWebController {
    private final VideoService videoService;
    private final TimeUtils timeUtils;
    private final MediaType mediaType = new MediaType("video", "webm");

    public VideoWebController(VideoService videoService, TimeUtils timeUtils) {
        this.videoService = videoService;
        this.timeUtils = timeUtils;
    }

    public record VideoWithElapsedTime(Video video, String elapsedTime) {
    }

    @GetMapping(path = "/video/{id}")
    public String getVideo(@PathVariable Long id, Model model) {
        Optional<Video> videoOpt = videoService.findVideoById(id);
        if (videoOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Video video = videoOpt.get();
        // use relative time to avoid determining client's time zone
        String elapsedTime = timeUtils.getElapsedTime(video.getCreatedAt(), Instant.now());
        model.addAttribute("videoDto", new VideoWithElapsedTime(video, elapsedTime));
        return "video";
    }

    @GetMapping(path = "/video/stream/{fileId}")
    public ResponseEntity<FileSystemResource> getVideoStream(@PathVariable String fileId) {
        Optional<Path> pathOpt = videoService.findVideoFileByFileId(fileId);
        if (pathOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        FileSystemResource fsr = new FileSystemResource(pathOpt.get());
        return ResponseEntity.ok().contentType(mediaType).body(fsr);
    }
}
