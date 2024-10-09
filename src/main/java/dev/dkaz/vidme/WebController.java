package dev.dkaz.vidme;

import dev.dkaz.vidme.thumbnail.ThumbnailService;
import dev.dkaz.vidme.video.Video;
import dev.dkaz.vidme.video.VideoService;
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
import java.util.List;
import java.util.Optional;

@Controller
public class WebController {
    private final VideoService videoService;
    private final ThumbnailService thumbnailService;
    private final HashIdGenerator hashIdGenerator;
    private final TimeUtil timeUtil;

    public WebController(VideoService videoService, ThumbnailService thumbnailService, HashIdGenerator hashIdGenerator, TimeUtil timeUtil) {
        this.videoService = videoService;
        this.thumbnailService = thumbnailService;
        this.hashIdGenerator = hashIdGenerator;
        this.timeUtil = timeUtil;
    }

    public record VideoWithHashUploadTime(Video video, String hashId, String timeSinceUpload) {}

    @GetMapping(path = "/")
    public String getHome(Model model) {
        List<Video> videos = videoService.findAllVideos();
        List<VideoWithHashUploadTime> videoDtos = videos.stream().map(video -> {
            String hashId = hashIdGenerator.encode(video.getId());
            String timeSinceUpload = timeUtil.getElapsedTime(video.getCreatedAt(), Instant.now());
            return new VideoWithHashUploadTime(video, hashId, timeSinceUpload);
        }).toList();
        model.addAttribute("videoDtos", videoDtos);
        return "home";
    }

    @GetMapping(path = "/video/{hashId}")
    public String getVideo(@PathVariable String hashId, Model model) {
        Long id = hashIdGenerator.decode(hashId);
        Optional<Video> videoOpt = videoService.findVideoById(id);
        if (videoOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Video video = videoOpt.get();
        // use relative time since we don't know client's time zone
        String timeSinceUpload = timeUtil.getElapsedTime(video.getCreatedAt(), Instant.now());
        model.addAttribute("videoDto", new VideoWithHashUploadTime(video, hashId, timeSinceUpload));
        return "video";
    }

    @GetMapping(path = "/video/stream/{fileId}")
    public ResponseEntity<FileSystemResource> getVideoFile(@PathVariable String fileId) {
        Optional<Path> pathOpt = videoService.findVideoFileById(fileId);
        if (pathOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        FileSystemResource fsr = new FileSystemResource(pathOpt.get());
        return ResponseEntity.ok().contentType(new MediaType("video", "webm")).body(fsr);
    }

    @GetMapping(path = "/thumbnail/{fileId}")
    public ResponseEntity<FileSystemResource> getThumbnailFile(@PathVariable String fileId) {
        Optional<Path> pathOpt = thumbnailService.findThumbnailFileById(fileId);
        if (pathOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        FileSystemResource fsr = new FileSystemResource(pathOpt.get());
        return ResponseEntity.ok().contentType(new MediaType("image", "jpeg")).body(fsr);
    }
}
