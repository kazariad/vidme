package dev.dkaz.vidme;

import dev.dkaz.vidme.thumbnail.ThumbnailService;
import dev.dkaz.vidme.video.Video;
import dev.dkaz.vidme.video.VideoService;
import dev.dkaz.vidme.video.VideoSort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
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
    private final int pageSize;

    public WebController(
            VideoService videoService, ThumbnailService thumbnailService, HashIdGenerator hashIdGenerator,
            TimeUtil timeUtil, @Value("${vidme.videos.page-size}") int pageSize
    ) {
        this.videoService = videoService;
        this.thumbnailService = thumbnailService;
        this.hashIdGenerator = hashIdGenerator;
        this.timeUtil = timeUtil;
        this.pageSize = pageSize;
    }

    public record VideoWithHashUploadTime(Video video, String hashId, String timeSinceUpload) {}

    @GetMapping(path = "/")
    public String getHome(Model model) {
        List<Video> videos = videoService.findFeaturedVideos(0, pageSize);
        List<VideoWithHashUploadTime> videoDtos = videos.stream().map(video -> {
            String hashId = hashIdGenerator.encode(video.getId());
            String timeSinceUpload = timeUtil.getElapsedTime(video.getCreatedAt(), Instant.now());
            return new VideoWithHashUploadTime(video, hashId, timeSinceUpload);
        }).toList();
        model.addAttribute("videoDtos", videoDtos);
        return "home";
    }

    @GetMapping(path = "/{hashId}")
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

    @GetMapping(path = "/videos")
    public String searchVideos(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "newest") String sort,
            Model model) {
        query = query.trim();
        VideoSort videoSort = VideoSort.valueOf(sort.toUpperCase());

        List<Video> videos = videoService.searchVideos(query, videoSort, 0, pageSize);
        List<VideoWithHashUploadTime> videoDtos = videos.stream().map(video -> {
            String hashId = hashIdGenerator.encode(video.getId());
            String timeSinceUpload = timeUtil.getElapsedTime(video.getCreatedAt(), Instant.now());
            return new VideoWithHashUploadTime(video, hashId, timeSinceUpload);
        }).toList();

        model.addAttribute("videoDtos", videoDtos);
        model.addAttribute("query", query);
        model.addAttribute("videoSort", videoSort);
        model.addAttribute("page", 0);
        return "videos";
    }

    @GetMapping(path = "/hx/videos", headers = {"HX-Request"})
    public String searchVideosHx(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam int page,
            Model model) {
        query = query.trim();
        VideoSort videoSort = VideoSort.valueOf(sort.toUpperCase());

        List<Video> videos = videoService.searchVideos(query, videoSort, page, pageSize);
        List<VideoWithHashUploadTime> videoDtos = videos.stream().map(video -> {
            String hashId = hashIdGenerator.encode(video.getId());
            String timeSinceUpload = timeUtil.getElapsedTime(video.getCreatedAt(), Instant.now());
            return new VideoWithHashUploadTime(video, hashId, timeSinceUpload);
        }).toList();

        model.addAttribute("videoDtos", videoDtos);
        model.addAttribute("query", query);
        model.addAttribute("videoSort", videoSort);
        model.addAttribute("page", page);
        return "videos-hx";
    }

    @GetMapping(path = "/video/{fileId}")
    public ResponseEntity<FileSystemResource> getVideoFile(@PathVariable String fileId) {
        Optional<Path> pathOpt = videoService.findVideoFileById(fileId);
        if (pathOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        FileSystemResource fsr = new FileSystemResource(pathOpt.get());
        return ResponseEntity.ok().contentType(new MediaType("video", "mp4")).body(fsr);
    }

    @GetMapping(path = "/thumbnail/{fileId}")
    public ResponseEntity<FileSystemResource> getThumbnailFile(@PathVariable String fileId) {
        Optional<Path> pathOpt = thumbnailService.findThumbnailFileById(fileId);
        if (pathOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        FileSystemResource fsr = new FileSystemResource(pathOpt.get());
        return ResponseEntity.ok().contentType(new MediaType("image", "jpeg")).body(fsr);
    }
}
